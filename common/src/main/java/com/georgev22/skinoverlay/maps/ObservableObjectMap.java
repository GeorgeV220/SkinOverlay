package com.georgev22.skinoverlay.maps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/**
 * An implementation of the {@link ObjectMap} interface that provides an easy way to add listeners
 * that get notified when a new entry is added to the map.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ObservableObjectMap<K, V> extends ConcurrentObjectMap<K, V> {

    private final List<MapChangeListener<K, V>> listeners = new ArrayList<>();

    /**
     * Adds a {@link MapChangeListener} to this map.
     *
     * @param listener the listener to be added
     */
    public void addListener(MapChangeListener<K, V> listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link MapChangeListener} to this map.
     *
     * @param listener the listener to be removed
     */
    public void removeListener(MapChangeListener<K, V> listener) {
        listeners.remove(listener);
    }

    /**
     * Returns an unmodifiable List of the registered MapChangeListeners.
     *
     * @return An unmodifiable List of the registered MapChangeListeners.
     */
    public List<MapChangeListener<K, V>> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously contained a mapping
     * for the key, the old value is replaced by the specified value. Notifies all registered listeners with the
     * added key-value pair.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     */
    @Override
    public V put(@NotNull K key, @NotNull V value) {
        V oldValue = super.put(key, value);
        fireEntryAddedEvent(key, value);
        return oldValue;
    }

    /**
     * Associates the specified value with the specified key in this map if it is not already associated with a value.
     * If the specified key is already associated with a value, the existing value is returned and no change is made.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     */
    @Override
    public V putIfAbsent(K key, V value) {
        V oldValue = super.putIfAbsent(key, value);
        fireEntryAddedEvent(key, value);
        return oldValue;
    }

    /**
     * Copies all the mappings from the specified map to this map.
     * The effect of this call is equivalent to that of calling
     * {@code put(k, v)} on this map once for each mapping from key {@code k} to value {@code v} in the specified map.
     * The behavior of this operation is undefined if the specified map is modified while the operation is in progress.
     *
     * @param m the map whose mappings are to be added to this map
     */
    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::fireEntryAddedEvent);
        super.putAll(m);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * The value previously associated with the key is returned.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or {@code null} if there was no mapping for the key
     */
    @Override
    public V remove(@NotNull Object key) {
        fireEntryRemovedEvent(key, null);
        return super.remove(key);
    }

    /**
     * Removes the entry for a key only if currently mapped to a given value.
     *
     * @param key   key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     */
    @Override
    public boolean remove(Object key, Object value) {
        fireEntryRemovedEvent(key, value);
        return super.remove(key, value);
    }

    /**
     * Replaces the entry for a key only if currently mapped to some value.
     *
     * @param key      key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        fireEntryRemovedEvent(key, oldValue);
        fireEntryAddedEvent(key, newValue);
        return super.replace(key, oldValue, newValue);
    }

    /**
     * Replaces the entry for a key only if currently mapped to some value.
     *
     * @param key   key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     */
    @Override
    public V replace(K key, V value) {
        fireEntryRemovedEvent(key, null);
        fireEntryAddedEvent(key, value);
        return super.replace(key, value);
    }

    /**
     * Replaces each entry's value with the result of invoking the given function on that entry.
     *
     * @param function the function to apply to each entry
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Map<K, V> oldMap = new ConcurrentObjectMap<>(this);

        super.replaceAll(function);

        for (K key : oldMap.keySet()) {
            V oldValue = oldMap.get(key);
            V newValue = get(key);
            if (!Objects.equals(oldValue, newValue)) {
                fireEntryRemovedEvent(key, oldValue);
                fireEntryAddedEvent(key, newValue);
            }
        }
    }


    /**
     * Notifies all registered listeners that a new entry has been added to the map.
     *
     * @param key   the key of the added entry
     * @param value the value of the added entry
     */
    private void fireEntryAddedEvent(K key, V value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryAdded(key, value);
        }
    }

    /**
     * Notifies all registered listeners that an entry has been removed from the map.
     *
     * @param key   the key of the removed entry
     * @param value the value of the removed entry, or {@code null} if the key was not previously mapped
     */
    private void fireEntryRemovedEvent(Object key, @Nullable Object value) {
        for (MapChangeListener<K, V> listener : listeners) {
            listener.entryRemoved(key, value);
        }
    }

    /**
     * A listener interface for receiving notifications when a new entry is added to an {@link ObservableObjectMap}.
     *
     * @param <K> the type of keys maintained by the map
     * @param <V> the type of mapped values
     */
    public interface MapChangeListener<K, V> {
        /**
         * Called when a new entry is added to the map.
         *
         * @param key   the key of the added entry
         * @param value the value of the added entry
         */
        void entryAdded(K key, V value);

        /**
         * Called when an entry is removed from the map.
         *
         * @param key   the key of the removed entry
         * @param value the value of the removed entry, or {@code null} if there was no value associated with the key
         */
        void entryRemoved(Object key, @Nullable Object value);
    }

}
