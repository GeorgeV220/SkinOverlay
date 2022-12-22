package com.georgev22.skinoverlay.utilities.player;


import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.library.utilities.Utils.Callback;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.OptionsUtil;
import com.georgev22.skinoverlay.utilities.interfaces.IDatabaseType;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
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
                    Property property = skinOverlay.getSkinHandler().getGameProfile(new PlayerObject.PlayerObjectWrapper(user.getUniqueId(), skinOverlay.isBungee()).getPlayerObject()).getProperties().get("textures").iterator().next();
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
                userData.load(new Callback<Boolean>() {
                    @Override
                    public Boolean onSuccess() {
                        map.append(userData.user().getUniqueId(), userData.user());
                        return true;
                    }

                    @Contract(pure = true)
                    @Override
                    public @NotNull Boolean onFailure() {
                        return true;
                    }

                    @Override
                    public @NotNull Boolean onFailure(@NotNull Throwable throwable) {
                        throwable.printStackTrace();
                        return onFailure();
                    }
                });
            }
            return map;
        }
    }
}