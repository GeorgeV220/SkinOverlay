package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandContext;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.CommandCompletion;
import com.georgev22.skinoverlay.command.annotation.Description;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

@Subcommand("url")
@Permission("skinoverlay.wear.url")
@Description("Wear an overlay on a player's skin")
@CommandCompletion("<link> false|true|@players false|true false|true false|true false|true false|true false|true @players")
public class WearUrlSubCommand extends SkinOverlayBaseCommand {

    @Override
    protected void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        if (args.length < 1) {
            sendInsufficientArguments(commandIssuer);
            return;
        }

        try {
            URL url = new URL(args[0]);
            Optional<SPlayer> targetPlayer = resolveTargetPlayer(commandIssuer, args);
            if (targetPlayer.isEmpty()) {
                return;
            }

            byte[] imageBytes = downloadImageBytes(url);
            if (imageBytes == null) {
                MessagesUtil.INVALID_URL.msg(commandIssuer, new HashObjectMap<String, String>().append("%url%", url.toString()), true);
                return;
            }

            SkinParts skinParts = createSkinPartsFromBytes(imageBytes, url);
            applySkinToPlayer(commandIssuer, targetPlayer.get(), skinParts);

        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error executing WearUrlSubCommand:", e);
            MessagesUtil.ERROR.msg(commandIssuer, new HashObjectMap<String, String>().append("%error%", e.getMessage()), true);
        }
    }

    private void sendInsufficientArguments(CommandIssuer issuer) {
        MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(
                issuer,
                new HashObjectMap<String, String>().append("%command%", "url <url> <player>"),
                true
        );
    }

    private Optional<SPlayer> resolveTargetPlayer(CommandIssuer issuer, String @NotNull [] args) {
        if (args.length > 1) {
            return getPlayerObject(issuer, args[1]);
        } else if (issuer.isPlayer()) {
            return Optional.of(mainPlugin.getPlayerProvider().getSPlayer(issuer.getUniqueId()));
        } else {
            sendInsufficientArguments(issuer);
            return Optional.empty();
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
                MessagesUtil.ERROR.msg(issuer, new HashObjectMap<String, String>().append("%error%", "Failed to retrieve or generate skin."), true);
                return;
            }

            Skin skin = optionalSkin.get();
            mainPlugin.getSkinApplier().setSkin(player, skin);

            MessagesUtil.DONE.msg(
                    issuer,
                    new HashObjectMap<String, String>()
                            .append("%player%", player.getName())
                            .append("%url%", skin.skinURL()),
                    true
            );
        }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
    }
}
