package com.georgev22.skinoverlay;

import co.aikar.commands.*;
import com.georgev22.library.database.DatabaseType;
import com.georgev22.library.database.DatabaseWrapper;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObjectMap.Pair;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.maps.UnmodifiableObjectMap;
import com.georgev22.library.minecraft.scheduler.MinecraftScheduler;
import com.georgev22.library.utilities.EntityManager;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.library.yaml.serialization.ConfigurationSerialization;
import com.georgev22.skinoverlay.commands.SkinOverlayCommand;
import com.georgev22.skinoverlay.event.EventManager;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.skin.Part;
import com.georgev22.skinoverlay.handler.skin.SkinParts;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.hook.hooks.SkinHookImpl;
import com.georgev22.skinoverlay.hook.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.DebugListeners;
import com.georgev22.skinoverlay.listeners.ObservableListener;
import com.georgev22.skinoverlay.listeners.PlayerListeners;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.data.User;
import com.georgev22.skinoverlay.storage.gsonAdapters.*;
import com.georgev22.skinoverlay.storage.manager.SkinManager;
import com.georgev22.skinoverlay.storage.manager.UserManager;
import com.georgev22.skinoverlay.utilities.Locale;
import com.georgev22.skinoverlay.utilities.PluginMessageUtils;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.Updater;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinFileCache;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class SkinOverlay {

    @Getter
    @Setter
    private static SkinOverlay instance = null;

    @Getter
    private final SkinOverlayImpl skinOverlay;

    @Getter
    @Setter
    private SkinHandler skinHandler;

    @Getter
    @Setter
    private SkinHook skinHook;

    @Getter
    private final SkinHook defaultSkinHook = new SkinHookImpl();

    @Getter
    @Setter
    @ApiStatus.Internal
    private PluginMessageUtils pluginMessageUtils;

    @Getter
    private FileManager fileManager;

    @Getter
    private DatabaseWrapper databaseWrapper = null;

    @Getter
    private File skinsDataFolder;

    @Setter
    @ApiStatus.Internal
    private CommandManager<?, ?, ?, ?, ?, ?> commandManager;

    @Getter
    private UserManager userManager;

    @Getter
    private SkinManager skinManager;

    @Getter
    private EventManager eventManager;

    @Getter
    private SkinFileCache skinFileCache;

    @Setter
    @Getter
    private MinecraftScheduler<?, ?, ?, ?> minecraftScheduler;

    @Getter
    private Gson gson;

    public SkinOverlay(SkinOverlayImpl skinOverlay) {
        this.skinOverlay = skinOverlay;
        instance = this;
    }

    public void onLoad() {
        fileManager = FileManager.getInstance();
        try {
            fileManager.loadFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            MessagesUtil.repairPaths(Locale.fromString(OptionsUtil.LOCALE.getStringValue()));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error loading the language file: ", e);
        }
        eventManager = new EventManager(getLogger(), this.getClass());
        skinFileCache = new SkinFileCache();
    }

    public void onEnable() {
        ConfigurationSerialization.registerClass(User.class, "SkinOverlayUser");
        ConfigurationSerialization.registerClass(Skin.class, "SkinOverlaySkin");
        ConfigurationSerialization.registerClass(SkinParts.class, "SkinParts");
        ConfigurationSerialization.registerClass(Part.class, "SkinPart");
        ConfigurationSerialization.registerClass(SProperty.class, "SProperty");
        ConfigurationSerialization.registerClass(SerializableBufferedImage.class, "SerializableBufferedImage");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SerializableBufferedImage.class, new SerializableBufferedImageTypeAdapter())
                .registerTypeAdapter(Part.class, new PartTypeAdapter())
                .registerTypeAdapter(SkinParts.class, new SkinPartsTypeAdapter())
                .registerTypeAdapter(SProperty.class, new SPropertyTypeAdapter())
                .registerTypeAdapter(Skin.class, new SkinTypeAdapter())
                .registerTypeAdapter(User.class, new UserTypeAdapter())
                //.setPrettyPrinting()
                .create();
        switch (OptionsUtil.SKIN_HOOK.getStringValue().toLowerCase(Locale.ENGLISH_US.getLocale())) {
            case "skinsrestorer" -> {
                if (skinOverlay.isPluginEnabled(type().equals(SkinOverlayImpl.Type.VELOCITY) ? "skinsrestorer" : "SkinsRestorer")) {
                    setSkinHook(new SkinsRestorerHook());
                }
            }
            case "skinoverlay" -> setSkinHook(new SkinHookImpl());
            default -> setSkinHook(defaultSkinHook);
        }
        this.skinsDataFolder = new File(this.getDataFolder(), "skins");
        this.skinFileCache.cache();

        try {
            setupDatabase();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Cannot initialize the database: ", e.getCause());
        }

        new Updater();
        HandlerList.bakeAll();
        eventManager.register(new PlayerListeners());
        if (OptionsUtil.DEBUG.getBooleanValue()) {
            getLogger().warning("Debug mode is enabled!");
            getLogger().warning("Be prepared for a lot of log messages");
            eventManager.register(new DebugListeners());
        }
        this.setupCommands();
    }

    public void onDisable() {
        userManager.saveAll();
        skinManager.saveAll();
        if (databaseWrapper != null && databaseWrapper.isConnected()) {
            databaseWrapper.disconnect();
        }
        unregisterCommands();
        this.minecraftScheduler.cancelTasks(this.skinOverlay.plugin());
        HandlerList.unregisterAll();
    }

    /**
     * Returns the type of server implementation (Bukkit, Bungee or Velocity).
     *
     * @return The type of server implementation.
     */
    public SkinOverlayImpl.Type type() {
        return skinOverlay.type();
    }

    /**
     * Returns the plugin's data folder.
     *
     * @return The plugin's data folder.
     */
    public File getDataFolder() {
        return skinOverlay.dataFolder();
    }

    /**
     * Returns the plugin's logger.
     *
     * @return The plugin's logger.
     */
    public Logger getLogger() {
        return skinOverlay.logger();
    }

    /**
     * Returns the plugin's description.
     *
     * @return The plugin's description.
     */
    public SkinOverlayImpl.Description getDescription() {
        return skinOverlay.description();
    }

    /**
     * Enables or disables the plugin.
     *
     * @param enable true to enable, false to disable.
     * @return True if the plugin was successfully enabled or disabled, false otherwise.
     */
    public boolean setEnable(boolean enable) {
        return skinOverlay.enable(enable);
    }

    /**
     * Returns whether the plugin is currently enabled.
     *
     * @return True if the plugin is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return skinOverlay.enabled();
    }

    /**
     * Saves a resource from the plugin's JAR file to the plugin's data folder.
     *
     * @param resource The path of the resource to save.
     * @param replace  True to replace the file if it already exists, false otherwise.
     */
    public void saveResource(@NotNull String resource, boolean replace) {
        skinOverlay.saveResource(resource, replace);
    }

    /**
     * Returns whether the server is running in online mode.
     *
     * @return True if the server is running in online mode, false otherwise.
     */
    public boolean isOnlineMode() {
        return skinOverlay.onlineMode();
    }

    /**
     * Returns a boolean value indicating whether a specified plugin is enabled.
     *
     * @param pluginName the name of the plugin to check
     * @return {@code true} if the plugin is enabled, {@code false} otherwise.
     */
    public boolean isPluginEnabled(String pluginName) {
        return skinOverlay.isPluginEnabled(pluginName);
    }

    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    public <T> T getPlugin() {
        return skinOverlay.plugin();
    }

    /**
     * Returns the server implementation instance.
     *
     * @return The server implementation instance.
     */
    public Object getServerImplementation() {
        return skinOverlay.serverImpl();
    }

    /**
     * Returns the version of the server implementation.
     *
     * @return The version of the server implementation.
     */
    public String getServerVersion() {
        return skinOverlay.serverVersion();
    }

    /**
     * Prints a message to the console.
     *
     * @param msg The message(s) to print.
     */
    public void print(String... msg) {
        skinOverlay.print(msg);
    }

    /**
     * Returns a list of PlayerObjects for all online players.
     */
    public List<PlayerObject> onlinePlayers() {
        return new ArrayList<>(skinOverlay.onlinePlayers().values());
    }

    /**
     * Returns an UnmodifiableObjectMap of PlayerObject instances representing all online players on the server.
     *
     * @return An UnmodifiableObjectMap of PlayerObject instances representing all online players on the server.
     */
    public UnmodifiableObjectMap<UUID, PlayerObject> onlinePlayersMap() {
        return new UnmodifiableObjectMap<>(skinOverlay.onlinePlayers());
    }

    /**
     * Returns true if the player with the given name is currently online, and false otherwise.
     *
     * @param playerName the name of the player to check
     */
    public boolean isOnline(String playerName) {
        return onlinePlayers().stream().anyMatch(playerObject -> playerObject.playerName().equalsIgnoreCase(playerName));
    }

    /**
     * Returns true if the player with the given UUID is currently online, and false otherwise.
     *
     * @param uuid the UUID of the player to check
     */
    public boolean isOnline(UUID uuid) {
        return onlinePlayers().stream().anyMatch(playerObject -> playerObject.playerUUID().equals(uuid));
    }

    /**
     * Returns an Optional containing the PlayerObject for the player with the given name,
     * or an empty Optional if the player is not online.
     *
     * @param playerName the name of the player to get the PlayerObject for
     */
    public Optional<PlayerObject> getPlayer(String playerName) {
        return onlinePlayers().stream().filter(playerObject -> playerObject.playerName().equalsIgnoreCase(playerName)).findFirst();
    }

    /**
     * Returns an Optional containing the PlayerObject for the player with the given UUID,
     * or an empty Optional if the player is not online.
     *
     * @param uuid the UUID of the player to get the PlayerObject for
     */
    public Optional<PlayerObject> getPlayer(UUID uuid) {
        return onlinePlayers().stream().filter(playerObject -> playerObject.playerUUID().equals(uuid)).findFirst();
    }

    /**
     * Returns the configuration for this SkinOverlay.
     */
    public FileConfiguration getConfig() {
        return fileManager.getConfig().getFileConfiguration();
    }

    /**
     * Returns a list of all available overlay names.
     */
    public List<String> getOverlayList() {
        return Arrays.stream(Objects.requireNonNull(getSkinsDataFolder().listFiles())).map(File::getName).filter(file -> file.endsWith(".png")).map(file -> file.substring(0, file.length() - 4)).collect(Collectors.toList());
    }

    /**
     * Sets up a connection to the specified database type, which can be File, MySQL, SQLite, PostgreSQL or MongoDB.
     * This method throws a SQLException if there's an issue with the connection or configuration,
     * and a ClassNotFoundException
     * if the specified database driver can't be found.
     *
     * @throws SQLException           If there's an issue with the connection or configuration.
     * @throws ClassNotFoundException If the specified database driver can't be found.
     */
    private void setupDatabase() throws Exception {
        ObjectMap<String, Pair<String, String>> map = new HashObjectMap<String, Pair<String, String>>()
                .append("entity_id", Pair.create("VARCHAR(38)", "NULL"))
                .append("data", Pair.create("LONGTEXT", "NULL"));
        switch (OptionsUtil.DATABASE_TYPE.getStringValue()) {
            case "MySQL" -> {
                if (databaseWrapper == null || !databaseWrapper.isConnected()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.MYSQL,
                            OptionsUtil.DATABASE_HOST.getStringValue(),
                            OptionsUtil.DATABASE_PORT.getIntValue(),
                            OptionsUtil.DATABASE_USER.getStringValue(),
                            OptionsUtil.DATABASE_PASSWORD.getStringValue(),
                            OptionsUtil.DATABASE_DATABASE.getStringValue(),
                            getLogger());
                    sqlConnect(map);
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: MySQL");
                }
            }
            case "PostgreSQL" -> {
                if (databaseWrapper == null || !databaseWrapper.isConnected()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.POSTGRESQL,
                            OptionsUtil.DATABASE_HOST.getStringValue(),
                            OptionsUtil.DATABASE_PORT.getIntValue(),
                            OptionsUtil.DATABASE_USER.getStringValue(),
                            OptionsUtil.DATABASE_PASSWORD.getStringValue(),
                            OptionsUtil.DATABASE_DATABASE.getStringValue(),
                            getLogger());
                    sqlConnect(map);
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: PostgreSQL");
                }
            }
            case "SQLite" -> {
                if (databaseWrapper == null || !databaseWrapper.isConnected()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.SQLITE,
                            getDataFolder().getAbsolutePath(),
                            0,
                            "",
                            "",
                            OptionsUtil.DATABASE_FILE_NAME.getStringValue(),
                            getLogger());
                    sqlConnect(map);
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: SQLite");
                }
            }
            case "MongoDB" -> {
                if (databaseWrapper == null || !databaseWrapper.isConnected()) {
                    databaseWrapper = new DatabaseWrapper(DatabaseType.MONGO,
                            OptionsUtil.DATABASE_MONGO_HOST.getStringValue(),
                            OptionsUtil.DATABASE_MONGO_PORT.getIntValue(),
                            OptionsUtil.DATABASE_MONGO_USER.getStringValue(),
                            OptionsUtil.DATABASE_MONGO_PASSWORD.getStringValue(),
                            OptionsUtil.DATABASE_MONGO_DATABASE.getStringValue(),
                            getLogger());
                    databaseWrapper.connect();
                    this.userManager = new UserManager(databaseWrapper, OptionsUtil.DATABASE_MONGO_USERS_COLLECTION.getStringValue());
                    this.skinManager = new SkinManager(databaseWrapper, OptionsUtil.DATABASE_MONGO_SKINS_COLLECTION.getStringValue());
                    getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: MongoDB");
                }
            }
            default -> {
                databaseWrapper = null;
                this.userManager = new UserManager(new File(this.getDataFolder(), "userdata"), null);
                this.skinManager = new SkinManager(new File(this.getDataFolder(), "skindata"), null);
                getLogger().log(Level.INFO, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database: File");
            }
        }

        userManager.loadAll();
        skinManager.loadAll();

        onlinePlayers().forEach(player -> userManager.getEntity(player.playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            return user;
        }));

    }

    private void sqlConnect(ObjectMap<String, Pair<String, String>> map) throws SQLException, ClassNotFoundException {
        databaseWrapper.connect();
        if (databaseWrapper.getSQLDatabase() == null) {
            this.getLogger().log(Level.SEVERE, "[" + getDescription().name() + "] [" + getDescription().version() + "] Database is not connected");
            return;
        }
        databaseWrapper.getSQLDatabase().createTable(OptionsUtil.DATABASE_USERS_TABLE_NAME.getStringValue(), map);
        databaseWrapper.getSQLDatabase().createTable(OptionsUtil.DATABASE_SKINS_TABLE_NAME.getStringValue(), map);
        this.userManager = new UserManager(databaseWrapper, OptionsUtil.DATABASE_USERS_TABLE_NAME.getStringValue());
        this.skinManager = new SkinManager(databaseWrapper, OptionsUtil.DATABASE_SKINS_TABLE_NAME.getStringValue());
    }


    /**
     * Registers and sets up the commands for this SkinOverlay.
     * If the SkinOverlay is not a proxy and the 'PROXY' option is enabled, no commands will be registered.
     */
    private void setupCommands() {
        if (!type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        // Enable unstable API for deprecated 'help' command
        //noinspection deprecation
        commandManager.enableUnstableAPI("help");
        // Load command locales
        loadCommandLocales();
        // Register SkinOverlayCommand and command completions if 'COMMAND_SKINOVERLAY' option is enabled
        if (OptionsUtil.COMMAND_SKINOVERLAY.getBooleanValue()) {
            commandManager.registerCommand(new SkinOverlayCommand());
            commandManager.getCommandCompletions().registerCompletion("overlays", context -> getOverlayList());
        }
    }

    /**
     * Adds the specified list of {@link ObservableListener}s to this {@link EntityManager} instance.
     * Each listener in the
     * list will be registered with the {@link ObservableObjectMap}
     * that holds the loaded users in the user manager,
     * and
     * will be notified whenever a new user is added or removed to the map.
     *
     * @param managerListeners the list of listeners to be added
     */
    public void registerUserManagerListeners(@NotNull List<ObservableListener<UUID, User>> managerListeners) {
        for (ObservableListener<UUID, User> managerListener : managerListeners) {
            this.userManager.getLoadedEntities().addListener(managerListener);
        }
    }

    /**
     * Adds the specified list of {@link ObservableListener}s to this {@link SkinOverlayImpl} instance.
     * Each listener in the
     * list will be registered with the {@link ObservableObjectMap}
     * that holds the online players in the server,
     * and
     * will be notified whenever a new player is added or removed to the map.
     *
     * @param pListeners the list of listeners to be added
     */
    public void registerOnlinePlayersListeners(@NotNull List<ObservableListener<UUID, PlayerObject>> pListeners) {
        for (ObservableListener<UUID, PlayerObject> pListener : pListeners) {
            this.skinOverlay.onlinePlayers().addListener(pListener);
        }
    }

    /**
     * Unregisters the commands for this SkinOverlay.
     * If the SkinOverlay is not a proxy and the 'PROXY' option is enabled, no commands will be unregistered.
     */
    private void unregisterCommands() {
        if (!type().isProxy() && OptionsUtil.PROXY.getBooleanValue())
            return;
        List<RootCommand> rootCommands = new ArrayList<>(commandManager.getRegisteredRootCommands());
        rootCommands.forEach(rootCommand -> {
            switch (type()) {
                case BUKKIT -> ((PaperCommandManager) commandManager).unregisterCommand(rootCommand.getDefCommand());
                case BUNGEE -> ((BungeeCommandManager) commandManager).unregisterCommand(rootCommand.getDefCommand());
                case VELOCITY ->
                        ((VelocityCommandManager) commandManager).unregisterCommand(rootCommand.getDefCommand());
            }
        });
    }

    /**
     * Loads the command locales for this SkinOverlay.
     * If a 'lang_en.yml' language file exists in the data folder, it will be used as the default language file.
     * Otherwise, the default English language file provided by the command manager will be used.
     */
    @ApiStatus.Internal
    public void loadCommandLocales() {
        try {
            // Set the default locale to English
            commandManager.getLocales().setDefaultLocale(MessagesUtil.getLocale().getLocale());
            // Load the language file based on the server platform
            switch (type()) {
                case BUNGEE -> ((BungeeCommandManager) commandManager).getLocales()
                        .loadYamlLanguageFile(MessagesUtil.getMessagesCFG().getFile(), MessagesUtil.getLocale().getLocale());
                case BUKKIT -> ((PaperCommandManager) commandManager).getLocales()
                        .loadYamlLanguageFile(MessagesUtil.getMessagesCFG().getFile(), MessagesUtil.getLocale().getLocale());
                case VELOCITY -> ((VelocityCommandManager) commandManager).getLocales()
                        .loadYamlLanguageFile(MessagesUtil.getMessagesCFG().getFile(), MessagesUtil.getLocale().getLocale());
            }
            // Enable per-issuer locale support
            commandManager.usePerIssuerLocale(true);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load language config messages_" + OptionsUtil.LOCALE.getStringValue() + "': ", e);
        }
    }
}
