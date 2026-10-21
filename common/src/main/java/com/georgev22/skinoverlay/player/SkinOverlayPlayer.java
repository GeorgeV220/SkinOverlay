package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.storage.data.PlayerData;

import java.util.Optional;

/**
 * Represents a player supported by SkinOverlay, providing a platform-independent
 * abstraction over player implementations.
 *
 * <p>This interface exposes common player functionality while allowing access
 * to the underlying platform-specific player object, such as a Bukkit
 * {@code Player} or a Velocity {@code Player}.</p>
 */
public interface SkinOverlayPlayer extends CommandIssuer {

    /**
     * Retrieves the player data.
     *
     * @return an Optional containing the player data if available
     * , or an empty Optional if no player data is available
     */
    Optional<PlayerData> getPlayerData();

    /**
     * Checks whether this player is currently online.
     *
     * @return {@code true} if the player is online, {@code false} otherwise
     */
    boolean isOnline();

    /**
     * Gets the underlying platform-specific player object.
     *
     * <p>The returned object depends on the platform on which SkinOverlay
     * is running. For example, this may be a Bukkit {@code Player} or a
     * Velocity {@code Player}.</p>
     *
     * <p>The caller is responsible for ensuring that the returned object
     * is compatible with the expected platform and player type.</p>
     *
     * @param <T> the expected platform-specific player type
     * @return the underlying player object
     */
    <T> T getPlatformPlayer();

    /**
     * Checks whether this player is using Minecraft Bedrock Edition.
     *
     * <p>The default implementation identifies Bedrock players by checking
     * whether their UUID, without hyphens, starts with {@code 000000}.
     * This relies on the UUID convention used by the relevant Bedrock
     * integration and may not be reliable for every integration.</p>
     *
     * @return {@code true} if this player is identified as a Bedrock player,
     * {@code false} otherwise
     */
    default boolean isBedrockPlayer() {
        return this.getUniqueId().toString().replace("-", "").startsWith("000000");
    }

    /**
     * Gets the cached game profile associated with this player.
     *
     * <p>The profile is retrieved through SkinOverlay's game profile
     * provider. If no cached profile is available, the provider's
     * implementation determines how the profile is obtained.</p>
     *
     * @return the game profile associated with this player
     */
    default SGameProfile getGameProfile() {
        return SkinOverlay.getInstance()
                .getGameProfileProvider()
                .getCachedGameProfile(this);
    }
}
