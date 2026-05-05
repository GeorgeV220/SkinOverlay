package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.appliers.VelocitySkinApplier;
import com.georgev22.skinoverlay.command.VelocityCommandManager;
import com.georgev22.skinoverlay.hooks.SkinHookNoop;
import com.georgev22.skinoverlay.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.velocity.PlayerListeners;
import com.georgev22.skinoverlay.messaging.MessageManagerNoop;
import com.georgev22.skinoverlay.messaging.RedisManager;
import com.georgev22.skinoverlay.messaging.VelocityPluginMessageManager;
import com.georgev22.skinoverlay.providers.VelocityGameProfileProvider;
import com.georgev22.skinoverlay.providers.VelocityPlayerProvider;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.scheduler.VelocityMinecraftScheduler;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.LoggerWrapper;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

import static com.georgev22.skinoverlay.messaging.VelocityPluginMessageManager.inChannelIdentifier;

@Plugin(
        id = BuildParameters.PLUGIN_ID,
        name = BuildParameters.PLUGIN_NAME,
        version = BuildParameters.VERSION,
        description = BuildParameters.DESCRIPTION,
        authors = BuildParameters.AUTHOR,
        url = BuildParameters.URL,
        dependencies = {
                @Dependency(id = "skinsrestorer", optional = true)
        }
)
public class SkinOverlayVelocity {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final ProxyServer server;

    @Inject
    public SkinOverlayVelocity(ProxyServer server, Logger logger, @DataDirectory @NotNull Path dataDirectory) {
        this.server = server;
        this.skinOverlay.setLogger(new LoggerWrapper(logger));
        this.skinOverlay.setProxy(true);
        File dataDirectoryFile = dataDirectory.toFile();
        if (!dataDirectoryFile.exists()) {
            if (dataDirectoryFile.mkdirs()) {
                this.skinOverlay.getLogger().info("Folder " + dataDirectory + " has been created!");
            } else {
                this.skinOverlay.getLogger().warning("Failed to create folder " + dataDirectory);
            }
        }
        this.skinOverlay.setDataFolder(dataDirectoryFile);
        this.skinOverlay.setPlugin(this);
        this.skinOverlay.setScheduler(new VelocityMinecraftScheduler<>(server));
        this.skinOverlay.setCommandManager(new VelocityCommandManager(server));
        this.skinOverlay.onLoad();
        if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("PluginMessage"))
            this.server.getChannelRegistrar().register(inChannelIdentifier);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (server.getPluginManager().getPlugin("skinsrestorer").isPresent()) {
            this.skinOverlay.setSkinHook(new SkinsRestorerHook());
        } else {
            this.skinOverlay.setSkinHook(new SkinHookNoop());
        }
        this.skinOverlay.setSkinApplier(new VelocitySkinApplier());
        this.skinOverlay.setGameProfileProvider(new VelocityGameProfileProvider());
        this.skinOverlay.setPlayerProvider(new VelocityPlayerProvider(server));
        this.skinOverlay.setAudienceProvider(new VelocityAudienceProvider(this, server));
        this.skinOverlay.setOnlineMode(server.getConfiguration().isOnlineMode());
        if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("PluginMessage")) {
            VelocityPluginMessageManager velocityPluginMessageManager = new VelocityPluginMessageManager(server);
            this.skinOverlay.setMessageManager(velocityPluginMessageManager);
            this.server.getEventManager().register(this, velocityPluginMessageManager);
            this.skinOverlay.getLogger().info("Plugin message connection type: " + OptionsUtil.CONNECTION_TYPE.getStringValue());
        } else if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("Redis")) {
            this.skinOverlay.setMessageManager(new RedisManager(
                    OptionsUtil.REDIS_HOST.getStringValue(),
                    OptionsUtil.REDIS_PORT.getIntValue(),
                    OptionsUtil.REDIS_PASSWORD.getStringValue()
            ));
        } else {
            this.skinOverlay.setMessageManager(new MessageManagerNoop());
        }
        this.skinOverlay.getMessageManager().subscribePlayerJoin(uuid -> {
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                this.skinOverlay.getLogger().info("Player " + uuid + " has joined the server.");
            }
            this.skinOverlay.getScheduler().runAsyncTask(this.skinOverlay.getPlugin(), () -> {
                EntityManagerRegistry.getInstance().getTyped(PlayerData.class)
                        .flatMap(entityManager -> entityManager.findById(uuid))
                        .ifPresent(playerData -> {
                            Skin skin = playerData.getCurrentSkin();
                            if (skin != null) {
                                this.skinOverlay.getSkinApplier().setSkin(
                                        this.skinOverlay.getPlayerProvider().getSPlayer(uuid),
                                        skin
                                );
                            }
                        });
            });
        });

        this.server.getEventManager().register(this, new PlayerListeners());
        this.skinOverlay.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.skinOverlay.onDisable();
    }
}
