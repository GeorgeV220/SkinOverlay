package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.appliers.*;
import com.georgev22.skinoverlay.command.BukkitCommandManager;
import com.georgev22.skinoverlay.hooks.SkinHookNoop;
import com.georgev22.skinoverlay.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.listeners.bukkit.PluginMessageListenerImpl;
import com.georgev22.skinoverlay.message.MessageManagerNoop;
import com.georgev22.skinoverlay.message.RedisManager;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.providers.*;
import com.georgev22.skinoverlay.scheduler.MinecraftBukkitScheduler;
import com.georgev22.skinoverlay.scheduler.MinecraftFoliaScheduler;
import com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion.V1_20_R1;
import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion;

public class SkinOverlayBukkit extends JavaPlugin {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Override
    public void onLoad() {
        this.skinOverlay.setPlugin(this);
        this.skinOverlay.setLogger(this.getLogger());
        this.skinOverlay.setDataFolder(this.getDataFolder());
        this.skinOverlay.setCommandManager(new BukkitCommandManager(this));
        this.skinOverlay.setPlayerProvider(new BukkitPlayerProvider());

        if (BukkitMinecraftUtils.isPaper() && getCurrentVersion().isAboveOrEqual(V1_20_R1)) {
            this.skinOverlay.setSkinApplier(new NoopSkinApplier());
            this.skinOverlay.setGameProfileProvider(new PaperGameProfileProvider());
        } else {
            switch (getCurrentVersion()) {
                case V1_8_R1, V1_8_R2, V1_8_R3, V1_9_R1, V1_9_R2, V1_10_R1, V1_11_R1, V1_12_R1, V1_13_R1, V1_13_R2,
                     V1_14_R1, V1_15_R1, V1_16_R1, V1_16_R2, V1_16_R3 -> {
                    this.skinOverlay.setGameProfileProvider(new BukkitLegacyGameProfileProvider());
                    this.skinOverlay.setSkinApplier(new LegacySkinApplier());
                }
                case V1_17_R1 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_17_R1());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_17_R1());
                }
                case V1_18_R1 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_18_R1());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_18_R1());
                }
                case V1_18_R2 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_18_R2());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_18_R2());
                }
                case V1_19_R1 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_19_R1());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_19_R1());
                }
                case V1_19_R2 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_19_R2());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_19_R2());
                }
                case V1_19_R3 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_19_R3());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_19_R3());
                }
                case V1_20_R1 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_20_R1());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_20_R1());
                }
                case V1_20_R2 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_20_R2());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_20_R2());
                }
                case V1_20_R3 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_20_R3());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_20_R3());
                }
                case V1_21_R1 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R1());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R1());
                }
                case V1_21_R2 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R2());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R2());
                }
                case V1_21_R3 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R3());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R3());
                }
                case V1_21_R4 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R4());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R4());
                }
                case V1_21_R5 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R5());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R5());
                }
                case V1_21_R6 -> {
                    this.skinOverlay.setGameProfileProvider(new GameProfileProvider_1_21_R6());
                    this.skinOverlay.setSkinApplier(new SkinApplier_1_21_R6());
                }
                default -> {
                    this.skinOverlay.setSkinApplier(new NoopSkinApplier());
                    this.skinOverlay.setGameProfileProvider(new GameProfileProviderNoop());
                    this.getLogger().info("SkinOverlay does not support " + Bukkit.getBukkitVersion());
                }
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            this.skinOverlay.setSkinHook(new SkinsRestorerHook());
            this.skinOverlay.setGameProfileProvider(new GameProfileProvider_SkinsRestorer());
            this.skinOverlay.setSkinApplier(new NoopSkinApplier());
        } else {
            this.skinOverlay.setSkinHook(new SkinHookNoop());
        }
        this.skinOverlay.setOnlineMode(Bukkit.getOnlineMode());
        this.skinOverlay.setProxy(false);
        if (BukkitMinecraftUtils.isFolia()) {
            this.skinOverlay.setScheduler(
                    new MinecraftFoliaScheduler()
            );
        } else {
            this.skinOverlay.setScheduler(
                    new MinecraftBukkitScheduler()
            );
        }
        this.skinOverlay.onLoad();
    }

    @Override
    public void onEnable() {
        this.skinOverlay.setAudienceProvider(BukkitAudiences.create(this));

        BukkitMinecraftUtils.registerListeners(
                this,
                new PlayerListeners()
        );

        if (OptionsUtil.PROXY.getBooleanValue()) {
            if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("PluginMessage")) {
                this.skinOverlay.setMessageManager(new PluginMessageListenerImpl());
            } else if (OptionsUtil.CONNECTION_TYPE.getStringValue().equalsIgnoreCase("Redis")) {
                this.skinOverlay.setMessageManager(new RedisManager(
                        OptionsUtil.REDIS_HOST.getStringValue(),
                        OptionsUtil.REDIS_PORT.getIntValue(),
                        OptionsUtil.REDIS_PASSWORD.getStringValue()
                ));
            } else {
                this.skinOverlay.setMessageManager(new MessageManagerNoop());
            }
            skinOverlay.getMessageManager().subscribeSkinProperty((uuid, skin) -> {
                SPlayer player = this.skinOverlay.getPlayerProvider().getSPlayer(uuid);
                if (player != null) {
                    skinOverlay.getSkinApplier().setSkin(player, skin);
                }
            });
        }
        // call onEnable
        skinOverlay.onEnable();
    }

    @Override
    public void onDisable() {
        skinOverlay.onDisable();
    }
}
