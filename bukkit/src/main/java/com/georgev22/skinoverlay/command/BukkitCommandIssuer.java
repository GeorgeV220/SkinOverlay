package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitCommandIssuer implements CommandIssuer {

    private final CommandSender sender;

    public BukkitCommandIssuer(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        if (!isPlayer()) {
            SkinOverlay.getInstance().getAudienceProvider().console().sendMessage(component);
            return;
        }
        SkinOverlay.getInstance().getAudienceProvider().player(this.getUniqueId()).sendMessage(component);
    }

    @Override
    public <T> @NotNull T getIssuer() {
        //noinspection unchecked
        return (T) this.sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return this.sender.isOp();
    }

    @Override
    public UUID getUniqueId() {
        if (isPlayer()) {
            return ((Player) this.sender).getUniqueId();
        }
        return Utils.generateUUID("VoidChestConsole");

    }

    @Override
    public String getName() {
        return this.sender.getName();
    }
}
