package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectUserEvent;
import com.georgev22.skinoverlay.handler.Skin;
import com.georgev22.skinoverlay.storage.User;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * The skin of the player.
     */
    private Skin skin;
    private boolean cancelled = false;

    /**
     * Constructs a new PlayerObjectUpdateSkinEvent.
     *
     * @param playerObject the player object related to this event.
     * @param user         the user who owns the player object.
     * @param skin         the skin to update the player's skin to.
     * @param async        whether this event should be handled asynchronously or not.
     *                     If true, the event will be handled on a separate thread.
     *                     If false, the event will be handled on the main thread.
     */
    public PlayerObjectUpdateSkinEvent(PlayerObject playerObject, User user, @Nullable Skin skin, boolean async) {
        super(playerObject, user, async);
        this.skin = skin;
    }

    /**
     * Returns the skin options of the player.
     * If the skin options are null, this method will try to retrieve the skin options from the user's custom data.
     *
     * @return the skin options of the player.
     */
    public Skin getSkin() {
        return skin == null ? getUser().skin() : skin;
    }

    /**
     * Sets the skin options of the player.
     *
     * @param skin the new skin of the player.
     */
    public void setSkin(Skin skin) {
        getUser().addCustomData("skin", this.skin = skin);
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
