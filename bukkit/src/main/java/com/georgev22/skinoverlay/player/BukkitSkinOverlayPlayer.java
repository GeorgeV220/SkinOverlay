package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.command.BukkitCommandIssuer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BukkitSkinOverlayPlayer extends BukkitCommandIssuer implements SkinOverlayPlayer {

    private final Player player;

    private PlayerData playerData;

    public BukkitSkinOverlayPlayer(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean isOnline() {
        return this.player.isOnline();
    }

    @Override
    public <T> T getPlatformPlayer() {
        //noinspection unchecked
        return (T) this.player;
    }

    @Override
    public Optional<PlayerData> getPlayerData() {
        if (playerData == null) {
            Optional<EntityManager<PlayerData>> playerDataEntityManager =
                    EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
            if (playerDataEntityManager.isEmpty()) return Optional.empty();
            Optional<PlayerData> playerDataOptional = playerDataEntityManager.get().findById(this.player.getUniqueId());
            if (playerDataOptional.isEmpty()) return Optional.empty();
            this.playerData = playerDataOptional.get();
        }
        return Optional.of(playerData);
    }
}
