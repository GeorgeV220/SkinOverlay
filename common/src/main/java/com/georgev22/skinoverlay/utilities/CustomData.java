package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.maps.ObjectMap;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * The {@code CustomData} class provides storage and management for custom key-value data.
 * It allows adding, retrieving, and managing data that can be associated with user-defined
 * keys and arbitrary object values. The class ensures strong type safety when retrieving
 * data by supports runtime type casting.
 * <p>
 * This class is immutable in terms of the field reference but allows modification of its
 * internal data structure. It is designed to support flexible extension of custom functionality.
 */
public final class CustomData {

    private final ObjectMap<String, Object> customData;

    public CustomData() {
        this.customData = ObjectMap.newConcurrentObjectMap();
    }

    /**
     * Sets the custom data for the menu. This method clears any existing custom data
     * and replaces it with the new data provided.
     *
     * @param customData The map of custom data to be set, where the keys are strings
     *                   and the values are arbitrary objects.
     */
    public void set(ObjectMap<String, Object> customData) {
        this.customData.clear();
        this.customData.putAll(customData);
    }

    /**
     * Sets a custom data entry in the internal data store.
     *
     * @param key   the key associated with the custom data, must not be null
     * @param value the value associated with the key, can be null
     */
    public void set(String key, Object value) {
        customData.append(key, value);
    }

    /**
     * Retrieves the custom data associated with the menu.
     *
     * @return An ObjectMap containing the custom data, where the keys are strings,
     * and the values are objects. If no custom data exists, returns an empty ObjectMap.
     */
    public ObjectMap<String, Object> getCustomData() {
        return customData;
    }

    /**
     * Retrieves a custom data object associated with the specified key.
     * Casts the retrieved object to the specified type if present.
     * Returns null if the key does not exist or if the cast fails.
     *
     * @param key the key associated with the desired custom data
     * @param <T> the type to cast the retrieved custom data to
     * @return the custom data cast to the specified type, or null if the key does not exist or the cast fails
     */
    @Nullable
    public <T> T get(String key) {
        if (!customData.containsKey(key)) {
            return null;
        }
        try {
            //noinspection unchecked
            return (T) customData.get(key);
        } catch (ClassCastException e) {
           SkinOverlay.getInstance().getLogger()
                    .log(Level.SEVERE, "Failed to cast custom data value for key " + key + " to the specified type.", e);
            return null;
        }
    }

    /**
     * Retrieves and casts a custom data object associated with the specified key.
     *
     * @param key  The key associated with the desired custom data
     * @param type The class to cast the retrieved custom data to
     * @param <T>  The type to cast the retrieved custom data to
     * @return The custom data cast to the specified type, or null if the key does not exist or the cast fails
     */
    @Nullable
    public <T> T getAs(String key, Class<T> type) {
        if (!customData.containsKey(key)) {
            return null;
        }
        try {
            Object value = customData.get(key);
            return type.cast(value);
        } catch (ClassCastException e) {
            SkinOverlay.getInstance().getLogger()
                    .log(Level.SEVERE, String.format("Failed to cast custom data value for key %s to the specified type.", key), e);
            return null;
        }
    }

    /**
     * Adds a custom data entry to the internal data store.
     *
     * @param key   the key associated with the custom data, must not be null
     * @param value the value associated with the key, can be null
     */
    public void add(String key, Object value) {
        customData.append(key, value);
    }

    /**
     * Removes a custom data entry from the internal data store.
     *
     * @param key the key associated with the custom data, must not be null
     */
    public void remove(String key) {
        if (key == null) return;
        if (!customData.containsKey(key)) return;
        customData.remove(key);
    }

    /**
     * Returns a string representation of the custom data.
     *
     * @return a string representation of the custom data
     */
    @Override
    public String toString() {
        return customData.toString();
    }
}
