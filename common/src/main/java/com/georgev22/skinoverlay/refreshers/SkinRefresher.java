package com.georgev22.skinoverlay.refreshers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Responsible for refreshing a player's client state after their {@link SGameProfile}
 * has been modified (e.g., skin/texture changes).
 *
 * <p>This class does <b>not</b> modify the player's skin data directly. Instead,
 * it ensures that the updated profile is properly reflected on the client by
 * triggering the necessary updates such as packet sending, respawning, or
 * visibility refreshes.</p>
 *
 * <p>Implementations are typically version-specific and may use platform-dependent
 * mechanisms (e.g., NMS packets) to force the client to reload the player's appearance.</p>
 */
public abstract class SkinRefresher {

    protected SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Triggers a client-side refresh for the given {@link SPlayer}.
     *
     * <p>This method should ensure that any previously applied changes to the player's
     * {@link SGameProfile} (such as skin updates) become visible to the player and
     * other clients.</p>
     *
     * @param player the player whose client state should be refreshed
     */
    public void refresh(@NotNull final SPlayer player) {
    }

    /**
     * Triggers a client-side refresh for the given {@link SPlayer} after a specific
     * {@link Skin} has been applied.
     *
     * <p>This overload allows implementations to optionally use the provided
     * {@link Skin} for additional logic during the refresh process.</p>
     *
     * @param player the player whose client state should be refreshed
     * @param skin   the skin that was applied prior to the refresh
     */
    public void refresh(@NotNull final SPlayer player, @NotNull final Skin skin) {
        refresh(player);
    }

    /**
     * Sends the necessary packets to the client to force a visual update of the player's
     * appearance.
     *
     * <p>This is typically the core mechanism used to synchronize the updated
     * {@link SGameProfile} with the client.</p>
     *
     * @param player the player whose client should receive the update packets
     * @return a {@link CompletableFuture} that completes with {@code true} if the
     * update was successful, or {@code false} otherwise
     */
    protected abstract @NotNull CompletableFuture<Boolean> sendPackets(@NotNull SPlayer player);

}
