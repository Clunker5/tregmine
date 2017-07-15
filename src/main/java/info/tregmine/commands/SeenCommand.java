package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.ILogDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.util.Date;

public class SeenCommand extends AbstractCommand {

    public SeenCommand(Tregmine tregmine) {
        super(tregmine, "seen");
    }

    @Override
    public boolean handleOther(Server server, String[] args) {
        if (args.length != 1) {
            return false;
        }

        GenericPlayer target = tregmine.getPlayerOffline(args[0]);
        if (target == null) {
            server.getConsoleSender().sendMessage("Could not find player: " + args[0]);
            return true;
        }

        try (IContext ctx = tregmine.createContext()) {
            ILogDAO logDAO = ctx.getLogDAO();
            Date seen = logDAO.getLastSeen(target);

            server.getConsoleSender().sendMessage(args[0] + " was last seen on: " + seen);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        GenericPlayer target = null;
        try {
            target = tregmine.getPlayerOffline(args[0]);
        } catch (NullPointerException e) {
            player.sendMessage(
                    new TextComponent(ChatColor.RED + "That player was not found, check the spelling and try again."));
            return true;
        }
        if (target == null) {
            error(player, "Could not find player: " + ChatColor.YELLOW + args[0]);
            return true;
        }

        try (IContext ctx = tregmine.createContext()) {
            ILogDAO logDAO = ctx.getLogDAO();
            Date seen = logDAO.getLastSeen(target);

            if (seen != null) {
                player.sendMessage(new TextComponent(ChatColor.GREEN + ""),
                        new TextComponent(target.getChatName()),
                        new TextComponent(ChatColor.YELLOW + " was last seen on: " + ChatColor.AQUA + seen));
            } else {
                player.sendMessage(new TextComponent(ChatColor.GREEN + ""),
                        new TextComponent(target.getChatName()),
                        new TextComponent(ChatColor.YELLOW + " hasn't been seen for a while."));
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
