package com.georgev22.skinoverlay.hook;

import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface SkinHook {

    Property getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

}
