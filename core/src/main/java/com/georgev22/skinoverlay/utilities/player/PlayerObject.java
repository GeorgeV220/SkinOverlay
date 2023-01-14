package com.georgev22.skinoverlay.utilities.player;

import java.util.List;
import java.util.UUID;

public interface PlayerObject {
    Object getPlayer();

    UUID playerUUID();

    String playerName();

    default boolean isBedrock() {
        return this.playerUUID().toString().replace("-", "").startsWith("000000");
    }

    void sendMessage(String input);

    void sendMessage(List<String> input);

    void sendMessage(String... input);


}
