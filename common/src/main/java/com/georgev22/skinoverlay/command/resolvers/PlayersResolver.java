package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersResolver implements ArgumentResolver {

    @Override
    public List<String> resolve(@NotNull CommandIssuer commandIssuer, String @NotNull ... args) {
        String prefix = args.length > 0 ? args[0].toLowerCase() : "";
        return SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().stream()
                .map(SPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toList());
    }

    @Override
    public Object resolveValue(@NotNull CommandIssuer commandIssuer, @NotNull String arg) {
        return SkinOverlay.getInstance().getPlayerProvider().getSPlayer(arg);
    }
}
