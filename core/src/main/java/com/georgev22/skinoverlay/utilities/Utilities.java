package com.georgev22.skinoverlay.utilities;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import com.georgev22.skinoverlay.utilities.interfaces.ImageSupplier;
import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

public class Utilities {

    private static final SkinOverlay skinOverlay = SkinOverlay.getInstance();


    public static void setSkin(SkinOptions skinOptions, @NotNull PlayerObject playerObject, String @NotNull [] properties) {
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
                user.addCustomData("skinOptions", skinOptionsToBytes(skinOptions));
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.addCustomData("skinProperty", new SProperty(properties[0], properties[1], properties[2]));
            updateSkin(playerObject, true);
            skinOverlay.getUserManager().save(user);
        });
    }

    public static void setSkin(ImageSupplier imageSupplier, SkinOptions skinOptions, @NotNull PlayerObject playerObject, @Nullable CommandIssuer commandIssuer) {
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
                byte[] profileBytes = skinOverlay.getSkinHandler().getProfileBytes(playerObject, user.getCustomData("defaultSkinProperty"));
                JsonElement json = JsonParser.parseString(new String(profileBytes));
                JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
                for (JsonElement object : properties) {
                    if (!object.getAsJsonObject().get("name").getAsString().equals("textures")) continue;
                    if (overlay == null) {
                        SGameProfile gameProfile = skinOverlay.getSkinHandler().getGameProfile(playerObject);
                        gameProfile.removeProperty("textures");
                        gameProfile.addProperty("textures", new SProperty("textures", object.getAsJsonObject().get("value").getAsString(), object.getAsJsonObject().get("signature").getAsString()));
                        user.addCustomData("skinOptions", skinOptionsToBytes(skinOptions));
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
                            user.addCustomData("skinOptions", skinOptionsToBytes(skinOptions));
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
        }).thenAccept(user -> {
            if (user != null)
                Utilities.updateSkin(playerObject, true);
            else
                skinOverlay.getLogger().log(Level.SEVERE, "User is null");
        });
    }

    public static void updateSkin(@NotNull PlayerObject playerObject, boolean forOthers) {
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
            SGameProfile gameProfile = playerObject.gameProfile();
            gameProfile.removeProperty("textures");
            gameProfile.addProperty("textures", user.getCustomData("skinProperty"));
            if (skinOverlay.type().equals(SkinOverlayImpl.Type.PAPER)) {
                SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) playerObject.player();
                    player.hidePlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                    player.showPlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                    try {
                        skinOverlay.getSkinHandler().updateSkin(playerObject, getSkinOptions(user.getCustomData("skinOptions")), new Utils.Callback<>() {
                            @Override
                            public Boolean onSuccess() {
                                if (forOthers) {
                                    skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                                        org.bukkit.entity.Player p = (org.bukkit.entity.Player) playerObjects.player();
                                        p.hidePlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                                        p.showPlayer((org.bukkit.plugin.Plugin) skinOverlay.getSkinOverlay().plugin(), player);
                                    });
                                }
                                return true;
                            }

                            @Override
                            public Boolean onFailure() {
                                return false;
                            }
                        });
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }, 20L);
            } else {
                try {
                    skinOverlay.getSkinHandler().updateSkin(playerObject, getSkinOptions(user.getCustomData("skinOptions")), user.getCustomData("skinProperty"), new Utils.Callback<>() {
                        @Override
                        public Boolean onSuccess() {
                            skinOverlay.getLogger().info("Success");
                            return true;
                        }

                        @Override
                        public Boolean onFailure() {
                            skinOverlay.getLogger().info("Error");
                            return false;
                        }
                    });
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).handle((unused, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return unused;
            }
            return unused;
        });
    }

    @Contract("_ -> new")
    public static @NotNull SProperty propertyFromLinkedTreeMap(@NotNull LinkedTreeMap<String, String> linkedTreeMap) {
        return new SProperty(linkedTreeMap.get("name"), linkedTreeMap.get("value"), linkedTreeMap.get("signature"));
    }

    public static String skinOptionsToBytes(SkinOptions skinOptions) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(skinOptions);
            final byte[] byteArray = bos.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        }
    }

    public static SkinOptions getSkinOptions(@NotNull String bytes) throws IOException, ClassNotFoundException {
        final byte[] skinOptionsBytes = Base64.getDecoder().decode(bytes);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(skinOptionsBytes); ObjectInput in = new ObjectInputStream(bis)) {
            return (SkinOptions) in.readObject();
        }
    }

    public static @Nullable String decrypt(String encryptedText) {
        try {
            byte[] salt = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 0, 16);
            KeySpec spec = new PBEKeySpec(OptionsUtil.SECRET.getStringValue().toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 16, Base64.getDecoder().decode(encryptedText).length);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @Nullable String encrypt(String plaintext) {
        try {
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(OptionsUtil.SECRET.getStringValue().toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[16 + encryptedBytes.length];
            System.arraycopy(salt, 0, combined, 0, 16);
            System.arraycopy(encryptedBytes, 0, combined, 16, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
