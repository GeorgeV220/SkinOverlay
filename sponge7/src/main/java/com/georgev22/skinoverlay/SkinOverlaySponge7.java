package com.georgev22.skinoverlay;

import co.aikar.commands.Sponge7CommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.Sponge7MinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.LoggerWrapper;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.handlers.SkinHandler_Sponge7;
import com.georgev22.skinoverlay.listeners.sponge.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.sponge.PlayerListeners;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectSponge7;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
@Plugin(id = "skinoverlay", name = "SkinOverlay", version = "${version}", authors = {"${author}"})
public class SkinOverlaySponge7 implements SkinOverlayImpl {

    private final File dataFolder;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final PluginContainer pluginContainer;
    private final SpongeAudiences adventure;
    private static SkinOverlaySponge7 skinOverlaySponge;

    private final Server server;

    private int tick = 0;

    private boolean isEnabled;

    public static SkinOverlaySponge7 getInstance() {
        return skinOverlaySponge;
    }

    @Inject
    public SkinOverlaySponge7(Logger logger, PluginContainer container, @ConfigDir(sharedRoot = false) @NotNull Path configDir, final SpongeAudiences spongeAudiences) {
        this.logger = logger;
        this.pluginContainer = container;
        this.dataFolder = new File(configDir.toUri());
        this.pluginManager = Sponge.getPluginManager();
        this.server = Sponge.getServer();
        this.adventure = spongeAudiences;
        skinOverlaySponge = this;
        onInit();
    }

    @Listener
    public void onInitialize(GameInitializationEvent event) throws ExecutionException, InterruptedException {
        onLoad();
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        onEnable();
    }

    public void onInit() {
        try {
            new LibraryLoader(this.getClass(), Sponge.getGame().getClass().getClassLoader().getParent(), this.dataFolder(), logger()).loadAll(false);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().setCommandManager(new Sponge7CommandManager(pluginContainer));
        SkinOverlay.getInstance().onLoad(this);
        SkinOverlay.getInstance().setupCommands();
        Sponge7MinecraftUtils.registerListeners(pluginContainer, new PlayerListeners(), new DeveloperInformListener());
    }


    public void onLoad() throws ExecutionException, InterruptedException {
        Sponge.getScheduler().createSyncExecutor(pluginContainer).schedule(() -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++), 50L, TimeUnit.MILLISECONDS).get();
    }

    public void onEnable() {
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Sponge7());
        SkinOverlay.getInstance().onEnable();
        this.isEnabled = true;
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        this.isEnabled = false;
    }

    @Override
    public Type type() {
        return Type.SPONGE7;
    }

    @Override
    public File dataFolder() {
        return dataFolder;
    }

    @Override
    public java.util.logging.Logger logger() {
        return new LoggerWrapper(logger);
    }

    @Override
    public Description description() {
        return new Description("${pluginName}", "${version}", this.getClass().getCanonicalName(), Lists.newArrayList("${author}"));
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
        return isEnabled;
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
        return server.getOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        return server.getOnlinePlayers().stream().map(PlayerObjectSponge7::new).collect(Collectors.toList());
    }

    @Override
    public Object plugin() {
        return pluginContainer;
    }

    @Override
    public Server serverImpl() {
        return server;
    }

    @Override
    public String serverVersion() {
        return Sponge.getPlatform().getMinecraftVersion().getName();
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public SpongeAudiences adventure() {
        return adventure;
    }
}