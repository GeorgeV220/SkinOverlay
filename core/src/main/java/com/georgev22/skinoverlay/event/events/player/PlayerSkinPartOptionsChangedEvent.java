package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

public class PlayerSkinPartOptionsChangedEvent extends PlayerObjectEvent {

    private static final HandlerList handlers = new HandlerList();
    private final boolean hasSkinPartsChanged;

    /**
     * Constructs a {@code PlayerSkinPartOptionsChangedEvent} with the specified player object and asynchronous status.
     *
     * @param playerObject the player object associated with this event
     * @param async        whether this event should be run asynchronously
     */
    public PlayerSkinPartOptionsChangedEvent(PlayerObject playerObject, boolean hasSkinPartsChanged, boolean async) {
        super(playerObject, async);
        this.hasSkinPartsChanged = hasSkinPartsChanged;
    }

    public boolean hasSkinPartsChanged() {
        return hasSkinPartsChanged;
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
