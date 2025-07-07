package com.georgev22.skinoverlay.providers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.georgev22.skinoverlay.player.SPlayer;
import com.georgev22.skinoverlay.skin.SGameProfile;
import com.georgev22.skinoverlay.skin.SProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperGameProfileProvider extends GameProfileProvider {

    @Override
    public PlayerProfile getInternalGameProfile(@NotNull SPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        return bukkitPlayer.getPlayerProfile();
    }

    @Override
    public SGameProfile getGameProfile(@NotNull SPlayer player) {
        if (sGameProfiles.containsKey(player)) {
            return sGameProfiles.get(player);
        }
        SGameProfile gameProfile = new SGameProfile(player.getName(), player.getUniqueId());
        PlayerProfile playerProfile = getInternalGameProfile(player);
        playerProfile.getProperties().forEach(profileProperty -> gameProfile.addProperty(
                profileProperty.getName(), new SProperty(profileProperty.getValue(), profileProperty.getSignature())
        ));
        return sGameProfiles.append(player, gameProfile).get(player);
    }

    @Override
    public void applyUpdatedGameProfile(@NotNull SPlayer player) {
        Player bukkitPlayer = player.getPlayer();
        PlayerProfile playerProfile = bukkitPlayer.getPlayerProfile();
        playerProfile.getProperties().removeIf(profileProperty -> profileProperty.getName().equalsIgnoreCase("textures"));
        SGameProfile gameProfile = this.getGameProfile(player);
        gameProfile.getProperties()
                .forEach((s, property) -> playerProfile.getProperties().add(new ProfileProperty(s, property.value(), property.signature())));
        bukkitPlayer.setPlayerProfile(playerProfile);
    }
}
