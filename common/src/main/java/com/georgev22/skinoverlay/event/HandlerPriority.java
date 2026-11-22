package com.georgev22.skinoverlay.event;

/**
 * Defines the priority of event handlers.
 * Handlers are executed from LOWEST to HIGHEST, then MONITOR runs last regardless of cancellation.
 */
public enum HandlerPriority {
    /**
     * Lowest priority, runs first.
     */
    LOWEST,

    /**
     * Low priority, runs after LOWEST.
     */
    LOW,

    /**
     * Normal priority, runs after LOW.
     */
    NORMAL,

    /**
     * High priority, runs after NORMAL.
     */
    HIGH,

    /**
     * Highest priority, runs after HIGH.
     */
    HIGHEST,

    /**
     * Monitor priority, always runs last regardless of cancellation.
     * Use for logging or observing without modifying the event flow.
     */
    MONITOR
}
