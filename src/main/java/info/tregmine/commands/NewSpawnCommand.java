package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.Location;
import org.bukkit.World;

public class NewSpawnCommand extends AbstractCommand {
    public NewSpawnCommand(Tregmine tregmine) {
        super(tregmine, "newspawn", Tregmine.PermissionDefinitions.SENIOR_REQUIRED);
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        World world = player.getWorld();
        if (world == null) {
            // TODO: error message
            return false;
        }

        Location loc = player.getLocation();
        world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        return true;
    }
}
