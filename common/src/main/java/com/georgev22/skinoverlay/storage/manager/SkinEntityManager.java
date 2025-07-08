package com.georgev22.skinoverlay.storage.manager;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.maps.ObservableObjectMap;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class SkinEntityManager implements EntityManager<Skin> {
    protected final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    protected final ObservableObjectMap<String, Skin> loadedEntities = new ObservableObjectMap<>();
    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    protected final Set<UUID> saveQueue = ConcurrentHashMap.newKeySet();
    protected boolean isSaving = false;
    protected boolean isShuttingDown = false;

    public SkinEntityManager() {
        long saveInterval = OptionsUtil.SAVE_INTERVAL.getLongValue();
        executorService.scheduleAtFixedRate(this::batchSave, saveInterval, saveInterval, TimeUnit.SECONDS);
    }

    @Override
    public void save(@NotNull Skin entity) {
        // Update memory cache
        this.loadedEntities.append(entity.getId().toString(), entity);
        // Add to save queue
        saveQueue.add(entity.getId());
    }

    @Override
    public Optional<Skin> getEntity(@NotNull String id, boolean loadIfExists) {
        if (loadedEntities.containsKey(id)) {
            return Optional.ofNullable(loadedEntities.get(id));
        }
        if (loadIfExists && exists(id)) {
            Optional<Skin> entity = load(id);
            if (entity.isPresent()) {
                loadedEntities.put(id, entity.get());
                return entity;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Skin> findById(@NotNull String id) {
        return loadedEntities.get(id) == null ? this.getEntity(id, true) : Optional.ofNullable(loadedEntities.get(id));
    }

    @Override
    public void saveAll(Consumer<Skin> consumer) {
        this.mainPlugin.getLogger().info("Saving all skins...");
        batchSave();
        List<Skin> entities = new ArrayList<>(loadedEntities.values());
        for (Skin entity : entities) {
            consumer.accept(entity);
            saveEntityWithRetry(entity);
        }
    }

    @Override
    public List<Skin> getAll() {
        return new ArrayList<>(loadedEntities.values());
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
    public Optional<Skin> create(@NotNull String id, @NotNull Consumer<Skin> consumer) {
        Skin skin = new Skin(UUID.fromString(id));
        consumer.accept(skin);
        saveEntityWithRetry(skin);
        return Optional.of(skin);
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

    protected void saveEntityWithRetry(Skin entity) {
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
                mainPlugin.getLogger().log(Level.WARNING, "Failed to save skin data for {0}, retry {1}/{2}",
                        new Object[]{entity.getId(), retryCount, maxRetries});
            }
        }

        if (!success) {
            mainPlugin.getLogger().log(Level.SEVERE, "Failed to save skin data for {0} after {1} retries",
                    new Object[]{entity.getId(), maxRetries});
        }
    }

    protected abstract boolean save0(Skin entity, boolean isShuttingDown);

    protected void postLoad(Skin entity) {
//        VoidChestAPI.getInstance().voidChestCacheController().add(entity, entity.blockLocation());
//        new VoidChestLoadEvent(entity).call();
    }

    protected void postSave(Skin entity) {
//        new VoidChestSaveEvent(entity).call();
    }

    @Override
    public void shutdown(Consumer<Skin> consumer) {
        isShuttingDown = true;
        executorService.shutdownNow();
        mainPlugin.getLogger().info("Saving all skins synchronously on shutdown...");
        List<Skin> entities = new ArrayList<>(loadedEntities.values());
        for (Skin entity : entities) {
            consumer.accept(entity);
            saveEntityWithRetry(entity);
        }
    }
}
