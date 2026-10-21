package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

@Subcommand({"clear", "reset"})
@CommandAlias("soclear")
@CommandCompletion("@players")
@Description("Removes the skin overlay")
@Permission("skinoverlay.wear.clear")
public class ClearSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer, @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        if (target == null) {
            if (!commandIssuer.isPlayer()) {
                CommandMessages.COMMAND_MISSING_ARGUMENT.msg(commandIssuer,
                        Placeholder.builder(commandIssuer.audience())
                                .placeholder("%command%", "clear <player>").placeholder("%arg%", "target").build());
                return;
            }
        }
        CommandIssuer resetTarget = target == null ? commandIssuer : target;
        SkinOverlayPlayer player = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(commandIssuer.getUniqueId());
        if (player == null) {
            CommandMessages.COMMAND_OFFLINE_PLAYER.msg(resetTarget, Placeholder.builder(commandIssuer.audience())
                    .placeholder("%player%", resetTarget.getName()).build());
            return;
        }

        EntityManager<PlayerData> entityManager = EntityManagerRegistry.getInstance()
                .getTyped(PlayerData.class).orElse(null);

        if (entityManager == null) {
            return;
        }

        PlayerData playerData = entityManager.findById(resetTarget.getUniqueId()).orElse(null);

        if (playerData == null) {
            return;
        }

        Skin defaultPlayerSkin = playerData.getDefaultSkin();

        mainPlugin.getSkinProvider().setSkin(player, defaultPlayerSkin);
        CommandMessages.COMMAND_OVERLAY_RESET.msg(
                commandIssuer,
                Placeholder.builder(commandIssuer.audience()).placeholder("%player%", player.getName())
                        .build()
        );
    }
}
