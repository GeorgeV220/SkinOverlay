
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class SkinHandler_1_19 extends SkinHandler {
    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property, @NotNull final Utils.Callback<Boolean> callback) {
        this.updateSkin(playerObject, reset, skinName, callback);
    }

    @Override
    public void updateSkin(@NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, @NotNull final Utils.Callback<Boolean> callback) {
        try {

            Player player = (Player) playerObject.player();
            final CraftPlayer craftPlayer = (CraftPlayer) player;
            final ServerPlayer entityPlayer = craftPlayer.getHandle();


            ClientboundPlayerInfoPacket removePlayer = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, ImmutableList.of(entityPlayer));
            ClientboundPlayerInfoPacket addPlayer = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, ImmutableList.of(entityPlayer));

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
                    true,
                    entityPlayer.getLastDeathLocation()
            );

            Location l = player.getLocation();
            ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0, false);
            ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

            sendPacket(entityPlayer, removePlayer);
            sendPacket(entityPlayer, addPlayer);

            sendPacket(entityPlayer, respawn);

            SynchedEntityData synchedEntityData = entityPlayer.getEntityData();

            if (reset | skinName.equalsIgnoreCase("default")) {
                synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
            } else {
                synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE),
                        (byte)
                                ((skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".cape", false) ? 0x01 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".jacket", false) ? 0x02 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".left_sleeve", false) ? 0x04 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".right_sleeve", false) ? 0x08 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".left_pants", false) ? 0x10 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".right_pants", false) ? 0x20 : 0x0) |
                                        (skinOverlay.getConfig().getBoolean("Options.overlays." + skinName + ".hat", false) ? 0x40 : 0x0))
                );
            }


            ClientboundSetEntityDataPacket clientboundSetEntityDataPacket = new ClientboundSetEntityDataPacket(entityPlayer.getId(), synchedEntityData, true);

            sendPacket(entityPlayer, clientboundSetEntityDataPacket);

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
        return entityPlayer.gameProfile;
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

