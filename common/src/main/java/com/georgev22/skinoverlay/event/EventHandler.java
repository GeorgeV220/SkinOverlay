package com.georgev22.skinoverlay.event;

/**
 * Functional interface for handling events of type T.
 *
 * @param <T> the event type
 */
@FunctionalInterface
public interface EventHandler<T extends Event> {

    /**
     * Handles the given event.
     *
     * @param event the event to handle
     */
    void handle(T event);
}
