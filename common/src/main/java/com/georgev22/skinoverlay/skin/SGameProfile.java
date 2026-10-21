package com.georgev22.skinoverlay.skin;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;

import java.util.UUID;

/**
 * Represents a platform-independent Minecraft game profile containing a
 * player's name, unique identifier, and associated properties.
 *
 * <p>Properties can be retrieved, added, updated, or removed to support
 * operations such as managing player skins and other profile metadata.</p>
 */
public class SGameProfile {

    private final String name;
    private final UUID uuid;
    private final ObjectMap<String, SProperty> properties = new HashObjectMap<>();

    /**
     * Creates a game profile with the specified name and unique identifier.
     *
     * @param name the player's name
     * @param uuid the player's unique identifier
     */
    public SGameProfile(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Creates a game profile with the specified name, unique identifier,
     * and initial properties.
     *
     * <p>The supplied properties are copied into this profile's internal
     * property map. Subsequent changes to this profile's properties do not
     * modify the supplied map.</p>
     *
     * @param name the player's name
     * @param uuid the player's unique identifier
     * @param properties the initial properties to associate with the profile
     */
    public SGameProfile(String name, UUID uuid, ObjectMap<String, SProperty> properties) {
        this.name = name;
        this.uuid = uuid;
        this.properties.putAll(properties);
    }

    /**
     * Gets the player's name associated with this profile.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's unique identifier.
     *
     * @return the player's UUID
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Gets the map of properties associated with this profile.
     *
     * <p>The returned map is the profile's internal mutable map. Changes
     * made directly to it affect this profile.</p>
     *
     * @return the mutable map of profile properties, keyed by property name
     */
    public ObjectMap<String, SProperty> getProperties() {
        return properties;
    }

    /**
     * Gets the property associated with the specified key.
     *
     * @param property the name of the property to retrieve
     * @return the associated property, or {@code null} if no such property exists
     */
    public SProperty getProperty(String property) {
        return this.properties.get(property);
    }

    /**
     * Adds a property to this profile.
     *
     * <p>The property's value is appended under the specified key according
     * to the behavior of the underlying {@link ObjectMap} implementation.</p>
     *
     * @param property the name of the property to add
     * @param value the property value
     * @return this profile, for method chaining
     */
    public SGameProfile addProperty(String property, SProperty value) {
        this.properties.append(property, value);
        return this;
    }

    /**
     * Removes the property associated with the specified key.
     *
     * @param property the name of the property to remove
     * @return this profile, for method chaining
     */
    public SGameProfile removeProperty(String property) {
        this.properties.remove(property);
        return this;
    }

    /**
     * Sets the property associated with the specified key.
     *
     * <p>This method delegates to {@link ObjectMap#append(Object, Object)}.
     * Whether an existing value is replaced or additional values are stored
     * depends on the underlying map implementation.</p>
     *
     * @param property the name of the property to set
     * @param value the property value
     * @return this profile, for method chaining
     */
    public SGameProfile setProperty(String property, SProperty value) {
        this.properties.append(property, value);
        return this;
    }
}
