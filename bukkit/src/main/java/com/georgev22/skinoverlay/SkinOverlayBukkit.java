package com.georgev22.skinoverlay;

import com.georgev22.skinoverlay.refreshers.*;
import com.georgev22.skinoverlay.command.BukkitCommandManager;
import com.georgev22.skinoverlay.hooks.SkinHookNoop;
import com.georgev22.skinoverlay.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.listeners.bukkit.PlayerListeners;
import com.georgev22.skinoverlay.listeners.bukkit.PluginMessageListenerImpl;
import com.georgev22.skinoverlay.messaging.MessageManagerNoop;
import com.georgev22.skinoverlay.messaging.RedisManager;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.providers.*;
import com.georgev22.skinoverlay.scheduler.MinecraftBukkitScheduler;
import com.georgev22.skinoverlay.scheduler.MinecraftFoliaScheduler;
import com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils;
import com.georgev22.skinoverlay.utilities.SkinOverlayVersionResolver;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion.getCurrent;

public class SkinOverlayBukkit extends JavaPlugin {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Override
    public void onLoad() {
        this.skinOverlay.setPlugin(this);
        this.skinOverlay.setLogger(this.getLogger());
        this.skinOverlay.setDataFolder(this.getDataFolder());
        this.skinOverlay.setCommandManager(new BukkitCommandManager(this));
        this.skinOverlay.setPlayerProvider(new BukkitPlayerProvider());

        if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            this.skinOverlay.setSkinHook(new SkinsRestorerHook());
            this.skinOverlay.setGameProfileProvider(new GameProfileProvider_SkinsRestorer());
            this.skinOverlay.setSkinRefresher(new NoopSkinRefresher());
        } else {
            this.skinOverlay.setSkinHook(new SkinHookNoop());
            if (BukkitMinecraftUtils.isPaper() && getCurrent().isAtLeast(1, 20)) {
                this.skinOverlay.setSkinRefresher(new NoopSkinRefresher());
                this.skinOverlay.setGameProfileProvider(new PaperGameProfileProvider());
            } else {
                new SkinOverlayVersionResolver(this.skinOverlay).resolve();
            }
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
                    skinOverlay.getSkinProvider().setSkin(player, skin);
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
