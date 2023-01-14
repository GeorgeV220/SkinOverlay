package com.georgev22.skinoverlay.utilities.player;


import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils.Callback;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.IDatabaseType;
import com.mojang.authlib.properties.Property;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to handle all user's data and anything related to them.
 */
public record UserData(User user) {

    private static final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    private static final ObjectMap<UUID, User> allUsersMap = new ConcurrentObjectMap<>();

    public UserData(@NotNull User user) {
        if (!allUsersMap.containsKey(user.getUniqueId())) {
            allUsersMap.append(user.getUniqueId(), new User(user.getUniqueId()));
        }
        this.user = allUsersMap.get(user.getUniqueId());

    }

    /**
     * Returns all the players in a map
     *
     * @return all the players
     */
    public static ObjectMap<UUID, User> getAllUsersMap() {
        return allUsersMap;
    }

    /**
     * Load all users
     *
     * @throws Exception When something goes wrong
     */
    public static void loadAllUsers() throws Exception {
        allUsersMap.putAll(skinOverlay.getIDatabaseType().getAllUsers());
    }

    /**
     * Returns a copy of this UserData class for a specific user.
     *
     * @param playerObject PlayerObject object
     * @return a copy of this UserData class for a specific user.
     */
    @Contract("_ -> new")
    public static @NotNull UserData getUser(@NotNull PlayerObject playerObject) {
        return getUser(playerObject.playerUUID());
    }

    /**
     * Returns a copy of this UserData class for a specific user.
     *
     * @param uuid Player's Unique identifier
     * @return a copy of this UserData class for a specific user.
     */
    @Contract("_ -> new")
    public static @NotNull UserData getUser(@NotNull UUID uuid) {
        return new UserData(new User(uuid));
    }

    public String getSkinName() {
        return user().getSkinName();
    }

    public Property getSkinProperty() {
        return user.getSkinProperty();
    }

    public Property getDefaultSkinProperty() {
        return user.getDefaultSkinProperty();
    }

    public static SkinOverlay getSkinOverlay() {
        return skinOverlay;
    }

    public UserData setSkinName(String skinName) {
        user.append("skinName", skinName);
        return this;
    }

    public UserData setProperty(Property property) {
        user.append("skinProperty", property);
        return this;
    }

    public UserData setDefaultSkinProperty(Property property) {
        user.append("defaultSkinProperty", property);
        return this;
    }

    /**
     * Load user data
     *
     * @param callback Callback
     * @throws Exception When something goes wrong
     */
    public UserData load(Callback<Boolean> callback) throws Exception {
        skinOverlay.getIDatabaseType().load(user, callback);
        return this;
    }

