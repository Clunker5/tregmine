package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;

public class SendBackCommand extends AbstractCommand {
    Tregmine plugin;

    public SendBackCommand(Tregmine tregmine) {
        super(tregmine, "sendback", Tregmine.PermissionDefinitions.SENIOR_REQUIRED);
        plugin = tregmine;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (args.length != 1) {
            error(player, "Arguments error: /sendback <player name>");
            return true;
        }
        List<GenericPlayer> players = plugin.matchPlayer(args[0]);
        if (players.size() != 1) {
            error(player, "Player is not online!");
            return true;
        }
        GenericPlayer target = players.get(0);
        if (target.getLastPos() == null) {
            error(player, "Player does not have a last location.");
            return true;
        }
        if (!target.teleport(target.getLastPos())) {
            Location pos = target.getLastPos();
            error(player, "Player could not be teleported. Here are the coordinates:");
            player.sendMessage(
                    ChatColor.AQUA + "X" + pos.getBlockX() + " Y" + pos.getBlockY() + " Z" + pos.getBlockZ());
            return true;
        }
        return true;
    }
}
