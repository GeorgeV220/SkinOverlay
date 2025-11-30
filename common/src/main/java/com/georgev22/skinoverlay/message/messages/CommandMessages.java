package com.georgev22.skinoverlay.message.messages;

import com.georgev22.skinoverlay.message.MessageEntry;
import org.jetbrains.annotations.NotNull;

public enum CommandMessages implements MessageEntry {
    COMMAND_INVALID_ARGUMENT("Messages.command.invalid-argument", "&c&l(!) &cInvalid argument (%arg%) for command %command%"),
    COMMAND_MISSING_ARGUMENT("Messages.command.missing-argument", "&c&l(!) &cMissing argument (%arg%) for command %command%"),
    COMMAND_USAGE("Messages.command.usage", "&c&l(!) &cUsage: %usage%"),
    COMMAND_TARGET_DENIED("Messages.command.target-denied", "&c&l(!) &cThis command can only be executed by: %targets%"),
    COMMAND_PERMISSION_DENIED("Messages.command.permission-denied", "&c&l(!) &cYou do not have the correct permissions to do this!"),
    COMMAND_ERROR("Messages.command.error", "&c&l(!) &cAn error occurred while executing command %command%"),
    COMMAND_OFFLINE_PLAYER("Messages.command.offline-player", "&c&l(!) &cPlayer %player% is offline!"),

    COMMAND_HELP("Messages.command.help",
            "",
            "&c&l(!) &cSkinOverlay Commands:",
            "&8» &7/skinoverlay &ehelp &7- &oShows this help.",
            "&8» &7/skinoverlay &eoverlay &o<overlay> &7- &oWear a specific overlay from the plugin files.",
            "&8» &7/skinoverlay &eurl &o<url> &7- &oWear a specific overlay from a URL.",
            "&8» &7/skinoverlay &ereset &7- &oReset the player's skin.",
            "&8» &7/skinoverlay &ereload &7- &oReload the plugin configuration files (some settings need server restart).",
            ""),

    COMMAND_OVERLAY_NOT_FOUND("Messages.command.overlay.not-found", "&c&l(!)&c Overlay %overlay% not found!"),

    COMMAND_OVERLAY_DONE("Messages.command.overlay.done", "&a&l(!)&a Overlay %url% applied!"),

    COMMAND_OVERLAY_RESET("Messages.command.overlay.reset", "&a&l(!)&a Default skin applied (%player%)!"),

    COMMAND_FAILED_TO_RETRIEVE_OR_GENERATE_SKIN("Messages.command.failed-to-retrieve-or-generate-skin", "&c&l(!)&c Failed to retrieve or generate skin for %player%!"),

    COMMAND_INVALID_URL("Messages.command.invalid-url", "&c&l(!)&c Invalid URL or failed to download image: %url%");

    private final String path;
    private String[] messages;

    CommandMessages(String path, String... defaultMessages) {
        this.path = path;
        this.messages = defaultMessages;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String[] getDefaultMessages() {
        return messages;
    }

    @Override
    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    @Override
    public String[] getMessages() {
        return messages;
    }

    @Override
    public @NotNull String getFile() {
        return "commands";
    }
}
