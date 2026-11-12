package com.georgev22.skinoverlay.datastructures.maps;

import com.georgev22.skinoverlay.utilities.Copyable;
import com.georgev22.skinoverlay.utilities.DeepCloner;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * An extension of the {@link Map} interface with additional utilities for
 * fluent operations, conditional modifications, type-safe retrieval, and cloning.
 *
 * <p>This interface is the common base for specialized {@code ObjectMap}
 * implementations such as {@link LinkedHashObjectMap}, {@link ConcurrentHashObjectMap},
 * {@link HashObjectMap}, and {@link TreeObjectMap}.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface ObjectMap<K, V> extends Map<K, V>, Copyable<ObjectMap<K, V>> {

    /**
     * Creates an unmodifiable {@link ObjectMap} containing the given entries.
     * <p>
     * This method allows you to create a map from a variable number of key-value entries.
     * The returned map cannot be modified; attempts to put, remove, or change entries
     * will throw an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param <K>     the type of keys maintained by the map
     * @param <V>     the type of mapped values
     * @param entries the key-value entries to include in the map; must not be {@code null}
     * @return a new unmodifiable {@link ObjectMap} containing the specified entries
     * @implNote The returned map is an instance of {@link UnmodifiableObjectMap}.
     * @see UnmodifiableObjectMap
     */
    @Contract("_ -> new")
    @SafeVarargs
    static <K, V> @NonNull ObjectMap<K, V> ofEntries(Entry<K, V> @NonNull ... entries) {
        //noinspection ConstantValue
        if (entries == null) throw new IllegalArgumentException("Entries cannot be null");
        for (Entry<K, V> entry : entries) {
            if (entry == null) throw new IllegalArgumentException("Entries cannot be null");
            if (entry.getKey() == null) throw new IllegalArgumentException("Keys cannot be null");
            if (entry.getValue() == null) throw new IllegalArgumentException("Values cannot be null");
        }
        return new UnmodifiableObjectMap<>(entries);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <K, V> @NonNull Entry<K, V> entry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }

    /**
     * Adds or replaces a single entry, returning this map for fluent chaining.
     *
     * @param key   the key
     * @param value the value
     * @return this map
     */
    ObjectMap<K, V> append(final K key, final V value);

    /**
     * Adds or replaces all entries from the given map, returning this map for fluent chaining.
     *
     * @param map the map to append
     * @return this map
     */
    ObjectMap<K, V> append(final Map<K, V> map);

    /**
     * Adds or replaces all entries from the given {@link ObjectMap}, returning this map for fluent chaining.
     *
     * @param map the map to append
     * @return this map
     */
    ObjectMap<K, V> append(final ObjectMap<K, V> map);

    /**
     * Adds or replaces an entry if the given {@link Optional} contains a value.
     *
     * @param key   the key
     * @param value optional value (if empty, nothing is added)
     * @return this map
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default ObjectMap<K, V> append(final K key, final @NonNull Optional<? extends V> value) {
        value.ifPresent(v -> append(key, v));
        return this;
    }

    /**
     * Adds or replaces an entry if the condition is true.
     *
     * @param key    the key
     * @param value  the value
     * @param ifTrue whether to insert
     * @return this map
     */
    ObjectMap<K, V> append(final K key, final V value, boolean ifTrue);

    /**
     * Adds or replaces an entry with one of two possible values depending on the condition.
     *
     * @param key          the key
     * @param valueIfTrue  value if condition is true
     * @param valueIfFalse value if condition is false
     * @param ifTrue       condition
     * @return this map
     */
    ObjectMap<K, V> append(final K key, final V valueIfTrue, final V valueIfFalse, boolean ifTrue);

    /**
     * Adds or replaces all entries from the given map if the condition is true.
     *
     * @param map    the map
     * @param ifTrue condition
     * @return this map
     */
    ObjectMap<K, V> append(final Map<K, V> map, boolean ifTrue);

    /**
     * Adds or replaces all entries from one of two maps depending on the condition.
     *
     * @param mapIfTrue  map if condition is true
     * @param mapIfFalse map if condition is false
     * @param ifTrue     condition
     * @return this map
     */
    ObjectMap<K, V> append(final Map<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Adds or replaces all entries from the given {@link ObjectMap} if the condition is true.
     *
     * @param map    the map
     * @param ifTrue condition
     * @return this map
     */
    ObjectMap<K, V> append(final ObjectMap<K, V> map, boolean ifTrue);

    /**
     * Adds or replaces all entries from one of two maps depending on the condition.
     *
     * @param mapIfTrue  map if condition is true
     * @param mapIfFalse map if condition is false
     * @param ifTrue     condition
     * @return this map
     */
    ObjectMap<K, V> append(final ObjectMap<K, V> mapIfTrue, final Map<K, V> mapIfFalse, boolean ifTrue);

    /**
     * Removes an entry by key.
     *
     * @param key the key to remove
     * @return this map
     */
    ObjectMap<K, V> removeEntry(final K key);

    /**
     * Removes all entries with keys present in the given map.
     *
     * @param map the map whose keys should be removed
     * @return this map
     */
    ObjectMap<K, V> removeEntries(final Map<K, V> map);

    /**
     * Removes all entries with keys present in the given {@link ObjectMap}.
     *
     * @param map the map whose keys should be removed
     * @return this map
     */
    ObjectMap<K, V> removeEntries(final ObjectMap<K, V> map);

    /**
     * Removes an entry if the condition is true.
     *
     * @param key    the key to remove
     * @param ifTrue condition
     * @return this map
     */
    ObjectMap<K, V> removeEntry(final K key, boolean ifTrue);

    /**
     * Removes all entries from the given map if the condition is true.
     *
     * @param map    the map whose keys should be removed
     * @param ifTrue condition
     * @return this map
     */
    ObjectMap<K, V> removeEntries(final Map<K, V> map, boolean ifTrue);

    /**
     * Removes all entries from the given {@link ObjectMap} if the condition is true.
     *
     * @param map    the map whose keys should be removed
     * @param ifTrue condition
     * @return this map
     */
    ObjectMap<K, V> removeEntries(final ObjectMap<K, V> map, boolean ifTrue);

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

    /**
     * Converts this map into a new {@link HashObjectMap}.
     *
     * @return a new {@link HashObjectMap} containing the same entries
     */
    default HashObjectMap<K, V> toHashObjectMap() {
        return new HashObjectMap<>(this);
    }

    /**
     * Converts this map into a new {@link ConcurrentHashObjectMap}.
     *
     * @return a new {@link ConcurrentHashObjectMap} containing the same entries
     */
    default ConcurrentHashObjectMap<K, V> toConcurrentObjectMap() {
        return new ConcurrentHashObjectMap<>(this);
    }

    /**
     * Converts this map into a new {@link LinkedHashObjectMap}.
     *
     * @return a new {@link LinkedHashObjectMap} containing the same entries
     */
    default LinkedHashObjectMap<K, V> toLinkedObjectMap() {
        return new LinkedHashObjectMap<>(this);
    }

    /**
     * Converts this map into a new {@link ObservableObjectMap}.
     *
     * @return a new {@link ObservableObjectMap} containing the same entries
     */
    default ObservableObjectMap<K, V> toObservableObjectMap() {
        return new ObservableObjectMap<>(this);
    }

    /**
     * Converts this map into a new {@link TreeObjectMap}.
     *
     * @return a new {@link TreeObjectMap} containing the same entries
     */
    default TreeObjectMap<K, V> toTreeObjectMap() {
        return new TreeObjectMap<>(this);
    }

    /**
     * Converts this map into a new unmodifiable wrapper.
     *
     * @return an {@link UnmodifiableObjectMap} containing the same entries
     */
    default UnmodifiableObjectMap<K, V> toUnmodifiableObjectMap() {
        return new UnmodifiableObjectMap<>(this);
    }

    /**
     * Creates a new instance of the specified map type with the same entries.
     * <p>The class must define a constructor accepting a {@link Map} or {@link ObjectMap}.
     *
     * @param clazz the map class
     * @return a new map of the requested type
     * @throws RuntimeException if no suitable constructor exists
     */
    @NonNull
    default ObjectMap<K, V> toObjectMap(@NonNull Class<? extends ObjectMap<K, V>> clazz) {
        Constructor<?> constructor;
        try {
            try {
                constructor = clazz.getConstructor(Map.class);
            } catch (NoSuchMethodException e) {
                constructor = clazz.getConstructor(ObjectMap.class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No suitable constructor found for " + clazz.getName(), e);
        }

        try {
            //noinspection unchecked
            return (ObjectMap<K, V>) constructor.newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new empty {@link ObjectMap} of the same type as this instance.
     *
     * @return a new empty map
     */
    ObjectMap<K, V> newObjectMap();

    /**
     * Creates a new {@link ObjectMap} of the same type as this instance,
     * containing all entries from the given map.
     *
     * @param map the source map
     * @return a new populated map
     */
    default ObjectMap<K, V> newObjectMap(Map<K, V> map) {
        ObjectMap<K, V> newObjectMap = newObjectMap();
        newObjectMap.putAll(map);
        return newObjectMap;
    }

    /**
     * Creates a new {@link ObjectMap} of the same type as this instance,
     * containing all entries from the given {@link ObjectMap}.
     *
     * @param map the source map
     * @return a new populated map
     */
    default ObjectMap<K, V> newObjectMap(ObjectMap<K, V> map) {
        ObjectMap<K, V> newObjectMap = newObjectMap();
        newObjectMap.putAll(map);
        return newObjectMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default @NonNull ObjectMap<K, V> shallowCopy() {
        ObjectMap<K, V> copy = newObjectMap();
        copy.putAll(this);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    default @NonNull ObjectMap<K, V> deepCopy() {
        ObjectMap<K, V> copy = newObjectMap();
        for (Entry<K, V> entry : entrySet()) {
            try {
                copy.put((K) DeepCloner.cloneValue(entry.getKey()), (V) DeepCloner.cloneValue(entry.getValue()));
            } catch (UnsupportedOperationException e) {
                break; // target map does not support modifications
            } catch (Exception ignored) {
            }
        }
        return copy;
    }
}
