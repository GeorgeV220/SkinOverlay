package com.georgev22.skinoverlay.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BukkitPluginCommandWrapper extends Command {
    private final BaseCommand baseCommand;

    public BukkitPluginCommandWrapper(@NotNull BaseCommand command, String alias) {
        super(alias);
        this.baseCommand = command;
        setAliases(new ArrayList<>(List.of(command.getAliases(false))));
        setDescription(command.getDescription());
        setUsage(command.getUsage());
        setPermission(command.getPermission());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        CommandIssuer commandIssuer = new BukkitCommandIssuer(sender);
        baseCommand.execute(commandIssuer, args);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) {
        CommandIssuer commandIssuer = new BukkitCommandIssuer(sender);
        return baseCommand.tabComplete(commandIssuer, args).stream().toList();
    }
}