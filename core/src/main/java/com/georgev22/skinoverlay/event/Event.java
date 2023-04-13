package com.georgev22.skinoverlay.event;

import org.jetbrains.annotations.NotNull;

/**
 * An interface that represents an event.
 */
public abstract class Event {

    private String name;
    private final boolean async;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public Event() {
        this(false);
    }

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param isAsync true indicates the event will fire asynchronously, false
     *                by default from default constructor
     */
    public Event(boolean isAsync) {
        this.async = isAsync;
    }

    @NotNull
    public abstract HandlerList getHandlers();

    /**
     * Returns whether this event should be run asynchronously.
     *
     * @return {@code true} if this event should be run asynchronously, {@code false} otherwise
     */
    public final boolean isAsynchronous() {
        return async;
    }

    /**
     * Convenience method for providing a user-friendly identifier. By
     * default, it is the event's class's {@linkplain Class#getSimpleName()
     * simple name}.
     *
     * @return name of this event
     */
    public String getEventName() {
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }

}