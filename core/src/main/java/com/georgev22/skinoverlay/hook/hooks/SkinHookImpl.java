package com.georgev22.skinoverlay.hook.hooks;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.hook.SkinHook;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SkinHookImpl implements SkinHook {


    /**
     * Retrieves the SProperty for the given PlayerObject.
     *
     * @param playerObject The player object to retrieve the SProperty for.
     * @return The SProperty for the given PlayerObject, or null if the property cannot be retrieved.
     * @throws IOException          if an IO exception occurs while retrieving the property.
     * @throws ExecutionException   if an exception occurs while executing the property retrieval.
     * @throws InterruptedException if the current thread is interrupted while waiting for the retrieval to complete.
     */
    @Override
    public @Nullable SProperty getProperty(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        final JsonElement json = JsonParser.parseString(new String(SkinOverlay.getInstance().getSkinHandler().getProfileBytes(playerObject, null)));
        final JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
        SProperty property = null;
        for (final JsonElement object : properties) {
            if (object.getAsJsonObject().get("name").getAsString().equals("textures")) {
                property = new SProperty("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString());
            }
        }
        return property;
    }
}
