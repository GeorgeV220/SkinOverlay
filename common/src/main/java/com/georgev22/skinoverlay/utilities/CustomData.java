package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.Cloneable;
import java.util.Map;
import java.util.function.Supplier;
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
public final class CustomData implements Cloneable, Copyable<CustomData> {

    private ObjectMap<String, Object> customData;

    public CustomData() {
        this.customData = ObjectMaps.newConcurrentHashObjectMap();
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
     * Sets the custom data for the menu. This method clears any existing custom data
     * and replaces it with the new data provided.
     *
     * @param customData The map of custom data to be set, where the keys are strings
     *                   and the values are arbitrary objects.
     */
    public void set(Map<String, Object> customData) {
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
     * Sets a dynamic custom data entry using a {@link Supplier}. The value is computed
     * each time it is retrieved.
     *
     * @param key      the key associated with the custom data, must not be null
     * @param supplier a {@link Supplier} that provides the value when requested, must not be null
     * @param <T>      the type of the value provided by the supplier
     */
    public <T> void setDynamic(String key, Supplier<T> supplier) {
        this.customData.append(key, supplier);
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
     * If the value is a {@link Supplier}, it is evaluated and the result is cast to the specified type.
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
            Object value = customData.get(key);
            //noinspection unchecked
            return value instanceof Supplier ? ((Supplier<T>) value).get() : (T) value;
        } catch (ClassCastException e) {
            SkinOverlay.getInstance().getLogger()
                    .log(Level.SEVERE, "Failed to cast custom data value for key " + key + " to the specified type.", e);
            return null;
        }
    }

    /**
     * Retrieves a custom data object associated with the specified key.
     * If the key does not exist, returns the default value.
     *
     * @param key          the key associated with the desired custom data
     * @param defaultValue the default value to return if the key does not exist
     * @param <T>          the type of the value
     * @return the custom data object associated with the specified key, or the default value if the key does not exist
     */
    @NotNull
    public <T> T getOr(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Retrieves and casts a custom data object associated with the specified key.
     * If the value is a {@link Supplier}, it is evaluated and the result is cast to the specified type each time.
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
            if (value instanceof Supplier<?> supplier) {
                Object suppliedValue = supplier.get();
                return type.cast(suppliedValue);
            } else {
                return type.cast(value);
            }
        } catch (ClassCastException e) {
            SkinOverlay.getInstance().getLogger()
                    .log(Level.SEVERE, String.format("Failed to cast custom data value for key %s to the specified type.", key), e);
            return null;
        }
    }

    /**
     * Retrieves and casts a custom data object associated with the specified key.
     * If the value is a {@link Supplier}, it is evaluated and the result is cast to the specified type each time.
     * If the key does not exist, returns the default value.
     *
     * @param key          The key associated with the desired custom data
     * @param defaultValue The default value to return if the key does not exist
     * @param type         The class to cast the retrieved custom data to
     * @param <T>          The type to cast the retrieved custom data to
     * @return The custom data cast to the specified type, or the default value if the key does not exist or the cast fails
     */
    @NotNull
    public <T> T getOr(String key, T defaultValue, Class<T> type) {
        T value = getAs(key, type);
        return value != null ? value : defaultValue;
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

    @Override
    public @NotNull CustomData clone() {
        try {
            CustomData clone = (CustomData) super.clone();
            clone.customData = ObjectMaps.newConcurrentHashObjectMap();
            for (Map.Entry<String, Object> entry : customData.entrySet()) {
                clone.customData.put(entry.getKey(), DeepCloner.cloneValue(entry.getValue()));
            }

            return clone;
        } catch (Exception e) {
            throw new AssertionError("Something went wrong while cloning CustomData", e);
        }
    }

    @Override
    public @NotNull CustomData shallowCopy() {
        try {
            return (CustomData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Something went wrong while shallow copying CustomData", e);
        }
    }

    @Override
    public @NotNull CustomData deepCopy() {
        CustomData copy = new CustomData();
        copy.customData = ObjectMaps.newConcurrentHashObjectMap();
        for (Map.Entry<String, Object> entry : customData.entrySet()) {
            copy.customData.put(entry.getKey(), DeepCloner.cloneValue(entry.getValue()));
        }

        return copy;
    }
}
