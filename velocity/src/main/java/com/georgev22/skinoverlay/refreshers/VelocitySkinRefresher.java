package com.georgev22.skinoverlay.refreshers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class VelocitySkinRefresher extends SkinRefresher {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Override
    public void refresh(@NotNull SkinOverlayPlayer player, @NotNull Skin skin) {
        skinOverlay.getMessageManager().publishSkinProperty(player.getUniqueId(), skin);
    }

    @Override
    protected @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SkinOverlayPlayer player) {
        return CompletableFuture.completedFuture(true);
    }
}
