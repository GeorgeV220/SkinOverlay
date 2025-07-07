package com.georgev22.skinoverlay.player;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.skin.SGameProfile;

public interface SPlayer extends CommandIssuer {

    boolean isOnline();

    <T> T getPlayer();

    /**
     * Checks if the player is using Bedrock Edition.
     *
     * @return true if the player is using Bedrock Edition, false otherwise.
     */
    default boolean isBedrock() {
        return this.getUniqueId().toString().replace("-", "").startsWith("000000");
    }

    default SGameProfile getGameProfile() {
        return SkinOverlay.getInstance().getGameProfileProvider().getCachedGameProfile(this);
    }
}
