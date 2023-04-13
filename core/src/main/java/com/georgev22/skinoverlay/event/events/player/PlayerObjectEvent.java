package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An event that represents a player object event.
 */
public class PlayerObjectEvent extends UserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The player object associated with this event.
     */
    private final PlayerObject playerObject;

    /**
     * Constructs a {@code PlayerObjectEvent} with the specified player object, user, and asynchronous status.
     *
     * @param playerObject the player object associated with this event
     * @param user         the user associated with this event, or {@code null} if there is no user associated with this event
     * @param async        whether this event should be run asynchronously
     */
    public PlayerObjectEvent(PlayerObject playerObject, @Nullable UserManager.User user, boolean async) {
        super(user == null ? new UserManager.User(playerObject.playerUUID()) : user, async);
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
}