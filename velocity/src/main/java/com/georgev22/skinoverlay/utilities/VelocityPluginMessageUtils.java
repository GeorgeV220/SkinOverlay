package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlayVelocity;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;

public class VelocityPluginMessageUtils extends PluginMessageUtils {

    public void sendDataToServer(@NotNull String subChannel, String... dataArray) {
        if (getObject() == null) {
            skinOverlay.getLogger().severe("ServerInfo is null!!");
            return;
        }
        if (SkinOverlayVelocity.getInstance().getProxy().getServer(((ServerInfo) getObject()).getName()).isPresent()) {
            SkinOverlayVelocity.getInstance().getProxy().getServer(((ServerInfo) getObject()).getName()).get().sendPluginMessage(this::getChannel, this.toByteArray(subChannel, dataArray));
        }
    }

    public void sendDataToPlayer(@NotNull String subChannel, @NotNull PlayerObject player, String... dataArray) {
        ((Player) player.player()).sendPluginMessage(this::getChannel, this.toByteArray(subChannel, dataArray));
    }
}
