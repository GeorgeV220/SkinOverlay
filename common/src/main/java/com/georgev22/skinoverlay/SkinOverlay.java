package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.hooks.placeholder.PlaceholderHook;
import com.georgev22.skinoverlay.refreshers.SkinRefresher;
import com.georgev22.skinoverlay.command.CommandManager;
import com.georgev22.skinoverlay.command.CompletionEngine;
import com.georgev22.skinoverlay.command.commands.SkinOverlayMain;
import com.georgev22.skinoverlay.event.EventBus;
import com.georgev22.skinoverlay.event.HandlerPriority;
import com.georgev22.skinoverlay.event.events.player.SPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SPlayerLeaveEvent;
import com.georgev22.skinoverlay.hooks.SkinHook;
import com.georgev22.skinoverlay.listeners.PlayerListeners;
import com.georgev22.skinoverlay.message.MessageEntry;
import com.georgev22.skinoverlay.message.MessagesRegistry;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.message.messages.CoreMessages;
import com.georgev22.skinoverlay.messaging.MessageManager;
import com.georgev22.skinoverlay.providers.GameProfileProvider;
import com.georgev22.skinoverlay.providers.PlayerProvider;
import com.georgev22.skinoverlay.providers.SkinProvider;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.scheduler.MinecraftScheduler;
import com.georgev22.skinoverlay.storage.DatabaseManager;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.ManagedEntity;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.utilities.GsonUtils;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinFileCache;
import com.google.gson.Gson;
import net.kyori.adventure.audience.Audience;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for managing the core components of SkinOverlay.
 */
public class SkinOverlay {

    private static SkinOverlay instance;

    /**
     * Gets the singleton instance of SkinOverlay.
     *
     * @return the SkinOverlay instance
     */
    public static SkinOverlay getInstance() {
        return instance == null ? instance = new SkinOverlay() : instance;
    }

    private EventBus eventBus;
    private FileManager fileManager;
    private SkinFileCache skinFileCache;
    private Gson gson;
    private CommandManager commandManager;
    private Object plugin;
    private MinecraftScheduler<?, ?, ?, ?, ?> scheduler;
    private Audience consoleAudience;
    private Logger logger;
    private PlayerProvider playerProvider;
    private GameProfileProvider gameProfileProvider;
    private SkinProvider skinProvider;
    private SkinRefresher skinRefresher;
    private SkinHook skinHook;
    private PlaceholderHook placeholderHook;
    private boolean isOnlineMode;
    private boolean isProxy;
    private File dataFolder;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;

