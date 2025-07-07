package com.georgev22.skinoverlay.storage;

import com.georgev22.skinoverlay.storage.data.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Interface for managing entities with CRUD operations and additional helper methods.
 *
 * @param <E> the type of entity to manage
 */
public interface EntityManager<E extends Entity> {

    /**
     * Saves the specified entity.
     *
     * @param entity the entity to save
     */
    void save(@NotNull E entity);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    Optional<E> findById(@NotNull String id);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param uuid the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    default Optional<E> findById(@NotNull UUID uuid) {
        return findById(uuid.toString());
    }

    /**
     * Deletes the specified entity.
     *
     * @param entity the entity to delete
     */
    void delete(@NotNull E entity);

    /**
     * Loads an entity by its unique identifier.
     *
     * @param id the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    Optional<E> load(@NotNull String id);

    /**
     * Loads an entity by its unique identifier.
     *
     * @param uuid the unique identifier
     * @return an {@link Optional} containing the entity if found, or an empty Optional if not found
     */
    default Optional<E> load(@NotNull UUID uuid) {
        return load(uuid.toString());
    }

    /**
     * Loads all entities.
     */
    void loadAll();

    /**
     * Saves all entities.
     */
    default void saveAll() {
        saveAll(entity -> {
        });
    }

    /**
     * Saves all entities and executes the provided consumer on each one.
     *
     * @param consumer the consumer to apply to each saved entity
     */
    void saveAll(Consumer<E> consumer);

    /**
     * Returns all entities managed by this manager.
     *
     * @return a list of all entities
     */
    List<E> getAll();

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param id the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    boolean exists(@NotNull String id);

    /**
     * Checks if an entity with the specified identifier exists.
     *
     * @param uuid the unique identifier
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    default boolean exists(@NotNull UUID uuid) {
        return exists(uuid.toString());
    }

    /**
     * Retrieves an entity by its unique identifier. Optionally, loads the entity
     * if it exists.
     *
     * @param id           the unique identifier
     * @param loadIfExists whether to load the entity if it exists
     * @return an {@link Optional} containing the entity if found, or empty if not found
     */
    Optional<E> getEntity(@NotNull String id, boolean loadIfExists);

    /**
     * Retrieves an entity by its unique identifier. Optionally, loads the entity
     * if it exists.
     *
     * @param uuid         the unique identifier
     * @param loadIfExists whether to load the entity if it exists
     * @return an {@link Optional} containing the entity if found, or empty if not found
     */
    default Optional<E> getEntity(@NotNull UUID uuid, boolean loadIfExists) {
        return getEntity(uuid.toString(), loadIfExists);
    }

    /**
     * Creates a new entity with the specified identifier and executes the provided consumer.
     *
     * @param id           the unique identifier
     * @param consumer     the consumer to apply to the new entity
     * @return an {@link Optional} containing the new entity if created, or empty if not created
     */
    Optional<E> create(@NotNull String id, @NotNull Consumer<E> consumer);

    /**
     * Creates a new entity with the specified identifier and executes the provided consumer.
     *
     * @param uuid         the unique identifier
     * @param consumer     the consumer to apply to the new entity
     * @return an {@link Optional} containing the new entity if created, or empty if not created
     */
    default Optional<E> create(@NotNull UUID uuid, @NotNull Consumer<E> consumer) {
        return create(uuid.toString(), consumer);
    }

    /**
     * Gets the name of this entity manager.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the simple name of this entity manager.
     *
     * @return the simple name
     */
    String getSimpleName();

    /**
     * Shuts down this manager.
     */
    @ApiStatus.Internal
    default void shutdown() {
        shutdown(entity -> {
        });
    }

    /**
     * Shuts down this manager and applies the given consumer to each entity.
     *
     * @param consumer the consumer to apply
     */
    @ApiStatus.Internal
    void shutdown(Consumer<E> consumer);

}
