package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.providers.GameProfileProvider;
import com.georgev22.skinoverlay.refreshers.SkinRefresher;
import com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public record VersionRange(
        MinecraftVersion min,
        MinecraftVersion max,
        Supplier<GameProfileProvider> provider,
        Supplier<SkinRefresher> refresher
) {
    public boolean matches(@NonNull MinecraftVersion v) {
        return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
    }
}