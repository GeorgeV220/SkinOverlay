package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.MessageEntry;
import com.georgev22.skinoverlay.message.MessagesRegistry;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.message.messages.CoreMessages;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Subcommand("reload")
@Permission("skinoverlay.reload")
public class ReloadSubCommand extends SkinOverlayBaseCommand {
    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer) {
        //TODO RELOAD SKIN HANDLER
        mainPlugin.getFileManager().getConfig().reloadFile();
        try {
            MessagesRegistry.registerAll(new MessageEntry[][]{
                    CommandMessages.values(),
                    CoreMessages.values(),
            });
        } catch (Exception e) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error loading the language file: ", e);
        }
        CoreMessages.PLUGIN_RELOAD.msg(commandIssuer);
    }
}
