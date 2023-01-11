package com.georgev22.skinoverlay.utilities;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.interfaces.ImageSupplier;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;
import java.util.logging.Level;

public class Utilities {

    private static final SkinOverlay skinOverlay = SkinOverlay.getInstance();


    public static void setSkin(String skinName, @NotNull PlayerObject playerObject, String @NotNull [] properties) {
        Validate.isTrue((properties.length == 3 ? 1 : 0) != 0, "Properties length must be 3");
        UserData userData = UserData.getUser(playerObject);
        userData.setSkinName(skinName);
        userData.setProperty(new Property(properties[0], properties[1], properties[2]));
        updateSkin(playerObject, true, false);
    }

    public static void setSkin(ImageSupplier imageSupplier, String skinName, PlayerObject playerObject, @Nullable CommandIssuer commandIssuer) {
        SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
            Image overlay;
            try {
                overlay = imageSupplier.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UserData userData = UserData.getUser(playerObject);
            try {
                byte[] profileBytes = skinOverlay.getSkinHandler().getProfileBytes(playerObject, userData.getDefaultSkinProperty());
                JsonElement json = JsonParser.parseString(new String(profileBytes));
                JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
                for (JsonElement object : properties) {
                    if (!object.getAsJsonObject().get("name").getAsString().equals("textures")) continue;
                    if (overlay == null) {
                        GameProfile gameProfile = skinOverlay.getSkinHandler().getGameProfile(playerObject);
                        PropertyMap pm = gameProfile.getProperties();
                        Collection<Property> gameProfileProperties = pm.get("textures");
                        Property property = pm.get("textures").iterator().next();
                        pm.remove("textures", property);
                        pm.put("textures", new Property("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString()));
                        userData.setSkinName(skinName);
                        userData.setProperty(new Property("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString()));
                        Utilities.updateSkin(playerObject, true, true);
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
                            .setProperty(ObjectMap.Pair.create("Content-Type", ("multipart/form-data;boundary=" + boundary)))
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
                            userData.setSkinName(skinName);
                            userData.setProperty(new Property("textures", texturesValue, texturesSignature));
                            Utilities.updateSkin(playerObject, true, false);
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
                throw new RuntimeException(exception);
            }
        });
    }

    public static void updateSkin(@NotNull PlayerObject playerObject, boolean forOthers, boolean reset) {
        SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
            UserData userData = UserData.getUser(playerObject);
            GameProfile gameProfile;
            try {
                gameProfile = skinOverlay.getSkinHandler().getGameProfile(playerObject);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            PropertyMap pm = gameProfile.getProperties();
            Property property = pm.get("textures").iterator().next();
            pm.remove("textures", property);
            pm.put("textures", userData.getSkinProperty());
            if (skinOverlay.type().equals(SkinOverlayImpl.Type.PAPER)) {
                SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) playerObject.getPlayer();
                    player.hidePlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().getPlugin(), player);
                    player.showPlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().getPlugin(), player);
                    skinOverlay.getSkinHandler().updateSkin(skinOverlay.getConfig(), playerObject, reset, userData.getSkinName());
                    if (forOthers) {
                        skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                            org.bukkit.entity.Player p = (org.bukkit.entity.Player) playerObjects.getPlayer();
                            p.hidePlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().getPlugin(), player);
                            p.showPlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().getPlugin(), player);
                        });
                    }
                }, 20L);
            } else {
                skinOverlay.getSkinHandler().updateSkin(skinOverlay.getConfig(), playerObject, reset, userData.getSkinName(), userData.getSkinProperty());
            }
        }, 20L);
    }

    public static class Request {
        private String address;
        private byte[] bytes;
        private int httpCode;
        private HttpsURLConnection httpsURLConnection;

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

        public Request openConnection(String address) throws IOException {
            this.address = address;
            final URL url = new URL(address);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
            return this;
        }

        public Request setRequestProperty(String key, String value) {
            this.httpsURLConnection.setRequestProperty(key, value);
            return this;
        }

        @SafeVarargs
        @Contract("_ -> this")
        public final Request setProperty(final ObjectMap.Pair<String, String> @NotNull ... pairs) {
            for (final ObjectMap.Pair<String, String> pair : pairs) {
                this.httpsURLConnection.setRequestProperty(pair.key(), pair.value());
            }
            return this;
        }

        public Request writeToOutputStream(final String @NotNull ... data) throws IOException {
            for (final String str : data) {
                this.httpsURLConnection.getOutputStream().write(str.getBytes());
            }
            return this;
        }

        public Request writeToOutputStream(final byte @NotNull []... data) throws IOException {
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

        public String getAddress() {
            return this.address;
        }

        public HttpsURLConnection getHttpsURLConnection() {
            return httpsURLConnection;
        }
    }

}
