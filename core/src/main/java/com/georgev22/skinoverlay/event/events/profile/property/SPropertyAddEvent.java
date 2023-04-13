package com.georgev22.skinoverlay.event.events.profile.property;

import com.georgev22.skinoverlay.event.Cancellable;
import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.HandlerList;
import com.georgev22.skinoverlay.handler.SProperty;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is fired when a SProperty is added.
 */
public class SPropertyAddEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String propertyName;
    private SProperty property;
    private boolean cancelled;

    /**
     * Constructs a new SPropertyAddEvent with the given property name, SProperty, and whether the event should be run asynchronously.
     *
     * @param propertyName the name of the property that was added
     * @param property     the SProperty that was added
     * @param async        whether the event should be run asynchronously
     */
    public SPropertyAddEvent(String propertyName, SProperty property, boolean async) {
        super(async);
        this.propertyName = propertyName;
        this.property = property;
    }

    /**
     * Returns the name of the property that was added.
     *
     * @return the name of the property that was added
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the SProperty that was added.
     *
     * @return the SProperty that was added
     */
    public SProperty getProperty() {
        return property;
    }

    /**
     * Sets the name of the property that was added.
     *
     * @param propertyName the new name of the property that was added
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Sets the SProperty that was added.
     *
     * @param property the new SProperty that was added
     */
    public void setProperty(SProperty property) {
        this.property = property;
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
