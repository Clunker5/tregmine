package info.tregmine.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class AchievementListener implements Listener{
	private Tregmine t;

    public AchievementListener(Tregmine instance)
    {
        this.t = instance;
    }
    
    @EventHandler
    public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        TregminePlayer achiever = t.getPlayer(event.getPlayer());
        String alltogether = achiever.getChatName() + " won the achievement " + event.getAchievement().name();
       
    }
}
