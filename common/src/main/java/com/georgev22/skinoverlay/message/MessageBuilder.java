package com.georgev22.skinoverlay.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

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
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class MessageBuilder {
    private final ArrayList<TextDecoration> currentDecorations;
    private TextComponent.Builder componentBuilder;
    private TextColor currentColor = NamedTextColor.WHITE;
    private ClickEvent currentClickEvent;
    private HoverEvent<?> currentHoverEvent;
    private Placeholder placeholder = new Placeholder();

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
    public static @NonNull MessageBuilder builder() {
        return new MessageBuilder();
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
     * Appends a newline character to the message.
     *
     * <p>This is equivalent to appending "\n".</p>
     *
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder appendln() {
        return append("\n");
    }

    /**
     * Appends a {@link ComponentLike} object to the message, followed by a newline.
     *
     * <p>This method appends the {@link ComponentLike}, and then appends a newline character.</p>
     *
     * @param componentLike The {@link ComponentLike} object to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder appendln(ComponentLike componentLike) {
        return append(componentLike).append("\n");
    }

    /**
     * Appends the given plain text to the message, followed by a newline character.
     *
     * @param text The text to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder appendln(String text) {
        return append(text).append("\n");
    }

    /**
     * Appends a {@link ComponentLike} to the message.
     *
     * @param componentLike The {@link ComponentLike} to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder append(ComponentLike componentLike) {
        componentBuilder.append(componentLike);
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
        componentBuilder.append(miniMessage(miniMessage, tagResolver)).decoration(TextDecoration.ITALIC, false);
        return this;
    }

    /**
     * Converts a MiniMessage string into a Component using an optional TagResolver.
     *
     * @param message     The MiniMessage string to convert.
     * @param tagResolver The TagResolver to use, can be null if not needed.
     * @return The converted Component.
     */
    private @NonNull Component miniMessage(String message, TagResolver tagResolver) {
        return MessageParser.miniMessage(message, tagResolver);
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
    public void send(@NonNull Audience audience) {
        audience.sendMessage(build());
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
        return MessageParser.miniMessage(
                this.placeholder.resolve(
                        MiniMessage.miniMessage().serialize(componentBuilder.build())
                ),
                TagResolver.empty()
        );
    }

    /**
     * Serializes the built component into a string using Legacy format.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * String messageString = messageBuilder
     *     .append("Hello, World!")
     *     .buildLegacyString();
     * }</pre>
     *
     * @return The serialized Legacy string.
     */
    public String buildLegacyString() {
        return LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build()
                .serialize(build());
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
     * Builds the Legacy string and resets the current MessageBuilder.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * String legacyString = messageBuilder
     *     .append("Hello, World!")
     *     .buildAndResetLegacyString();
     * }</pre>
     *
     * @return The serialized Legacy string.
     */
    public String buildAndResetLegacyString() {
        String legacyString = this.buildLegacyString();
        this.reset();
        return legacyString;
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
        this.placeholder = new Placeholder();
        return this.resetStyles();
    }

    /**
     * Sets the target {@link Audience} for PlaceholderAPI integration.
     * <p>
     * This allows placeholders to be resolved using PlaceholderAPI for the given target.
     *
     * @param target The target player or server operator, can be null.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder placeholderContext(@Nullable Audience target) {
        this.placeholder.setTarget(target);
        return this;
    }

    /**
     * Adds a simple placeholder key-value pair that will be replaced in messages.
     * <p>
     * Example usage:
     * <pre>{@code
     * messageBuilder.addPlaceholder("%player%", player.getName());
     * }</pre>
     *
     * @param key   The placeholder key to replace (e.g., "%player%").
     * @param value The value to replace the placeholder with.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder addPlaceholder(@NonNull String key, @NonNull String value) {
        this.placeholder.addPlaceholder(key, value);
        return this;
    }

    /**
     * Adds multiple boolean states for dynamic inline switches in messages.
     * <p>
     * Example usage:
     * <pre>{@code
     * Map<String, Boolean> states = Map.of("autosell", true, "linked", false);
     * messageBuilder.addStates(states);
     * // Then in message: "AutoSell is {autosell:enabled|disabled}, Chest is {linked:linked|not linked}"
     * // Output: "AutoSell is enabled, Chest is not linked"
     * }</pre>
     *
     * @param states A map containing key-boolean pairs representing states.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder addStates(@NonNull Map<String, Boolean> states) {
        this.placeholder.addStates(states);
        return this;
    }

    /**
     * Adds a single boolean state for dynamic inline switches in messages.
     * <p>
     * Example usage:
     * <pre>{@code
     * messageBuilder.addState("autosell", true);
     * // Then in message: "AutoSell is {autosell:enabled|disabled}"
     * // Output: "AutoSell is enabled"
     * }</pre>
     *
     * @param key   The key representing the state.
     * @param state The boolean value of the state.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder addState(@NonNull String key, boolean state) {
        this.placeholder.addState(key, state);
        return this;
    }

    /**
     * Replaces the current {@link Placeholder} instance with a new one.
     * <p>
     * This can be useful if you want to set multiple placeholders and states at once.
     *
     * @param placeholder The new {@link Placeholder} instance to use.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder placeholders(@Nullable Placeholder placeholder) {
        if (placeholder == null) {
            return this;
        }
        this.placeholder.merge(placeholder);
        return this;
    }

    /**
     * Adds multiple string placeholders at once from a map.
     * <p>
     * Example usage:
     * <pre>{@code
     * Map<String, String> placeholders = Map.of("%player%", player.getName(), "%island%", island.getName());
     * messageBuilder.placeholders(placeholders);
     * }</pre>
     *
     * @param placeholders A map containing key-value pairs for placeholders.
     * @return The current {@link MessageBuilder} instance for method chaining.
     */
    public MessageBuilder placeholders(@NonNull Map<String, String> placeholders) {
        this.placeholder.addPlaceholders(placeholders);
        return this;
    }
}
