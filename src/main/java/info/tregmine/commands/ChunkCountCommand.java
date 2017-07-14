package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.Server;
import org.bukkit.World;

public class ChunkCountCommand extends AbstractCommand {
    public ChunkCountCommand(Tregmine tregmine) {
        super(tregmine, "chunkcount", Tregmine.PermissionDefinitions.ADMIN_REQUIRED);
    }

    @Override
    public boolean handleOther(Server server, String[] args) {
        for (World world : server.getWorlds()) {
            int chunks = world.getLoadedChunks().length;
            Tregmine.LOGGER.info(world.getName() + ": " + chunks + " chunks loaded, autosave: " + world.isAutoSave());
        }

        return true;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        Server server = player.getServer();
        for (World world : server.getWorlds()) {
            int chunks = world.getLoadedChunks().length;
            player.sendMessage(
                    world.getName() + ": " + chunks + " chunks loaded, autosave: " + world.isAutoSave());
        }

        return true;
    }
}
