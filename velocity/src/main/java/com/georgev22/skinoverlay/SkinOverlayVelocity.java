package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.appliers.VelocitySkinApplier;
import com.georgev22.skinoverlay.command.VelocityCommandManager;
import com.georgev22.skinoverlay.hooks.SkinHookNoop;
import com.georgev22.skinoverlay.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.message.RedisManager;
import com.georgev22.skinoverlay.message.VelocityPluginMessageManager;
import com.georgev22.skinoverlay.providers.VelocityGameProfileProvider;
import com.georgev22.skinoverlay.providers.VelocityPlayerProvider;
import com.georgev22.skinoverlay.scheduler.VelocityMinecraftScheduler;
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

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

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
        this.skinOverlay.setLogger(logger);
        this.skinOverlay.setProxy(true);
        File dataDirectoryFile = dataDirectory.toFile();
        if (!dataDirectoryFile.exists()) {
            if (dataDirectoryFile.mkdirs()) {
                logger.info("Folder " + dataDirectory + " has been created!");
            } else {
                logger.warning("Failed to create folder " + dataDirectory);
            }
        }
        this.skinOverlay.setDataFolder(dataDirectoryFile);
        this.skinOverlay.setPlugin(this);
        this.skinOverlay.setScheduler(new VelocityMinecraftScheduler<>(server));
        this.skinOverlay.setCommandManager(new VelocityCommandManager(server));
        this.skinOverlay.onLoad();

        if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("PluginMessage")) {
            this.skinOverlay.setMessageManager(new VelocityPluginMessageManager(server));
        } else {
            this.skinOverlay.setMessageManager(new RedisManager(
                    OptionsUtil.REDIS_HOST.getStringValue(),
                    OptionsUtil.REDIS_PORT.getIntValue(),
                    OptionsUtil.REDIS_PASSWORD.getStringValue()
            ));
        }
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

        this.skinOverlay.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.skinOverlay.onDisable();
    }
}
