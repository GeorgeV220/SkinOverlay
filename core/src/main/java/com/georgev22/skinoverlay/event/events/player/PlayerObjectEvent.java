package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents a player object event.
 */
public class PlayerObjectEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    /**
     * The player object associated with this event.
     */
    private final PlayerObject playerObject;

    /**
     * Constructs a {@code PlayerObjectEvent} with the specified player object and asynchronous status.
     *
     * @param playerObject the player object associated with this event
     * @param async        whether this event should be run asynchronously
     */
    public PlayerObjectEvent(PlayerObject playerObject, boolean async) {
        super(async);
        this.playerObject = playerObject;
    }

    /**
     * Returns the player object associated with this event.
     *
     * @return the player object associated with this event
     */
    public PlayerObject getPlayerObject() {
        return playerObject;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Cancels the event.
     *
     * @return {@code true} if the event was successfully cancelled, {@code false} otherwise
     */
    @Override
    public boolean cancel() {
        return cancelled = true;
    }

    /**
     * Returns whether the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}