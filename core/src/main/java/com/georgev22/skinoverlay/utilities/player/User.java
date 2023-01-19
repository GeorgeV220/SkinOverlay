package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
        return getString("skinName", "default");
    }

    public Property getSkinProperty() {
        return get("skinProperty", getDefaultSkinProperty());
    }

    public Property getDefaultSkinProperty() {
        try {
            return get("defaultSkinProperty", SkinOverlay.getInstance().getSkinHandler().getSkin(getPlayer().orElseThrow()));
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<PlayerObject> getPlayer() {
        return get("playerObject", SkinOverlay.getInstance().getPlayer(uuid));
    }

}