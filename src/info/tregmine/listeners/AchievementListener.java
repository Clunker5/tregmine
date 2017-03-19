package info.tregmine.listeners;

import org.bukkit.event.Listener;

import info.tregmine.Tregmine;

public class AchievementListener implements Listener {
	private Tregmine t;

	public AchievementListener(Tregmine instance) {
		this.t = instance;
	}

	// @EventHandler
	// public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent
	// event) {
	// event.setCancelled(true);
	// TregminePlayer achiever = t.getPlayer(event.getPlayer());
	// Achievement achievement = event.getAchievement();
	// achiever.awardAchievement(achievement);
	// t.broadcast(achiever.getChatName(), new TextComponent(
	// ChatColor.YELLOW + " won the achievement " + ChatColor.AQUA +
	// event.getAchievement().name()));
	// }
}
