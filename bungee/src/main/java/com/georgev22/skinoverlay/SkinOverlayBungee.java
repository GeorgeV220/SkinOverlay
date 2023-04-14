package com.georgev22.skinoverlay;

import co.aikar.commands.BungeeCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.handlers.SkinHandler_BungeeCord;
import com.georgev22.skinoverlay.hook.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.bungee.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bungee.PlayerListeners;
import com.georgev22.skinoverlay.utilities.BungeeCordPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
public class SkinOverlayBungee extends Plugin implements SkinOverlayImpl {

    private int tick = 0;

    private boolean enabled = false;

    private BungeeAudiences adventure;

    private static SkinOverlayBungee skinOverlayBungee;

    public static SkinOverlayBungee getInstance() {
        return skinOverlayBungee;
    }

    @Override
    public void onLoad() {
        skinOverlayBungee = this;
        try {
            new LibraryLoader(this.getClass(), this.getDataFolder()).loadAll(true);
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
    }

    @Override
    public void onEnable() {
        this.adventure = BungeeAudiences.create(this);
        getProxy().getScheduler().schedule(this, () -> SchedulerManager.getScheduler().mainThreadHeartbeat(tick++), 0, 50L, TimeUnit.MILLISECONDS);
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler_BungeeCord());
        switch (OptionsUtil.SKIN_HOOK.getStringValue()) {
            case "SkinsRestorer" -> {
                if (getProxy().getPluginManager().getPlugin("SkinsRestorer") != null) {
                    SkinOverlay.getInstance().setSkinHook(new SkinsRestorerHook());
                }
            }
            default -> SkinOverlay.getInstance().setSkinHook(null);
        }
        SkinOverlay.getInstance().setCommandManager(new BungeeCommandManager(this));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        SkinOverlay.getInstance().setPluginMessageUtils(new BungeeCordPluginMessageUtils());
        BungeeMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        getProxy().registerChannel("skinoverlay:bungee");
        getProxy().registerChannel("skinoverlay:message");
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(this, 17475);
        enabled = true;
    }

    @Override
    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        getProxy().getScheduler().cancel(this);
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        enabled = false;
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

    @Override
    public List<PlayerObject> onlinePlayers() {
        return getProxy().getPlayers().stream().map(PlayerObjectBungee::new).collect(Collectors.toList());
    }

    @Override
    public Object plugin() {
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

    public @NotNull BungeeAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        }
        return this.adventure;
    }
}
