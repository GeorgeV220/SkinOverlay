package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectUserEvent;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * This event is called when a player's skin is ready to be updated in the game.
 * This event is cancellable, which allows you to prevent the player's skin from being updated.
 * If you want to modify the skin options of a player, use {@link PlayerObjectPreUpdateSkinEvent}.
 */
public class PlayerObjectUpdateSkinEvent extends PlayerObjectUserEvent implements Cancellable {

    /**
     * A list of all registered handlers for this event.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The skin options of the player.
     */
    private SkinOptions skinOptions;

    /**
     * Constructs a new PlayerObjectUpdateSkinEvent.
     *
     * @param playerObject the player object related to this event.
     * @param user         the user who owns the player object.
     * @param skinOptions  the skin options to update the player's skin to.
     * @param async        whether this event should be handled asynchronously or not.
     *                     If true, the event will be handled on a separate thread.
     *                     If false, the event will be handled on the main thread.
     */
    public PlayerObjectUpdateSkinEvent(PlayerObject playerObject, UserManager.User user, @Nullable SkinOptions skinOptions, boolean async) {
        super(playerObject, user, async);
        this.skinOptions = skinOptions;
    }

    /**
     * Returns the skin options of the player.
     * If the skin options are null, this method will try to retrieve the skin options from the user's custom data.
     *
     * @return the skin options of the player.
     * @throws IOException            if an error occurs while retrieving the skin options from the user's custom data.
     * @throws ClassNotFoundException if the class of the serialized object cannot be found.
     */
    public SkinOptions getSkinOptions() throws IOException, ClassNotFoundException {
        return skinOptions == null ? Utilities.getSkinOptions(getUser().getCustomData("skinOptions")) : skinOptions;
    }

    /**
     * Sets the skin options of the player.
     *
     * @param skinOptions the new skin options of the player.
     */
    public void setSkinOptions(SkinOptions skinOptions) {
        getUser().addCustomData("skinOptions", this.skinOptions = skinOptions);
    }

    /**
     * Returns a list of all registered handlers for this event.
     *
     * @return a list of event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns a list of all registered handlers for this event.
     *
     * @return a list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
