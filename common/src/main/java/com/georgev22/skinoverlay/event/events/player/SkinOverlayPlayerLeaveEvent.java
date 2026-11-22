package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;

@SuppressWarnings("ClassCanBeRecord")
public class SkinOverlayPlayerLeaveEvent implements Event {

    private final SkinOverlayPlayer player;

    public SkinOverlayPlayerLeaveEvent(SkinOverlayPlayer player) {
        this.player = player;
    }

    public SkinOverlayPlayer getPlayer() {
        return player;
    }

}