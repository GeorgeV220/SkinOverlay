
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.SkinOverlays;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.lib.PaperLib;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;

public class SkinHandler_1_19_R2 extends SkinHandler {
    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, @NotNull String skinName, Property property, final Utils.@NotNull Callback<Boolean> callback) {
        this.updateSkin(playerObject, skinName, callback);
    }

    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, @NotNull String skinName, final Utils.@NotNull Callback<Boolean> callback) {
        try {
            Player player = (Player) playerObject.player();
            final CraftPlayer craftPlayer = (CraftPlayer) player;
            final ServerPlayer entityPlayer = craftPlayer.getHandle();


            ClientboundPlayerInfoRemovePacket removePlayer = new ClientboundPlayerInfoRemovePacket(List.of(entityPlayer.getUUID()));
            ClientboundPlayerInfoUpdatePacket addPlayer = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer));
            ServerLevel world = entityPlayer.getLevel();
            ServerPlayerGameMode gamemode = entityPlayer.gameMode;

            ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                    world.dimensionTypeId(),
                    world.dimension(),
                    BiomeManager.obfuscateSeed(world.getSeed()),
                    gamemode.getGameModeForPlayer(),
                    gamemode.getPreviousGameModeForPlayer(),
                    world.isDebug(),
                    world.isFlat(),
                    (byte) 3,
                    entityPlayer.getLastDeathLocation()
            );

            Location l = player.getLocation();
            ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0, false);
            ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

            sendPacket(entityPlayer, removePlayer);
            sendPacket(entityPlayer, addPlayer);

            sendPacket(entityPlayer, respawn);

            SynchedEntityData synchedEntityData = entityPlayer.getEntityData();

            EntityDataAccessor<Byte> entityDataAccessor;

            synchedEntityData.set(entityDataAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinOverlays.getFlags(skinName));

            synchedEntityData.markDirty(entityDataAccessor);

            synchedEntityData.refresh(entityPlayer);

            entityPlayer.onUpdateAbilities();

            sendPacket(entityPlayer, pos);
            sendPacket(entityPlayer, slot);
            craftPlayer.updateScaledHealth();
            player.updateInventory();
            entityPlayer.resetSentInfo();
            callback.onSuccess();
        } catch (Exception exception) {
            callback.onFailure(exception);
        }
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        Player player = (Player) playerObject.player();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        if (PaperLib.isSpigot())
            try {
                Field field = entityPlayer.getClass().getDeclaredField("cs");
                if (Modifier.isPrivate(field.getModifiers())) {
                    return (GameProfile) Utils.Reflection.fetchDeclaredField(entityPlayer.getClass().getSuperclass(), entityPlayer, "cs");
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException exception) {
                exception.printStackTrace();
            }
        return entityPlayer.gameProfile;
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

