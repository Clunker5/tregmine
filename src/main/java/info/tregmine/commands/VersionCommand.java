package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;

/**
 * Created by ericrabil on 4/2/17.
 */
public class VersionCommand extends AbstractCommand {

    private Tregmine plugin;

    public VersionCommand(Tregmine tregmine) {
        super(tregmine, "version");
        this.plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.sendMessage(this.plugin.version);
        return true;
    }
}
