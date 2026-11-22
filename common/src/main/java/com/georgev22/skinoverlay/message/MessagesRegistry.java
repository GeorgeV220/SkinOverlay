package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utils;
import com.georgev22.skinoverlay.utilities.config.CFG;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class MessagesRegistry {

    private static final Map<String, CFG> cachedFiles = new HashMap<>();

    private MessagesRegistry() {
    }

    /**
     * Registers all message entries inside the appropriate YML file.
     *
     * @param entries The message entries to register.
     */
    public static void registerAll(MessageEntry @NotNull [] @NotNull [] entries) {
        for (MessageEntry[] entry : entries) {
            for (MessageEntry messageEntry : entry) {
                register(messageEntry);
            }
        }
    }

    /**
     * Registers a message entry inside the appropriate YML file.
     *
     * @param entry The message entry to register.
     */
    public static void register(@NotNull MessageEntry entry) {
        String fileName = entry.getFile();
        CFG cfg = cachedFiles.computeIfAbsent(fileName, f ->
                new CFG(f, new File(SkinOverlay.getInstance().getDataFolder(), "messages"), false, false,
                        SkinOverlay.getInstance().getLogger(), SkinOverlay.getInstance().getClass()));

        cfg.reloadFile();

        boolean changed = false;

        if (cfg.getFileConfiguration().contains(entry.getPath())) {

            if (Utils.isList(cfg.getFileConfiguration(), entry.getPath())) {
                entry.setMessages(
                        cfg.getFileConfiguration().getStringList(entry.getPath())
                                .toArray(new String[0])
                );
            } else {
                entry.setMessages(new String[]{
                        cfg.getFileConfiguration().getString(entry.getPath())
                });
            }

        } else {
            // Write default values
            if (entry.getDefaultMessages().length > 1) {
                cfg.getFileConfiguration().set(entry.getPath(), entry.getDefaultMessages());
            } else {
                cfg.getFileConfiguration().set(entry.getPath(), entry.getDefaultMessages()[0]);
            }
            changed = true;
        }

        if (changed) {
            cfg.saveFile();
        }
    }

    /**
     * Gets the CFG instance for a specific file of a plugin.
     */
    public static @Nullable CFG getMessagesCFG(String fileName) {
        return cachedFiles.get(fileName);
    }
}
