package com.georgev22.skinoverlay.listeners;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.EventHandlerAnnotation;
import com.georgev22.skinoverlay.event.HandlerPriority;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerJoinEvent;
import com.georgev22.skinoverlay.event.events.player.SkinOverlayPlayerLeaveEvent;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.registry.EntityManagerRegistry;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import com.georgev22.skinoverlay.storage.EntityManager;
import com.georgev22.skinoverlay.storage.data.PlayerData;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.SerializableBufferedImage;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.georgev22.skinoverlay.utilities.skin.SkinParts;
import com.georgev22.skinoverlay.utilities.skin.Model;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerListeners {

    private final SkinOverlay mainPlugin = SkinOverlay.getInstance();

    @EventHandlerAnnotation(priority = HandlerPriority.LOWEST)
    public void onPlayerJoin(@NotNull SkinOverlayPlayerJoinEvent event) {
        SkinOverlayPlayer player = event.getPlayer();

        if (!mainPlugin.isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
            // If we don't delay the publishPlayerJoin, the proxy won't receive the plugin message (idk why)
            this.mainPlugin.getScheduler().createDelayedForEntity(
                    this.mainPlugin.getPlugin(),
                    () -> mainPlugin.getMessageManager().publishPlayerJoin(player.getUniqueId()),
                    () -> {
                    },
                    player.getPlatformPlayer(),
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

        Optional<PlayerData> playerDataOptional = playerDataManager.findById(player.getUniqueId());
        if (playerDataOptional.isEmpty()) {
            playerDataOptional = playerDataManager.create(player.getUniqueId(), null);
            if (playerDataOptional.isEmpty()) {
                return;
            }
        }

        PlayerData playerData = playerDataOptional.get();

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

        BufferedImage defaultSkinImage;
        try {
            defaultSkinImage = mainPlugin.getSkinProvider().getSkinImage(property).getBufferedImage();
        } catch (IOException e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error loading Default Skin:", e);
            return;
        }

        SerializableBufferedImage defaultSkinImageSerializable = new SerializableBufferedImage(defaultSkinImage);

        UUID uuid = Utils.generateUUID("default" + player.getUniqueId());
        Optional<Skin> defaultSkinOptional = skinManager.findById(uuid);
        if (defaultSkinOptional.isEmpty()) {
            defaultSkinOptional = skinManager.create(uuid, null);
            if (defaultSkinOptional.isEmpty()) {
                return;
            }
        }
        Skin defaultSkin = defaultSkinOptional.get();
        Skin previousDefault = playerData.getDefaultSkin();
        Model previousModel = previousDefault == null ? null : previousDefault.skinModel();
        defaultSkin.setSkinParts(new SkinParts(defaultSkinImageSerializable, "default"));
        defaultSkin.setProperty(property);
        defaultSkin.setBaseImageUUID(mainPlugin.getSkinProvider().createBaseImageUUID(defaultSkinImage));

        playerData.setDefaultSkin(defaultSkin);

        Skin currentSkin = playerData.getCurrentSkin();
        List<String> equipped = playerData.getEquippedOverlays();
        boolean modelChanged = previousModel != null && previousModel != defaultSkin.skinModel();
        boolean rebuild = !equipped.isEmpty() && (currentSkin == null
                || currentSkin.equals(defaultSkin) || modelChanged
                || !defaultSkin.getBaseImageUUID().equals(currentSkin.getBaseImageUUID()));

        if (currentSkin == null || currentSkin.equals(defaultSkin) || rebuild) {
            playerData.setCurrentSkin(defaultSkin);
        } else {
            mainPlugin.getSkinProvider().setSkinOnJoin(player, currentSkin, equipped);
        }

        skinManager.save(defaultSkin);
        playerDataManager.save(playerData);

        if (rebuild) {
            try {
                mainPlugin.getOverlayProvider().wear(player, mainPlugin.getOverlayProvider().equipped(player))
                        .whenComplete((skin, error) -> {
                            if (error != null) {
                                mainPlugin.getLogger().log(Level.SEVERE, "Error rebuilding overlays for " + player.getName(), error);
                            }
                        });
            } catch (RuntimeException exception) {
                mainPlugin.getLogger().log(Level.SEVERE, "Error resolving overlays for " + player.getName(), exception);
            }
        }
    }

    @EventHandlerAnnotation(priority = HandlerPriority.LOWEST)
    public void onPlayerLeave(@NotNull SkinOverlayPlayerLeaveEvent event) {
        SkinOverlayPlayer player = event.getPlayer();
        EntityManagerRegistry.getInstance().getTyped(PlayerData.class)
                .ifPresent(entityManager -> entityManager.findById(player.getUniqueId())
                        .ifPresent(entityManager::save));
        this.mainPlugin.getPlayerProvider().remove(player.getUniqueId());
    }

}
