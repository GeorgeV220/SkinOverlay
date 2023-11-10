package com.georgev22.skinoverlay.event.events.user.data.add;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.data.UserModifyDataEvent;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.storage.data.User;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents the addition of data to a user.
 */
public class UserAddDataEvent extends UserModifyDataEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private ObjectMap.Pair<String, ?> objectPair;
    private boolean cancelled = false;

    /**
     * Constructs a {@code UserAddDataEvent} with the specified user, asynchronous status, and data.
     *
     * @param user       the user associated with this event
     * @param async      whether this event should be run asynchronously
     * @param objectPair the data being added to the user
     */
    public UserAddDataEvent(@NotNull User user, ObjectMap.@NotNull Pair<String, ?> objectPair, boolean async) {
        super(user, async);
        this.objectPair = objectPair;
    }

    /**
     * Returns the data being added to the user.
     *
     * @return the data being added to the user
     */
    public ObjectMap.Pair<String, ?> getData() {
        return objectPair;
    }

    /**
     * Sets the data being added to the user.
     *
     * @param objectPair the data being added to the user
     */
    public void setData(ObjectMap.Pair<String, ?> objectPair) {
        this.objectPair = objectPair;
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
