package com.georgev22.skinoverlay.event.events.player;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.storage.data.PlayerData;

@SuppressWarnings("ClassCanBeRecord")
public class PlayerDataLoadEvent implements Event {
    private final PlayerData playerData;

    public PlayerDataLoadEvent(PlayerData playerData) {
        this.playerData = playerData;
    }


    public PlayerData getPlayerData() {
        return playerData;
    }
}
