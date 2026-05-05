package com.georgev22.skinoverlay.datastructures.maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe implementation of {@link ObjectMap} backed by a {@link ConcurrentHashMap}.
 * <p>
 * This class supports multiple ways of construction:
 * <ul>
 *     <li>Create a new empty backing map.</li>
 *     <li>Create a new backing map initialized with another map.</li>
 *     <li>Wrap (delegate to) an existing {@link ConcurrentHashMap} without copying.</li>
 *     <li>Create a new backing map with a specified initial capacity and/or load factor.</li>
 * </ul>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class ConcurrentHashObjectMap<K, V> extends AbstractObjectMap<K, V> implements ObjectMap<K, V> {

    private int initialCapacity = -1;
    private float loadFactor = -1;

    /**
     * Creates an empty {@code ConcurrentHashObjectMap} with a new {@link ConcurrentHashMap}.
     */
    public ConcurrentHashObjectMap() {
        super(new ConcurrentHashMap<>());
    }

    /**
     * Creates a new {@code ConcurrentHashObjectMap} by copying all mappings
     * from the given {@link ObjectMap} into a new {@link ConcurrentHashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public ConcurrentHashObjectMap(final ObjectMap<K, V> map) {
        super(new ConcurrentHashMap<>(map));
    }

    /**
     * Creates a new {@code ConcurrentHashObjectMap} by copying all mappings
     * from the given {@link Map} into a new {@link ConcurrentHashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public ConcurrentHashObjectMap(final Map<K, V> map) {
        super(new ConcurrentHashMap<>(map));
    }

    /**
     * Creates a {@code ConcurrentHashObjectMap} that directly delegates
     * to the provided {@link ConcurrentHashMap} without copying.
     * <p>
     * Changes in this instance will directly affect the given map.
     *
     * @param backingMap the {@link ConcurrentHashMap} to delegate to
     */
    public ConcurrentHashObjectMap(final ConcurrentHashMap<K, V> backingMap) {
        super(backingMap);
    }

    /**
     * Constructs a new {@code ConcurrentHashObjectMap} with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the map
     */
    public ConcurrentHashObjectMap(final int initialCapacity) {
        super(new ConcurrentHashMap<>(initialCapacity));
        this.initialCapacity = initialCapacity;
    }

    /**
     * Constructs a new {@code ConcurrentHashObjectMap} with the specified
     * initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the map
     * @param loadFactor      the load factor threshold for resizing
     */
    public ConcurrentHashObjectMap(final int initialCapacity, final float loadFactor) {
        super(new ConcurrentHashMap<>(initialCapacity, loadFactor));
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a new empty {@code ConcurrentHashObjectMap} of the same type.
     * Capacity and load factor are preserved if they were explicitly set.
     */
    @Override
    public ConcurrentHashObjectMap<K, V> newObjectMap() {
        return (initialCapacity > 0)
                ? (loadFactor > 0
                ? new ConcurrentHashObjectMap<>(initialCapacity, loadFactor)
                : new ConcurrentHashObjectMap<>(initialCapacity))
                : new ConcurrentHashObjectMap<>();
    }
}
