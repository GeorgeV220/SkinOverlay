package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.Skin;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileBungee;
import com.georgev22.skinoverlay.utilities.player.User;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinHandler_BungeeCord extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) playerObject.player();
                skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:bungee");
                skinOverlay.getPluginMessageUtils().setObject(proxiedPlayer.getServer().getInfo());
                if (skin.skinOptions().getSkinName().equalsIgnoreCase("default")) {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("reset", playerObject.playerUUID().toString(), Utils.serializeObjectToString(skin), "true");
                } else {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("change", playerObject.playerUUID().toString(), Utils.serializeObjectToString(skin), "true");
                }
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public SGameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        ObjectMap<String, SProperty> properties = new HashObjectMap<>();
        for (Property property : ((InitialHandler) ((ProxiedPlayer) playerObject.player()).getPendingConnection()).getLoginProfile().getProperties()) {
            properties.append(property.getName(), new SProperty(property.getName(), property.getValue(), property.getSignature()));
        }
        return new SGameProfileBungee(playerObject.playerName(), playerObject.playerUUID(), properties);
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, this.getGameProfile0(playerObject)).get(playerObject);
    }

    @Override
    protected void updateSkin0(User user, PlayerObject playerObject, boolean forOthers) {
        updateSkin1(user, playerObject, forOthers);
    }
}
