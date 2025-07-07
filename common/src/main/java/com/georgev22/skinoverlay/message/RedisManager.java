package com.georgev22.skinoverlay.message;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.storage.data.Skin;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.logging.Level;

public class RedisManager implements MessageManager {
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

    @Override
    public void publishSkinProperty(@NotNull UUID playerUUID, @NotNull Skin skin) {
        String message = playerUUID + "|" + skin.toBase64();
        jedis.publish("skinoverlay:skinupdate", message);
    }

    @Override
    public void subscribeSkinProperty(@NotNull SkinPropertyHandler handler) {
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

                                skinOverlay.getScheduler().runTask(skinOverlay.getPlugin(), () -> handler.handle(uuid, skin));
                            } catch (Exception e) {
                                skinOverlay.getLogger().log(Level.SEVERE, "Invalid skin property message: " + message, e);
                            }
                        }
                    };

                    subJedis.subscribe(jedisPubSub, "skinoverlay:skinupdate");
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

    @Override
    public void close() {
        running = false;
        if (subscriberThread != null) {
            subscriberThread.interrupt();
        }
        jedis.close();
    }
}
