package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public interface PlayerObject {
    Object getPlayer();

    UUID playerUUID();

    String playerName();

    default boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    void sendMessage(String input);

    void sendMessage(List<String> input);

    void sendMessage(String... input);

    class PlayerObjectWrapper {
        private final UUID uuid;
        private final SkinOverlayImpl.Type type;

        public PlayerObjectWrapper(final UUID uuid, final SkinOverlayImpl.Type type) {
            this.uuid = uuid;
            this.type = type;
        }

        public PlayerObject getPlayerObject() {
            switch (type) {
                case BUNGEE -> {
                    return new PlayerObjectBungee(net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(this.uuid));
                }
                case PAPER -> {
                    return new PlayerObjectBukkit(org.bukkit.Bukkit.getOfflinePlayer(this.uuid));
                }
                case VELOCITY -> {
                    try {
                        return new PlayerObjectVelocity(((ProxyServer) SkinOverlay.getInstance().getSkinOverlay().getPlugin().getClass().getMethod("getProxy").invoke(SkinOverlay.getInstance().getSkinOverlay().getPlugin())).getPlayer(this.uuid).get());
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
