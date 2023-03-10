package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.properties.Property;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import org.jetbrains.annotations.NotNull;

public class SkinsRestorerHook implements SkinHook {


    private final SkinsRestorerAPI skinsRestorerAPI;

    public SkinsRestorerHook() {
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
    }

    @Override
    public Property getProperty(@NotNull PlayerObject playerObject) {
        IProperty iProperty = skinsRestorerAPI.getSkinData(skinsRestorerAPI.getSkinName(playerObject.playerName()));
        if (iProperty == null) {
            throw new RuntimeException();
        }
        return new Property(iProperty.getName(), iProperty.getValue(), iProperty.getSignature());
    }


}
