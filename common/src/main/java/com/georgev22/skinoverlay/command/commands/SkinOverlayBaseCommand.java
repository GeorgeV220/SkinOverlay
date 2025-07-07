package com.georgev22.skinoverlay.command.commands;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.BaseCommand;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Level;

public abstract class SkinOverlayBaseCommand extends BaseCommand {
    protected final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    @Override
    public void addSubcommand(@NotNull BaseCommand subcommand) {
        try {
            super.addSubcommand(subcommand);
        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Failed to register subcommand " + subcommand.getClass().getName(), e);
        }
    }

    protected @NotNull Optional<SPlayer> getPlayerObject(@NotNull CommandIssuer issuer, String name) {
        Optional<SPlayer> target;
        if (issuer.hasPermission("skinoverlay.wear.overlay.others")) {
            target = mainPlugin.getPlayerProvider().isOnline(name)
                    ? Optional.ofNullable(mainPlugin.getPlayerProvider().getSPlayer(name)) : Optional.empty();
            if (target.isEmpty()) {
                MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", name), true);
                return Optional.empty();
            }
        } else {
            MessagesUtil.NO_PERMISSION.msg(issuer);
            return Optional.empty();
        }
        return target;
    }
}
