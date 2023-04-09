package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectEvent;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.Nullable;

public class PlayerObjectPreUpdateSkinEvent extends PlayerObjectEvent implements Event, Cancellable {

    public PlayerObjectPreUpdateSkinEvent(PlayerObject playerObject, UserManager.@Nullable User user, boolean async) {
        super(playerObject, user, async);
    }

}
