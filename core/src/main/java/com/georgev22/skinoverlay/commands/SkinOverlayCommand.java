package com.georgev22.skinoverlay.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.config.FileManager;
import com.georgev22.skinoverlay.utilities.MessagesUtil;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

@CommandAlias("skinoverlay|soverlay|skino")
public class SkinOverlayCommand extends BaseCommand {
    protected final ObjectMap<String, String> placeholders = new HashObjectMap<>();
    protected final FileManager fm = SkinOverlay.getInstance().getFileManager();
    protected SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Default
    @HelpCommand
    @Subcommand("help")
    @CommandAlias("shelp")
    @Description("{@@commands.descriptions.skinoverlay.help}")
    @CommandPermission("skinoverlay.default")
    public void onHelp(final @NotNull CommandIssuer issuer) {
        for (String input : Arrays.asList(
                "&c&l(!)&c Commands &c&l(!)",
                "&6/skinoverlay reload",
                "&6/skinoverlay overlay",
                "&6/skinoverlay url",
                "&6/skinoverlay clear",
                "&c&l==============")) {
            issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize(input)));
        }
    }

    @Subcommand("reload")
    @CommandAlias("skinoverlayreload|soverlayreload|skinoreload|sreload")
    @CommandPermission("skinoverlay.reload")
    public void reload(final @NotNull CommandIssuer issuer) {
        skinOverlay.getUserManager().getLoadedUsers().forEach((uuid, loadedUser) -> {
            skinOverlay.getUserManager().getUser(uuid).handle((user, throwable) -> {
                if (throwable != null) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                    return null;
                }
                return user;
            }).thenApply(user -> {
                if (user == null) {
                    return null;
                }
                skinOverlay.getUserManager().save(user);
                return user;
            }).thenAccept(user -> {
                if (user != null) {
                    Optional<PlayerObject> optionalPlayerObject = skinOverlay.getPlayer(user.getId());
                    if (optionalPlayerObject.isPresent() && optionalPlayerObject.get().isOnline())
                        Utilities.updateSkin(optionalPlayerObject.get(), true);
                }
            });
        });
        skinOverlay.getFileManager().getConfig().reloadFile();
        skinOverlay.getFileManager().getData().reloadFile();
        skinOverlay.getFileManager().getMessages().reloadFile();
        MessagesUtil.repairPaths(fm.getMessages());
        issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize("&a&l(!)&a Plugin reloaded!")));

    }

    @Subcommand("overlay")
    @CommandAlias("wear|swear")
    @CommandCompletion("@overlays @players ")
    @Description("{@@commands.descriptions.skinoverlay.overlay}")
    @CommandPermission("skinoverlay.wear.overlay")
    @Syntax("wear <overlay> [player]")
    public void overlay(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        if (args.length == 0) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear <overlay> <player>"), true);
            return;
        }
        String overlay = args[0];
        Optional<PlayerObject> target;
        if (args.length > 1) {
            target = getPlayerObject(issuer, args);
            if (target.isEmpty()) return;
        } else {
            if (!(issuer.isPlayer())) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
                return;
            }
            target = skinOverlay.getPlayer(issuer.getUniqueId());
        }
        Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), overlay + ".png")), new SkinOptions(overlay), target.orElseThrow(), issuer);
    }

    @Subcommand("url")
    @CommandAlias("wurl|swurl|wearurl")
    @CommandCompletion("<link> false|true false|true false|true false|true false|true false|true @players")
    @CommandPermission("skinoverlay.wear.url")
    @Syntax("url <url> <options> [player]")
    public void url(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        try {
            URL url = new URL(args[0]);
            Optional<PlayerObject> target = skinOverlay.getPlayer(issuer.getUniqueId());


            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (InputStream stream = url.openStream()) {
                byte[] buffer = new byte[4096];

                while (true) {
                    int bytesRead = stream.read(buffer);
                    if (bytesRead < 0) {
                        break;
                    }
                    output.write(buffer, 0, bytesRead);
                }
            }

            SkinOptions skinOptions;
            if (args.length == 8) {
                skinOptions = new SkinOptions(url.toString(), Boolean.parseBoolean(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]), Boolean.parseBoolean(args[6]), Boolean.parseBoolean(args[7]));
            } else {
                skinOptions = new SkinOptions("custom2");
                if (args.length > 1 & args.length < 3) {
                    target = getPlayerObject(issuer, args);
                    if (target.isEmpty()) return;
                } else {
                    if (!(issuer.isPlayer())) {
                        MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "url <url> <options> <player>"), true);
                        return;
                    }
                    target = skinOverlay.getPlayer(issuer.getUniqueId());
                }
            }

            Utilities.setSkin(() -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())), skinOptions, target.orElseThrow(), issuer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Optional<PlayerObject> getPlayerObject(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        Optional<PlayerObject> target;
        if (issuer.hasPermission("skinoverlay.wear.overlay.others")) {
            target = skinOverlay.isOnline(args[1]) ? skinOverlay.getPlayer(args[1]) : Optional.empty();
            if (target.isEmpty()) {
                MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", args[1]), true);
                return Optional.empty();
            }
        } else {
            MessagesUtil.NO_PERMISSION.msg(issuer);
            return Optional.empty();
        }
        return target;
    }

    @Subcommand("clear")
    @CommandAlias("sclear")
    @CommandCompletion("@players ")
    @Description("{@@commands.descriptions.skinoverlay.clear}")
    @CommandPermission("skinoverlay.wear.clear")
    @Syntax("clear [player]")
    public void clear(@NotNull CommandIssuer issuer, String[] args) {
        if (!issuer.isPlayer()) {
            if (args.length == 0) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear clear <player>"), true);
                return;
            }
            clear0(issuer, args[0]);
            return;
        }
        if (args.length == 0) {
            Utilities.setSkin(() -> null, new SkinOptions("default"), skinOverlay.getPlayer(issuer.getUniqueId()).orElseThrow(), issuer);
        } else {
            clear0(issuer, args[0]);
        }

    }

    private void clear0(@NotNull CommandIssuer issuer, String target) {
        Optional<PlayerObject> optionalPlayerObject = skinOverlay.getPlayer(target);
        if (optionalPlayerObject.isEmpty()) {
            MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", target), true);
            return;
        }
        Utilities.setSkin(() -> null, new SkinOptions("default"), optionalPlayerObject.orElseThrow(), issuer);
    }
}