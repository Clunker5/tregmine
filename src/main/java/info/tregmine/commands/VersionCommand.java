package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

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
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Tregmine " + this.plugin.getDescription().getVersion().replace("Beta", ChatColor.RED + "Beta"));
        return true;
    }
}
