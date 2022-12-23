package com.georgev22.skinoverlay.utilities;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.handler.SkinHandler.Request;
import com.georgev22.skinoverlay.utilities.interfaces.ImageSupplier;
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
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static void setSkin(ImageSupplier imageSupplier, String skinName, PlayerObject playerObject) {
        SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
            Image overlay;
            try {
                overlay = imageSupplier.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UserData userData = UserData.getUser(playerObject);
            try {
                byte[] profileBytes = skinOverlay.getSkinHandler().getProfileBytes(playerObject, userData.getDefaultSkinProperty() != null);
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
                    Request request = new Request("https://api.mineskin.org/generate/upload?visibility=1").postRequest().setProperty(ObjectMap.Pair.create("Content-Type", ("multipart/form-data;boundary=" + boundary))).writeToOutputStream(twoHyphens + boundary + crlf, "Content-Disposition: form-data; name=\"file\";filename=\"file.png\"" + crlf, crlf).writeToOutputStream(new byte[][]{stream.toByteArray()}).writeToOutputStream(crlf, twoHyphens + boundary + twoHyphens + crlf).closeOutputStream().finalizeRequest();
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
                            skinOverlay.getLogger().log(Level.INFO, Utils.placeHolder(MessagesUtil.DONE.getMessages()[0], new HashObjectMap<String, String>().append("%url%", texture.get("url").getAsString()), true));
                        }
                    }
                    skinOverlay.getLogger().log(Level.SEVERE, "Unknown error code: " + request.getHttpCode());
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
            if (!skinOverlay.isBungee()) {
                SchedulerManager.getScheduler().runTaskLater(skinOverlay.getClass(), () -> {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) playerObject.getPlayer();
                    player.hidePlayer(player);
                    player.showPlayer(player);
                    skinOverlay.getSkinHandler().updateSkin(skinOverlay.getConfig(), playerObject, reset, userData.getSkinName());
                    if (forOthers) {
                        skinOverlay.onlinePlayers().stream().filter(playerObjects -> playerObjects != playerObject).forEach(playerObjects -> {
                            org.bukkit.entity.Player p = (org.bukkit.entity.Player) playerObjects.getPlayer();
                            p.hidePlayer(player);
                            p.showPlayer(player);
                        });
                    }
                }, 20L);
            } else {
                skinOverlay.getSkinHandler().updateSkin(skinOverlay.getConfig(), playerObject, reset, userData.getSkinName(), userData.getSkinProperty());
            }
        }, 20L);
    }

    public static @NotNull GameProfile createGameProfile(@NotNull PlayerObject playerObject) {
        GameProfile gameProfile = new GameProfile(playerObject.playerUUID(), playerObject.playerName());
        try {
            gameProfile.getProperties().put("textures", skinOverlay.getSkinHandler().getSkin(playerObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gameProfile;
    }


}
