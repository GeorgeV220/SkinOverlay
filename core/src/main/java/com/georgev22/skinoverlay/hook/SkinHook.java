package com.georgev22.skinoverlay.hook;

import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface SkinHook {

    @Nullable SProperty getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

}
