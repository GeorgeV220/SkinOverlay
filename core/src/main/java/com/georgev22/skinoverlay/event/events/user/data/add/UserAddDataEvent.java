package com.georgev22.skinoverlay.event.events.user.data.add;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.events.user.data.UserModifyDataEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that represents the addition of data to a user.
 */
public class UserAddDataEvent extends UserModifyDataEvent implements Event, Cancellable {

    private ObjectMap.Pair<String, ?> objectPair;

    /**
     * Constructs a {@code UserAddDataEvent} with the specified user, asynchronous status, and data.
     *
     * @param user       the user associated with this event
     * @param async      whether this event should be run asynchronously
     * @param objectPair the data being added to the user
     */
    public UserAddDataEvent(UserManager.@NotNull User user, boolean async, ObjectMap.@NotNull Pair<String, ?> objectPair) {
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
}
