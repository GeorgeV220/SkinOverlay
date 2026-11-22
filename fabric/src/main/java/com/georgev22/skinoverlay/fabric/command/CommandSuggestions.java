package com.georgev22.skinoverlay.fabric.command;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

final class CommandSuggestions {
    private CommandSuggestions() {
    }

    static CompletableFuture<Suggestions> suggest(
            SuggestionsBuilder builder, Function<String[], Collection<String>> completer) {
        String input = builder.getInput();
        String remainder = input.substring(builder.getStart()).stripLeading();
        String[] args = remainder.isEmpty() ? new String[]{""} : remainder.split("\\s+", -1);

        int tokenStart = input.length();
        while (tokenStart > builder.getStart() && !Character.isWhitespace(input.charAt(tokenStart - 1))) {
            tokenStart--;
        }
        SuggestionsBuilder tokenBuilder = builder.createOffset(tokenStart);
        completer.apply(args).forEach(tokenBuilder::suggest);
        return tokenBuilder.buildFuture();
    }
}
