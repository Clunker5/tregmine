package info.tregmine.discord.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Flags;
import info.tregmine.discord.Discord;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    Tregmine plugin;
    private Discord srv;

    public PlayerDeathListener(Discord srv) {
        this.srv = srv;
        this.plugin = this.srv.getPlugin();
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        // return if death messages are disabled
        if (!this.plugin.getConfig().getBoolean("discord.bridge-functionality.death-message"))
            return;

        GenericPlayer victim = plugin.getPlayer(event.getEntity());
        if (victim.hasFlag(Flags.INVISIBLE))
            return;

        this.srv.sendMessage(this.srv.getChatChannel(), ChatColor.stripColor(event.getDeathMessage()));
    }
}