package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IMentorLogDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

import static org.bukkit.ChatColor.YELLOW;

public class ForceCommand extends AbstractCommand {
    public ForceCommand(Tregmine tregmine) {
        super(tregmine, "force", Tregmine.PermissionDefinitions.RESIDENT_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 2) {
            return false;
        }
        String playerPattern = args[0];
        String channel = args[1];

        List<GenericPlayer> matches = tregmine.matchPlayer(playerPattern);
        if (matches.size() > 1) {
            player.sendMessage(ChatColor.RED + "Your player name is too broad. Please narrow your search term and try again.");
            return true;
        }

        if (matches.size() == 0) {
            player.sendMessage(ChatColor.RED + "The player specified is not online or does not exist.");
            return true;
        }

        GenericPlayer toPlayer = matches.get(0);

        if (toPlayer.hasFlag(GenericPlayer.Flags.FORCESHIELD) && !player.getRank().canOverrideForceShield()) {
            toPlayer.sendMessage(new TextComponent(ChatColor.AQUA.toString()), player.decideVS(toPlayer), new TextComponent(" tried to force you into a channel!"));
            player.sendMessage(new TextComponent(ChatColor.AQUA + "Can not force "), player.decideVS(player), new TextComponent(" into a channel!"));
            return true;
        }
        String oldChannel = player.getChatChannel();
        player.setChatChannel(channel);
        toPlayer.setChatChannel(channel);
        toPlayer.sendMessage(new TextComponent(YELLOW + ""), player.decideVS(player),
                new TextComponent(" forced you into channel " + channel.toUpperCase()));
        toPlayer.sendMessage(YELLOW + "Write /channel global to switch back to " + "the global chat.");
        player.sendMessage(new TextComponent(YELLOW + "You are now in a forced chat " + channel.toUpperCase() + " with"), toPlayer.decideVS(player), new TextComponent("."));
        LOGGER.info(player.getName() + " FORCED CHAT WITH " + toPlayer.getDisplayName() + " IN CHANNEL "
                + channel.toUpperCase());

        for (GenericPlayer players : tregmine.getOnlinePlayers()) {
            if (oldChannel.equalsIgnoreCase(players.getChatChannel())) {
                players.sendMessage(new TextComponent(player.decideVS(players) + "" + ChatColor.YELLOW + " and "
                ), toPlayer.getChatName(), new TextComponent(ChatColor.YELLOW + " have left channel " + oldChannel));
                players.sendMessage(player.decideVS(players), new TextComponent(YELLOW + " and "),
                        toPlayer.decideVS(players), new TextComponent(YELLOW + " have left channel " + oldChannel));
            } else if (channel.equalsIgnoreCase(players.getChatChannel())) {
                players.sendMessage(player.decideVS(players), new TextComponent(YELLOW + " and "),
                        toPlayer.decideVS(players), new TextComponent(YELLOW + " have joined channel " + channel));
            }
        }

        // If this is a mentor forcing his student, log it in the mentorlog
        GenericPlayer student = player.getStudent();
        if (student != null && student.getId() == toPlayer.getId() && !"global".equalsIgnoreCase(channel)) {
            try (IContext ctx = tregmine.createContext()) {
                IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                int mentorLogId = mentorLogDAO.getMentorLogId(student, player);
                mentorLogDAO.updateMentorLogChannel(mentorLogId, channel);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
