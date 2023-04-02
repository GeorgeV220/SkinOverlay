package com.georgev22.skinoverlay;

import co.aikar.commands.PaperCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.handlers.*;
import com.georgev22.skinoverlay.hook.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.bukkit.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

    private BukkitAudiences adventure;

    private static SkinOverlayBukkit skinOverlayBukkit;

    public static SkinOverlayBukkit getInstance() {
        return skinOverlayBukkit == null ? SkinOverlayBukkit.getPlugin(SkinOverlayBukkit.class) : skinOverlayBukkit;
    }

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
        if (BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(V1_16_R1))
            this.adventure = BukkitAudiences.create(this);
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
            case V1_19_R3 -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_1_19_R3());
            case UNKNOWN -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Unsupported());
            default -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Legacy());
        }
        switch (OptionsUtil.SKIN_HOOK.getStringValue()) {
            case "SkinsRestorer" -> {
                if (getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
                    SkinOverlay.getInstance().setSkinHook(new SkinsRestorerHook());
                }
            }
            default -> SkinOverlay.getInstance().setSkinHook(null);
        }
        SkinOverlay.getInstance().setCommandManager(new PaperCommandManager(this));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        BukkitMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        if (OptionsUtil.PROXY.getBooleanValue())
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "skinoverlay:bungee", new PlayerListeners());
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(this, 17474);
        if (!PaperLib.isPaper())
            PaperLib.suggestPaper(this);
    }

    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        Bukkit.getScheduler().cancelTasks(this);
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
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

    @Override
    public void print(String... msg) {
        BukkitMinecraftUtils.printMsg(msg);
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
}