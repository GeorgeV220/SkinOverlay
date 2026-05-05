package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.Utils.Request;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.OverlayOptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;
import com.georgev22.skinoverlay.utilities.skin.MinecraftSkinRenderer;
import com.georgev22.skinoverlay.utilities.skin.Part;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.google.gson.*;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineskin.Java11RequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.JobInfo;
import org.mineskin.data.Visibility;
import org.mineskin.request.GenerateRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class SkinProvider {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    protected final MineSkinClient mineskinClient;

    public SkinProvider() {
        String mineSkinAPIKey = OptionsUtil.MINESKIN_API_KEY.getStringValue();
        if (mineSkinAPIKey.equalsIgnoreCase("none") || mineSkinAPIKey.isBlank()) {
            mineSkinAPIKey = null;
        }
        this.mineskinClient =
                MineSkinClient.builder()
                        .requestHandler(Java11RequestHandler::new)
                        .userAgent("SkinOverlay/v1.0")
                        .apiKey(mineSkinAPIKey)
                        .build();
    }

    public CompletableFuture<Optional<Skin>> retrieveOrGenerateSkin(@NotNull SPlayer player, @NotNull SkinParts skinParts) {
        UUID skinUUID = Utils.generateUUID(skinParts.getSkinName() + player.getUniqueId().toString());
        @NotNull Optional<EntityManager<Skin>> skinEntityManager = EntityManagerRegistry.getInstance().getTyped(Skin.class);
        if (skinEntityManager.isEmpty()) {
            skinOverlay.getLogger().log(Level.SEVERE, "SkinEntityManager cannot be null", new SkinException("SkinEntityManager cannot be null"));
            return CompletableFuture.completedFuture(Optional.empty());
        }
        EntityManager<Skin> skinManager = skinEntityManager.get();
        return CompletableFuture.supplyAsync(() -> {
            boolean exists = skinManager.exists(skinUUID);
            if (exists) {
                skinOverlay.getLogger().info("Skin: " + skinUUID + " found for player: " + player.getName());
                return skinManager.findById(skinUUID);
            } else if (skinParts.getSkinName().equalsIgnoreCase("default")) {
                this.skinOverlay.getLogger().warning("Default skin not found for player: " + player.getName());
                try {
                    SProperty property = this.getSkin(player);
                    Skin skin = new Skin(skinUUID);
                    skin.setProperty(property);
                    skin.setSkinParts(skinParts);
                    if (!this.skinOverlay.isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                        return Optional.of(skin);
                    }
                    skinManager.save(skin);
                    return Optional.of(skin);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    this.skinOverlay.getLogger().log(Level.SEVERE, "Failed to get skin for player: " + player.getName(), e);
                    return Optional.empty();
                }
            } else {
                try {
                    SkinConfigurationFile skinConfigurationFile = this.skinOverlay.getSkinFileCache().getCacheSkinConfig(skinParts.getSkinName());

                    if (skinConfigurationFile == null) {
                        skinOverlay.getLogger().log(Level.SEVERE, "SkinConfigurationFile cannot be null", new SkinException("SkinConfigurationFile cannot be null"));
                        return Optional.empty();
                    }

                    FileConfiguration fileConfiguration = skinConfigurationFile.getFileConfiguration();

                    BufferedImage currentSkin = this.getSkinImage(this.getProfileBytes(player, this.skinOverlay.getSkinHook().getProperty(player)));

                    SkinParts currentSkinParts = new SkinParts(new SerializableBufferedImage(currentSkin), "currentSkin");
                    currentSkinParts.createParts();

                    List<Part> overlaySkinParts = new ArrayList<>();
                    for (Part part : skinParts.getParts().values()) {
                        if (part.name().startsWith("Jacket")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_JACKET.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Hat")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_HAT.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Left_Sleeve")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_LEFT_SLEEVE.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Right_Sleeve")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_RIGHT_SLEEVE.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Left_Pants")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_LEFT_PANTS.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Right_Pants")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_OVERLAY_RIGHT_PANTS.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        overlaySkinParts.add(part);
                    }

                    List<Part> newSkinParts = new ArrayList<>();
                    for (Part part : currentSkinParts.getParts().values()) {
                        if (part.name().startsWith("Jacket")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_JACKET.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Hat")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_HAT.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Left_Sleeve")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_LEFT_SLEEVE.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Right_Sleeve")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_RIGHT_SLEEVE.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Left_Pants")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_LEFT_PANTS.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        if (part.name().startsWith("Right_Pants")) {
                            if (!part.isEmpty() & !OverlayOptionsUtil.PARTS_PLAYER_RIGHT_PANTS.getBooleanValue(fileConfiguration)) {
                                continue;
                            }
                        }
                        newSkinParts.add(part);
                    }

                    MinecraftSkinRenderer defaultSkinRenderer = new MinecraftSkinRenderer(newSkinParts.toArray(Part[]::new));
                    defaultSkinRenderer.createFullSkinImage();

                    MinecraftSkinRenderer overlaySkinRenderer = new MinecraftSkinRenderer(overlaySkinParts.toArray(Part[]::new));
                    overlaySkinRenderer.createFullSkinImage();

                    BufferedImage skinToBeGenerated = new BufferedImage(currentSkin.getWidth(), currentSkin.getHeight(), 2);
                    Graphics2D canvas = skinToBeGenerated.createGraphics();

                    canvas.drawImage(defaultSkinRenderer.getFullSkinImage().getBufferedImage(), 0, 0, null);
                    canvas.drawImage(overlaySkinRenderer.getFullSkinImage().getBufferedImage(), 0, 0, null);
                    canvas.dispose();

                    GenerateRequest generateRequest = GenerateRequest.upload(skinToBeGenerated)
                            .name("SkinOverlay-" + player.getName() + "-" + skinParts.getSkinName())
                            .visibility(Visibility.UNLISTED);
                    SProperty property = mineskinClient
                            .queue()
                            .submit(generateRequest)
                            .thenCompose(queueResponse -> {
                                JobInfo job = queueResponse.getJob();
                                return job.waitForCompletion(mineskinClient);
                            })
                            .thenCompose(jobReference -> jobReference.getOrLoadSkin(mineskinClient))
                            .thenApply(skinInfo -> {
                                String value = skinInfo.texture().data().value();
                                String signature = skinInfo.texture().data().signature();
                                return new SProperty(value, signature);
                            }).join();

                    Skin skin = new Skin(skinUUID);
                    skin.setSkinParts(skinParts);
                    skin.setProperty(property);
                    if (!skinOverlay.isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                        return Optional.of(skin);
                    }
                    skinManager.save(skin);
                    return Optional.of(skin);
                } catch (IOException | RuntimeException exception) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error generating or retrieving the skin:", exception);
                    return Optional.empty();
                }
            }
        });
    }


    /**
     * Apply the skin for the specified {@link SPlayer}
     *
     * @param player Player's {@link SPlayer} object.
     * @param skin   Skin
     */
    public void setSkin(@NotNull SPlayer player, @NotNull Skin skin) {
        SGameProfile gameProfile = skinOverlay.getGameProfileProvider().getGameProfile(player);
        gameProfile.setProperty("textures", skin.getProperty());
        skinOverlay.getGameProfileProvider().applyUpdatedGameProfile(player);
        this.skinOverlay.getSkinRefresher().refresh(player, skin);

        Optional<EntityManager<PlayerData>> optionalPlayerDataEntityManager = EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
        if (optionalPlayerDataEntityManager.isEmpty()) {
            return;
        }
        EntityManager<PlayerData> playerDataEntityManager = optionalPlayerDataEntityManager.get();
        Optional<PlayerData> optionalPlayerData = playerDataEntityManager.findById(player.getUniqueId());
        if (optionalPlayerData.isEmpty()) {
            return;
        }
        PlayerData playerData = optionalPlayerData.get();
        playerData.setCurrentSkin(skin);

        playerDataEntityManager.save(playerData);
    }

    /**
     * Retrieves {@link SPlayer}'s {@link SGameProfile} bytes
     *
     * @param player   {@link SPlayer} object
     * @param property If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link SPlayer}'s {@link SGameProfile} bytes
     * @throws IOException When an I/O exception to some sort has occurred.
     */
    public byte[] getProfileBytes(@NotNull final SPlayer player, @Nullable SProperty property) throws IOException {
        return player.isBedrock() ? this.getBedrockProfileBytes(player, property) : this.getJavaProfileBytes(player, property);
    }

    /**
     * Retrieves Bedrock {@link SPlayer}'s {@link SGameProfile} bytes
     *
     * @param player   {@link SPlayer} object
     * @param property If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link SPlayer}'s {@link SGameProfile} bytes
     * @throws IOException When an I/O exception to some sort has occurred.
     */
    public byte[] getBedrockProfileBytes(@NotNull final SPlayer player, final SProperty property) throws IOException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(player, property).getAsJsonObject().toString().getBytes()).readAllBytes() :
                new ByteArrayInputStream(this.createJsonForBedrock(player).getAsJsonObject().toString().getBytes()).readAllBytes();
    }

    /**
     * Retrieves Java {@link SPlayer}'s {@link SGameProfile} bytes
     *
     * @param player   {@link SPlayer} object
     * @param property If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return {@link SPlayer}'s {@link SGameProfile} bytes
     * @throws IOException When an I/O exception to some sort has occurred.
     */
    public byte[] getJavaProfileBytes(@NotNull final SPlayer player, @Nullable SProperty property) throws IOException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(player, property)
                        .getAsJsonObject().toString().getBytes()).readAllBytes() :
                new Request()
                        .openConnection(
                                String.format(
                                        "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                        (skinOverlay.isOnlineMode() ?
                                                player.getUniqueId() :
                                                getUUID(player.getName()))
                                                .toString().replaceAll("-", ""))
                        )
                        .getRequest()
                        .finalizeRequest()
                        .getBytes();
    }

    /**
     * Retrieves a skin image from the provided SPlayer.
     *
     * @param player The SPlayer for which to fetch the skin.
     * @return The BufferedImage representing the player's skin.
     * @throws IOException          If an I/O error occurs while fetching the image.
     * @throws ExecutionException   If an exception occurs during execution.
     * @throws InterruptedException If the execution is interrupted.
     */
    public BufferedImage getSkinImage(final @NotNull SPlayer player) throws IOException, ExecutionException, InterruptedException {
        SGameProfile gameProfile = skinOverlay.getGameProfileProvider().getGameProfile(player);
        SProperty sProperty = gameProfile.getProperties().get("textures") != null
                ? gameProfile.getProperties().get("textures")
                : getSkin(player);
        String url = JsonParser.parseString(new String(Base64.getDecoder().decode(sProperty.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
        return ImageIO.read(new URL(url));
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Player
     *
     * @param player {@link SPlayer}'s object
     * @return the Skin {@link SProperty} for the specified {@link SPlayer}
     * @throws IOException          When an I/O exception to some sort has occurred.
     * @throws ExecutionException   When attempting to retrieve the result of a task that aborted by throwing an exception.
     * @throws InterruptedException When a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     */
    public SProperty getSkin(@NotNull final SPlayer player) throws IOException, ExecutionException, InterruptedException {
        return player.isBedrock() ? this.getXUIDSkin(this.getXUID(player)) : this.getJavaSkin(player);
    }

    /**
     * Retrieves a skin image from the provided SProperty.
     *
     * @param sProperty The SProperty containing the skin information.
     * @return The BufferedImage representing the player's skin.
     * @throws IOException If an I/O error occurs while fetching the image.
     */
    public BufferedImage getSkinImage(final @NotNull SProperty sProperty) throws IOException {
        String url = JsonParser.parseString(new String(Base64.getDecoder().decode(sProperty.value())))
                .getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();
        return ImageIO.read(new URL(url));
    }

    /**
     * Retrieves a skin image from the provided profile bytes.
     *
     * @param profileBytes The profile bytes containing skin information.
     * @return The BufferedImage representing the player's skin.
     * @throws IOException If an I/O error occurs while fetching the image.
     */
    public BufferedImage getSkinImage(final byte @NotNull [] profileBytes) throws IOException {
        JsonElement json = JsonParser.parseString(new String(profileBytes));
        JsonArray properties = json.getAsJsonObject().getAsJsonArray("properties");

        JsonElement textures = findTexturesProperty(properties);
        if (textures == null) {
            skinOverlay.getLogger().log(Level.SEVERE, "Property object 'textures' not found", new SkinException("Property object 'textures' not found"));
            return null;
        }

        String base64Texture = textures.getAsJsonObject().get("value").getAsString();
        String decodedTexture = new String(Base64.getDecoder().decode(base64Texture));

        JsonElement textureJson = JsonParser.parseString(decodedTexture);
        String skinUrl = textureJson.getAsJsonObject()
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();

        return ImageIO.read(new URL(skinUrl));
    }

    /**
     * Find the 'textures' property within a JsonArray of properties.
     *
     * @param properties The JsonArray of properties to search for 'textures'.
     * @return The JsonElement representing the 'textures' property, or null if not found.
     */
    public JsonElement findTexturesProperty(@NotNull JsonArray properties) {
        for (JsonElement property : properties) {
            if (property.isJsonObject() && "textures".equals(property.getAsJsonObject().get("name").getAsString())) {
                return property;
            }
        }
        return null;
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
            skinOverlay.getLogger().log(Level.SEVERE, "Unable to check if username " + username + " is a premium account.", e);
            return false;
        }
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Java Player
     *
     * @param player {@link SPlayer}'s object
     * @return the Skin {@link SProperty} for the specified Java Player
     * @throws IOException When an I/O exception to some sort has occurred.
     */
    public SProperty getJavaSkin(final SPlayer player) throws IOException {
        if (skinOverlay.getSkinHook().getProperty(player) != null) {
            return skinOverlay.getSkinHook().getProperty(player);
        }
        final JsonElement json = JsonParser.parseString(new String(this.getProfileBytes(player, null)));
        final JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
        SProperty property = null;
        for (final JsonElement object : properties) {
            if (object.getAsJsonObject().get("name").getAsString().equals("textures")) {
                property = new SProperty(object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString());
            }
        }
        return property;
    }

    /**
     * Retrieves the Skin {@link SProperty} for the specified Bedrock Player
     *
     * @param xuid Player's XUID (check {@link #getXUID(SPlayer)})
     * @return the Skin {@link SProperty} for the specified Bedrock Player
     * @throws IOException When an I/O exception to some sort has occurred.
     */
    public SProperty getXUIDSkin(final String xuid) throws IOException {
        final Request profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
        final JsonElement json = JsonParser.parseString(new String(profileBytes.getBytes()));
        return new SProperty(json.getAsJsonObject().get("value").getAsString(), json.getAsJsonObject().get("signature").getAsString());
    }

    /**
     * Retrieves the XUID for the specified Bedrock Player
     *
     * @param player {@link SPlayer}'s object
     * @return the XUID for the specified Bedrock Player
     * @throws IOException When the request fails
     */
    public String getXUID(@NotNull final SPlayer player) throws IOException {
        Request request = new Request().openConnection(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", player.getName().replace(".", ""))).getRequest().finalizeRequest();
        final int httpCode = request.getHttpCode();
        if (httpCode != 200) {
            request = new Request()
                    .openConnection(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", player.getName().replace(".", "").replace("_", "%20")))
                    .getRequest()
                    .finalizeRequest();
        }
        final byte[] profileBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(profileBytes));
        return json.getAsJsonObject().get("xuid").getAsString();
    }


    /**
     * Creates a similar JSON as the Java one for the specified Bedrock Player
     *
     * @param player {@link SPlayer}'s object
     * @return a JSON for the specified Bedrock Player
     * @throws IOException When the request fails
     */
    public JsonObject createJsonForBedrock(@NotNull final SPlayer player) throws IOException {
        final byte[] profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s",
                this.getXUID(player))).getRequest().finalizeRequest().getBytes();
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
     * @param player   {@link SPlayer}'s object
     * @param property If you want to use a {@link SProperty} instead of {@link SGameProfile} ones
     * @return a JSON from a {@link SGameProfile} or {@link SProperty}
     */
    public JsonObject createJsonFromProperty(@NotNull final SPlayer player, @Nullable SProperty property) {
        if (property == null)
            property = skinOverlay.getGameProfileProvider().getGameProfile(player).getProperties().get("textures");
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
     * Retrieves the UUID for the specified Java Player.
     * <p>
     * If the specified player is not a premium account, returns a default UUID (Steve).
     *
     * @param playerName The player's Minecraft username.
     * @return The UUID for the specified Java Player.
     * @throws IOException If an I/O exception to some sort has occurred.
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

}
