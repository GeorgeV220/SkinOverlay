package com.georgev22.skinoverlay.utilities.config;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.utilities.Locale;
import com.georgev22.skinoverlay.utilities.MessageBuilder;
import com.georgev22.skinoverlay.utilities.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.georgev22.skinoverlay.utilities.Utils.placeHolder;

public enum MessagesUtil {
    HELP_FORMAT("Messages.help",
            "",
            "&c&l(!) &cSkinOverlay Commands:",
            "&8» &7/skinoverlay &ehelp &7- &oShows this help.",
            "&8» &7/skinoverlay &eoverlay &o<overlay> &7- &oWear a specific overlay from the plugin files.",
            "&8» &7/skinoverlay &eurl &o<url> &7- &oWear a specific overlay from a URL.",
            "&8» &7/skinoverlay &ereset &7- &oReset the player's skin.",
            "&8» &7/skinoverlay &ereload &7- &oReload the plugin configuration files (some settings need server restart).",
            ""
    ),
    NO_PERMISSION("Messages.No Permission", "&c&l(!)&c You do not have the correct permissions to do this!"),
    ONLY_PLAYER_COMMAND("Messages.Only Player Command", "&c&l(!)&c Only players can run this command!"),
    OFFLINE_PLAYER("Messages.Offline Player", "&c&l(!)&c Player %player% is offline!"),
    DONE("Messages.Overlay Applied", "&a&l(!)&a Overlay %url% applied!"),
    RESET("Messages.Overlay Reset", "&a&l(!)&a Default skin applied (%player%)!"),
    OVERLAY_NOT_FOUND("Messages.Overlay Not Found", "&c&l(!)&c Overlay %overlay% not found!"),
    INSUFFICIENT_ARGUMENTS("Messages.Insufficient arguments", "&c&l(!)&c Insufficient arguments (%command%)"),
    INVALID_URL("Messages.Invalid URL", "&c&l(!)&c Invalid URL or failed to download image: %url%"),
    ERROR("Messages.Error", "&c&l(!)&c An error occurred: %error%"),
    FAILED_TO_RETRIEVE_OR_GENERATE_SKIN("Messages.Failed To Retrieve Or Generate Skin", "&c&l(!)&c Failed to retrieve or generate skin for %player%!"),
    ;
    private static final SkinOverlay mainPlugin = SkinOverlay.getInstance();
    private static CFG messagesCFG;
    private static Locale locale;
    private final String path;
    private String[] messages;

    MessagesUtil(String path, String... messages) {
        this.messages = messages;
        this.path = path;
    }

    public static void repairPaths(Locale locale) throws Exception {
        if (messagesCFG == null | !Objects.equals(MessagesUtil.locale, locale)) {
            setLocale(locale);
            setMessagesCFG(new CFG("messages_" + locale.getStringLocale(), mainPlugin.getDataFolder(), true, true, mainPlugin.getLogger(), mainPlugin.getClass()));
        }
        boolean changed = false;
        for (MessagesUtil enumMessage : MessagesUtil.values()) {
            if (messagesCFG.getFileConfiguration().contains(enumMessage.getPath())) {
                setPathToMessage(messagesCFG, enumMessage);
                continue;
            }
            setMessageToPath(messagesCFG, enumMessage);
            if (changed) continue;
            changed = true;
        }
        if (changed) {
            messagesCFG.saveFile();
        }
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        MessagesUtil.locale = locale;
    }

    public static CFG getMessagesCFG() {
        return messagesCFG;
    }

    public static void setMessagesCFG(CFG messagesCFG) {
        MessagesUtil.messagesCFG = messagesCFG;
    }

    private static void setMessageToPath(CFG cfg, @NotNull MessagesUtil enumMessage) {
        if (enumMessage.isMultiLined()) {
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages());
        } else {
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages()[0]);
        }
    }

    private static void setPathToMessage(@NotNull CFG cfg, @NotNull MessagesUtil enumMessage) {
        if (Utils.isList(cfg.getFileConfiguration(), enumMessage.getPath())) {
            enumMessage.setMessages(cfg.getFileConfiguration().getStringList(enumMessage.getPath()).toArray(new String[0]));
        } else {
            enumMessage.setMessages(cfg.getFileConfiguration().getString(enumMessage.getPath()));
        }
    }

    private boolean isMultiLined() {
        return this.messages.length > 1;
    }

    public String getPath() {
        return this.path;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public void setMessages(String messages) {
        this.messages[0] = messages;
    }

    public void msg(@NotNull CommandIssuer sender) {
        this.msg(sender, new HashObjectMap<>(), false, MessageType.CHAT);
    }

    public void msg(@NotNull CommandIssuer sender, MessageType messageType) {
        this.msg(sender, new HashObjectMap<>(), false, messageType);
    }

    public void msg(@NotNull CommandIssuer sender, Map<String, String> map, boolean ignoreCase) {
        this.msg(sender, map, ignoreCase, MessageType.CHAT);
    }

    public void msg(CommandIssuer sender, Map<String, String> map, boolean ignoreCase, @NotNull MessageType messageType) {
        MessageBuilder messageBuilder = new MessageBuilder();
        TagResolver tagResolver = StandardTags.defaults(); // ALL TAG RESOLVERS
        Audience audience = mainPlugin.getAudienceProvider().player(sender.getUniqueId());
        switch (messageType) {
            case ACTIONBAR -> audience.sendActionBar(
                    messageBuilder
                            .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), tagResolver)
                            .buildAndReset()
            );
            case TITLE -> audience.showTitle(Title.title(
                    messageBuilder
                            .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), tagResolver)
                            .buildAndReset(),
                    messages.length > 1
                            ? messageBuilder
                            .appendMiniMessage(placeHolder(messages[1], map, ignoreCase), tagResolver)
                            .buildAndReset()
                            : Component.text(""),
                    Title.DEFAULT_TIMES
            ));
            case CHAT -> {
                if (this.isMultiLined()) {
                    Arrays.stream(messages).forEach(message -> audience.sendMessage(
                            messageBuilder
                                    .appendMiniMessage(placeHolder(message, map, ignoreCase), tagResolver)
                                    .buildAndReset()
                    ));
                } else {
                    audience.sendMessage(
                            messageBuilder
                                    .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), tagResolver)
                                    .buildAndReset()
                    );
                }
            }
        }
    }

    public void msgConsole() {
        msgConsole(new HashMap<>(), false);
    }

    public void msgConsole(Map<String, String> map, boolean ignoreCase) {
        MessageBuilder messageBuilder = new MessageBuilder();
        TagResolver tagResolver = StandardTags.defaults(); // ALL TAG RESOLVERS
        Audience audience = mainPlugin.getAudienceProvider().console();
        if (this.isMultiLined()) {
            Arrays.stream(messages).forEach(message -> audience.sendMessage(
                    messageBuilder
                            .appendMiniMessage(placeHolder(message, map, ignoreCase), tagResolver)
                            .buildAndReset()
            ));
        } else {
            audience.sendMessage(
                    messageBuilder
                            .appendMiniMessage(placeHolder(messages[0], map, ignoreCase), tagResolver)
                            .buildAndReset()
            );
        }
    }

    public void msgAll() {
        mainPlugin.getPlayerProvider().getOnlinePlayers()
                .forEach(this::msg);
    }

    public void msgAll(Map<String, String> map, boolean ignoreCase) {
        mainPlugin.getPlayerProvider().getOnlinePlayers()
                .forEach(player -> this.msg(player, map, ignoreCase));
    }

    public enum MessageType {
        CHAT,
        ACTIONBAR,
        TITLE,
    }

}