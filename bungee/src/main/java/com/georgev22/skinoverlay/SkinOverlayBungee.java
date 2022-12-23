package com.georgev22.skinoverlay;

import co.aikar.commands.BungeeCommandManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.PlayerObjectBungee;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SkinOverlayBungee extends Plugin implements SkinOverlayImpl {

    private int tick = 0;

    private boolean enabled = false;

    @Override
    public void onLoad() {
        SkinOverlay.getInstance().onLoad(this, new BungeeCommandManager(this));
        ProxyServer.getInstance().getScheduler().schedule(this, () -> SkinOverlay.getInstance().getScheduler().mainThreadHeartbeat(tick++), 0, 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onEnable() {
        SkinOverlay.getInstance().setSkinHandler(new SkinHandler() {
            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
                //TODO BUNGEECORD
            }

            @Override
            public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
                //TODO BUNGEECORD
            }

            @Override
            protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
                return Utilities.createGameProfile(playerObject);
            }
        });
        SkinOverlay.getInstance().onEnable();
        enabled = true;
    }

    @Override
    public void onDisable() {
        SkinOverlay.getInstance().onDisable();
        enabled = false;
    }

    @Override
    public boolean isBungee() {
        return true;
    }

    @Override
    public Description description() {
        return new Description(getDescription().getName(), getDescription().getVersion(), getDescription().getMain(), Collections.singletonList(getDescription().getAuthor()));
    }

    @Override
    public boolean setEnable(boolean enable) {
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
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
    public boolean isOnlineMode() {
        return ProxyServer.getInstance().getConfig().isOnlineMode();
    }

    @Override
    public List<PlayerObject> onlinePlayers() {
        List<PlayerObject> playerObjects = new ArrayList<>();
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> playerObjects.add(new PlayerObjectBungee(proxiedPlayer)));
        return playerObjects;
    }
}
