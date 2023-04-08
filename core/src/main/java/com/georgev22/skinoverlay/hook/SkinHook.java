package com.georgev22.skinoverlay.hook;

import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * The SkinHook interface defines the methods required for a skin hook.
 * A skin hook is responsible for retrieving the skin property for a given player object.
 */
public interface SkinHook {

    /**
     * Retrieves the SProperty for the given PlayerObject.
     *
     * @param playerObject The player object to retrieve the SProperty for.
     * @return The SProperty for the given PlayerObject, or null if the property cannot be retrieved.
     * @throws IOException          if an IO exception occurs while retrieving the property.
     * @throws ExecutionException   if an exception occurs while executing the property retrieval.
     * @throws InterruptedException if the current thread is interrupted while waiting for the retrieval to complete.
     */
    @Nullable SProperty getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

}
