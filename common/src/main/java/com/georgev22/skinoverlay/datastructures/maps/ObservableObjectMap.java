package com.georgev22.skinoverlay.datastructures.maps;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

/**
 * An observable concurrent map implementation that extends {@link ConcurrentHashObjectMap}
 * and allows registering listeners to react to changes in the map.
 * <p>
 * This class notifies all registered {@link MapChangeListener}s whenever an entry
 * is added, removed, or updated. It is especially useful in reactive systems,
 * caches, and event-driven applications.
 * <p>
 * Thread-safety:
 * <ul>
 *     <li>The underlying storage is a {@link java.util.concurrent.ConcurrentHashMap}.</li>
 *     <li>Listeners are stored in a {@link CopyOnWriteArrayList}, so they can be
 *     safely added and removed while notifications are being dispatched.</li>
 * </ul>
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ObservableObjectMap<K, V> extends ConcurrentHashObjectMap<K, V> {

    /**
     * Registered listeners that will be notified when map entries change.
     */
    private final List<MapChangeListener<K, V>> listeners = new CopyOnWriteArrayList<>();

    private int initialCapacity = -1;
    private float loadFactor = -1;

    /**
     * Creates an empty {@link ObservableObjectMap}.
     */
    public ObservableObjectMap() {
        super();
    }

    /**
     * Creates a new {@link ObservableObjectMap} initialized with the entries from another {@link ObjectMap}.
     *
     * @param map the initial map
     */
    public ObservableObjectMap(final ObjectMap<K, V> map) {
        super(map);
    }

    /**
     * Creates a new {@link ObservableObjectMap} initialized with the entries from another {@link Map}.
     *
     * @param map the initial map
     */
    public ObservableObjectMap(final Map<K, V> map) {
        super(map);
    }

    /**
     * Creates a new {@link ObservableObjectMap} with a given initial capacity.
     *
     * @param initialCapacity initial capacity
     */
    public ObservableObjectMap(final int initialCapacity) {
        super(initialCapacity);
        this.initialCapacity = initialCapacity;
    }

    /**
     * Creates a new {@link ObservableObjectMap} with a given initial capacity and load factor.
     *
     * @param initialCapacity initial capacity
     * @param loadFactor      load factor
     */
    public ObservableObjectMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    /**
     * Registers a listener to receive notifications when map entries change.
     *
     * @param listener listener to add
     */
    public void addListener(@NonNull MapChangeListener<K, V> listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a previously registered listener.
     *
     * @param listener listener to remove
     */
    public void removeListener(@NonNull MapChangeListener<K, V> listener) {
        listeners.remove(listener);
    }

    /**
     * Returns an unmodifiable view of the registered listeners.
     *
     * @return listeners list
     */
    public List<MapChangeListener<K, V>> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(@NonNull K key, @NonNull V value) {
        V oldValue = super.put(key, value);
        if (oldValue == null) {
            fireEntryAdded(key, value);
        } else if (!Objects.equals(oldValue, value)) {
            fireEntryUpdated(key, oldValue, value);
        }
        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putIfAbsent(@NonNull K key, @NonNull V value) {
        V oldValue = super.putIfAbsent(key, value);
        if (oldValue == null) {
            fireEntryAdded(key, value);
        }
        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue()); // reuse put logic for notifications
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(@NonNull Object key) {
        V oldValue = super.remove(key);
        if (oldValue != null) {
            fireEntryRemoved(key, oldValue);
        }
        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            fireEntryRemoved(key, value);
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);
        if (replaced && !Objects.equals(oldValue, newValue)) {
            fireEntryUpdated(key, oldValue, newValue);
        }
        return replaced;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V replace(K key, V value) {
        V oldValue = super.replace(key, value);
        if (oldValue != null && !Objects.equals(oldValue, value)) {
            fireEntryUpdated(key, oldValue, value);
        }
        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceAll(@NonNull BiFunction<? super K, ? super V, ? extends V> function) {
        if (listeners.isEmpty()) {
            super.replaceAll(function);
            return;
        }

        Map<K, V> oldSnapshot = new HashMap<>(this);
        super.replaceAll(function);

        for (Entry<K, V> entry : entrySet()) {
            V oldValue = oldSnapshot.get(entry.getKey());
            V newValue = entry.getValue();
            if (!Objects.equals(oldValue, newValue)) {
                fireEntryUpdated(entry.getKey(), oldValue, newValue);
            }
        }
    }

    private void fireEntryAdded(K key, V value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryAdded(key, value);
        }
    }

    private void fireEntryRemoved(Object key, @Nullable Object value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryRemoved(key, value);
        }
    }

    private void fireEntryUpdated(K key, @Nullable V oldValue, @NonNull V newValue) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryUpdated(key, oldValue, newValue);
        }
    }

    /**
     * Listener interface for receiving map change events.
     *
     * @param <K> key type
     * @param <V> value type
     */
    public interface MapChangeListener<K, V> {

        /**
         * Called when a new entry is added to the map.
         *
         * @param key   the added key
         * @param value the added value
         */
        void entryAdded(K key, V value);

        /**
         * Called when an entry is removed from the map.
         *
         * @param key   the removed key
         * @param value the removed value (may be {@code null})
         */
        void entryRemoved(Object key, @Nullable Object value);

        /**
         * Called when an entry is updated in the map.
         *
         * @param key      the updated key
         * @param oldValue the previous value (may be {@code null})
         * @param newValue the new value
         */
        void entryUpdated(K key, @Nullable V oldValue, @NonNull V newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableObjectMap<K, V> newObjectMap() {
        if (initialCapacity > 0) {
            return (loadFactor > 0)
                    ? new ObservableObjectMap<>(initialCapacity, loadFactor)
                    : new ObservableObjectMap<>(initialCapacity);
        }
        return new ObservableObjectMap<>();
    }
}
