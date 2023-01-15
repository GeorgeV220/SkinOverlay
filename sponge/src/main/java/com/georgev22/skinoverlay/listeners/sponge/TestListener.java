package com.georgev22.skinoverlay.listeners.sponge;

import com.georgev22.skinoverlay.SkinOverlay;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

public class TestListener {

    @Listener
    public void handle(RegisterCommandEvent<Command> event) {
        SkinOverlay.getInstance().getLogger().info("?????????? GT 2");
    }
}
