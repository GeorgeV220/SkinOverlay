package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * Wrapper class to adapt BaseCommand to Velocity SimpleCommand.
 */
public class VelocityPluginCommandWrapper implements SimpleCommand {

    private final BaseCommand baseCommand;

    public VelocityPluginCommandWrapper(BaseCommand baseCommand) {
        this.baseCommand = baseCommand;
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        try {
            baseCommand.execute(new VelocityCommandIssuer(source), args);
        } catch (Exception e) {
            SkinOverlay.getInstance().getLogger().log(Level.SEVERE, "An error occurred while executing the command.", e);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        String permission = baseCommand.getPermission();
        return permission == null || permission.isEmpty() || invocation.source().hasPermission(permission);
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        CommandIssuer commandIssuer = new VelocityCommandIssuer(invocation.source());
        return baseCommand.tabComplete(commandIssuer, invocation.arguments()).stream().toList();
    }
}