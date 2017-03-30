package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;

import static org.bukkit.ChatColor.YELLOW;

public class WeatherCommand extends AbstractCommand {
    public WeatherCommand(Tregmine tregmine) {
        super(tregmine, "weather");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        if (player.getWorld().getName() == "vanilla") {
            player.sendMessage(ChatColor.RED + "You cannot use that command in this world!");
            return true;
        }
        if (!player.getRank().canSetWeather()) {
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("/weather <downfall or clear>");
            player.resetPlayerWeather();
            return true;
        }

        try {
            WeatherType type = WeatherType.valueOf(args[0].toUpperCase());
            player.setPlayerWeather(type);
            player.sendMessage(YELLOW + "Weather set to " + type);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
