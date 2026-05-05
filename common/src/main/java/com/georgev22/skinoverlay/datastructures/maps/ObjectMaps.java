package com.georgev22.skinoverlay.datastructures.maps;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Utility class providing static factory methods for creating various {@link ObjectMap} implementations.
 *
 * <p>This class centralizes creation of {@link LinkedHashObjectMap}, {@link ConcurrentHashObjectMap},
 * {@link HashObjectMap}, {@link TreeObjectMap}, and {@link UnmodifiableObjectMap} instances.
 * It supports both empty and pre-populated maps, improving readability and reusability.</p>
 */
public final class ObjectMaps {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ObjectMaps() {
        // Prevent instantiation
    }

    /**
     * Creates a new empty {@link LinkedHashObjectMap}.
     *
     * @return a new {@link LinkedHashObjectMap} instance
     */
    @Contract(" -> new")
    public static @NonNull <K, V> LinkedHashObjectMap<K, V> newLinkedHashObjectMap() {
        return new LinkedHashObjectMap<>();
    }

    /**
     * Creates a new empty {@link ConcurrentHashObjectMap}.
     *
     * @return a new {@link ConcurrentHashObjectMap} instance
     */
    @Contract(" -> new")
    public static @NonNull <K, V> ConcurrentHashObjectMap<K, V> newConcurrentHashObjectMap() {
        return new ConcurrentHashObjectMap<>();
    }

    /**
     * Creates a new empty {@link HashObjectMap}.
     *
     * @return a new {@link HashObjectMap} instance
     */
    @Contract(" -> new")
    public static @NonNull <K, V> HashObjectMap<K, V> newHashObjectMap() {
        return new HashObjectMap<>();
    }

    /**
     * Creates a new empty {@link TreeObjectMap}.
     *
     * @return a new {@link TreeObjectMap} instance
     */
    @Contract(" -> new")
    public static @NonNull <K, V> TreeObjectMap<K, V> newTreeObjectMap() {
        return new TreeObjectMap<>();
    }

    /**
     * Creates a new {@link LinkedHashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link LinkedHashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> LinkedHashObjectMap<K, V> newLinkedHashObjectMap(ObjectMap<K, V> map) {
        return new LinkedHashObjectMap<>(map);
    }

    /**
     * Creates a new {@link LinkedHashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link LinkedHashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> LinkedHashObjectMap<K, V> newLinkedHashObjectMap(Map<K, V> map) {
        return new LinkedHashObjectMap<>(map);
    }

    /**
     * Creates a new {@link ConcurrentHashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link ConcurrentHashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> ConcurrentHashObjectMap<K, V> newConcurrentHashObjectMap(ObjectMap<K, V> map) {
        return new ConcurrentHashObjectMap<>(map);
    }

    /**
     * Creates a new {@link ConcurrentHashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link ConcurrentHashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> ConcurrentHashObjectMap<K, V> newConcurrentHashObjectMap(Map<K, V> map) {
        return new ConcurrentHashObjectMap<>(map);
    }

    /**
     * Creates a new {@link HashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link HashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> HashObjectMap<K, V> newHashObjectMap(ObjectMap<K, V> map) {
        return new HashObjectMap<>(map);
    }

    /**
     * Creates a new {@link HashObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link HashObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> HashObjectMap<K, V> newHashObjectMap(Map<K, V> map) {
        return new HashObjectMap<>(map);
    }

    /**
     * Creates a new {@link TreeObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link TreeObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> TreeObjectMap<K, V> newTreeObjectMap(ObjectMap<K, V> map) {
        return new TreeObjectMap<>(map);
    }

    /**
     * Creates a new {@link TreeObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new {@link TreeObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> TreeObjectMap<K, V> newTreeObjectMap(Map<K, V> map) {
        return new TreeObjectMap<>(map);
    }

    /**
     * Creates a new unmodifiable {@link ObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new unmodifiable {@link ObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> UnmodifiableObjectMap<K, V> newUnmodifiableObjectMap(ObjectMap<K, V> map) {
        return new UnmodifiableObjectMap<>(map);
    }

    /**
     * Creates a new unmodifiable {@link ObjectMap} containing all entries from the given map.
     *
     * @param map the source map
     * @return a new unmodifiable {@link ObjectMap} with the same mappings
     */
    @Contract("_ -> new")
    public static @NonNull <K, V> UnmodifiableObjectMap<K, V> newUnmodifiableObjectMap(Map<K, V> map) {
        return new UnmodifiableObjectMap<>(map);
    }
}
