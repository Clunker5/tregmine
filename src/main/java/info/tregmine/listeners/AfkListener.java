package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.TregminePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public class AfkListener implements Listener {
    private Tregmine t;

    public AfkListener(Tregmine instance) {
        this.t = instance;
    }

    TregminePlayer matchPlayer(Player player) {
        return t.getPlayer(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if ((e.getDamager() instanceof Player)) {
            TregminePlayer player = t.getPlayer((Player) e.getDamager());
            updateActivity(player);
        }
        if ((e.getEntity() instanceof Player)) {
            TregminePlayer player = t.getPlayer((Player) e.getEntity());
            if (player.isAfk()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBook(PlayerEditBookEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerGameMode(PlayerGameModeChangeEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerItemChange(PlayerItemHeldEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        t.getPlayer(e.getPlayer()).setLastOnlineActivity(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        TregminePlayer a = matchPlayer(e.getPlayer());
        if (a.isAfk()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    @EventHandler
    public void playerToggleSneak(PlayerToggleSneakEvent e) {
        updateActivity(matchPlayer(e.getPlayer()));
    }

    void updateActivity(TregminePlayer player) {
        final long currentTime = System.currentTimeMillis();
        player.setLastOnlineActivity(currentTime);
    }
}
