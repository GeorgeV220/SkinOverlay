package com.georgev22.skinoverlay.storage.data;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.utilities.Entity;

import java.util.UUID;

/**
 * Represents data associated with a player identified by a UUID.
 * It implements the {@link Entity} interface, providing a unique identifier and custom data storage.
 */
public class Data implements Entity {

    /**
     * Custom data storage for this entity.
     */
    private final ConcurrentObjectMap<String, Object> customData;

    /**
     * The UUID of the player associated with this data.
     */
    private final UUID uuid;

    /**
     * Constructs a new Data object with the given UUID.
     *
     * @param uuid The UUID of the player associated with this data.
     */
    public Data(UUID uuid) {
        this.customData = new ConcurrentObjectMap<>();
        this.uuid = uuid;
    }

    /**
     * Gets the UUID of the player associated with this data.
     *
     * @return The UUID of the player.
     */
    @Override
    public UUID getId() {
        return this.uuid;
    }

    /**
     * Gets the custom data storage for this entity.
     *
     * @return The custom data storage.
     */
    @Override
    public ConcurrentObjectMap<String, Object> getCustomData() {
        return this.customData;
    }
}
