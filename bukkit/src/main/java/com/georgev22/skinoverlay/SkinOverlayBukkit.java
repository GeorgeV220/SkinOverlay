package com.georgev22.skinoverlay;

import co.aikar.commands.PaperCommandManager;
import com.georgev22.api.libraryloader.LibraryLoader;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.api.libraryloader.exceptions.InvalidDependencyException;
import com.georgev22.api.libraryloader.exceptions.UnknownDependencyException;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.handlers.*;
import com.georgev22.skinoverlay.listeners.bukkit.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bukkit.PaperPlayerListeners;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.utilities.BukkitPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.bukkit.SkinOverlayBukkitScheduler;
import com.georgev22.skinoverlay.utilities.bukkit.SkinOverlayFoliaScheduler;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

import static com.georgev22.library.minecraft.BukkitMinecraftUtils.MinecraftVersion.*;

@MavenLibrary(groupId = "org.mongodb", artifactId = "mongo-java-driver", version = "3.12.7")
@MavenLibrary(groupId = "mysql", artifactId = "mysql-connector-java", version = "8.0.22")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.34.0")
@MavenLibrary(groupId = "com.google.guava", artifactId = "guava", version = "30.1.1-jre")
@MavenLibrary(groupId = "org.postgresql", artifactId = "postgresql", version = "42.2.18")
@MavenLibrary(groupId = "commons-io", artifactId = "commons-io", version = "2.11.0")
@MavenLibrary(groupId = "commons-codec", artifactId = "commons-codec", version = "1.15")
@MavenLibrary(groupId = "commons-lang", artifactId = "commons-lang", version = "2.6")
@MavenLibrary(groupId = "org.jsoup", artifactId = "jsoup", version = "1.15.3")
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinOverlayBukkit extends JavaPlugin implements SkinOverlayImpl {
    private LibraryLoader libraryLoader;

    private int tick = 0;

    private BukkitAudiences adventure;
    private SkinOverlay skinOverlay;

    private SkinOverlayBukkitScheduler scheduler;

    @Override
    public void onLoad() {
        try {
            if (getCurrentVersion().isBelow(V1_16_R3)) {
                this.libraryLoader = new LibraryLoader(this.getClassLoader(), this.getDataFolder());
                this.libraryLoader.loadAll(this, true);
            }
        } catch (InvalidDependencyException | UnknownDependencyException e) {
            throw new RuntimeException(e);
        }
        skinOverlay = new SkinOverlay(this);
        skinOverlay.onLoad();
    }

    @Override
    public void onEnable() {
        if (BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(V1_16_R1))
            this.adventure = BukkitAudiences.create(this);
        if (isFolia()) {
            this.scheduler = new SkinOverlayFoliaScheduler();
        } else {
            this.scheduler = new SkinOverlayBukkitScheduler();
        }
        scheduler.createRepeatingTask(this, () -> {
            tick++;
            SchedulerManager.getScheduler().mainThreadHeartbeat(tick);
        }, 1L, 1L);
        switch (getCurrentVersion()) {
            case V1_17_R1 -> skinOverlay.setSkinHandler(new SkinHandler_1_17());
            case V1_18_R1 -> skinOverlay.setSkinHandler(new SkinHandler_1_18());
            case V1_18_R2 -> skinOverlay.setSkinHandler(new SkinHandler_1_18_R2());
            case V1_19_R1 -> skinOverlay.setSkinHandler(new SkinHandler_1_19());
            case V1_19_R2 -> skinOverlay.setSkinHandler(new SkinHandler_1_19_R2());
            case V1_19_R3 -> {
                if (isFolia()) {
                    skinOverlay.setSkinHandler(new SkinHandler_Folia_1_19_R3());
                } else {
                    skinOverlay.setSkinHandler(new SkinHandler_1_19_R3());
                }
            }
            case UNKNOWN -> skinOverlay.setSkinHandler(new SkinHandler_Unsupported());
            default -> skinOverlay.setSkinHandler(new SkinHandler_Legacy());
        }
        skinOverlay.setCommandManager(new PaperCommandManager(this));
        skinOverlay.onEnable();
        BukkitMinecraftUtils.registerListeners(this, new PlayerListeners(), new DeveloperInformListener());
        if (PaperLib.isPaper() & getCurrentVersion().isAboveOrEqual(V1_15_R1))
            BukkitMinecraftUtils.registerListeners(this, new PaperPlayerListeners());
        if (OptionsUtil.PROXY.getBooleanValue()) {
            skinOverlay.setPluginMessageUtils(new BukkitPluginMessageUtils());
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "skinoverlay:bungee", new PlayerListeners());
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "skinoverlay:message");
        }
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(this, 17474);
        if (!PaperLib.isPaper())
            PaperLib.suggestPaper(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        skinOverlay.onDisable();
        scheduler.cancelTasks(this);
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        if (getCurrentVersion().isBelow(V1_16_R3)) {
            try {
                this.libraryLoader.unloadAll();
            } catch (InvalidDependencyException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public Type type() {
        return Type.BUKKIT;
    }

    @Override
    public File dataFolder() {
        return this.getDataFolder();
    }

    @Override
    public Logger logger() {
        return this.getLogger();
    }

    @Override
    public Description description() {
        return new Description(this.getName(), this.getDescription().getVersion(), this.getDescription().getMain(), this.getDescription().getAuthors());
    }

    @Override
    public boolean enable(boolean enable) {
        this.setEnabled(enable);
        return enabled();
    }

    @Override
    public boolean enabled() {
        return this.isEnabled();
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        super.saveResource(resource, replace);
    }

    @Override
    public boolean onlineMode() {
        return Bukkit.getOnlineMode();
    }

    private final ObservableObjectMap<UUID, PlayerObject> players = new ObservableObjectMap<>();

    @Override
    public ObservableObjectMap<UUID, PlayerObject> onlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (players.containsKey(player.getUniqueId())) {
                continue;
            }
            players.append(player.getUniqueId(), new PlayerObjectBukkit(player));
        }
        return players;
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return this.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    @Override
    public Object plugin() {
        return this;
    }

    @Override
    public Object serverImpl() {
        return this.getServer();
    }

    @Override
    public String serverVersion() {
        return Bukkit.getBukkitVersion();
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}