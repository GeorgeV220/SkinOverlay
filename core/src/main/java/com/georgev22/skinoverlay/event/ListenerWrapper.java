package com.georgev22.skinoverlay.event;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A wrapper for event listeners, containing metadata about the listener and the method it should invoke when an event
 * occurs.
 */
public record ListenerWrapper(
        Class<?> aClass,
        EventListener listener,
        Method method,
        EventPriority eventPriority,
        boolean ignoreCancelled
) {
    /**
     * Creates a new listener wrapper instance.
     *
     * @param aClass          The class that this listener belongs to.
     * @param listener        The listener objects itself.
     * @param method          The method to invoke when an event occurs.
     * @param eventPriority   The priority of this listener relative to other listeners.
     * @param ignoreCancelled Whether this listener should ignore cancelled events.
     */
    public ListenerWrapper(
            Class<?> aClass,
            EventListener listener,
            Method method,
            EventPriority eventPriority,
            boolean ignoreCancelled
    ) {
        this.aClass = aClass;
        this.listener = listener;
        this.method = method;
        this.eventPriority = eventPriority;
        this.ignoreCancelled = ignoreCancelled;
        this.method.setAccessible(true);
    }

    /**
     * Invokes the wrapped listener's method with the given event.
     *
     * @param event The event to pass to the listener.
     * @throws InvocationTargetException If the method invocation throws an exception.
     * @throws IllegalAccessException    If the method is inaccessible.
     */
    public void callEvent(@NotNull final Event event) throws InvocationTargetException, IllegalAccessException {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled() && !ignoreCancelled()) {
                return;
            }
        }
        method.invoke(listener, event);
    }
}
