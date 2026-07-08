package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.providers.GameProfileProvider;
import com.georgev22.skinoverlay.refreshers.SkinRefresher;
import com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/**
 * Represents a supported Minecraft version range and the components that should
 * be used for that range.
 *
 * <p>A {@code VersionRange} defines the minimum and maximum Minecraft versions
 * supported by a specific {@link GameProfileProvider} and {@link SkinRefresher}.
 * The providers are created lazily using {@link Supplier} instances, allowing
 * implementations to be initialized only when required.</p>
 *
 * @param min       the minimum supported Minecraft version (inclusive)
 * @param max       the maximum supported Minecraft version (inclusive)
 * @param provider  supplier that creates the {@link GameProfileProvider} for this range
 * @param refresher supplier that creates the {@link SkinRefresher} for this range
 */
public record VersionRange(
        MinecraftVersion min,
        MinecraftVersion max,
        Supplier<GameProfileProvider> provider,
        Supplier<SkinRefresher> refresher
) {

    /**
     * Creates a version range that only matches a single Minecraft version.
     *
     * @param version   the Minecraft version supported by this range
     * @param provider  supplier that creates the {@link GameProfileProvider} for this version
     * @param refresher supplier that creates the {@link SkinRefresher} for this version
     */
    public VersionRange(MinecraftVersion version, Supplier<GameProfileProvider> provider, Supplier<SkinRefresher> refresher) {
        this(version, version, provider, refresher);
    }

    /**
     * Checks whether the specified Minecraft version is contained within this range.
     *
     * <p>The comparison is inclusive, meaning that versions equal to {@link #min()}
     * or {@link #max()} will match.</p>
     *
     * @param v the Minecraft version to check
     * @return {@code true} if the version is within this range, otherwise {@code false}
     */
    public boolean matches(@NonNull MinecraftVersion v) {
        return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
    }
}