package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.skin.Overlay;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

/**
 * Shares target resolution, comma-separated argument parsing and completion messages for clothing commands.
 */
abstract class OverlayCommandSupport extends SkinOverlayBaseCommand {
    protected SkinOverlayPlayer target(CommandIssuer issuer, SkinOverlayPlayer requested) {
        SkinOverlayPlayer player = requested;
        if (player == null && issuer.isPlayer())
            player = mainPlugin.getPlayerProvider().getSkinOverlayPlayer(issuer.getUniqueId());
        if (player == null) {
            CommandMessages.COMMAND_MISSING_ARGUMENT.msg(issuer, Placeholder.builder(issuer.audience())
                    .placeholder("%command%", "skinoverlay").placeholder("%arg%", "target").build());
        } else if (!player.isOnline()) {
            CommandMessages.COMMAND_OFFLINE_PLAYER.msg(issuer, Placeholder.builder(issuer.audience())
                    .placeholder("%player%", player.getName()).build());
            return null;
        }
        return player;
    }

    protected List<String> identifiers(String input) {
        List<String> identifiers = Arrays.stream(input.split(",", -1)).map(String::trim).toList();
        if (identifiers.stream().anyMatch(String::isEmpty))
            throw new IllegalArgumentException("Specify comma-separated overlay identifiers without empty entries");
        return identifiers;
    }

    protected List<Overlay> overlays(String input) {
        return identifiers(input).stream().map(mainPlugin.getOverlayRegistry()::require).toList();
    }

    protected void wear(CommandIssuer issuer, SkinOverlayPlayer requested, String input, boolean add) {
        SkinOverlayPlayer player = target(issuer, requested);
        if (player == null) return;
        try {
            List<Overlay> overlays = overlays(input);
            result(issuer, player, add ? mainPlugin.getOverlayProvider().add(player, overlays)
                    : mainPlugin.getOverlayProvider().wear(player, overlays), false);
        } catch (IllegalArgumentException exception) {
            error(issuer, exception);
        }
    }

    protected void result(CommandIssuer issuer, SkinOverlayPlayer player, CompletableFuture<Skin> future, boolean reset) {
        future.whenCompleteAsync((skin, failure) -> {
            if (failure != null) {
                error(issuer, failure);
                return;
            }
            (reset ? CommandMessages.COMMAND_OVERLAY_RESET : CommandMessages.COMMAND_OVERLAY_DONE).msg(issuer,
                    Placeholder.builder(issuer.audience())
                            .placeholder("%player%", player.getName())
                            .placeholder("%url%", Objects.requireNonNullElse(skin.skinURL(), ""))
                            .placeholder("%name%", skin.getSkinParts().getSkinName())
                            .placeholder("%skinParts%", skin.getSkinParts().toString()).build());
        }, runnable -> mainPlugin.getScheduler().runTask(mainPlugin.getPlugin(), runnable));
    }

    protected void error(CommandIssuer issuer, Throwable failure) {
        while (failure instanceof CompletionException && failure.getCause() != null) failure = failure.getCause();
        if (!(failure instanceof IllegalArgumentException))
            mainPlugin.getLogger().log(Level.WARNING, "Overlay operation failed", failure);
        CommandMessages.COMMAND_OVERLAY_ERROR.msg(issuer, Placeholder.builder(issuer.audience())
                .placeholder("%error%", Objects.requireNonNullElse(failure.getMessage(), "Overlay operation failed")).build());
    }
}
