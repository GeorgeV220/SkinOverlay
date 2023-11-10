package com.georgev22.skinoverlay.event.events.profile.property;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.handler.SProperty;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is fired when a SProperty is removed.
 */
public class SPropertyRemoveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String propertyName;
    private final SProperty property;
    private boolean cancelled = false;

    /**
     * Constructs a new SPropertyRemoveEvent with the given property name, SProperty, and whether the event should be run asynchronously.
     *
     * @param propertyName the name of the property that was removed
     * @param property     the SProperty that was removed
     * @param async        whether the event should be run asynchronously
     */
    public SPropertyRemoveEvent(String propertyName, SProperty property, boolean async) {
        super(async);
        this.propertyName = propertyName;
        this.property = property;
    }

    /**
     * Returns the name of the property that was removed.
     *
     * @return the name of the property that was removed
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the SProperty that was removed.
     *
     * @return the SProperty that was removed
     */
    public SProperty getProperty() {
        return property;
    }

    /**
     * Cancels the event.
     *
     * @return {@code true} if the event was successfully cancelled, {@code false} otherwise
     */
    @Override
    public boolean cancel() {
        return cancelled = true;
    }

    /**
     * Returns whether the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
