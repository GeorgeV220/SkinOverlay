
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.handler.profile.SGameProfileMojang;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class SkinHandler_1_20_2 extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player player = playerObject.player();
                final CraftPlayer craftPlayer = (CraftPlayer) player;
                final ServerPlayer entityPlayer = craftPlayer.getHandle();

                ClientboundPlayerInfoRemovePacket removePlayer = new ClientboundPlayerInfoRemovePacket(List.of(entityPlayer.getUUID()));
                ClientboundPlayerInfoUpdatePacket addPlayer = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer));
                //noinspection resource
                ServerLevel world = entityPlayer.serverLevel();
                ServerPlayerGameMode gamemode = entityPlayer.gameMode;

                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                        new CommonPlayerSpawnInfo(
                                world.dimensionTypeId(),
                                world.dimension(),
                                BiomeManager.obfuscateSeed(world.getSeed()),
                                gamemode.getGameModeForPlayer(),
                                gamemode.getPreviousGameModeForPlayer(),
                                world.isDebug(),
                                world.isFlat(),
                                entityPlayer.getLastDeathLocation(),
                                entityPlayer.getPortalCooldown()
                        ),
                        (byte) 3
                );
                Location l = player.getLocation();
                ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);
                ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

                sendPacket(entityPlayer, removePlayer);
                sendPacket(entityPlayer, addPlayer);

                sendPacket(entityPlayer, respawn);

                entityPlayer.onUpdateAbilities();

                sendPacket(entityPlayer, pos);
                sendPacket(entityPlayer, slot);
                craftPlayer.updateScaledHealth();
                //noinspection UnstableApiUsage
                player.updateInventory();
                entityPlayer.resetSentInfo();
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public void applySkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            Player player = playerObject.player();
            player.hidePlayer(skinOverlay.getSkinOverlay().plugin(), player);
            player.showPlayer(skinOverlay.getSkinOverlay().plugin(), player);
            skinOverlay.getSkinHandler().updateSkin(playerObject, skin).handleAsync((aBoolean, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return aBoolean;
            }).thenAccept(aBoolean -> SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                if (aBoolean)
                    skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                        Player p = playerObjects.player();
                        p.hidePlayer(skinOverlay.getSkinOverlay().plugin(), player);
                        p.showPlayer(skinOverlay.getSkinOverlay().plugin(), player);
                    });
            }));
        }, 20L);
    }

    @Override
    public GameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) {
        Player player = playerObject.player();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.getGameProfile();
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
        gameProfile.getProperties().forEach((s, property) -> propertyObjectMap.append(s, new SProperty(property.name(), property.value(), property.signature())));
        return new SGameProfileMojang(gameProfile.getName(), gameProfile.getId(), propertyObjectMap);
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

