package com.georgev22.skinoverlay.maps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * Returns an unmodifiable view of the
 * specified map. Query operations on the returned map "read through"
 * to the specified map, and attempts to modify the returned
 * map, whether direct or via its collection views, result in an
 * {@code UnsupportedOperationException}.<p>
 */
public class UnmodifiableObjectMap<K, V> implements ObjectMap<K, V>, Serializable {

    private final HashObjectMap<K, V> hashObjectMap = new HashObjectMap<>();

    public UnmodifiableObjectMap(ObjectMap<K, V> map) {
        hashObjectMap.append(map);
    }

    public UnmodifiableObjectMap(Map<K, V> map) {
        hashObjectMap.append(map);
    }

    /**
     * Put/replace the given key/value pair into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append("b", 2)}
     * </pre>
     *
     * @param key   key
     * @param value value
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(Map<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace a given map into this ObjectMap and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.append("a", 1).append(map)}
     * </pre>
     *
     * @param map the map to append to the current one
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> append(ObjectMap<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue("b", 2, check2)}
     * </pre>
     *
     * @param key    key
     * @param value  value
     * @param ifTrue ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(K key, V value, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue("b", 3, 4, check2)}
     * </pre>
     *
     * @param key          key
     * @param valueIfTrue  the value if the ifTrue is true
     * @param valueIfFalse the value if the ifTrue is false
     * @param ifTrue       ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(K key, V valueIfTrue, V valueIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(Map<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(Map<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace a given map into this ObjectMap if boolean is true and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, check1).appendIfTrue(map, check2)}
     * </pre>
     *
     * @param map    key
     * @param ifTrue ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(ObjectMap<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Put/replace the given key/value pair into ObjectMap if boolean is true or not and return this.  Useful for chaining puts in a single expression, e.g.
     * <pre>
     * user.appendIfTrue("a", 1, 2, check1).appendIfTrue(map1, map2, check2)}
     * </pre>
     *
     * @param mapIfTrue  the map if the ifTrue is true
     * @param mapIfFalse the map if the ifTrue is false
     * @param ifTrue     ifTrue
     * @return this
     */
    @Override
    public UnmodifiableObjectMap<K, V> appendIfTrue(ObjectMap<K, V> mapIfTrue, Map<K, V> mapIfFalse, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes the entry with the specified key from the ObjectMap.
     *
     * @param key the key of the entry to be removed
     * @return the modified ObjectMap with the specified entry removed, or the original ObjectMap if the key was not found
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntry(K key) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes all entries with keys present in the specified map from the ObjectMap.
     *
     * @param map the map containing the keys to be removed
     * @return the modified ObjectMap with the entries corresponding to the specified keys removed
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(Map<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes all entries with keys present in the specified ObjectMap from the ObjectMap.
     *
     * @param map the ObjectMap containing the keys to be removed
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified ObjectMap removed
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntries(ObjectMap<K, V> map) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes the entry with the specified key from the ObjectMap if the condition is true.
     *
     * @param key    the key of the entry to be removed
     * @param ifTrue the condition to check before removing the entry
     * @return the modified ObjectMap with the specified entry removed if the condition is true, or the original ObjectMap otherwise
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntryIfTrue(K key, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes all entries with keys present in the specified map from the ObjectMap if the condition is true.
     *
     * @param map    the map containing the keys to be removed
     * @param ifTrue the condition to check before removing the entries
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified map removed if the condition is true, or the original ObjectMap otherwise
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntriesIfTrue(Map<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes all entries with keys present in the specified ObjectMap from the ObjectMap if the condition is true.
     *
     * @param map    the ObjectMap containing the keys to be removed
     * @param ifTrue the condition to check before removing the entries
     * @return the modified ObjectMap with the entries corresponding to the keys in the specified ObjectMap removed if the condition is true, or the original ObjectMap otherwise
     */
    @Override
    public UnmodifiableObjectMap<K, V> removeEntriesIfTrue(ObjectMap<K, V> map, boolean ifTrue) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Gets the value of the given key as an Integer.
     *
     * @param key the key
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    @Override
    public Integer getInteger(Object key) {
        return hashObjectMap.getInteger(key);
    }

    /**
     * Gets the value of the given key as a primitive int.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as an integer, which may be null
     * @throws ClassCastException if the value is not an integer
     */
    @Override
    public int getInteger(Object key, int defaultValue) {
        return hashObjectMap.getInteger(key, defaultValue);
    }

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key the key
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    @Override
    public Long getLong(Object key) {
        return hashObjectMap.getLong(key);
    }

    /**
     * Gets the value of the given key as a Long.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a long, which may be null
     * @throws ClassCastException if the value is not an long
     */
    @Override
    public Long getLong(Object key, long defaultValue) {
        return hashObjectMap.getLong(key, defaultValue);
    }

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key the key
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    @Override
    public Double getDouble(Object key) {
        return hashObjectMap.getDouble(key);
    }

    /**
     * Gets the value of the given key as a Double.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a double, which may be null
     * @throws ClassCastException if the value is not an double
     */
    @Override
    public Double getDouble(Object key, double defaultValue) {
        return hashObjectMap.getDouble(key, defaultValue);
    }

    /**
     * Gets the value of the given key as a String.
     *
     * @param key the key
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    @Override
    public String getString(Object key) {
        return hashObjectMap.getString(key);
    }

    /**
     * Gets the value of the given key as a String.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a String, which may be null
     * @throws ClassCastException if the value is not a String
     */
    @Override
    public String getString(Object key, String defaultValue) {
        return hashObjectMap.getString(key, defaultValue);
    }

    /**
     * Gets the value of the given key as a Boolean.
     *
     * @param key the key
     * @return the value as a Boolean, which may be null
     * @throws ClassCastException if the value is not an boolean
     */
    @Override
    public Boolean getBoolean(Object key) {
        return hashObjectMap.getBoolean(key);
    }

    /**
     * Gets the value of the given key as a primitive boolean.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a primitive boolean
     * @throws ClassCastException if the value is not a boolean
     */
    @Override
    public boolean getBoolean(Object key, boolean defaultValue) {
        return hashObjectMap.getBoolean(key, defaultValue);
    }

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key the key
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    @Override
    public Date getDate(Object key) {
        return hashObjectMap.getDate(key);
    }

    /**
     * Gets the value of the given key as a Date.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value as a Date, which may be null
     * @throws ClassCastException if the value is not a Date
     */
    @Override
    public Date getDate(Object key, Date defaultValue) {
        return hashObjectMap.getDate(key, defaultValue);
    }

    /**
     * Gets the list value of the given key, casting the list elements to the given {@code Class<T>}.  This is useful to avoid having
     * casts in client code, though the effect is the same.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the list value to
     * @return the list value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the elements in the list value of the given key is not of type T or the value is not a list
     */
    @Override
    public <T> List<T> getList(Object key, Class<T> clazz) {
        return hashObjectMap.getList(key, clazz);
    }

    /**
     * Gets the list value of the given key, casting the list elements to {@code Class<T>} or returning the default list value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param clazz        the non-null class to cast the list value to
     * @param defaultValue what to return if the value is null
     * @return the list value of the given key, or the default list value if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    @Override
    public <T> List<T> getList(Object key, Class<T> clazz, List<T> defaultValue) {
        return hashObjectMap.getList(key, clazz, defaultValue);
    }

    /**
     * Gets the value of the given key, casting it to the given {@code Class<T>}.  This is useful to avoid having casts in client code,
     * though the effect is the same.  So to get the value of a key that is of type String, you would write {@code String name =
     * doc.get("name", String.class)} instead of {@code String name = (String) doc.get("x") }.
     *
     * @param key   the key
     * @param clazz the non-null class to cast the value to
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    @Override
    public <T> T get(Object key, Class<T> clazz) {
        return hashObjectMap.get(key, clazz);
    }

    /**
     * Gets the value of the given key, casting it to {@code Class<T>} or returning the default value if null.
     * This is useful to avoid having casts in client code, though the effect is the same.
     *
     * @param key          the key
     * @param defaultValue what to return if the value is null
     * @return the value of the given key, or null if the instance does not contain this key.
     * @throws ClassCastException if the value of the given key is not of type T
     */
    @Override
    public <T> T get(Object key, T defaultValue) {
        return hashObjectMap.get(key, defaultValue);
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return hashObjectMap.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return hashObjectMap.isEmpty();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.  More formally, returns {@code true} if and only if
     * this map contains a mapping for a key {@code k} such that
     * {@code Objects.equals(key, k)}.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public boolean containsKey(Object key) {
        return hashObjectMap.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to a value {@code v} such that
     * {@code Objects.equals(value, v)}.  This operation
     * will probably require time linear in the map size for most
     * implementations of the {@code Map} interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     * specified value
     * @throws ClassCastException   if the value is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified value is null and this
     *                              map does not permit null values
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public boolean containsValue(Object value) {
        return hashObjectMap.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that
     * {@code Objects.equals(key, k)},
     * then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}.  The {@link #containsKey
     * containsKey} operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public V get(Object key) {
        return hashObjectMap.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * {@code m} is said to contain a mapping for a key {@code k} if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * {@code true}.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key},
     * if the implementation supports {@code null} values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     * @throws NullPointerException          if the specified key or value is null
     *                                       and this map does not permit null keys or values
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     */
    @Nullable
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key {@code k} to value {@code v} such that
     * {@code Objects.equals(key, k)}, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     *
     * <p>Returns the value to which this map previously associated the key,
     * or {@code null} if the map contained no mapping for the key.
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to {@code null}.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the key is of an inappropriate type for
     *                                       this map
     *                                       (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified key is null and this
     *                                       map does not permit null keys
     *                                       (<a href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(Object, Object) put(k, v)} on this map once
     * for each mapping from key {@code k} to value {@code v} in the
     * specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the {@code putAll} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map
     * @throws NullPointerException          if the specified map is null, or if
     *                                       this map does not permit null keys or values, and the
     *                                       specified map contains null keys or values
     * @throws IllegalArgumentException      if some property of a key or value in
     *                                       the specified map prevents it from being stored in this map
     */
    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *                                       is not supported by this map
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("UnmodifiableObjectMap");
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @NotNull
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(hashObjectMap.keySet());
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a collection view of the values contained in this map
     */
    @NotNull
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(hashObjectMap.values());
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(hashObjectMap.entrySet());
    }
}
