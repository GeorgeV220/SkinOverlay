package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.skinoverlay.SkinOverlay;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerObjectBukkit extends PlayerObject {
    private final OfflinePlayer offlinePlayer;

    public PlayerObjectBukkit(final OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public OfflinePlayer player() {
        return this.offlinePlayer;
    }

    @Override
    public Audience audience() {
        return BukkitMinecraftUtils.MinecraftVersion.getCurrentVersion().isBelow(BukkitMinecraftUtils.MinecraftVersion.V1_16_R1) ? SkinOverlay.getInstance().getSkinOverlay().adventure().player(player().getUniqueId()) : player().getPlayer();
    }

    @Override
    public UUID playerUUID() {
        return this.offlinePlayer.getUniqueId();
    }

    @Override
    public String playerName() {
        return this.offlinePlayer.getName();
    }

    @Override
    public void sendMessage(String input) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input);
    }

    @Override
    public void sendMessage(List<String> input) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input);
    }

    @Override
    public void sendMessage(String... input) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input);
    }

    @Override
    public void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        BukkitMinecraftUtils.msg(Objects.requireNonNull(offlinePlayer.getPlayer()), input, placeholders, ignoreCase);
    }

    @Override
    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    @Override
    public boolean permission(String permission) {
        return isOnline() && player().getPlayer().hasPermission(permission);
    }
}
