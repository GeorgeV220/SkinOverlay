package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

public class PlayerObjectConnectionEvent extends PlayerObjectEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ConnectionType connectionType;

    /**
     * Constructs a {@code PlayerObjectConnectionEvent} with the specified player object, connection type and asynchronous status.
     *
     * @param playerObject   the player object associated with this event
     * @param connectionType the player connection type
     * @param async          whether this event should be run asynchronously
     */
    public PlayerObjectConnectionEvent(PlayerObject playerObject, ConnectionType connectionType, boolean async) {
        super(playerObject, async);
        this.connectionType = connectionType;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum ConnectionType {
        CONNECT,
        DISCONNECT
    }
}
