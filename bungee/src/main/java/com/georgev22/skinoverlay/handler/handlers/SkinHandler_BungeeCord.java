package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileBungee;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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
                if (skin.skinParts().getSkinName().equalsIgnoreCase("default")) {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("reset", playerObject.playerUUID().toString(), Utils.serializeObjectToString(skin), "true");
                } else {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("change", playerObject.playerUUID().toString(), Utils.serializeObjectToString(skin), "true");
                }
                return true;
            } catch (Exception exception) {
                skinOverlay.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
                return false;
            }
        });
    }

    @Override
    public void applySkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        skinOverlay.getSkinHandler().updateSkin(playerObject, skin).handleAsync((aBoolean, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                return false;
            }
            return aBoolean;
        });
    }

    @Override
    public SGameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) {
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
        return sGameProfiles.append(playerObject, this.getInternalGameProfile(playerObject)).get(playerObject);
    }
}
