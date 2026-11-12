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
 * Replaces a player's loadout with a registered overlay or a comma-separated combination.
 * The legacy single-overlay syntax and aliases use the same queued operation.
 */
@Subcommand({"wear", "overlay", "multioverlay"})
@CommandAlias("sowear")
@Permission("skinoverlay.wear.overlay")
@Description("Replace a player's overlays with one overlay or a comma-separated combination")
@Usage("/skinoverlay wear <overlay,...> [player]")
public class WearSubCommand extends OverlayCommandSupport {
    @Default
    protected void handle(CommandIssuer issuer,
                          @Argument(name = "overlays", completion = "@overlay-list") String overlays,
                          @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        wear(issuer, target, overlays, false);
    }
}
