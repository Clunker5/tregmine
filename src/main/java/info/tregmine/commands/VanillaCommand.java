package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;

public class VanillaCommand extends AbstractCommand {
    Tregmine plugin;

    public VanillaCommand(Tregmine t) {
        super(t, "vanilla", Tregmine.PermissionDefinitions.DONATOR_REQUIRED);
        plugin = t;
    }

    @Override
    public boolean handlePlayer(GenericPlayer sender, String[] args) {
        if (plugin.getVanillaWorld() == null) {
            sender.sendMessage(ChatColor.RED + "The server does not have the vanilla world enabled.");
            return true;
        }
        if (sender.getWorld() == plugin.getServer().getWorld("world")) {
            sender.gotoWorld(sender.getPlayer(), plugin.getServer().getWorld("vanilla").getSpawnLocation(),
                    ChatColor.YELLOW + "Thanks for riding the Starlight Express!",
                    ChatColor.RED + "The Starlight Express is having some issues, try again later.");
        } else if (sender.getWorld() == plugin.getVanillaWorld()) {
            sender.gotoWorld(sender.getPlayer(), plugin.getServer().getWorld("world").getSpawnLocation(),
                    ChatColor.YELLOW + "Thanks for riding the Starlight Express!",
                    ChatColor.RED + "The Starlight Express is having some issues, try again later.");
        } else {
            sender.sendMessage(
                    ChatColor.RED + "You cannot switch between worlds if you are not in WORLD or VANILLA");
        }
        return true;
    }
}
