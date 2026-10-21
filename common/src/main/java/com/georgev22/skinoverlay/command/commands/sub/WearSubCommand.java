package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.skin.Overlay;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Subcommand({"wear", "overlay"})
@CommandAlias("sowear")
@Permission("skinoverlay.wear.overlay")
@Description("Wear an overlay on a player's skin")
@CommandCompletion("@overlays @players")
public class WearSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer,
                          @Argument(name = "overlay", completion = "@overlays") Overlay overlay,
                          @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        if (overlay == null) {
            CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer, Placeholder.builder(commandIssuer.audience())
                    .placeholder("%command%", "wear <overlay> <player>").placeholder("%arg%", "overlay").build());
            return;
        }

        mainPlugin.getLogger().info("Overlay: " + overlay.key());

        if (target == null && commandIssuer.isPlayer()) {
            target = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(commandIssuer.getUniqueId());
        }

        if (target == null) {
            CommandMessages.COMMAND_MISSING_ARGUMENT.msg(
                    commandIssuer,
                    Placeholder.builder(commandIssuer.audience())
                            .placeholder("%command%", "wear <overlay> <player>")
                            .placeholder("%arg%", "target")
                            .build()
            );
            return;
        }

        SkinOverlayPlayer finalTarget = target;
        mainPlugin.getSkinProvider()
                .retrieveOrGenerateSkin(
                        target,
                        overlay
                ).thenAcceptAsync(optionalSkin -> {
                    if (optionalSkin.isEmpty()) {
                        mainPlugin.getLogger().info("Skin is null");
                        return;
                    }
                    Skin skin = optionalSkin.get();
                    mainPlugin.getSkinProvider()
                            .setSkin(finalTarget, skin);
                    CommandMessages.COMMAND_OVERLAY_DONE.msg(
                            commandIssuer,
                            Placeholder.builder(commandIssuer.audience())
                                    .placeholder("%player%", finalTarget.getName())
                                    .placeholder("%url%", Objects.requireNonNullElse(skin.skinURL(), ""))
                                    .placeholder("%name%", skin.getSkinParts().getSkinName())
                                    .placeholder("%skinParts%", skin.getSkinParts().toString()).build()
                    );
                }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
    }
}
