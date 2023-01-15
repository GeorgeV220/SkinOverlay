package com.georgev22.skinoverlay.utilities.player;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.minecraft.VelocityMinecraftUtils;
import com.georgev22.skinoverlay.SkinOverlay;
import com.google.common.collect.Lists;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.ModInfo;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;

public class PlayerObjectVelocity implements PlayerObject {

    private final Player player;

    public PlayerObjectVelocity(final UUID uuid, final String name) {
        this.player = new Player() {
            @Override
            public String getUsername() {
                return name;
            }

            @Override
            public @Nullable Locale getEffectiveLocale() {
                return Locale.US;
            }

            @Override
            public void setEffectiveLocale(Locale locale) {
                throw new UnsupportedOperationException();
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }

            @Override
            public Optional<ServerConnection> getCurrentServer() {
                return Optional.empty();
            }

            @Override
            public PlayerSettings getPlayerSettings() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<ModInfo> getModInfo() {
                return Optional.empty();
            }

            @Override
            public long getPing() {
                return 0;
            }

            @Override
            public boolean isOnlineMode() {
                return SkinOverlay.getInstance().isOnlineMode();
            }

            @Override
            public ConnectionRequestBuilder createConnectionRequest(RegisteredServer server) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<GameProfile.Property> getGameProfileProperties() {
                return getGameProfile().getProperties();
            }

            @Override
            public void setGameProfileProperties(List<GameProfile.Property> properties) {
                throw new UnsupportedOperationException();
            }

            @Override
            public GameProfile getGameProfile() {
                return new GameProfile(uuid, name, Lists.newArrayList());
            }

            @Override
            public void clearHeaderAndFooter() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Component getPlayerListHeader() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Component getPlayerListFooter() {
                throw new UnsupportedOperationException();
            }

            @Override
            public TabList getTabList() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void disconnect(Component reason) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void spoofChatInput(String input) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendResourcePack(String url) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendResourcePack(String url, byte[] hash) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendResourcePackOffer(ResourcePackInfo packInfo) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @Nullable ResourcePackInfo getAppliedResourcePack() {
                return null;
            }

            @Override
            public @Nullable ResourcePackInfo getPendingResourcePack() {
                return null;
            }

            @Override
            public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
                return false;
            }

            @Override
            public @Nullable String getClientBrand() {
                return null;
            }

            @Override
            public Tristate getPermissionValue(String permission) {
                throw new UnsupportedOperationException();
            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return ((ProxyServer) SkinOverlay.getInstance().getSkinOverlay().getServerImpl()).getBoundAddress();
            }

            @Override
            public Optional<InetSocketAddress> getVirtualHost() {
                return Optional.empty();
            }

            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return ProtocolVersion.MINECRAFT_1_12_2;
            }

            @Override
            public @NotNull Identity identity() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public PlayerObjectVelocity(final Player player) {
        this.player = player;
    }

    @Override
    public Object getPlayer() {
        return player;
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public String playerName() {
        return player.getUsername();
    }

    @Override
    public void sendMessage(String input) {
        VelocityMinecraftUtils.msg(player, input);
    }

    @Override
    public void sendMessage(@NotNull List<String> input) {
        VelocityMinecraftUtils.msg(player, input);
    }

    @Override
    public void sendMessage(String @NotNull ... input) {
        VelocityMinecraftUtils.msg(player, input);
    }

    @Override
    public void sendMessage(String input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        VelocityMinecraftUtils.msg(player, input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(List<String> input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        VelocityMinecraftUtils.msg(player, input, placeholders, ignoreCase);
    }

    @Override
    public void sendMessage(String[] input, ObjectMap<String, String> placeholders, boolean ignoreCase) {
        VelocityMinecraftUtils.msg(player, input, placeholders, ignoreCase);
    }

    @Override
    public boolean isOnline() {
        return player.isActive();
    }
}
