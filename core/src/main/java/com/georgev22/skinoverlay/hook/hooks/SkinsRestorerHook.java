package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SkinsRestorerHook implements SkinHook {


    private final SkinsRestorerAPI skinsRestorerAPI;
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public SkinsRestorerHook() {
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
    }

    @Override
    @Nullable
    public SProperty getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        try {
            String skinName = skinsRestorerAPI.getSkinName(playerObject.playerName());
            if (skinName == null) {
                return null;
            }
            IProperty iProperty = skinsRestorerAPI.getSkinData(skinName);
            if (iProperty == null) {
                return null;
            }
            return new SProperty(iProperty.getName(), iProperty.getValue(), iProperty.getSignature());
        } catch (Exception exception) {
            return skinOverlay.getSkinHandler().getSkin(playerObject);
        }
    }


}
