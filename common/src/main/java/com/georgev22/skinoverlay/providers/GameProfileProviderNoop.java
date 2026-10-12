package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import org.jetbrains.annotations.NotNull;

public class GameProfileProviderNoop extends GameProfileProvider {

    @Override
    public Object getInternalGameProfile(@NotNull SkinOverlayPlayer player) {
        return new SGameProfile(player.getName(), player.getUniqueId());
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SkinOverlayPlayer player) {
        return new SGameProfile(player.getName(), player.getUniqueId());
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SkinOverlayPlayer player) {

    }
}
