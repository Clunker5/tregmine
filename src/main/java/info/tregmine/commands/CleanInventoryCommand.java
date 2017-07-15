package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

public class CleanInventoryCommand extends AbstractCommand {
    public CleanInventoryCommand(Tregmine tregmine) {
        super(tregmine, "clean", null, true);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.sendMessage(ChatColor.AQUA + "Your inventory has been cleared.");
        player.getInventory().clear();
        return true;
    }
}
