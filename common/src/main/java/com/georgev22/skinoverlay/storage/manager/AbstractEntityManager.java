package com.georgev22.skinoverlay.storage.manager;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.datastructures.lists.UnmodifiableArrayList;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObservableObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.UnmodifiableObjectMap;
import com.georgev22.skinoverlay.storage.EntityFactory;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.ManagedEntity;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.task.ExecutorManager;
import com.georgev22.skinoverlay.task.ExecutorType;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class AbstractEntityManager<E extends Entity> implements EntityManager<E> {

    private static final ObjectMap<
            Class<? extends Entity>,
            EntityFactory<? extends Entity>
            > FACTORIES = new HashObjectMap<>();
    private static final List<ManagedEntity<? extends Entity>> MANAGED_ENTITIES = List.of(
            new ManagedEntity<>(
                    "playerData",
                    PlayerData.class,
                    data -> new PlayerData(data.get("id", UUID.class))
            ),
            new ManagedEntity<>(
                    "skin",
                    Skin.class,
                    data -> new Skin(data.get("id", UUID.class))
            )
    );

    static {
        for (ManagedEntity<? extends Entity> entity : MANAGED_ENTITIES) {
            FACTORIES.put(entity.type(), entity.factory());
        }
    }

    public static List<ManagedEntity<? extends Entity>> getManagedEntities() {
        return MANAGED_ENTITIES;
    }

    protected final ObservableObjectMap<String, E> loadedEntities = new ObservableObjectMap<>();
    protected final UnmodifiableObjectMap<String, E> unmodifiableLoadedEntities = new UnmodifiableObjectMap<>(loadedEntities);
    protected final SkinOverlay main = SkinOverlay.getInstance();
    protected final ExecutorService executorService = ExecutorManager.getInstance().getExecutor(ExecutorType.IO);
    protected final Set<UUID> saveQueue = ConcurrentHashMap.newKeySet();
    protected boolean isShuttingDown = false;
    private final ManagedEntity<E> managedEntity;
    private final Lock saveLock = new ReentrantLock();

    public AbstractEntityManager(ManagedEntity<E> managedEntity) {
        long saveInterval = OptionsUtil.SAVE_INTERVAL.getLongValue();
        ExecutorManager.getInstance().scheduleAtFixedRate(saveInterval, saveInterval, TimeUnit.SECONDS, this::batchSave);
        this.managedEntity = managedEntity;
    }

    @Override
    public ManagedEntity<E> getManagedEntity() {
        return managedEntity;
    }

    /**
     * Saves the provided entity.
     *
     * @param entity the entity to be saved
     */
    @Override
    public void save(@NonNull E entity) {
        // Update memory cache
        this.loadedEntities.append(entity.getUniqueId().toString(), entity);
        // Add to save queue
        saveQueue.add(entity.getUniqueId());
    }

    @Override
    public Optional<E> findById(@NonNull String id) {
        return Optional.ofNullable(loadedEntities.get(id));
    }

    @Override
    public void saveAll(Consumer<E> consumer) {
        main.getLogger().info("Saving all " + this.getManagedEntity().key() + " data...");

        for (E entity : new ArrayList<>(loadedEntities.values())) {
            consumer.accept(entity);
            saveQueue.add(entity.getUniqueId());
        }

        batchSave();
    }

    @Override
    public List<E> getAll() {
        return new UnmodifiableArrayList<>(this.loadedEntities.values());
    }

    @Override
    public UnmodifiableObjectMap<String, E> getLoadedEntities() {
        return this.unmodifiableLoadedEntities;
    }

    @Override
    public Optional<E> create(@NonNull ObjectMap<String, Object> data,
                              @Nullable Consumer<E> consumer) {
        EntityFactory<?> factory = FACTORIES.get(this.getEntityClass());
        if (factory == null) {
            main.getLogger().log(Level.WARNING, "No entity factory registered for " + this.getManagedEntity().key());
            main.getLogger().log(Level.WARNING, "Valid factories: ");
            for (Map.Entry<Class<? extends Entity>, EntityFactory<? extends Entity>> entityClass : FACTORIES.entrySet()) {
                main.getLogger().log(Level.WARNING, " - " + entityClass.getKey().getSimpleName());
            }
            return Optional.empty();
        }

        E entity = this.getEntityClass().cast(factory.create(data));
        if (consumer != null) {
            consumer.accept(entity);
        }

        loadedEntities.put(entity.getUniqueId().toString(), entity);
        saveQueue.add(entity.getUniqueId());
        entity.postCreate();

        return Optional.of(entity);
    }

    @Override
    public void delete(@NonNull E entity) {
        saveQueue.remove(entity.getUniqueId());
        this.loadedEntities.remove(entity.getUniqueId().toString());
        entity.postDelete();
        this.delete0(entity);
    }

    @Override
    public CompletableFuture<Optional<E>> load(@NonNull String id) {
        if (loadedEntities.containsKey(id)) {
            return CompletableFuture.completedFuture(Optional.of(loadedEntities.get(id)));
        }
        return CompletableFuture.supplyAsync(() -> {
            Optional<E> entity = load0(id);
            entity.ifPresent(Entity::postLoad);
            return entity;
        }, executorService);
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public void shutdown(Consumer<E> consumer) {
        isShuttingDown = true;

        for (E entity : new ArrayList<>(loadedEntities.values())) {
            consumer.accept(entity);
            saveQueue.add(entity.getUniqueId());
        }

        saveLock.lock();
        try {
            batchSave();
        } finally {
            saveLock.unlock();
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    protected void batchSave() {
        if (!saveLock.tryLock()) {
            return;
        }

        if (OptionsUtil.DEBUG.getBooleanValue())
            main.getLogger().info("Saving " + this.getManagedEntity().key() + " data...");

        try {
            if (saveQueue.isEmpty()) return;

            Set<UUID> toSave = new HashSet<>(saveQueue);
            saveQueue.clear();

            for (UUID id : toSave) {
                E entity = loadedEntities.get(id.toString());
                if (entity != null) {
                    saveEntityWithRetry(entity);
                }
            }
        } finally {
            saveLock.unlock();
        }
    }

    protected void saveEntityWithRetry(E entity) {
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            if (save0(entity)) {
                entity.postSave();
                return;
            }

            main.getLogger().log(
                    Level.WARNING,
                    "Failed to save {0} {1}, retry {2}/{3}",
                    new Object[]{this.getManagedEntity().key(), entity.getUniqueId(), attempt, maxRetries}
            );
        }

        main.getLogger().log(
                Level.SEVERE,
                "Failed to save {0} {1} after {2} retries",
                new Object[]{this.getManagedEntity().key(), entity.getUniqueId(), maxRetries}
        );
    }

    protected abstract boolean save0(@NonNull E entity);

    protected abstract void delete0(@NonNull E entity);

    protected abstract Optional<E> load0(@NonNull String id);

    protected void logLoadAll(@NonNull List<String> successList, @NonNull List<String> failedList, long startTime) {
        long duration = System.nanoTime() - startTime;

        this.main.getLogger().info("Finished loading entity " + getManagedEntity().key() + " data:");
        this.main.getLogger().info("  ✔ Loaded: " + successList.size());
        this.main.getLogger().info("  ✘ Failed: " + failedList.size());
        this.main.getLogger().info("  ⏱ Duration: " + (duration / 1_000_000.0) + " ms");

        if (!failedList.isEmpty()) {
            if (!OptionsUtil.DEBUG.getBooleanValue())
                this.main.getLogger().info("Enable DEBUG mode for more details.");
            else
                this.main.getLogger().warning("Failed to load the following " + this.getManagedEntity().key() + "s: " + String.join(", ", failedList));
        }
    }

}
