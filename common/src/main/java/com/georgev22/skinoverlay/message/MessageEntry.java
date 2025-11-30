package com.georgev22.skinoverlay.message;


import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

import static com.georgev22.skinoverlay.utilities.Utils.placeHolder;


/**
 * Represents a single configurable message entry in a plugin.
 * <p>
 * Implementations are usually enums, where each constant represents
 * a message key with its default values.
 */
public interface MessageEntry {

    MessageEntry NONE = null;

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
        msg(issuer, new HashObjectMap<>(), false, MessageType.CHAT);
    }

    /**
     * Sends a message to a sender with a specific type.
     *
     * @param issuer the receiver
     */
    default void msg(CommandIssuer issuer, MessageType type) {
        msg(issuer, new HashObjectMap<>(), false, type);
    }

    /**
     * Sends a message with placeholder replacement.
     *
     * @param issuer     the receiver
     * @param map        placeholder replacements
     * @param ignoreCase whether to ignore case when replacing
     */
    default void msg(CommandIssuer issuer, Map<String, String> map, boolean ignoreCase) {
        msg(issuer, map, ignoreCase, MessageType.CHAT);
    }

    /**
     * Sends a message with full customization.
     *
     * @param issuer     the receiver
     * @param map        placeholder replacements
     * @param ignoreCase whether to ignore case when replacing
     * @param type       how the message should be shown
     */
    default void msg(CommandIssuer issuer,
                     Map<String, String> map, boolean ignoreCase,
                     @NotNull MessageType type) {

        String[] messages = this.getMessages();
        MessageBuilder builder = new MessageBuilder();
        TagResolver resolver = StandardTags.defaults();

        Audience audience = SkinOverlay.getInstance().getAudienceProvider().player(issuer.getUniqueId());

        switch (type) {
            case ACTIONBAR -> {
                audience.sendActionBar(
                        builder.appendMiniMessage(
                                        placeHolder(messages[0], map, ignoreCase),
                                        resolver
                                )
                                .buildAndReset()
                );
            }
            case TITLE -> {
                Component title = builder
                        .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), resolver)
                        .buildAndReset();

                Component subtitle = messages.length > 1
                        ? builder.appendMiniMessage(placeHolder(messages[1], map, ignoreCase), resolver).buildAndReset()
                        : Component.text("");

                audience.showTitle(Title.title(title, subtitle, Title.DEFAULT_TIMES));
            }
            case CHAT -> {
                if (messages.length > 1) {
                    Arrays.stream(messages).forEach(msg ->
                            audience.sendMessage(builder
                                    .appendMiniMessage(placeHolder(msg, map, ignoreCase), resolver)
                                    .buildAndReset())
                    );
                } else {
                    audience.sendMessage(builder
                            .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), resolver)
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
        Audience console = SkinOverlay.getInstance().getAudienceProvider().console();
        MessageBuilder messageBuilder = new MessageBuilder();
        TagResolver resolver = StandardTags.defaults();
        if (messages.length > 1) {
            for (String message : messages) {
                messageBuilder.appendMiniMessage(placeHolder(message, map, ignoreCase), resolver);
            }
            messageBuilder.send(console);
        } else {
            messageBuilder.appendMiniMessage(placeHolder(messages[0], map, ignoreCase), resolver);
            messageBuilder.send(console);
        }
    }

    /**
     * Sends a message to all online players.
     *
     */
    default void msgAll() {
        msgAll(Map.of(), false);
    }

    /**
     * Sends a message to all online players with placeholders.
     *
     * @param map        placeholder replacements
     * @param ignoreCase whether to ignore case
     */
    default void msgAll(Map<String, String> map, boolean ignoreCase) {
        SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().forEach(p -> msg(p, map, ignoreCase));
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
     * @param map        placeholder replacements
     * @param ignoreCase whether to ignore case
     * @param type       the message type (e.g. ACTIONBAR or CHAT)
     */
    default void msgAll(Map<String, String> map, boolean ignoreCase, MessageType type) {
        SkinOverlay.getInstance().getPlayerProvider().getOnlinePlayers().forEach(p -> msg(p, map, ignoreCase, type));
    }
}
