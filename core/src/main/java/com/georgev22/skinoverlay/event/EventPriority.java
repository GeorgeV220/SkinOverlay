package com.georgev22.skinoverlay.event;

/**
 * This enum represents the priority level of an event.
 */
public enum EventPriority {

    /**
     * Event call is of very low importance and should be run first, to allow
     * other plugins to further customise the outcome
     */
    LOW("low"),

    /**
     * Event call is neither important nor unimportant, and may be run
     * normally
     */
    NORMAL("normal"),

    /**
     * Event call is of high importance
     */
    HIGH("high"),

    /**
     * Event call is critical and must have the final say in what happens
     * to the event
     */
    HIGHEST("highest"),

    /**
     * The Event is listened to purely for monitoring the outcome of an event.
     * <p>
     * No modifications to the event should be made under this priority
     */
    MONITOR("monitor"),

    ;

    /**
     * The string representation of the priority level.
     */
    private final String priority;

    /**
     * Constructs an {@code EventPriority} with the specified priority string.
     *
     * @param priority the string representation of the priority level
     */
    EventPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Returns the string representation of the priority level.
     *
     * @return the string representation of the priority level
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Returns the {@code EventPriority} with the specified ordinal value.
     *
     * @param ordinal the ordinal value of the {@code EventPriority} to return
     * @return the {@code EventPriority} with the specified ordinal value
     * @throws IllegalArgumentException if the ordinal value is less than 0 or greater than or equal to the length of the {@code EventPriority} array
     */
    public static EventPriority getPriority(int ordinal) {
        if (ordinal < 0 || ordinal >= EventPriority.values().length) {
            throw new IllegalArgumentException("Invalid ordinal value: " + ordinal);
        }
        return EventPriority.values()[ordinal];
    }

    /**
     * Returns the string representation of this {@code EventPriority}.
     *
     * @return the string representation of this {@code EventPriority}
     */
    @Override
    public String toString() {
        return super.toString();
    }
}