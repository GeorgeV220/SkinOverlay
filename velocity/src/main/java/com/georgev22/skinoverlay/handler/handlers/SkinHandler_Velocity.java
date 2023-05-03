package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfile_Velocity;
import com.georgev22.skinoverlay.storage.User;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkinHandler_Velocity extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player player = (Player) playerObject.player();
                skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:bungee");
                skinOverlay.getPluginMessageUtils().setObject(player.getCurrentServer().orElseThrow().getServerInfo());
                if (skinOptions.getSkinName().equalsIgnoreCase("default")) {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("reset", playerObject.playerUUID().toString(), SkinOptions.skinOptionsToBytes(skinOptions));
                } else {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("change", playerObject.playerUUID().toString(), SkinOptions.skinOptionsToBytes(skinOptions));
                }
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player player = (Player) playerObject.player();
                skinOverlay.getPluginMessageUtils().setChannel("skinoverlay:bungee");
                skinOverlay.getPluginMessageUtils().setObject(player.getCurrentServer().orElseThrow().getServerInfo());
                if (skinOptions.getSkinName().equalsIgnoreCase("default")) {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("resetWithProperties", playerObject.playerUUID().toString(), SkinOptions.skinOptionsToBytes(skinOptions), property.name(), property.value(), property.signature());
                } else {
                    skinOverlay.getPluginMessageUtils().sendDataToServer("changeWithProperties", playerObject.playerUUID().toString(), SkinOptions.skinOptionsToBytes(skinOptions), property.name(), property.value(), property.signature());
                }
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        return ((Player) playerObject.player()).getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
    }

    public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        gameProfile.getProperties().forEach(property -> propertyObjectMap.append(property.getName(), new SProperty(property.getName(), property.getValue(), property.getSignature())));
        return new SGameProfile_Velocity(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
    }

    @Override
    protected void updateSkin0(User user, PlayerObject playerObject, boolean forOthers) {
        updateSkin1(user, playerObject, forOthers);
    }
}
