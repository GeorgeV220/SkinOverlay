package com.georgev22.skinoverlay.hooks;

import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The SkinHook interface defines the methods required for a skin hook.
 * A skin hook is responsible for retrieving the skin property for a given player object.
 */
public interface SkinHook {

    /**
     * Retrieves the SProperty for the given {@link SPlayer}.
     *
     * @param player The player object to retrieve the SProperty for.
     * @return The SProperty for the given {@link SPlayer}, or null if the property cannot be retrieved.
     */
    @Nullable SProperty getProperty(@NotNull SPlayer player);

}
