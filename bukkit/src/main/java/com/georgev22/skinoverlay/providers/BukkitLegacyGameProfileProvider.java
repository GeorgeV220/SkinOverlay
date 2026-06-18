package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftReflection.obcClass;
import static com.georgev22.skinoverlay.utilities.Utils.Reflection.fetchMethodAndInvoke;

public class BukkitLegacyGameProfileProvider extends GameProfileProvider {

    @Override
    public GameProfile getInternalGameProfile(@NotNull SPlayer player) {
        try {
            Class<?> craftPlayerClass = obcClass("entity.CraftPlayer");
            Player bukkitPlayer = player.getPlayer();
            return (GameProfile) fetchMethodAndInvoke(craftPlayerClass, "getProfile", bukkitPlayer, new Object[]{}, new Class[]{});
        } catch (Exception e) {
            skinOverlay.getLogger().log(Level.SEVERE, "Error while trying to retrieve internal game profile: ", e);
            skinOverlay.getLogger().info("Trying to create a new game profile...");
            GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
            if (!gameProfile.getProperties().containsKey("textures")) {
                try {
                    SProperty property = this.skinOverlay.getSkinProvider().getSkin(player);
                    gameProfile.getProperties().put("textures", new Property(property.value(), property.signature()));
                } catch (IOException | ExecutionException | InterruptedException exception) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error while trying to apply new texture: ", exception);
                }
            }
            return gameProfile;
        }
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        GameProfile gameProfile = this.getInternalGameProfile(player);
        gameProfile.getProperties().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.getValue(), property.getSignature())));
        SGameProfile sGameProfile = new SGameProfile(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
        return sGameProfiles.append(player, sGameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        GameProfile internalGameProfile = this.getInternalGameProfile(player);
        internalGameProfile.getProperties().removeAll("textures");
        SGameProfile gameProfile = this.getGameProfile(player);
        gameProfile.getProperties().forEach((s, property) -> internalGameProfile.getProperties()
                .put(s, new Property(s, property.value(), property.signature())));
    }
}
