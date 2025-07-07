package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class VelocityCommandManager extends CommandManager {

    private final ProxyServer proxyServer;
    private final com.velocitypowered.api.command.CommandManager velocityServerCommandManager;

    public VelocityCommandManager(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        this.velocityServerCommandManager = proxyServer.getCommandManager();
    }

    @Override
    protected void registerCommand0(@NotNull BaseCommand command) {
        for (String alias : command.getAliases(false)) {
            CommandMeta commandMeta = velocityServerCommandManager.metaBuilder(alias)
                    .plugin(SkinOverlay.getInstance().getPlugin())
                    .build();
            proxyServer.getCommandManager().register(commandMeta, new VelocityPluginCommandWrapper(command));
        }
    }
}
