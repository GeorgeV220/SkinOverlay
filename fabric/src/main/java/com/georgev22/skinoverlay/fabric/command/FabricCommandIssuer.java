package com.georgev22.skinoverlay.fabric.command;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.fabric.utilities.FabricAdventure;
import com.georgev22.skinoverlay.utilities.Utils;
import net.kyori.adventure.audience.Audience;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FabricCommandIssuer implements CommandIssuer {

    private final CommandSourceStack source;

    public FabricCommandIssuer(@NotNull CommandSourceStack source) {
        this.source = source;
    }

    @Override
    public boolean isPlayer() {
        return source.getEntity() instanceof ServerPlayer;
    }

    @Override
    public Audience audience() {
        return FabricAdventure.getFabricAudiences().audience(source);
    }

    @Override
    public <T> @NotNull T getIssuer() {
        Object issuer = isPlayer() ? source.getPlayer() : source;
        //noinspection unchecked
        return (T) issuer;
    }

    @Override
    public boolean hasPermission(String permission) {
        return permission == null || permission.isBlank() || isOp();
    }

    @Override
    public boolean isOp() {
        if (!isPlayer()) {
            return true;
        }
        ServerPlayer player = source.getPlayer();
        return player != null && source.getServer().getPlayerList().isOp(new NameAndId(player.getGameProfile()));
    }

    @Override
    public UUID getUniqueId() {
        ServerPlayer player = source.getPlayer();
        return player == null ? Utils.generateUUID("SkinOverlayConsole") : player.getUUID();
    }

    @Override
    public String getName() {
        ServerPlayer player = source.getPlayer();
        return player == null ? source.getTextName() : player.getPlainTextName();
    }

    public CommandSourceStack source() {
        return source;
    }
}
