package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import org.jetbrains.annotations.NotNull;

@Subcommand({"clear", "reset"})
@CommandAlias("soclear")
@CommandCompletion("@players")
@Description("Removes the skin overlay")
@Permission("skinoverlay.wear.clear")
public class ClearSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer, @Argument(name = "target", completion = "@players", optional = true) SPlayer target) {
        if (target == null) {
            if (!commandIssuer.isPlayer()) {
                CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer,
                        Placeholder.builder(commandIssuer.audience())
                                .placeholder("%command%", "clear <player>").placeholder("%arg%", "target").build());
                return;
            }
            SPlayer player = mainPlugin.getPlayerProvider().getSPlayer(commandIssuer.getUniqueId());
            if (player == null) {
                return;
            }
            SkinParts skinParts = new SkinParts(null, "default");
            mainPlugin.getSkinProvider().retrieveOrGenerateSkin(
                            player,
                            skinParts)
                    .thenAcceptAsync(optionalSkin -> {
                        if (optionalSkin.isEmpty()) {
                            return;
                        }
                        Skin skin = optionalSkin.get();
                        mainPlugin.getSkinProvider().setSkin(player, skin);
                        CommandMessages.COMMAND_OVERLAY_RESET.msg(
                                commandIssuer,
                                Placeholder.builder(commandIssuer.audience()).placeholder("%player%", player.getName())
                                        .build()
                        );

                    }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
        } else {
            if (!target.isOnline()) {
                CommandMessages.COMMAND_OFFLINE_PLAYER.msg(
                        commandIssuer,
                        Placeholder.builder(commandIssuer.audience()).placeholder("%player%", target.getName())
                                .build());
                return;
            }
            SkinParts skinParts = new SkinParts(null, "default");
            mainPlugin.getSkinProvider().retrieveOrGenerateSkin(
                            target,
                            skinParts)
                    .thenAcceptAsync(skinOptional -> {
                        if (skinOptional.isEmpty()) {
                            return;
                        }
                        Skin skin = skinOptional.get();
                        mainPlugin.getSkinProvider().setSkin(target, skin);
                        CommandMessages.COMMAND_OVERLAY_RESET.msg(
                                commandIssuer,
                                Placeholder.builder(commandIssuer.audience()).build()
                        );

                    }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
        }
    }
}
