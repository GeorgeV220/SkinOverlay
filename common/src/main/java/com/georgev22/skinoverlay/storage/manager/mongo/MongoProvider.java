package com.georgev22.skinoverlay.storage.manager.mongo;

import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.jspecify.annotations.NonNull;

public final class MongoProvider {
    private static MongoClient client;

    public static @NonNull MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(
                    OptionsUtil.DATABASE_MONGO_HOST.getStringValue() + ":" +
                            OptionsUtil.DATABASE_MONGO_PORT.getIntValue()
            );
        }
        return client.getDatabase(OptionsUtil.DATABASE_MONGO_DATABASE.getStringValue());
    }

    public static void shutdown() {
        if (client != null) {
            client.close();
        }
    }
}
