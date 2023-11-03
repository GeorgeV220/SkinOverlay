package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileMojang;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinHandler_Unsupported extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            throw new UnsupportedOperationException("Unsupported Minecraft Version");
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
    public GameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
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
        return sGameProfiles.append(playerObject, wrapper(this.getInternalGameProfile(playerObject))).get(playerObject);
    }

    public static @NotNull SGameProfile wrapper(@NotNull GameProfile gameProfile) {
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        gameProfile.getProperties().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.getName(), property.getValue(), property.getSignature())));
        return new SGameProfileMojang(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
    }
}