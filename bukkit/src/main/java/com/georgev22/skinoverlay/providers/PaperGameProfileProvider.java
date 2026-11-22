package com.georgev22.skinoverlay.providers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.georgev22.skinoverlay.player.SkinOverlayPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperGameProfileProvider extends GameProfileProvider {

    @Override
    public PlayerProfile getInternalGameProfile(@NotNull SkinOverlayPlayer player) {
        Player bukkitPlayer = player.getPlatformPlayer();
        return bukkitPlayer.getPlayerProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SkinOverlayPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        SGameProfile gameProfile = new SGameProfile(player.getName(), player.getUniqueId());
        PlayerProfile playerProfile = getInternalGameProfile(player);
        playerProfile.getProperties().forEach(profileProperty -> gameProfile.addProperty(
                profileProperty.getName(), new SProperty(profileProperty.getName(), profileProperty.getValue(), profileProperty.getSignature())
        ));
        return sGameProfiles.append(player, gameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SkinOverlayPlayer player) {
        Player bukkitPlayer = player.getPlatformPlayer();
        PlayerProfile playerProfile = bukkitPlayer.getPlayerProfile();
        playerProfile.getProperties().removeIf(profileProperty -> profileProperty.getName().equalsIgnoreCase("textures"));
        SGameProfile gameProfile = this.getGameProfile(player);
        gameProfile.getProperties()
                .forEach((name, property) -> playerProfile.getProperties()
                        .add(new ProfileProperty(name, property.value(), property.signature())));
        bukkitPlayer.setPlayerProfile(playerProfile);
    }
}
