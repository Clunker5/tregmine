package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.CommandStatus;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

public class CleanInventoryCommand extends AbstractCommand {
    public CleanInventoryCommand(Tregmine tregmine) {
        super(tregmine, "clean");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        player.sendMessage(ChatColor.AQUA + "Your inventory has been cleared.");
        player.getInventory().clear();
        return true;
    }
}
