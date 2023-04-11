package com.georgev22.skinoverlay.listeners;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.event.EventListener;
import com.georgev22.skinoverlay.event.EventPriority;
import com.georgev22.skinoverlay.event.Handler;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectEvent;
import com.georgev22.skinoverlay.event.events.player.UserPlayerObjectEvent;
import com.georgev22.skinoverlay.event.events.player.skin.PlayerObjectPreUpdateSkinEvent;
import com.georgev22.skinoverlay.event.events.player.skin.PlayerObjectUpdateSkinEvent;
import com.georgev22.skinoverlay.event.events.profile.ProfileCreatedEvent;
import com.georgev22.skinoverlay.event.events.profile.property.SPropertyAddEvent;
import com.georgev22.skinoverlay.event.events.profile.property.SPropertyRemoveEvent;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.event.events.user.data.UserModifyDataEvent;
import com.georgev22.skinoverlay.event.events.user.data.add.UserAddDataEvent;
import com.georgev22.skinoverlay.event.events.user.data.load.UserPostLoadEvent;
import com.georgev22.skinoverlay.event.events.user.data.load.UserPreLoadEvent;

public class DebugListeners implements EventListener {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Handler(priority = EventPriority.HIGHEST)
    public void onPlayerObjectPreUpdateSkin(PlayerObjectPreUpdateSkinEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("PlayerObjectPreUpdateSkinEvent: " + event.getPlayerObject().playerName());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onPlayerObjectUpdateSkinEvent(PlayerObjectUpdateSkinEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("PlayerObjectUpdateSkinEvent: " + event.getPlayerObject().playerName());
        skinOverlay.getLogger().info("PlayerObjectUpdateSkinEvent: " + event.getSkinOptions().toString());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onPlayerObjectEvent(PlayerObjectEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("PlayerObjectEvent: " + event.getPlayerObject().playerName());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserPlayerObjectEvent(UserPlayerObjectEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserPlayerObjectEvent: " + event.getPlayerObject().playerName());
        skinOverlay.getLogger().info("UserPlayerObjectEvent: " + event.getUser());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserEvent(UserEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserEvent: " + event.getUser());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserAddDataEvent(UserAddDataEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserAddDataEvent: " + event.getUser());
        skinOverlay.getLogger().info("UserAddDataEvent: " + event.getData());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserPostLoadEvent(UserPostLoadEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserPostLoadEvent: " + event.getUser());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserPreLoadEvent(UserPreLoadEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserPreLoadEvent: " + event.getUser());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onUserModifyDataEvent(UserModifyDataEvent event) {
        if (event.isCancelled()) return;
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("UserModifyDataEvent: " + event.getUser());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onProfileCreatedEvent(ProfileCreatedEvent event) {
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("ProfileCreatedEvent: " + event.getProfile());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onSPropertyAddEvent(SPropertyAddEvent event) {
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("SPropertyAddEvent: " + event.getPropertyName());
        skinOverlay.getLogger().info("SPropertyAddEvent: " + event.getProperty());
        skinOverlay.getLogger().info("===== Debug =====");
    }

    @Handler(priority = EventPriority.HIGHEST)
    public void onSPropertyRemoveEvent(SPropertyRemoveEvent event) {
        skinOverlay.getLogger().info("===== Debug =====");
        skinOverlay.getLogger().info("SPropertyRemoveEvent: " + event.getPropertyName());
        skinOverlay.getLogger().info("SPropertyRemoveEvent: " + event.getProperty());
        skinOverlay.getLogger().info("===== Debug =====");
    }

}
