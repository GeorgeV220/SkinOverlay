package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import org.jetbrains.annotations.NotNull;

public class GameProfileProviderNoop extends GameProfileProvider {

    @Override
    public Object getInternalGameProfile(@NotNull SPlayer player) {
        return new SGameProfile(player.getName(), player.getUniqueId());
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        return new SGameProfile(player.getName(), player.getUniqueId());
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {

    }
}
