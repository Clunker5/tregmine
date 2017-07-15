package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

public class BackCommand extends AbstractCommand {
    Tregmine plugin;

    public BackCommand(Tregmine tregmine) {
        super(tregmine, "back", Tregmine.PermissionDefinitions.STAFF_REQUIRED);
        plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (player.getLastPos() == null) {
            return error(player,  "You don't have a last location!");
        }
        boolean success = player.teleport(player.getLastPos());
        if (!success) {
            error(player,  "Failed to teleport back. Sorry!");
            if (!player.getLastPos().toString().isEmpty()) {
                error(player, "But... I can give you your coordinates. X" + player.getLastPos().getBlockX()
                                + " Y" + player.getLastPos().getBlockY() + " Z" + player.getLastPos().getBlockZ());
            }
        }
        return true;
    }
}
