package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collection;

import static org.bukkit.ChatColor.WHITE;

public class ActionCommand extends AbstractCommand {
    public ActionCommand(Tregmine tregmine) {
        super(tregmine, "action");
    }

    private String argsToMessage(String[] args) {
        StringBuffer buf = new StringBuffer();
        buf.append(args[0]);
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            return false;
        }

        Server server = player.getServer();
        String channel = player.getChatChannel();
        String msg = argsToMessage(args);
        if (player.getRank() != Rank.RESIDENT && player.getRank() != Rank.SETTLER && player.getRank() != Rank.TOURIST
                && player.getRank() != Rank.UNVERIFIED) {
            msg = ChatColor.translateAlternateColorCodes('#', msg);
        } else {
            player.sendMessage(ChatColor.RED + "You are not allowed to use chat colors!");
        }

        Collection<? extends Player> players = server.getOnlinePlayers();
        for (Player tp : players) {
            GenericPlayer to = tregmine.getPlayer(tp);
            if (!channel.equals(to.getChatChannel())) {
                continue;
            }

            boolean ignored;
            try (IContext ctx = tregmine.createContext()) {
                IPlayerDAO playerDAO = ctx.getPlayerDAO();
                ignored = playerDAO.doesIgnore(to, player);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            if (player.getRank().canNotBeIgnored())
                ignored = false;
            if (ignored == true)
                continue;
            TextComponent begin = new TextComponent("* ");
            TextComponent middle = new TextComponent(player.decideVS(to));
            TextComponent end = new TextComponent(" " + WHITE + msg);
            to.sendMessage(begin, middle, end);
        }
        Tregmine.LOGGER.info("* " + player.getName() + " " + msg);
        if (player.getRank() != Rank.SENIOR_ADMIN && player.getRank() != Rank.JUNIOR_ADMIN) {
            msg = msg.replaceAll("@everyone", "").replaceAll("@here", "");
        }
        this.tregmine.getDiscordDelegate().getChatChannel().sendMessage("**" + player.getChatNameNoColor() + "** " + ChatColor.stripColor(msg)).complete();
        return true;
    }
}
