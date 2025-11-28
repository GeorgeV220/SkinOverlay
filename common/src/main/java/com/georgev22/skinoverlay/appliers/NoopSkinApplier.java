package com.georgev22.skinoverlay.appliers;

import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NoopSkinApplier extends SkinApplier {

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player) {
        return CompletableFuture.completedFuture(true);
    }
}
