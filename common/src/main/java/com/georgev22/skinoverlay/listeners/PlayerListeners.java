package com.georgev22.skinoverlay.listeners;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.events.player.SPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerListeners {

    private final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    public void onPlayerJoin(@NotNull SPlayerJoinEvent event) {
        SPlayer player = event.getPlayer();

        if (!mainPlugin.isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            // If we don't delay the publishPlayerJoin, the proxy won't receive the plugin message (idk why)
            this.mainPlugin.getScheduler().createDelayedForEntity(
                    this.mainPlugin.getPlugin(),
                    () -> mainPlugin.getMessageManager().publishPlayerJoin(player.getUniqueId()),
                    () -> {
                    },
                    player.getPlayer(),
                    20L
            );
            return;
        }

        Optional<EntityManager<PlayerData>> playerDataManagerOpt = EntityManagerRegistry.getInstance().getTyped(PlayerData.class);
        Optional<EntityManager<Skin>> skinManagerOpt = EntityManagerRegistry.getInstance().getTyped(Skin.class);

        if (playerDataManagerOpt.isEmpty() || skinManagerOpt.isEmpty()) {
            return;
        }

        EntityManager<PlayerData> playerDataManager = playerDataManagerOpt.get();
        EntityManager<Skin> skinManager = skinManagerOpt.get();

        PlayerData playerData = playerDataManager.findById(player.getUniqueId()).orElse(null);

        if (playerData == null) {
            return;
        }

        SGameProfile gameProfile = player.getGameProfile();
        SProperty property = gameProfile.getProperty("textures");

        if (property == null) {
            try {
                property = mainPlugin.getSkinProvider().getJavaSkin(player);
            } catch (IOException e) {
                mainPlugin.getLogger().log(Level.SEVERE, "Error loading Skin:", e);
                return;
            }
        }

        UUID uuid = Utils.generateUUID("default" + player.getUniqueId());
        Skin defaultSkin = skinManager.findById(uuid).orElseGet(() -> new Skin(uuid));
        defaultSkin.setProperty(property);

        playerData.setDefaultSkin(defaultSkin);

        if (playerData.getCurrentSkin() != null && !playerData.getCurrentSkin().equals(defaultSkin)) {
            mainPlugin.getSkinApplier().setSkin(player, playerData.getCurrentSkin());
        }

        skinManager.save(defaultSkin);
        playerDataManager.save(playerData);
    }

    public void onPlayerLeave(@NotNull SPlayerLeaveEvent event) {
        SPlayer player = event.getPlayer();
        EntityManagerRegistry.getInstance().getTyped(PlayerData.class)
                .ifPresent(entityManager -> entityManager.findById(player.getUniqueId())
                        .ifPresent(entityManager::save));
    }

}
