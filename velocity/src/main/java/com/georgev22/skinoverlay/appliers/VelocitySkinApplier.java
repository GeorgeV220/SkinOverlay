package com.georgev22.skinoverlay.appliers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class VelocitySkinApplier extends SkinApplier {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Override
    protected void applySkin(@NotNull SPlayer player, @NotNull Skin skin) {
        skinOverlay.getMessageManager().publishSkinProperty(player.getUniqueId(), skin);
    }

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.completedFuture(true);
    }
}
