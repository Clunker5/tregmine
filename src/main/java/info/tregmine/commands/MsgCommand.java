package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.DiscordCommandSender;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Notification;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

public class MsgCommand extends AbstractCommand {
    private final TextComponent toFlag = new TextComponent("(to) ");
    private final TextComponent fromFlag = new TextComponent("(from) ");
    public MsgCommand(Tregmine tregmine) {
        super(tregmine, "msg");
        this.toFlag.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        this.fromFlag.setColor(net.md_5.bungee.api.ChatColor.GREEN);
    }

    private String argsToMessage(String[] args) {
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {

        if (args.length < 2) {
            return false;
        }

        if (player instanceof DiscordCommandSender) return false;

        String message = ChatColor.translateAlternateColorCodes('#', argsToMessage(args));
        TextComponent messageTC = new TextComponent(": " + message);
        messageTC.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        String[] receivingPlayers = args[0].split(",");
        try (IContext ctx = tregmine.createContext()) {
            IPlayerDAO playerDAO = ctx.getPlayerDAO();

            for (String possiblePlayer : receivingPlayers) {
                List<GenericPlayer> candidates = tregmine.matchPlayer(possiblePlayer);

                if (candidates.size() != 1) {
                    error(player, "No player found by the name of " + possiblePlayer);
                    return true;
                }

                GenericPlayer receivingPlayer = candidates.get(0);

                boolean ignored;
                ignored = playerDAO.doesIgnore(receivingPlayer, player);

                if (player.getRank().canNotBeIgnored())
                    ignored = false;
                if (ignored)
                    continue;


                // Show message in senders terminal, as long as the recipient
                // isn't
                // invisible, to prevent /msg from giving away hidden players
                // presence

                if (!receivingPlayer.hasFlag(GenericPlayer.Flags.INVISIBLE) || player.getRank().canSeeHiddenInfo()) {
                    player.sendMessage(this.toFlag, receivingPlayer.decideVS(player), messageTC);
                } else {
                    error(player, "No player found by the name of " + possiblePlayer);
                }
                receivingPlayer.setLastMessenger(player.getName());
                // Send message to recipient
                receivingPlayer.sendNotification(Notification.MESSAGE, this.fromFlag,
                        player.decideVS(receivingPlayer), messageTC);
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
