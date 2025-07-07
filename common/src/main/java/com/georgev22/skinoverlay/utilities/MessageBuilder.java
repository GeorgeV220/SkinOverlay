package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import com.georgev22.skinoverlay.maps.Pair;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A utility class for building and sending styled chat messages to players in a Minecraft server environment
 * using the Adventure API. This class supports text colors, decorations, click events, hover events, and MiniMessage
 * formatting.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Create an instance of the MessageBuilder
 * MessageBuilder messageBuilder = new MessageBuilder();
 *
 * // Build a message using plain text and styles
 * messageBuilder
 *     .append("Welcome, ")
 *     .color(NamedTextColor.AQUA)
 *     .decorate(TextDecoration.BOLD)
 *     .append(player.getName())
 *     .color(NamedTextColor.GREEN)
 *     .append("! Click ")
 *     .color(NamedTextColor.YELLOW)
 *     .append("here")
 *     .color(NamedTextColor.RED)
 *     .decorate(TextDecoration.UNDERLINED)
 *     .clickEvent(ClickEvent.Action.RUN_COMMAND, "/help")
 *     .hoverEvent("Click to run /help")
 *     .append(" for more info.")
 *     .send(player);
 *
 * // Using MiniMessage for more complex formatting
 * messageBuilder
 *     .appendMiniMessage("<bold><aqua>Welcome, </aqua></bold><green>" + player.getName() + "</green>")
 *     .appendMiniMessage("<yellow> Click <underlined><red>here</red></underlined> for more info.</yellow>")
 *     .clickEvent(ClickEvent.Action.RUN_COMMAND, "/help")
 *     .hoverEvent("Click to run /help")
 *     .send(player);
 * }</pre>
 */
public class MessageBuilder {

    /**
     * Color map for mapping legacy colors to MiniMessage colors.
     * <p>
     * Map key: Legacy color code <br>
     * Map value: <br>
     * - Pair key: MiniMessage color (e.g., {@literal <color>}) <br>
     * - Pair value: Indicates if the color needs to be closed (e.g., {@literal </color>})
     */
    @NotNull
    public static final ObjectMap<String, Pair<String, Boolean>> COLOR_MAP = new HashObjectMap<String, Pair<String, Boolean>>()
            .append("0", Pair.create("black", true))
            .append("1", Pair.create("dark_blue", true))
            .append("2", Pair.create("dark_green", true))
            .append("3", Pair.create("dark_aqua", true))
            .append("4", Pair.create("dark_red", true))
            .append("5", Pair.create("dark_purple", true))
            .append("6", Pair.create("gold", true))
            .append("7", Pair.create("gray", true))
            .append("8", Pair.create("dark_gray", true))
            .append("9", Pair.create("blue", true))
            .append("a", Pair.create("green", true))
            .append("b", Pair.create("aqua", true))
            .append("c", Pair.create("red", true))
            .append("d", Pair.create("light_purple", true))
            .append("e", Pair.create("yellow", true))
            .append("f", Pair.create("white", true))
            .append("l", Pair.create("bold", false))
            .append("m", Pair.create("strikethrough", false))
            .append("n", Pair.create("underline", false))
            .append("o", Pair.create("italic", false))
            .append("r", Pair.create("reset", true))
            .append("k", Pair.create("obfuscated", false));
    private final ArrayList<TextDecoration> currentDecorations;
    private TextComponent.Builder componentBuilder;
    private TextColor currentColor = NamedTextColor.WHITE;
    private ClickEvent currentClickEvent;
    private HoverEvent<?> currentHoverEvent;

    /**
     * Constructs a new MessageBuilder instance.
     */
    public MessageBuilder() {
        this.componentBuilder = Component.text();
        this.currentDecorations = new ArrayList<>();
    }

    /**
     * Creates a new MessageBuilder instance.
     *
     * @return A new MessageBuilder instance.
     */
    @Contract(" -> new")
    public static @NotNull MessageBuilder builder() {
        return new MessageBuilder();
    }

    /**
     * Translates legacy color codes (e.g., {@literal &}colorCode) to MiniMessage colors (e.g., {@literal <color>}).
     *
     * @param message The message to translate.
     * @return The translated message with MiniMessage formatting.
     */
    public static String translateLegacyColors(String message) {
        boolean colorMode = false;
        boolean tagNeedToClose = false;
        String tagToClose = "";

        StringBuilder newText = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (ch == '&' && colorMode) {
                newText.append('&');
                continue;
            }

            if (ch == '&') {
                colorMode = true;
                continue;
            }

            if (colorMode && COLOR_MAP.containsKey(String.valueOf(ch))) {
                if (tagNeedToClose) {
                    tagNeedToClose = false;
                    newText.append("</").append(tagToClose).append(">");
                }

                newText.append("<").append(COLOR_MAP.get(String.valueOf(ch)).key()).append(">");

                if (!COLOR_MAP.get(String.valueOf(ch)).value()) {
                    tagNeedToClose = true;
                    tagToClose = COLOR_MAP.get(String.valueOf(ch)).key();
                }

                colorMode = false;
                continue;
            } else if (colorMode) {
                colorMode = false;
                newText.append('&');
            }

            newText.append(ch);
        }

        if (tagNeedToClose) {
            newText.append("</").append(tagToClose).append(">");
        }

