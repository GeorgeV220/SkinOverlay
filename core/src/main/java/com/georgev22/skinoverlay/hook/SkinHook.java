package com.georgev22.skinoverlay.hook;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SkinHook {

    @Nullable Property getProperty(@NotNull PlayerObject playerObject);

}
