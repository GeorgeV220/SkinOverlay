package com.georgev22.skinoverlay.fabric.messaging;

import com.georgev22.skinoverlay.messaging.MessageData;
import com.georgev22.skinoverlay.messaging.MessageManager;
import com.georgev22.skinoverlay.storage.data.Skin;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public final class FabricPluginMessageManager extends MessageManager {

    private static boolean payloadsRegistered;

    private final MinecraftServer server;
    private BiConsumer<UUID, Skin> handler = (_, _) -> {
    };
    private boolean closed;

    public static synchronized void registerPayloads() {
        if (payloadsRegistered) {
            return;
        }
        PayloadTypeRegistry.serverboundPlay().register(ToBackendPayload.TYPE, ToBackendPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(FromBackendPayload.TYPE, FromBackendPayload.CODEC);
        payloadsRegistered = true;
    }

    public FabricPluginMessageManager(@NotNull MinecraftServer server) {
        this.server = server;
        if (!ServerPlayNetworking.registerGlobalReceiver(ToBackendPayload.TYPE, (payload, context) -> {
            if (!closed && context.server() == this.server) {
                receive(payload.data());
            }
        })) {
            throw new IllegalStateException("SkinOverlay plugin message receiver is already registered");
        }
    }

    private void receive(byte[] message) {
        try {
            MessageData data = readByteArray(message);
            if (!data.subChannel().equalsIgnoreCase("skinupdate")) {
                if (OptionsUtil.DEBUG.getBooleanValue()) {
                    mainPlugin.getLogger().warning("Unknown subchannel: " + data.subChannel());
                }
                return;
            }
            String[] entries = data.dataEntries();
            if (entries.length != 2) {
                mainPlugin.getLogger().severe("Invalid skin property message: expected two entries");
                return;
            }
            UUID uuid = UUID.fromString(entries[0]);
            Skin skin = Skin.fromBase64(entries[1]);
            handler.accept(uuid, skin);
        } catch (Exception exception) {
            mainPlugin.getLogger().log(Level.SEVERE, "Error handling skin property message", exception);
        }
    }

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        // Skin updates are published by the proxy.
    }

    @Override
    public void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler) {
        this.handler = handler;
    }

    @Override
    public void publishPlayerJoin(@NotNull UUID playerUUID) {
        server.execute(() -> {
            if (closed) {
                return;
            }
            ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
            if (player == null) {
                mainPlugin.getLogger().warning("Player " + playerUUID + " not found");
                return;
            }
            byte[] data = toByteArray("playerjoin", playerUUID.toString());
            ServerPlayNetworking.send(player, new FromBackendPayload(data));
            if (OptionsUtil.DEBUG.getBooleanValue()) {
                mainPlugin.getLogger().info("Sent player join message to " + CHANNEL_FROM_BACKEND
                        + ": " + playerUUID + " (" + data.length + " bytes)");
            }
        });
    }

    @Override
    public void subscribePlayerJoin(Consumer<UUID> handler) {
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        ServerPlayNetworking.unregisterGlobalReceiver(ToBackendPayload.TYPE.id());
        handler = (uuid, skin) -> {
        };
    }

    private static <T> StreamCodec<RegistryFriendlyByteBuf, T> rawCodec(
            Function<byte[], T> factory, Function<T, byte[]> data) {
        return StreamCodec.of((buffer, payload) -> buffer.writeBytes(data.apply(payload)), buffer -> {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            return factory.apply(bytes);
        });
    }

    private record ToBackendPayload(byte[] data) implements CustomPacketPayload {
        private static final Type<ToBackendPayload> TYPE = new Type<>(Identifier.parse(CHANNEL_TO_BACKEND));
        private static final StreamCodec<RegistryFriendlyByteBuf, ToBackendPayload> CODEC =
                rawCodec(ToBackendPayload::new, ToBackendPayload::data);

        @Override
        public @NonNull Type<ToBackendPayload> type() {
            return TYPE;
        }
    }

    private record FromBackendPayload(byte[] data) implements CustomPacketPayload {
        private static final Type<FromBackendPayload> TYPE = new Type<>(Identifier.parse(CHANNEL_FROM_BACKEND));
        private static final StreamCodec<RegistryFriendlyByteBuf, FromBackendPayload> CODEC =
                rawCodec(FromBackendPayload::new, FromBackendPayload::data);

        @Override
        public @NonNull Type<FromBackendPayload> type() {
            return TYPE;
        }
    }
}
