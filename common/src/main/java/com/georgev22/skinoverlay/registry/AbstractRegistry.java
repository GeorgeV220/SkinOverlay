package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.datastructures.maps.ConcurrentHashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMaps;
import com.georgev22.skinoverlay.datastructures.maps.UnmodifiableObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A base implementation of {@link Registry} that stores key–value
 * pairs inside a thread-safe {@link ConcurrentHashObjectMap}.
 * <p>
 * Subclasses may extend this to customize key extraction behavior
 * for the {@code register(V)} and {@code replaceOrRegister(V)} methods.
 * </p>
 *
 * @param <K> the key type used for lookup
 * @param <V> the stored value type
 * @see Registry
 */
public abstract class AbstractRegistry<K, V> implements Registry<K, V> {

    protected final ObjectMap<K, V> registry;

    /**
     * Creates a new abstract registry backed by a {@link ConcurrentHashObjectMap}.
     * <p>
     * The default constructor ensures a thread-safe underlying storage,
     * while allowing subclasses to build additional behavior.
     * </p>
     */
    protected AbstractRegistry() {
        this.registry = ObjectMaps.newConcurrentHashObjectMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(@NotNull K key, @NotNull V value) {
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Already registered for key: " + key);
        }
        registry.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean replaceOrRegister(@NotNull K key, @NotNull V value) {
        boolean exists = registry.containsKey(key);
        registry.put(key, value);
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unregister(@NotNull K key) {
        return registry.remove(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Optional<V> get(K key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registry.get(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(@NotNull K key) {
        return registry.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ObjectMap<K, V> entries() {
        return new UnmodifiableObjectMap<>(registry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        registry.clear();
    }
}
