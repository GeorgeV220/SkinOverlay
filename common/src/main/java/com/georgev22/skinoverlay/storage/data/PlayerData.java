package com.georgev22.skinoverlay.storage.data;

import com.georgev22.skinoverlay.utilities.CustomData;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents player-specific data within the SkinOverlay system.
 * This includes the player's UUID, default skin, current skin, and associated custom data.
 * Implements {@link Entity} for unified entity identification and data access.
 * <p>
 * The {@code equals} method compares only the UUID for identity checks,
 * while {@code equalsExact} compares all fields for strict equality.
 * </p>
 *
 * @author George
 */
public class PlayerData implements Entity {

    /**
     * The UUID of the player.
     */
    private final UUID uuid;

    /**
     * The custom data associated with this player.
     */
    private final CustomData customData = new CustomData();

    /**
     * The default skin assigned to this player.
     */
    private Skin defaultSkin;

    /**
     * The current skin being used by this player.
     */
    private Skin currentSkin;

    /**
     * Constructs a new {@code PlayerData} instance for the specified player UUID.
     *
     * @param uuid the UUID of the player
     */
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the UUID of this player.
     *
     * @return the player's UUID
     */
    @Override
    public UUID getId() {
        return this.uuid;
    }

    /**
     * Returns the default skin of this player.
     *
     * @return the default {@link Skin}, or {@code null} if not set
     */
    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    /**
     * Returns the current skin of this player.
     *
     * @return the current {@link Skin}, or {@code null} if not set
     */
    public Skin getCurrentSkin() {
        return currentSkin;
    }

    /**
     * Sets the current skin of this player.
     *
     * @param skin the new current {@link Skin}
     */
    public void setCurrentSkin(Skin skin) {
        this.currentSkin = skin;
    }

    /**
     * Sets the default skin of this player.
     *
     * @param skin the new default {@link Skin}
     */
    public void setDefaultSkin(Skin skin) {
        this.defaultSkin = skin;
    }

    /**
     * Returns the custom data container for this player.
     *
     * @return the {@link CustomData} instance
     */
    @Override
    public CustomData customData() {
        return this.customData;
    }

    /**
     * Checks whether this {@code PlayerData} is exactly equal to another object.
     * This strict comparison checks UUID, default skin, current skin, and custom data.
     *
     * @param o the object to compare with
     * @return {@code true} if all fields are exactly equal, otherwise {@code false}
     */
    @Override
    public boolean equalsExact(Object o) {
        if (!(o instanceof PlayerData other)) return false;
        return Objects.equals(this.uuid, other.uuid) &&
                Objects.equals(this.defaultSkin, other.defaultSkin) &&
                Objects.equals(this.currentSkin, other.currentSkin) &&
                Objects.equals(this.customData, other.customData);
    }

    /**
     * Checks whether this {@code PlayerData} is equal to another object.
     * Two {@code PlayerData} instances are considered equal if their UUIDs are equal.
     *
     * @param obj the object to compare with
     * @return {@code true} if the UUIDs are equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerData other)) return false;
        return Objects.equals(this.uuid, other.uuid);
    }

    /**
     * Returns a hash code value for this {@code PlayerData}.
     * The hash code is based solely on the player's UUID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
