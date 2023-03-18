package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.properties.Property;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SkinsRestorerHook implements SkinHook {


    private final SkinsRestorerAPI skinsRestorerAPI;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SkinsRestorerHook() {
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
    }

    @Override
    public Property getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        String skinName = skinsRestorerAPI.getSkinName(playerObject.playerName());
        IProperty iProperty = skinsRestorerAPI.getSkinData(skinName);
        if (skinName == null | iProperty == null) {
            return playerObject.gameProfile().getProperties().get("textures").stream().filter(property -> property.getName().equalsIgnoreCase("textures")).findFirst().orElse(skinOverlay.getSkinHandler().getSkin(playerObject));
        }
        return new Property(iProperty.getName(), iProperty.getValue(), iProperty.getSignature());
    }


}
