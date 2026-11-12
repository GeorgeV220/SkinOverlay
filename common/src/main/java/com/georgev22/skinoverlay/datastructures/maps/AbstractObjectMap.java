package com.georgev22.skinoverlay.datastructures.maps;

import com.georgev22.skinoverlay.utilities.DeepCloner;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract base class for {@link ObjectMap} implementations.
 * Provides common typed getter implementations delegating to the underlying {@link Map}.
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractObjectMap<K, V> implements ObjectMap<K, V> {

    protected Map<K, V> delegate;

    protected AbstractObjectMap(Map<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
    }

    /**
     * Attempts to retrieve a value associated with the specified key and cast it to the given class.
     * If the value is null or cannot be cast to the specified class, null is returned.
     *
     * @param key   the key whose associated value is to be returned
     * @param clazz the class object of the type to cast the value to
     * @param <T>   the type of the value to be returned
     * @return the value associated with the key cast to the specified class, or null if the value is null or the cast fails
     */
    public <T> T castOrNull(Object key, Class<T> clazz) {
        //noinspection SuspiciousMethodCalls
        Object val = delegate.get(key);
        if (val == null) return null;
        try {
            return clazz.cast(val);
        } catch (ClassCastException ignored) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Set<K> keySet() {
        return delegate.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Collection<V> values() {
        return delegate.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    /**
     * Adds or replaces a single entry, returning this map for fluent chaining.
     *
     * @param key   the key
     * @param value the value
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(K key, V value) {
        delegate.put(key, value);
        return this;
    }

    /**
     * Adds or replaces all entries from the given map, returning this map for fluent chaining.
     *
     * @param map the map to append
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(Map<K, V> map) {
        delegate.putAll(map);
        return this;
    }

    /**
     * Adds or replaces all entries from the given {@link ObjectMap}, returning this map for fluent chaining.
     *
     * @param map the map to append
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(ObjectMap<K, V> map) {
        delegate.putAll(map);
        return this;
    }

    /**
     * Adds or replaces an entry if the given {@link Optional} contains a value.
     *
     * @param key   the key
     * @param value optional value (if empty, nothing is added)
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(K key, @NonNull Optional<? extends V> value) {
        value.ifPresent(v -> delegate.put(key, v));
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
    @Override
    public ObjectMap<K, V> append(K key, V value, boolean ifTrue) {
        if (ifTrue) delegate.put(key, value);
        return this;
    }

    /**
     * Adds or replaces an entry with one of two possible values depending on the condition.
     *
     * @param key          the key
     * @param valueIfTrue  value if condition is true
     * @param valueIfFalse value if condition is false
     * @param ifTrue       condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(K key, V valueIfTrue, V valueIfFalse, boolean ifTrue) {
        if (ifTrue) delegate.put(key, valueIfTrue);
        else delegate.put(key, valueIfFalse);
        return this;
    }

    /**
     * Adds or replaces all entries from the given map if the condition is true.
     *
     * @param map    the map
     * @param ifTrue condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(Map<K, V> map, boolean ifTrue) {
        if (ifTrue) delegate.putAll(map);
        return this;
    }

    /**
     * Adds or replaces all entries from one of two maps depending on the condition.
     *
     * @param mapIfTrue  map if condition is true
     * @param mapIfFalse map if condition is false
     * @param ifTrue     condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(Map<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        if (ifTrue) delegate.putAll(mapIfTrue);
        else delegate.putAll(mapIfFalse);
        return this;
    }

    /**
     * Adds or replaces all entries from the given {@link ObjectMap} if the condition is true.
     *
     * @param map    the map
     * @param ifTrue condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(ObjectMap<K, V> map, boolean ifTrue) {
        if (ifTrue) delegate.putAll(map);
        return this;
    }

    /**
     * Adds or replaces all entries from one of two maps depending on the condition.
     *
     * @param mapIfTrue  map if condition is true
     * @param mapIfFalse map if condition is false
     * @param ifTrue     condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> append(ObjectMap<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        if (ifTrue) delegate.putAll(mapIfTrue);
        else delegate.putAll(mapIfFalse);
        return this;
    }

    /**
     * Removes an entry by key.
     *
     * @param key the key to remove
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntry(K key) {
        delegate.remove(key);
        return this;
    }

    /**
     * Removes all entries with keys present in the given map.
     *
     * @param map the map whose keys should be removed
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntries(Map<K, V> map) {
        delegate.keySet().removeAll(map.keySet());
        return this;
    }

    /**
     * Removes all entries with keys present in the given {@link ObjectMap}.
     *
     * @param map the map whose keys should be removed
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntries(ObjectMap<K, V> map) {
        delegate.keySet().removeAll(map.keySet());
        return this;
    }

    /**
     * Removes an entry if the condition is true.
     *
     * @param key    the key to remove
     * @param ifTrue condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntry(K key, boolean ifTrue) {
        if (ifTrue) delegate.remove(key);
        return this;
    }

    /**
     * Removes all entries from the given map if the condition is true.
     *
     * @param map    the map whose keys should be removed
     * @param ifTrue condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntries(Map<K, V> map, boolean ifTrue) {
        if (ifTrue) delegate.keySet().removeAll(map.keySet());
        return this;
    }

    /**
     * Removes all entries from the given {@link ObjectMap} if the condition is true.
     *
     * @param map    the map whose keys should be removed
     * @param ifTrue condition
     * @return this map
     */
    @Override
    public ObjectMap<K, V> removeEntries(ObjectMap<K, V> map, boolean ifTrue) {
        if (ifTrue) delegate.keySet().removeAll(map.keySet());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(Object key) {
        return castOrNull(key, Long.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong(Object key, long defaultValue) {
        return Optional.ofNullable(getLong(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(Object key) {
        return castOrNull(key, Double.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble(Object key, double defaultValue) {
        return Optional.ofNullable(getDouble(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(Object key) {
        return castOrNull(key, Date.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(Object key, Date defaultValue) {
        return Optional.ofNullable(getDate(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(Object key, Class<T> clazz) {
        //noinspection SuspiciousMethodCalls
        Object val = delegate.get(key);
        if (val == null) return null;
        if (!(val instanceof List<?> list)) {
            throw new ClassCastException("Value is not a List for key: " + key);
        }
        for (Object obj : list) {
            if (!clazz.isInstance(obj)) {
                throw new ClassCastException(
                        "List element is not of type " + clazz.getName() + " for key: " + key
                );
            }
        }
        //noinspection unchecked
        return (List<T>) list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(Object key, Class<T> clazz, List<T> defaultValue) {
        List<T> list = getList(key, clazz);
        return list != null ? list : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInteger(Object key) {
        return castOrNull(key, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInteger(Object key, int defaultValue) {
        return Optional.ofNullable(getInteger(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(Object key) {
        return castOrNull(key, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(Object key, String defaultValue) {
        return Optional.ofNullable(getString(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean(Object key) {
        return castOrNull(key, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(Object key, boolean defaultValue) {
        return Optional.ofNullable(getBoolean(key)).orElse(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T get(Object key, Class<T> clazz) {
        return castOrNull(key, clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T get(Object key, T defaultValue) {
        //noinspection SuspiciousMethodCalls
        Object val = delegate.get(key);
        //noinspection unchecked
        return (T) (val != null ? val : defaultValue);
    }


    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.   Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException            if the specified action is null
     * @throws ConcurrentModificationException if an entry is found to be
     *                                         removed during iteration
     * @implSpec The default implementation is equivalent to, for this {@code map}:
     * <pre> {@code
     * for (Map.Entry<K, V> entry : map.entrySet())
     *     action.accept(entry.getKey(), entry.getValue());
     * }</pre>
     * <p>
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        delegate.forEach(action);
    }

    /**
     * Replaces each entry's value with the result of invoking the given
     * function on that entry until all entries have been processed or the
     * function throws an exception.  Exceptions thrown by the function are
     * relayed to the caller.
     *
     * @param function the function to apply to each entry
     * @throws UnsupportedOperationException   if the {@code set} operation
     *                                         is not supported by this map's entry set iterator.
     * @throws ClassCastException              if the class of a replacement value
     *                                         prevents it from being stored in this map
     *                                         ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException            if the specified function is null, or if a
     *                                         replacement value is null and this map does not permit null values
     *                                         ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException        if some property of a replacement value
     *                                         prevents it from being stored in this map
     *                                         ({@linkplain Collection##optional-restrictions optional})
     * @throws ConcurrentModificationException if an entry is found to be
     *                                         removed during iteration
     * @implSpec <p>The default implementation is equivalent to, for this {@code map}:
     * <pre> {@code
     * for (Map.Entry<K, V> entry : map.entrySet())
     *     entry.setValue(function.apply(entry.getKey(), entry.getValue()));
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        delegate.replaceAll(function);
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the key or value is of an inappropriate
     *                                       type for this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException          if the specified key or value is null,
     *                                       and this map does not permit null keys or values
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation is equivalent to, for this {@code map}:
     *
     * <pre> {@code
     * V v = map.get(key);
     * if (v == null)
     *     v = map.put(key, value);
     *
     * return v;
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public @Nullable V putIfAbsent(K key, V value) {
        return delegate.putIfAbsent(key, value);
    }

    /**
     * Removes the entry for the specified key only if it is currently
     * mapped to the specified value.
     *
     * @param key   key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the key or value is of an inappropriate
     *                                       type for this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException          if the specified key or value is null,
     *                                       and this map does not permit null keys or values
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation is equivalent to, for this {@code map}:
     *
     * <pre> {@code
     * if (map.containsKey(key) && Objects.equals(map.get(key), value)) {
     *     map.remove(key);
     *     return true;
     * } else
     *     return false;
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    /**
     * Replaces the entry for the specified key only if currently
     * mapped to the specified value.
     *
     * @param key      key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of a specified key or value
     *                                       prevents it from being stored in this map
     * @throws NullPointerException          if a specified key or newValue is null,
     *                                       and this map does not permit null keys or values
     * @throws NullPointerException          if oldValue is null and this map does not
     *                                       permit null values ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of a specified key
     *                                       or value prevents it from being stored in this map
     * @implSpec The default implementation is equivalent to, for this {@code map}:
     *
     * <pre> {@code
     * if (map.containsKey(key) && Objects.equals(map.get(key), oldValue)) {
     *     map.put(key, newValue);
     *     return true;
     * } else
     *     return false;
     * }</pre>
     * <p>
     * The default implementation does not throw NullPointerException
     * for maps that do not support null values if oldValue is null unless
     * newValue is also null.
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    /**
     * Replaces the entry for the specified key only if it is
     * currently mapped to some value.
     *
     * @param key   key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException          if the specified key or value is null,
     *                                       and this map does not permit null keys or values
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     * @implSpec The default implementation is equivalent to, for this {@code map}:
     *
     * <pre> {@code
     * if (map.containsKey(key)) {
     *     return map.put(key, value);
     * } else
     *     return null;
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     * @since 1.8
     */
    @Override
    public @Nullable V replace(K key, V value) {
        return delegate.replace(key, value);
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the mapping function returns {@code null}, no mapping is recorded.
     * If the mapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.  The most
     * common usage is to construct a new object serving as an initial
     * mapped value or memoized result, as in:
     *
     * <pre> {@code
     * map.computeIfAbsent(key, k -> new Value(f(k)));
     * }</pre>
     *
     * <p>Or to implement a multi-value map, {@code Map<K,Collection<V>>},
     * supporting multiple values per key:
     *
     * <pre> {@code
     * map.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
     * }</pre>
     *
     * <p>The mapping function should not modify this map during computation.
     *
     * @param key             key with which the specified value is to be associated
     * @param mappingFunction the mapping function to compute a value
     * @return the current (existing or computed) value associated with
     * the specified key, or null if the computed value is null
     * @throws NullPointerException          if the specified key is null and
     *                                       this map does not support null keys, or the mappingFunction
     *                                       is null
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation is equivalent to the following steps for this
     * {@code map}, then returning the current value or {@code null} if now
     * absent:
     *
     * <pre> {@code
     * if (map.get(key) == null) {
     *     V newValue = mappingFunction.apply(key);
     *     if (newValue != null)
     *         map.put(key, newValue);
     * }
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about detecting if the
     * mapping function modifies this map during computation and, if
     * appropriate, reporting an error. Non-concurrent implementations should
     * override this method and, on a best-effort basis, throw a
     * {@code ConcurrentModificationException} if it is detected that the
     * mapping function modifies this map during computation. Concurrent
     * implementations should override this method and, on a best-effort basis,
     * throw an {@code IllegalStateException} if it is detected that the
     * mapping function modifies this map during computation and as a result
     * computation would never complete.
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties. In particular, all implementations of
     * subinterface {@link ConcurrentMap} must document
     * whether the mapping function is applied once atomically only if the value
     * is not present.
     * @since 1.8
     */
    @Override
    public V computeIfAbsent(K key, @NonNull Function<? super K, ? extends V> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    /**
     * If the value for the specified key is present and non-null, attempts to
     * compute a new mapping given the key and its current mapped value.
     *
     * <p>If the remapping function returns {@code null}, the mapping is removed.
     * If the remapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and the current mapping is left unchanged.
     *
     * <p>The remapping function should not modify this map during computation.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the remapping function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException          if the specified key is null and
     *                                       this map does not support null keys, or the
     *                                       remappingFunction is null
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation is equivalent to performing the following
     * steps for this {@code map}, then returning the current value or
     * {@code null} if now absent:
     *
     * <pre> {@code
     * if (map.get(key) != null) {
     *     V oldValue = map.get(key);
     *     V newValue = remappingFunction.apply(key, oldValue);
     *     if (newValue != null)
     *         map.put(key, newValue);
     *     else
     *         map.remove(key);
     * }
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about detecting if the
     * remapping function modifies this map during computation and, if
     * appropriate, reporting an error. Non-concurrent implementations should
     * override this method and, on a best-effort basis, throw a
     * {@code ConcurrentModificationException} if it is detected that the
     * remapping function modifies this map during computation. Concurrent
     * implementations should override this method and, on a best-effort basis,
     * throw an {@code IllegalStateException} if it is detected that the
     * remapping function modifies this map during computation and as a result
     * computation would never complete.
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties. In particular, all implementations of
     * subinterface {@link ConcurrentMap} must document
     * whether the remapping function is applied once atomically only if the
     * value is not present.
     * @since 1.8
     */
    @Override
    public V computeIfPresent(K key, @NonNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    /**
     * Attempts to compute a mapping for the specified key and its current
     * mapped value (or {@code null} if there is no current mapping). For
     * example, to either create or append a {@code String} msg to a value
     * mapping:
     *
     * <pre> {@code
     * map.compute(key, (k, v) -> (v == null) ? msg : v.concat(msg))}</pre>
     * (Method {@link #merge merge()} is often simpler to use for such purposes.)
     *
     * <p>If the remapping function returns {@code null}, the mapping is removed
     * (or remains absent if initially absent).  If the remapping function
     * itself throws an (unchecked) exception, the exception is rethrown, and
     * the current mapping is left unchanged.
     *
     * <p>The remapping function should not modify this map during computation.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the remapping function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException          if the specified key is null and
     *                                       this map does not support null keys, or the
     *                                       remappingFunction is null
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @implSpec The default implementation is equivalent to performing the following
     * steps for this {@code map}:
     *
     * <pre> {@code
     * V oldValue = map.get(key);
     * V newValue = remappingFunction.apply(key, oldValue);
     * if (newValue != null) {
     *     map.put(key, newValue);
     * } else if (oldValue != null || map.containsKey(key)) {
     *     map.remove(key);
     * }
     * return newValue;
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about detecting if the
     * remapping function modifies this map during computation and, if
     * appropriate, reporting an error. Non-concurrent implementations should
     * override this method and, on a best-effort basis, throw a
     * {@code ConcurrentModificationException} if it is detected that the
     * remapping function modifies this map during computation. Concurrent
     * implementations should override this method and, on a best-effort basis,
     * throw an {@code IllegalStateException} if it is detected that the
     * remapping function modifies this map during computation and as a result
     * computation would never complete.
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties. In particular, all implementations of
     * subinterface {@link ConcurrentMap} must document
     * whether the remapping function is applied once atomically only if the
     * value is not present.
     * @since 1.8
     */
    @Override
    public V compute(K key, @NonNull BiFunction<? super K, ? super @Nullable V, ? extends V> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    /**
     * If the specified key is not already associated with a value or is
     * associated with null, associates it with the given non-null value.
     * Otherwise, replaces the associated value with the results of the given
     * remapping function, or removes if the result is {@code null}. This
     * method may be of use when combining multiple mapped values for a key.
     * For example, to either create or append a {@code String msg} to a
     * value mapping:
     *
     * <pre> {@code
     * map.merge(key, msg, String::concat)
     * }</pre>
     *
     * <p>If the remapping function returns {@code null}, the mapping is removed.
     * If the remapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and the current mapping is left unchanged.
     *
     * <p>The remapping function should not modify this map during computation.
     *
     * @param key               key with which the resulting value is to be associated
     * @param value             the non-null value to be merged with the existing value
     *                          associated with the key or, if no existing value or a null value
     *                          is associated with the key, to be associated with the key
     * @param remappingFunction the remapping function to recompute a value if
     *                          present
     * @return the new value associated with the specified key, or null if no
     * value is associated with the key
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     *                                       ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException          if the specified key is null and this map
     *                                       does not support null keys or the value or remappingFunction is
     *                                       null
     * @implSpec The default implementation is equivalent to performing the following
     * steps for this {@code map}, then returning the current value or
     * {@code null} if absent:
     *
     * <pre> {@code
     * V oldValue = map.get(key);
     * V newValue = (oldValue == null) ? value :
     *              remappingFunction.apply(oldValue, value);
     * if (newValue == null)
     *     map.remove(key);
     * else
     *     map.put(key, newValue);
     * }</pre>
     *
     * <p>The default implementation makes no guarantees about detecting if the
     * remapping function modifies this map during computation and, if
     * appropriate, reporting an error. Non-concurrent implementations should
     * override this method and, on a best-effort basis, throw a
     * {@code ConcurrentModificationException} if it is detected that the
     * remapping function modifies this map during computation. Concurrent
     * implementations should override this method and, on a best-effort basis,
     * throw an {@code IllegalStateException} if it is detected that the
     * remapping function modifies this map during computation and as a result
     * computation would never complete.
     *
     * <p>The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties. In particular, all implementations of
     * subinterface {@link ConcurrentMap} must document
     * whether the remapping function is applied once atomically only if the
     * value is not present.
     * @since 1.8
     */
    @Override
    public V merge(K key, @NonNull V value, @NonNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    private void putDelegate(K key, V value) {
        delegate.put(key, value);
    }

    private void putAllDelegate(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract AbstractObjectMap<K, V> newObjectMap();

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ObjectMap<K, V> shallowCopy() {
        AbstractObjectMap<K, V> copy = newObjectMap();
        copy.putAllDelegate(this);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ObjectMap<K, V> deepCopy() {
        AbstractObjectMap<K, V> copy = newObjectMap();
        for (Entry<K, V> entry : entrySet()) {
            try {
                copy.putDelegate(
                        (K) DeepCloner.cloneValue(entry.getKey()),
                        (V) DeepCloner.cloneValue(entry.getValue()));
            } catch (UnsupportedOperationException e) {
                break; // target map does not support modifications
            } catch (Exception ignored) {
            }
        }
        return copy;
    }
}
