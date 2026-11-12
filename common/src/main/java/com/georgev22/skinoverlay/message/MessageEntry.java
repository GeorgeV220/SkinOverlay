package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a single configurable message entry in a plugin.
 * <p>
 * Implementations are usually enums, where each constant represents
 * a message key with its default values.
 */
public interface MessageEntry {

    /**
     * @return the path of this message in the configuration file
     */
    String getPath();

    /**
     * @return the default messages for this entry
     */
    String[] getDefaultMessages();

    /**
     * Updates the messages for this entry (loaded from config).
     *
     * @param messages array of messages (multi-line support)
     */
    void setMessages(String[] messages);

    /**
     * @return the current messages (from config, or defaults if not overridden)
     */
    String[] getMessages();

    /**
     * Name of the YAML file, WITHOUT .yml extension
     * Example: "core", "player", "menus"
     */
    @NotNull String getFile();

    /**
     * Represents how a message should be displayed to a player.
     */
    enum MessageType {
        CHAT,
        ACTIONBAR,
        TITLE
    }

    /**
     * Sends a message to a sender as chat by default.
     *
     * @param issuer the receiver
     */
    default void msg(CommandIssuer issuer) {
        msg(issuer, null, MessageType.CHAT);
    }

    /**
     * Sends a message to a sender with a specific type.
     *
     * @param issuer the receiver
     */
    default void msg(CommandIssuer issuer, MessageType type) {
        msg(issuer, null, type);
    }

    /**
     * Sends a message with placeholder replacement.
     *
     * @param issuer      the receiver
     * @param placeholder placeholder replacements
     */
    default void msg(CommandIssuer issuer, Placeholder placeholder) {
        msg(issuer, placeholder, MessageType.CHAT);
    }

    /**
     * Sends a message with full customization.
     *
     * @param issuer      the receiver
     * @param placeholder placeholder replacements
     * @param type        how the message should be shown
     */
    default void msg(CommandIssuer issuer,
                     @Nullable Placeholder placeholder,
                     @NonNull MessageType type) {

        String[] messages = this.getMessages();
        MessageBuilder builder = new MessageBuilder();
        TagResolver resolver = StandardTags.defaults();

        Audience audience = issuer.audience();

        switch (type) {
            case ACTIONBAR -> audience.sendActionBar(
                    builder
                            .placeholderContext(audience)
                            .placeholders(placeholder)
                            .appendMiniMessage(messages[0], resolver)
                            .buildAndReset()
            );
            case TITLE -> {
                Component title = builder
                        .placeholderContext(audience)
                        .placeholders(placeholder)
                        .appendMiniMessage(messages[0], resolver)
                        .buildAndReset();

                Component subtitle = messages.length > 1
                        ? builder
                        .placeholderContext(audience)
                        .placeholders(placeholder)
                        .appendMiniMessage(messages[1], resolver)
                        .buildAndReset()
                        : Component.text("");

                audience.showTitle(Title.title(title, subtitle, Title.DEFAULT_TIMES));
            }
            case CHAT -> {
                if (messages.length > 1) {
                    Arrays.stream(messages).forEach(msg ->
                            audience.sendMessage(builder
                                    .placeholderContext(audience)
                                    .placeholders(placeholder)
                                    .appendMiniMessage(msg, resolver)
                                    .buildAndReset())
                    );
                } else {
                    audience.sendMessage(builder
                            .placeholderContext(audience)
                            .placeholders(placeholder)
                            .appendMiniMessage(messages[0], resolver)
                            .buildAndReset());
                }
            }
        }
    }

    /**
     * Sends a message to the console.
     *
     */
    default void msgConsole() {
        msgConsole(Map.of(), false);
    }

    /**
     * Sends a message to the console with placeholders.
     *
     * @param map        placeholder replacements
     * @param ignoreCase whether to ignore case
     */
    default void msgConsole(Map<String, String> map, boolean ignoreCase) {
        String[] messages = this.getMessages();
        Audience console = SkinOverlay.getInstance().getConsoleAudience();
        MessageBuilder builder = new MessageBuilder();
        TagResolver resolver = StandardTags.defaults();
        if (messages.length > 1) {
            Arrays.stream(messages).forEach(msg -> console
                    .sendMessage(builder
                            .placeholderContext(console)
                            .placeholders(new HashObjectMap<>(map))
                            .appendMiniMessage(msg, resolver)
                            .buildAndReset()));
        } else {
            console.sendMessage(builder
                    .placeholderContext(console)
                    .placeholders(new HashObjectMap<>(map))
                    .appendMiniMessage(messages[0], resolver)
                    .buildAndReset());
        }
    }

    /**
     * Sends a message to all online players.
     *
     */
    default void msgAll() {
        msgAll((Placeholder) null);
    }

    /**
     * Sends a message to all online players with placeholders.
     *
     * @param placeholder placeholder replacements
     */
    default void msgAll(@Nullable Placeholder placeholder) {
        SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().forEach(p -> msg(p, placeholder));
    }

    /**
     * Sends a message to all online players.
     *
     * @param type the message type (e.g. ACTIONBAR or CHAT)
     */
    default void msgAll(MessageType type) {
        SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().forEach(p -> msg(p, type));
    }

    /**
     * Sends a message to all online players with placeholders.
     *
     * @param placeholder placeholder replacements
     * @param type        the message type (e.g. ACTIONBAR or CHAT)
     */
    default void msgAll(Placeholder placeholder, MessageType type) {
        SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().forEach(p -> msg(p, placeholder, type));
    }
}
