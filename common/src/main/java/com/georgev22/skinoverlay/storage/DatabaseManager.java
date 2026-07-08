package com.georgev22.skinoverlay.storage;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.manager.AbstractEntityManager;
import com.georgev22.skinoverlay.storage.manager.gson.FileEntityManager;
import com.georgev22.skinoverlay.storage.manager.mongo.MongoEntityManager;
import com.georgev22.skinoverlay.storage.manager.mongo.MongoProvider;
import com.georgev22.skinoverlay.storage.manager.sql.DataSourceProvider;
import com.georgev22.skinoverlay.storage.manager.sql.SQLEntityManager;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;

public class DatabaseManager {

    private final SkinOverlay plugin;

    public DatabaseManager(SkinOverlay plugin) {
        this.plugin = plugin;
    }

    private boolean sql = false;

    public void setupDatabase() {
        String dbType = OptionsUtil.DATABASE_TYPE.getStringValue().toLowerCase();

        switch (dbType) {
            case "file" -> setupFileDatabase();
            case "mysql", "sqlite" -> setupSQLDatabase(dbType);
            case "mongodb" -> setupMongoDBDatabase();
            default -> throw new IllegalArgumentException("Unknown database type: " + dbType);
        }

        plugin.getLogger().info("Database type: " + dbType);
    }

    public void loadAllData() {
        EntityManagerRegistry entityManagerRegistry = EntityManagerRegistry.getInstance();

        // Load skins
        Optional<EntityManager<Skin>> voidEntityManager =
                entityManagerRegistry.getTyped(Skin.class);

        if (voidEntityManager.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, "Error retrieving skin entity manager!");
            return;
        }

        voidEntityManager.get().loadAll();

        // Load player data
        Optional<EntityManager<PlayerData>> playerEntityManager =
                entityManagerRegistry.getTyped(PlayerData.class);

        if (playerEntityManager.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, "Error retrieving player data entity manager!");
            return;
        }

        playerEntityManager.get().loadAll();
    }

    public void shutdown() {
        if (sql) {
            DataSourceProvider.shutdown();
        }
        EntityManagerRegistry entityManagerRegistry = EntityManagerRegistry.getInstance();
        for (EntityManager<?> entityManager : entityManagerRegistry.entries().values()) {
            entityManager.shutdown();
        }
    }

    private void setupFileDatabase() {
        EntityManagerRegistry entityManagerRegistry = EntityManagerRegistry.getInstance();
        File folder = new File(plugin.getDataFolder(), "save-data");

        if (!folder.exists() && folder.mkdirs()) {
            plugin.getLogger().info("Created " + folder.getPath() + " folder");
        }

        for (ManagedEntity<? extends Entity> managedEntity : AbstractEntityManager.getManagedEntities()) {
            if (entityManagerRegistry.get(managedEntity.type()).isEmpty()) {
                registerEntityManager(entityManagerRegistry, managedEntity,
                        new FileEntityManager<>(managedEntity, new File(folder, managedEntity.key())));
            }
        }
    }

    private void setupSQLDatabase(String type) {
        sql = true;
        switch (type) {
            case "mysql" -> DataSourceProvider.initializeForMySQL(
                    OptionsUtil.DATABASE_HOST.getStringValue(),
                    OptionsUtil.DATABASE_PORT.getIntValue(),
                    OptionsUtil.DATABASE_DATABASE.getStringValue(),
                    OptionsUtil.DATABASE_USER.getStringValue(),
                    OptionsUtil.DATABASE_PASSWORD.getStringValue());
            case "sqlite" -> DataSourceProvider.initializeForSQLite(
                    plugin.getDataFolder().getPath() + File.separator + OptionsUtil.DATABASE_FILE_NAME.getStringValue()
            );
        }

        EntityManagerRegistry entityManagerRegistry = EntityManagerRegistry.getInstance();

        for (ManagedEntity<? extends Entity> managedEntity : AbstractEntityManager.getManagedEntities()) {
            if (entityManagerRegistry.get(managedEntity.type()).isEmpty()) {
                registerEntityManager(entityManagerRegistry, managedEntity,
                        new SQLEntityManager<>(managedEntity, DataSourceProvider.getDataSource()));
            }
        }
    }

    private void setupMongoDBDatabase() {
        EntityManagerRegistry entityManagerRegistry = EntityManagerRegistry.getInstance();

        for (ManagedEntity<? extends Entity> managedEntity : AbstractEntityManager.getManagedEntities()) {
            if (entityManagerRegistry.get(managedEntity.type()).isEmpty()) {
                registerEntityManager(entityManagerRegistry, managedEntity,
                        new MongoEntityManager<>(managedEntity, MongoProvider.getDatabase()));
            }
        }
    }

    private void registerEntityManager(EntityManagerRegistry entityManagerRegistry,
                                       ManagedEntity<? extends Entity> managedEntity,
                                       EntityManager<?> entityManager) {
        try {
            entityManagerRegistry.register(managedEntity.type(), entityManager);
            plugin.getLogger().info("Registered " + managedEntity.type().getSimpleName() + " entity manager");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to register " + managedEntity.type().getSimpleName() + " entity manager", e);
        }
    }
}