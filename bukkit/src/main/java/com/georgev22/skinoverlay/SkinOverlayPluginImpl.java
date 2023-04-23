package com.georgev22.skinoverlay;

import co.aikar.commands.PaperCommandManager;
import com.georgev22.api.libraryloader.annotations.MavenLibrary;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.handlers.*;
import com.georgev22.skinoverlay.hook.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.bukkit.DeveloperInformListener;
import com.georgev22.skinoverlay.listeners.bukkit.GlowStonePlayerListeners;
import com.georgev22.skinoverlay.listeners.bukkit.PaperPlayerListeners;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.utilities.BukkitPluginMessageUtils;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBukkit;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
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
@MavenLibrary("org.apache.commons:commons-lang3:3.12.0:https://repo1.maven.org/maven2/")
public class SkinOverlayPluginImpl implements SkinOverlayImpl {

    private int tick = 0;

    private BukkitAudiences adventure;

    private final JavaPlugin plugin;
    private final boolean glowStone;
    private static SkinOverlayPluginImpl instance;

    public static SkinOverlayPluginImpl getInstance() {
        return instance;
    }

    public SkinOverlayPluginImpl(JavaPlugin javaPlugin, boolean glowStone) {
        instance = this;
        this.plugin = javaPlugin;
        this.glowStone = glowStone;
    }

    public void onLoad() {
        SkinOverlay.getInstance().onLoad(this);
    }

    public void onEnable() {
        if (BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(V1_16_R1) | glowStone)
            this.adventure = BukkitAudiences.create(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
            case UNKNOWN -> {
                if (glowStone) {
                    SkinOverlay.getInstance().setSkinHandler(new SkinHandler_GlowStone());
                } else {
                    SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Unsupported());
                }
            }
            default -> SkinOverlay.getInstance().setSkinHandler(new SkinHandler_Legacy());
        }
        switch (OptionsUtil.SKIN_HOOK.getStringValue()) {
            case "SkinsRestorer" -> {
                if (plugin.getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
                    SkinOverlay.getInstance().setSkinHook(new SkinsRestorerHook());
                }
            }
            default -> SkinOverlay.getInstance().setSkinHook(null);
        }
        SkinOverlay.getInstance().setCommandManager(new PaperCommandManager(plugin));
        SkinOverlay.getInstance().onEnable();
        SkinOverlay.getInstance().setupCommands();
        BukkitMinecraftUtils.registerListeners(plugin, new PlayerListeners(), new DeveloperInformListener());
        if (glowStone)
            BukkitMinecraftUtils.registerListeners(plugin, new GlowStonePlayerListeners());
        if (PaperLib.isPaper() & getCurrentVersion().isAboveOrEqual(V1_15_R1))
            BukkitMinecraftUtils.registerListeners(plugin, new PaperPlayerListeners());
        if (OptionsUtil.PROXY.getBooleanValue()) {
            SkinOverlay.getInstance().setPluginMessageUtils(new BukkitPluginMessageUtils());
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "skinoverlay:bungee", new PlayerListeners());
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "skinoverlay:message");
        }
        if (OptionsUtil.METRICS.getBooleanValue())
            new Metrics(plugin, 17474);
        if (!PaperLib.isPaper() & !glowStone)
            PaperLib.suggestPaper(plugin);
    }

    public void onDisable() {
        Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin);
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin);
        SkinOverlay.getInstance().onDisable();
        Bukkit.getScheduler().cancelTasks(plugin);
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }


    @Override
    public Type type() {
        return Type.BUKKIT;
    }

    @Override
    public File dataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger logger() {
        return plugin.getLogger();
    }

    @Override
    public Description description() {
        return new Description(plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), plugin.getDescription().getAuthors());
    }

    @Override
    public boolean enable(boolean enable) {
        plugin.setEnabled(enable);
        return enabled();
    }

    @Override
    public boolean enabled() {
        return plugin.isEnabled();
    }

    @Override
    public void saveResource(@NotNull String resource, boolean replace) {
        plugin.saveResource(resource, replace);
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
    public Object plugin() {
        return plugin;
    }

    @Override
    public Object serverImpl() {
        return plugin.getServer();
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
}