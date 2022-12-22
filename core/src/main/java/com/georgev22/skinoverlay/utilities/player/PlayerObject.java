package com.georgev22.skinoverlay.utilities.player;

import java.util.UUID;

public interface PlayerObject {
    Object getPlayer();

    UUID playerUUID();

    String playerName();

    default boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    public static class PlayerObjectWrapper {
        private final UUID uuid;
        private final boolean isBungee;

        public PlayerObjectWrapper(final UUID uuid, final boolean isBungee) {
            this.uuid = uuid;
            this.isBungee = isBungee;
        }

        public PlayerObject getPlayerObject() {
            return this.isBungee ? new PlayerObjectBungee(net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(this.uuid)) : new PlayerObjectBukkit(org.bukkit.Bukkit.getOfflinePlayer(this.uuid));
        }
    }
}
