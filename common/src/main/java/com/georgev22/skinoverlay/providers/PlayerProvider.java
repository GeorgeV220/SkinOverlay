package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;

import java.util.List;
import java.util.UUID;

public abstract class PlayerProvider {

    public abstract SPlayer getSPlayer(Object player);

    public abstract SPlayer getSPlayer(CommandIssuer commandIssuer);

    public abstract SPlayer getSPlayer(String name);

    public abstract SPlayer getSPlayer(UUID uuid);

    public abstract List<SPlayer> getOnlinePlayers();

    public boolean isOnline(SPlayer player) {
        try {
            return player.isOnline() && player.getPlayer() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnline(String name) {
        try {
            SPlayer player = getSPlayer(name);
            return isOnline(player);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnline(UUID uuid) {
        try {
            SPlayer player = getSPlayer(uuid);
            return isOnline(player);
        } catch (Exception e) {
            return false;
        }
    }
}
