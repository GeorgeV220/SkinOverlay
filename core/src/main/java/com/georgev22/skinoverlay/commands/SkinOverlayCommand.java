package com.georgev22.skinoverlay.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Locale;
import com.georgev22.skinoverlay.utilities.SkinOptions;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
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
import java.util.UUID;
import java.util.logging.Level;

@CommandAlias("skinoverlay|soverlay|skino")
public final class SkinOverlayCommand extends BaseCommand {
    private final ObjectMap<String, String> placeholders = new HashObjectMap<>();
    private final FileManager fm = SkinOverlay.getInstance().getFileManager();
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    @Default
    @HelpCommand
    @Subcommand("help")
    @CommandAlias("shelp")
    @Description("{@@Commands.Descriptions.SkinOverlay.help}")
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
    @Description("{@@Commands.Descriptions.SkinOverlay.reload}")
    @CommandPermission("skinoverlay.reload")
    public void reload(final @NotNull CommandIssuer issuer) {
        //TODO RELOAD SKIN HANDLER
        skinOverlay.getFileManager().getConfig().reloadFile();
        try {
            MessagesUtil.repairPaths(Locale.fromString(OptionsUtil.LOCALE.getStringValue()));
            MessagesUtil.getMessagesCFG().reloadFile();
            skinOverlay.loadCommandLocales();
        } catch (Exception e) {
            skinOverlay.getLogger().log(Level.SEVERE, "Error loading the language file: ", e);
        }
        issuer.sendMessage(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacy('&').deserialize("&a&l(!)&a Plugin reloaded!")));

    }

    @Subcommand("overlay")
    @CommandAlias("wear|swear")
    @CommandCompletion("@overlays @players ")
    @Description("{@@Commands.Descriptions.SkinOverlay.overlay}")
    @CommandPermission("skinoverlay.wear.overlay")
    @Syntax("wear <overlay> [player]")
    public void overlay(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        if (args.length < 1) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear <overlay> <player>"), true);
            return;
        }

        var overlay = args[0];
        Optional<PlayerObject> target;

        if (args.length > 1) {
            target = getPlayerObject(issuer, args[1]);
            if (target.isEmpty()) return;
        } else if (!(issuer.isPlayer())) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
            return;
        } else {
            target = skinOverlay.getPlayer(issuer.getUniqueId());
        }
        UUID skinUUID = Utilities.generateUUID(overlay + target.orElseThrow().playerUUID().toString());
        skinOverlay.getSkinHandler().retrieveOrGenerateSkin(
                        target.orElseThrow(),
                        () -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), overlay + ".png")),
                        new SkinOptions(overlay))
                .thenAccept(skin -> {
                    if (skin != null) {
                        skinOverlay.getSkinHandler().setSkin(target.orElseThrow(), skin);
                        MessagesUtil.DONE.msg(
                                issuer,
                                new HashObjectMap<String, String>()
                                        .append("%player%", target.orElseThrow().playerName())
                                        .append("%url%", skin.skinURL())
                                        .append("%name%", skin.skinOptions().getSkinName())
                                        .append("%skinOptions%", skin.skinOptions().toString()),
                                true
                        );
                    }
                });
    }

    @Subcommand("url")
    @CommandAlias("wurl|swurl|wearurl")
    @CommandCompletion("<link> false|true|@players false|true false|true false|true false|true false|true false|true @players")
    @Description("{@@Commands.Descriptions.SkinOverlay.url}")
    @CommandPermission("skinoverlay.wear.url")
    @Syntax("url <url> <options> [player]")
    public void url(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        if (args.length < 1) {
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "url <url> <options> <player>"), true);
            return;
        }

        try {
            URL url = new URL(args[0]);
            Optional<PlayerObject> target = skinOverlay.getPlayer(issuer.getUniqueId());
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (InputStream stream = url.openStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) > -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            var skinOptions = args.length == 8
                    ? new SkinOptions(args[0],
                    Boolean.parseBoolean(args[1]),
                    Boolean.parseBoolean(args[2]),
                    Boolean.parseBoolean(args[3]),
                    Boolean.parseBoolean(args[4]),
                    Boolean.parseBoolean(args[5]),
                    Boolean.parseBoolean(args[6]),
                    Boolean.parseBoolean(args[7]))
                    : new SkinOptions("custom2");

            if (args.length > 1 & args.length < 3) {
                target = getPlayerObject(issuer, args[1]);
                if (target.isEmpty()) return;
            } else if (args.length > 8) {
                target = getPlayerObject(issuer, args[8]);
                if (target.isEmpty()) return;
            } else if (!(issuer.isPlayer())) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "url <url> <options> <player>"), true);
                return;
            }

            Optional<PlayerObject> finalTarget = target;
            UUID skinUUID = Utilities.generateUUID(url + finalTarget.orElseThrow().playerUUID().toString());
            skinOverlay.getSkinHandler().retrieveOrGenerateSkin(
                    target.orElseThrow(),
                    () -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())),
                    skinOptions).thenAccept(skin -> {
                if (skin != null) {
                    skinOverlay.getSkinHandler().setSkin(finalTarget.orElseThrow(), skin);
                    MessagesUtil.DONE.msg(
                            issuer,
                            new HashObjectMap<String, String>()
                                    .append("%player%", finalTarget.orElseThrow().playerName())
                                    .append("%url%", skin.skinURL()),
                            true
                    );
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Optional<PlayerObject> getPlayerObject(@NotNull CommandIssuer issuer, String name) {
        Optional<PlayerObject> target;
        if (issuer.hasPermission("skinoverlay.wear.overlay.others")) {
            target = skinOverlay.isOnline(name) ? skinOverlay.getPlayer(name) : Optional.empty();
            if (target.isEmpty()) {
                MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", name), true);
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
    @Description("{@@Commands.Descriptions.SkinOverlay.clear}")
    @CommandPermission("skinoverlay.wear.clear")
    @Syntax("clear [player]")
    public void clear(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        if (args.length == 0) {
            if (!issuer.isPlayer()) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "clear <player>"), true);
                return;
            }
            PlayerObject playerObject = skinOverlay.getPlayer(issuer.getUniqueId()).orElseThrow();
            SkinOptions skinOptions = new SkinOptions();
            UUID skinUUID = Utilities.generateUUID(skinOptions.getSkinName() + playerObject.playerUUID().toString());
            skinOverlay.getSkinHandler().retrieveOrGenerateSkin(
                            playerObject,
                            null,
                            skinOptions)
                    .thenAccept(skin -> {
                        if (skin != null) {
                            skinOverlay.getSkinHandler().setSkin(playerObject, skin);
                            MessagesUtil.RESET.msg(
                                    issuer,
                                    new HashObjectMap<String, String>().append("%player%", playerObject.playerName()),
                                    true
                            );
                        }
                    });
        } else {
            Optional<PlayerObject> optionalPlayerObject = skinOverlay.getPlayer(args[0]);
            if (optionalPlayerObject.isEmpty()) {
                MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", args[0]), true);
                return;
            }
            SkinOptions skinOptions = new SkinOptions();
            UUID skinUUID = Utilities.generateUUID(skinOptions.getSkinName() + optionalPlayerObject.orElseThrow().playerUUID().toString());
            skinOverlay.getSkinHandler().retrieveOrGenerateSkin(
                            optionalPlayerObject.orElseThrow(),
                            null,
                            skinOptions)
                    .thenAccept(skin -> {
                        if (skin != null) {
                            skinOverlay.getSkinHandler().setSkin(optionalPlayerObject.orElseThrow(), skin);
                            MessagesUtil.RESET.msg(
                                    issuer,
                                    new HashObjectMap<String, String>().append("%player%", optionalPlayerObject.orElseThrow().playerName()),
                                    true
                            );
                        }
                    });
        }
    }
}