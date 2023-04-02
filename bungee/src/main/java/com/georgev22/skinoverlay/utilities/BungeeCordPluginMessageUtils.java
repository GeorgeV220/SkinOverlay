package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public final class BungeeCordPluginMessageUtils extends PluginMessageUtils {

    @Override
    public void sendDataToServer(@NotNull String subChannel, String... dataArray) {
        if (getObject() == null) {
            skinOverlay.getLogger().severe("ServerInfo is null!!");
            return;
        }
        ((ServerInfo) getObject()).sendData(getChannel(), this.toByteArray(subChannel, dataArray));
    }

    @Override
    public void sendDataToPlayer(@NotNull String subChannel, @NotNull PlayerObject player, String... dataArray) {
        ((ProxiedPlayer) player.player()).sendData(getChannel(), this.toByteArray(subChannel, dataArray));
    }
}
