package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.properties.Property;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkinsRestorerHook implements SkinHook {


    private final SkinsRestorerAPI skinsRestorerAPI;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SkinsRestorerHook() {
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
    }

    @Override
    @Nullable
    public Property getProperty(@NotNull PlayerObject playerObject) {
        String skinName = skinsRestorerAPI.getSkinName(playerObject.playerName());
        if (skinName == null) {
            return null;
        }
        IProperty iProperty = skinsRestorerAPI.getSkinData(skinName);
        if (iProperty == null) {
            return null;
        }
        return new Property(iProperty.getName(), iProperty.getValue(), iProperty.getSignature());
    }


}
