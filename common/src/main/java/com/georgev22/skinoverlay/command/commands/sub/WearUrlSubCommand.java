package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

@Subcommand("url")
@CommandAlias("sowearurl")
@Permission("skinoverlay.wear.url")
@Description("Wear an overlay on a player's skin")
@CommandCompletion("<link> @players")
public class WearUrlSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer,
                          @Argument(name = "url") String urlStr,
                          @Argument(name = "target", completion = "@players", optional = true) SPlayer target) {

        try {
            URL url = new URL(urlStr);

            byte[] imageBytes = downloadImageBytes(url);
            if (imageBytes == null) {
                CommandMessages.COMMAND_INVALID_URL.msg(commandIssuer, new HashObjectMap<String, String>().append("%url%", url.toString()), true);
                return;
            }

            if (target == null && !commandIssuer.isPlayer()) {
                CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer, new HashObjectMap<String, String>().append("%command%", "url <url> <player>").append("%arg%", "target"), true);
                return;
            }

            if (target == null)
                target = mainPlugin.getPlayerProvider().getSPlayer(commandIssuer.getUniqueId());

            SkinParts skinParts = createSkinPartsFromBytes(imageBytes, url);
            applySkinToPlayer(commandIssuer, target, skinParts);

        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error executing WearUrlSubCommand:", e);
            CommandMessages.COMMAND_ERROR.msg(commandIssuer, new HashObjectMap<String, String>().append("%error%", e.getMessage()), true);
        }
    }

    private byte @Nullable [] downloadImageBytes(URL url) {
        try (InputStream stream = url.openStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.WARNING, "Failed to download image from URL: " + url, e);
            return null;
        }
    }

    private @NotNull SkinParts createSkinPartsFromBytes(byte[] bytes, URL url) throws Exception {
        SerializableBufferedImage bufferedImage = new SerializableBufferedImage(ImageIO.read(new ByteArrayInputStream(bytes)));
        return new SkinParts(bufferedImage, "customURL-" + url);
    }

    private void applySkinToPlayer(CommandIssuer issuer, SPlayer player, SkinParts skinParts) {
        mainPlugin.getSkinProvider().retrieveOrGenerateSkin(player, skinParts).thenAcceptAsync(optionalSkin -> {
            if (optionalSkin.isEmpty()) {
                CommandMessages.COMMAND_ERROR.msg(issuer, new HashObjectMap<String, String>().append("%error%", "Failed to retrieve or generate skin."), true);
                return;
            }

            Skin skin = optionalSkin.get();
            mainPlugin.getSkinApplier().setSkin(player, skin);

            CommandMessages.COMMAND_OVERLAY_DONE.msg(
                    issuer,
                    new HashObjectMap<String, String>()
                            .append("%player%", player.getName())
                            .append("%url%", skin.skinURL())
                            .append("%overlay%", skin.getSkinParts().getSkinName()),
                    true
            );
        }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
    }
}
