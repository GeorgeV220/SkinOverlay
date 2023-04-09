package com.georgev22.skinoverlay.event.events.player.skin;

import com.georgev22.library.utilities.UserManager;
import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.events.player.PlayerObjectEvent;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import org.jetbrains.annotations.Nullable;

public class PlayerObjectUpdateSkinEvent extends PlayerObjectEvent implements Event, Cancellable {

    private SkinOptions skinOptions;

    public PlayerObjectUpdateSkinEvent(PlayerObject playerObject, UserManager.@Nullable User user, boolean async, SkinOptions skinOptions) {
        super(playerObject, user, async);
        this.skinOptions = skinOptions;
    }

    public SkinOptions getSkinOptions() {
        return skinOptions;
    }

    public void setSkinOptions(SkinOptions skinOptions) {
        getUser().addCustomData("skinOptions", this.skinOptions = skinOptions);
    }
}
