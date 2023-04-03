package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileMojang;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SkinHandler_Unsupported extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions) {
        return CompletableFuture.supplyAsync(() -> {
            throw new UnsupportedOperationException("Unsupported Minecraft Version");
        });
    }

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property) {
        return updateSkin(playerObject, skinOptions);
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
        if (!gameProfile.getProperties().containsKey("textures")) {
            SProperty property = getSkin(playerObject);
            gameProfile.getProperties().put("textures", new Property(property.name(), property.value(), property.signature()));
        }
        return gameProfile;
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
    }

    public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        gameProfile.getProperties().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.getName(), property.getValue(), property.getSignature())));
        return new SGameProfileMojang(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
    }

    @Override
    protected void updateSkin0(UserManager.User user, PlayerObject playerObject, boolean forOthers) {
        updateSkin1(user, playerObject, forOthers);
    }
}