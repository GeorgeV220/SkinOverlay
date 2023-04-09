package com.georgev22.skinoverlay.event.events.user.data.load;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is triggered after a user's data is loaded.
 */
public class UserPostLoadEvent extends UserEvent implements Event {

    /**
     * Constructs a {@code UserPostLoadEvent} with the specified user and asynchronous status.
     *
     * @param user  the user associated with this event
     * @param async whether this event should be run asynchronously
     */
    public UserPostLoadEvent(@NotNull UserManager.User user, boolean async) {
        super(user, async);
    }
}