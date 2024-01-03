package com.georgev22.skinoverlay.storage.data;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import com.georgev22.library.yaml.serialization.SerializableAs;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.skin.SkinParts;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.google.gson.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents a player's skin data, including properties, skin parts, and skin name.
 * Extends {@link Data} and implements {@link ConfigurationSerializable}.
 */
@ApiStatus.NonExtendable
@SerializableAs("SkinOverlaySkin")
public class Skin extends Data implements ConfigurationSerializable {

    private SProperty property;
    private SkinParts skinParts;

    private String skinName;

    /**
     * Creates a new Skin object with the given UUID, initializing custom data.
     *
     * @param uuid The UUID of the player associated with this skin.
     */
    public Skin(UUID uuid) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("skinParts", this.skinParts = new SkinParts());
        addCustomData("skinName", this.skinName = "empty");
    }

    /**
     * Creates a new Skin object with the given UUID, SProperty, and skin name, initializing custom data.
     *
     * @param uuid      The UUID of the player associated with this skin.
     * @param sProperty The SProperty representing the player's skin properties.
     * @param skinName  The name of the skin.
     */
    public Skin(UUID uuid, SProperty sProperty, String skinName) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinName", this.skinName = skinName);
        try {
            addCustomData("skinParts", this.skinParts = new SkinParts(
                    new SerializableBufferedImage(SkinOverlay.getInstance().getSkinHandler().getSkinImage(sProperty)),
                    skinName
            ));
        } catch (IOException e) {
            SkinOverlay.getInstance().getLogger().log(Level.SEVERE, "Could not load skin " + skinName, e);
            addCustomData("skinParts", this.skinParts = new SkinParts(null, skinName));
        }
    }

    /**
     * Creates a new Skin object with the given UUID, SProperty, and SkinParts, initializing custom data.
     *
     * @param uuid      The UUID of the player associated with this skin.
     * @param sProperty The SProperty representing the player's skin properties.
     * @param skinParts The SkinParts containing the skin image.
     */
    public Skin(UUID uuid, SProperty sProperty, @NotNull SkinParts skinParts) {
        super(uuid);
        addCustomData("entity_id", uuid.toString());
        addCustomData("property", this.property = sProperty);
        addCustomData("skinParts", this.skinParts = skinParts);
        addCustomData("skinName", this.skinName = skinParts.getSkinName());
    }

    /**
     * Gets the SProperty representing the player's skin properties.
     *
     * @return The SProperty object.
     */
    public @Nullable SProperty skinProperty() {
        return getCustomData("property") != null ? getCustomData("property") : property;
    }

    /**
     * Gets the SkinParts containing the skin image.
     *
     * @return The SkinParts object.
     */
    public SkinParts skinParts() {
        return getCustomData("skinParts") != null ? getCustomData("skinParts") : skinParts;
    }

    /**
     * Gets the name of the skin.
     *
     * @return The name of the skin.
     */
    public String skinName() {
        return skinName;
    }

    /**
     * Sets the SkinParts for this skin.
     *
     * @param skinParts The SkinParts to set.
     */
    public void setSkinParts(SkinParts skinParts) {
        addCustomData("skinParts", this.skinParts = skinParts);
    }

    /**
     * Sets the SProperty for this skin.
     *
     * @param property The SProperty to set.
     */
    public void setProperty(SProperty property) {
        addCustomData("property", this.property = property);
    }

    /**
     * Sets the name of the skin.
     *
     * @param skinName The name of the skin.
     */
    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    /**
     * Gets the URL of the skin based on the SProperty.
     *
     * @return The URL of the skin.
     */
    public @Nullable String skinURL() {
        return JsonParser.parseString(new String(Base64.getDecoder().decode(property.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
    }

    /**
     * Returns a string representation of the Skin object.
     *
     * @return A string containing information about the Skin object.
     */
    @Override
    public String toString() {
        return "Skin{" +
                "property=" + property +
                ", skinParts=" + skinParts +
                ", skinURL=" + skinURL() +
                '}';
    }

    /**
     * Gets the UUID of the player associated with this skin.
     *
     * @return The UUID of the player.
     */
    @Override
    public UUID getId() {
        return this.getCustomData("entity_id") != null ? UUID.fromString(this.getCustomData("entity_id")) : null;
    }

    /**
     * Serializes the Skin object to a JSON string using the Gson library.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a Skin object using the Gson library.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized Skin object.
     */
    public static Skin fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, Skin.class);
    }

    /**
     * Serializes the Skin object to a map for YAML configuration serialization.
     *
     * @return A map containing the serialized data of the Skin object.
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("entity_id", getCustomData("entity_id"));
        map.put("property", property);
        map.put("skinParts", skinParts);
        map.put("skinName", skinName);
        return map;
    }

    /**
     * Deserializes a map to a Skin object for YAML configuration deserialization.
     *
     * @param data The map containing the serialized data of the Skin object.
     * @return The deserialized Skin object.
     */
    @Contract("_ -> new")
    public static @NotNull Skin deserialize(@NotNull Map<String, Object> data) {
        return new Skin(UUID.fromString((String) data.get("entity_id")), (SProperty) data.get("property"), (String) data.get("skinName"));
    }
}
