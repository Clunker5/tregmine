package com.scarsz.discordsrv.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.scarsz.discordsrv.DiscordSRV;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.Flags;
import net.dv8tion.jda.JDA;

public class PlayerDeathListener implements Listener {
	JDA api;
	Tregmine plugin;

	public PlayerDeathListener(JDA api, Tregmine tregmine) {
		this.api = api;
		this.plugin = tregmine;
	}

	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent event) {
		// return if death messages are disabled
		if (!DiscordSRV.plugin.getConfig().getBoolean("MinecraftPlayerDeathMessageEnabled"))
			return;

		TregminePlayer victim = plugin.getPlayer(event.getEntity());
		if (victim.hasFlag(Flags.INVISIBLE))
			return;

		DiscordSRV.sendMessage(DiscordSRV.chatChannel, ChatColor.stripColor(event.getDeathMessage()));
	}
}