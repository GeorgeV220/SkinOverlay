package com.georgev22.skinoverlay.storage;

import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.storage.data.Entity;

/**
 * Factory interface used to create {@link Entity} instances.
 *
 * <p>This interface defines a generic constructor-like contract for entities,
 * using a key-value map to pass arbitrary data or parameters required
 * for construction. It is used both when creating new entities via
 * {@code EntityManager#create} and when reconstructing entities
 * from persisted data.
 *
 * <p>Implementations can use the provided map in any way they need, e.g.,
 * passing values to constructors, initializing default fields, or performing
 * validation.
 *
 * <p>This is a functional interface and can be implemented using
 * lambda expressions or method references.
 *
 * @param <E> the type of entity created by this factory
 */
@FunctionalInterface
public interface EntityFactory<E extends Entity> {

    /**
     * Creates a new entity instance using the provided data.
     *
     * @param data a map containing arbitrary parameters for entity construction
     * @return a newly constructed entity instance
     */
    E create(ObjectMap<String, Object> data);
}
