package com.georgev22.skinoverlay.datastructures.maps;

import org.jspecify.annotations.NonNull;

import java.util.*;

public class TreeObjectMap<K, V> extends AbstractObjectMap<K, V> implements ObjectMap<K, V>, NavigableMap<K, V>, Cloneable {

    private Comparator<? super K> comparator;

    /**
     * Creates a TreeObjectMap instance.
     */
    public TreeObjectMap() {
        super(new TreeMap<>());
        this.comparator = null;
    }

    /**
     * Creates a TreeObjectMap instance with the specified comparator.
     *
     * @param comparator the comparator to order the keys
     */
    public TreeObjectMap(Comparator<? super K> comparator) {
        super(new TreeMap<>(comparator));
        this.comparator = comparator;
    }

    /**
     * Creates a TreeObjectMap instance initialized with the given ObjectMap.
     *
     * @param map the initial ObjectMap
     */
    public TreeObjectMap(final ObjectMap<K, V> map) {
        super(new TreeMap<>(map));
        if (map instanceof TreeObjectMap<K, V> treeObjectMap) {
            this.comparator = treeObjectMap.comparator;
        } else {
            this.comparator = null;
        }
    }

    /**
     * Creates a TreeObjectMap instance initialized with the given Map.
     *
     * @param map the initial Map
     */
    public TreeObjectMap(final Map<K, V> map) {
        super(new TreeMap<>(map));
        if (map instanceof TreeMap<K, V> treeMap) {
            this.comparator = treeMap.comparator();
        } else {
            this.comparator = null;
        }
    }

    /**
     * Creates a TreeObjectMap instance with the specified comparator and initialized with the given ObjectMap.
     *
     * @param comparator the comparator to order the keys
     * @param map        the initial ObjectMap
     */
    public TreeObjectMap(Comparator<? super K> comparator, final ObjectMap<K, V> map) {
        super(new TreeMap<>(comparator));
        this.comparator = comparator;
        putAll(map);
    }

    /**
     * Creates a TreeObjectMap instance with the specified comparator and initialized with the given Map.
     *
     * @param comparator the comparator to order the keys
     * @param map        the initial Map
     */
    public TreeObjectMap(Comparator<? super K> comparator, final Map<K, V> map) {
        super(new TreeMap<>(comparator));
        this.comparator = comparator;
        putAll(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeObjectMap<K, V> newObjectMap() {
        return new TreeObjectMap<>(comparator);
    }

    /**
     * Returns the comparator used to order the keys in this map.
     *
     * @return the comparator used to order the keys
     */
    public Comparator<? super K> comparator() {
        return comparator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> lowerEntry(K key) {
        return ((NavigableMap<K, V>) delegate).lowerEntry(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K lowerKey(K key) {
        return ((NavigableMap<K, V>) delegate).lowerKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> floorEntry(K key) {
        return ((NavigableMap<K, V>) delegate).floorEntry(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K floorKey(K key) {
        return ((NavigableMap<K, V>) delegate).floorKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return ((NavigableMap<K, V>) delegate).ceilingEntry(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K ceilingKey(K key) {
        return ((NavigableMap<K, V>) delegate).ceilingKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> higherEntry(K key) {
        return ((NavigableMap<K, V>) delegate).higherEntry(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K higherKey(K key) {
        return ((NavigableMap<K, V>) delegate).higherKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> firstEntry() {
        return ((NavigableMap<K, V>) delegate).firstEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> lastEntry() {
        return ((NavigableMap<K, V>) delegate).lastEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> pollFirstEntry() {
        return ((NavigableMap<K, V>) delegate).pollFirstEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry<K, V> pollLastEntry() {
        return ((NavigableMap<K, V>) delegate).pollLastEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<K, V> descendingMap() {
        return ((NavigableMap<K, V>) delegate).descendingMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<K> navigableKeySet() {
        return ((NavigableMap<K, V>) delegate).navigableKeySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<K> descendingKeySet() {
        return ((NavigableMap<K, V>) delegate).descendingKeySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return ((NavigableMap<K, V>) delegate).subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return ((NavigableMap<K, V>) delegate).headMap(toKey, inclusive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return ((NavigableMap<K, V>) delegate).tailMap(fromKey, inclusive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SortedMap<K, V> subMap(K fromKey, K toKey) {
        return ((SortedMap<K, V>) delegate).subMap(fromKey, toKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SortedMap<K, V> headMap(K toKey) {
        return ((SortedMap<K, V>) delegate).headMap(toKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull SortedMap<K, V> tailMap(K fromKey) {
        return ((SortedMap<K, V>) delegate).tailMap(fromKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K firstKey() {
        return ((SortedMap<K, V>) delegate).firstKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K lastKey() {
        return ((SortedMap<K, V>) delegate).lastKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putFirst(K k, V v) {
        return ((SortedMap<K, V>) delegate).putFirst(k, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putLast(K k, V v) {
        return ((SortedMap<K, V>) delegate).putLast(k, v);
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
     * {@inheritDoc}
     */
    @Override
    public NavigableMap<K, V> reversed() {
        return ((NavigableMap<K, V>) delegate).reversed();
    }

    /**
     * Creates a shallow clone of this {@link TreeObjectMap}.
     * <p>
     * The returned {@code TreeObjectMap} is a new instance with its own
     * backing {@link TreeMap}, so modifications to the clone's entries
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
     * @return a shallow clone of this {@code TreeObjectMap}
     */
    @Override
    public TreeObjectMap<K, V> clone() {
        try {
            @SuppressWarnings("unchecked")
            TreeObjectMap<K, V> clone = (TreeObjectMap<K, V>) super.clone();
            if (this.delegate instanceof TreeMap<K, V> treeMap) {
                //noinspection unchecked
                clone.delegate = (TreeMap<K, V>) treeMap.clone();
                clone.comparator = treeMap.comparator();
            } else {
                clone.delegate = new TreeMap<>(this.delegate);
                clone.comparator = this.comparator;
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should never happen
        }
    }
}
