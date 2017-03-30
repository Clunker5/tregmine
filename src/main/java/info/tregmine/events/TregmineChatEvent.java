package info.tregmine.events;

import info.tregmine.api.GenericPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TregmineChatEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private GenericPlayer player;
    private String message;
    private String channel;
    private boolean web;

    public TregmineChatEvent(GenericPlayer player, String message, String channel, boolean web) {
        this.player = player;
        this.message = message;
        this.channel = channel;
        this.web = web;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getMessage() {
        return message;
    }

    public GenericPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    public boolean isWebChat() {
        return web;
    }
}
