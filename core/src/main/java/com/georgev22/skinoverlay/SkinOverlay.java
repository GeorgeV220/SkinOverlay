package com.georgev22.skinoverlay;

import co.aikar.commands.*;
import com.georgev22.library.database.DatabaseType;
import com.georgev22.library.database.DatabaseWrapper;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.commands.SkinOverlayCommand;
import com.georgev22.skinoverlay.config.FileManager;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.MessagesUtil;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.Updater;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SkinOverlay {

    private static SkinOverlay instance = null;

    @Getter
    private SkinOverlayImpl skinOverlay;

    @Getter
    @Setter
    private SkinHandler skinHandler;

    @Getter
    @Setter
    private SkinHook skinHook;

    @Getter
    private FileManager fileManager;

    @Getter
    private DatabaseWrapper databaseWrapper = null;

    private Connection connection = null;

    private MongoClient mongoClient = null;

    @Getter
    private MongoDatabase mongoDatabase = null;

    @Getter
    private File skinsDataFolder;

    @Getter
    @Setter
    private CommandManager<?, ?, ?, ?, ?, ?> commandManager;

    @Getter
    private UserManager userManager;

    public static SkinOverlay getInstance() {
        return instance == null ? (instance = new SkinOverlay()) : instance;
    }

    public void onLoad(SkinOverlayImpl skinOverlay) {
        this.skinOverlay = skinOverlay;
        fileManager = FileManager.getInstance();
        try {
            fileManager.loadFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MessagesUtil.repairPaths(fileManager.getMessages());
    }


    public void onEnable() {
        this.skinsDataFolder = new File(this.getDataFolder(), "skins");
        if (!this.skinsDataFolder.exists()) {
            if (this.skinsDataFolder.mkdirs()) {
                getLogger().log(Level.INFO, "Skins data folder was successfully created!!");
            }
            for (String resource : new String[]{"alley", "bubbo_transparent", "fire_demon", "flame", "glare", "hoodie", "migrator", "pirate", "smoking", "policeman", "mustache"}) {
                if (new File(this.skinsDataFolder, resource + ".png").exists()) continue;
                try {
                    Utils.saveResource("skins/" + resource + ".png", false, this.getDataFolder(), this.getClass());
                } catch (Exception e) {
                    this.getLogger().log(Level.WARNING, "Cannot save default skins: ", e.getCause());
                    e.printStackTrace();
                }
            }
        }

        try {
            setupDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (OptionsUtil.UPDATER.getBooleanValue()) {
            new Updater();
        }
    }

    public void onDisable() {
        userManager.getLoadedUsers().forEach((uuid, loadedUser) -> userManager.getUser(uuid).handle((user, throwable) -> {
            if (throwable != null) {
                getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            return user;
        }).thenAccept(user -> {
            if (user != null)
                userManager.save(user);
        }));
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (mongoClient != null) {
            mongoClient.close();
        }
        unregisterCommands();
        SchedulerManager.getScheduler().cancelTasks(this.getClass());
    }

    public SkinOverlayImpl.Type type() {
        return skinOverlay.type();
    }

    public File getDataFolder() {
        return skinOverlay.dataFolder();
    }

    public Logger getLogger() {
        return skinOverlay.logger();
    }

    public SkinOverlayImpl.Description getDescription() {
        return skinOverlay.description();
    }

    public boolean setEnable(boolean enable) {
        return skinOverlay.enable(enable);
    }

    public boolean isEnabled() {
        return skinOverlay.enabled();
    }

    public void saveResource(@NotNull String resource, boolean replace) {
        skinOverlay.saveResource(resource, replace);
    }

    public boolean isOnlineMode() {
        return skinOverlay.onlineMode();
    }

    public List<PlayerObject> onlinePlayers() {
        return skinOverlay.onlinePlayers();
    }

    public boolean isOnline(String playerName) {
        return onlinePlayers().stream().anyMatch(playerObject -> playerObject.playerName().equalsIgnoreCase(playerName));
    }

    public boolean isOnline(UUID uuid) {
        return onlinePlayers().stream().anyMatch(playerObject -> playerObject.playerUUID().equals(uuid));
    }

    public Optional<PlayerObject> getPlayer(String playerName) {
        return onlinePlayers().stream().filter(playerObject -> playerObject.playerName().equalsIgnoreCase(playerName)).findFirst();
    }

    public Optional<PlayerObject> getPlayer(UUID uuid) {
        return onlinePlayers().stream().filter(playerObject -> playerObject.playerUUID().equals(uuid)).findFirst();
    }

    public FileConfiguration getConfig() {
        return fileManager.getConfig().getFileConfiguration();
    }

    public List<String> getOverlayList() {
        return Arrays.stream(Objects.requireNonNull(getSkinsDataFolder().listFiles())).map(File::getName).filter(file -> file.endsWith(".png")).map(file -> file.substring(0, file.length() - 4)).collect(Collectors.toList());
    }

    /**
     * Setup database Values: File, MySQL, SQLite
     *
     * @throws java.sql.SQLException  When something goes wrong
     * @throws ClassNotFoundException When class is not found
     */
    private void setupDatabase() throws Exception {
        ObjectMap<String, ObjectMap.Pair<String, String>> map = new HashObjectMap<String, ObjectMap.Pair<String, String>>()
                .append("user_id", ObjectMap.Pair.create("VARCHAR(38)", "NULL"))
                .append("user_json", ObjectMap.Pair.create("LONGTEXT", "NULL"));
        switch (OptionsUtil.DATABASE_TYPE.getStringValue()) {
            case "MySQL" -> {
                if (connection == null || connection.isClosed()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.MYSQL,
                            OptionsUtil.DATABASE_HOST.getStringValue(),
                            OptionsUtil.DATABASE_PORT.getStringValue(),
                            OptionsUtil.DATABASE_USER.getStringValue(),
                            OptionsUtil.DATABASE_PASSWORD.getStringValue(),
                            OptionsUtil.DATABASE_DATABASE.getStringValue());
                    connection = databaseWrapper.connect().getSQLConnection();
                    databaseWrapper.getSQLDatabase().createTable(OptionsUtil.DATABASE_TABLE_NAME.getStringValue(), map);
                    this.userManager = new UserManager(UserManager.Type.SQL, connection, OptionsUtil.DATABASE_TABLE_NAME.getStringValue());
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: MySQL");
                }
            }
            case "PostgreSQL" -> {
                if (connection == null || connection.isClosed()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.POSTGRESQL,
                            OptionsUtil.DATABASE_HOST.getStringValue(),
                            OptionsUtil.DATABASE_PORT.getStringValue(),
                            OptionsUtil.DATABASE_USER.getStringValue(),
                            OptionsUtil.DATABASE_PASSWORD.getStringValue(),
                            OptionsUtil.DATABASE_DATABASE.getStringValue());
                    connection = databaseWrapper.connect().getSQLConnection();
                    databaseWrapper.getSQLDatabase().createTable(OptionsUtil.DATABASE_TABLE_NAME.getStringValue(), map);
                    this.userManager = new UserManager(UserManager.Type.SQL, connection, OptionsUtil.DATABASE_TABLE_NAME.getStringValue());
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: PostgreSQL");
                }
            }
            case "SQLite" -> {
                if (connection == null || connection.isClosed()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.SQLITE, getDataFolder().getAbsolutePath(), OptionsUtil.DATABASE_SQLITE.getStringValue());
                    connection = databaseWrapper.connect().getSQLConnection();
                    databaseWrapper.getSQLDatabase().createTable(OptionsUtil.DATABASE_TABLE_NAME.getStringValue(), map);
                    this.userManager = new UserManager(UserManager.Type.SQL, connection, OptionsUtil.DATABASE_TABLE_NAME.getStringValue());
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: SQLite");
                }
            }
            case "MongoDB" -> {
                databaseWrapper = new DatabaseWrapper(DatabaseType.MONGO,
                        OptionsUtil.DATABASE_MONGO_HOST.getStringValue(),
                        OptionsUtil.DATABASE_MONGO_PORT.getStringValue(),
                        OptionsUtil.DATABASE_MONGO_USER.getStringValue(),
                        OptionsUtil.DATABASE_MONGO_PASSWORD.getStringValue(),
                        OptionsUtil.DATABASE_MONGO_DATABASE.getStringValue())
                        .connect();
                mongoClient = databaseWrapper.getMongoClient();
                mongoDatabase = databaseWrapper.getMongoDatabase();
                this.userManager = new UserManager(UserManager.Type.SQL, databaseWrapper.getMongoDB(), OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue());
                getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: MongoDB");
            }
            default -> {
                databaseWrapper = null;
                mongoClient = null;
                mongoDatabase = null;
                this.userManager = new UserManager(UserManager.Type.JSON, new File(this.getDataFolder(), "userdata"), null);
                getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: File");
            }
        }

        //userManager.loadAll();

        onlinePlayers().forEach(player -> {
            userManager.getUser(player.playerUUID()).handle((user, throwable) -> {
                if (throwable != null) {
                    getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                    return null;
                }
                return user;
            });
        });

    }


    public void setupCommands() {
        if (OptionsUtil.PROXY.getBooleanValue())
            return;
        //noinspection deprecation
        commandManager.enableUnstableAPI("help");

        loadCommandLocales();

        if (OptionsUtil.COMMAND_SKINOVERLAY.getBooleanValue()) {
            commandManager.registerCommand(new SkinOverlayCommand());
            commandManager.getCommandCompletions().registerCompletion("overlays", context -> getOverlayList());
        }
    }

    private void unregisterCommands() {
        if (!OptionsUtil.PROXY.getBooleanValue())
            switch (type()) {
                case PAPER -> ((PaperCommandManager) commandManager).unregisterCommand(new SkinOverlayCommand());
                case BUNGEE -> ((BungeeCommandManager) commandManager).unregisterCommand(new SkinOverlayCommand());
                case VELOCITY -> ((VelocityCommandManager) commandManager).unregisterCommand(new SkinOverlayCommand());
            }
    }

    private void loadCommandLocales() {
        try {
            saveResource("lang_en.yml", true);
            commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);
            switch (type()) {
                case BUNGEE ->
                        ((BungeeCommandManager) commandManager).getLocales().loadYamlLanguageFile(new File(getDataFolder(), "lang_en.yml"), Locale.ENGLISH);
                case PAPER ->
                        ((PaperCommandManager) commandManager).getLocales().loadYamlLanguageFile(new File(getDataFolder(), "lang_en.yml"), Locale.ENGLISH);
                case VELOCITY ->
                        ((VelocityCommandManager) commandManager).getLocales().loadYamlLanguageFile(new File(getDataFolder(), "lang_en.yml"), Locale.ENGLISH);
                case SPONGE8 ->
                        ((SpongeCommandManager) commandManager).getLocales().loadYamlLanguageFile(new File(getDataFolder(), "lang_en.yml"), Locale.ENGLISH);
                case SPONGE7 -> ((Sponge7CommandManager) commandManager).getLocales().loadLanguages();
            }
            commandManager.usePerIssuerLocale(true);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load language config 'lang_en.yaml': ", e);
        }
    }
}
