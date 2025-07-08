package com.georgev22.skinoverlay.storage.manager;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.maps.ObservableObjectMap;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class PlayerEntityManager implements EntityManager<PlayerData> {

    protected final ObservableObjectMap<String, PlayerData> loadedEntities = new ObservableObjectMap<>();
    protected final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    protected final Set<UUID> saveQueue = ConcurrentHashMap.newKeySet();
    protected boolean isShuttingDown = false;
    protected boolean isSaving = false;

    public PlayerEntityManager() {
        long saveInterval = OptionsUtil.SAVE_INTERVAL.getLongValue();
        executorService.scheduleAtFixedRate(this::batchSave, saveInterval, saveInterval, TimeUnit.SECONDS);
    }

    /**
     * Saves the provided entity.
     *
     * @param entity the entity to be saved
     */
    @Override
    public void save(@NotNull PlayerData entity) {
        // Update memory cache
        this.loadedEntities.append(entity.getId().toString(), entity);
        // Add to save queue
        saveQueue.add(entity.getId());
    }

    @Override
    public Optional<PlayerData> findById(@NotNull String id) {
        return this.loadedEntities.get(id) == null ? this.getEntity(id, true) : Optional.ofNullable(this.loadedEntities.get(id));
    }

    @Override
    public void saveAll(Consumer<PlayerData> consumer) {
        this.mainPlugin.getLogger().info("Saving all player data...");
        batchSave();
        for (PlayerData entity : new ArrayList<>(loadedEntities.values())) {
            consumer.accept(entity);
            saveEntityWithRetry(entity);
        }
    }

    @Override
    public List<PlayerData> getAll() {
        return new ArrayList<>(loadedEntities.values());
    }

    @Override
    public Optional<PlayerData> create(@NotNull String id, @NotNull Consumer<PlayerData> consumer) {
        PlayerData entity = new PlayerData(UUID.fromString(id));
        consumer.accept(entity);
        saveEntityWithRetry(entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<PlayerData> getEntity(@NotNull String id, boolean loadIfExists) {
        if (this.loadedEntities.containsKey(id)) {
            return Optional.ofNullable(this.loadedEntities.get(id));
        }
        if (loadIfExists && this.exists(id)) {
            Optional<PlayerData> entity = this.load(id);
            if (entity.isPresent()) {
                this.loadedEntities.put(id, entity.get());
                return entity;
            }
        }
        return create(id, entity -> {
        });
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void shutdown(Consumer<PlayerData> consumer) {
        isShuttingDown = true;
        executorService.shutdownNow();
        this.mainPlugin.getLogger().info("Saving all player data synchronously on shutdown...");
        List<PlayerData> entities = new ArrayList<>(loadedEntities.values());
        for (PlayerData entity : entities) {
            consumer.accept(entity);
            saveEntityWithRetry(entity);
        }
    }

    protected void saveEntityWithRetry(PlayerData entity) {
        if (!SkinOverlay.getInstance().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            success = save0(entity, isShuttingDown);
            if (!success) {
                retryCount++;
                mainPlugin.getLogger().log(Level.WARNING, "Failed to save player data for {0}, retry {1}/{2}",
                        new Object[]{entity.getId(), retryCount, maxRetries});
            }
        }

        if (!success) {
            mainPlugin.getLogger().log(Level.SEVERE, "Failed to save player data for {0} after {1} retries",
                    new Object[]{entity.getId(), maxRetries});
        }
    }

    protected void batchSave() {
        if (isSaving || saveQueue.isEmpty()) return;

        isSaving = true;
        Set<UUID> toSave = new HashSet<>(saveQueue);
        saveQueue.clear();

        for (UUID entityId : toSave) {
            if (this.loadedEntities.get(entityId.toString()) == null) {
                continue;
            }
            saveEntityWithRetry(this.loadedEntities.get(entityId.toString()));
        }

        isSaving = false;
    }

    protected abstract boolean save0(PlayerData entity, boolean isShuttingDown);

}
