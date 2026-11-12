package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.Argument;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Description;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.annotation.Usage;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;

/**
 * Restores a player's default skin or removes selected pieces through the nested {@code overlay} command.
 * Both operations use the same per-player queue as wear and add requests.
 */
@Subcommand({"clear", "reset"})
@CommandAlias("soclear")
@Permission("skinoverlay.wear.clear")
@Description("Remove all overlays or selected pieces of clothing")
@Usage("/skinoverlay clear [player] | clear overlay <overlay,...> [player]")
public class ClearSubCommand extends OverlayCommandSupport {
    @Default
    protected void handle(CommandIssuer issuer,
                          @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        SkinOverlayPlayer player = target(issuer, target);
        if (player != null) result(issuer, player, mainPlugin.getOverlayProvider().clear(player), true);
    }

    @Subcommand("overlay")
    protected void remove(CommandIssuer issuer,
                          @Argument(name = "overlays", completion = "@overlay-list") String overlays,
                          @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        SkinOverlayPlayer player = target(issuer, target);
        if (player == null) return;
        try {
            result(issuer, player, mainPlugin.getOverlayProvider().remove(player, identifiers(overlays)), false);
        } catch (IllegalArgumentException exception) {
            error(issuer, exception);
        }
    }
}
