package com.georgev22.skinoverlay.event;

/**
 * An interface that represents an event.
 */
public interface Event {

    /**
     * Returns whether this event should be run asynchronously.
     *
     * @return {@code true} if this event should be run asynchronously, {@code false} otherwise
     */
    boolean runAsync();

}