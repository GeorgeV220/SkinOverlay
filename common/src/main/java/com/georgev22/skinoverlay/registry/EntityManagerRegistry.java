package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.logging.Level;

/**
 * A centralized global registry for {@link EntityManager} instances.
 * <p>
 * Each registered manager is indexed by the {@link Class} of the entity type it manages.
 * This enables fast and type-safe lookup of storage handlers used throughout the API.
 * </p>
 *
 * <h2>Key Characteristics</h2>
 * <ul>
 *     <li>Singleton access via {@link #getInstance()}</li>
 *     <li>Automatically extracts keys from registered values using {@link EntityManager#getEntityClass()}</li>
 *     <li>Provides type-safe lookups with {@link #getTyped(Class)}</li>
 * </ul>
 *
 * <p>This registry is primarily intended for internal use but remains public API for plugin extension.</p>
 *
 * @see EntityManager
 * @see Entity
 * @see AbstractRegistry
 * @see Registry
 */
@ApiStatus.Internal
public final class EntityManagerRegistry
        extends AbstractRegistry<Class<? extends Entity>, EntityManager<? extends Entity>> {

    /**
     * Singleton instance of this registry.
     */
    private static final EntityManagerRegistry INSTANCE = new EntityManagerRegistry();

    /**
     * Constructs the registry.
     * <p>
     * Restricted as private to enforce the singleton pattern.
     * </p>
     */
    private EntityManagerRegistry() {
    }

    /**
     * Returns the global singleton instance of this registry.
     *
     * @return the shared {@link EntityManagerRegistry} instance
     */
    public static EntityManagerRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the given manager using the entity class returned by
     * {@link EntityManager#getEntityClass()} as its key.
     *
     * @param value the manager to register
     * @throws IllegalArgumentException if a manager for the same entity class is already registered
     * @see Registry#register(Object, Object)
     */
    @Override
    public void register(@NonNull EntityManager<? extends Entity> value) throws IllegalArgumentException {
        super.register(value.getEntityClass(), value);
    }

    /**
     * Registers or replaces a manager based on {@link EntityManager#getEntityClass()}.
     *
     * @param value the manager to register or replace
     * @return {@code true} if a previous manager was replaced, {@code false} if newly registered
     * @see Registry#replaceOrRegister(Object, Object)
     */
    @Override
    public boolean replaceOrRegister(@NonNull EntityManager<? extends Entity> value) {
        return super.replaceOrRegister(value.getEntityClass(), value);
    }

    /**
     * Retrieves a typed {@link EntityManager} for the specified entity type.
     *
     * <p>This performs both lookup and runtime validation to ensure safe casting.</p>
     * <p>If type mismatch is detected, the result is empty and—when debug mode is enabled—
     * a detailed {@link ClassCastException} is logged.</p>
     *
     * @param <T>   the entity type handled by the requested manager
     * @param clazz the entity class key
     * @return an {@link Optional} containing the validated, typed manager if present
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> @NonNull Optional<EntityManager<T>> getTyped(Class<T> clazz) {
        Optional<? extends EntityManager<? extends Entity>> optionalRaw = super.get(clazz);

        if (optionalRaw.isEmpty()) {
            return Optional.empty();
        }

        EntityManager<? extends Entity> rawManager = optionalRaw.get();

        if (!clazz.isAssignableFrom(rawManager.getEntityClass())) {
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                SkinOverlay.getInstance().getLogger().log(
                        Level.SEVERE,
                        "Failed to get typed manager",
                        new ClassCastException(
                                "EntityManager registered for "
                                        + rawManager.getEntityClass().getName()
                                        + " cannot be cast to EntityManager<" + clazz.getName() + ">"
                        )
                );
            }
            return Optional.empty();
        }

        return Optional.of((EntityManager<T>) rawManager);
    }
}
