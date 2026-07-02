package com.georgev22.skinoverlay.hooks.placeholder;

import com.georgev22.skinoverlay.BuildParameters;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.exceptions.SkinException;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.utilities.config.OverlayOptionsUtil;
import com.georgev22.skinoverlay.utilities.config.SkinConfigurationFile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.audience.Audience;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;

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
            String skinName = playerData.getCurrentSkin().getSkinParts().getSkinName();
            if (skinName.equalsIgnoreCase("custom")) {
                return "Custom";
            }
            SkinConfigurationFile skinConfigurationFile = this.skinOverlay.getSkinFileCache().getCacheSkinConfig(skinName);

            if (skinConfigurationFile == null) {
                this.skinOverlay.getLogger().log(Level.SEVERE, "SkinConfigurationFile cannot be null", new SkinException("SkinConfigurationFile cannot be null"));
                return "Unknown";
            }

            FileConfiguration fileConfiguration = skinConfigurationFile.getFileConfiguration();
            return OverlayOptionsUtil.PLACEHOLDER.getStringValue(fileConfiguration);
        }

        return null;

    }
}
