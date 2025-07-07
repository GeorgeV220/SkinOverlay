package com.georgev22.skinoverlay.hooks;

import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The SkinHookNoop class implements the SkinHook interface and returns null for all property retrievals.
 */
public class SkinHookNoop implements SkinHook {
    @Override
    public @Nullable SProperty getProperty(@NotNull SPlayer player) {
        return null;
    }
}
