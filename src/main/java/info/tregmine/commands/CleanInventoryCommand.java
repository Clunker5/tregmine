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

        if (args.length != 1 && !player.hasCommandStatus(CommandStatus.CLEAN_VERIFY)) {
            player.sendMessage(ChatColor.RED + "Are you sure you want to run that command?");
            player.sendMessage(ChatColor.RED + "Type /clean yes to confirm");
            player.setCommandStatus(CommandStatus.CLEAN_VERIFY);
            return true;
        } else if (args.length != 1 && player.hasCommandStatus(CommandStatus.CLEAN_VERIFY)) {
            player.sendMessage(ChatColor.RED + "Type /clean yes to confirm");
            return true;
        } else if (args[0].toLowerCase().contains("yes")) {
            player.removeCommandStatus(CommandStatus.CLEAN_VERIFY);
            player.sendMessage(ChatColor.AQUA + "Your inventory has been cleared.");
        } else {
            this.invalidArguments(player, "/clean <yes>");
            return true;
        }
        player.getInventory().clear();
        return true;
    }
}
