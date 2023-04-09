package com.georgev22.skinoverlay.event.events.user;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents a user event.
 */
public class UserEvent implements Event, Cancellable {

    /**
     * The user associated with this event.
     */
    private final UserManager.@NotNull User user;

    /**
     * Whether this event should be run asynchronously.
     */
    private final boolean async;

    /**
     * Whether this event has been cancelled.
     */
    private boolean cancelled;

    /**
     * Constructs a {@code UserEvent} with the specified user and asynchronous status.
     *
     * @param user  the user associated with this event
     * @param async whether this event should be run asynchronously
     */
    public UserEvent(@NotNull UserManager.User user, boolean async) {
        this.user = user;
        this.async = async;
    }

    /**
     * Returns the user associated with this event.
     *
     * @return the user associated with this event
     */
    public UserManager.@NotNull User getUser() {
        return user;
    }

    /**
     * Returns whether this event should be run asynchronously.
     *
     * @return {@code true} if this event should be run asynchronously, {@code false} otherwise
     */
    @Override
    public boolean runAsync() {
        return async;
    }

    /**
     * Cancels this event.
     *
     * @return {@code true} if the event was cancelled, {@code false} otherwise
     */
    @Override
    public boolean cancel() {
        return cancelled = true;
    }

    /**
     * Returns whether this event has been cancelled.
     *
     * @return {@code true} if this event has been cancelled, {@code false} otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}