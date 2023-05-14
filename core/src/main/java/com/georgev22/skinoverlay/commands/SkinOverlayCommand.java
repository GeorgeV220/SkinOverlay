package com.georgev22.skinoverlay.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.config.FileManager;
import com.georgev22.skinoverlay.event.events.player.skin.PlayerObjectPreUpdateSkinEvent;
import com.georgev22.skinoverlay.event.events.user.UserEvent;
import com.georgev22.skinoverlay.event.events.user.data.UserModifyDataEvent;
import com.georgev22.skinoverlay.handler.Skin;
import com.georgev22.skinoverlay.utilities.*;
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
public class SkinOverlayCommand extends BaseCommand {
    protected final ObjectMap<String, String> placeholders = new HashObjectMap<>();
    protected final FileManager fm = SkinOverlay.getInstance().getFileManager();
    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();

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
        skinOverlay.getUserManager().getLoadedEntities().forEach((uuid, loadedUser) -> skinOverlay.getUserManager().getEntity(uuid).handle((user, throwable) -> {
            if (throwable != null) {
                skinOverlay.getLogger().log(Level.SEVERE, "Error: ", throwable);
                return null;
            }
            UserEvent event = new UserEvent(user, false);
            skinOverlay.getEventManager().callEvent(event);
            return user;
        }).thenApply(user -> {
            if (user == null) {
                return null;
            }
            UserModifyDataEvent modifyDataEvent = new UserModifyDataEvent(user, false);
            skinOverlay.getEventManager().callEvent(modifyDataEvent);
            if (modifyDataEvent.isCancelled())
                return user;
            if (!skinOverlay.getSkinOverlay().type().isProxy() && OptionsUtil.PROXY.getBooleanValue()) {
                return user;
            }
            skinOverlay.getUserManager().save(user);
            return user;
        }).thenAccept(user -> {
            if (user != null) {
                Optional<PlayerObject> optionalPlayerObject = skinOverlay.getPlayer(user.getId());
                if (optionalPlayerObject.isPresent() && optionalPlayerObject.get().isOnline()) {
                    PlayerObjectPreUpdateSkinEvent event = new PlayerObjectPreUpdateSkinEvent(optionalPlayerObject.get(), user, false);
                    skinOverlay.getEventManager().callEvent(event);
                    if (event.isCancelled())
                        return;
                    skinOverlay.getSkinHandler().updateSkin(event.getPlayerObject(), true);
                }
            }
        }));
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
        skinOverlay.getSkinManager().getEntity(skinUUID)
                .handleAsync((skin, throwable) -> {
                    if (throwable != null) {
                        skinOverlay.getLogger().log(Level.SEVERE, "Error SkinCommand 130:", throwable);
                    }
                    return skin;
                })
                .handleAsync((skin, throwable) -> {
                    if (skin == null) {
                        skin = new Skin(skinUUID, null, new SkinOptions(overlay));
                    }
                    if (!skin.skinOptions().equals(new SkinOptions(overlay))) {
                        skin.setSkinOptions(new SkinOptions(overlay));
                    }
                    return skin;
                })
                .thenAccept(skin -> skinOverlay.getSkinHandler().setSkin(
                        () -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), overlay + ".png")),
                        skin,
                        target.orElseThrow()
                ).thenAccept(
                        entity -> MessagesUtil.DONE.msg(
                                issuer,
                                new HashObjectMap<String, String>()
                                        .append("%player%", target.orElseThrow().playerName())
                                        .append("%url%", entity.skin().skinURL()),
                                true
                        )
                ));

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
                    ? new SkinOptions(args[0], Boolean.parseBoolean(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]), Boolean.parseBoolean(args[6]), Boolean.parseBoolean(args[7]))
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
            UUID skinUUID = Utilities.generateUUID(url + target.orElseThrow().playerUUID().toString());
            skinOverlay.getSkinManager().getEntity(skinUUID)
                    .handleAsync((skin, throwable) -> {
                        if (throwable != null) {
                            skinOverlay.getLogger().log(Level.SEVERE, "Error SkinCommand 197:", throwable);
                        }
                        return skin;
                    })
                    .handleAsync((skin, throwable) -> {
                        if (skin == null) {
                            skin = new Skin(skinUUID, null, skinOptions);
                        }
                        if (!skin.skinOptions().equals(skinOptions)) {
                            skin.setSkinOptions(skinOptions);
                        }
                        return skin;
                    })
                    .thenAccept(skin -> skinOverlay.getSkinHandler().setSkin(
                            () -> ImageIO.read(new ByteArrayInputStream(output.toByteArray())),
                            skin,
                            finalTarget.orElseThrow()
                    ).thenAccept(
                            entity -> MessagesUtil.DONE.msg(
                                    issuer,
                                    new HashObjectMap<String, String>()
                                            .append("%player%", finalTarget.orElseThrow().playerName())
                                            .append("%url%", entity.skin().skinURL()),
                                    true
                            )
                    ));

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
    public void clear(@NotNull CommandIssuer issuer, String[] args) {
        if (!issuer.isPlayer()) {
            if (args.length == 0) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "clear <player>"), true);
                return;
            }
            clear0(issuer, args[0]);
            return;
        }
        if (args.length == 0) {
            PlayerObject playerObject = skinOverlay.getPlayer(issuer.getUniqueId()).orElseThrow();
            UUID skinUUID = Utilities.generateUUID("default" + playerObject.playerUUID().toString());
            skinOverlay.getSkinManager().getEntity(skinUUID)
                    .handle((skin, throwable) -> {
                        if (throwable != null) {
                            skinOverlay.getLogger().log(Level.SEVERE, "Error:", throwable);
                        }
                        return skin;
                    })
                    .handle((skin, throwable) -> skin == null ? new Skin(skinUUID, null, "default") : skin)
                    .thenAccept(skin -> skinOverlay.getSkinHandler().setSkin(
                                    () -> null,
                                    skin,
                                    playerObject)
                            .thenAccept(user -> MessagesUtil.RESET.msg(
                                            issuer,
                                            new HashObjectMap<String, String>().append("%player%", playerObject.playerName()),
                                            true
                                    )
                            ));
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
        UUID skinUUID = Utilities.generateUUID("default" + optionalPlayerObject.orElseThrow().playerUUID().toString());
        skinOverlay.getSkinManager().getEntity(skinUUID)
                .handle((skin, throwable) -> {
                    if (throwable != null) {
                        skinOverlay.getLogger().log(Level.SEVERE, "Error:", throwable);
                    }
                    return skin;
                })
                .handle((skin, throwable) -> skin == null ? new Skin(skinUUID, null, "default") : skin)
                .thenAccept(skin -> skinOverlay.getSkinHandler().setSkin(
                                () -> null,
                                skin,
                                optionalPlayerObject.orElseThrow())
                        .thenAccept(user -> MessagesUtil.RESET.msg(
                                        issuer,
                                        new HashObjectMap<String, String>().append("%player%", optionalPlayerObject.orElseThrow().playerName()),
                                        true
                                )
                        ));
    }
}