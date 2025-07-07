package com.georgev22.skinoverlay.command.commands;

import com.georgev22.skinoverlay.command.CommandContext;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.commands.sub.ClearSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.ReloadSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.WearSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.WearUrlSubCommand;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import org.jetbrains.annotations.NotNull;

@CommandAlias({"skinoverlay", "soverlay", "skino"})
@Permission("skinoverlay.help")
@CommandCompletion("help|reload|wear|reset|url")
@Description("SkinOverlay commands")
@Usage("/skinoverlay [arguments]")
public class SkinOverlayMain extends SkinOverlayBaseCommand {

    public SkinOverlayMain() {
        this.addSubcommand(new WearSubCommand());
        this.addSubcommand(new ClearSubCommand());
        this.addSubcommand(new WearUrlSubCommand());
        this.addSubcommand(new ReloadSubCommand());
    }

    @Override
    protected void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        MessagesUtil.HELP_FORMAT.msg(commandIssuer);
    }
}
