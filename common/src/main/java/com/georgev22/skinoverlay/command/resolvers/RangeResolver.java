package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RangeResolver implements ArgumentResolver {
    @Override
    public List<String> resolve(@NotNull CommandIssuer commandIssuer, String @NotNull ... args) {
        if (args.length == 0 || !args[0].matches("\\d+-\\d+")) {
            return List.of();
        }

        String[] range = args[0].split("-");
        int min = Integer.parseInt(range[0]);
        int max = Integer.parseInt(range[1]);

        List<String> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }
}
