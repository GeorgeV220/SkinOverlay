package com.georgev22.skinoverlay.event.interfaces;

/**
 * An interface representing an event that can be cancelled.
 */
public interface Cancellable {

    /**
     * Cancels the event.
     *
     * @return {@code true} if the event was successfully cancelled, {@code false} otherwise
     */
    boolean cancel();

    /**
     * Returns whether the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    boolean isCancelled();

}