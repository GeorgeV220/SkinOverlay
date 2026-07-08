package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameProfileProvider_1_19 extends GameProfileProvider {
    @Override
    public @NotNull GameProfile getInternalGameProfile(@NotNull SPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        final CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        ObjectMap<String, SProperty> propertyObjectMap = new HashObjectMap<>();
        GameProfile gameProfile = this.getInternalGameProfile(player);
        gameProfile.getProperties().forEach((name, property) -> propertyObjectMap
                .append(name, new SProperty(property.getName(), property.getValue(), property.getSignature())));
        SGameProfile sGameProfile = new SGameProfile(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
        return sGameProfiles.append(player, sGameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        GameProfile internalGameProfile = this.getInternalGameProfile(player);
        internalGameProfile.getProperties().removeAll("textures");
        SGameProfile gameProfile = this.getGameProfile(player);
        gameProfile.getProperties().forEach((name, property) -> internalGameProfile.getProperties()
                .put(name, new Property(property.name(), property.value(), property.signature())));
    }
}
