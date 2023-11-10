package com.georgev22.skinoverlay;

import co.aikar.commands.BungeeCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.handlers.SkinHandler_BungeeCord;
import com.georgev22.skinoverlay.listeners.bungee.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bungee.PlayerListeners;
import com.georgev22.skinoverlay.utilities.BungeeCordPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinOverlayBungee extends Plugin implements SkinOverlayImpl {

    private int tick = 0;
    private boolean enabled = false;
    private SkinOverlay skinOverlay;

    private BungeeAudiences adventure;

    private LibraryLoader libraryLoader;

    private static SkinOverlayBungee skinOverlayBungee;

    public static SkinOverlayBungee getInstance() {
        return skinOverlayBungee;
    }

    @Override
    public void onLoad() {
        this.skinOverlay = new SkinOverlay(this);
        skinOverlayBungee = this;
        try {
            this.libraryLoader = new LibraryLoader(this.getClass().getClassLoader(), this.getDataFolder());
            this.libraryLoader.loadAll(this, true);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        skinOverlay.onLoad();
    }

    @Override
    public void onEnable() {
        this.adventure = BungeeAudiences.create(this);
        getProxy().getScheduler().schedule(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++), 0, 50L, TimeUnit.MILLISECONDS);
        skinOverlay.setSkinHandler(new SkinHandler_BungeeCord());
        skinOverlay.setCommandManager(new BungeeCommandManager(this));
        skinOverlay.onEnable();
        skinOverlay.setPluginMessageUtils(new BungeeCordPluginMessageUtils());
        BungeeMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        getProxy().registerChannel("skinoverlay:bungee");
        getProxy().registerChannel("skinoverlay:message");
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(this, 17475);
        enabled = true;
    }

    @Override
    public void onDisable() {
        skinOverlay.onDisable();
        getProxy().getScheduler().cancel(this);
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        enabled = false;
        try {
            this.libraryLoader.unloadAll();
        } catch (InvalidDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Type type() {
        return Type.BUNGEE;
    }

    @Override
    public File dataFolder() {
        return getDataFolder();
    }

    @Override
    public Logger logger() {
        return getLogger();
    }

    @Override
    public Description description() {
        return new Description(getDescription().getName(), getDescription().getVersion(), getDescription().getMain(), Collections.singletonList(getDescription().getAuthor()));
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
            Utils.saveResource(resource, replace, getDataFolder(), this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onlineMode() {
        return getProxy().getConfig().isOnlineMode();
    }

    private final ObservableObjectMap<UUID, PlayerObject> players = new ObservableObjectMap<>();

    @Override
    public ObservableObjectMap<UUID, PlayerObject> onlinePlayers() {
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            if (players.containsKey(player.getUniqueId())) {
                continue;
            }
            players.append(player.getUniqueId(), new PlayerObjectBungee(player));
        }
        return players;
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return getProxy().getPluginManager().getPlugin(pluginName) != null;
    }

    @Override
    public Plugin plugin() {
        return this;
    }

    @Override
    public ProxyServer serverImpl() {
        return getProxy();
    }

    @Override
    public String serverVersion() {
        return getProxy().getVersion();
    }

    @Override
    public @NotNull BungeeAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        }
        return this.adventure;
    }
}
