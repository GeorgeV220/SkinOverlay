package com.georgev22.skinoverlay.datastructures.maps;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Returns an unmodifiable view of the
 * specified map. Query operations on the returned map "read through"
 * to the specified map, and attempts to modify the returned
 * map, whether direct or via its collection views, result in an
 * {@code UnsupportedOperationException}.<p>
 */
public class UnmodifiableObjectMap<K, V> extends AbstractObjectMap<K, V> implements ObjectMap<K, V>, Serializable {

    @SafeVarargs
    public UnmodifiableObjectMap(Entry<K, V> @NonNull ... entries) {
        super(new ConcurrentHashObjectMap<>());
        for (Entry<K, V> entry : entries) {
            this.delegate.put(entry.getKey(), entry.getValue());
        }
    }

    public UnmodifiableObjectMap(ObjectMap<K, V> map) {
        super(map);
    }

    public UnmodifiableObjectMap(Map<K, V> map) {
        super(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(Map<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(ObjectMap<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(K key, V value, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(K key, V valueIfTrue, V valueIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(Map<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(Map<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(ObjectMap<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(ObjectMap<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntry(K key) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(Map<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(ObjectMap<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntry(K key, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(Map<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(ObjectMap<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V computeIfAbsent(K key, @NonNull Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V computeIfPresent(K key, @NonNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V compute(K key, @NonNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V merge(K key, @NonNull V value, @NonNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> original = super.entrySet();
        Set<Entry<K, V>> wrapped = new HashSet<>();

        for (Entry<K, V> entry : original) {
            wrapped.add(new AbstractMap.SimpleImmutableEntry<>(entry));
        }

        return Collections.unmodifiableSet(wrapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnmodifiableObjectMap<K, V> newObjectMap() {
        return new UnmodifiableObjectMap<>(new HashObjectMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectMap<K, V> newObjectMap(Map<K, V> map) {
        return new UnmodifiableObjectMap<>(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectMap<K, V> newObjectMap(ObjectMap<K, V> map) {
        return new UnmodifiableObjectMap<>(map);
    }
}
