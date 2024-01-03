package com.georgev22.skinoverlay.storage.data;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import com.georgev22.library.yaml.serialization.SerializableAs;
import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a user with associated skin data, including default and current skins.
 * Extends {@link Data} and implements {@link ConfigurationSerializable}.
 */
@SerializableAs("SkinOverlayUser")
public class User extends Data implements ConfigurationSerializable {

    /**
     * Creates a new User object with the given UUID, initializing custom data.
     *
     * @param uuid The UUID of the user.
     */
    public User(UUID uuid) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
    }

    /**
     * Gets the default skin associated with the user.
     *
     * @return The default skin.
     */
    public Skin defaultSkin() {
        return getCustomData("defaultSkin");
    }

    /**
     * Gets the current skin associated with the user.
     *
     * @return The current skin.
     */
    public Skin skin() {
        return getCustomData("skin");
    }

    /**
     * Sets the current skin for the user.
     *
     * @param skin The skin to set as the current skin.
     */
    public void setSkin(Skin skin) {
        addCustomData("skin", skin);
    }

    /**
     * Sets the default skin for the user.
     *
     * @param skin The skin to set as the default skin.
     */
    public void setDefaultSkin(Skin skin) {
        addCustomData("defaultSkin", skin);
    }

    /**
     * Gets the UUID of the user.
     *
     * @return The UUID of the user.
     */
    @Override
    public UUID getId() {
        return this.getCustomData("entity_id") != null ? UUID.fromString(this.getCustomData("entity_id")) : null;
    }

    /**
     * Serializes the User object to a JSON string using the Gson library.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a User object using the Gson library.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized User object.
     */
    public static User fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, User.class);
    }

    /**
     * Serializes the User object to a map for YAML configuration serialization.
     *
     * @return A map containing the serialized data of the User object.
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("entity_id", this.getCustomData("entity_id"));
        map.put("defaultSkin", defaultSkin());
        map.put("skin", skin());
        return map;
    }

    /**
     * Deserializes a map to a User object for YAML configuration deserialization.
     *
     * @param map The map containing the serialized data of the User object.
     * @return The deserialized User object.
     */
    public static @NotNull User deserialize(@NotNull Map<String, Object> map) {
        User user = new User(UUID.fromString((String) map.get("entity_id")));
        user.setSkin((Skin) map.get("skin"));
        user.setDefaultSkin((Skin) map.get("defaultSkin"));
        return user;
    }
}