    /**
     * Save all user's data
     *
     * @param async True if you want to save async
     */
    public UserData save(boolean async, Callback<Boolean> callback) {
        if (async) {
            SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> save(callback));
        } else {
            save(callback);
        }
        return this;
    }

    private void save(Callback<Boolean> callback) {
        try {
            skinOverlay.getIDatabaseType().save(user);
            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getCause());
        }
    }

    /**
     * Reset user's stats
     */
    public UserData reset() {
        SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
            try {
                skinOverlay.getIDatabaseType().reset(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    /**
     * Delete user from database
     */
    public UserData delete() {
        SchedulerManager.getScheduler().runTaskAsynchronously(skinOverlay.getClass(), () -> {
            try {
                skinOverlay.getIDatabaseType().delete(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    public static class SQLUserUtils implements IDatabaseType {
        @Override
        public void save(@NotNull User user) throws SQLException, ClassNotFoundException {
            skinOverlay.getDatabaseWrapper().getSQLDatabase().updateSQL(
                    "UPDATE `" +
                            OptionsUtil.DATABASE_TABLE_NAME.getStringValue() +
                            "` SET `property-name` = '" + user.getSkinProperty().getName() +
                            "', `property-value` = '" + user.getSkinProperty().getValue() +
                            "', `property-signature` = '" + user.getSkinProperty().getSignature() +
                            "', `skinName` = '" + user.getSkinName() +
                            "' WHERE `uuid` = '" + user.getUniqueId() + "'");
        }

        @Override
        public void delete(@NotNull User user) throws SQLException, ClassNotFoundException {
            skinOverlay.getDatabaseWrapper().getSQLDatabase().updateSQL("DELETE FROM `" + OptionsUtil.DATABASE_TABLE_NAME.getStringValue() + "` WHERE `uuid` = '" + user.getUniqueId().toString() + "';");
            allUsersMap.remove(user.getUniqueId());
        }

        @Override
        public void load(final User user, final Callback<Boolean> callback) {
            this.setupUser(user, new Callback<>() {

                public Boolean onSuccess() {
                    try {
                        ResultSet resultSet = skinOverlay.getDatabaseWrapper().getSQLDatabase().querySQL("SELECT * FROM `" + OptionsUtil.DATABASE_TABLE_NAME.getStringValue() + "` WHERE `uuid` = '" + user.getUniqueId().toString() + "'");
                        while (resultSet.next()) {
                            user
                                    .append("skinName", resultSet.getString("skinName"))
                                    .append("skinProperty",
                                            new Property(
                                                    resultSet.getString("property-name"),
                                                    resultSet.getString("property-value"),
                                                    resultSet.getString("property-signature")
                                            )
                                    );
                        }
                        return callback.onSuccess();
                    } catch (ClassNotFoundException | SQLException throwable) {
                        return callback.onFailure(throwable.getCause());
                    }
                }

                public Boolean onFailure() {
                    return false;
                }

                public Boolean onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                    return this.onFailure();
                }
            });
        }

        @Override
        public boolean playerExists(@NotNull User user) throws SQLException, ClassNotFoundException {
            return skinOverlay.getDatabaseWrapper().getSQLDatabase().querySQL("SELECT * FROM " + OptionsUtil.DATABASE_TABLE_NAME.getStringValue() + " WHERE `uuid` = '" + user.getUniqueId().toString() + "'").next();
        }

        @Override
        public void setupUser(User user, Callback<Boolean> callback) {
            try {
                if (!this.playerExists(user)) {
                    Property property = skinOverlay.getSkinHandler().getGameProfile(new PlayerObjectWrapper(user.getUniqueId(), skinOverlay.type())).getProperties().get("textures").iterator().next();
                    skinOverlay.getDatabaseWrapper().getSQLDatabase().updateSQL("INSERT INTO `" + OptionsUtil.DATABASE_TABLE_NAME.getStringValue() + "` (`uuid`, `skinName`, `property-name`, `property-value`, `property-signature`) VALUES ('" + user.getUniqueId().toString() + "', 'default', '" + property.getName() + "', '" + property.getValue() + "', '" + property.getSignature() + "');");
                }
                callback.onSuccess();
            } catch (ClassNotFoundException | SQLException | IOException throwables) {
                callback.onFailure(throwables.getCause());
            }
        }

        /**
         * Get all users from the database
         *
         * @return all the users from the database
         * @throws SQLException           When something goes wrong
         * @throws ClassNotFoundException When the class is not found
         */
        public ObjectMap<UUID, User> getAllUsers() throws Exception {
            ObjectMap<UUID, User> map = new ConcurrentObjectMap<>();
            ResultSet resultSet = skinOverlay.getDatabaseWrapper().getSQLDatabase().querySQL("SELECT * FROM `" + OptionsUtil.DATABASE_TABLE_NAME.getStringValue() + "`");
            while (resultSet.next()) {
                UserData userData = UserData.getUser(UUID.fromString(resultSet.getString("uuid")));
                load0(map, userData);
            }
            return map;
        }
    }

    /**
     * All Mongo Utils for the user
     * Everything here must run asynchronously
     * Expect shits to happen
     */
    public static class MongoDBUtils implements IDatabaseType {

        /**
         * Save all user's data
         */
        public void save(@NotNull User user) {
            BasicDBObject query = new BasicDBObject();
            query.append("uuid", user.getUniqueId().toString());

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.append("$set", new BasicDBObject()
                    .append("uuid", user.getUniqueId().toString())
                    .append("property-name", user.getSkinProperty().getName())
                    .append("property-value", user.getSkinProperty().getValue())
                    .append("property-signature", user.getSkinProperty().getSignature())
                    .append("skinName", user.getSkinName())
            );

            skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).updateOne(query, updateObject);
        }

        /**
         * Load user data
         *
         * @param user     User
         * @param callback Callback
         */
        public void load(User user, Callback<Boolean> callback) {
            setupUser(user, new Callback<>() {
                @Override
                public Boolean onSuccess() {
                    BasicDBObject searchQuery = new BasicDBObject();
                    searchQuery.append("uuid", user.getUniqueId().toString());
                    FindIterable<Document> findIterable = skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).find(searchQuery);
                    Document document = findIterable.first();
                    if (document == null) {
                        return callback.onFailure(new Throwable("Document is null!!"));
                    }
                    user
                            .append("skinProperty", new Property(document.getString("property-name"), document.getString("property-value"), document.getString("property-signature")))
                            .append("skinName", document.getString("skinName"));
                    return callback.onSuccess();
                }

                @Contract(pure = true)
                @Override
                public @NotNull Boolean onFailure() {
                    return false;
                }

                @Override
                public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                    callback.onFailure(throwable.getCause());
                    return onFailure();
                }
            });
        }

        /**
         * Set up the user
         *
         * @param user     User object
         * @param callback Callback
         */
        public void setupUser(User user, Callback<Boolean> callback) {
            if (!playerExists(user)) {
                skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).insertOne(new Document()
                        .append("uuid", user.getUniqueId().toString())
                        .append("property-name", user.getDefaultSkinProperty().getName())
                        .append("property-value", user.getDefaultSkinProperty().getValue())
                        .append("property-signature", user.getDefaultSkinProperty().getSignature())
                        .append("skinName", user.getSkinName())
                );
            }
            callback.onSuccess();
        }

        /**
         * Check if the user exists
         *
         * @return true if user exists or false when is not
         */
        public boolean playerExists(@NotNull User user) {
            long count = skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).countDocuments(new BsonDocument("uuid", new BsonString(user.getUniqueId().toString())));
            return count > 0;
        }

        /**
         * Remove user's data from database.
         */
        public void delete(@NotNull User user) {
            BasicDBObject theQuery = new BasicDBObject();
            theQuery.put("uuid", user.getUniqueId().toString());
            DeleteResult result = skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).deleteMany(theQuery);
            if (result.getDeletedCount() > 0) {
                allUsersMap.remove(user.getUniqueId());
            }
        }


        /**
         * Get all users from the database
         *
         * @return all the users from the database
         */
        public ObjectMap<UUID, User> getAllUsers() {
            ObjectMap<UUID, User> map = new ConcurrentObjectMap<>();
            FindIterable<Document> iterable = skinOverlay.getMongoDatabase().getCollection(OptionsUtil.DATABASE_MONGO_COLLECTION.getStringValue()).find();
            iterable.forEach((Block<Document>) document -> {
                UserData userData = UserData.getUser(UUID.fromString(document.getString("uuid")));
                try {
                    load0(map, userData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return map;
        }

    }

    private static void load0(ObjectMap<UUID, User> map, @NotNull UserData userData) throws Exception {
        userData.load(new Callback<>() {
            @Override
            public Boolean onSuccess() {
                map.append(userData.user().getUniqueId(), userData.user());
                return true;
            }

            @Contract(pure = true)
            @Override
            public @NotNull Boolean onFailure() {
                return false;
            }

            @Override
            public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                throwable.printStackTrace();
                return onFailure();
            }
        });
    }

    public static class Cache implements IDatabaseType {

        private final List<User> userList = new ArrayList<>();

        @Override
        public void save(User user) throws Exception {

        }

        @Override
        public void load(User user, Callback<Boolean> callback) throws Exception {
            setupUser(user, new Callback<>() {
                @Override
                public Boolean onSuccess() {
                    return userList.add(user);
                }

                @Override
                public Boolean onFailure() {
                    return false;
                }

                @Override
                public Boolean onFailure(Throwable throwable) {
                    return super.onFailure(throwable);
                }
            });
        }

        @Override
        public void setupUser(User user, Callback<Boolean> callback) {
            if (!playerExists(user)) {
                userList.add(user);
                callback.onSuccess();
            }
        }

        @Override
        public void delete(User user) {
            userList.remove(user);
        }

        @Override
        public boolean playerExists(User user) {
            return userList.contains(user);
        }

        @Override
        public ObjectMap<UUID, User> getAllUsers() {
            ObjectMap<UUID, User> map = new ConcurrentObjectMap<>();
            for (User user : userList) {
                map.append(user.getUniqueId(), user);
            }
            return map;
        }
    }


}