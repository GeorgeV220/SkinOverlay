
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
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
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SkinHandler_1_19_R2 extends SkinHandler {
    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
        this.updateSkin(fileConfiguration, playerObject, reset, skinName);
    }

    @Override
    public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {

        Player player = (Player) playerObject.getPlayer();
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

        if (reset | skinName.equalsIgnoreCase("default")) {
            synchedEntityData.set(entityDataAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
        } else {
            synchedEntityData.set(entityDataAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE),
                    (byte)
                            ((fileConfiguration.getBoolean("Options.overlays." + skinName + ".cape", false) ? 0x01 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".jacket", false) ? 0x02 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".left_sleeve", false) ? 0x04 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".right_sleeve", false) ? 0x08 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".left_pants", false) ? 0x10 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".right_pants", false) ? 0x20 : 0x00) |
                                    (fileConfiguration.getBoolean("Options.overlays." + skinName + ".hat", false) ? 0x40 : 0x00))
            );
        }

        synchedEntityData.markDirty(entityDataAccessor);


        ClientboundSetEntityDataPacket clientboundSetEntityDataPacket = new ClientboundSetEntityDataPacket(entityPlayer.getId(), Objects.requireNonNull(synchedEntityData.packDirty()));

        sendPacket(entityPlayer, clientboundSetEntityDataPacket);

        entityPlayer.onUpdateAbilities();

        sendPacket(entityPlayer, pos);
        sendPacket(entityPlayer, slot);
        craftPlayer.updateScaledHealth();
        player.updateInventory();
        entityPlayer.resetSentInfo();
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        Player player = (Player) playerObject.getPlayer();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.gameProfile;
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

