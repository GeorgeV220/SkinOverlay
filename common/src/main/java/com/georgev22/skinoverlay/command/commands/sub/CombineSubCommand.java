package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.Argument;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Description;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.annotation.Usage;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.utilities.skin.Overlay;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Saves a reusable named combination on a worker thread and reports its registry identifier.
 * This command creates an overlay without equipping it on a player.
 */
@Subcommand("combine")
@Permission("skinoverlay.overlay.combine")
@Description("Create and save a named overlay combination")
@Usage("/skinoverlay combine <name> <overlay,...>")
public class CombineSubCommand extends OverlayCommandSupport {
    @Default
    protected void handle(CommandIssuer issuer,
                          @Argument(name = "name") String name,
                          @Argument(name = "overlays", completion = "@overlay-list") String overlays) {
        try {
            List<Overlay> components = overlays(overlays);
            CompletableFuture.supplyAsync(() -> {
                try {
                    return mainPlugin.getOverlayRegistry().combine(name, components);
                } catch (IOException exception) {
                    throw new CompletionException(exception);
                }
            }).whenCompleteAsync((overlay, failure) -> {
                if (failure != null) error(issuer, failure);
                else CommandMessages.COMMAND_OVERLAY_SAVED.msg(issuer, Placeholder.builder(issuer.audience())
                        .placeholder("%overlay%", overlay.identifier()).build());
            }, runnable -> mainPlugin.getScheduler().runTask(mainPlugin.getPlugin(), runnable));
        } catch (IllegalArgumentException exception) {
            error(issuer, exception);
        }
    }
}
