package com.georgev22.skinoverlay.handler;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.skin.PlayerObjectUpdateSkinEvent;
import com.georgev22.skinoverlay.utilities.MessagesUtil;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.Utilities.Request;
import com.georgev22.skinoverlay.utilities.interfaces.ImageSupplier;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.*;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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

    /**
     * Update the skin for the specified {@link PlayerObject}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options
     */
    public abstract CompletableFuture<Boolean> updateSkin(
            @NotNull final PlayerObject playerObject,
            @NotNull final SkinOptions skinOptions);

    /**
     * Update the skin for the specified {@link PlayerObject} and {@link SProperty}
     *
     * @param playerObject Player's {@link PlayerObject} object.
     * @param skinOptions  Skin options)
     * @param property     {@link SProperty} to set
     */
    public abstract CompletableFuture<Boolean> updateSkin(@NotNull final PlayerObject playerObject,
                                                          @NotNull final SkinOptions skinOptions,
                                                          final SProperty property);

    /**
     * Retrieves {@link PlayerObject}'s {@link SGameProfile}
     *
     * @param playerObject {@link PlayerObject} object
     * @return {@link PlayerObject}'s {@link SGameProfile}
     * @throws IOException          When an I/O exception of some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public abstract Object getGameProfile0(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

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

    public void setSkin(SkinOptions skinOptions, @NotNull PlayerObject playerObject, String @NotNull [] properties) {
        Validate.isTrue((properties.length == 3 ? 1 : 0) != 0, "Properties length must be 3");
        skinOverlay.getUserManager().getUser(playerObject.playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            return user;
        }).thenAccept(user -> {
            if (user == null) {
                skinOverlay.getLogger().log(Level.SEVERE, "User is null");
                return;
            }
            try {
                user.addCustomData("skinOptions", Utilities.skinOptionsToBytes(skinOptions));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.addCustomData("skinProperty", new SProperty(properties[0], properties[1], properties[2]));
            updateSkin(playerObject, true);
            if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                return;
            }
            skinOverlay.getUserManager().save(user);
        });
    }

    public void setSkin(ImageSupplier imageSupplier, SkinOptions skinOptions, @NotNull PlayerObject playerObject, @Nullable CommandIssuer commandIssuer) {
        skinOverlay.getUserManager().getUser(playerObject.playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            return user;
        }).thenApplyAsync(user -> {
            if (user == null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user");
                return null;
            }
            Image overlay;
            try {
                overlay = imageSupplier.get();
            } catch (IOException e) {
                overlay = null;
            }
            try {
                byte[] profileBytes = getProfileBytes(playerObject, user.getCustomData("defaultSkinProperty"));
                JsonElement json = JsonParser.parseString(new String(profileBytes));
                JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
                for (JsonElement object : properties) {
                    if (!object.getAsJsonObject().get("name").getAsString().equals("textures")) continue;
                    if (overlay == null) {
                        SGameProfile gameProfile = getGameProfile(playerObject);
                        gameProfile.removeProperty("textures");
                        gameProfile.addProperty("textures", new SProperty("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString()));
                        user.addCustomData("skinOptions", Utilities.skinOptionsToBytes(skinOptions));
                        user.addCustomData("skinProperty", new SProperty("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString()));
                        if (commandIssuer == null) {
                            MessagesUtil.RESET.msgConsole(new HashObjectMap<String, String>().append("%player%", playerObject.playerName()), true);
                        } else {
                            MessagesUtil.RESET.msg(commandIssuer, new HashObjectMap<String, String>().append("%player%", playerObject.playerName()), true);
                        }
                        break;
                    }
                    String base64 = object.getAsJsonObject().get("value").getAsString();
                    String value = new String(Base64.getDecoder().decode(base64));
                    JsonElement textureJson = JsonParser.parseString(value);
                    String skinUrl = textureJson.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                    BufferedImage skin = ImageIO.read(new URL(skinUrl));
                    BufferedImage image = new BufferedImage(skin.getWidth(), skin.getHeight(), 2);
                    Graphics2D canvas = image.createGraphics();
                    canvas.drawImage(skin, 0, 0, null);
                    canvas.drawImage(overlay, 0, 0, null);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(image, "PNG", stream);
                    canvas.dispose();
                    String boundary = "*****";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    Request request = new Request()
                            .openConnection("https://api.mineskin.org/generate/upload?visibility=1")
                            .postRequest()
                            .setRequestProperty("Content-Type", ("multipart/form-data;boundary=" + boundary))
                            .writeToOutputStream(twoHyphens + boundary + crlf, "Content-Disposition: form-data; name=\"file\";filename=\"file.png\"" + crlf, crlf)
                            .writeToOutputStream(new byte[][]{stream.toByteArray()}).writeToOutputStream(crlf, twoHyphens + boundary + twoHyphens + crlf)
                            .closeOutputStream()
                            .finalizeRequest();
                    switch (request.getHttpCode()) {
                        case 429 -> skinOverlay.getLogger().log(Level.SEVERE, "Too many requests");
                        case 200 -> {
                            JsonElement response = JsonParser.parseString(new String(request.getBytes()));
                            JsonObject texture = response.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("texture");
                            String texturesValue = texture.get("value").getAsString();
                            String texturesSignature = texture.get("signature").getAsString();
                            user.addCustomData("skinOptions", Utilities.skinOptionsToBytes(skinOptions));
                            user.addCustomData("skinProperty", new SProperty("textures", texturesValue, texturesSignature));
                            if (commandIssuer == null) {
                                MessagesUtil.DONE.msgConsole(new HashObjectMap<String, String>().append("%player%", playerObject.playerName()).append("%url%", texture.get("url").getAsString()), true);
                            } else {
                                MessagesUtil.DONE.msg(commandIssuer, new HashObjectMap<String, String>().append("%player%", playerObject.playerName()).append("%url%", texture.get("url").getAsString()), true);
                            }

                        }
                        default ->
                                skinOverlay.getLogger().log(Level.SEVERE, "Unknown error code: " + request.getHttpCode());
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return user;
        }).thenApplyAsync(user -> {
            if (user != null) {
                if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                    return user;
                }
                skinOverlay.getUserManager().save(user);
            }
            return user;
        }).thenAccept(user -> {
            if (user != null)
                updateSkin(playerObject, true);
            else
                skinOverlay.getLogger().log(Level.SEVERE, "User is null");
        });
    }

    public void updateSkin(@NotNull PlayerObject playerObject, boolean forOthers) {
        skinOverlay.getUserManager().getUser(playerObject.playerUUID()).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error retrieving user: ", throwable);
                return null;
            }
            return user;
        }).thenAccept(user -> {
            if (user == null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error(updateSkin): User is null");
                return;
            }
            PlayerObjectUpdateSkinEvent event;
            try {
                event = new PlayerObjectUpdateSkinEvent(playerObject, user, Utilities.getSkinOptions(user.getCustomData("skinOptions")), true);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            skinOverlay.getEventManager().callEvent(event);
            if (event.isCancelled())
                return;
            event.getPlayerObject().gameProfile().removeProperty("textures").addProperty("textures", event.getUser().getCustomData("skinProperty"));
            if (event.getUser().getCustomData("skinProperty") != null)
                updateSkin0(event.getUser(), event.getPlayerObject(), forOthers);
        }).handle((unused, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof OperationNotSupportedException)
                    skinOverlay.getLogger().info("Unsupported Minecraft Version");
                else
                    skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return unused;
            }
            return unused;
        });
    }

    protected abstract void updateSkin0(UserManager.User user, PlayerObject playerObject, boolean forOthers);

    protected void updateSkin1(UserManager.User user, PlayerObject playerObject, boolean forOthers) {
        try {
            updateSkin(playerObject, Utilities.getSkinOptions(user.getCustomData("skinOptions")), user.getCustomData("skinProperty")).handleAsync((unused, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error", throwable);
                }
                return unused;
            });
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
     * Retrieves the Skin {@link SProperty} for the specified Bedrock Player
     *
     * @param xuid Player's XUID (check {@link SkinHandler#getXUID(PlayerObject)})
     * @return the Skin {@link SProperty} for the specified Bedrock Player
     * @throws IOException When an I/O exception of some sort has occurred.
     */
    public SProperty getXUIDSkin(final String xuid) throws IOException {
        final Request profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
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
