package com.georgev22.skinoverlay.refreshers;

import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NoopSkinRefresher extends SkinRefresher {

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SkinOverlayPlayer player) {
        return CompletableFuture.completedFuture(true);
    }
}
