package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PlayerObjectWrapper implements PlayerObject {

    private final String name;
    private final UUID uuid;
    private final SkinOverlayImpl.Type type;

    public PlayerObjectWrapper(final UUID uuid, final SkinOverlayImpl.Type type) {
        this.name = null;
        this.uuid = uuid;
        this.type = type;
    }

    public PlayerObjectWrapper(@Nullable final String name, final UUID uuid, final SkinOverlayImpl.Type type) {
        this.name = name;
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
                return ((com.velocitypowered.api.proxy.ProxyServer) SkinOverlay.getInstance().getSkinOverlay().getServerImpl()).getPlayer(this.uuid).map(PlayerObjectVelocity::new).orElseGet(() -> new PlayerObjectVelocity(uuid, name));
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public Object getPlayer() {
        return getPlayerObject().getPlayer();
    }

    @Override
    public UUID playerUUID() {
        return getPlayerObject().playerUUID();
    }

    @Override
    public String playerName() {
        return getPlayerObject().playerName();
    }

    @Override
    public void sendMessage(String input) {
        getPlayerObject().sendMessage(input);
    }

    @Override
    public void sendMessage(List<String> input) {
        getPlayerObject().sendMessage(input);
    }

    @Override
    public void sendMessage(String... input) {
        getPlayerObject().sendMessage(input);
    }
}