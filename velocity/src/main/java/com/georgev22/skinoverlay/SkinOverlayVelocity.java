package com.georgev22.skinoverlay;

import co.aikar.commands.VelocityCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.listeners.velocity.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.velocity.PlayerListeners;
import com.georgev22.skinoverlay.utilities.VelocityPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@Plugin(id = "skinoverlay", name = "${pluginName}", version = "${version}", description = "SkinOverlay", authors = {"${author}"})
public class SkinOverlayVelocity implements SkinOverlayImpl {

    private final ProxyServer server;
    private final Logger logger;

    private final Path dataDirectory;

    private final File dataFolder;

    private final Plugin pluginAnnotation;
    private int tick = 0;

    private boolean enabled = false;

    private static SkinOverlayVelocity instance;

    public static SkinOverlayVelocity getInstance() {
        return instance;
    }

    @Contract(pure = true)
    @Inject
    public SkinOverlayVelocity(@NotNull ProxyServer server, @NotNull Logger logger, @DataDirectory @NotNull Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.dataFolder = dataDirectory.toFile();
        this.pluginAnnotation = this.getClass().getAnnotation(Plugin.class);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        onLoad();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        onDisable();
    }

    public void onLoad() {
        try {
            new LibraryLoader(this.getClass(), this.getDataFolder()).loadAll();
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
        onEnable();
    }

    public void onEnable() {
        getProxy().getScheduler().buildTask(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++)).repeat(Duration.ofMillis(50L)).schedule();
        SkinOverlay.getInstance().setCommandManager(new VelocityCommandManager(getProxy(), this, getDataFolder()));
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler() {
            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
                if (reset) {
                    new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "reset", playerObject.playerUUID().toString(), "default");
                } else {
                    new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "change", playerObject.playerUUID().toString(), skinName);
                }
            }

            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
                if (reset) {
                    new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "resetWithProperties", playerObject.playerUUID().toString(), "default", property.getName(), property.getValue(), property.getSignature());
                } else {
                    new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "changeWithProperties", playerObject.playerUUID().toString(), skinName, property.getName(), property.getValue(), property.getSignature());
                }
            }

            @Override
            protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
                GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
                for (com.velocitypowered.api.util.GameProfile.Property property : ((Player) playerObject.getPlayer()).getGameProfile().getProperties()) {
                    gameProfile.getProperties().put(property.getName(), new Property(property.getName(), property.getValue(), property.getSignature()));
                }
                return gameProfile;
            }
        });
        SkinOverlay.getInstance().onEnable();
        //BungeeMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        getProxy().getEventManager().register(this, new DeveloperInformListener());
        getProxy().getEventManager().register(this, new PlayerListeners());
        enabled = true;
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        enabled = false;
    }

    @Override
    public Type type() {
        return Type.VELOCITY;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public Description description() {
        return new Description(pluginAnnotation.name(), pluginAnnotation.version(), this.getClass().getCanonicalName(), Arrays.stream(pluginAnnotation.authors()).toList());
    }

    @Override
    public boolean setEnable(boolean enable) {
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        try {
            Utils.saveResource(resource, replace, getDataFolder(), this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOnlineMode() {
        return server.getConfiguration().isOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        List<PlayerObject> playerObjects = Lists.newArrayList();
        for (Player player : server.getAllPlayers()) {
            playerObjects.add(new PlayerObjectVelocity(player));
        }
        return playerObjects;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getProxy() {
        return server;
    }

    @Override
    public Object getPlugin() {
        return this;
    }
}