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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

@Subcommand({"wear", "overlay"})
@Permission("skinoverlay.wear.overlay")
@Description("Wear an overlay on a player's skin")
@CommandCompletion("@overlays @players")
public class WearSubCommand extends SkinOverlayBaseCommand {

    @Override
    protected void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        if (args.length < 1) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(commandIssuer, new HashObjectMap<String, String>()
                    .append("%command%", "wear <overlay> <player>"), true);
            return;
        }

        var overlay = args[0];
        SPlayer target;

        if (args.length > 1) {
            Optional<SPlayer> optionalSPlayer = getPlayerObject(commandIssuer, args[1]);
            if (optionalSPlayer.isEmpty()) return;
            target = optionalSPlayer.get();
        } else if (!(commandIssuer.isPlayer())) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(commandIssuer,
                    new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
            return;
        } else {
            target = mainPlugin.getPlayerProvider().getSPlayer(commandIssuer.getUniqueId());
        }
        SkinParts skinParts;
        try {
            File overlayFile = new File(mainPlugin.getSkinsDataFolder(), overlay + ".png");
            if (!overlayFile.exists()) {
                MessagesUtil.OVERLAY_NOT_FOUND.msg(commandIssuer, new HashObjectMap<String, String>().append("%overlay%", overlay), true);
                return;
            }
            skinParts = new SkinParts(new SerializableBufferedImage(ImageIO.read(overlayFile)), overlay);
        } catch (IOException e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error while trying to load the skin: ", e);
            return;
        }
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
                            .setSkin(target, skin);
                    MessagesUtil.DONE.msg(
                            commandIssuer,
                            new HashObjectMap<String, String>()
                                    .append("%player%", target.getName())
                                    .append("%url%", skin.skinURL())
                                    .append("%name%", skin.getSkinParts().getSkinName())
                                    .append("%skinParts%", skin.getSkinParts().toString()),
                            true
                    );
                }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
    }
}
