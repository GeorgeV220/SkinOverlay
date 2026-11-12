package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.Argument;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Description;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.annotation.Usage;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;

/**
 * Adds one or more registered overlays to an existing loadout through the queued overlay API.
 */
@Subcommand({"add", "equip"})
@Permission("skinoverlay.wear.overlay")
@Description("Add overlays to the player's current clothing")
@Usage("/skinoverlay add <overlay,...> [player]")
public class AddSubCommand extends OverlayCommandSupport {
    @Default
    protected void handle(CommandIssuer issuer,
                          @Argument(name = "overlays", completion = "@overlay-list") String overlays,
                          @Argument(name = "target", completion = "@players", optional = true) SkinOverlayPlayer target) {
        wear(issuer, target, overlays, true);
    }
}
