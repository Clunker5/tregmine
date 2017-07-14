package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.Set;

import static org.bukkit.ChatColor.RED;

public class SetSpawnerCommand extends AbstractCommand {
    public SetSpawnerCommand(Tregmine tregmine) {
        super(tregmine, "setspawner", Tregmine.PermissionDefinitions.BUILDER_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(RED + "Type /spawner <mobname> whilst pointing " + "at a spawner");
            return false;
        }

        Block target = player.getDelegate().getTargetBlock((Set<Material>) null, 15);
        if (!target.getType().equals(Material.MOB_SPAWNER)) {
            player.sendMessage(RED + "Please point at a spawner.");
            return false;
        }

        CreatureSpawner spawner = (CreatureSpawner) target.getState();
        try {
            spawner.setSpawnedType(EntityType.valueOf(args[0].toUpperCase()));
        } catch (Exception error) {
            player.sendMessage(RED + "An error occured. Did you specify a valid " + "mob type?");
        }

        return true;
    }
}
