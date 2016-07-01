package com.scarsz.discordsrv.listeners;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.scarsz.discordsrv.DiscordSRV;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import info.tregmine.api.TregminePlayer.ChatState;
import net.dv8tion.jda.JDA;

public class ChatListener implements Listener {

	JDA api;
	Tregmine plugin;
	DiscordSRV srv;

	public ChatListener(JDA api, Tregmine tregmine, DiscordSRV discordsrv) {
		this.api = api;
		this.plugin = tregmine;
		this.srv = discordsrv;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		// ReportCanceledChatEvents debug message
		if (DiscordSRV.plugin.getConfig().getBoolean("ReportCanceledChatEvents"))
			DiscordSRV.plugin.getLogger().info("Chat message received, canceled: " + event.isCancelled());

		// return if event canceled
		if (DiscordSRV.plugin.getConfig().getBoolean("DontSendCanceledChatEvents") && event.isCancelled())
			return;

		// return if should not send in-game chat
		if (!DiscordSRV.plugin.getConfig().getBoolean("DiscordChatChannelMinecraftToDiscord"))
			return;

		// return if user is unsubscribed from Discord and config says don't
		// send those peoples' messages
		if (!DiscordSRV.getSubscribed(event.getPlayer().getUniqueId())
				&& !DiscordSRV.plugin.getConfig().getBoolean("MinecraftUnsubscribedMessageForwarding"))
			return;

		// return if doesn't match prefix filter
		if (!event.getMessage().startsWith(DiscordSRV.plugin.getConfig().getString("DiscordChatChannelPrefix")))
			return;

		TregminePlayer sender = plugin.getPlayer(event.getPlayer());
		// if(sender.getChatChannel().toLowerCase() != "global"){
		// return;
		// }
		if (sender.getChatChannel().toUpperCase() != "GLOBAL") {
			return;
		}

		if (sender.getChatState() != ChatState.CHAT) {
			return;
		}

		if (event.getMessage().toLowerCase().contains("%cancel%")) {
			return;
		}
		String name = "";
		if (sender.hasNick()) {
			name = sender.getNickname().getNicknamePlaintext();
		} else {
			name = sender.getChatNameNoColor();
		}
		String message = DiscordSRV.plugin.getConfig().getString("MinecraftChatToDiscordMessageFormat")
				.replaceAll("&([0-9a-qs-z])", "").replace("%message%", ChatColor.stripColor(event.getMessage()))
				.replace("%primarygroup%", srv.getPrimaryGroup(event.getPlayer())).replace("%displayname%", name)
				.replace("%username%", ChatColor.stripColor(event.getPlayer().getName()))
				.replace("%time%", new Date().toString());

		message = DiscordSRV.convertMentionsFromNames(message);
		DiscordSRV.sendMessage(DiscordSRV.chatChannel, message);
	}
}