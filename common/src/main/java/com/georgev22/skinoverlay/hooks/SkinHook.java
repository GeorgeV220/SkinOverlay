package com.georgev22.skinoverlay.hooks;

import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.utilities.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The SkinHook interface defines the methods required for a skin hook.
 * A skin hook is responsible for retrieving the skin property for a given player object.
 */
public interface SkinHook extends Keyed {

    /**
     * Retrieves the SProperty for the given {@link SkinOverlayPlayer}.
     *
     * @param player The player object to retrieve the SProperty for.
     * @return The SProperty for the given {@link SkinOverlayPlayer}, or null if the property cannot be retrieved.
     */
    @Nullable SProperty getProperty(@NotNull SkinOverlayPlayer player);

    /**
     * Retrieves the simple name of the SkinHook (e.g. SkinsRestorer)
     *
     * @return The simple name of the SkinHook
     */
    String getName();

}
