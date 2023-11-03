package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectUserEvent;
import com.georgev22.skinoverlay.storage.data.User;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is called before the player's skin is updated.
 */
public class PlayerObjectPreUpdateSkinEvent extends PlayerObjectUserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    /**
     * This event is called when a player's skin is about to be updated.
     * It is called before the new skin options are applied to the player
     * and allows other plugins to modify the skin options
     * or cancel the skin update entirely.
     *
     * <p>The event can be cancelled to prevent the skin from being updated. If the event is cancelled, the player's skin will not be changed.</p>
     *
     * <p>The event is fired asynchronously by default.</p>
     *
     * @param playerObject The player object being updated.
     * @param user         The user associated with the player object.
     * @param async        Whether the event is asynchronous.
     */
    public PlayerObjectPreUpdateSkinEvent(PlayerObject playerObject, User user, boolean async) {
        super(playerObject, user, async);
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
