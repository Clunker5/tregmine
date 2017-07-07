package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import static org.bukkit.ChatColor.YELLOW;

public class GameModeCommand extends AbstractCommand {
    private GameMode mode;

    public GameModeCommand(Tregmine tregmine, String name, GameMode mode) {
        super(tregmine, name);
        this.mode = mode;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName().equalsIgnoreCase("vanilla") || player.isInVanillaWorld()) {
            player.setFireTicks(30);
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (this.mode != null) {
            if (!player.getRank().getPermittedGamemodes().contains(this.mode)) {
                player.sendMessage(ChatColor.RED + "You don't have the permissions to switch to " + this.mode.name().toLowerCase() + "!");
                return true;
            }
            player.setGameMode(mode);
            player.sendMessage(YELLOW + "You are now in " + mode.toString().toLowerCase() + " mode. ");

            if (player.getRank().canFly()) {
                player.setAllowFlight(true);
            }
        } else {
            //Generic Mode!
            if (args.length != 1) {
                String modes = "";
                for (GameMode m : GameMode.values()) {
                    ChatColor color =
                            player.getRank().getPermittedGamemodes().contains(m) ? ChatColor.GREEN : ChatColor.RED;
                    modes += m.name().toLowerCase() + " ";
                }
                player.sendMessage(ChatColor.AQUA + "Gamemodes available: " + modes);
                return true;
            }
            GameMode switchTo;
            try {
                switchTo = GameMode.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "The specified gamemode does not exist!");
                return true;
            }
            if (!player.getRank().getPermittedGamemodes().contains(switchTo)) {
                player.sendMessage(ChatColor.RED + "You don't have the permissions to switch to " + switchTo.name().toLowerCase() + "!");
                return true;
            }
            player.setGameMode(switchTo);
            player.sendMessage(YELLOW + "You are now in " + switchTo.toString().toLowerCase() + " mode. ");

            if (player.getRank().canFly()) {
                player.setAllowFlight(true);
            }
        }

        return true;
    }
}
