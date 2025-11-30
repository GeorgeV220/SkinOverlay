package com.georgev22.skinoverlay.message.messages;

import com.georgev22.skinoverlay.message.MessageEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Default core messages provided by the Core plugin.
 * <p>
 * Other plugins can define their own enums implementing {@link MessageEntry}.
 */
public enum CoreMessages implements MessageEntry {

    PLUGIN_RELOAD("Messages.plugin-reload", "&a&l(!) &aAll data have been reloaded successfully."),
    PLUGIN_LOADING_DATA("Messages.plugin-loading-data", "&aLoading plugin data."),
    NUMBER_INVALID("Messages.number.invalid", "&c&l(!) &c%number%&c is not a valid number."),

    ;

    private final String path;
    private String[] messages;

    CoreMessages(String path, String... defaultMessages) {
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
        return "core";
    }
}
