package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlayBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class BukkitCommandManager extends CommandManager {

    private final SkinOverlayBukkit plugin;

    public BukkitCommandManager(Logger logger, SkinOverlayBukkit plugin) {
        super(logger);
        this.plugin = plugin;
    }

    @Override
    protected void registerCommand0(@NotNull BaseCommand command) throws ReflectiveOperationException {
        CommandMap commandMap;
        try {
            commandMap = plugin.getServer().getCommandMap();
        } catch (Exception e) {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        }

        for (String alias : command.getAliases(false)) {
            BukkitPluginCommandWrapper wrapper = new BukkitPluginCommandWrapper(command, alias);
            commandMap.register(plugin.getName(), wrapper);
        }
    }
}
