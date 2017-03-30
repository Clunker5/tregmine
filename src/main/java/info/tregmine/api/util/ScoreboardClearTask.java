package info.tregmine.api.util;

import info.tregmine.api.GenericPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardClearTask implements Runnable {
    private GenericPlayer player;

    private ScoreboardClearTask(GenericPlayer player) {
        this.player = player;
    }

    public static void start(Plugin plugin, GenericPlayer player) {
        Runnable runnable = new ScoreboardClearTask(player);

        Server server = Bukkit.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, runnable, 400);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }

        try {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            player.setScoreboard(manager.getNewScoreboard());
        } catch (IllegalStateException e) {
            // We don't really care
        }
    }
}
