package info.tregmine.discord.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.GenericPlayer.Flags;
import info.tregmine.discord.Discord;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerJoinLeaveListener implements Listener {

    Map<Player, Boolean> playerStatusIsOnline = new HashMap<Player, Boolean>();
    private Tregmine plugin;
    private Discord srv;

    public PlayerJoinLeaveListener(Discord srv) {
        this.srv = srv;
        this.plugin = this.srv.getPlugin();
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // If player is OP & update is available tell them
        // This is foolishness.
        // if (event.getPlayer().isOp() && Discord.updateIsAvailable)
        // event.getPlayer().sendMessage(ChatColor.AQUA + "An update to
        // Discord is available. Download it at
        // http://dev.bukkit.org/bukkit-plugins/discordsrv/");

        // Make sure join messages enabled
        if (!this.plugin.getConfig().getBoolean("discord.bridge-functionality.join-leave.join.enabled"))
            return;

        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.hasFlag(Flags.INVISIBLE)) {
            return;
        }

        // Assign player's status to online since they don't have silent join
        // permissions
        playerStatusIsOnline.put(event.getPlayer(), true);

        // Player doesn't have silent join permission, send join message
        this.srv.sendMessage(this.srv.getChatChannel(),
                this.plugin.getConfig().getString("discord.bridge-functionality.join-leave.join.format")
                        .replace("%username%", event.getPlayer().getName())
                        .replace("%displayname%", ChatColor.stripColor(event.getPlayer().getDisplayName())));
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Make sure quit messages enabled
        if (!this.plugin.getConfig().getBoolean("discord.bridge-functionality.join-leave.leave.enabled"))
            return;

        GenericPlayer player = plugin.getPlayerOffline(event.getPlayer());
        if (player.hasFlag(Flags.INVISIBLE)) {
            return;
        }

        // Remove player from status map to help with memory management
        playerStatusIsOnline.remove(event.getPlayer());

        // Player doesn't have silent quit, show quit message
        this.srv.sendMessage(this.srv.getChatChannel(),
                this.plugin.getConfig().getString("discord.bridge-functionality.join-leave.leave.format")
                        .replace("%username%", event.getPlayer().getName())
                        .replace("%displayname%", ChatColor.stripColor(event.getPlayer().getDisplayName())));
    }
}
