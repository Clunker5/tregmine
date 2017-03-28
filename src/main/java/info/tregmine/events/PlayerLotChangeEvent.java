package info.tregmine.events;

import info.tregmine.api.TregminePlayer;
import info.tregmine.zones.Lot;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerLotChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Location from;
    private Location to;
    private TregminePlayer player;
    private Lot oldLot;
    private Lot newLot;

    public PlayerLotChangeEvent(Location fromLoc, Location toLoc, TregminePlayer playerInvolved, Lot previousLot,
                                Lot currentLot) {
        this.from = fromLoc;
        this.to = toLoc;
        this.player = playerInvolved;
        this.oldLot = previousLot;
        this.newLot = currentLot;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location newFrom) {
        this.from = newFrom;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Lot getNew() {
        return newLot;
    }

    public void setNew(Lot value) {
        this.newLot = value;
    }

    public Lot getOld() {
        return oldLot;
    }

    public void setOld(Lot value) {
        this.oldLot = value;
    }

    public TregminePlayer getPlayer() {
        return player;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location newTo) {
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
