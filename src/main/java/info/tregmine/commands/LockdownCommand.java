package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

public class LockdownCommand extends AbstractCommand {
    Tregmine plugin;

    public LockdownCommand(Tregmine tregmine) {
        super(tregmine, "lockdown", Tregmine.PermissionDefinitions.SENIOR_REQUIRED);
        plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You must specify <on|off>");
            return true;
        }
        System.out.println(args[0]);
        boolean state;
        if (args[0].equalsIgnoreCase("on")) {
            state = true;
        } else if (args[0].equalsIgnoreCase("off")) {
            state = false;
        } else {
            player.sendMessage(ChatColor.RED + "You must specify <on|off>");
            return true;
        }
        plugin.setLockdown(state);
        return true;
    }

}
