package info.tregmine.events;

import info.tregmine.api.GenericPlayer;
import info.tregmine.zones.Zone;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerZoneChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Location from;
    private Location to;
    private GenericPlayer player;
    private Zone oldZone;
    private Zone newZone;

    public PlayerZoneChangeEvent(Location fromLoc, Location toLoc, GenericPlayer playerInvolved, Zone previousZone,
                                 Zone currentZone) {
        this.from = fromLoc;
        this.to = toLoc;
        this.player = playerInvolved;
        this.oldZone = previousZone;
        this.newZone = currentZone;
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

    public Zone getNew() {
        return newZone;
    }

    public void setNew(Zone value) {
        this.newZone = value;
    }

    public Zone getOld() {
        return oldZone;
    }

    public void setOld(Zone value) {
        this.oldZone = value;
    }

    public GenericPlayer getPlayer() {
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
