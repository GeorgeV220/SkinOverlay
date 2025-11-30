package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.appliers.SkinApplier;
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
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.storage.gson.*;
import com.georgev22.skinoverlay.storage.manager.gson.PlayerFileManager;
import com.georgev22.skinoverlay.storage.manager.gson.SkinFileManager;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.utilities.config.SkinFileCache;
import com.georgev22.skinoverlay.utilities.skin.Part;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.platform.AudienceProvider;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;

import java.io.File;
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
    private AudienceProvider audienceProvider;
    private Logger logger;
    private PlayerProvider playerProvider;
    private GameProfileProvider gameProfileProvider;
    private SkinProvider skinProvider;
    private SkinApplier skinApplier;
    private SkinHook skinHook;
    private boolean isOnlineMode;
    private boolean isProxy;
    private File dataFolder;
    private MessageManager messageManager;

    /**
     * Loads the plugin components. Should be called during plugin load phase.
     */
    public void onLoad() {
        this.eventBus = new EventBus();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SerializableBufferedImage.class, new SerializableBufferedImageTypeAdapter())
                .registerTypeAdapter(Part.class, new PartTypeAdapter())
                .registerTypeAdapter(SkinParts.class, new SkinPartsTypeAdapter())
                .registerTypeAdapter(SProperty.class, new SPropertyTypeAdapter())
                .registerTypeAdapter(Skin.class, new SkinTypeAdapter())
                .registerTypeAdapter(PlayerData.class, new PlayerDataTypeAdapter())
                .setPrettyPrinting()
                .create();
        this.fileManager = FileManager.getInstance();
        try {
            this.fileManager.loadFiles();
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
        PlayerFileManager playerFileManager = new PlayerFileManager(new File(getDataFolder(), "playerdata"));
        EntityManagerRegistry.registerManager(PlayerData.class, playerFileManager);

        SkinFileManager skinFileManager = new SkinFileManager(new File(getDataFolder(), "skindata"));
        EntityManagerRegistry.registerManager(Skin.class, skinFileManager);

        playerFileManager.loadAll();
        skinFileManager.loadAll();
    }

    /**
     * Enables the plugin components. Should be called during plugin enable phase.
     */
    public void onEnable() {
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
        if (this.audienceProvider != null) {
            this.audienceProvider.close();
            audienceProvider = null;
        }

        EntityManagerRegistry.getManager(PlayerData.class).ifPresent(EntityManager::saveAll);
        EntityManagerRegistry.getManager(Skin.class).ifPresent(EntityManager::saveAll);
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
    public <Plugin, Location, World, Chunk, Entity> MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler() {
        //noinspection unchecked
        return (MinecraftScheduler<Plugin, Location, World, Chunk, Entity>) scheduler;
    }

    /**
     * Sets the MinecraftScheduler instance.
     *
     * @param scheduler the MinecraftScheduler
     */
    public void setScheduler(MinecraftScheduler<?, ?, ?, ?, ?> scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Gets the AudienceProvider instance.
     *
     * @return the AudienceProvider
     */
    public AudienceProvider getAudienceProvider() {
        return audienceProvider;
    }

    /**
     * Sets the AudienceProvider instance.
     *
     * @param audienceProvider the AudienceProvider
     */
    public void setAudienceProvider(AudienceProvider audienceProvider) {
        this.audienceProvider = audienceProvider;
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
     * Gets the SkinApplier instance.
     *
     * @return the SkinApplier
     */
    public SkinApplier getSkinApplier() {
        return skinApplier;
    }

    /**
     * Sets the SkinApplier instance.
     *
     * @param skinApplier the SkinApplier
     */
    public void setSkinApplier(SkinApplier skinApplier) {
        this.skinApplier = skinApplier;
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
}
