package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import org.jetbrains.annotations.NotNull;

public abstract class GameProfileProvider {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    protected final ObjectMap<SPlayer, SGameProfile> sGameProfiles = new HashObjectMap<>();

    public abstract Object getInternalGameProfile(@NotNull SPlayer player);

    public abstract SGameProfile getGameProfile(@NotNull SPlayer player);

    public SGameProfile getCachedGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        return getGameProfile(player);
    }

    public abstract void applyUpdatedGameProfile(@NotNull SPlayer player);

}
