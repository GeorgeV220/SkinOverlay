package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.providers.GameProfileProviderNoop;
import com.georgev22.skinoverlay.refreshers.NoopSkinRefresher;

public final class SkinOverlayVersionResolver {

    private final SkinOverlay skinOverlay;

    public SkinOverlayVersionResolver(SkinOverlay skinOverlay) {
        this.skinOverlay = skinOverlay;
    }

    public void resolve() {
        BukkitMinecraftUtils.MinecraftVersion current = BukkitMinecraftUtils.MinecraftVersion.getCurrent();
        for (VersionRange range : VersionRegistry.RANGES) {
            if (range.matches(current)) {
                skinOverlay.setGameProfileProvider(range.provider().get());
                skinOverlay.setSkinRefresher(range.refresher().get());
                return;
            }
        }

        skinOverlay.setSkinRefresher(new NoopSkinRefresher());
        skinOverlay.setGameProfileProvider(new GameProfileProviderNoop());

        skinOverlay.getLogger().info("SkinOverlay does not support " + current);
        skinOverlay.getLogger().info("Install SkinsRestorer in order for SkinOverlay to use their API to apply skins.");
    }
}