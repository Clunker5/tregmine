package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public class RegenerateChunkCommand extends AbstractCommand {
    public RegenerateChunkCommand(Tregmine tregmine) {
        super(tregmine, "regeneratechunk", Tregmine.PermissionDefinitions.SENIOR_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getFillBlock1() == null) {
            player.sendMessage(ChatColor.RED + "You haven't made a selection! [Wand is the wooden shovel]");
            return true;
        }

        World world = player.getWorld();
        Block b1 = player.getFillBlock1();
        Chunk chunk = b1.getChunk();
        world.regenerateChunk(chunk.getX(), chunk.getZ());

        return true;
    }
}
