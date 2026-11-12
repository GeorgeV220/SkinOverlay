package com.georgev22.skinoverlay.command.commands;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.CommandCompletion;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Description;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Usage;
import com.georgev22.skinoverlay.command.commands.sub.AddSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.ClearSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.CombineSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.InfoSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.ReloadSubCommand;
import com.georgev22.skinoverlay.command.commands.sub.WearSubCommand;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import org.jetbrains.annotations.NotNull;

@CommandAlias({"skinoverlay", "soverlay", "skino"})
@Permission("skinoverlay.help")
@CommandCompletion("help|reload|wear|overlay|multioverlay|add|equip|combine|clear|reset|info")
@Description("SkinOverlay commands")
@Usage("/skinoverlay [arguments]")
public class SkinOverlayMain extends SkinOverlayBaseCommand {

    public SkinOverlayMain() {
        this.addSubcommand(new WearSubCommand());
        this.addSubcommand(new AddSubCommand());
        this.addSubcommand(new CombineSubCommand());
        this.addSubcommand(new ClearSubCommand());
        // TODO re implement wearurl
        this.addSubcommand(new ReloadSubCommand());
        this.addSubcommand(new InfoSubCommand());
    }

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer) {
        CommandMessages.COMMAND_HELP.msg(commandIssuer);
    }
}
