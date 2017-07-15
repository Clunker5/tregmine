package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

import java.util.List;

public class FreezeCommand extends AbstractCommand {
    private Tregmine plugin;

    public FreezeCommand(Tregmine tregmine) {
        super(tregmine, "freeze", Tregmine.PermissionDefinitions.SENIOR_REQUIRED, true);
        plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length != 1) {
            error(player, "Invalid arguments! Use /freeze <player>");
            return true;
        }
        String raw = args[0];
        List<GenericPlayer> victims = plugin.matchPlayer(raw);
        if (victims.size() != 1) {
            error(player, "That player is not online!");
            return true;
        }
        GenericPlayer victim = victims.get(0);
        if (player.isInVanillaWorld()) {
            error(player, "You cannot freeze that player because they are in the vanilla world!");
            return true;
        }
        boolean newValue = !victim.getFrozen();
        victim.setFrozen(newValue);
        String getState = (newValue ? "frozen" : "unfrozen");
        ChatColor color = (newValue ? ChatColor.RED : ChatColor.GREEN);
        player.sendMessage(victim.getName() + ChatColor.BLUE + " has been " + getState + ".");
        victim.sendMessage(color + "You have been " + getState + ".");
        return true;
    }
}
