package com.georgev22.skinoverlay.hooks;

import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.utilities.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The SkinHookNoop class implements the SkinHook interface and returns null for all property retrievals.
 */
public class SkinHookNoop implements SkinHook {

    private static final NamespacedKey key = new NamespacedKey("skinoverlay", "noop");

    @Override
    public @Nullable SProperty getProperty(@NotNull SPlayer player) {
        return null;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public String getName() {
        return "None";
    }
}
