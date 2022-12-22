package com.georgev22.skinoverlay.utilities.interfaces;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.utilities.player.User;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.georgev22.library.utilities.Utils.*;

public interface IDatabaseType {

    void save(User user) throws Exception;

    void load(User user, Callback<Boolean> callback) throws Exception;

    void setupUser(User user, Callback<Boolean> callback) throws Exception;

    default void reset(@NotNull User user) throws Exception {
//TODO RESET
        save(user);
    }

    void delete(User user) throws Exception;

    boolean playerExists(User user) throws Exception;

    ObjectMap<UUID, User> getAllUsers() throws Exception;

}