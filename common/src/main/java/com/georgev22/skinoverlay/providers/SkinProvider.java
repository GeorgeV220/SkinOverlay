package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.registry.Registries;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.Utils.Request;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;
import com.georgev22.skinoverlay.utilities.skin.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.mineskin.Java11RequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Visibility;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SkinProvider {

    private static final String CACHE_VERSION = "v2";

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    protected final MineSkinClient mineskinClient;

    private final ConcurrentHashMap<UUID, CompletableFuture<Optional<Skin>>> inFlightGenerations =
            new ConcurrentHashMap<>();

    public SkinProvider() {
        String mineSkinAPIKey = OptionsUtil.MINESKIN_API_KEY.getStringValue();

        if (mineSkinAPIKey.equalsIgnoreCase("none") || mineSkinAPIKey.isBlank()) {
            mineSkinAPIKey = null;
        }

        this.mineskinClient = MineSkinClient.builder()
                .requestHandler(Java11RequestHandler::new)
                .userAgent("SkinOverlay/v1.0")
                .apiKey(mineSkinAPIKey)
                .build();
    }

    public CompletableFuture<Optional<Skin>> retrieveOrGenerateSkin(
            @NotNull SkinOverlayPlayer player,
            @NotNull Overlay overlay
    ) {
        Optional<EntityManager<Skin>> optionalSkinManager =
                EntityManagerRegistry.getInstance().getTyped(Skin.class);

        if (optionalSkinManager.isEmpty()) {
            skinOverlay.getLogger().log(
                    Level.SEVERE,
                    "SkinEntityManager cannot be null",
                    new SkinException("SkinEntityManager cannot be null")
            );

            return CompletableFuture.completedFuture(Optional.empty());
        }

        Optional<PlayerData> playerDataOptional = player.getPlayerData();

        if (playerDataOptional.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        EntityManager<Skin> skinManager = optionalSkinManager.get();

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return prepareOverlaySkin(
                                player,
                                overlay,
                                skinManager
                        );
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new CompletionException(exception);
                    } catch (IOException | ExecutionException exception) {
                        throw new CompletionException(exception);
                    }
                })
                .thenCompose(prepared -> {
                    if (prepared.cachedSkin().isPresent()) {
                        skinOverlay.getLogger().info(
                                "Skin: " + prepared.skinUUID() +
                                        " found for player: " +
                                        player.getName()
                        );

                        return CompletableFuture.completedFuture(
                                prepared.cachedSkin()
                        );
                    }

                    return generateDeduplicated(
                            prepared.skinUUID(),
                            () -> generateMineSkin(
                                    player,
                                    overlay,
                                    prepared.skinUUID(),
                                    prepared.image(),
                                    skinManager
                            )
                    );
                })
                .exceptionally(throwable -> {
                    Throwable actual =
                            unwrapCompletionException(throwable);

                    skinOverlay.getLogger().log(
                            Level.SEVERE,
                            "Error generating or retrieving skin for " +
                                    player.getName() +
                                    " using overlay " + overlay.key(),
                            actual
                    );

                    return Optional.empty();
                });
    }

    private PreparedOverlaySkin prepareOverlaySkin(
            SkinOverlayPlayer player,
            Overlay overlay,
            EntityManager<Skin> skinManager
    ) throws IOException, ExecutionException, InterruptedException {

        SkinConfigurationFile skinConfigurationFile = overlay.configurationFile();

        if (skinConfigurationFile == null) {
            throw new IOException(
                    "SkinConfigurationFile cannot be null for skin " +
                            overlay.key()
            );
        }

        FileConfiguration configuration =
                skinConfigurationFile.getFileConfiguration();

        Skin baseSkin = player.getPlayerData().map(PlayerData::getDefaultSkin).orElse(null);
        SProperty property = baseSkin == null ? null : baseSkin.getProperty();
        if (property == null) {
            property = Registries.SKIN_HOOK.getSelected()
                        .map(skinHook -> skinHook.getProperty(player))
                        .orElse(null);
        }

        if (property == null) {
            property = getSkin(player);
        }

        Model currentModel = extractSkinModel(property);

        SerializableBufferedImage currentSkin = baseSkin != null && baseSkin.getSkinParts() != null
                ? baseSkin.getSkinParts().getFullSkin() : getSkinImage(property);

        // convert player skin to overlay model
        if (currentModel != overlay.model()) {
            currentSkin = SkinConverter.convert(currentSkin, currentModel, overlay.model());
        }

        SkinParts currentSkinParts = new SkinParts(
                currentSkin,
                "currentSkin"
        );

        SkinParts overlaySkinParts = new SkinParts(overlay.getBufferedImage(), overlay.key());
        overlaySkinParts.createParts(overlay.model());
        currentSkinParts.createParts(overlay.model());

        List<Part> playerParts = OverlayRenderer.parts(
                currentSkinParts,
                configuration,
                true
        );

        List<Part> overlayParts = OverlayRenderer.parts(
                overlaySkinParts,
                configuration,
                false
        );

        BufferedImage renderedSkin = renderSkin(
                currentSkin.getBufferedImage(),
                playerParts,
                overlayParts
        );

        UUID skinUUID = createImageCacheUUID(
                player,
                overlay.identifier(),
                renderedSkin
        );

        Optional<Skin> existing = skinManager.findById(skinUUID);

        return new PreparedOverlaySkin(
                skinUUID,
                renderedSkin,
                existing
        );
    }

    private @NonNull CompletableFuture<Optional<Skin>> generateMineSkin(
            @NonNull SkinOverlayPlayer player,
            @NonNull Overlay overlay,
            UUID skinUUID,
            BufferedImage image,
            EntityManager<Skin> skinManager
    ) {
        GenerateRequest generateRequest = GenerateRequest
                .upload(image)
                // Generated overlay keys contain a full hash; keep the upload label within API limits.
                .name("SkinOverlay-" + skinUUID.toString().substring(0, 8))
                .visibility(Visibility.UNLISTED)
                .variant(overlay.model() == Model.SLIM ? Variant.SLIM : Variant.CLASSIC);

        return mineskinClient
                .queue()
                .submit(generateRequest)
                .thenCompose(queueResponse ->
                        queueResponse.getJob()
                                .waitForCompletion(mineskinClient)
                )
                .thenCompose(jobReference ->
                        jobReference.getOrLoadSkin(mineskinClient)
                )
                .thenApply(skinInfo -> new SProperty(
                        "textures",
                        skinInfo.texture().data().value(),
                        skinInfo.texture().data().signature()
                ))
                .thenApplyAsync(property ->
                        createAndSaveSkin(
                                player,
                                new SkinParts(new SerializableBufferedImage(image), overlay.key()),
                                skinUUID,
                                property,
                                skinManager
                        )
                );
    }

    /**
     * Prevent duplicate MineSkin generations for the same skin.
     */
    private @NonNull CompletableFuture<Optional<Skin>> generateDeduplicated(
            UUID skinUUID,
            Supplier<CompletableFuture<Optional<Skin>>> generator
    ) {
        CompletableFuture<Optional<Skin>> placeholder =
                new CompletableFuture<>();

        CompletableFuture<Optional<Skin>> existing =
                inFlightGenerations.putIfAbsent(
                        skinUUID,
                        placeholder
                );

        if (existing != null) {
            return existing;
        }

        try {
            generator.get().whenComplete((skin, throwable) -> {
                inFlightGenerations.remove(
                        skinUUID,
                        placeholder
                );

                if (throwable != null) {
                    placeholder.completeExceptionally(throwable);
                } else {
                    placeholder.complete(skin);
                }
            });
        } catch (Throwable throwable) {
            inFlightGenerations.remove(
                    skinUUID,
                    placeholder
            );

            placeholder.completeExceptionally(throwable);
        }

        return placeholder;
    }

    private Optional<Skin> createAndSaveSkin(
            SkinOverlayPlayer player,
            SkinParts skinParts,
            UUID skinUUID,
            SProperty property,
            @NonNull EntityManager<Skin> skinManager
    ) {
        Optional<Skin> skinOptional =
                skinManager.create(
                        skinUUID,
                        skin -> {
                            skin.setProperty(property);
                            skin.setSkinParts(skinParts);
                        }
                );

        if (skinOptional.isEmpty()) {
            skinOverlay.getLogger().severe(
                    "Couldn't create skin for " +
                            player.getName() +
                            "!"
            );

            return Optional.empty();
        }

        Skin skin = skinOptional.get();

        if (!skinOverlay.isProxy() &&
                OptionsUtil.PROXY.getBooleanValue()) {
            return Optional.of(skin);
        }

        skinManager.save(skin);

        return Optional.of(skin);
    }

    private @NonNull BufferedImage renderSkin(
            @NonNull BufferedImage currentSkin,
            @NonNull List<Part> playerParts,
            @NonNull List<Part> overlayParts
    ) {
        MinecraftSkinRenderer playerRenderer =
                new MinecraftSkinRenderer(
                        playerParts.toArray(Part[]::new)
                );

        playerRenderer.createFullSkinImage();

        MinecraftSkinRenderer overlayRenderer =
                new MinecraftSkinRenderer(
                        overlayParts.toArray(Part[]::new)
                );

        overlayRenderer.createFullSkinImage();

        BufferedImage result = new BufferedImage(
                currentSkin.getWidth(),
                currentSkin.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = result.createGraphics();

        try {
            graphics.drawImage(
                    playerRenderer
                            .getFullSkinImage()
                            .getBufferedImage(),
                    0,
                    0,
                    null
            );

            graphics.drawImage(
                    overlayRenderer
                            .getFullSkinImage()
                            .getBufferedImage(),
                    0,
                    0,
                    null
            );
        } finally {
            graphics.dispose();
        }

        return result;
    }

    private @NonNull UUID createPropertyCacheUUID(
            @NonNull SkinOverlayPlayer player,
            String skinName,
            @NonNull SProperty property
    ) {
        String fingerprint = sha256(
                property.value()
                        .getBytes(StandardCharsets.UTF_8)
        );

        return Utils.generateUUID(
                CACHE_VERSION +
                        "|" +
                        player.getUniqueId() +
                        "|" +
                        skinName +
                        "|" +
                        fingerprint
        );
    }

    private @NonNull UUID createImageCacheUUID(
            SkinOverlayPlayer player,
            String skinName,
            BufferedImage image
    ) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (!ImageIO.write(image, "png", output)) {
            throw new IOException(
                    "Could not encode generated skin as PNG"
            );
        }

        String fingerprint =
                sha256(output.toByteArray());

        return Utils.generateUUID(
                CACHE_VERSION +
                        "|" +
                        player.getUniqueId() +
                        "|" +
                        skinName +
                        "|" +
                        fingerprint
        );
    }

    private @NonNull String sha256(byte[] bytes) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(bytes);

            StringBuilder result =
                    new StringBuilder(hash.length * 2);

            for (byte value : hash) {
                result.append(
                        String.format("%02x", value & 0xff)
                );
            }

            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable",
                    exception
            );
        }
    }

    /**
     * Applies a signed skin and clears any recorded overlay loadout.
     * <p>
     * Use the overload accepting equipped identifiers when restoring a skin that already
     * represents a loadout. Call this method through the player's entity scheduler.
     * </p>
     *
     * @param player player whose game profile and visible skin should be updated
     * @param skin signed skin to apply
     */
    public void setSkin(
            @NotNull SkinOverlayPlayer player,
            @NotNull Skin skin
    ) {
        setSkin(player, skin, List.of());
    }

    /**
     * Applies a signed skin and records the source overlays represented by it.
     * <p>
     * The current skin and equipped identifiers are saved in the same player-data update
     * when the player's entity is loaded. A save request may be batched according to the
     * configured save interval. This method does not validate overlay identifiers or render
     * artwork; callers must supply identifiers matching the skin and use the player's entity scheduler.
     * </p>
     *
     * @param player player whose game profile and visible skin should be updated
     * @param skin signed skin to apply
     * @param equippedOverlays model-qualified leaf identifiers represented by the skin;
     *                         an empty collection clears the recorded loadout
     */
    public void setSkin(
            @NotNull SkinOverlayPlayer player,
            @NotNull Skin skin,
            @NotNull Collection<String> equippedOverlays
    ) {
        SGameProfile gameProfile =
                skinOverlay
                        .getGameProfileProvider()
                        .getGameProfile(player);

        gameProfile.setProperty(
                "textures",
                skin.getProperty()
        );

        skinOverlay
                .getGameProfileProvider()
                .applyUpdatedGameProfile(player);

        skinOverlay
                .getSkinRefresher()
                .refresh(player, skin);

        Optional<EntityManager<PlayerData>> optionalManager =
                EntityManagerRegistry
                        .getInstance()
                        .getTyped(PlayerData.class);

        if (optionalManager.isEmpty()) {
            return;
        }

        EntityManager<PlayerData> playerDataManager =
                optionalManager.get();

        Optional<PlayerData> optionalPlayerData =
                playerDataManager.findById(
                        player.getUniqueId()
                );

        if (optionalPlayerData.isEmpty()) {
            return;
        }

        PlayerData playerData =
                optionalPlayerData.get();

        playerData.setCurrentSkin(skin);
        playerData.setEquippedOverlays(equippedOverlays);

        playerDataManager.save(playerData);
    }

    /**
     * Retrieves {@link SkinOverlayPlayer}'s profile bytes.
     */
    public byte[] getProfileBytes(
            @NotNull SkinOverlayPlayer player,
            @Nullable SProperty property
    ) throws IOException {
        return player.isBedrockPlayer()
                ? getBedrockProfileBytes(player, property)
                : getJavaProfileBytes(player, property);
    }

    public byte[] getBedrockProfileBytes(
            @NotNull SkinOverlayPlayer player,
            @Nullable SProperty property
    ) throws IOException {
        JsonObject json = property != null
                ? createJsonFromProperty(player, property)
                : createJsonForBedrock(player);

        return json
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getJavaProfileBytes(
            @NotNull SkinOverlayPlayer player,
            @Nullable SProperty property
    ) throws IOException {
        if (property != null) {
            return createJsonFromProperty(player, property)
                    .toString()
                    .getBytes(StandardCharsets.UTF_8);
        }

        UUID uuid = skinOverlay.isOnlineMode()
                ? player.getUniqueId()
                : getUUID(player.getName());

        return new Request()
                .openConnection(
                        String.format(
                                "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                uuid.toString().replace("-", "")
                        )
                )
                .getRequest()
                .finalizeRequest()
                .getBytes();
    }

    public SProperty getSkin(
            @NotNull SkinOverlayPlayer player
    ) throws IOException, ExecutionException, InterruptedException {
        return player.isBedrockPlayer()
                ? getXUIDSkin(getXUID(player))
                : getJavaSkin(player);
    }

    public SerializableBufferedImage getSkinImage(
            @NotNull SkinOverlayPlayer player
    ) throws IOException, ExecutionException, InterruptedException {
        SGameProfile gameProfile =
                skinOverlay
                        .getGameProfileProvider()
                        .getGameProfile(player);

        SProperty property =
                gameProfile.getProperties().get("textures");

        if (property == null) {
            property = getSkin(player);
        }

        return getSkinImage(property);
    }

    public SerializableBufferedImage getSkinImage(
            @NotNull SProperty property
    ) throws IOException {
        String url = extractSkinURL(property.value());
        return downloadImage(url);
    }

    public SerializableBufferedImage getSkinImage(
            byte @NotNull [] profileBytes
    ) throws IOException {
        JsonElement json = JsonParser.parseString(
                new String(
                        profileBytes,
                        StandardCharsets.UTF_8
                )
        );

        JsonArray properties =
                json.getAsJsonObject()
                        .getAsJsonArray("properties");

        JsonElement textures =
                findTexturesProperty(properties);

        if (textures == null) {
            throw new IOException(
                    "Property object 'textures' not found"
            );
        }

        String base64Texture =
                textures
                        .getAsJsonObject()
                        .get("value")
                        .getAsString();

        return downloadImage(
                extractSkinURL(base64Texture)
        );
    }

    public String extractSkinURL(
            String base64Texture
    ) throws IOException {
        try {
            String decodedTexture =
                    new String(
                            Base64.getDecoder()
                                    .decode(base64Texture),
                            StandardCharsets.UTF_8
                    );

            JsonObject textureJson =
                    JsonParser.parseString(decodedTexture)
                            .getAsJsonObject();

            JsonObject textures =
                    textureJson.getAsJsonObject("textures");

            if (textures == null) {
                throw new IOException(
                        "Texture payload does not contain 'textures'"
                );
            }

            JsonObject skin =
                    textures.getAsJsonObject("SKIN");

            if (skin == null || !skin.has("url")) {
                throw new IOException(
                        "Texture payload does not contain a skin URL"
                );
            }

            return skin.get("url").getAsString();
        } catch (IllegalArgumentException exception) {
            throw new IOException(
                    "Invalid Base64 texture property",
                    exception
            );
        }
    }

    public Model extractSkinModel(SProperty property) throws IOException {
        if (property == null) {
            return Model.CLASSIC;
        }
        try {
            String decodedTexture = new String(
                    Base64.getDecoder().decode(property.value()),
                    StandardCharsets.UTF_8
            );

            JsonObject textureJson = JsonParser.parseString(decodedTexture)
                    .getAsJsonObject();

            JsonObject textures = textureJson.getAsJsonObject("textures");
            if (textures == null) {
                throw new IOException(
                        "Texture payload does not contain 'textures'"
                );
            }

            JsonObject skin = textures.getAsJsonObject("SKIN");
            if (skin == null) {
                throw new IOException(
                        "Texture payload does not contain a skin"
                );
            }

            JsonObject metadata = skin.getAsJsonObject("metadata");
            if (metadata == null || !metadata.has("model")) {
                return Model.CLASSIC;
            }

            String model = metadata.get("model").getAsString();

            return "slim".equalsIgnoreCase(model)
                    ? Model.SLIM
                    : Model.CLASSIC;

        } catch (IllegalArgumentException exception) {
            throw new IOException(
                    "Invalid Base64 texture property",
                    exception
            );
        }
    }

    private @NonNull SerializableBufferedImage downloadImage(
            String url
    ) throws IOException {
        BufferedImage image;

        try {
            image = ImageIO.read(
                    URI.create(url).toURL()
            );
        } catch (IllegalArgumentException exception) {
            throw new IOException(
                    "Invalid skin URL: " + url,
                    exception
            );
        }

        if (image == null) {
            throw new IOException(
                    "Could not decode skin image from " + url
            );
        }

        return new SerializableBufferedImage(image);
    }

    public JsonElement findTexturesProperty(
            @NotNull JsonArray properties
    ) {
        for (JsonElement property : properties) {
            if (!property.isJsonObject()) {
                continue;
            }

            JsonObject object =
                    property.getAsJsonObject();

            if (!object.has("name")) {
                continue;
            }

            if ("textures".equals(
                    object.get("name").getAsString()
            )) {
                return property;
            }
        }

        return null;
    }

    /**
     * Checks whether the specified username currently has a Mojang profile.
     */
    public boolean isUsernamePremium(String username) {
        try {
            return lookupJavaUUID(username).isPresent();
        } catch (IOException exception) {
            skinOverlay.getLogger().log(
                    Level.WARNING,
                    "Unable to check Java profile for username " +
                            username,
                    exception
            );

            return false;
        }
    }

    public SProperty getJavaSkin(
            SkinOverlayPlayer player
    ) throws IOException {
        SProperty hookProperty =
                Registries.SKIN_HOOK.getSelected()
                        .map(skinHook -> skinHook.getProperty(player))
                        .orElse(null);

        if (hookProperty != null) {
            return hookProperty;
        }

        JsonElement json = JsonParser.parseString(
                new String(
                        getProfileBytes(player, null),
                        StandardCharsets.UTF_8
                )
        );

        JsonArray properties =
                json.getAsJsonObject()
                        .getAsJsonArray("properties");

        JsonElement textureElement =
                findTexturesProperty(properties);

        if (textureElement == null) {
            throw new IOException(
                    "No texture property found for player " +
                            player.getName()
            );
        }

        JsonObject object =
                textureElement.getAsJsonObject();

        if (!object.has("signature")) {
            throw new IOException(
                    "Texture property has no signature for player " +
                            player.getName()
            );
        }

        return new SProperty(
                object.get("name").getAsString(),
                object.get("value").getAsString(),
                object.get("signature").getAsString()
        );
    }

    public SProperty getXUIDSkin(
            String xuid
    ) throws IOException {
        Request request = new Request()
                .openConnection(
                        String.format(
                                "https://api.geysermc.org/v2/skin/%s",
                                xuid
                        )
                )
                .getRequest()
                .finalizeRequest();

        JsonElement json = JsonParser.parseString(
                new String(
                        request.getBytes(),
                        StandardCharsets.UTF_8
                )
        );

        return new SProperty(
                "textures",
                json.getAsJsonObject()
                        .get("value")
                        .getAsString(),
                json.getAsJsonObject()
                        .get("signature")
                        .getAsString()
        );
    }

    public String getXUID(
            @NotNull SkinOverlayPlayer player
    ) throws IOException {
        String username =
                player.getName().replace(".", "");

        Request request = new Request()
                .openConnection(
                        String.format(
                                "https://api.geysermc.org/v2/xbox/xuid/%s",
                                username
                        )
                )
                .getRequest()
                .finalizeRequest();

        if (request.getHttpCode() != 200) {
            request = new Request()
                    .openConnection(
                            String.format(
                                    "https://api.geysermc.org/v2/xbox/xuid/%s",
                                    username.replace("_", "%20")
                            )
                    )
                    .getRequest()
                    .finalizeRequest();
        }

        JsonElement json = JsonParser.parseString(
                new String(
                        request.getBytes(),
                        StandardCharsets.UTF_8
                )
        );

        return json
                .getAsJsonObject()
                .get("xuid")
                .getAsString();
    }

    public JsonObject createJsonForBedrock(
            @NotNull SkinOverlayPlayer player
    ) throws IOException {
        return createJsonFromProperty(
                player,
                getXUIDSkin(getXUID(player))
        );
    }

    public JsonObject createJsonFromProperty(
            @NotNull SkinOverlayPlayer player,
            @Nullable SProperty property
    ) {
        if (property == null) {
            property = skinOverlay
                    .getGameProfileProvider()
                    .getGameProfile(player)
                    .getProperties()
                    .get("textures");
        }

        if (property == null) {
            throw new IllegalStateException(
                    "Player " + player.getName() +
                            " has no textures property"
            );
        }

        JsonObject textureProperty =
                new JsonObject();

        textureProperty.add(
                "name",
                new JsonPrimitive("textures")
        );

        textureProperty.add(
                "value",
                new JsonPrimitive(property.value())
        );

        if (property.signature() != null) {
            textureProperty.add(
                    "signature",
                    new JsonPrimitive(
                            property.signature()
                    )
            );
        }

        JsonArray properties =
                new JsonArray();

        properties.add(textureProperty);

        JsonObject result =
                new JsonObject();

        result.add(
                "properties",
                properties
        );

        return result;
    }

    /**
     * Retrieves the UUID for the specified Java player.
     * <p>
     * If Mojang reports that the username does not exist, the configured
     * default skin UUID is returned.
     * <p>
     * Network failures are NOT treated as a nonexistent account.
     */
    public UUID getUUID(
            String playerName
    ) throws IOException {
        return lookupJavaUUID(playerName)
                .orElseGet(
                        () -> UUID.fromString(
                                OptionsUtil.DEFAULT_SKIN_UUID
                                        .getStringValue()
                        )
                );
    }

    private Optional<UUID> lookupJavaUUID(
            String playerName
    ) throws IOException {
        HttpURLConnection connection = null;

        try {
            connection =
                    (HttpURLConnection) URI.create(
                                    "https://api.mojang.com/users/profiles/minecraft/" +
                                            playerName
                            )
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status =
                    connection.getResponseCode();

            if (status == 204 || status == 404) {
                return Optional.empty();
            }

            if (status != 200) {
                throw new IOException(
                        "Mojang returned HTTP " +
                                status +
                                " while resolving " +
                                playerName
                );
            }

            StringBuilder content =
                    new StringBuilder();

            try (
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            )
            ) {
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            JsonObject json =
                    JsonParser.parseString(
                                    content.toString()
                            )
                            .getAsJsonObject();

            if (!json.has("id")) {
                throw new IOException(
                        "Mojang response contains no UUID for " +
                                playerName
                );
            }

            return Optional.of(
                    parseUndashedUUID(
                            json.get("id").getAsString()
                    )
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private @NonNull UUID parseUndashedUUID(
            @NonNull String uuid
    ) {
        if (uuid.contains("-")) {
            return UUID.fromString(uuid);
        }

        return UUID.fromString(
                uuid.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                )
        );
    }

    private Throwable unwrapCompletionException(
            Throwable throwable
    ) {
        if (throwable instanceof CompletionException &&
                throwable.getCause() != null) {
            return throwable.getCause();
        }

        return throwable;
    }

    private record PreparedOverlaySkin(UUID skinUUID, BufferedImage image, Optional<Skin> cachedSkin) {

    }
}
