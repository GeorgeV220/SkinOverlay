package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.skin.Overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * Applies persistent overlay loadouts through the platform's skin provider and entity scheduler.
 * <p>
 * Mutating operations execute in submission order for each player, including resets. Rendering and
 * generation run on worker threads; skin application runs through the player's entity scheduler.
 * Returned futures complete after application and the player-data save request, which may be batched
 * by the configured storage backend. Validation and generation failures leave the loadout unchanged
 * and do not prevent subsequent queued operations from running.
 * </p>
 * <p>
 * Combinations are expanded to source identifiers before saving the loadout. Keep source overlays
 * registered, and save runtime sources through the overlay registry if they must survive a restart.
 * Callers should handle exceptional completion without blocking a server thread on the future.
 * Cancelling a returned future does not cancel an already queued operation.
 * </p>
 */
public final class OverlayProvider {
    private final SkinOverlay plugin;
    private final Map<UUID, CompletableFuture<Void>> pending = new HashMap<>();

    /**
     * Creates a loadout provider for an initialized plugin instance.
     *
     * @param plugin plugin supplying the overlay registry, skin provider and platform scheduler
     */
    public OverlayProvider(SkinOverlay plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
    }

    /**
     * Replaces the player's equipped overlays, or restores the default skin for an empty collection.
     * <p>
     * The input collection is copied at submission time. Duplicate source identifiers are applied
     * once, while combinations are expanded and validated when this request reaches the queue head.
     * </p>
     *
     * @param player   online player whose loadout should change
     * @param overlays replacement source overlays or combinations; may be empty
     * @return a future containing the applied signed skin, or completing exceptionally if validation,
     * generation or application fails
     */
    public CompletableFuture<Skin> wear(SkinOverlayPlayer player, Collection<Overlay> overlays) {
        List<Overlay> requested = List.copyOf(overlays);
        return submit(player, () -> apply(player, requested));
    }

    /**
     * Adds overlays to the loadout present when this request reaches the queue head.
     * <p>
     * Repeated source identifiers are deduplicated. Different overlays occupying the same
     * type and layer are rejected rather than replacing an equipped piece implicitly.
     * </p>
     *
     * @param player   online player whose loadout should change
     * @param overlays source overlays or combinations to add
     * @return a future containing the applied signed skin, or completing exceptionally if sources
     * cannot be resolved, overlays conflict, or generation or application fails
     */
    public CompletableFuture<Skin> add(SkinOverlayPlayer player, Collection<Overlay> overlays) {
        List<Overlay> requested = List.copyOf(overlays);
        return submit(player, () -> {
            List<Overlay> combined = new ArrayList<>(equipped(player));
            combined.addAll(requested);
            return apply(player, combined);
        });
    }

    /**
     * Removes individual pieces or a named combination's source pieces from the queued loadout.
     * <p>
     * Identifiers may be model-qualified names or unqualified keys of equipped pieces. A named
     * combination is resolved through the registry and expanded before removal. Removing the
     * last piece restores the default skin; other removals recompose the remaining pieces
     * over the original skin. Removing no equipped pieces is an error.
     * </p>
     *
     * @param player      online player whose loadout should change
     * @param identifiers non-empty collection of piece or combination identifiers to remove
     * @return a future containing the applied signed skin, or completing exceptionally if identifiers
     * cannot be resolved, nothing is removed, or generation or application fails
     */
    public CompletableFuture<Skin> remove(SkinOverlayPlayer player, Collection<String> identifiers) {
        List<String> requested = List.copyOf(identifiers);
        return submit(player, () -> {
            if (requested.isEmpty()) throw new IllegalArgumentException("Specify an overlay to remove");
            List<Overlay> current = equipped(player);
            Set<String> removed = new HashSet<>();
            for (String identifier : requested) {
                List<Overlay> matches = current.stream().filter(overlay -> overlay.identifier().equals(identifier) || overlay.key().equals(identifier)).toList();
                if (!matches.isEmpty()) {
                    matches.forEach(overlay -> removed.add(overlay.identifier()));
                    continue;
                }
                Overlay overlay = plugin.getOverlayRegistry().require(identifier);
                plugin.getOverlayRegistry().flatten(List.of(overlay)).forEach(leaf -> removed.add(leaf.identifier()));
            }
            List<Overlay> remaining = current.stream().filter(overlay -> !removed.contains(overlay.identifier())).toList();
            if (remaining.size() == current.size())
                throw new IllegalArgumentException("None of those overlays are equipped");
            return apply(player, remaining);
        });
    }

