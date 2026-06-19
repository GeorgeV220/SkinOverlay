package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.utilities.Utils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class VelocityCommandIssuer implements CommandIssuer {

    private final CommandSource source;

    public VelocityCommandIssuer(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean isPlayer() {
        return this.source instanceof Player;
    }

    @Override
    public Audience audience() {
        return this.source;
    }

    @Override
    public <T> @NotNull T getIssuer() {
        return (T) this.source;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.source.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public UUID getUniqueId() {
        if (isPlayer()) {
            return ((Player) this.source).getUniqueId();
        }
        return Utils.generateUUID("SkinOverlayConsole");
    }

    @Override
    public String getName() {
        if (isPlayer()) {
            return ((Player) this.source).getUsername();
        } else {
            return "Console";
        }
    }
}
