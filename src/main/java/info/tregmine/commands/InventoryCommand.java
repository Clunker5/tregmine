package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;

public class InventoryCommand extends AbstractCommand {
    public InventoryCommand(Tregmine tregmine) {
        super(tregmine, "inv", Tregmine.PermissionDefinitions.ADMIN_REQUIRED, true);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            error(player, "Incorrect usage:");
            player.sendMessage(ChatColor.AQUA + "/inv inspect <name> - Inspect someones inventory");
            player.sendMessage(
                    ChatColor.AQUA + "/inv reload <name> <true/false> - Reload inventory, optional save");
            player.sendMessage(ChatColor.AQUA + "/inv save - Save your current inventory to database");
            return true;
        }
        if ("save".equalsIgnoreCase(args[0]) && args.length == 1) {
            player.saveInventory(player.getCurrentInventory());
            return true;
        }
        if ("reload".equalsIgnoreCase(args[0]) && args.length == 3) {
            List<GenericPlayer> candidates = tregmine.matchPlayer(args[1]);
            if (candidates.size() != 1) {
                error(player, "Player: " + args[1] + " not found!");
                return true;
            }
            GenericPlayer candidate = candidates.get(0);
            boolean state = "true".equalsIgnoreCase(args[2]);

            if (state) {
                candidate.loadInventory(candidate.getCurrentInventory(), true);
            } else {
                candidate.loadInventory(candidate.getCurrentInventory(), false);
            }
            player.sendMessage(new TextComponent(ChatColor.GREEN + "Reloaded "), candidate.decideVS(player),
                    new TextComponent("'s inventory from DB!"));
            return true;
        }

        if ("inspect".equalsIgnoreCase(args[0]) && args.length == 2) {
            List<GenericPlayer> candidates = tregmine.matchPlayer(args[1]);
            if (candidates.size() != 1) {
                error(player, "Player: " + args[1] + " not found!");
                return true;
            }
            GenericPlayer candidate = candidates.get(0);
            player.openInventory(candidate.getInventory());
            player.sendMessage(new TextComponent(ChatColor.GREEN + "Inspecting "), candidate.decideVS(player),
                    new TextComponent("'s inventory!"));
            return true;
        }

        error(player, "Incorrect usage:");
        player.sendMessage(ChatColor.AQUA + "/inv inspect <name> - Inspect someones inventory");
        player.sendMessage(ChatColor.AQUA + "/inv reload <name> <true/false> - Reload inventory, optional save");
        player.sendMessage(ChatColor.AQUA + "/inv save - Save your current inventory to database");
        return true;
    }
}
