package info.tregmine.discord.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.ChatState;
import info.tregmine.discord.Discord;
import net.dv8tion.jda.core.JDA;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;

public class ChatListener implements Listener {

    JDA api;
    Tregmine plugin;
    Discord srv;

    public ChatListener(Discord srv) {
        this.srv = srv;
        this.api = this.srv.getAPI();
        this.plugin = this.srv.getPlugin();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        // ReportCanceledChatEvents debug message
        if (this.plugin.getConfig().getBoolean("discord.debug.chat.report-cancelled-chat-events"))
            Tregmine.LOGGER.info("Chat message received, canceled: " + event.isCancelled());

        // return if event canceled
        if (this.plugin.getConfig().getBoolean("discord.debug.chat.dont-send-cancelled-chat-events")
                && event.isCancelled())
            return;

        // return if should not send in-game chat
        if (!this.plugin.getConfig().getBoolean("discord.bridge-functionality.minecraft-to-discord"))
            return;

        // return if user is unsubscribed from Discord and config says don't
        // send those peoples' messages
        if (!this.srv.getSubscribed(event.getPlayer().getUniqueId())
                && !this.plugin.getConfig().getBoolean("discord.bridge-functionality.forward-unsubscribed"))
            return;

        GenericPlayer sender = plugin.getPlayer(event.getPlayer());
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
        String message = this.plugin.getConfig().getString("discord.bridge-functionality.formatting.from-minecraft")
                .replaceAll("&([0-9a-qs-z])", "").replace("%message%", ChatColor.stripColor(event.getMessage()))
                .replace("%primarygroup%", srv.getPrimaryGroup(event.getPlayer())).replace("%displayname%", name)
                .replace("%username%", ChatColor.stripColor(event.getPlayer().getName()))
                .replace("%time%", new Date().toString());

        message = this.srv.convertMentionsFromNames(message);
        this.srv.sendMessage(this.srv.getChatChannel(), message);
    }
}