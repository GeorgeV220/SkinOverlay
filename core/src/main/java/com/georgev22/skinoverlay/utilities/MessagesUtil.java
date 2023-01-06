package com.georgev22.skinoverlay.utilities;

import co.aikar.commands.CommandIssuer;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.utilities.Utils;
import com.georgev22.library.yaml.configmanager.CFG;
import com.georgev22.skinoverlay.SkinOverlay;

import java.util.Map;

import com.georgev22.skinoverlay.utilities.interfaces.SkinOverlayImpl;
import org.jetbrains.annotations.NotNull;

public enum MessagesUtil {
    NO_PERMISSION("Messages.No Permission", "&c&l(!)&c You do not have the correct permissions to do this!"),
    ONLY_PLAYER_COMMAND("Messages.Only Player Command", "&c&l(!)&c Only players can run this command!"),
    OFFLINE_PLAYER("Messages.Offline Player", "&c&l(!)&c Player %player% is offline!"),
    DONE("Messages.Overlay Applied", "&a&l(!)&a Overlay %url% applied!"),
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
        this.msg(issuer.getIssuer(), new HashObjectMap<String, String>(), false);
    }

    public void msg(CommandIssuer issuer, Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
                BungeeMinecraftUtils.msg(issuer.getIssuer(), this.getMessages(), map, ignoreCase);
            } else {
                BukkitMinecraftUtils.msg(issuer.getIssuer(), this.getMessages(), map, ignoreCase);
            }
        } else if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
            BungeeMinecraftUtils.msg(issuer.getIssuer(), this.getMessages()[0], map, ignoreCase);
        } else {
            BukkitMinecraftUtils.msg(issuer.getIssuer(), this.getMessages()[0], map, ignoreCase);
        }
    }

    public void msgAll() {
        if (this.isMultiLined()) {
            if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
                SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BungeeMinecraftUtils.msg((net.md_5.bungee.api.CommandSender) playerObject.getPlayer(), this.getMessages()));
            } else {
                SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BukkitMinecraftUtils.msg((org.bukkit.command.CommandSender) playerObject.getPlayer(), this.getMessages()));
            }
        } else if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
            SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BungeeMinecraftUtils.msg((net.md_5.bungee.api.CommandSender) playerObject.getPlayer(), this.getMessages()[0]));
        } else {
            SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BukkitMinecraftUtils.msg((org.bukkit.command.CommandSender) playerObject.getPlayer(), this.getMessages()[0]));
        }
    }

    public void msgAll(Map<String, String> map, boolean ignoreCase) {
        if (this.isMultiLined()) {
            if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
                SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BungeeMinecraftUtils.msg((net.md_5.bungee.api.CommandSender) playerObject.getPlayer(), this.getMessages()));
            } else {
                SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BukkitMinecraftUtils.msg((org.bukkit.command.CommandSender) playerObject.getPlayer(), this.getMessages()[0], map, ignoreCase));
            }
        } else if (SkinOverlay.getInstance().type().equals(SkinOverlayImpl.Type.BUNGEE)) {
            SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BungeeMinecraftUtils.msg((net.md_5.bungee.api.CommandSender) playerObject.getPlayer(), this.getMessages(), map, ignoreCase));
        } else {
            SkinOverlay.getInstance().getSkinOverlay().onlinePlayers().forEach(playerObject -> BukkitMinecraftUtils.msg((org.bukkit.command.CommandSender) playerObject.getPlayer(), this.getMessages()[0], map, ignoreCase));
        }
    }

}

