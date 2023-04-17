package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlayBukkit;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitPluginMessageUtils extends PluginMessageUtils {

    public void sendDataToServer(@NotNull String subChannel, String... dataArray) {
        Bukkit.getServer().sendPluginMessage((Plugin) skinOverlay.getSkinOverlay().plugin(), getChannel(), this.toByteArray(subChannel, dataArray));
    }

    @Override
    public void sendDataToPlayer(@NotNull String subChannel, @NotNull PlayerObject player, String... dataArray) {
        ((Player) player.player()).sendPluginMessage((Plugin) skinOverlay.getSkinOverlay().plugin(), getChannel(), this.toByteArray(subChannel, dataArray));
    }
}
