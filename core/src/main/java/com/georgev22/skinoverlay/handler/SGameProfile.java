package com.georgev22.skinoverlay.handler;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.UnmodifiableObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.profile.ProfileCreatedEvent;
import com.georgev22.skinoverlay.event.events.profile.property.SPropertyAddEvent;
import com.georgev22.skinoverlay.event.events.profile.property.SPropertyRemoveEvent;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.UUID;

/**
 * The SGameProfile class represents a simplified version of a GameProfile, containing a player's name, UUID, and properties.
 */
public abstract class SGameProfile {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * The player's name.
     */
    private final String name;

    /**
     * The player's UUID.
     */
    private final UUID uuid;

    /**
     * The player's properties.
     */
    private final ObjectMap<String, SProperty> properties;

    /**
     * Constructs an {@link SGameProfile} object with the specified name and UUID, and an empty properties map.
     *
     * @param name the player's name
     * @param uuid the player's UUID
     */
    public SGameProfile(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        this.properties = new HashObjectMap<>();
        this.skinOverlay.getEventManager().callEvent(new ProfileCreatedEvent(this, false));
    }

    /**
     * Constructs an {@link SGameProfile} object with the specified name, UUID, and properties map.
     *
     * @param name       the player's name
     * @param uuid       the player's UUID
     * @param properties the player's properties map
     */
    public SGameProfile(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
        this.skinOverlay.getEventManager().callEvent(new ProfileCreatedEvent(this, false));
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's UUID.
     *
     * @return the player's UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns an unmodifiable view of the player's properties map.
     *
     * @return an unmodifiable view of the player's properties map
     */
    @UnmodifiableView
    public UnmodifiableObjectMap<String, SProperty> getProperties() {
        return new UnmodifiableObjectMap<>(properties);
    }

    /**
     * Adds a property to the player's properties map.
     *
     * @param propertyName the name of the property to add
     * @param sProperty    the {@link SProperty} object representing the property to add
     * @return this {@link SGameProfile} object
     */
    public SGameProfile addProperty(String propertyName, SProperty sProperty) {
        if (sProperty == null) {
            return this;
        }
        SPropertyAddEvent sPropertyAddEvent = new SPropertyAddEvent(propertyName, sProperty, false);
        skinOverlay.getEventManager().callEvent(sPropertyAddEvent);
        if (sPropertyAddEvent.isCancelled()) {
            return this;
        }
        this.properties.append(sPropertyAddEvent.getPropertyName(), sPropertyAddEvent.getProperty());
        this.apply();
        return this;
    }

    /**
     * Removes a property from the player's properties map.
     *
     * @param propertyName the name of the property to remove
     * @return this {@link SGameProfile} object
     */
    public SGameProfile removeProperty(String propertyName) {
        if (!this.properties.containsKey(propertyName)) {
            return this;
        }
        SPropertyRemoveEvent sPropertyRemoveEvent = new SPropertyRemoveEvent(propertyName, this.properties.get(propertyName), false);
        skinOverlay.getEventManager().callEvent(sPropertyRemoveEvent);
        if (sPropertyRemoveEvent.isCancelled()) {
            return this;
        }
        this.properties.remove(sPropertyRemoveEvent.getPropertyName());
        this.apply();
        return this;
    }

    /**
     * Returns the {@link SProperty} associated with the specified property name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the {@code SProperty} object associated with the given property name
     */
    public SProperty getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    public abstract void apply();

    @Override
    public String toString() {
        return "SGameProfile{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                ", properties=" + properties +
                '}';
    }
}