        message = newText.toString();
        return message;
    }

    /**
     * Appends plain text to the message and applies the current styles.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .append("Hello ")
     *     .color(NamedTextColor.RED)
     *     .decorate(TextDecoration.BOLD)
     *     .append("World!");
     * }</pre>
     *
     * @param text The text to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder append(String text) {
        TextComponent.Builder textComponent = Component.text()
                .content(text)
                .color(currentColor);

        for (TextDecoration decoration : currentDecorations) {
            textComponent.decoration(decoration, true);
        }

        if (currentClickEvent != null) {
            textComponent.clickEvent(currentClickEvent);
        }

        if (currentHoverEvent != null) {
            textComponent.hoverEvent(currentHoverEvent);
        }

        componentBuilder.append(textComponent.build());
        return this;
    }

    /**
     * Appends MiniMessage-formatted text, allowing complex formatting using MiniMessage syntax.
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .appendMiniMessage("<bold><green>Hello, World!</green></bold>")
     *     .clickEvent(ClickEvent.Action.RUN_COMMAND, "/example")
     *     .hoverEvent("Execute command /example");
     * }</pre>
     *
     * @param miniMessage The MiniMessage string to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder appendMiniMessage(String miniMessage) {
        return appendMiniMessage(miniMessage, null);
    }

    /**
     * Appends MiniMessage-formatted text, allowing complex formatting using MiniMessage syntax.
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .appendMiniMessage("<bold><green>Hello, World!</green></bold>")
     *     .clickEvent(ClickEvent.Action.RUN_COMMAND, "/example")
     *     .hoverEvent("Execute command /example");
     * }</pre>
     *
     * @param miniMessage The MiniMessage string to append.
     * @param tagResolver A tag resolver for parsing the MiniMessage, can be null.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder appendMiniMessage(String miniMessage, TagResolver tagResolver) {
        componentBuilder.append(miniMessage(miniMessage, tagResolver));
        return this;
    }

    /**
     * Converts a MiniMessage string into a Component using an optional TagResolver.
     *
     * @param message     The MiniMessage string to convert.
     * @param tagResolver The TagResolver to use, can be null if not needed.
     * @return The converted Component.
     */
    private @NotNull Component miniMessage(String message, @Nullable TagResolver tagResolver) {
        if (tagResolver != null) {
            return MiniMessage.miniMessage().deserialize(translateLegacyColors(message), tagResolver);
        }
        return MiniMessage.miniMessage().deserialize(translateLegacyColors(message));
    }

    /**
     * Sets the color of the subsequent text.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .color(NamedTextColor.BLUE)
     *     .append("This text is blue");
     * }</pre>
     *
     * @param color The TextColor to set.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder color(TextColor color) {
        this.currentColor = color;
        return this;
    }

    /**
     * Adds text decorations like bold, italic, etc.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .decorate(TextDecoration.BOLD, TextDecoration.ITALIC)
     *     .append("Bold and Italic Text");
     * }</pre>
     *
     * @param decorations The TextDecorations to apply.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder decorate(TextDecoration... decorations) {
        Collections.addAll(this.currentDecorations, decorations);
        return this;
    }

    /**
     * Removes text decorations.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .decorate(TextDecoration.BOLD)
     *     .append("Bold text")
     *     .removeDecorate(TextDecoration.BOLD)
     *     .append("Normal text");
     * }</pre>
     *
     * @param decorations The TextDecorations to remove.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder removeDecorate(TextDecoration... decorations) {
        this.currentDecorations.removeAll(Arrays.asList(decorations));
        return this;
    }

    /**
     * Sets a click event for the subsequent text.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .append("Click me")
     *     .clickEvent(ClickEvent.Action.RUN_COMMAND, "/example");
     * }</pre>
     *
     * @param action The ClickEvent.Action to set.
     * @param value  The value associated with the action.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder clickEvent(ClickEvent.Action action, String value) {
        this.currentClickEvent = ClickEvent.clickEvent(action, value);
        return this;
    }

    /**
     * Sets a hover event for the subsequent text.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .append("Hover over me")
     *     .hoverEvent("This is hover text");
     * }</pre>
     *
     * @param hoverText The text to show on hover.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder hoverEvent(String hoverText) {
        this.currentHoverEvent = HoverEvent.showText(miniMessage(hoverText, null));
        return this;
    }

    /**
     * Sends the constructed message to the specified player.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .append("Hello, ")
     *     .append(player.getName())
     *     .append("!")
     *     .send(player);
     * }</pre>
     *
     * @param audience The player to send the message to.
     */
    public void send(Audience audience) {
        audience.sendMessage(componentBuilder.build());
    }

    /**
     * Builds the component for further use.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Component component = messageBuilder
     *     .append("Hello, World!")
     *     .build();
     * }</pre>
     *
     * @return The constructed Component.
     */
    public Component build() {
        return componentBuilder.build();
    }

    /**
     * Builds the component and resets the current MessageBuilder.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Component component = messageBuilder
     *     .append("Hello, World!")
     *     .buildAndReset();
     * }</pre>
     *
     * @return The constructed Component.
     */
    public Component buildAndReset() {
        Component component = this.build();
        this.reset();
        return component;
    }

    /**
     * Serializes the built component into a string using MiniMessage format.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * String messageString = messageBuilder
     *     .append("Hello, World!")
     *     .buildString();
     * }</pre>
     *
     * @return The serialized MiniMessage string.
     */
    public String buildString() {
        return MiniMessage.miniMessage().serialize(build());
    }

    /**
     * Resets the current styles and events, restoring them to default values.
     * <p>Example usage:</p>
     * <pre>{@code
     * messageBuilder
     *     .resetStyles()
     *     .append("This text has default styling");
     * }</pre>
     *
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder resetStyles() {
        this.currentColor = NamedTextColor.WHITE;
        this.currentDecorations.clear();
        this.currentClickEvent = null;
        this.currentHoverEvent = null;
        return this;
    }

    public MessageBuilder reset() {
        this.componentBuilder = Component.text();
        return this.resetStyles();
    }
}