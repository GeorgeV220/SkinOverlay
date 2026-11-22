package com.georgev22.skinoverlay.datastructures.maps;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for MultiMaps, supporting multiple values per key.
 * <p>
 * Subclasses must define the type of collection to store the values
 * (e.g., List or Set) by implementing {@link #createEmptyCollection()}.
 *
 * @param <K> key type
 * @param <V> value type
 * @param <M> the concrete subclass type for fluent API
 */
public abstract class AbstractObjectMultiMap<K, V, M extends AbstractObjectMultiMap<K, V, M>>
        extends AbstractObjectMap<K, Collection<V>> {

    protected AbstractObjectMultiMap(Map<K, Collection<V>> map) {
        super(map);
    }

    /**
     * Subclasses must provide the type of collection used to store values.
     * For example, a ListMultiMap would return new ArrayList<>(), a SetMultiMap would return new HashSet<>().
     *
     * @return a new empty collection for values
     */
    protected abstract Collection<V> createEmptyCollection();

    /**
     * Returns {@code this} cast to the concrete subclass type.
     */
    @SuppressWarnings("unchecked")
    protected M self() {
        return (M) this;
    }

    /**
     * Adds a value to the collection associated with the given key.
     *
     * @param key   the key
     * @param value the value to add
     * @return this map for fluent chaining
     */
    public M appendValue(K key, V value) {
        delegate.computeIfAbsent(key, k -> createEmptyCollection()).add(value);
        return self();
    }

    /**
     * Adds multiple values to the collection associated with the given key.
     *
     * @param key    the key
     * @param values the values to add
     * @return this map for fluent chaining
     */
    public M appendValues(K key, Collection<V> values) {
        delegate.computeIfAbsent(key, k -> createEmptyCollection()).addAll(values);
        return self();
    }

    /**
     * Removes a value from the collection associated with the given key.
     * If the collection becomes empty, the key is removed from the map.
     *
     * @param key   the key
     * @param value the value to remove
     * @return true if the value was removed, false otherwise
     */
    public boolean removeValue(K key, V value) {
        Collection<V> values = delegate.get(key);
        if (values != null) {
            boolean removed = values.remove(value);
            if (values.isEmpty()) delegate.remove(key);
            return removed;
        }
        return false;
    }

    /**
     * Retrieves a specific element from the collection associated with the given key.
     *
     * <p>If the key exists and the collection contains the element, the element is returned.
     * If the key does not exist or the element is not present, {@code null} is returned.</p>
     *
     * @param key     the key whose associated collection to search
     * @param element the element to find in the collection
     * @return the element if found, or {@code null} if not found
     */
    public @Nullable V getValue(K key, V element) {
        Collection<V> values = delegate.get(key);
        if (values != null) {
            for (V v : values) {
                if (Objects.equals(v, element)) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a specific element from the collection associated with the given key,
     * wrapped in an {@link Optional}.
     *
     * <p>If the key exists and the collection contains the element, an {@link Optional}
     * containing the element is returned. If the key does not exist or the element is not
     * present, {@link Optional#empty()} is returned.</p>
     *
     * @param key     the key whose associated collection to search
     * @param element the element to find in the collection
     * @return an {@link Optional} containing the element if found, or {@link Optional#empty()} if not found
     */
    public Optional<V> getValueOptional(K key, V element) {
        return Optional.ofNullable(getValue(key, element));
    }

    /**
     * Retrieves the collection of values associated with the given key.
     *
     * @param key the key
     * @return the collection of values, or an empty collection if none exist
     */
    public Collection<V> getValues(K key) {
        return delegate.computeIfAbsent(key, k -> createEmptyCollection());
    }
}
