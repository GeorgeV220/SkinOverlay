package com.georgev22.skinoverlay.event.events.user.data.load;

import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.utilities.player.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An event that is triggered before a user's data is loaded.
 */
public class UserPreLoadEvent extends UserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private boolean cancelled = false;

    /**
     * Constructs a {@code UserPreLoadEvent} with the specified UUID and asynchronous status.
     *
     * @param uuid  the UUID of the user associated with this event
     * @param async whether this event should be run asynchronously
     */
    public UserPreLoadEvent(@NotNull UUID uuid, boolean async) {
        super(new User(uuid), async);
        this.uuid = uuid;
    }

    /**
     * Returns the UUID of the user associated with this event.
     *
     * @return the UUID of the user associated with this event
     */
    public UUID getUUID() {
        return uuid;
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}