package info.tregmine.discord.listeners;

import info.tregmine.api.GenericPlayer;
import info.tregmine.discord.DiscordDelegate;
import info.tregmine.events.TregmineChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftListener implements Listener {

    private DiscordDelegate delegate;

    public MinecraftListener(DiscordDelegate delegate) {
        this.delegate = delegate;
    }

    @EventHandler
    public void onTregmineChat(TregmineChatEvent event) {
        GenericPlayer sender = event.getPlayer();
        if (sender.isMuted() || sender.isHidden() || event.getMessage().contains("%cancel%")) {
            event.setCancelled(true);
            return;
        }
        this.delegate.sendChat("**" + sender.getName() + "**: " + event.getMessage().replaceAll("#[0-9a-fA-Fk-oK-O]", ""));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GenericPlayer player = this.delegate.getPlugin().getPlayer(event.getPlayer());
        if (player.isHidden())
            return;
        this.delegate.sendChat("*Welcome " + player.getChatNameNoColor() + "!*");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GenericPlayer player = this.delegate.getPlugin().getPlayer(event.getPlayer());
        if (player.isHidden())
            return;
        if (player.getQuitMessage() == null)
            this.delegate.sendChat("*Quit: " + player.getChatNameNoColor() + "*");
        else
            this.delegate.sendChat("*" + player.getChatNameNoColor() + " quit: **" + player.getQuitMessage() + "***");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        GenericPlayer player = this.delegate.getPlugin().getPlayer(event.getEntity());
        if (player.isHidden())
            return;
        this.delegate.sendChat(event.getDeathMessage());
    }

}
