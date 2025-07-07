package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersResolver implements ArgumentResolver {
    @Override
    public List<String> resolve(@NotNull CommandIssuer commandIssuer, String... args) {
        return SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().stream()
                .map(SPlayer::getName)
                .collect(Collectors.toList());
    }
}
