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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Subcommand({"wear", "overlay"})
@CommandAlias("sowear")
@Permission("skinoverlay.wear.overlay")
@Description("Wear an overlay on a player's skin")
@CommandCompletion("@overlays @players")
public class WearSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer,
                          @Argument(name = "overlay", completion = "@overlays") String overlay,
                          @Argument(name = "target", completion = "@players", optional = true) SPlayer target) {
        if (overlay == null || overlay.isEmpty()) {
            CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer, new HashObjectMap<String, String>()
                    .append("%command%", "wear <overlay> <player>").append("%arg%", "overlay"), true);
            return;
        }

        if (target == null) {
            if (!commandIssuer.isPlayer()) {
                CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer, new HashObjectMap<String, String>()
                        .append("%command%", "wear <overlay> <player>").append("%arg%", "target"), true);
                return;
            }
            target = mainPlugin.getPlayerProvider().getSPlayer(commandIssuer.getUniqueId());
        }
        SkinParts skinParts;
        try {
            File overlayFile = new File(mainPlugin.getSkinsDataFolder(), overlay + ".png");
            if (!overlayFile.exists()) {
                CommandMessages.COMMAND_OVERLAY_NOT_FOUND.msg(commandIssuer, new HashObjectMap<String, String>().append("%overlay%", overlay), true);
                return;
            }
            skinParts = new SkinParts(new SerializableBufferedImage(ImageIO.read(overlayFile)), overlay);
        } catch (IOException e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error while trying to load the skin: ", e);
            return;
        }
        SPlayer finalTarget = target;
        mainPlugin.getSkinProvider()
                .retrieveOrGenerateSkin(
                        target,
                        skinParts
                ).thenAcceptAsync(optionalSkin -> {
                    if (optionalSkin.isEmpty()) {
                        mainPlugin.getLogger().info("Skin is null");
                        return;
                    }
                    Skin skin = optionalSkin.get();
                    mainPlugin.getSkinApplier()
                            .setSkin(finalTarget, skin);
                    CommandMessages.COMMAND_OVERLAY_DONE.msg(
                            commandIssuer,
                            new HashObjectMap<String, String>()
                                    .append("%player%", finalTarget.getName())
                                    .append("%url%", skin.skinURL())
                                    .append("%name%", skin.getSkinParts().getSkinName())
                                    .append("%skinParts%", skin.getSkinParts().toString()),
                            true
                    );
                }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
    }
}
