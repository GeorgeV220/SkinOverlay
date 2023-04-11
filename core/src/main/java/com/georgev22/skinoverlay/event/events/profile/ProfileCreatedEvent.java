package com.georgev22.skinoverlay.event.events.profile;

import com.georgev22.library.maps.UnmodifiableObjectMap;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.handler.SGameProfile;
import com.georgev22.skinoverlay.handler.SProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.UUID;

/**
 * An event that is fired when a new SGameProfile is created.
 */
public class ProfileCreatedEvent implements Event {

    private final String name;
    private final UUID uuid;
    private final UnmodifiableObjectMap<String, SProperty> properties;
    private final SGameProfile profile;
    private final boolean async;

    /**
     * Constructs a new ProfileCreatedEvent with the given SGameProfile and whether the event should be run asynchronously.
     *
     * @param sGameProfile the SGameProfile that was created
     * @param async        whether the event should be run asynchronously
     */
    public ProfileCreatedEvent(@NotNull SGameProfile sGameProfile, boolean async) {
        this.name = sGameProfile.getName();
        this.uuid = sGameProfile.getUUID();
        this.properties = sGameProfile.getProperties();
        this.profile = sGameProfile;
        this.async = async;
    }

    /**
     * Returns the name associated with the created profile.
     *
     * @return the name associated with the created profile
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the UUID associated with the created profile.
     *
     * @return the UUID associated with the created profile
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns an unmodifiable view of the properties associated with the created profile.
     *
     * @return an unmodifiable view of the properties associated with the created profile
     */
    @UnmodifiableView
    public UnmodifiableObjectMap<String, SProperty> getProperties() {
        return properties;
    }

    /**
     * Returns the SGameProfile that was created.
     *
     * @return the SGameProfile that was created
     */
    public SGameProfile getProfile() {
        return profile;
    }

    /**
     * Returns whether this event should be run asynchronously.
     *
     * @return {@code true} if this event should be run asynchronously, {@code false} otherwise
     */
    @Override
    public boolean runAsync() {
        return async;
    }
}
