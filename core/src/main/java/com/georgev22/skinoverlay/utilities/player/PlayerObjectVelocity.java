package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerObjectVelocity implements PlayerObject {

    private final Player player;

    public PlayerObjectVelocity(final Player player) {
        this.player = player;
    }

    @Override
    public Object getPlayer() {
        return player;
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public String playerName() {
        return player.getUsername();
    }

    @Override
    public void sendMessage(String input) {
        VelocityMinecraftUtils.msg(player, input);
    }

    @Override
    public void sendMessage(@NotNull List<String> input) {
        VelocityMinecraftUtils.msg(player, input);
    }

    @Override
    public void sendMessage(String @NotNull ... input) {
        VelocityMinecraftUtils.msg(player, input);
    }
}
