package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IPlayerDAO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

public class PropertyCommand extends AbstractCommand {
    private Tregmine plugin;

    public PropertyCommand(Tregmine plugin) {
        super(plugin, "property");
        this.plugin = plugin;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String args[]) {
        if (player.getRank() != Rank.SENIOR_ADMIN) {
            player.sendMessage(ChatColor.RED + "You're not allowed to change player properties!");
            return true;
        }
        if (args.length <= 2) {
            player.sendMessage(ChatColor.RED + "You gave the wrong amount of arguments.");
            player.sendMessage(ChatColor.RED + "/property <target player> <key> <value>");
            return true;
        }
        List<GenericPlayer> matches = this.plugin.matchPlayer(args[0]);
        if (matches.size() != 1) {
            player.sendMessage(ChatColor.RED + "The target player specified does not exist");
            return true;
        }
        GenericPlayer target = matches.get(0);
        String updatedValue = "";
        for (String value : args) {
            if (value.equals(args[0]) || value.equals(args[1]))
                continue;
            if (value.length() == 0) {
                updatedValue = value;
            } else {
                updatedValue += " " + value;
            }
        }
        try (IContext ctx = tregmine.createContext()) {
            IPlayerDAO h = ctx.getPlayerDAO();
            h.updateProperty(target, args[1], updatedValue);
            player.sendMessage(new TextComponent(ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been set to "
                    + ChatColor.GOLD + updatedValue + ChatColor.GREEN + " for " + ChatColor.GOLD + player.getName()));
        } catch (DAOException g) {
            g.printStackTrace();
            player.sendMessage(ChatColor.RED + "Something went wrong!");
            return true;
        }

        return true;
    }
}
