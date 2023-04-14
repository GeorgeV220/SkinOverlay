package com.georgev22.skinoverlay.utilities;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.minecraft.*;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.configmanager.CFG;
import com.georgev22.skinoverlay.SkinOverlay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import static com.georgev22.library.utilities.Utils.placeHolder;

public enum MessagesUtil {
    NO_PERMISSION("Messages.No Permission", "&c&l(!)&c You do not have the correct permissions to do this!"),
    ONLY_PLAYER_COMMAND("Messages.Only Player Command", "&c&l(!)&c Only players can run this command!"),
    OFFLINE_PLAYER("Messages.Offline Player", "&c&l(!)&c Player %player% is offline!"),
    DONE("Messages.Overlay Applied", "&a&l(!)&a Overlay %url% applied!"),
    RESET("Messages.Overlay Reset", "&a&l(!)&a Default skin applied(%player%)!!"),
    INSUFFICIENT_ARGUMENTS("Messages.Insufficient arguments", "&c&l(!)&c Insufficient arguments (%command%)");
    private String[] messages;
    private final String path;

    MessagesUtil(String path, String... messages) {
        this.messages = messages;
        this.path = path;
    }

    private boolean isMultiLined() {
        return this.messages.length > 1;
    }

    public static void repairPaths(CFG cfg) {
        boolean changed = false;
        for (MessagesUtil enumMessage : MessagesUtil.values()) {
            if (cfg.getFileConfiguration().contains(enumMessage.getPath())) {
                MessagesUtil.setPathToMessage(cfg, enumMessage);
                continue;
            }
            MessagesUtil.setMessageToPath(cfg, enumMessage);
            if (changed) continue;
            changed = true;
        }
        if (changed) {
            cfg.saveFile();
        }
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

    public void msg(@NotNull CommandIssuer issuer) {
        this.msg(issuer, new HashObjectMap<>(), false);
    }

    public void msg(CommandIssuer issuer, Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            for (String message : messages) {
                issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(placeHolder(message, map, ignoreCase))));
            }
        } else {
            issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(placeHolder(this.getMessages()[0], map, ignoreCase))));
        }
    }

    public void msgConsole() {
        msgConsole(new HashMap<>(), false);
    }

    public void msgConsole(Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            switch (SkinOverlay.getInstance().type()) {
                case BUKKIT ->
                        BukkitMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case BUNGEE ->
                        BungeeMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case VELOCITY ->
                        VelocityMinecraftUtils.printMsg(Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case SPONGE7 ->
                        Sponge7MinecraftUtils.printMsg(SkinOverlay.getInstance().getLogger(), Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
                case SPONGE8 ->
                        Sponge8MinecraftUtils.printMsg(SkinOverlay.getInstance().getLogger(), Utils.placeHolder(Arrays.stream(this.getMessages()).toList(), map, ignoreCase));
            }
        } else {
            switch (SkinOverlay.getInstance().type()) {
                case BUKKIT -> BukkitMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case BUNGEE -> BungeeMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case VELOCITY ->
                        VelocityMinecraftUtils.printMsg(Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case SPONGE7 ->
                        Sponge7MinecraftUtils.printMsg(SkinOverlay.getInstance().getLogger(), Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
                case SPONGE8 ->
                        Sponge8MinecraftUtils.printMsg(SkinOverlay.getInstance().getLogger(), Utils.placeHolder(this.getMessages()[0], map, ignoreCase));
            }
        }
    }

    public void msgAll() {
        if (this.isMultiLined()) {
            SkinOverlay.getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(this.getMessages()));
        } else {
            SkinOverlay.getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(this.getMessages()[0]));
        }
    }

    public void msgAll(Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            SkinOverlay.getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(placeHolder(this.getMessages(), map, ignoreCase)));
        } else {
            SkinOverlay.getInstance().onlinePlayers().forEach(playerObject -> playerObject.sendMessage(placeHolder(this.getMessages()[0], map, ignoreCase)));
        }
    }

}

