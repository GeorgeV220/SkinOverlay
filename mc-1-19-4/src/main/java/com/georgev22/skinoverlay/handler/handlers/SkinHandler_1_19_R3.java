
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
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
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.georgev22.skinoverlay.handler.handlers.SkinHandler_Unsupported.wrapper;

public class SkinHandler_1_19_R3 extends SkinHandler {
    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, SProperty property) {
        return this.updateSkin(playerObject, skinOptions);
    }

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions) {
        return CompletableFuture.supplyAsync(() -> {
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
                ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);
                ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

                sendPacket(entityPlayer, removePlayer);
                sendPacket(entityPlayer, addPlayer);

                sendPacket(entityPlayer, respawn);

                SynchedEntityData synchedEntityData = entityPlayer.getEntityData();

                EntityDataAccessor<Byte> entityDataAccessor;

                synchedEntityData.set(entityDataAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), skinOptions.getFlags());

                synchedEntityData.markDirty(entityDataAccessor);

                synchedEntityData.refresh(entityPlayer);

                entityPlayer.onUpdateAbilities();

                sendPacket(entityPlayer, pos);
                sendPacket(entityPlayer, slot);
                craftPlayer.updateScaledHealth();
                player.updateInventory();
                entityPlayer.resetSentInfo();
                return true;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public GameProfile getGameProfile0(@NotNull PlayerObject playerObject) {
        Player player = (Player) playerObject.player();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.getGameProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getGameProfile0(playerObject))).get(playerObject);
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    @Override
    protected void updateSkin0(UserManager.User user, PlayerObject playerObject, boolean forOthers) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            Player player = (Player) playerObject.player();
            player.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            player.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            try {
                skinOverlay.getSkinHandler().updateSkin(playerObject, Utilities.getSkinOptions(user.getCustomData("skinOptions"))).handleAsync((aBoolean, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return false;
                    }
                    return aBoolean;
                }).thenAccept(aBoolean -> SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                    if (aBoolean)
                        if (forOthers) {
                            skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                                Player p = (Player) playerObjects.player();
                                p.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                                p.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                            });
                        }
                }));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, 20L);
    }
}

