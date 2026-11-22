package com.georgev22.skinoverlay.datastructures.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * A non-thread-safe implementation of {@link ObjectMap} backed by a {@link HashMap}.
 * <p>
 * This class supports multiple construction modes:
 * <ul>
 *     <li>Create a new empty backing {@link HashMap}.</li>
 *     <li>Create a new backing {@link HashMap} initialized with another map (copy constructor).</li>
 *     <li>Wrap (delegate to) an existing {@link HashMap} without copying.</li>
 *     <li>Create a new backing {@link HashMap} with a specified initial capacity and/or load factor.</li>
 * </ul>
 * <p>
 * Implements {@link Cloneable} for shallow cloning of the map.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class HashObjectMap<K, V> extends AbstractObjectMap<K, V> implements ObjectMap<K, V>, Cloneable {

    private int initialCapacity = -1;
    private float loadFactor = -1;

    /**
     * Creates an empty {@code HashObjectMap} with a new {@link HashMap}.
     */
    public HashObjectMap() {
        super(new HashMap<>());
    }

    /**
     * Creates a new {@code HashObjectMap} by copying all mappings
     * from the given {@link ObjectMap} into a new {@link HashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public HashObjectMap(final ObjectMap<K, V> map) {
        super(new HashMap<>(map));
    }

    /**
     * Creates a new {@code HashObjectMap} by copying all mappings
     * from the given {@link Map} into a new {@link HashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public HashObjectMap(final Map<K, V> map) {
        super(new HashMap<>(map));
    }

    /**
     * Creates a {@code HashObjectMap} that directly delegates
     * to the provided {@link HashMap} without copying.
     * <p>
     * Changes in this instance will directly affect the given map.
     *
     * @param backingMap the {@link HashMap} to delegate to
     */
    public HashObjectMap(final HashMap<K, V> backingMap) {
        super(backingMap);
    }

    /**
     * Constructs a new {@code HashObjectMap} with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the map
     */
    public HashObjectMap(final int initialCapacity) {
        super(new HashMap<>(initialCapacity));
        this.initialCapacity = initialCapacity;
    }

    /**
     * Constructs a new {@code HashObjectMap} with the specified
     * initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the map
     * @param loadFactor      the load factor threshold for resizing
     */
    public HashObjectMap(final int initialCapacity, final float loadFactor) {
        super(new HashMap<>(initialCapacity, loadFactor));
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a new empty {@code HashObjectMap} of the same type.
     * Capacity and load factor are preserved if they were explicitly set.
     */
    @Override
    public HashObjectMap<K, V> newObjectMap() {
        return (initialCapacity > 0)
                ? (loadFactor > 0
                ? new HashObjectMap<>(initialCapacity, loadFactor)
                : new HashObjectMap<>(initialCapacity))
                : new HashObjectMap<>();
    }

    /**
     * Creates a shallow clone of this {@link HashObjectMap}.
     * <p>
     * The returned {@code HashObjectMap} is a new instance with its own
     * backing {@link HashMap}, so modifications to the clone's entries
     * do not affect the original map and vice versa.
     * <p>
     * <b>Note:</b> This is a shallow copy. The keys and values themselves
     * are not cloned; if they are mutable objects, changes to them will
     * be reflected in both the original and the clone.
     * <p>
     * If a deep copy is required, use the {@link #deepCopy()} method
     * provided by the {@link ObjectMap}
     * interface (which extends {@link com.georgev22.skinoverlay.utilities.Copyable}).
     *
     * @return a shallow clone of this {@code HashObjectMap}
     */
    @Override
    public HashObjectMap<K, V> clone() {
        try {
            @SuppressWarnings("unchecked")
            HashObjectMap<K, V> clone = (HashObjectMap<K, V>) super.clone();
            if (this.delegate instanceof HashMap<K, V> hashMap) {
                //noinspection unchecked
                clone.delegate = (HashMap<K, V>) hashMap.clone();
            } else {
                clone.delegate = new HashMap<>(this.delegate);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should never happen
        }
    }

}
