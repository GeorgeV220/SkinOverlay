package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfile_Velocity;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinHandler_Velocity extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player player = playerObject.player();
                skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:bungee");
                skinOverlay.getPluginMessageUtils().setObject(player.getCurrentServer().orElseThrow().getServerInfo());
                if (skin.skinParts().getSkinName().equalsIgnoreCase("default")) {
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
    public GameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) {
        return ((Player) playerObject.player()).getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getInternalGameProfile(playerObject))).get(playerObject);
    }

    public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        gameProfile.getProperties().forEach(property -> propertyObjectMap.append(property.getName(), new SProperty(property.getName(), property.getValue(), property.getSignature())));
        return new SGameProfile_Velocity(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
    }
}
