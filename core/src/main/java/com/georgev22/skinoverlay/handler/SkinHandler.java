package com.georgev22.skinoverlay.handler;

import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities.Request;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class SkinHandler {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    /**
     * Update the skin for the specified {@link PlayerObject}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options
     * @param callback     Callback
     */
    public abstract void updateSkin(
            @NotNull final PlayerObject playerObject,
            @NotNull final SkinOptions skinOptions,
            @NotNull final Utils.Callback<Boolean> callback);

    /**
     * Update the skin for the specified {@link PlayerObject} and {@link Property}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options)
     * @param property     {@link Property} to set
     * @param callback     Callback
     */
    public abstract void updateSkin(@NotNull final PlayerObject playerObject,
                                    @NotNull final SkinOptions skinOptions,
                                    final Property property,
                                    @NotNull final Utils.Callback<Boolean> callback);

    /**
     * Retrieves {@link PlayerObject}'s {@link GameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link GameProfile}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    protected abstract <T> GameProfile getGameProfile0(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

    /**
     * Retrieves {@link PlayerObject}'s {@link GameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link GameProfile}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public GameProfile getGameProfile(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        final GameProfile gameProfile = this.getGameProfile0(playerObject);
        if (!gameProfile.getProperties().containsKey("textures")) {
            gameProfile.getProperties().put("textures", this.getSkin(playerObject));
        }
        return gameProfile;
    }

    /**
     * Retrieves {@link PlayerObject}'s {@link GameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link Property} instead of {@link GameProfile} ones
     * @return {@link PlayerObject}'s {@link GameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getProfileBytes(@NotNull final PlayerObject playerObject, @Nullable Property property) throws IOException, ExecutionException, InterruptedException {
        return playerObject.isBedrock() ? this.getBedrockProfileBytes(playerObject, property) : this.getJavaProfileBytes(playerObject, property);
    }

    /**
     * Retrieves Java {@link PlayerObject}'s {@link GameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link Property} instead of {@link GameProfile} ones
     * @return {@link PlayerObject}'s {@link GameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getJavaProfileBytes(@NotNull final PlayerObject playerObject, @Nullable Property property) throws IOException, ExecutionException, InterruptedException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(playerObject, property)
                        .getAsJsonObject().toString().getBytes()).readAllBytes() :
                new Request()
                        .openConnection(
                                String.format(
                                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                        (skinOverlay.isOnlineMode() ?
                                                playerObject.playerUUID() :
                                                getUUID(playerObject.playerName()))
                                                .toString().replaceAll("-", "")))
                        .getRequest()
                        .finalizeRequest()
                        .getBytes();
    }

    /**
     * Retrieves Bedrock {@link PlayerObject}'s {@link GameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link Property} instead of {@link GameProfile} ones
     * @return {@link PlayerObject}'s {@link GameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getBedrockProfileBytes(@NotNull final PlayerObject playerObject, final Property property) throws IOException, ExecutionException, InterruptedException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(playerObject, property).getAsJsonObject().toString().getBytes()).readAllBytes() :
                new ByteArrayInputStream(this.createJsonForBedrock(playerObject).getAsJsonObject().toString().getBytes()).readAllBytes();
    }

    /**
     * Creates a similar JSON as the Java one for the specified Bedrock Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return a JSON for the specified Bedrock Player
     * @throws IOException When an I/O exception of some sort has occurred.
     */
    public JsonObject createJsonForBedrock(@NotNull final PlayerObject playerObject) throws IOException {
        final byte[] profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", this.getXUID(playerObject))).getRequest().finalizeRequest().getBytes();
        final JsonElement json = JsonParser.parseString(new String(profileBytes));
        final JsonElement value = json.getAsJsonObject().get("value");
        final JsonElement signature = json.getAsJsonObject().get("signature");
        final JsonArray properties = new JsonArray();
        final JsonObject innerProperties = new JsonObject();
        innerProperties.add("name", new JsonPrimitive("textures"));
        innerProperties.add("value", value);
        innerProperties.add("signature", signature);
        properties.add(innerProperties);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("properties", properties);
        return jsonObject;
    }

    /**
     * Creates a JSON from a {@link GameProfile} or {@link Property}
     *
     * @param playerObject {@link PlayerObject}'s object
     * @param property     If you want to use a {@link Property} instead of {@link GameProfile} ones
     * @return a JSON from a {@link GameProfile} or {@link Property}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public JsonObject createJsonFromProperty(@NotNull final PlayerObject playerObject, @Nullable Property property) throws IOException, ExecutionException, InterruptedException {
        if (property == null)
            property = this.getGameProfile(playerObject).getProperties().get("textures").iterator().next();
        final JsonArray properties = new JsonArray();
        final JsonObject innerProperties = new JsonObject();
        innerProperties.add("name", new JsonPrimitive("textures"));
        innerProperties.add("value", new JsonPrimitive(property.getValue()));
        innerProperties.add("signature", new JsonPrimitive(property.getSignature()));
        properties.add(innerProperties);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("properties", properties);
        return jsonObject;
    }

    /**
     * Retrieves the XUID for the specified Bedrock Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return the XUID for the specified Bedrock Player
     * @throws IOException When an I/O exception of some sort has occurred.
     */
    public String getXUID(@NotNull final PlayerObject playerObject) throws IOException {
        Request request = new Request().openConnection(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", playerObject.playerName().replace(".", ""))).getRequest().finalizeRequest();
        final int httpCode = request.getHttpCode();
        if (httpCode != 200) {
            request = new Request()
                    .openConnection(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", playerObject.playerName().replace(".", "").replace("_", "%20")))
                    .getRequest()
                    .finalizeRequest();
        }
        final byte[] profileBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(profileBytes));
        return json.getAsJsonObject().get("xuid").getAsString();
    }

    /**
     * Retrieves the UUID for the specified Java Player.
     * <p>
     * If the specified player is not a premium account, returns a default UUID (Steve).
     *
     * @param playerName The player's Minecraft username.
     * @return The UUID for the specified Java Player.
     * @throws IOException If an I/O exception of some sort has occurred.
     */
    public UUID getUUID(final String playerName) throws IOException {
        if (!isUsernamePremium(playerName)) {
            return UUID.fromString(OptionsUtil.DEFAULT_SKIN_UUID.getStringValue());
        }
        Request request;
        try {
            request = new Request().openConnection(String.format("https://api.minetools.eu/uuid/%s", playerName)).getRequest().finalizeRequest();
        } catch (IOException ioException) {
            request = new Request().openConnection(String.format("https://api.mojang.com/users/profiles/minecraft/%s", playerName)).getRequest().finalizeRequest();
        }

        final byte[] jsonBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(jsonBytes));
        return UUID.fromString(json.getAsJsonObject().get("id").getAsString().replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }

    /**
     * Retrieves the Skin {@link Property} for the specified Bedrock Player
     *
     * @param xuid Player's XUID (check {@link SkinHandler#getXUID(PlayerObject)})
     * @return the Skin {@link Property} for the specified Bedrock Player
     * @throws IOException When an I/O exception of some sort has occurred.
     */
    public Property getXUIDSkin(final String xuid) throws IOException {
        final Request profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
        final JsonElement json = JsonParser.parseString(new String(profileBytes.getBytes()));
        return new Property("textures", json.getAsJsonObject().get("value").getAsString(), json.getAsJsonObject().get("signature").getAsString());
    }

    /**
     * Retrieves the Skin {@link Property} for the specified Java Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return the Skin {@link Property} for the specified Java Player
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public Property getJavaSkin(final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        if (skinOverlay.getSkinHook() != null) {
            return skinOverlay.getSkinHook().getProperty(playerObject);
        }
        final JsonElement json = JsonParser.parseString(new String(this.getProfileBytes(playerObject, null)));
        final JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
        Property property = null;
        for (final JsonElement object : properties) {
            if (object.getAsJsonObject().get("name").getAsString().equals("textures")) {
                property = new Property("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString());
            }
        }
        return property;
    }

    /**
     * Retrieves the Skin {@link Property} for the specified Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return the Skin {@link Property} for the specified {@link PlayerObject}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public Property getSkin(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        return playerObject.isBedrock() ? this.getXUIDSkin(this.getXUID(playerObject)) : this.getJavaSkin(playerObject);
    }

    /**
     * This method checks if a Minecraft username is a premium account.
     * A premium account is one that has paid for the game.
     *
     * @param username The Minecraft username to check.
     * @return True if the username is a premium account, false otherwise.
     */
    public boolean isUsernamePremium(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
                return jsonObject != null && jsonObject.has("id") && jsonObject.has("name");
            } else {
                con.disconnect();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static class SkinHandler_ extends SkinHandler {

        @Override
        public void updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, final Utils.@NotNull Callback<Boolean> callback) {
            skinOverlay.getLogger().info("Unsupported Minecraft Version");
        }

        @Override
        public void updateSkin(@NotNull PlayerObject playerObject, @NotNull SkinOptions skinOptions, Property property, final Utils.@NotNull Callback<Boolean> callback) {
            updateSkin(playerObject, skinOptions, callback);
        }

        @Override
        protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
            GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
            if (!gameProfile.getProperties().containsKey("textures")) {
                gameProfile.getProperties().put("textures", this.getSkin(playerObject));
            }
            return gameProfile;
        }
    }
}
