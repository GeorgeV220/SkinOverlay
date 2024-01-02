package com.georgev22.skinoverlay;

import co.aikar.commands.VelocityCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.georgev22.library.minecraft.scheduler.VelocityMinecraftScheduler;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.handlers.SkinHandler_Velocity;
import com.georgev22.skinoverlay.listeners.velocity.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.velocity.PlayerListeners;
import com.georgev22.skinoverlay.utilities.VelocityPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectVelocity;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.platform.AudienceProvider;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary(groupId = "org.jsoup", artifactId = "jsoup", version = "1.15.3")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
@Plugin(id = "skinoverlay", name = "${pluginName}", version = "${version}", description = "SkinOverlay", authors = {"${author}"}, dependencies = {@Dependency(id = "skinsrestorer", optional = true)})
@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinOverlayVelocity implements SkinOverlayImpl {

    private final ProxyServer server;
    private final Logger logger;

    private final Path dataDirectory;

    private final File dataFolder;

    private final Plugin pluginAnnotation;

    private final Metrics.Factory metricsFactory;

    private final SkinOverlay skinOverlay;

    private LibraryLoader libraryLoader;

    private int tick = 0;

    private boolean enabled = false;

    private static SkinOverlayVelocity instance;

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    public static SkinOverlayVelocity getInstance() {
        return instance;
    }

    @Contract(pure = true)
    @Inject
    public SkinOverlayVelocity(@NotNull ProxyServer server, @NotNull Logger logger, @DataDirectory @NotNull Path dataDirectory, Metrics.Factory metricsFactory) {
        VelocityMinecraftUtils.setServer(server);
        this.skinOverlay = new SkinOverlay(this);
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.dataFolder = dataDirectory.toFile();
        this.pluginAnnotation = this.getClass().getAnnotation(Plugin.class);
        this.metricsFactory = metricsFactory;
        this.server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("skinoverlay", "messagechannel"));
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
            this.libraryLoader = new LibraryLoader(this.getClass().getClassLoader(), this.dataFolder());
            this.libraryLoader.loadAll(this, true);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        skinOverlay.onLoad();
        onEnable();
    }

    public void onEnable() {
        this.server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("skinoverlay", "test"));
        this.skinOverlay.setMinecraftScheduler(new VelocityMinecraftScheduler<>());
        skinOverlay.setSkinHandler(new SkinHandler_Velocity());
        skinOverlay.setCommandManager(new VelocityCommandManager(getProxy(), this, dataFolder()));
        skinOverlay.onEnable();
        skinOverlay.setPluginMessageUtils(new VelocityPluginMessageUtils());
        this.server.getChannelRegistrar().register(MinecraftChannelIdentifier.from("skinoverlay:message"));
        VelocityMinecraftUtils.registerListeners(this, new DeveloperInformListener(), new PlayerListeners());
        if (OptionsUtil.METRICS.getBooleanValue())
            metricsFactory.make(this, 17476);
        enabled = true;
    }

    public void onDisable() {
        skinOverlay.onDisable();
        enabled = false;
        try {
            this.libraryLoader.unloadAll();
        } catch (InvalidDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Type type() {
        return Type.VELOCITY;
    }

    public File dataFolder() {
        return dataFolder;
    }

    public Logger logger() {
        return logger;
    }

    @Override
    public Description description() {
        return new Description(pluginAnnotation.name(), pluginAnnotation.version(), this.getClass().getCanonicalName(), Arrays.stream(pluginAnnotation.authors()).toList());
    }

    @Override
    public boolean enable(boolean enable) {
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        return enabled();
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        try {
            Utils.saveResource(resource, replace, dataFolder(), this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onlineMode() {
        return server.getConfiguration().isOnlineMode();
    }

    private final ObservableObjectMap<UUID, PlayerObject> players = new ObservableObjectMap<>();

    @Override
    public ObservableObjectMap<UUID, PlayerObject> onlinePlayers() {
        for (Player player : server.getAllPlayers()) {
            if (players.containsKey(player.getUniqueId())) {
                continue;
            }
            players.append(player.getUniqueId(), new PlayerObjectVelocity(player));
        }
        return players;
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return getProxy().getPluginManager().getPlugin(pluginName).isPresent();
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getProxy() {
        return server;
    }

    @Override
    public Object plugin() {
        return this;
    }

    @Override
    public ProxyServer serverImpl() {
        return server;
    }

    @Override
    public String serverVersion() {
        return SkinOverlayVelocity.getInstance().getProxy().getVersion().getName() + "-" + SkinOverlayVelocity.getInstance().getProxy().getVersion().getVersion();
    }

    @Override
    public AudienceProvider adventure() {
        return null;
    }
}