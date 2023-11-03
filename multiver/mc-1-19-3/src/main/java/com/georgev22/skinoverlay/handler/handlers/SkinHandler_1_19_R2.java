
package com.georgev22.skinoverlay.handler.handlers;

import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.handler.SkinHandler;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.GameProfile;
import io.papermc.lib.PaperLib;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.georgev22.skinoverlay.handler.handlers.SkinHandler_Unsupported.wrapper;

public final class SkinHandler_1_19_R2 extends SkinHandler {

    @Override
    public CompletableFuture<Boolean> updateSkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
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
                ClientboundPlayerPositionPacket pos = new ClientboundPlayerPositionPacket(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0, false);
                ClientboundSetCarriedItemPacket slot = new ClientboundSetCarriedItemPacket(player.getInventory().getHeldItemSlot());

                sendPacket(entityPlayer, removePlayer);
                sendPacket(entityPlayer, addPlayer);

                sendPacket(entityPlayer, respawn);

                /*SynchedEntityData synchedEntityData = entityPlayer.getEntityData();

                EntityDataAccessor<Byte> entityDataAccessor;

                synchedEntityData.set(entityDataAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), skin.skinParts().getFlags());

                synchedEntityData.markDirty(entityDataAccessor);

                synchedEntityData.refresh(entityPlayer);*/

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
    public void applySkin(@NotNull PlayerObject playerObject, @NotNull Skin skin) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            Player player = (Player) playerObject.player();
            player.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            player.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
            skinOverlay.getSkinHandler().updateSkin(playerObject, skin).handleAsync((aBoolean, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return aBoolean;
            }).thenAccept(aBoolean -> SchedulerManager.getScheduler().runTask(skinOverlay.getClass(), () -> {
                if (aBoolean)
                    skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                        Player p = (Player) playerObjects.player();
                        p.hidePlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                        p.showPlayer((Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                    });
            }));
        }, 20L);
    }

    @Override
    public GameProfile getInternalGameProfile(@NotNull PlayerObject playerObject) {
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
                skinOverlay.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        return entityPlayer.gameProfile;
    }

    @Override
    public SGameProfile getGameProfile(@NotNull PlayerObject playerObject) {
        if (sGameProfiles.containsKey(playerObject)) {
            return sGameProfiles.get(playerObject);
        }
        return sGameProfiles.append(playerObject, wrapper(this.getInternalGameProfile(playerObject))).get(playerObject);
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}

