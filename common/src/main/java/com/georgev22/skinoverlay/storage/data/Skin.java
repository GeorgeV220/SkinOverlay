package com.georgev22.skinoverlay.storage.data;

import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.gson.SkinTypeAdapter;
import com.georgev22.skinoverlay.utilities.CustomData;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a skin within the SkinOverlay system.
 * A skin is uniquely identified by its UUID and contains a {@link SProperty}
 * defining its texture and signature data, as well as associated {@link CustomData}.
 * <p>
 * The {@code equals} method compares only the skin's UUID for identity checks,
 * while {@code equalsExact} compares all fields for strict equality.
 * </p>
 */
public class Skin implements Entity {

    /**
     * The custom data associated with this skin.
     */
    private final CustomData customData = new CustomData();

    /**
     * The unique identifier for this skin.
     */
    private final UUID skinUniqueId;

    /**
     * The property containing texture and signature information for this skin.
     */
    private SProperty property;

    /**
     * The skin parts associated with this skin.
     */
    private SkinParts skinParts;

    /**
     * Constructs a new {@code Skin} instance with the specified UUID.
     *
     * @param id the unique identifier for the skin
     */
    public Skin(UUID id) {
        this.skinUniqueId = id;
    }

    /**
     * Returns the unique identifier of this skin.
     *
     * @return the skin's UUID
     */
    @Override
    public UUID getId() {
        return this.skinUniqueId;
    }

    /**
     * Returns the {@link SProperty} associated with this skin.
     *
     * @return the skin property, or {@code null} if not set
     */
    public SProperty getProperty() {
        return property;
    }

    /**
     * Sets the {@link SProperty} associated with this skin.
     *
     * @param property the skin property to set
     */
    public void setProperty(SProperty property) {
        this.property = property;
    }

    /**
     * Returns the {@link SkinParts} associated with this skin.
     *
     * @return the skin parts, or {@code null} if not set
     */
    public SkinParts getSkinParts() {
        return skinParts;
    }

    /**
     * Sets the {@link SkinParts} associated with this skin.
     *
     * @param skinParts the skin parts to set
     */
    public void setSkinParts(SkinParts skinParts) {
        this.skinParts = skinParts;
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
     * Returns the custom data container for this skin.
     *
     * @return the {@link CustomData} instance
     */
    @Override
    public CustomData customData() {
        return this.customData;
    }

    /**
     * Checks whether this {@code Skin} is exactly equal to another object.
     * This strict comparison checks UUID, property, and custom data.
     *
     * @param o the object to compare with
     * @return {@code true} if all fields are exactly equal, otherwise {@code false}
     */
    @Override
    public boolean equalsExact(Object o) {
        if (!(o instanceof Skin other)) return false;
        return Objects.equals(this.skinUniqueId, other.skinUniqueId) &&
                Objects.equals(this.property, other.property) &&
                Objects.equals(this.customData, other.customData);
    }

    /**
     * Checks whether this {@code Skin} is equal to another object.
     * Two {@code Skin} instances are considered equal if their UUIDs are equal.
     *
     * @param obj the object to compare with
     * @return {@code true} if the UUIDs are equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Skin other)) return false;
        return Objects.equals(this.skinUniqueId, other.skinUniqueId);
    }

    /**
     * Returns a hash code value for this {@code Skin}.
     * The hash code is based solely on the skin's UUID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return this.skinUniqueId.hashCode();
    }

    @ApiStatus.Internal
    public String toBase64() {
        return Base64.getEncoder().encodeToString(SkinTypeAdapter.toJson(this).getBytes(StandardCharsets.UTF_8));
    }

    @ApiStatus.Internal
    public static Skin fromBase64(String base64) {
        return SkinTypeAdapter.fromJson("", new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8));
    }
}