    /**
     * Loads the plugin components. Should be called during plugin load phase.
     */
    public void onLoad() {
        this.eventBus = new EventBus();
        this.gson = GsonUtils.getGson(true);
        this.fileManager = FileManager.getInstance();
        try {
            this.fileManager.loadFiles();
            OptionsUtil.reloadAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.skinFileCache = new SkinFileCache();
        this.skinFileCache.cache();
        this.skinProvider = new SkinProvider();
        try {
            MessagesRegistry.registerAll(new MessageEntry[][]{
                    CommandMessages.values(),
                    CoreMessages.values()
            });
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error loading the language file: ", e);
        }
        this.databaseManager = new DatabaseManager(this);
        databaseManager.setupDatabase();
        databaseManager.loadAllData();
    }

    /**
     * Enables the plugin components. Should be called during plugin enable phase.
     */
    public void onEnable() {
        if (placeholderHook != null) {
            try {
                if (placeholderHook.register()) {
                    getLogger().info("Placeholder hook registered successfully!");
                } else {
                    getLogger().warning("Placeholder hook registration failed!");
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to register placeholder hook", e);
            }
        } else {
            getLogger().info("Placeholder hook is not set!");
        }
        CompletionEngine.registerResolver("@overlays", (commandIssuer, args) ->
                skinFileCache.getSkinConfigurationFiles().keySet());
        this.commandManager.registerCommand(new SkinOverlayMain());
        getLogger().info("Commands registered successfully!");

        // Register event listeners
        PlayerListeners playerListeners = new PlayerListeners();
        this.getEventBus().register(SPlayerJoinEvent.class, playerListeners::onPlayerJoin, HandlerPriority.LOWEST);
        this.getEventBus().register(SPlayerLeaveEvent.class, playerListeners::onPlayerLeave, HandlerPriority.LOWEST);
    }

    /**
     * Disables the plugin components. Should be called during plugin disable phase.
     */
    public void onDisable() {
        if (this.scheduler != null) {
            this.scheduler.cancelTasks(getPlugin());
        }

        if (placeholderHook != null) {
            try {
                if (placeholderHook.unregister()) {
                    getLogger().info("Placeholder hook unregistered successfully!");
                } else {
                    getLogger().warning("Placeholder hook unregistration failed!");
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to unregister placeholder hook", e);
            }
        }

        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    /**
     * Gets the Gson instance used for JSON operations.
     *
     * @return the Gson instance
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Gets the FileManager instance.
     *
     * @return the FileManager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Gets the configuration FileConfiguration.
     *
     * @return the FileConfiguration
     */
    public FileConfiguration getConfig() {
        return fileManager.getConfig().getFileConfiguration();
    }

    /**
     * Gets the plugin data folder.
     *
     * @return the data folder
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * Sets the plugin data folder.
     *
     * @param dataFolder the data folder
     */
    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * Gets the skins data folder.
     *
     * @return the skins data folder
     */
    public File getSkinsDataFolder() {
        return new File(getDataFolder(), "skins");
    }

    /**
     * Gets the CommandManager instance.
     *
     * @return the CommandManager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Sets the CommandManager instance.
     *
     * @param commandManager the CommandManager
     */
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Gets the plugin instance.
     *
     * @param <T> plugin type
     * @return the plugin instance
     */
    public <T> T getPlugin() {
        //noinspection unchecked
        return (T) plugin;
    }

    /**
     * Sets the plugin instance.
     *
     * @param plugin the plugin instance
     * @param <T>    plugin type
     */
    public <T> void setPlugin(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the MinecraftScheduler instance.
     *
     * @return the MinecraftScheduler
     */
    public <Plugin, Location, World, Chunk, E> MinecraftScheduler<Plugin, Location, World, Chunk, E> getScheduler() {
        //noinspection unchecked
        return (MinecraftScheduler<Plugin, Location, World, Chunk, E>) scheduler;
    }

    /**
     * Sets the MinecraftScheduler instance.
     *
     * @param scheduler the MinecraftScheduler
     */
    public void setScheduler(MinecraftScheduler<?, ?, ?, ?, ?> scheduler) {
        this.scheduler = scheduler;
    }

    @ApiStatus.Internal
    @ApiStatus.Experimental
    public Audience getConsoleAudience() {
        return consoleAudience;
    }

    @ApiStatus.Internal
    @ApiStatus.Experimental
    public void setConsoleAudience(Audience consoleAudience) {
        this.consoleAudience = consoleAudience;
    }

    /**
     * Gets the logger instance.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Sets the logger instance.
     *
     * @param logger the logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Gets the PlayerProvider instance.
     *
     * @return the PlayerProvider
     */
    public PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    /**
     * Sets the PlayerProvider instance.
     *
     * @param playerProvider the PlayerProvider
     */
    public void setPlayerProvider(PlayerProvider playerProvider) {
        this.playerProvider = playerProvider;
    }

    /**
     * Gets the GameProfileProvider instance.
     *
     * @return the GameProfileProvider
     */
    public GameProfileProvider getGameProfileProvider() {
        return gameProfileProvider;
    }

    /**
     * Sets the GameProfileProvider instance.
     *
     * @param gameProfileProvider the GameProfileProvider
     */
    public void setGameProfileProvider(GameProfileProvider gameProfileProvider) {
        this.gameProfileProvider = gameProfileProvider;
    }

    /**
     * Gets the SkinProvider instance.
     *
     * @return the SkinProvider
     */
    public SkinProvider getSkinProvider() {
        return skinProvider;
    }

    /**
     * Gets the SkinRefresher instance.
     *
     * @return the SkinRefresher
     */
    public SkinRefresher getSkinRefresher() {
        return skinRefresher;
    }

    /**
     * Sets the SkinRefresher instance.
     *
     * @param skinRefresher the SkinRefresher
     */
    public void setSkinRefresher(SkinRefresher skinRefresher) {
        this.skinRefresher = skinRefresher;
    }

    /**
     * Gets the SkinHook instance.
     *
     * @return the SkinHook
     */
    public SkinHook getSkinHook() {
        return skinHook;
    }

    /**
     * Sets the SkinHook instance.
     *
     * @param skinHook the SkinHook
     */
    public void setSkinHook(SkinHook skinHook) {
        this.skinHook = skinHook;
    }

    /**
     * Gets the PlaceholderHook instance.
     *
     * @return the PlaceholderHook
     */
    public Optional<PlaceholderHook> getPlaceholderHook() {
        return Optional.ofNullable(placeholderHook);
    }

    /**
     * Sets the active {@link PlaceholderHook} implementation.
     * <p>
     * This method is intended for internal use only. The plugin automatically
     * manages placeholder hook initialization and registration during its
     * lifecycle.
     * <p>
     * Calling this method manually is strongly discouraged unless you fully
     * understand the placeholder hook lifecycle. In particular, registration
     * should be handled manually by the caller, as the plugin's normal
     * registration process occurs during {@code onEnable()}. Replacing the
     * hook after or during this process without properly registering it may
     * result in errors or undefined behavior.
     * <p>
     * If a custom hook is supplied, it is the caller's responsibility to ensure
     * that the hook is correctly initialized and that
     * {@link PlaceholderHook#isRegistered()} returns {@code true} before the
     * hook is used.
     * <p>
     * This method should only be called after the plugin has completed its
     * enable phase.
     *
     * @param placeholderHook the placeholder hook to use
     */
    @ApiStatus.Internal
    public void setPlaceholderHook(PlaceholderHook placeholderHook) {
        this.placeholderHook = placeholderHook;
    }

    /**
     * Checks if the server is in online mode.
     *
     * @return true if online mode, false otherwise
     */
    public boolean isOnlineMode() {
        return isOnlineMode;
    }

    /**
     * Sets the server online mode.
     *
     * @param isOnlineMode true for online mode, false otherwise
     */
    public void setOnlineMode(boolean isOnlineMode) {
        this.isOnlineMode = isOnlineMode;
    }

    /**
     * Checks if the server is running as a proxy.
     *
     * @return true if proxy, false otherwise
     */
    public boolean isProxy() {
        return isProxy;
    }

    /**
     * Sets whether the server is running as a proxy.
     *
     * @param isProxy true for proxy, false otherwise
     */
    public void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    /**
     * Gets the SkinFileCache instance.
     *
     * @return the SkinFileCache
     */
    public SkinFileCache getSkinFileCache() {
        return skinFileCache;
    }

    /**
     * Gets the EventBus instance.
     *
     * @return the EventBus
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Sets the MessageManager instance.
     *
     * @param messageManager the MessageManager
     */
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * Gets the MessageManager instance.
     *
     * @return the MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    private void registerEntityManager(EntityManagerRegistry entityManagerRegistry,
                                       ManagedEntity<? extends Entity> managedEntity,
                                       EntityManager<?> entityManager) {
        try {
            entityManagerRegistry.register(managedEntity.type(), entityManager);
            this.getLogger().info(
                    "Registered " + managedEntity.type().getSimpleName() + " entity manager");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE,
                    "Failed to register " + managedEntity.type().getSimpleName() + " entity manager", e);
        }
    }
}
