package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
     * @param playerObject the PlayerObject to retrieve skin property data for
     * @return the SProperty object containing the skin property data, or null if the skin data could not be retrieved
     * @throws IOException          if there is an IO error
     * @throws ExecutionException   if there is an Execution error
     * @throws InterruptedException if the thread is interrupted
     */
    @Override
    @Nullable
    public SProperty getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        try {
            Optional<SkinProperty> property = skinsRestorerAPI.getPlayerStorage().getSkinForPlayer(playerObject.playerUUID(), playerObject.playerName());
            return property.map(skinProperty -> new SProperty(SkinProperty.TEXTURES_NAME, skinProperty.getValue(), skinProperty.getSignature())).orElse(null);
        } catch (Exception exception) {
            return skinOverlay.getDefaultSkinHook().getProperty(playerObject);
        }
    }

}
