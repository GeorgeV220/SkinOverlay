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
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Subcommand({"clear", "reset"})
@CommandCompletion("@players")
@Description("Removes the skin overlay")
@Permission("skinoverlay.wear.clear")
public class ClearSubCommand extends SkinOverlayBaseCommand {

    @Override
    protected void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        if (args.length == 0) {
            if (!commandIssuer.isPlayer()) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(commandIssuer,
                        new HashObjectMap<String, String>().append("%command%", "clear <player>"), true);
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
                        mainPlugin.getSkinApplier().setSkin(player, skin);
                        MessagesUtil.RESET.msg(
                                commandIssuer,
                                new HashObjectMap<String, String>().append("%player%", player.getName()),
                                true
                        );

                    }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
        } else {
            Optional<SPlayer> optionalPlayerObject = getPlayerObject(commandIssuer, args[0]);
            if (optionalPlayerObject.isEmpty()) {
                MessagesUtil.OFFLINE_PLAYER.msg(commandIssuer, new HashObjectMap<String, String>().append("%player%", args[0]), true);
                return;
            }
            SkinParts skinParts = new SkinParts(null, "default");
            mainPlugin.getSkinProvider().retrieveOrGenerateSkin(
                            optionalPlayerObject.get(),
                            skinParts)
                    .thenAcceptAsync(skinOptional -> {
                        if (skinOptional.isEmpty()) {
                            return;
                        }
                        Skin skin = skinOptional.get();
                        mainPlugin.getSkinApplier().setSkin(optionalPlayerObject.get(), skin);
                        MessagesUtil.RESET.msg(
                                commandIssuer,
                                new HashObjectMap<String, String>().append("%player%", optionalPlayerObject.get().getName()),
                                true
                        );

                    }, runnable -> SkinOverlay.getInstance().getScheduler().runTask(SkinOverlay.getInstance().getPlugin(), runnable));
        }
    }
}
