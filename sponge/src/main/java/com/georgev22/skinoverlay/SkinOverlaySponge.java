package com.georgev22.skinoverlay;

import co.aikar.commands.SpongeCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.SpongeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.LoggerWrapper;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.listeners.sponge.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.sponge.PlayerListeners;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@Plugin("${pluginName}")
public class SkinOverlaySponge implements SkinOverlayImpl {
    private final File dataFolder;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final PluginContainer pluginContainer;

    private Server server;
    private int tick = 0;
    private boolean isEnabled;

    @Inject
    public SkinOverlaySponge(Logger logger, PluginContainer container, @ConfigDir(sharedRoot = false) @NotNull Path configDir) {
        this.logger = logger;
        this.pluginContainer = container;
        this.dataFolder = new File(configDir.toUri());
        this.pluginManager = Sponge.pluginManager();
        onInit();
    }

    public void onInit() {
        try {
            new LibraryLoader(this.getClass(), Sponge.game().getClass().getClassLoader().getParent(), this.getDataFolder(), getLogger()).loadAll(false);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().setCommandManager(new SpongeCommandManager(pluginContainer, getDataFolder()));
        SkinOverlay.getInstance().onLoad(this);
        SpongeMinecraftUtils.registerListeners(pluginContainer,
                new PlayerListeners(),
                new DeveloperInformListener());
    }

    @Listener
    public void onStartingEngine(final @NotNull StartingEngineEvent<Server> event) {
        this.server = event.engine();
        onLoad();
    }

    @Listener
    public void onStartedEngine(final @NotNull StartedEngineEvent<Server> event) {
        onEnable();
    }

    @Listener
    public void onStoppingEngine(final @NotNull StoppingEngineEvent<Server> event) {
        onDisable();
    }

    public void onLoad() {
        server.scheduler().submit(Task.builder().plugin(pluginContainer).execute(() -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++)).interval(50L, TimeUnit.MILLISECONDS).build());
    }

    public void onEnable() {
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler.SkinHandler_());
        SkinOverlay.getInstance().onEnable();
        this.isEnabled = true;
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        this.isEnabled = false;
    }

    @Override
    public Type type() {
        return Type.SPONGE;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return new LoggerWrapper(logger);
    }

    @Override
    public Description description() {
        return new Description("${pluginName}", "${version}", this.getClass().getCanonicalName(), Lists.newArrayList("${author}"));
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
        return isEnabled;
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
        return server.isOnlineModeEnabled();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        List<PlayerObject> playerObjects = Lists.newArrayList();
        server.onlinePlayers().forEach(serverPlayer -> playerObjects.add(new PlayerObjectSponge(serverPlayer.user())));
        return playerObjects;
    }

    @Override
    public Object getPlugin() {
        return this;
    }

    @Override
    public Server getServerImpl() {
        return server;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}