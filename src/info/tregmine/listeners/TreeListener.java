package info.tregmine.listeners;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import info.tregmine.Tregmine;

public class TreeListener extends JavaPlugin implements Listener{
	private Tregmine plugin;
	private final Random rand = new Random();
	public TreeListener(Tregmine tregmine){
		plugin = tregmine;
	}
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
			return;
		}

		final World world = block.getWorld();
		final int x = block.getX();
		final int y = block.getY();
		final int z = block.getZ();
		final int range = 4;
		final int off = range + 1;

		if (!validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off)) {
			return;
		}

		new BukkitRunnable() {
			public void run() {
				for (int offX = -range; offX <= range; offX++) {
					for (int offY = -range; offY <= range; offY++) {
						for (int offZ = -range; offZ <= range; offZ++) {
							Material m = world.getBlockAt(x + offX, y + offY, z + offZ).getType();
							if (m == Material.LEAVES || m == Material.LEAVES_2) {
								breakLeaf(world, x + offX, y + offY, z + offZ);
							}
						}
					}
				}
			}
		}.runTask(this);
	}

	private void breakLeaf(World world, int x, int y, int z) {
		Block block = world.getBlockAt(x, y, z);

		if ((block.getData() & 4) == 4) {
			return; // player placed leaf, ignore
		}

		byte range = 4;
		byte max = 32;
		int[] blocks = new int[max * max * max];
		int off = range + 1;
		int mul = max * max;
		int div = max / 2;

		if (validChunk(world, x - off, y - off, z - off, x + off, y + off, z + off)) {
			int offX;
			int offY;
			int offZ;
			Material type;

			for (offX = -range; offX <= range; offX++) {
				for (offY = -range; offY <= range; offY++) {
					for (offZ = -range; offZ <= range; offZ++) {
						type = world.getBlockAt(x + offX, y + offY, z + offZ).getType();
						blocks[(offX + div) * mul + (offY + div) * max + offZ + div] = (type == Material.LOG || type == Material.LOG_2 ? 0 : (type == Material.LEAVES || type == Material.LEAVES_2 ? -2 : -1));
					}
				}
			}

			int type1;
			for (offX = 1; offX <= 4; offX++) {
				for (offY = -range; offY <= range; offY++) {
					for (offZ = -range; offZ <= range; offZ++) {
						for (type1 = -range; type1 <= range; type1++) {
							if (blocks[(offY + div) * mul + (offZ + div) * max + type1 + div] == offX - 1) {
								if (blocks[(offY + div - 1) * mul + (offZ + div) * max + type1 + div] == -2)
									blocks[(offY + div - 1) * mul + (offZ + div) * max + type1 + div] = offX;

								if (blocks[(offY + div + 1) * mul + (offZ + div) * max + type1 + div] == -2)
									blocks[(offY + div + 1) * mul + (offZ + div) * max + type1 + div] = offX;

								if (blocks[(offY + div) * mul + (offZ + div - 1) * max + type1 + div] == -2)
									blocks[(offY + div) * mul + (offZ + div - 1) * max + type1 + div] = offX;

								if (blocks[(offY + div) * mul + (offZ + div + 1) * max + type1 + div] == -2)
									blocks[(offY + div) * mul + (offZ + div + 1) * max + type1 + div] = offX;

								if (blocks[(offY + div) * mul + (offZ + div) * max + (type1 + div - 1)] == -2)
									blocks[(offY + div) * mul + (offZ + div) * max + (type1 + div - 1)] = offX;

								if (blocks[(offY + div) * mul + (offZ + div) * max + type1 + div + 1] == -2)
									blocks[(offY + div) * mul + (offZ + div) * max + type1 + div + 1] = offX;
							}
						}
					}
				}
			}
		}

		if (blocks[div * mul + div * max + div] < 0) {
			LeavesDecayEvent event = new LeavesDecayEvent(block);
			plugin.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				return;
			}

			block.breakNaturally();

			if (rand.nextInt(10) == 0) {
				world.playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
			}
		}
	}

	public boolean validChunk(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		if (maxY >= 0 && minY < world.getMaxHeight()) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			maxZ >>= 4;

			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (!world.isChunkLoaded(x, z)) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}
}
