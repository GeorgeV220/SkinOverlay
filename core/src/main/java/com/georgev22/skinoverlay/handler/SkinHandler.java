package com.georgev22.skinoverlay.handler;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.yaml.file.FileConfiguration;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;

public abstract class SkinHandler {
    public abstract void updateSkin(@NotNull final FileConfiguration fileConfiguration,
                                    @NotNull final PlayerObject playerObject,
                                    final boolean reset,
                                    @NotNull final String skinName);

    public abstract void updateSkin(@NotNull final FileConfiguration fileConfiguration,
                                    @NotNull final PlayerObject playerObject,
                                    final boolean reset,
                                    @NotNull final String skinName,
                                    final Property property);

    protected abstract <T> GameProfile getGameProfile0(@NotNull final PlayerObject playerObject) throws IOException;

    public GameProfile getGameProfile(@NotNull final PlayerObject playerObject) throws IOException {
        final GameProfile gameProfile = this.getGameProfile0(playerObject);
        if (!gameProfile.getProperties().containsKey("textures")) {
            gameProfile.getProperties().put("textures", this.getSkin(playerObject));
        }
        return gameProfile;
    }

    public byte[] getProfileBytes(@NotNull final PlayerObject playerObject, final boolean fromProperties) throws IOException {
        return playerObject.isBedrock() ? this.getBedrockProfileBytes(playerObject) : this.getJavaProfileBytes(playerObject, fromProperties);
    }

    public byte[] getJavaProfileBytes(@NotNull final PlayerObject playerObject, final boolean fromProperties) throws IOException {
        return fromProperties ? new ByteArrayInputStream(this.createJsonFromProperty(playerObject).getAsJsonObject().toString().getBytes()).readAllBytes() : new Request(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", playerObject.playerUUID().toString().replaceAll("-", ""))).getRequest().finalizeRequest().getBytes();
    }

    public byte[] getBedrockProfileBytes(@NotNull final PlayerObject playerObject) throws IOException {
        final JsonObject jsonObject = this.createJsonForBedrock(playerObject).getAsJsonObject();
        final InputStream is = new ByteArrayInputStream(jsonObject.toString().getBytes());
        try {
            return is.readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    public JsonObject createJsonForBedrock(@NotNull final PlayerObject playerObject) throws IOException {
        final byte[] profileBytes = new Request(String.format("https://api.geysermc.org/v2/skin/%s", this.getXUID(playerObject))).getRequest().finalizeRequest().getBytes();
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

    public JsonObject createJsonFromProperty(@NotNull final PlayerObject playerObject) throws IOException {
        final Property property = this.getGameProfile(playerObject).getProperties().get("textures").iterator().next();
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
        Request request = new Request(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", playerObject.playerName().replace(".", ""))).getRequest().finalizeRequest();
        final int httpCode = request.getHttpCode();
        if (httpCode != 200) {
            request = new Request(String.format("https://api.geysermc.org/v2/xbox/xuid/%s", playerObject.playerName().replace(".", "").replace("_", "%20"))).getRequest().finalizeRequest();
        }
        final byte[] profileBytes = request.getBytes();
        final JsonElement json = JsonParser.parseString(new String(profileBytes));
        return json.getAsJsonObject().get("xuid").getAsString();
    }

    public Property getXUIDSkin(final String xuid) throws IOException {
        final Request profileBytes = new Request(String.format("https://api.geysermc.org/v2/skin/%s", xuid)).getRequest().finalizeRequest();
        final JsonElement json = JsonParser.parseString(new String(profileBytes.getBytes()));
        return new Property("textures", json.getAsJsonObject().get("value").getAsString(), json.getAsJsonObject().get("signature").getAsString());
    }

    public Property getJavaSkin(final PlayerObject playerObject) throws IOException {
        final JsonElement json = JsonParser.parseString(new String(this.getProfileBytes(playerObject, false)));
        final JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
        Property property = null;
        for (final JsonElement object : properties) {
            if (object.getAsJsonObject().get("name").getAsString().equals("textures")) {
                property = new Property("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString());
            }
        }
        return property;
    }

    public Property getSkin(@NotNull final PlayerObject playerObject) throws IOException {
        return playerObject.isBedrock() ? this.getXUIDSkin(this.getXUID(playerObject)) : this.getJavaSkin(playerObject);
    }

    public static class SkinHandler_ extends SkinHandler {
        @Override
        public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName) {
            SkinOverlay.getInstance().getLogger().log(Level.WARNING, "[SkinHandler]: updateSkin(); Unsupported Minecraft Version");
        }

        @Override
        public void updateSkin(@NotNull FileConfiguration fileConfiguration, @NotNull PlayerObject playerObject, boolean reset, @NotNull String skinName, Property property) {
            SkinOverlay.getInstance().getLogger().log(Level.WARNING, "[SkinHandler]: updateSkin(); Unsupported Minecraft Version");
        }

        @Override
        protected <T> GameProfile getGameProfile0(@NotNull PlayerObject playerObject) throws IOException {
            GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
            if (!gameProfile.getProperties().containsKey("textures")) {
                gameProfile.getProperties().put("textures", this.getSkin(playerObject));
            }
            return gameProfile;
        }
    }

    public static class Request {
        private final String address;
        private byte[] bytes;
        private int httpCode;
        private final HttpsURLConnection httpsURLConnection;

        public Request(final String address) throws IOException {
            this.address = address;
            final URL url = new URL(address);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
        }

        public String getAddress() {
            return this.address;
        }

        public Request getRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("GET");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            return this;
        }

        public Request postRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("POST");
            this.httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            this.httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            this.httpsURLConnection.setDoOutput(true);
            return this;
        }

        @SafeVarargs
        @Contract("_ -> this")
        public final Request setProperty(final ObjectMap.Pair<String, String>... pairs) {
            for (final ObjectMap.Pair<String, String> pair : pairs) {
                this.httpsURLConnection.setRequestProperty(pair.key(), pair.value());
            }
            return this;
        }

        public Request writeToOutputStream(final String... data) throws IOException {
            for (final String str : data) {
                this.httpsURLConnection.getOutputStream().write(str.getBytes());
            }
            return this;
        }

        public Request writeToOutputStream(final byte[]... data) throws IOException {
            for (final byte[] bytes : data) {
                this.httpsURLConnection.getOutputStream().write(bytes);
            }
            return this;
        }

        public Request closeOutputStream() throws IOException {
            this.httpsURLConnection.getOutputStream().close();
            return this;
        }

        public Request finalizeRequest() throws IOException {
            this.httpCode = this.httpsURLConnection.getResponseCode();
            this.bytes = this.httpsURLConnection.getInputStream().readAllBytes();
            return this;
        }

        public int getHttpCode() {
            return this.httpCode;
        }

        public byte[] getBytes() {
            return this.bytes;
        }
    }
}
