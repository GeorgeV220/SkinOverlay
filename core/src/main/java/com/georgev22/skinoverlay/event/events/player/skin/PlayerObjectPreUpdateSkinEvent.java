package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectEvent;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that is called before the player's skin is updated.
 */
public class PlayerObjectPreUpdateSkinEvent extends PlayerObjectEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

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
     * @param user         The user associated with the player object, or null if the player is not a valid user.
     * @param async        Whether the event is asynchronous.
     */
    public PlayerObjectPreUpdateSkinEvent(PlayerObject playerObject, UserManager.@Nullable User user, boolean async) {
        super(playerObject, user, async);
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
