package com.georgev22.skinoverlay.storage.manager;

import com.georgev22.library.database.DatabaseWrapper;
import com.georgev22.library.database.DatabaseWrapper.DatabaseObject;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.utilities.EntityManager;
import com.georgev22.library.yaml.file.YamlConfiguration;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.mongodb.annotations.Beta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link SkinManager} class is responsible for managing {@link Skin} objects in a persistence storage.
 * It supports multiple storage types including MySQL, SQLite, PostgreSQL, MongoDB, and FILE.
 * The class provides methods for checking if a {@link Skin} exists,
 * loading a {@link Skin}, and creating a {@link Skin}.
 *
 * @author <a href="https://github.com/GeorgeV220">GeorgeV220</a>
 */
public class SkinManager implements EntityManager<Skin> {
    private final File entitiesDirectory;
    private final DatabaseWrapper database;
    private final String collection;
    private final ObservableObjectMap<UUID, Skin> loadedEntities = new ObservableObjectMap<>();
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Constructor for the EntityManager class
     *
     * @param obj            the object to be used for storage (DatabaseWrapper or File)
     * @param collectionName the name of the collection to be used for MONGODB and SQL, null for other types
     */
    public SkinManager(Object obj, @Nullable String collectionName) {
        this.collection = collectionName;
        if (obj instanceof File folder) {
            this.entitiesDirectory = folder;
            this.database = null;
            if (!this.entitiesDirectory.exists()) {
                if (this.entitiesDirectory.mkdirs()) {
                    this.skinOverlay.getLogger().info("Created entities directory: " + this.entitiesDirectory.getAbsolutePath());
                }
            }
        } else if (obj instanceof DatabaseWrapper databaseWrapper) {
            this.entitiesDirectory = null;
            this.database = databaseWrapper;
        } else {
            this.entitiesDirectory = null;
            this.database = null;
        }
    }