    /**
     * Queues restoration of the player's default skin and removal of all equipped identifiers.
     *
     * @param player online player whose loadout should be cleared
     * @return a future containing the restored default skin, or completing exceptionally if the
     * default skin is unavailable or application fails
     */
    public CompletableFuture<Skin> clear(SkinOverlayPlayer player) {
        return wear(player, List.of());
    }

    /**
     * Resolves the player's currently recorded loadout to its source overlay instances.
     * <p>
     * This synchronous read does not wait for queued mutations. The returned list is immutable;
     * its overlay instances are the mutable instances held by the registry.
     * </p>
     *
     * @param player player whose loaded data should be read
     * @return an immutable list of equipped source overlays in their recorded order
     * @throws IllegalStateException    if the player's data is not loaded
     * @throws IllegalArgumentException if an equipped source identifier is missing from the registry
     */
    public List<Overlay> equipped(SkinOverlayPlayer player) {
        return data(player).getEquippedOverlays().stream().map(plugin.getOverlayRegistry()::require).toList();
    }

    private PlayerData data(SkinOverlayPlayer player) {
        return player.getPlayerData().orElseThrow(() -> new IllegalStateException("Player data is not loaded for " + player.getName()));
    }

    private CompletableFuture<Skin> apply(SkinOverlayPlayer player, List<Overlay> requested) {
        try {
            List<Overlay> leaves = requested.isEmpty() ? List.of() : plugin.getOverlayRegistry().flatten(requested);
            List<String> identifiers = leaves.stream().map(Overlay::identifier).toList();
            CompletableFuture<Skin> generated;
            if (leaves.isEmpty()) {
                generated = CompletableFuture.completedFuture(Objects.requireNonNull(data(player).getDefaultSkin(), "Default skin is not loaded"));
            } else {
                Overlay overlay = plugin.getOverlayRegistry().generate(leaves);
                generated = plugin.getSkinProvider().retrieveOrGenerateSkin(player, overlay)
                        .thenApply(skin -> skin.orElseThrow(() -> new IllegalStateException("Failed to retrieve or generate skin for " + player.getName())));
            }
            return generated.thenCompose(skin -> {
                CompletableFuture<Skin> applied = new CompletableFuture<>();
                plugin.getScheduler().createTaskForEntity(plugin.getPlugin(), () -> {
                    try {
                        if (!player.isOnline())
                            throw new IllegalStateException("Player " + player.getName() + " is offline");
                        plugin.getSkinProvider().setSkin(player, skin, identifiers);
                        applied.complete(skin);
                    } catch (Exception exception) {
                        applied.completeExceptionally(exception);
                    }
                }, () -> applied.completeExceptionally(new IllegalStateException("Player " + player.getName() + " is offline")), player.getPlatformPlayer());
                return applied;
            });
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    private CompletableFuture<Skin> submit(SkinOverlayPlayer player, Supplier<CompletableFuture<Skin>> operation) {
        CompletableFuture<Skin> result = new CompletableFuture<>();
        CompletableFuture<Void> tail = new CompletableFuture<>();
        CompletableFuture<Void> previous;
        synchronized (pending) {
            previous = pending.put(player.getUniqueId(), tail);
        }
        (previous == null ? CompletableFuture.<Void>completedFuture(null) : previous)
                .thenComposeAsync(ignored -> operation.get())
                .whenComplete((skin, error) -> {
                    if (error == null) result.complete(skin);
                    else
                        result.completeExceptionally(error instanceof CompletionException && error.getCause() != null ? error.getCause() : error);
                    synchronized (pending) {
                        pending.remove(player.getUniqueId(), tail);
                    }
                    tail.complete(null);
                });
        return result;
    }
}
