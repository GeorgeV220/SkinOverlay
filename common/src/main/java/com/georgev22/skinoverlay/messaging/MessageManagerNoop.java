package com.georgev22.skinoverlay.messaging;

import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageManagerNoop extends MessageManager {
    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {

    }

    @Override
    public void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler) {

    }

    @Override
    public void publishPlayerJoin(@NotNull UUID playerUUID) {

    }

    @Override
    public void subscribePlayerJoin(Consumer<UUID> handler) {

    }

    @Override
    public void close() {

    }
}
