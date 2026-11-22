package com.georgev22.skinoverlay.fabric;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerLeaveEvent;
import com.georgev22.skinoverlay.fabric.command.FabricCommandManager;
import com.georgev22.skinoverlay.fabric.messaging.FabricPluginMessageManager;
import com.georgev22.skinoverlay.fabric.providers.FabricGameProfileProvider;
import com.georgev22.skinoverlay.fabric.providers.FabricPlayerProvider;
import com.georgev22.skinoverlay.fabric.refreshers.FabricSkinRefresher;
import com.georgev22.skinoverlay.fabric.scheduler.FabricMinecraftScheduler;
import com.georgev22.skinoverlay.fabric.utilities.FabricAdventure;
import com.georgev22.skinoverlay.messaging.MessageManagerNoop;
import com.georgev22.skinoverlay.messaging.RedisManager;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.utilities.LoggerWrapper;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.io.File;
import java.util.logging.Level;

final class FabricSkinOverlayRuntime {

    private final FabricSkinOverlay plugin;
    private final File dataFolder;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final FabricCommandManager commandManager = new FabricCommandManager();
    private final Logger logger;
    private MinecraftServer server;
    private FabricMinecraftScheduler scheduler;
    private boolean enabled;

    FabricSkinOverlayRuntime(FabricSkinOverlay plugin, File dataFolder, Logger logger) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    void initialize() {
        skinOverlay.setPlugin(plugin);
        skinOverlay.setLogger(new LoggerWrapper(logger));
        skinOverlay.setDataFolder(dataFolder);
        skinOverlay.setCommandManager(commandManager);
        skinOverlay.setGameProfileProvider(new FabricGameProfileProvider());
        skinOverlay.setSkinRefresher(new FabricSkinRefresher());
        skinOverlay.setProxy(false);
        skinOverlay.setPluginProvider(_ -> false);

        try {
            skinOverlay.onLoad();
        } catch (RuntimeException exception) {
            skinOverlay.getLogger().log(Level.SEVERE, "Failed to load SkinOverlay on Fabric", exception);
            throw exception;
        }

        FabricPluginMessageManager.registerPayloads();
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerPlayerEvents.JOIN.register(player -> {
            SkinOverlayPlayer sPlayer = skinOverlay.getPlayerProvider().getSkinOverlayPlayer(player);
            if (sPlayer != null) {
                skinOverlay.getEventBus().post(new SkinOverlayPlayerJoinEvent(sPlayer));
            }
        });
        ServerPlayerEvents.LEAVE.register(player -> {
            SkinOverlayPlayer sPlayer = skinOverlay.getPlayerProvider().getSkinOverlayPlayer(player);
            if (sPlayer != null) {
                skinOverlay.getEventBus().post(new SkinOverlayPlayerLeaveEvent(sPlayer));
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            commandManager.bind(dispatcher);
        });
    }

    private void onServerStarting(MinecraftServer server) {
        FabricAdventure.init(server);
        this.server = server;
        this.scheduler = new FabricMinecraftScheduler(server);
        skinOverlay.setScheduler(scheduler);
        skinOverlay.setPlayerProvider(new FabricPlayerProvider(server));
        skinOverlay.setOnlineMode(server.usesAuthentication());
        if (OptionsUtil.PROXY.getBooleanValue()) {
            if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("PluginMessage")) {
                this.skinOverlay.setMessageManager(new FabricPluginMessageManager(server));
            } else if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("Redis")) {
                this.skinOverlay.setMessageManager(new RedisManager(
                        OptionsUtil.REDIS_HOST.getStringValue(),
                        OptionsUtil.REDIS_PORT.getIntValue(),
                        OptionsUtil.REDIS_PASSWORD.getStringValue()
                ));
            } else {
                this.skinOverlay.setMessageManager(new MessageManagerNoop());
            }
            skinOverlay.getMessageManager().subscribeSkinProperty((uuid, skin) -> {
                SkinOverlayPlayer player = this.skinOverlay.getPlayerProvider().getSkinOverlayPlayer(uuid);
                if (player != null) {
                    skinOverlay.getSkinProvider().setSkin(player, skin);
                }
            });
        }
        if (!enabled) {
            skinOverlay.onEnable();
            enabled = true;
        }
    }

    private void onServerStopping(MinecraftServer server) {
        if (this.server != server) {
            return;
        }

        if (enabled) {
            skinOverlay.onDisable();
            enabled = false;
        }
        if (skinOverlay.getMessageManager() != null) {
            skinOverlay.getMessageManager().close();
        }
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        commandManager.unbind();
        this.server = null;
        FabricAdventure.close();
    }
}
