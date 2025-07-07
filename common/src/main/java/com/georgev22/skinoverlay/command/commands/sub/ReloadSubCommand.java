package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandContext;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.utilities.Locale;
import com.georgev22.skinoverlay.utilities.MessageBuilder;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Subcommand("reload")
@Permission("skinoverlay.reload")
public class ReloadSubCommand extends SkinOverlayBaseCommand {
    @Override
    protected void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        //TODO RELOAD SKIN HANDLER
        mainPlugin.getFileManager().getConfig().reloadFile();
        try {
            MessagesUtil.repairPaths(Locale.fromString(OptionsUtil.LOCALE.getStringValue()));
            MessagesUtil.getMessagesCFG().reloadFile();
        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error loading the language file: ", e);
        }
        commandIssuer.sendMessage(MessageBuilder.builder().appendMiniMessage("&a&l(!)&a Plugin reloaded!").build());
    }
}
