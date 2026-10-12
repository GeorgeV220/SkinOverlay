package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.hooks.placeholder.PlaceholderHook;
import com.georgev22.skinoverlay.providers.PluginProvider;
import com.georgev22.skinoverlay.refreshers.SkinRefresher;
import com.georgev22.skinoverlay.command.CommandManager;
import com.georgev22.skinoverlay.command.CompletionEngine;
import com.georgev22.skinoverlay.command.commands.SkinOverlayMain;
import com.georgev22.skinoverlay.event.EventBus;
import com.georgev22.skinoverlay.event.HandlerPriority;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerLeaveEvent;
import com.georgev22.skinoverlay.listeners.PlayerListeners;
import com.georgev22.skinoverlay.message.MessageEntry;
import com.georgev22.skinoverlay.message.MessagesRegistry;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.message.messages.CoreMessages;
import com.georgev22.skinoverlay.messaging.MessageManager;
import com.georgev22.skinoverlay.providers.GameProfileProvider;
import com.georgev22.skinoverlay.providers.PlayerProvider;
import com.georgev22.skinoverlay.providers.SkinProvider;
import com.georgev22.skinoverlay.registry.RegistryManager;
import com.georgev22.skinoverlay.scheduler.MinecraftScheduler;
import com.georgev22.skinoverlay.storage.DatabaseManager;
import com.georgev22.skinoverlay.utilities.GsonUtils;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinFileCache;
import com.google.gson.Gson;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
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
     * Returns the global {@code SkinOverlay} instance.
     * <p>
     * The instance is created lazily on the first invocation and reused for the
     * lifetime of the application.
     *
     * @return the global {@code SkinOverlay} instance
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
    private PlaceholderHook placeholderHook;
    private boolean isOnlineMode;
    private boolean isProxy;
    private File dataFolder;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private ComponentLogger componentLogger;
    private PluginProvider pluginProvider;
    private RegistryManager registryManager;

    /**
     * Initializes the core components required by SkinOverlay.
     * <p>
     * This includes loading configuration files, caching skin files, registering
     * message definitions, and initializing persistent storage.
     * <p>
     * This method is intended to be called once during the platform's plugin
     * loading phase, before {@link #onEnable()}.
     *
     * @throws RuntimeException if the plugin configuration cannot be loaded
     */
    public void onLoad() {
        this.componentLogger = ComponentLogger.logger(logger.getName());
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
     * Enables SkinOverlay and registers components that require the server to be
     * fully initialized.
     * <p>
     * This loads the registries, registers the configured placeholder hook,
     * registers command completions and commands, and attaches internal event
     * listeners.
     * <p>
     * This method should be called once after {@link #onLoad()} during the
     * platform's plugin enable phase.
     */
    public void onEnable() {
        this.registryManager = new RegistryManager();
        registryManager.load();
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
        this.getEventBus().register(SkinOverlayPlayerJoinEvent.class, playerListeners::onPlayerJoin, HandlerPriority.LOWEST);
        this.getEventBus().register(SkinOverlayPlayerLeaveEvent.class, playerListeners::onPlayerLeave, HandlerPriority.LOWEST);
    }

    /**
     * Shuts down SkinOverlay and releases resources owned by the plugin.
     * <p>
     * Scheduled tasks are canceled, the active placeholder hook is unregistered,
     * and the database manager is shut down.
     * <p>
     * This method should be called once during the platform's plugin disable phase.
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
     * Returns the Gson instance used by SkinOverlay for JSON serialization
     * and deserialization.
     *
     * @return the configured Gson instance
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Returns the manager responsible for loading and accessing SkinOverlay
     * configuration files.
     *
     * @return the file manager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Returns the primary SkinOverlay configuration.
     *
     * @return the main plugin configuration
     */
    public FileConfiguration getConfig() {
        return fileManager.getConfig().getFileConfiguration();
    }

    /**
     * Returns the platform-specific data directory used by SkinOverlay.
     *
     * @return the plugin data directory
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * Sets the platform-specific data directory used by SkinOverlay.
     * <p>
     * This should be configured by the platform implementation before
     * {@link #onLoad()} is called.
     *
     * @param dataFolder the plugin data directory
     */
    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * Returns the directory containing SkinOverlay skin and overlay files.
     * <p>
     * The returned path is resolved relative to {@link #getDataFolder()} and does
     * not imply that the directory currently exists.
     *
     * @return the skins directory
     */
    public File getSkinsDataFolder() {
        return new File(getDataFolder(), "skins");
    }

    /**
     * Returns the command manager used to register and dispatch SkinOverlay
     * commands.
     *
     * @return the configured command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Sets the command manager used by SkinOverlay.
     * <p>
     * The manager must be configured before {@link #onEnable()}, as commands are
     * registered during the enable phase.
     *
     * @param commandManager the command manager to use
     */
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Returns the platform-specific plugin instance.
     * <p>
     * The requested type is not validated at runtime by SkinOverlay. Callers are
     * responsible for requesting the appropriate plugin type for the active
     * platform implementation.
     *
     * @param <T> the expected plugin type
     * @return the platform-specific plugin instance
     * @throws ClassCastException if the stored plugin is requested using an
     *                            incompatible type
     */
    public <T> T getPlugin() {
        //noinspection unchecked
        return (T) plugin;
    }

    /**
     * Sets the platform-specific plugin instance associated with SkinOverlay.
     *
     * @param plugin the platform plugin instance
     * @param <T>    the plugin type
     */
    public <T> void setPlugin(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the scheduler implementation provided by the active platform.
     * <p>
     * The generic platform types are not validated by SkinOverlay and must match
     * the scheduler implementation supplied through {@link #setScheduler(MinecraftScheduler)}.
     *
     * @param <Plugin>   the platform plugin type
     * @param <Location> the platform location type
     * @param <World>    the platform world type
     * @param <Chunk>    the platform chunk type
     * @param <E>        the scheduler-specific entity or execution type
     * @return the configured platform scheduler
     * @throws ClassCastException if incompatible generic platform types are assumed
     */
    public <Plugin, Location, World, Chunk, E> MinecraftScheduler<Plugin, Location, World, Chunk, E> getScheduler() {
        //noinspection unchecked
        return (MinecraftScheduler<Plugin, Location, World, Chunk, E>) scheduler;
    }

    /**
     * Sets the scheduler implementation used for platform-specific task execution.
     *
     * @param scheduler the platform scheduler to use
     */
    public void setScheduler(MinecraftScheduler<?, ?, ?, ?, ?> scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Returns the Adventure audience representing the server console.
     * <p>
     * This API is internal and experimental and may change without notice.
     *
     * @return the console audience
     */
    @ApiStatus.Internal
    @ApiStatus.Experimental
    public Audience getConsoleAudience() {
        return consoleAudience;
    }

    /**
     * Sets the Adventure audience representing the server console.
     * <p>
     * This is normally configured by the platform implementation and should not
     * be modified by external plugins.
     *
     * @param consoleAudience the console audience
     */
    @ApiStatus.Internal
    @ApiStatus.Experimental
    public void setConsoleAudience(Audience consoleAudience) {
        this.consoleAudience = consoleAudience;
    }

    /**
     * Returns the platform logger used by SkinOverlay.
     *
     * @return the configured logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Sets the platform logger used by SkinOverlay.
     * <p>
     * The logger should be configured before {@link #onLoad()}.
     *
     * @param logger the logger to use
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Returns the provider responsible for platform-specific player operations.
     *
     * @return the player provider
     */
    public PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    /**
     * Sets the provider responsible for platform-specific player operations.
     *
     * @param playerProvider the player provider to use
     */
    public void setPlayerProvider(PlayerProvider playerProvider) {
        this.playerProvider = playerProvider;
    }

    /**
     * Returns the provider responsible for reading and modifying player game
     * profiles.
     *
     * @return the game profile provider
     */
    public GameProfileProvider getGameProfileProvider() {
        return gameProfileProvider;
    }

    /**
     * Sets the provider responsible for platform-specific game profile operations.
     *
     * @param gameProfileProvider the game profile provider to use
     */
    public void setGameProfileProvider(GameProfileProvider gameProfileProvider) {
        this.gameProfileProvider = gameProfileProvider;
    }

    /**
     * Returns the provider responsible for retrieving and processing player skins.
     *
     * @return the skin provider
     */
    public SkinProvider getSkinProvider() {
        return skinProvider;
    }

    /**
     * Returns the platform-specific component responsible for refreshing player
     * skins.
     *
     * @return the skin refresher
     */
    public SkinRefresher getSkinRefresher() {
        return skinRefresher;
    }

    /**
     * Sets the platform-specific component used to refresh player skins.
     *
     * @param skinRefresher the skin refresher to use
     */
    public void setSkinRefresher(SkinRefresher skinRefresher) {
        this.skinRefresher = skinRefresher;
    }

    /**
     * Returns the currently configured placeholder integration, if one is
     * available.
     * <p>
     * The presence of a hook does not necessarily imply that it has been
     * successfully registered. Use {@link PlaceholderHook#isRegistered()} when
     * registration state matters.
     *
     * @return an {@link Optional} containing the configured placeholder hook,
     * or {@link Optional#empty()} if no integration is available
     */
    public Optional<PlaceholderHook> getPlaceholderHook() {
        return Optional.ofNullable(placeholderHook);
    }

    /**
     * Sets the placeholder integration used by SkinOverlay.
     * <p>
     * Platform implementations should normally configure the hook before
     * {@link #onEnable()}. Hooks present during the enable phase are automatically
     * registered by SkinOverlay.
     * <p>
     * Replacing the hook after the plugin has been enabled does not automatically
     * unregister the previous hook or register the replacement. When doing so,
     * the caller is responsible for managing the complete registration lifecycle.
     * <p>
     * This method is intended for internal platform integration and should not
     * normally be called by external plugins.
     *
     * @param placeholderHook the placeholder hook to use, or {@code null} to
     *                        disable placeholder integration
     */
    @ApiStatus.Internal
    public void setPlaceholderHook(PlaceholderHook placeholderHook) {
        this.placeholderHook = placeholderHook;
    }

    /**
     * Returns whether the server authenticates players through the official
     * Minecraft authentication services.
     *
     * @return {@code true} when the server is running in online mode;
     * {@code false} otherwise
     */
    public boolean isOnlineMode() {
        return isOnlineMode;
    }

    /**
     * Sets whether the server is operating in online mode.
     * <p>
     * This value should be supplied by the active platform implementation.
     *
     * @param isOnlineMode {@code true} for online mode; {@code false} otherwise
     */
    public void setOnlineMode(boolean isOnlineMode) {
        this.isOnlineMode = isOnlineMode;
    }

    /**
     * Returns whether SkinOverlay is running on a proxy platform rather than
     * directly on a Minecraft server.
     *
     * @return {@code true} when running on a proxy; {@code false} otherwise
     */
    public boolean isProxy() {
        return isProxy;
    }

    /**
     * Sets whether the active platform is a proxy.
     *
     * @param isProxy {@code true} when running on a proxy; {@code false} otherwise
     */
    public void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    /**
     * Returns the cache containing loaded skin and overlay configuration files.
     *
     * @return the skin file cache
     */
    public SkinFileCache getSkinFileCache() {
        return skinFileCache;
    }

    /**
     * Returns the internal event bus used to dispatch SkinOverlay events.
     *
     * @return the event bus
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

    /**
     * Returns the Adventure component logger used for component-based logging.
     *
     * @return the component logger
     */
    public ComponentLogger getComponentLogger() {
        return componentLogger;
    }

    /**
     * Returns the provider for platform-specific plugin discovery.
     *
     * @return the plugin provider
     */
    public PluginProvider getPluginProvider() {
        return pluginProvider;
    }

    /**
     * Sets the provider used for platform-specific plugin discovery.
     *
     * @param pluginProvider the plugin provider to use
     */
    public void setPluginProvider(PluginProvider pluginProvider) {
        this.pluginProvider = pluginProvider;
    }

    /**
     * Returns the registry manager responsible for initializing and managing
     * SkinOverlay registries.
     * <p>
     * The registry manager is initialized during {@link #onEnable()}.
     *
     * @return the registry manager
     */
    public RegistryManager getRegistryManager() {
        return registryManager;
    }
}
