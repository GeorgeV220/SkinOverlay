package com.georgev22.skinoverlay.event.events.user.data.load;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered after a user's data is loaded.
 */
public class UserPostLoadEvent extends UserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    /**
     * Constructs a {@code UserPostLoadEvent} with the specified user and asynchronous status.
     *
     * @param user  the user associated with this event
     * @param async whether this event should be run asynchronously
     */
    public UserPostLoadEvent(@NotNull UserManager.User user, boolean async) {
        super(user, async);
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