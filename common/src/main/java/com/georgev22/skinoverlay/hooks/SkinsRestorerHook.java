package com.georgev22.skinoverlay.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SProperty;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * SkinsRestorerHook class implements the SkinHook interface for handling skin property
 * data for players using the SkinsRestorer plugin.
 */
@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SkinsRestorerHook implements SkinHook {
    /**
     * SkinsRestorerAPI object for accessing the SkinsRestorer API.
     */
    private final SkinsRestorer skinsRestorerAPI;

    /**
     * SkinOverlay object for accessing the SkinOverlay plugin.
     */
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Constructor for SkinsRestorerHook class.
     * Initializes the SkinsRestorerAPI object.
     */
    public SkinsRestorerHook() {
        skinsRestorerAPI = SkinsRestorerProvider.get();
    }

    /**
     * Retrieves the skin property data for the given player using the SkinsRestorer plugin.
     * If the player does not have a skin set or the skin data cannot be retrieved,
     * the method returns null and the skin data is retrieved using SkinOverlay.
     *
     * @param player the {@link SPlayer} to retrieve skin property data for
     * @return the SProperty object containing the skin property data, or null if the skin data could not be retrieved
     */
    @Override
    @Nullable
    public SProperty getProperty(@NotNull SPlayer player) {
        Optional<SkinProperty> property;
        try {
            property = skinsRestorerAPI.getPlayerStorage().getSkinForPlayer(player.getUniqueId(), player.getName());
        } catch (DataRequestException e) {
            return null;
        }
        return property.map(skinProperty -> new SProperty("textures", skinProperty.getValue(), skinProperty.getSignature())).orElse(null);
    }

}
