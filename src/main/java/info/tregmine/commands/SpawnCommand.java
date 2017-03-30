package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;

public class SpawnCommand extends AbstractCommand {
    public SpawnCommand(Tregmine tregmine) {
        super(tregmine, "spawn");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.teleportWithHorse(player.getWorld().getSpawnLocation());
        return true;
    }
}
