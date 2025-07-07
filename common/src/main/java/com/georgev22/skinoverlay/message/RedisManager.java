package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.skin.SProperty;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.logging.Level;

public class RedisManager {
    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final String host;
    private final int port;
    private final String password;
    private final Jedis jedis;
    private Thread subscriberThread;
    private volatile boolean running = true;

    public RedisManager(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.jedis = new Jedis(host, port);
        if (password != null && !password.isEmpty()) {
            jedis.auth(password);
        }
    }

    public void publishSkinProperty(String channel, @NotNull UUID playerUUID, String value, String signature) {
        String message = playerUUID + "|" + value + "|" + signature;
        jedis.publish(channel, message);
    }

    public void subscribeSkinProperty(String channel, SkinPropertyHandler handler) {
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
                                String[] parts = message.split("\\|", 3);
                                if (parts.length != 3) {
                                    System.err.println("Invalid skin property message: " + message);
                                    return;
                                }
                                UUID uuid = UUID.fromString(parts[0]);
                                String value = parts[1];
                                String signature = parts[2];
                                SProperty property = new SProperty(value, signature);

//                                if (scheduler != null) {
//                                    scheduler.runTask(() -> handler.handle(uuid, property));
//                                } else {
//                                    handler.handle(uuid, property);
//                                }
                                skinOverlay.getScheduler().runTask(skinOverlay.getPlugin(), () -> handler.handle(uuid, property));
                            } catch (Exception e) {
                                skinOverlay.getLogger().log(Level.SEVERE, "Invalid skin property message: " + message, e);
                            }
                        }
                    };

                    subJedis.subscribe(jedisPubSub, channel);
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
        }, "RedisSubscriberThread");

        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    /**
     * Stop subscriber thread and close resources.
     */
    public void close() {
        running = false;
        if (subscriberThread != null) {
            subscriberThread.interrupt();
        }
        jedis.close();
    }

    public interface SkinPropertyHandler {
        void handle(UUID uuid, SProperty property);
    }
}
