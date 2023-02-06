package com.georgev22.skinoverlay;

import co.aikar.commands.PaperCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.SkinHandler.SkinHandler_;
import com.georgev22.skinoverlay.handler.handlers.*;
import com.georgev22.skinoverlay.listeners.bukkit.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion.*;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
public class SkinOverlayBukkit extends JavaPlugin implements SkinOverlayImpl {

    private int tick = 0;

    @Override
    public void onLoad() {
        try {
            if (getCurrentVersion().isBelow(V1_16_R3)) {
                new LibraryLoader(this.getClass(), this.getDataFolder()).loadAll(true);
            }
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        SkinOverlay.getInstance().onLoad(this);
    }

    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            tick++;
            SchedulerManager.getScheduler().mainThreadHeartbeat(tick);
        }, 0, 1L);
        switch (getCurrentVersion()) {
            case V1_17_R1 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_17());
            case V1_18_R1 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_18());
            case V1_18_R2 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_18_R2());
            case V1_19_R1 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_19());
            case V1_19_R2 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_19_R2());
            case UNKNOWN -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_());
            default -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Legacy());
        }
        SkinOverlay.getInstance().setCommandManager(new PaperCommandManager(this));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        BukkitMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        if (OptionsUtil.PROXY.getBooleanValue())
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "skinoverlay:bungee", new PlayerListeners());
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(this, 17474);
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        Bukkit.getScheduler().cancelTasks(this);
    }


    @Override
    public Type type() {
        return Type.PAPER;
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
        return new Description(getName(), getDescription().getVersion(), getDescription().getMain(), getDescription().getAuthors());
    }

    @Override
    public boolean enable(boolean enable) {
        setEnabled(enable);
        return enabled();
    }

    @Override
    public boolean enabled() {
        return isEnabled();
    }

    @Override
    public boolean onlineMode() {
        return Bukkit.getOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(PlayerObjectBukkit::new).collect(Collectors.toList());
    }

    @Override
    public Object plugin() {
        return this;
    }

    @Override
    public Object serverImpl() {
        return getServer();
    }

    @Override
    public String serverVersion() {
        return Bukkit.getBukkitVersion();
    }
}