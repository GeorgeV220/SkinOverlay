package com.georgev22.skinoverlay.maps;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ObjectMap<K, V> extends Map<K, V> {

    /**
     * Creates a new empty {@link LinkedObjectMap} instance.
     *
     * @return a new empty {@link LinkedObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull <K, V> LinkedObjectMap<K, V> newLinkedObjectMap() {
        return new LinkedObjectMap<>();
    }

    /**
     * Creates a new empty {@link ConcurrentObjectMap} instance.
     *
     * @return a new empty {@link ConcurrentObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull <K, V> ConcurrentObjectMap<K, V> newConcurrentObjectMap() {
        return new ConcurrentObjectMap<>();
    }

    /**
     * Creates a new empty {@link HashObjectMap} instance.
     *
     * @return a new empty {@link HashObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull <K, V> HashObjectMap<K, V> newHashObjectMap() {
        return new HashObjectMap<>();
    }

    /**
     * Creates a new empty {@link TreeObjectMap} instance.
     *
     * @return a new empty {@link TreeObjectMap} instance.
     */
    @Contract(" -> new")
    static @NotNull <K, V> TreeObjectMap<K, V> newTreeObjectMap() {
        return new TreeObjectMap<>();
    }

    /**
     * Creates a {@link LinkedObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link LinkedObjectMap#LinkedObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> LinkedObjectMap<K, V> newLinkedObjectMap(ObjectMap<K, V> map) {
        return new LinkedObjectMap<>(map);
    }

    /**
     * Creates a {@link LinkedObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link LinkedObjectMap#LinkedObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> LinkedObjectMap<K, V> newLinkedObjectMap(Map<K, V> map) {
        return new LinkedObjectMap<>(map);
    }

    /**
     * Creates a {@link ConcurrentObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link ConcurrentObjectMap#ConcurrentObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> ConcurrentObjectMap<K, V> newConcurrentObjectMap(ObjectMap<K, V> map) {
        return new ConcurrentObjectMap<>(map);
    }

    /**
     * Creates a {@link ConcurrentObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link ConcurrentObjectMap#ConcurrentObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> ConcurrentObjectMap<K, V> newConcurrentObjectMap(Map<K, V> map) {
        return new ConcurrentObjectMap<>(map);
    }

    /**
     * Creates a {@link HashObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link HashObjectMap#HashObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> HashObjectMap<K, V> newHashObjectMap(ObjectMap<K, V> map) {
        return new HashObjectMap<>(map);
    }

    /**
     * Creates a {@link HashObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link HashObjectMap#HashObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> HashObjectMap<K, V> newHashObjectMap(Map<K, V> map) {
        return new HashObjectMap<>(map);
    }

    /**
     * Creates a {@link TreeObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link TreeObjectMap#TreeObjectMap(ObjectMap)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> TreeObjectMap<K, V> newTreeObjectMap(ObjectMap<K, V> map) {
        return new TreeObjectMap<>(map);
    }

    /**
     * Creates a {@link TreeObjectMap} instance with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@link TreeObjectMap#TreeObjectMap(Map)} initialized with the mappings from {@code map}
     */
    @Contract("_ -> new")
    static @NotNull <K, V> TreeObjectMap<K, V> newTreeObjectMap(Map<K, V> map) {
        return new TreeObjectMap<>(map);
    }


    /**
     * Put/replace the given key/value pair into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append("b", 2)}
     * </pre>
     *
     * @param key   key
     * @param value value
     * @return this
     */
    ObjectMap<K, V> append(final K key, final V value);

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    ObjectMap<K, V> append(final Map<K, V> map);

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    ObjectMap<K, V> append(final ObjectMap<K, V> map);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue("b", 2, check2)}
     * </pre>
     *
     * @param key    key
     * @param value  value
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final K key, final V value, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue("b", 3, 4, check2)}
     * </pre>
     *
     * @param key          key
     * @param valueIfTrue  the value if the ifTrue is true
     * @param valueIfFalse the value if the ifTrue is false
     * @param ifTrue       ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final K key, final V valueIfTrue, final V valueIfFalse, boolean ifTrue);

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final Map<K, V> map, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final Map<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final ObjectMap<K, V> map, boolean ifTrue);

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    ObjectMap<K, V> appendIfTrue(final ObjectMap<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Removes the entry with the specified key from the ObjectMap.
     *
     * @param key the key of the entry to be removed
     * @return the modified ObjectMap with the specified entry removed, or the original ObjectMap if the key was not found
     */
    ObjectMap<K, V> removeEntry(final K key);

    /**
     * Removes all entries with keys present in the specified map from the ObjectMap.
     *
     * @param map the map containing the keys to be removed
     * @return the modified ObjectMap with the entries corresponding to the specified keys removed
     */
    ObjectMap<K, V> removeEntries(final Map<K, V> map);

    /**
     * Removes all entries with keys present in the specified ObjectMap from the ObjectMap.
     *
     * @param map the ObjectMap containing the keys to be removed
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified ObjectMap removed
     */
    ObjectMap<K, V> removeEntries(final ObjectMap<K, V> map);

    /**
     * Removes the entry with the specified key from the ObjectMap if the condition is true.
     *
     * @param key    the key of the entry to be removed
     * @param ifTrue the condition to check before removing the entry
     * @return the modified ObjectMap with the specified entry removed if the condition is true, or the original ObjectMap otherwise
     */
    ObjectMap<K, V> removeEntryIfTrue(final K key, boolean ifTrue);

    /**
     * Removes all entries with keys present in the specified map from the ObjectMap if the condition is true.
     *
     * @param map    the map containing the keys to be removed
     * @param ifTrue the condition to check before removing the entries
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified map removed if the condition is true, or the original ObjectMap otherwise
     */
    ObjectMap<K, V> removeEntriesIfTrue(final Map<K, V> map, boolean ifTrue);

    /**
     * Removes all entries with keys present in the specified ObjectMap from the ObjectMap if the condition is true.
     *
     * @param map    the ObjectMap containing the keys to be removed
     * @param ifTrue the condition to check before removing the entries
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified ObjectMap removed if the condition is true, or the original ObjectMap otherwise
     */
    ObjectMap<K, V> removeEntriesIfTrue(final ObjectMap<K, V> map, boolean ifTrue);

    /**
     * Gets the value of the given key as an Integer.
     *
     * @param key the key
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    Integer getInteger(final Object key);

    /**
     * Gets the value of the given key as a primitive int.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    int getInteger(final Object key, final int defaultValue);

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key the key
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    Long getLong(final Object key);

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    Long getLong(final Object key, final long defaultValue);

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key the key
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    Double getDouble(final Object key);

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    Double getDouble(final Object key, final double defaultValue);

    /**
     * Gets the value of the given key as a String.
     *
     * @param key the key
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    String getString(final Object key);

    /**
     * Gets the value of the given key as a String.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    String getString(final Object key, final String defaultValue);

    /**
     * Gets the value of the given key as a Boolean.
     *
     * @param key the key
     * @return the value as a Boolean, which may be null
     * @throws ClassCastException if the value is not an boolean
     */
    Boolean getBoolean(final Object key);

    /**
     * Gets the value of the given key as a primitive boolean.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a primitive boolean
     * @throws ClassCastException if the value is not a boolean
     */
    boolean getBoolean(final Object key, final boolean defaultValue);

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key the key
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    Date getDate(final Object key);

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    Date getDate(final Object key, final Date defaultValue);

    /**
     * Gets the list value of the given key, casting the list elements to the given {@code Class<T>}.  This is useful to avoid having
     * casts in client code, though the effect is the same.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the list value to
     * @param <T>   the type of the class
     * @return the list value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the elements in the list value of the given key is not of type T or the value is not a list
     */
    <T> List<T> getList(Object key, Class<T> clazz);

    /**
     * Gets the list value of the given key, casting the list elements to {@code Class<T>} or returning the default list value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param clazz        the non-null class to cast the list value to
     * @param defaultValue what to return if the value is null
     * @param <T>          the type of the class
     * @return the list value of the given key, or the default list value if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> List<T> getList(final Object key, final Class<T> clazz, final List<T> defaultValue);

    /**
     * Gets the value of the given key, casting it to the given {@code Class<T>}.  This is useful to avoid having casts in client code,
     * though the effect is the same.  So to get the value of a key that is of type String, you would write {@code String name =
     * doc.get("name", String.class)} instead of {@code String name = (String) doc.get("x") }.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the value to
     * @param <T>   the type of the class
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> T get(final Object key, final Class<T> clazz);

    /**
     * Gets the value of the given key, casting it to {@code Class<T>} or returning the default value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @param <T>          the type of the class
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    <T> T get(final Object key, final T defaultValue);
}
