package com.georgev22.skinoverlay;

import co.aikar.commands.VelocityCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfile_Velocity;
import com.georgev22.skinoverlay.hook.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.velocity.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.velocity.PlayerListeners;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.VelocityPluginMessageUtils;
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
import com.velocitypowered.api.util.GameProfile;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("com.mojang:authlib:3.11.50:https://nexus.velocitypowered.com/repository/maven-public/")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
@Plugin(id = "skinoverlay", name = "${pluginName}", version = "${version}", description = "SkinOverlay", authors = {"${author}"}, dependencies = {@Dependency(id = "skinsrestorer", optional = true)})
public class SkinOverlayVelocity implements SkinOverlayImpl {

    private final ProxyServer server;
    private final Logger logger;

    private final Path dataDirectory;

    private final File dataFolder;

    private final Plugin pluginAnnotation;

    private final Metrics.Factory metricsFactory;

    private ScheduledTask scheduledTask;

    private int tick = 0;

    private boolean enabled = false;

    private static SkinOverlayVelocity instance;

    public static SkinOverlayVelocity getInstance() {
        return instance;
    }

    @Contract(pure = true)
    @Inject
    public SkinOverlayVelocity(@NotNull ProxyServer server, @NotNull Logger logger, @DataDirectory @NotNull Path dataDirectory, Metrics.Factory metricsFactory) {
        VelocityMinecraftUtils.setServer(server);
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.dataFolder = dataDirectory.toFile();
        this.pluginAnnotation = this.getClass().getAnnotation(Plugin.class);
        this.metricsFactory = metricsFactory;
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
            new LibraryLoader(this.getClass(), this.dataFolder()).loadAll(true);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
        onEnable();
    }

    public void onEnable() {
        this.server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("skinoverlay", "test"));
        this.scheduledTask = getProxy().getScheduler().buildTask(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++)).repeat(Duration.ofMillis(50L)).schedule();
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler() {
            @Override
            public void updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, Utils.@NotNull Callback<Boolean> callback) {
                try {
                    if (skinOptions.getSkinName().equalsIgnoreCase("default")) {
                        new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "reset", playerObject.playerUUID().toString(), Utilities.skinOptionsToBytes(skinOptions));
                    } else {
                        new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "change", playerObject.playerUUID().toString(), Utilities.skinOptionsToBytes(skinOptions));
                    }
                    callback.onSuccess();
                } catch (Exception exception) {
                    callback.onFailure(exception);
                }
            }

            @Override
            public void updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property, Utils.@NotNull Callback<Boolean> callback) {
                try {
                    if (skinOptions.getSkinName().equalsIgnoreCase("default")) {
                        new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "resetWithProperties", playerObject.playerUUID().toString(), Utilities.skinOptionsToBytes(skinOptions), property.name(), property.value(), property.signature());
                    } else {
                        new VelocityPluginMessageUtils().sendDataTooAllServers(getProxy(), "changeWithProperties", playerObject.playerUUID().toString(), Utilities.skinOptionsToBytes(skinOptions), property.name(), property.value(), property.signature());
                    }
                    callback.onSuccess();
                } catch (Exception exception) {
                    callback.onFailure(exception);
                }
            }

            @Override
            public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
                return ((Player) playerObject.player()).getGameProfile();
            }

            @Override
            public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
                if (sGameProfiles.containsKey(playerObject)) {
                    return sGameProfiles.get(playerObject);
                }
                return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
            }

            public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
                ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
                gameProfile.getProperties().forEach(property -> propertyObjectMap.append(property.getName(), new SProperty(property.getName(), property.getValue(), property.getSignature())));
                return new SGameProfile_Velocity(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
            }
        });
        switch (OptionsUtil.SKIN_HOOK.getStringValue()) {
            case "SkinsRestorer" -> {
                if (getProxy().getPluginManager().getPlugin("skinsrestorer").isPresent()) {
                    SkinOverlay.getInstance().setSkinHook(new SkinsRestorerHook());
                }
            }
            default -> SkinOverlay.getInstance().setSkinHook(null);
        }
        SkinOverlay.getInstance().setCommandManager(new VelocityCommandManager(getProxy(), this, dataFolder()));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        VelocityMinecraftUtils.registerListeners(this, new DeveloperInformListener(), new PlayerListeners());
        if (OptionsUtil.METRICS.getBooleanValue())
            metricsFactory.make(this, 17476);
        enabled = true;
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        scheduledTask.cancel();
        enabled = false;
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

    @Override
    public List<PlayerObject> onlinePlayers() {
        return server.getAllPlayers().stream().map(PlayerObjectVelocity::new).collect(Collectors.toList());
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
    public void print(String... msg) {
        VelocityMinecraftUtils.printMsg(msg);
    }
}