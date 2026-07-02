package com.georgev22.skinoverlay.hooks.placeholder;

import com.georgev22.skinoverlay.BuildParameters;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.config.OverlayOptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class PlaceholderAPIHook extends PlaceholderExpansion implements PlaceholderHook {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

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
    public @NonNull String getIdentifier() {
        return BuildParameters.PLUGIN_NAME.toLowerCase();
    }

    @Override
    public @NonNull String getAuthor() {
        return BuildParameters.AUTHOR;
    }

    @Override
    public @NonNull String getVersion() {
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
    public @Nullable String onRequest(OfflinePlayer player, @NonNull String params) {
        if (player == null || params.isEmpty()) return null;

        Optional<EntityManager<PlayerData>> entityManagerOpt = EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
        if (entityManagerOpt.isEmpty()) return null;

        EntityManager<PlayerData> entityManager = entityManagerOpt.get();

        Optional<PlayerData> playerDataOptional = entityManager.findById(player.getUniqueId());
        if (playerDataOptional.isEmpty()) return null;

        PlayerData playerData = playerDataOptional.get();

        return handleParams(playerData, params);
    }

    private @Nullable String handleParams(PlayerData data, @NonNull String params) {
        return switch (params.toLowerCase()) {
            case "overlay" -> getOverlay(data);
            case "overlay_raw" -> data.getCurrentSkin().getSkinParts().getSkinName();
            default -> null;
        };
    }

    private String getOverlay(@NonNull PlayerData playerData) {
        String skinName = playerData.getCurrentSkin()
                .getSkinParts()
                .getSkinName();

        if (skinName == null) {
            return OptionsUtil.UNKNOWN_SKIN_NAME.getStringValue();
        }

        if (skinName.equalsIgnoreCase("default")) {
            return OptionsUtil.DEFAULT_SKIN_NAME.getStringValue();
        }

        if (skinName.equalsIgnoreCase("custom")) {
            return OptionsUtil.CUSTOM_SKIN_NAME.getStringValue();
        }

        if (skinName.startsWith("customURL-")) {
            return OptionsUtil.CUSTOM_SKIN_URL_NAME.getStringValue();
        }

        SkinConfigurationFile file = skinOverlay.getSkinFileCache()
                .getCacheSkinConfig(skinName);

        if (file == null) {
            return OptionsUtil.UNKNOWN_SKIN_NAME.getStringValue();
        }

        return OverlayOptionsUtil.PLACEHOLDER.getStringValue(file.getFileConfiguration());
    }
}
