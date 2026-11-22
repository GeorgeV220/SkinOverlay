package com.georgev22.skinoverlay.datastructures.maps;

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * An implementation of {@link ObjectMap} backed by a {@link LinkedHashMap}.
 * <p>
 * This class preserves insertion order and supports the {@link SequencedMap}
 * API introduced in Java 21.
 * <p>
 * Construction options include:
 * <ul>
 *     <li>Create a new empty backing {@link LinkedHashMap}.</li>
 *     <li>Create a new backing {@link LinkedHashMap} initialized with another map (copy constructor).</li>
 *     <li>Wrap (delegate to) an existing {@link LinkedHashMap} without copying.</li>
 *     <li>Create a new backing {@link LinkedHashMap} with a specified initial capacity and/or load factor.</li>
 * </ul>
 * <p>
 * Implements {@link Cloneable} for shallow cloning of the map.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class LinkedHashObjectMap<K, V> extends AbstractObjectMap<K, V>
        implements ObjectMap<K, V>, SequencedMap<K, V>, Cloneable {

    private int initialCapacity = -1;
    private float loadFactor = -1;

    /**
     * Creates an empty {@code LinkedHashObjectMap} with a new {@link LinkedHashMap}.
     */
    public LinkedHashObjectMap() {
        super(new LinkedHashMap<>());
    }

    /**
     * Creates a new {@code LinkedHashObjectMap} by copying all mappings
     * from the given {@link ObjectMap} into a new {@link LinkedHashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public LinkedHashObjectMap(final ObjectMap<K, V> map) {
        super(new LinkedHashMap<>(map));
    }

    /**
     * Creates a new {@code LinkedHashObjectMap} by copying all mappings
     * from the given {@link Map} into a new {@link LinkedHashMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public LinkedHashObjectMap(final Map<K, V> map) {
        super(new LinkedHashMap<>(map));
    }

    /**
     * Creates a {@code LinkedHashObjectMap} that directly delegates
     * to the provided {@link LinkedHashMap} without copying.
     * <p>
     * Changes in this instance will directly affect the given map.
     *
     * @param backingMap the {@link LinkedHashMap} to delegate to
     */
    public LinkedHashObjectMap(final LinkedHashMap<K, V> backingMap) {
        super(backingMap);
    }

    /**
     * Constructs a new {@code LinkedHashObjectMap} with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the map
     */
    public LinkedHashObjectMap(final int initialCapacity) {
        super(new LinkedHashMap<>(initialCapacity));
        this.initialCapacity = initialCapacity;
    }

    /**
     * Constructs a new {@code LinkedHashObjectMap} with the specified
     * initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity of the map
     * @param loadFactor      the load factor threshold for resizing
     */
    public LinkedHashObjectMap(final int initialCapacity, final float loadFactor) {
        super(new LinkedHashMap<>(initialCapacity, loadFactor));
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a new empty {@code LinkedHashObjectMap} of the same type.
     * Capacity and load factor are preserved if they were explicitly set.
     */
    @Override
    public LinkedHashObjectMap<K, V> newObjectMap() {
        return (initialCapacity > 0)
                ? (loadFactor > 0
                   ? new LinkedHashObjectMap<>(initialCapacity, loadFactor)
                   : new LinkedHashObjectMap<>(initialCapacity))
                : new LinkedHashObjectMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequencedMap<K, V> reversed() {
        return ((SequencedMap<K, V>) delegate).reversed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> firstEntry() {
        return ((SequencedMap<K, V>) delegate).firstEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> lastEntry() {
        return ((SequencedMap<K, V>) delegate).lastEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> pollFirstEntry() {
        return ((SequencedMap<K, V>) delegate).pollFirstEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> pollLastEntry() {
        return ((SequencedMap<K, V>) delegate).pollLastEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putFirst(K k, V v) {
        return ((SequencedMap<K, V>) delegate).putFirst(k, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putLast(K k, V v) {
        return ((SequencedMap<K, V>) delegate).putLast(k, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SequencedSet<K> sequencedKeySet() {
        return ((SequencedMap<K, V>) delegate).sequencedKeySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SequencedCollection<V> sequencedValues() {
        return ((SequencedMap<K, V>) delegate).sequencedValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SequencedSet<Entry<K, V>> sequencedEntrySet() {
        return ((SequencedMap<K, V>) delegate).sequencedEntrySet();
    }

    /**
     * Creates a shallow clone of this {@link LinkedHashObjectMap}.
     * <p>
     * The returned {@code LinkedHashObjectMap} is a new instance with its own
     * backing {@link LinkedHashMap}, so modifications to the clone's entries
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
     * @return a shallow clone of this {@code LinkedHashObjectMap}
     */
    @Override
    public LinkedHashObjectMap<K, V> clone() {
        try {
            //noinspection unchecked
            LinkedHashObjectMap<K, V> clone = (LinkedHashObjectMap<K, V>) super.clone();
            if (this.delegate instanceof LinkedHashMap<K, V> linkedHashMap) {
                //noinspection unchecked
                clone.delegate = (LinkedHashMap<K, V>) linkedHashMap.clone();
            } else {
                clone.delegate = new LinkedHashMap<>(this.delegate);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should never happen
        }
    }

}
