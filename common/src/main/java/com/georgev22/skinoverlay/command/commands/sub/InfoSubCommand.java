package com.georgev22.skinoverlay.command.commands.sub;

import com.georgev22.skinoverlay.BuildParameters;
import com.georgev22.skinoverlay.command.CommandContext;
import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.Default;
import com.georgev22.skinoverlay.command.annotation.Permission;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.commands.SkinOverlayBaseCommand;
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

        commandIssuer.sendMessage("Name: " + pluginName);
        commandIssuer.sendMessage("Version: " + pluginVersion);
        commandIssuer.sendMessage("Author: " + pluginAuthor);
        commandIssuer.sendMessage("Description: " + pluginDescription);
        commandIssuer.sendMessage("Website: " + pluginWebsite);
        commandIssuer.sendMessage("CI Name: " + CIName);
        commandIssuer.sendMessage("CI Build Number: " + CIBuildNumber);
        commandIssuer.sendMessage("Commit: " + commit);
        commandIssuer.sendMessage("Branch: " + branch);
        commandIssuer.sendMessage("Build Time: " + buildTime);
    }
}
