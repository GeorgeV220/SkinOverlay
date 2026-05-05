package com.georgev22.skinoverlay.refreshers;

import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.player.SPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SkinRefresher_1_21_R4 extends SkinRefresher {

    @Override
    public void refresh(@NotNull SPlayer player) {
        this.skinOverlay.getScheduler().createDelayedTask(skinOverlay.getPlugin(), () -> {
            Player bukkitPlayer = player.getPlayer();
            bukkitPlayer.hidePlayer(skinOverlay.getPlugin(), bukkitPlayer);
            bukkitPlayer.showPlayer(skinOverlay.getPlugin(), bukkitPlayer);
            this.sendPackets(player).handleAsync((result, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error updating skin", throwable);
                    return false;
                }
                return result;
            }).thenAccept(result -> this.skinOverlay.getScheduler().runTask(skinOverlay.getPlugin(), () -> {
                if (result)
                    skinOverlay.getPlayerProvider().getOnlinePlayers().stream()
                            .filter(onlinePlayer -> onlinePlayer != player).forEach(onlinePlayer -> {
                                Player p = onlinePlayer.getPlayer();
                                p.hidePlayer(skinOverlay.getPlugin(), bukkitPlayer);
                                p.showPlayer(skinOverlay.getPlugin(), bukkitPlayer);
                            });
            }));
        }, 20L);
    }

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Player bukkitPlayer = player.getPlayer();
                final CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
                final ServerPlayer entityPlayer = craftPlayer.getHandle();

                ServerLevel world = entityPlayer.serverLevel();

                CommonPlayerSpawnInfo spawnInfo = entityPlayer.createCommonSpawnInfo(world);
                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                        spawnInfo,
                        ClientboundRespawnPacket.KEEP_ALL_DATA
                );

                sendPacket(entityPlayer, new ClientboundPlayerInfoRemovePacket(List.of(bukkitPlayer.getUniqueId())));
                sendPacket(entityPlayer, ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer)));

                sendPacket(entityPlayer, respawn);

                entityPlayer.onUpdateAbilities();

                entityPlayer.connection.teleport(bukkitPlayer.getLocation());

                entityPlayer.resetSentInfo();

                PlayerList playerList = entityPlayer.server.getPlayerList();
                playerList.sendPlayerPermissionLevel(entityPlayer);
                playerList.sendLevelInfo(entityPlayer, world);
                playerList.sendAllPlayerInfo(entityPlayer);

                for (MobEffectInstance effect : entityPlayer.getActiveEffects()) {
                    sendPacket(entityPlayer, new ClientboundUpdateMobEffectPacket(entityPlayer.getId(), effect, false));
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
