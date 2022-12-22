package com.georgev22.skinoverlay.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.BungeeMinecraftUtils;
import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.georgev22.library.utilities.Utils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.config.FileManager;
import com.georgev22.skinoverlay.utilities.MessagesUtil;
import com.georgev22.skinoverlay.utilities.Utilities;
import com.georgev22.skinoverlay.utilities.player.PlayerObject;
import com.georgev22.skinoverlay.utilities.player.UserData;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
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
        if (skinOverlay.isBungee()) {
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&c&l(!)&c Commands &c&l(!)");
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay reload");
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay overlay");
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay url");
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay clear");
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&c&l==============");
        } else {
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&c&l(!)&c Commands &c&l(!)");
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay reload");
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay overlay");
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay url");
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&6/skinoverlay clear");
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&c&l==============");
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
                                    Utilities.updateSkin(new PlayerObject.PlayerObjectWrapper(uuid, skinOverlay.isBungee()).getPlayerObject(), true, false);
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
        if (skinOverlay.isBungee()) {
            BungeeMinecraftUtils.msg(issuer.getIssuer(), "&a&l(!)&a Plugin reloaded!");
        } else {
            BukkitMinecraftUtils.msg(issuer.getIssuer(), "&a&l(!)&a Plugin reloaded!");
        }

    }

    @Subcommand("overlay")
    @CommandAlias("wear|swear")
    @CommandCompletion("@overlays @players ")
    @Description("{@@commands.descriptions.skinoverlay.overlay}")
    @CommandPermission("skinoverlay.wear.overlay")
    @Syntax("overlay <overlay> [player]")
    public void overlay(@NotNull CommandIssuer issuer, String @NotNull [] args) {
        if (args.length == 0) {
            return;
        }
        String overlay = args[0];
        Optional<PlayerObject> target;
        if (args.length > 1) {
            if (issuer.hasPermission("skinoverlay.wear.overlay.others")) {
                target = skinOverlay.getSkinOverlay().onlinePlayers().stream().filter(playerObject -> playerObject.playerName().equalsIgnoreCase(args[0])).findFirst();
                if (target.isEmpty()) {
                    MessagesUtil.OFFLINE_PLAYER.msg(issuer.getIssuer(), new HashObjectMap<String, String>().append("%player%", args[1]), true);
                    return;
                }
            } else {
                MessagesUtil.NO_PERMISSION.msg(issuer.getIssuer());
                return;
            }
        } else {
            if (!(issuer.isPlayer())) {
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer.getIssuer(), new HashObjectMap<String, String>().append("%command%", "wear skin <overlay> <player>"), true);
                return;
            }
            target = Optional.of(new PlayerObject.PlayerObjectWrapper(issuer.getUniqueId(), skinOverlay.isBungee()).getPlayerObject());
        }
        Utilities.setSkin(() -> ImageIO.read(new File(skinOverlay.getSkinsDataFolder(), overlay + ".png")), overlay, new PlayerObject.PlayerObjectWrapper(target.get().playerUUID(), skinOverlay.isBungee()).getPlayerObject());
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
                MessagesUtil.INSUFFICIENT_ARGUMENTS.msg(issuer.getIssuer(), new HashObjectMap<String, String>().append("%command%", "wear clear <player>"), true);
                return;
            }
            clear0(issuer.getIssuer(), args[0]);
            return;
        }
        if (args.length == 0) {
            Utilities.setSkin(() -> null, "default", new PlayerObject.PlayerObjectWrapper(issuer.getUniqueId(), skinOverlay.isBungee()).getPlayerObject());
        } else {
            clear0(issuer.getIssuer(), args[0]);
        }

    }

    private void clear0(@NotNull CommandIssuer issuer, String target) {
        Optional<PlayerObject> optionalPlayerObject = skinOverlay.getSkinOverlay().onlinePlayers().stream().filter(playerObject -> playerObject.playerName().equalsIgnoreCase(target)).findFirst();
        if (optionalPlayerObject.isEmpty()) {
            MessagesUtil.OFFLINE_PLAYER.msg(issuer.getIssuer(), new HashObjectMap<String, String>().append("%player%", target), true);
            return;
        }
        Utilities.setSkin(() -> null, "default", new PlayerObject.PlayerObjectWrapper(optionalPlayerObject.get().playerUUID(), skinOverlay.isBungee()).getPlayerObject());
    }
}