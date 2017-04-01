package info.tregmine.discord.listeners;

import info.tregmine.discord.Discord;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class AchievementListener implements Listener {

    private Discord srv;

    public AchievementListener(Discord srv) {
        this.srv = srv;
    }

    @EventHandler
    public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        // return if achievement messages are disabled
        if (!this.srv.getPlugin().getConfig().getBoolean("discord.bridge-functionality.achievements.enabled"))
            return;

        // return if achievement or player objects are fucking knackered
        if (event == null || event.getAchievement() == null || event.getPlayer() == null)
            return;

        this.srv.getChatChannel().sendMessage(
                ChatColor.stripColor(
                        this.srv.getPlugin().getConfig().getString("discord.bridge-functionality.achievements.format")
                                .replace("%username%", event.getPlayer().getName())
                                .replace("%displayname%", event.getPlayer().getDisplayName())
                                .replace("%world%", event.getPlayer().getWorld().getName())
                                .replace("%achievement%", event.getAchievement().toString())));
    }
}
