package com.georgev22.skinoverlay.command.commands;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.BaseCommand;
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
}
