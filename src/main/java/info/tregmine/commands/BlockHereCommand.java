package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockHereCommand extends AbstractCommand {
    public BlockHereCommand(Tregmine tregmine) {
        super(tregmine, "blockhere", Tregmine.PermissionDefinitions.BUILDER_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld() == player.getPlugin().getVanillaWorld()
                || player.getWorld() == player.getPlugin().getVanillaNether()
                || player.getWorld() == player.getPlugin().getVanillaEnd()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }

        Block block = player.getWorld().getBlockAt(player.getLocation());
        block.setType(Material.DIRT);

        return true;
    }
}
