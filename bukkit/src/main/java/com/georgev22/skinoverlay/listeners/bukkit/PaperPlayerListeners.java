package com.georgev22.skinoverlay.listeners.bukkit;

import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperPlayerListeners implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onPLayerClientOptionsChange(PlayerClientOptionsChangeEvent event) {
        if (OptionsUtil.EXPERIMENTAL_FEATURES.getBooleanValue()) {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            if (event.hasSkinPartsChanged()) {
                skinOverlay.getPlayer(event.getPlayer().getUniqueId()).orElseThrow().updateSkin();
            }
        }
    }

}