    /**
     * Loads the {@link Skin} with the specified ID
     *
     * @param entityId the {@link UUID} of the entity to be loaded
     * @return a {@link CompletableFuture} containing the loaded {@link Skin} object
     */
    @Override
    public CompletableFuture<Skin> load(UUID entityId) {
        return exists(entityId)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.supplyAsync(() -> {
                            if (entitiesDirectory != null) {
                                File file = new File(entitiesDirectory, entityId + ".yml");
                                try {
                                    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                                    Skin entity = (Skin) yamlConfiguration.get("entity");
                                    loadedEntities.append(entityId, entity);
                                    return entity;
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (database != null) {
                                Pair<String, List<DatabaseObject>> retrievedData = database.retrieveData(collection, Pair.create("entity_id", entityId.toString()));
                                Optional<Skin> optionalEntity = retrievedData.value().stream()
                                        .filter(databaseObject -> databaseObject.data().get("data") != null)
                                        .map(databaseObject -> Skin.fromJson((String) databaseObject.data().get("data")))
                                        .findFirst();
                                Skin entity = optionalEntity.orElseGet(() -> new Skin(entityId));
                                loadedEntities.append(entityId, entity);
                                return entity;
                            } else {
                                return new Skin(entityId);
                            }
                        });
                    } else {
                        return createEntity(entityId);
                    }
                });
    }

    /**
     * Saves the specified {@link Skin}.
     *
     * @param entity the {@link Skin} to save
     * @return a {@link CompletableFuture} that completes when the {@link Skin} is saved
     */
    @Override
    public CompletableFuture<Void> save(Skin entity) {
        return CompletableFuture.runAsync(() -> {
            if (entitiesDirectory != null) {
                File file = new File(entitiesDirectory, entity.getId() + ".yml");
                try {
                    YamlConfiguration yamlConfiguration = new YamlConfiguration();
                    yamlConfiguration.set("entity", entity);
                    yamlConfiguration.save(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (database != null) {
                exists(entity.getId()).thenAccept(result -> {
                    ObjectMap<String, Object> entityData = new HashObjectMap<>();
                    entityData.append("entity_id", entity.getId().toString());
                    entityData.append("data", entity.toJson());
                    if (result) {
                        database.updateData(collection, Pair.create("entity_id", entity.getId().toString()), Pair.create("$set", entityData.removeEntry("entity_id")), null);
                    } else {
                        database.addData(collection, Pair.create(entity.getId().toString(), entityData));
                    }
                });
            }
            this.loadedEntities.append(entity.getId(), entity);
        });
    }

    /**
     * Deletes the specified entity.
     *
     * @param entity the {@link Skin} to delete
     * @return a {@link CompletableFuture} that completes when the {@link Skin} is deleted
     */
    @Override
    public CompletableFuture<Void> delete(Skin entity) {
        return CompletableFuture.runAsync(() -> {
            if (entitiesDirectory != null) {
                File file = new File(entitiesDirectory, entity.getId() + ".yml");
                if (file.exists()) {
                    if (file.delete()) {
                        this.skinOverlay.getLogger().info("Deleted Skin: " + file.getAbsolutePath());
                    }
                }
            } else if (database != null) {
                exists(entity.getId()).thenAccept(result -> {
                    ObjectMap<String, Object> entityData = new HashObjectMap<>(entity.getCustomData());
                    if (result) {
                        database.removeData(collection, Pair.create("entity_id", entity.getId().toString()), null);
                        this.skinOverlay.getLogger().info("Deleted Skin: " + entity.getId());
                    }
                });
            }
            this.loadedEntities.remove(entity.getId());
        });
    }

    /**
     * Creates a new {@link Skin} with the specified entity ID.
     *
     * @param entityId the {@link UUID} of the entity to create
     * @return a {@link CompletableFuture} that returns the newly created {@link Skin}
     */
    @Override
    public CompletableFuture<Skin> createEntity(UUID entityId) {
        return CompletableFuture.completedFuture(loadedEntities.append(entityId, new Skin(entityId)).get(entityId));
    }

    /**
     * Determines if a {@link Skin} with the specified entity ID exists.
     *
     * @param entityId the {@link UUID} of the entity to check
     * @return a {@link CompletableFuture} that returns true if a {@link Skin} with the specified ID exists, false otherwise
     */
    @Override
    public CompletableFuture<Boolean> exists(UUID entityId) {
        return CompletableFuture.supplyAsync(() -> {
            if (entitiesDirectory != null) {
                return new File(entitiesDirectory, entityId + ".yml").exists();
            } else if (database != null) {
                return database.exists(collection, Pair.create("entity_id", entityId.toString()), null);
            } else {
                return false;
            }
        });
    }

    /**
     * Retrieves the {@link Skin} with the given {@link UUID}.
     * <p>
     * If the entity is already loaded, it is returned immediately.
     * If not, it is loaded
     * asynchronously and returned in a {@link CompletableFuture}.
     *
     * @param entityId the {@link UUID} of the entity to retrieve
     * @return a {@link CompletableFuture} that will contain the {@link Skin} with the given id
     */
    @Override
    public CompletableFuture<Skin> getEntity(UUID entityId) {
        if (loadedEntities.containsKey(entityId)) {
            return CompletableFuture.completedFuture(loadedEntities.get(entityId));
        }

        return load(entityId);
    }

    /**
     * Saves all the loaded {@link Skin}s in the {@link #loadedEntities} map.
     * For each {@link Skin} in the map,
     * this method calls the {@link #save(Skin)} method to persist the {@link Skin}.
     */
    @Override
    public void saveAll() {
        ObjectMap<UUID, Skin> entities = new ObservableObjectMap<UUID, Skin>().append(loadedEntities);
        entities.forEach((uuid, entity) -> save(entity));
    }

    /**
     * Loads all the entities by retrieving their IDs and invoking the {@link #load(UUID)} method.
     * If the entities directory is specified, it scans the directory for entity files and extracts their IDs.
     * If the database is specified, it retrieves entity IDs from the database and loads them.
     */
    @Beta
    @Override
    public void loadAll() {
        List<UUID> entityIDs = new ArrayList<>();
        if (entitiesDirectory != null) {
            File[] files = this.entitiesDirectory.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                Arrays.stream(files).forEach(file -> entityIDs.add(UUID.fromString(file.getName().replace(".yml", ""))));
            }
        } else if (database != null) {
            Pair<String, List<DatabaseObject>> data = database.retrieveData(collection, Pair.create("entity_id", null));
            data.value().forEach(databaseObject -> entityIDs.add(UUID.fromString(String.valueOf(databaseObject.data().get("entity_id")))));
        }
        entityIDs.forEach(this::load);
    }

    /**
     * Retrieves the current map of loaded entities.
     *
     * @return the map of loaded entities with UUID as the key and Skin object as the value
     */
    @Override
    public ObservableObjectMap<UUID, Skin> getLoadedEntities() {
        return loadedEntities;
    }
}
