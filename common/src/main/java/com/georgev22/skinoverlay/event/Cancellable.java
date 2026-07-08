package com.georgev22.skinoverlay.event;

/**
 * Interface for events that can be cancelled to stop further propagation.
 */
public interface Cancellable {

    /**
     * Checks if this event is cancelled.
     *
     * @return true if cancelled, false otherwise
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of this event.
     *
     * @param cancelled true to cancel, false to allow propagation
     */
    void setCancelled(boolean cancelled);
}
