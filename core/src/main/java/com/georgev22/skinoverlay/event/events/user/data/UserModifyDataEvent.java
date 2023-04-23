package com.georgev22.skinoverlay.event.events.user.data;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents a modification of user data.
 */
public class UserModifyDataEvent extends UserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructs a {@code UserModifyDataEvent} with the specified user and asynchronous status.
     *
     * @param user  the user associated with this event
     * @param async whether this event should be run asynchronously
     */
    public UserModifyDataEvent(@NotNull UserManager.User user, boolean async) {
        super(user, async);
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