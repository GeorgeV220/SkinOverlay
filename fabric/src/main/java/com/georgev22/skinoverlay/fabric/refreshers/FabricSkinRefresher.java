package com.georgev22.skinoverlay.fabric.refreshers;

import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.refreshers.SkinRefresher;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class FabricSkinRefresher extends SkinRefresher {

    @Override
    public void refresh(@NotNull SPlayer player) {
        this.skinOverlay.getScheduler().createDelayedTask(skinOverlay.getPlugin(), () -> {
            ServerPlayer serverPlayer = player.getPlayer();
            this.sendPackets(player).handleAsync((result, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return result;
            });
        }, 20L);
    }

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ServerPlayer serverPlayer = player.getPlayer();

                ServerLevel serverLevel = serverPlayer.level();

                CommonPlayerSpawnInfo spawnInfo = serverPlayer.createCommonSpawnInfo(serverLevel);
                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                        spawnInfo,
                        ClientboundRespawnPacket.KEEP_ALL_DATA
                );

                sendPacket(serverPlayer, new ClientboundPlayerInfoRemovePacket(List.of(serverPlayer.getUUID())));
                sendPacket(serverPlayer, ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(serverPlayer)));

                sendPacket(serverPlayer, respawn);

                serverPlayer.onUpdateAbilities();

                double x = serverPlayer.getX();
                double y = serverPlayer.getY();
                double z = serverPlayer.getZ();
                float yaw = serverPlayer.getYRot();
                float pitch = serverPlayer.getXRot();
                serverPlayer.connection.teleport(x, y, z, yaw, pitch);

                serverPlayer.resetSentInfo();

                PlayerList playerList = serverLevel.getServer().getPlayerList();
                playerList.sendPlayerPermissionLevel(serverPlayer);
                playerList.sendLevelInfo(serverPlayer, serverLevel);
                playerList.sendAllPlayerInfo(serverPlayer);

                for (MobEffectInstance effect : serverPlayer.getActiveEffects()) {
                    sendPacket(serverPlayer, new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), effect, false));
                }
                return true;
            } catch (Exception exception) {
                throw new SkinException(exception);
            }
        }, runnable -> this.skinOverlay.getScheduler().runTask(this.skinOverlay.getPlugin(), runnable));
    }

    private void sendPacket(@NotNull ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
}
