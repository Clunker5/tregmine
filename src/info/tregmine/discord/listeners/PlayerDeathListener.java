package info.tregmine.discord.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import info.tregmine.discord.DiscordSRV;

public class PlayerDeathListener implements Listener {
	
	Tregmine plugin;
	private DiscordSRV srv;
	
	public PlayerDeathListener(DiscordSRV srv) {
		this.srv = srv;
		this.plugin = this.srv.getPlugin();
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event) {
		// return if death messages are disabled
		if (!this.plugin.getConfig().getBoolean("discord.bridge-functionality.death-message"))
			return;

		TregminePlayer victim = plugin.getPlayer(event.getEntity());
		if (victim.hasFlag(Flags.INVISIBLE))
			return;

		this.srv.sendMessage(this.srv.getChatChannel(), ChatColor.stripColor(event.getDeathMessage()));
	}
}