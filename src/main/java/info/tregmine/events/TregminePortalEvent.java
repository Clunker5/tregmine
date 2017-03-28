package info.tregmine.events;

import info.tregmine.api.TregminePlayer;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class TregminePortalEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private World from;
    private World to;
    private TregminePlayer player;

    public TregminePortalEvent(World from, World to, TregminePlayer playerInvolved) {
        this.from = from;
        this.to = to;
        this.player = playerInvolved;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public World getFrom() {
        return from;
    }

    public void setFrom(World newFrom) {
        this.from = newFrom;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public TregminePlayer getPlayer() {
        return player;
    }

    public World getTo() {
        return to;
    }

    public void setTo(World newTo) {
        this.to = newTo;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
