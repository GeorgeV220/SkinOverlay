package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class RedisManager extends MessageManager {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final String host;
    private final int port;
    private final String password;
    private final Jedis jedis;
    private Thread subscriberThread;
    private Thread publisherThread;
    private volatile boolean running = true;

    private BiConsumer<UUID, Skin> skinPropertyHandler = (uuid, skin) -> {
    };
    private Consumer<UUID> playerJoinHandler = uuid -> {
    };

    public RedisManager(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.jedis = new Jedis(host, port);
        if (password != null && !password.isEmpty()) {
            jedis.auth(password);
        }
    }

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        String message = playerUUID + "|" + skin.toBase64();
        jedis.publish(CHANNEL_TO_BACKEND, message);
    }

    @Override
    public void subscribeSkinProperty(@NotNull BiConsumer<UUID, Skin> handler) {
        this.skinPropertyHandler = handler;
        if (subscriberThread != null) {
            subscriberThread.interrupt();
            subscriberThread = null;
        }
        subscriberThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try (Jedis subJedis = new Jedis(host, port)) {
                    if (password != null && !password.isEmpty()) {
                        subJedis.auth(password);
                    }

                    JedisPubSub jedisPubSub = new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String message) {
                            try {
                                String[] parts = message.split("\\|", 2);
                                if (parts.length != 2) {
                                    System.err.println("Invalid skin property message: " + message);
                                    return;
                                }
                                UUID uuid = UUID.fromString(parts[0]);
                                String base64Skin = parts[1];
                                Skin skin = Skin.fromBase64(base64Skin);

                                skinOverlay.getScheduler().runTask(skinOverlay.getPlugin(), () -> {
                                    skinPropertyHandler.accept(uuid, skin);
                                });

                            } catch (Exception e) {
                                skinOverlay.getLogger().log(Level.SEVERE, "Invalid skin property message: " + message, e);
                            }
                        }
                    };

                    subJedis.subscribe(jedisPubSub, CHANNEL_TO_BACKEND);
                } catch (Exception e) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Redis subscribe connection lost, retrying in 5 seconds...", e);
                    try {
                        //noinspection BusyWait
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "SkinOverlayRedisSkinUpdateThread");

        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    @Override
    public void subscribePlayerJoin(Consumer<UUID> handler) {
        this.playerJoinHandler = handler;
        if (publisherThread != null) {
            publisherThread.interrupt();
            publisherThread = null;
        }
        publisherThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try (Jedis subJedis = new Jedis(host, port)) {
                    if (password != null && !password.isEmpty()) {
                        subJedis.auth(password);
                    }
                    subJedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String message) {
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(message);
                            } catch (Exception e) {
                                skinOverlay.getLogger().log(Level.SEVERE, "Error parsing UUID from redis message: " + message, e);
                                return;
                            }
                            playerJoinHandler.accept(uuid);
                        }
                    }, CHANNEL_FROM_BACKEND);
                } catch (Exception e) {
                    skinOverlay.getLogger().log(Level.SEVERE, "Redis subscribe connection lost, retrying in 5 seconds...", e);
                    try {
                        //noinspection BusyWait
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "SkinOverlayRedisPlayerJoinThread");
        publisherThread.setDaemon(true);
        publisherThread.start();
    }

    public void publishPlayerJoin(@NotNull UUID playerUUID) {
        jedis.publish(CHANNEL_FROM_BACKEND, playerUUID.toString());
    }

    @Override
    public void close() {
        running = false;
        if (subscriberThread != null) {
            subscriberThread.interrupt();
        }
        if (publisherThread != null) {
            publisherThread.interrupt();
        }
        jedis.close();
    }
}
