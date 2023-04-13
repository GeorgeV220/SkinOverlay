package com.georgev22.skinoverlay.event;

/**
 * Represents the priority level of an event listener.
 * Listeners with lower priority will be called before those with higher priority.
 * The execution order of listeners with the same priority is undefined.
 * <p>
 * It is not recommended to make data changes in listeners with priority {@link #LOWEST}.
 * Data changes should be made in listeners with priority {@link #NORMAL} or {@link #HIGHEST}.
 * <p>
 * The recommended priority levels for most listeners are {@link #NORMAL} and {@link #HIGHEST}.
 */
public enum EventPriority {
    /**
     * Lowest priority. Executed first.
     * Not recommended for data changes.
     */
    LOWEST(-1000),

    /**
     * Low priority.
     */
    LOW(-500),

    /**
     * Normal priority.
     * Recommended for most listeners.
     */
    NORMAL(0),

    /**
     * High priority.
     * Recommended for data changes.
     */
    HIGH(500),

    /**
     * Highest priority. Executed last.
     * Recommended for data changes.
     */
    HIGHEST(1000);

    private final int value;

    /**
     * Constructs an {@code EventPriority} with the given value.
     *
     * @param value the priority value
     */
    EventPriority(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value of this priority level.
     *
     * @return the integer value of this priority level
     */
    public int getValue() {
        return value;
    }
}
