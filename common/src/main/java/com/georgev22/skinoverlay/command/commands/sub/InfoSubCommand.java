package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.BuildParameters;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
import com.georgev22.skinoverlay.message.MessageBuilder;
import org.jetbrains.annotations.NotNull;

@Subcommand("info")
@CommandAlias("soinfo")
@Permission("skinoverlay.info")
public class InfoSubCommand extends SkinOverlayBaseCommand {

    @Default
    protected void handle(@NotNull CommandIssuer commandIssuer) {
        String pluginName = BuildParameters.PLUGIN_NAME;
        String pluginVersion = BuildParameters.VERSION;
        String pluginAuthor = BuildParameters.AUTHOR;
        String pluginDescription = BuildParameters.DESCRIPTION;
        String pluginWebsite = BuildParameters.URL;
        String CIName = BuildParameters.CI_NAME;
        String CIBuildNumber = BuildParameters.CI_BUILD_NUMBER;
        String commit = BuildParameters.COMMIT;
        String branch = BuildParameters.BRANCH;
        String buildTime = BuildParameters.BUILD_TIME;

        MessageBuilder.builder()
                .appendln("Name: " + pluginName)
                .appendln("Version: " + pluginVersion)
                .appendln("Author: " + pluginAuthor)
                .appendln("Description: " + pluginDescription)
                .appendln("Website: " + pluginWebsite)
                .appendln("CI Name: " + CIName)
                .appendln("CI Build Number: " + CIBuildNumber)
                .appendln("Commit: " + commit)
                .appendln("Branch: " + branch)
                .appendln("Build Time: " + buildTime)
                .send(commandIssuer.audience());
    }
}
