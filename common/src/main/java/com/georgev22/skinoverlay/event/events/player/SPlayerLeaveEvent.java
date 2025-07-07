package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.player.SPlayer;

@SuppressWarnings("ClassCanBeRecord")
public class SPlayerLeaveEvent implements Event {

    private final SPlayer player;

    public SPlayerLeaveEvent(SPlayer player) {
        this.player = player;
    }

    public SPlayer getPlayer() {
        return player;
    }

}