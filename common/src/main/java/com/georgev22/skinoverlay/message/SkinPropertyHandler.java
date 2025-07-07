package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.storage.data.Skin;

import java.util.UUID;

public interface SkinPropertyHandler {
    void handle(UUID uuid, Skin skin);
}