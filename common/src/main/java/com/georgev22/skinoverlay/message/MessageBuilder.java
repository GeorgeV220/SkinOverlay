package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.datastructures.maps.ObjectMap;
import com.georgev22.skinoverlay.utilities.Utils;
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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;

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
    private ObjectMap<String, String> placeholders = new HashObjectMap<>();

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
        text = replacePlaceholders(text);
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
     * Appends a component to the message.
     *
     * @param component The component to append.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder append(Component component) {
        componentBuilder.append(component);
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
    private @NotNull Component miniMessage(String message, TagResolver tagResolver) {
        message = replacePlaceholders(message);
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
        value = replacePlaceholders(value);
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
        hoverText = replacePlaceholders(hoverText);
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
    public void send(@NotNull Audience audience) {
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
        this.placeholders.clear();
        return this.resetStyles();
    }

    /**
     * Sets the placeholders for the message.
     *
     * @param placeholders The placeholders to set.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder placeholders(ObjectMap<String, String> placeholders) {
        this.placeholders = new HashObjectMap<>(placeholders);
        return this;
    }

    /**
     * Clears all placeholders from the message.
     *
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder clearPlaceholders() {
        this.placeholders.clear();
        return this;
    }

    /**
     * Adds a placeholder to the message.
     *
     * @param key   The placeholder key.
     * @param value The placeholder value.
     * @return The MessageBuilder instance for method chaining.
     */
    public MessageBuilder addPlaceholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    /**
     * Attempts to convert this message (built as a MiniMessage string) into a Paper Adventure
     * {@code net.kyori..adventure.text.Component} instance at runtime, without directly referencing
     * Adventure API classes in bytecode.
     *
     * <p>This method is designed for environments where Adventure is shaded and relocated (e.g. Spigot),
     * but Paper provides its own Adventure API. Direct casts or static imports would be relocated by the
     * shadow plugin, causing runtime type mismatches. To avoid relocation, class names are constructed
     * dynamically and reflection is used to invoke the MiniMessage deserializer on Paper.</p>
     *
     * <p>If running on a non-Paper server (or if the Paper MiniMessage API is not available), this
     * method returns {@code null}. The caller must handle the {@code null} return and fall back to the
     * relocated Adventure API.</p>
     *
     * @return a Paper {@code net.kyori.adventure.text.Component} instance if Paper Adventure is present,
     * otherwise {@code null}
     */
    public @Nullable Object toPaperComponent() {
        try {
            Class<?> mmClass = Class.forName(
                    n("net", "kyori", "adventure", "text", "minimessage", "MiniMessage")
            );
            Class<?> componentClass = Class.forName(
                    n("net", "kyori", "adventure", "text", "Component")
            );

            Class<?> tagResolverClass = Class.forName(
                    n("net", "kyori", "adventure", "text", "minimessage", "tag", "resolver", "TagResolver")
            );

            Object mm = mmClass.getMethod("miniMessage").invoke(null);

            Object emptyResolvers = java.lang.reflect.Array.newInstance(tagResolverClass, 0);

            return mmClass
                    .getMethod("deserialize", String.class, java.lang.reflect.Array.newInstance(tagResolverClass, 0).getClass())
                    .invoke(mm, buildString(), emptyResolvers);

        } catch (Throwable throwable) {
            SkinOverlay.getInstance().getLogger().log(
                    Level.WARNING, "Could not deserialize MiniMessage", throwable
            );
            return null;
        }
    }

    /**
     * Replaces placeholders in the given string with their corresponding values.
     *
     * @param input The input string containing placeholders.
     * @return The input string with placeholders replaced.
     */
    private String replacePlaceholders(String input) {
        return Utils.placeHolder(input, placeholders, true);
    }

    private String n(String... p) {
        return String.join(".", p);
    }
}
