package com.georgev22.skinoverlay.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    private static final Pattern MINI_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern QUOTED_TAG_PATTERN = Pattern.compile(
            "<([a-zA-Z0-9:_-]+):\"((?:[^\"\\\\]|\\\\.)*)\">"
    );

    /**
     * Parses a string containing legacy Minecraft color codes and MiniMessage tags,
     * converting legacy codes outside tags to MiniMessage format and fixing legacy codes inside quoted tag arguments.
     *
     * @param input the input string containing legacy codes and MiniMessage tags
     * @param tagResolver optional resolver for custom MiniMessage tags, can be null
     * @return the parsed Component
     */
    public static @NotNull Component miniMessage(@NotNull String input, @Nullable TagResolver tagResolver) {
        //noinspection UnnecessaryUnicodeEscape
        input = input.replace('\u00A7', '&');
        input = fixLegacyInQuotedText(input);

        Matcher matcher = MINI_TAG_PATTERN.matcher(input);
        int lastEnd = 0;
        StringBuilder miniMessageBuilder = new StringBuilder();

        while (matcher.find()) {
            String textPart = input.substring(lastEnd, matcher.start());
            miniMessageBuilder.append(legacyToMiniMessage(textPart));
            miniMessageBuilder.append(matcher.group());
            lastEnd = matcher.end();
        }

        miniMessageBuilder.append(legacyToMiniMessage(input.substring(lastEnd)));

        String combinedMiniMessage = miniMessageBuilder.toString();

        if (tagResolver != null) {
            return MiniMessage.miniMessage().deserialize(combinedMiniMessage, tagResolver);
        }
        return MiniMessage.miniMessage().deserialize(combinedMiniMessage);
    }

    /**
     * Converts legacy color codes within quoted text arguments of MiniMessage tags to MiniMessage format.
     *
     * @param input the raw input string
     * @return input with legacy codes inside quoted tag arguments converted to MiniMessage
     */
    private static @NotNull String fixLegacyInQuotedText(@NotNull String input) {
        Matcher matcher = QUOTED_TAG_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String tagName = matcher.group(1);
            String quotedText = matcher.group(2);

            String unescapedQuoted = quotedText.replace("\\\"", "\"").replace("\\\\", "\\");
            String fixedQuoted = legacyToMiniMessage(unescapedQuoted);
            String escapedFixedQuoted = fixedQuoted.replace("\"", "\\\"");

            String replacement = "<" + tagName + ":\"" + escapedFixedQuoted + "\">";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Converts legacy Minecraft color codes in a string to MiniMessage format.
     *
     * @param legacyText text containing legacy color codes
     * @return MiniMessage-formatted string
     */
    private static @NotNull String legacyToMiniMessage(@NotNull String legacyText) {
        if (legacyText.isEmpty()) return "";
        Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
        return MiniMessage.miniMessage().serialize(comp);
    }
}
