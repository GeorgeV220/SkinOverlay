package com.georgev22.skinoverlay.listeners.bungee;

import com.georgev22.skinoverlay.SkinOverlay;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class DeveloperInformListener implements Listener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        skinOverlay.getPlayer(e.getPlayer().getUniqueId()).orElseThrow().developerInform();
    }
}
