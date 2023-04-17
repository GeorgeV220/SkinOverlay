package com.georgev22.skinoverlay.listeners.bukkit;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import net.glowstone.events.player.PlayerClientOptionsChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GlowStonePlayerListeners implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClientSettingsChange(PlayerClientOptionsChangeEvent event) {
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
