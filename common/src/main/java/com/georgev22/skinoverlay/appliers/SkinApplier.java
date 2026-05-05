package com.georgev22.skinoverlay.appliers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class SkinApplier {

    protected SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Apply the skin for the specified {@link SPlayer}
     *
     * @param player Player's {@link SPlayer} object.
     * @param skin   Skin
     */
    public void setSkin(@NotNull SPlayer player, @NotNull Skin skin) {
        SGameProfile gameProfile = skinOverlay.getGameProfileProvider().getGameProfile(player);
        gameProfile.setProperty("textures", skin.getProperty());
        skinOverlay.getGameProfileProvider().applyUpdatedGameProfile(player);
        applySkin(player, skin);

        Optional<EntityManager<PlayerData>> optionalPlayerDataEntityManager = EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
        if (optionalPlayerDataEntityManager.isEmpty()) {
            return;
        }
        EntityManager<PlayerData> playerDataEntityManager = optionalPlayerDataEntityManager.get();
        Optional<PlayerData> optionalPlayerData = playerDataEntityManager.findById(player.getUniqueId());
        if (optionalPlayerData.isEmpty()) {
            return;
        }
        PlayerData playerData = optionalPlayerData.get();
        playerData.setCurrentSkin(skin);

        playerDataEntityManager.save(playerData);
    }

    /**
     * Apply the skin for the specified {@link SPlayer}
     *
     * @param player Player's {@link SPlayer} object.
     */
    protected void applySkin(@NotNull final SPlayer player) {
    }

    /**
     * Apply the skin for the specified {@link SPlayer}
     *
     * @param player Player's {@link SPlayer} object.
     * @param skin   Skin
     */
    protected void applySkin(@NotNull final SPlayer player, @NotNull final Skin skin) {
        applySkin(player);
    }

    protected abstract @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player);

}
