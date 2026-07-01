package com.georgev22.skinoverlay.hooks.placeholder;

import com.georgev22.skinoverlay.BuildParameters;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlaceholderAPIHook extends PlaceholderExpansion implements PlaceholderHook {

    @Override
    public String resolve(Audience audience, String input) {
        if (audience == null) return input;
        try {
            return me.clip.placeholderapi.PlaceholderAPI
                    .setBracketPlaceholders(audience instanceof Player player ? player : null, input);
        } catch (Throwable ignored) {
            return input;
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return BuildParameters.PLUGIN_NAME.toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return BuildParameters.AUTHOR;
    }

    @Override
    public @NotNull String getVersion() {
        return BuildParameters.VERSION;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        Optional<EntityManager<PlayerData>> entityManagerOpt = EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
        if (entityManagerOpt.isEmpty()) return null;

        EntityManager<PlayerData> entityManager = entityManagerOpt.get();

        Optional<PlayerData> playerDataOptional = entityManager.findById(player.getUniqueId());
        if (playerDataOptional.isEmpty()) return null;

        PlayerData playerData = playerDataOptional.get();

        if (params.equalsIgnoreCase("overlay")) {
            return playerData.getCurrentSkin().getSkinParts().getSkinName();
        }

        return null;

    }
}
