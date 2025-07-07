package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface MessageManager {

    void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin);

    void subscribeSkinProperty(@NotNull SkinPropertyHandler handler);

    void close();
}
