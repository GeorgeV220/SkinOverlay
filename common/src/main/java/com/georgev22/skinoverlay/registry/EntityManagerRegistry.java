package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton registry for managing EntityManager instances by entity class.
 */
public class EntityManagerRegistry {
    private static final ObjectMap<Class<? extends Entity>, EntityManager<? extends Entity>> managers = new HashObjectMap<>();

    /**
     * Registers an EntityManager for a specific entity class.
     *
     * @param entityClass the class of the entity
     * @param manager     the manager to register
     * @param <E>         the type of entity
     * @throws IllegalArgumentException if a manager is already registered for the given entity class
     */
    public static <E extends Entity> void registerManager(Class<E> entityClass, EntityManager<E> manager) throws IllegalArgumentException {
        if (managers.containsKey(entityClass)) {
            throw new IllegalArgumentException("An EntityManager is already registered for class " + entityClass.getName());
        }
        managers.put(entityClass, manager);
    }

    /**
     * Registers a new EntityManager for a specific entity class, replacing any existing manager.
     * <p>
     * **Warning**: Replacing an existing manager may lead to data loss or inconsistencies if
     * the new manager does not properly handle existing entities. Use this method with caution.
     * </p>
     *
     * @param entityClass the class of the entity
     * @param manager     the manager to register or replace
     * @param <E>         the type of entity
     * @return {@code true} if an existing manager was replaced, {@code false} if this is a new registration
     */
    public static <E extends Entity> boolean replaceOrRegisterManager(Class<E> entityClass, EntityManager<E> manager) {
        boolean isReplacing = managers.containsKey(entityClass);
        managers.put(entityClass, manager);
        return isReplacing;
    }

    /**
     * Retrieves the EntityManager for a specific entity class.
     *
     * @param entityClass the class of the entity
     * @param <E>         the type of entity
     * @return the EntityManager instance, or {@code Optional.empty()} if not registered
     */
    @SuppressWarnings("unchecked")
    public static <E extends Entity> @NotNull Optional<EntityManager<E>> getManager(Class<E> entityClass) {
        return Optional.ofNullable((EntityManager<E>) managers.get(entityClass));
    }

    /**
     * Checks if an EntityManager is registered for a specific entity class.
     *
     * @param entityClass the class of the entity
     * @param <E>         the type of entity
     * @return {@code true} if an EntityManager is registered, {@code false} otherwise
     */
    public static <E extends Entity> boolean containsManager(Class<E> entityClass) {
        return managers.containsKey(entityClass);
    }

    @Contract(pure = true)
    public static @NotNull @UnmodifiableView Map<Class<? extends Entity>, EntityManager<? extends Entity>> getManagers() {
        return Collections.unmodifiableMap(managers);
    }
}
