package com.georgev22.skinoverlay.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.config.FileManager;
import com.georgev22.skinoverlay.utilities.MessagesUtil;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
        UserData.getAllUsersMap().forEach((uuid, skinUser) ->
                UserData.getUser(uuid).save(false, new Utils.Callback<>() {
                    @Override
                    public Boolean onSuccess() {
                        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                        try {
                            UserData.getUser(uuid).load(new Utils.Callback<>() {
                                @Override
                                public Boolean onSuccess() {
                                    atomicBoolean.set(true);
                                    Utilities.updateSkin(skinUser.getPlayer().orElseThrow(), true, false);
                                    return atomicBoolean.get();
                                }

                                @Override
                                public Boolean onFailure() {
                                    atomicBoolean.set(false);
                                    return atomicBoolean.get();
                                }

                                @Override
                                public Boolean onFailure(Throwable throwable) {
                                    atomicBoolean.set(false);
                                    return super.onFailure(throwable);
                                }
                            });
                        } catch (Exception e) {
                            atomicBoolean.set(false);
                            skinOverlay.getLogger().log(Level.SEVERE, "Error: ", e);
                        }
                        return atomicBoolean.get();
                    }

                    @Override
                    public Boolean onFailure() {
                        return false;
                    }

                    @Override
                    public Boolean onFailure(Throwable throwable) {
                        return super.onFailure(throwable);
                    }
                }));
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
            MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
            return;
        }
        String overlay = args[0];
        Optional<PlayerObject> target;
        if (args.length > 1) {
            if (issuer.hasPermission("skinoverlay.wear.overlay.others")) {
                target = skinOverlay.isOnline(args[1]) ? skinOverlay.getPlayer(args[1]) : Optional.empty();
                if (target.isEmpty()) {
                    MessagesUtil.OFFLINE_PLAYER.msg(issuer, new HashObjectMap<String, String>().append("%player%", args[1]), true);
                    return;
                }
            } else {
                MessagesUtil.NO_PERMISSION.msg(issuer);
                return;
            }
        } else {
            if (!(issuer.isPlayer())) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer, new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
                return;
            }
            target = skinOverlay.getPlayer(issuer.getUniqueId());
        }
        Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), overlay + ".png")), overlay, target.orElseThrow(), issuer);
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
            Utilities.setSkin(() -> null, "default", skinOverlay.getPlayer(issuer.getUniqueId()).orElseThrow(), issuer);
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
        Utilities.setSkin(() -> null, "default", optionalPlayerObject.orElseThrow(), issuer);
    }
}