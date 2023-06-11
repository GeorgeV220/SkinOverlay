package com.georgev22.skinoverlay.handler;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.ImageSupplier;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineskin.MineskinClient;
import org.mineskin.data.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public abstract class SkinHandler {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    protected final ObjectMap<PlayerObject, SGameProfile> sGameProfiles = new HashObjectMap<>();

    protected final MineskinClient mineskinClient;

    public SkinHandler() {
        mineskinClient = (OptionsUtil.MINESKIN_API_KEY.getStringValue().equalsIgnoreCase("none") ||
                OptionsUtil.MINESKIN_API_KEY.getStringValue().isBlank()) ?
                new MineskinClient("SkinOverlay") :
                new MineskinClient("SkinOverlay", OptionsUtil.MINESKIN_API_KEY.getStringValue());
    }

    /**
     * Update the skin for the specified {@link PlayerObject}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skin         Skin
     */
    public abstract CompletableFuture<Boolean> updateSkin(
            @NotNull final PlayerObject playerObject,
            @NotNull final Skin skin);

    public abstract void applySkin(@NotNull final PlayerObject playerObject, @NotNull final Skin skin);

    /**
     * Retrieves {@link PlayerObject}'s internal game profile
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s internal game profile
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public abstract Object getInternalGameProfile(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

    /**
     * Retrieves {@link PlayerObject}'s {@link SGameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link SGameProfile}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public abstract SGameProfile getGameProfile(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

    public CompletableFuture<Skin> retrieveOrGenerateSkin(@NotNull PlayerObject playerObject, @Nullable ImageSupplier imageSupplier, @NotNull SkinOptions skinOptions) {
        if (skinOptions == null) {
            throw new SkinException("SkinOptions cannot be null");
        }

        UUID skinUUID = Utilities.generateUUID(skinOptions.getSkinName() + playerObject.playerUUID().toString());
        return skinOverlay.getSkinManager().exists(skinUUID).thenApply(result -> {
            if (result) {
                skinOverlay.getLogger().info("Cached skin: " + skinUUID);
                return skinOverlay.getSkinManager().getEntity(skinUUID).join();
            } else {
                try {
                    skinOverlay.getLogger().info("New skin: " + skinUUID);
                    if (imageSupplier == null) {
                        throw new SkinException("ImageSupplier cannot be null");
                    }

                    Image overlay = imageSupplier.get();

                    byte[] profileBytes = getProfileBytes(playerObject, null);

                    JsonElement json = JsonParser.parseString(new String(profileBytes));
                    JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
                    JsonElement obj = null;
                    for (JsonElement object : properties) {
                        if (!object.getAsJsonObject().get("name").getAsString().equals("textures")) continue;
                        obj = object;
                        break;
                    }

                    if (obj == null) {
                        throw new SkinException("Property object cannot be null");
                    }

                    String base64 = obj.getAsJsonObject().get("value").getAsString();
                    String value = new String(Base64.getDecoder().decode(base64));
                    JsonElement textureJson = JsonParser.parseString(value);
                    String skinUrl = textureJson.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                    BufferedImage bufferedImage = ImageIO.read(new URL(skinUrl));
                    BufferedImage image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 2);
                    Graphics2D canvas = image.createGraphics();
                    canvas.drawImage(bufferedImage, 0, 0, null);
                    canvas.drawImage(overlay, 0, 0, null);
                    canvas.dispose();
                    Texture texture = mineskinClient.generateUpload(image).get().data.texture;
                    if (texture == null) {
                        throw new SkinException("Texture cannot be null");
                    }
                    SProperty property = new SProperty("textures", texture.value, texture.signature);
                    Skin skin = new Skin(skinUUID, property, skinOptions);
                    if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                        return skin;
                    }
                    skinOverlay.getSkinManager().save(skin);
                    return skin;
                } catch (IOException | ExecutionException | InterruptedException | RuntimeException exception) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error generating or retrieving the skin:", exception);
                    return null;
                }
            }
        });
    }

    public void setSkin(@NotNull PlayerObject playerObject, Skin skin) {
        skinOverlay.getUserManager().getEntity(playerObject.playerUUID()).
                handle((user, throwable) -> {
                    if (throwable != null) {
                        skinOverlay.getLogger().log(Level.SEVERE, "Error while getting the user: ", throwable);
                        return null;
                    }
                    return user;
                }).thenAccept(user -> {
                    if (user != null) {
                        try {
                            SGameProfile gameProfile = getGameProfile(playerObject);
                            gameProfile.removeProperty("textures").addProperty("textures", skin.skinProperty());
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            skinOverlay.getLogger().log(Level.SEVERE, "Error while trying to apply new texture: ", e);
                            return;
                        }
                        user.addCustomData("skin", skin);
                        applySkin(playerObject, skin);
                        if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                            return;
                        }
                        skinOverlay.getUserManager().save(user);
                    }
                });
    }

    /**
     * Retrieves {@link PlayerObject}'s {@link SGameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link PlayerObject}'s {@link SGameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getProfileBytes(@NotNull final PlayerObject playerObject, @Nullable SProperty property) throws IOException, ExecutionException, InterruptedException {
        return playerObject.isBedrock() ? this.getBedrockProfileBytes(playerObject, property) : this.getJavaProfileBytes(playerObject, property);
    }

    /**
     * Retrieves Java {@link PlayerObject}'s {@link SGameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link PlayerObject}'s {@link SGameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getJavaProfileBytes(@NotNull final PlayerObject playerObject, @Nullable SProperty property) throws IOException, ExecutionException, InterruptedException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(playerObject, property)
                        .getAsJsonObject().toString().getBytes()).readAllBytes() :
                new Utilities.Request()
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
     * Retrieves Bedrock {@link PlayerObject}'s {@link SGameProfile} bytes
     *
     * @param playerObject {@link PlayerObject} object
     * @param property     If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link PlayerObject}'s {@link SGameProfile} bytes
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public byte[] getBedrockProfileBytes(@NotNull final PlayerObject playerObject, final SProperty property) throws IOException, ExecutionException, InterruptedException {
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
        final byte[] profileBytes = new Utilities.Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", this.getXUID(playerObject))).getRequest().finalizeRequest().getBytes();
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
     * Creates a JSON from a {@link SGameProfile} or {@link SProperty}
     *
     * @param playerObject {@link PlayerObject}'s object
     * @param property     If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return a JSON from a {@link SGameProfile} or {@link SProperty}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public JsonObject createJsonFromProperty(@NotNull final PlayerObject playerObject, @Nullable SProperty property) throws IOException, ExecutionException, InterruptedException {
        if (property == null)
            property = this.getGameProfile(playerObject).getProperties().get("textures");
        final JsonArray properties = new JsonArray();
        final JsonObject innerProperties = new JsonObject();
        innerProperties.add("name", new JsonPrimitive("textures"));
        innerProperties.add("value", new JsonPrimitive(property.value()));
        innerProperties.add("signature", new JsonPrimitive(property.signature()));
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
        Utilities.Request request = new Utilities.Request().openConnection(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", playerObject.playerName().replace(".", ""))).getRequest().finalizeRequest();
        final int httpCode = request.getHttpCode();
        if (httpCode != 200) {
            request = new Utilities.Request()
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
        Utilities.Request request;
        try {
            request = new Utilities.Request().openConnection(String.format("https://api.minetools.eu/uuid/%s", playerName)).getRequest().finalizeRequest();
        } catch (IOException ioException) {
            request = new Utilities.Request().openConnection(String.format("https://api.mojang.com/users/profiles/minecraft/%s", playerName)).getRequest().finalizeRequest();
        }

        final byte[] jsonBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(jsonBytes));
        return UUID.fromString(json.getAsJsonObject().get("id").getAsString().replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Bedrock Player
     *
     * @param xuid Player's XUID (check {@link #getXUID(PlayerObject)})
     * @return the Skin {@link SProperty} for the specified Bedrock Player
     * @throws IOException When an I/O exception of some sort has occurred.
     */
    public SProperty getXUIDSkin(final String xuid) throws IOException {
        final Utilities.Request profileBytes = new Utilities.Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
        final JsonElement json = JsonParser.parseString(new String(profileBytes.getBytes()));
        return new SProperty("textures", json.getAsJsonObject().get("value").getAsString(), json.getAsJsonObject().get("signature").getAsString());
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Java Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return the Skin {@link SProperty} for the specified Java Player
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public SProperty getJavaSkin(final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        if (skinOverlay.getSkinHook() != null && skinOverlay.getSkinHook().getProperty(playerObject) != null) {
            return skinOverlay.getSkinHook().getProperty(playerObject);
        }
        return skinOverlay.getDefaultSkinHook().getProperty(playerObject);
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Player
     *
     * @param playerObject {@link PlayerObject}'s object
     * @return the Skin {@link SProperty} for the specified {@link PlayerObject}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public SProperty getSkin(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
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
}
