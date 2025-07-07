package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.player.SPlayer;

@SuppressWarnings("ClassCanBeRecord")
public class SPlayerJoinEvent implements Event {

    private final SPlayer player;

    public SPlayerJoinEvent(SPlayer player) {
        this.player = player;
    }

    public SPlayer getPlayer() {
        return player;
    }

}
