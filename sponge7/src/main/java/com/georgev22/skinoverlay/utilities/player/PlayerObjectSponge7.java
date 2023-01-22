package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.Sponge7MinecraftUtils;
import com.georgev22.skinoverlay.SkinOverlaySponge7;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.UUID;

public class PlayerObjectSponge7 extends PlayerObject {

    private final User user;

    public PlayerObjectSponge7(Player serverPlayer) {
        this.user = serverPlayer;
    }

    public PlayerObjectSponge7(User user) {
        this.user = user;
    }

    @Override
    public Player getPlayer() {
        return user.getPlayer().orElseThrow();
    }

    public Audience audience() {
        return SkinOverlaySponge7.getInstance().adventure().player(getPlayer());
    }

    @Override
    public UUID playerUUID() {
        return user.getUniqueId();
    }

    @Override
    public String playerName() {
        return user.getName();
    }

    @Override
    public void sendMessage(String input) {
        Sponge7MinecraftUtils.msg(audience(), input);
    }

    @Override
    public void sendMessage(List<String> input) {
        Sponge7MinecraftUtils.msg(audience(), input);
    }

    @Override
    public void sendMessage(String... input) {
        Sponge7MinecraftUtils.msg(audience(), input);
    }

    @Override
    public void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        Sponge7MinecraftUtils.msg(audience(), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        Sponge7MinecraftUtils.msg(audience(), input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        Sponge7MinecraftUtils.msg(audience(), input, placeholders, ignoreCase);
    }

    @Override
    public boolean isOnline() {
        return user.isOnline();
    }
}
