package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.datastructures.maps.ConcurrentHashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.UnmodifiableObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractRegistry<K, V> implements Registry<K, V> {

    protected final ObjectMap<K, V> registry = new ConcurrentHashObjectMap<>();

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
        if (!registry.containsKey(key)) {
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
}
