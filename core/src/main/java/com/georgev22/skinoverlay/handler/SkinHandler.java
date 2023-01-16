package com.georgev22.skinoverlay.handler;

import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities.Request;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class SkinHandler {

    protected SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public abstract void updateSkin(@NotNull final FileConfiguration fileConfiguration,
                                    @NotNull final PlayerObject playerObject,
                                    final boolean reset,
                                    @NotNull final String skinName);

    public abstract void updateSkin(@NotNull final FileConfiguration fileConfiguration,
                                    @NotNull final PlayerObject playerObject,
                                    final boolean reset,
                                    @NotNull final String skinName,
                                    final Property property);

    protected abstract <T> GameProfile getGameProfile0(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException;

    public GameProfile getGameProfile(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        final GameProfile gameProfile = this.getGameProfile0(playerObject);
        if (!gameProfile.getProperties().containsKey("textures")) {
            gameProfile.getProperties().put("textures", this.getSkin(playerObject));
        }
        return gameProfile;
    }

    public byte[] getProfileBytes(@NotNull final PlayerObject playerObject, @Nullable Property property) throws IOException, ExecutionException, InterruptedException {
        return playerObject.isBedrock() ? this.getBedrockProfileBytes(playerObject, property) : this.getJavaProfileBytes(playerObject, property);
    }

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

    public byte[] getBedrockProfileBytes(@NotNull final PlayerObject playerObject, final Property property) throws IOException, ExecutionException, InterruptedException {
        return property != null ?
                new ByteArrayInputStream(this.createJsonFromProperty(playerObject, property).getAsJsonObject().toString().getBytes()).readAllBytes() :
                new ByteArrayInputStream(this.createJsonForBedrock(playerObject).getAsJsonObject().toString().getBytes()).readAllBytes();
    }

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

    public UUID getUUID(final String playerName) throws IOException {
        Request request = new Request().openConnection(String.format("https://api.minetools.eu/uuid/%s", playerName)).getRequest().finalizeRequest();
        if (request.getHttpCode() != 200) {
            request = new Request().openConnection(String.format("https://api.mojang.com/users/profiles/minecraft/%s", playerName)).getRequest().finalizeRequest();
        }

        final byte[] jsonBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(jsonBytes));
        return UUID.fromString(json.getAsJsonObject().get("id").getAsString().replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }

    public Property getXUIDSkin(final String xuid) throws IOException {
        final Request profileBytes = new Request().openConnection(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
        final JsonElement json = JsonParser.parseString(new String(profileBytes.getBytes()));
        return new Property("textures", json.getAsJsonObject().get("value").getAsString(), json.getAsJsonObject().get("signature").getAsString());
    }

    public Property getJavaSkin(final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
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

    public Property getSkin(@NotNull final PlayerObject playerObject) throws IOException, ExecutionException, InterruptedException {
        return playerObject.isBedrock() ? this.getXUIDSkin(this.getXUID(playerObject)) : this.getJavaSkin(playerObject);
    }

    public static class SkinHandler_ extends SkinHandler {
        @Override
        public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
            throw new UnsupportedOperationException("[SkinHandler]: updateSkin(); Unsupported Minecraft Version");
        }

        @Override
        public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
            updateSkin(fileConfiguration, playerObject, reset, skinName);
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
