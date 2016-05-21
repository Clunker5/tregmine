package info.tregmine.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;

public class EggListener implements Listener {
	private Tregmine plugin;

	public EggListener(Tregmine instance) {
		this.plugin = instance;
		this.plugin.getLogger().info("Egg Listener is online!");
	}

	@EventHandler
	public void onEggThrown(PlayerEggThrowEvent event) {
		TregminePlayer player = plugin.getPlayer(event.getPlayer());

		if (!player.hasBlockPermission(player.getLocation(), true)) {
			event.setHatching(false);
		}
	}
}
