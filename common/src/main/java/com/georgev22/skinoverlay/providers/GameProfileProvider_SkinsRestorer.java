package com.georgev22.skinoverlay.providers;

import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GameProfileProvider_SkinsRestorer extends GameProfileProvider {

    private final SkinsRestorer skinsRestorerAPI;

    public GameProfileProvider_SkinsRestorer() {
        skinsRestorerAPI = SkinsRestorerProvider.get();
    }

    @Override
    public SGameProfile getInternalGameProfile(@NotNull SPlayer player) {
        Optional<SkinProperty> property;
        try {
            property = skinsRestorerAPI.getPlayerStorage().getSkinForPlayer(player.getUniqueId(), player.getName());
        } catch (DataRequestException e) {
            property = Optional.empty();
        }
        SGameProfile gameProfile = new SGameProfile(player.getName(), player.getUniqueId());
        property.map(skinProperty -> new SProperty(skinProperty.getValue(), skinProperty.getSignature())).ifPresent(sProperty -> gameProfile.setProperty("textures", sProperty));
        return gameProfile;
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        return sGameProfiles.append(player, getInternalGameProfile(player)).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        SGameProfile gameProfile = getGameProfile(player);
        SProperty property = gameProfile.getProperty("textures");
        if (property == null) {
            this.skinOverlay.getLogger().warning("Skin Property is null for player: " + player.getName());
            return;
        }
        SkinProperty skinProperty = SkinProperty.of(property.value(), property.signature());
        skinsRestorerAPI.getSkinApplier(player.getPlayer().getClass()).applySkin(player.getPlayer(), skinProperty);
    }
}
