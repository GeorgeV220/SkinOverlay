package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract provider responsible for handling {@link SGameProfile} retrieval,
 * caching, and application for {@link SPlayer} instances.
 */
public abstract class GameProfileProvider {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    protected final ObjectMap<SPlayer, SGameProfile> sGameProfiles = new HashObjectMap<>();

    /**
     * Retrieves the underlying platform-specific game profile object
     * associated with the given player.
     *
     * <p>This may return objects such as Mojang's {@code GameProfile}
     * depending on the platform implementation.</p>
     *
     * @param player the player whose internal profile is requested
     * @return the platform-specific game profile object (never {@code null})
     */
    public abstract Object getInternalGameProfile(@NotNull SPlayer player);

    /**
     * Retrieves the {@link SGameProfile} associated with the given player.
     *
     * <p>Implementations may fetch, construct, or convert the profile
     * from the platform-specific representation.</p>
     *
     * @param player the player whose profile is requested
     * @return the corresponding {@link SGameProfile} (never {@code null})
     */
    public abstract SGameProfile getGameProfile(@NotNull SPlayer player);

    /**
     * Retrieves a cached {@link SGameProfile} if available,
     * otherwise falls back to {@link #getGameProfile(SPlayer)}.
     *
     * <p>This method does not automatically store the result in the cache;
     * caching behavior should be handled by the implementation if needed.</p>
     *
     * @param player the player whose profile is requested
     * @return the cached or newly retrieved {@link SGameProfile}
     */
    public SGameProfile getCachedGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        return getGameProfile(player);
    }

    /**
     * Applies any updates made to the player's {@link SGameProfile}.
     *
     * <p>This typically involves updating the player's skin or profile data
     * on the client by sending the necessary packets or triggering a refresh
     * depending on the platform.</p>
     *
     * @param player the player whose updated profile should be applied
     */
    public abstract void applyUpdatedGameProfile(@NotNull SPlayer player);

}