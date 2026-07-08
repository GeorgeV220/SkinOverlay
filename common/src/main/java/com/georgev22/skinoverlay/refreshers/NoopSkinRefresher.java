package com.georgev22.skinoverlay.refreshers;

import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NoopSkinRefresher extends SkinRefresher {

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.completedFuture(true);
    }
}
