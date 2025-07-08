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
        Optional<EntityManager<PlayerData>> optionalPlayerDataEntityManager = EntityManagerRegistry.getManager(PlayerData.class);
        if (optionalPlayerDataEntityManager.isEmpty()) {
            return;
        }

        Optional<EntityManager<Skin>> optionalSkinEntityManager = EntityManagerRegistry.getManager(Skin.class);
        if (optionalSkinEntityManager.isEmpty()) {
            return;
        }

        EntityManager<PlayerData> playerDataEntityManager = optionalPlayerDataEntityManager.get();

        EntityManager<Skin> skinEntityManager = optionalSkinEntityManager.get();

        Optional<PlayerData> optionalPlayerData = playerDataEntityManager.findById(player.getUniqueId());
        if (optionalPlayerData.isEmpty()) {
            optionalPlayerData = playerDataEntityManager.create(player.getUniqueId().toString(), ignore -> {
            });
            if (optionalPlayerData.isEmpty()) {
                return;
            }
        }
        SGameProfile gameProfile = player.getGameProfile();
        SProperty property = gameProfile.getProperty("textures");
        if (property == null) {
            try {
                property = mainPlugin.getSkinProvider().getJavaSkin(player);
            } catch (IOException e) {
                this.mainPlugin.getLogger().log(Level.SEVERE, "Error loading Skin:", e);
                return;
            }
        }
        UUID uuid = Utils.generateUUID("default" + player.getUniqueId().toString());
        Skin defaultSkin = optionalSkinEntityManager.get().findById(uuid).orElse(new Skin(uuid));
        defaultSkin.setProperty(property);

        PlayerData playerData = optionalPlayerData.get();
        playerData.setDefaultSkin(defaultSkin);
        if (playerData.getCurrentSkin() != null && !playerData.getCurrentSkin().equals(defaultSkin)) {
            this.mainPlugin.getSkinApplier().setSkin(player, playerData.getCurrentSkin());
        }

        skinEntityManager.save(defaultSkin);
        playerDataEntityManager.save(playerData);
    }

    public void onPlayerLeave(@NotNull SPlayerLeaveEvent event) {
        SPlayer player = event.getPlayer();
        Optional<EntityManager<PlayerData>> optionalPlayerDataEntityManager = EntityManagerRegistry.getManager(PlayerData.class);
        if (optionalPlayerDataEntityManager.isEmpty()) {
            return;
        }
        EntityManager<PlayerData> playerDataEntityManager = optionalPlayerDataEntityManager.get();
        Optional<PlayerData> optionalPlayerData = playerDataEntityManager.findById(player.getUniqueId());
        if (optionalPlayerData.isEmpty()) {
            return;
        }
        PlayerData playerData = optionalPlayerData.get();
        if (!this.mainPlugin.isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            return;
        }
        playerDataEntityManager.save(playerData);
    }

}
