package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User extends ConcurrentObjectMap<String, Object> {

    private final UUID uuid;

    /**
     * Creates a User instance.
     *
     * @param uuid Player Unique identifier
     */
    public User(UUID uuid) {
        this.uuid = uuid;
        append("uuid", uuid);
    }

    /**
     * Creates a User instance initialized with the given map.
     *
     * @param uuid User Unique ID
     * @param map  initial map
     * @see User#User(UUID)
     */
    public User(UUID uuid, final @NotNull ObjectMap<String, Object> map) {
        super(map.append("uuid", uuid));
        this.uuid = uuid;
    }

    /**
     * Returns User's Unique ID
     *
     * @return User's Unique ID
     */
    public UUID getUniqueId() {
        return uuid;
    }

    public String getSkinName() {
        return getString("skinName");
    }

    public Property getSkinProperty() {
        return get("skinProperty", Property.class);
    }

    @Nullable
    public Property getDefaultSkinProperty() {
        return get("defaultSkinProperty", Property.class);
    }

}
