package com.georgev22.skinoverlay.event;

import com.georgev22.skinoverlay.event.interfaces.Cancellable;
import com.georgev22.skinoverlay.event.interfaces.EventListener;
import com.georgev22.skinoverlay.exceptions.EventException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

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
     */
    public void callEvent(@NotNull final Event event) {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled() && !ignoreCancelled()) {
                return;
            }
        }
        (event.isAsynchronous() ? new AsyncExecutor() : new SyncExecutor()).execute(() -> {
            try {
                method.invoke(listener, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new EventException("Event " + event.getEventName() + " failed to fire due to an error", e);
            }
        });
    }

    @Override
    public String toString() {
        return "ListenerWrapper{" +
                "EventManager caller=" + aClass.getSimpleName() +
                ", listener=" + listener.getClass().getSimpleName() +
                ", method=" + method.getName() +
                ", eventPriority=" + eventPriority.name() + " (slot: " + eventPriority.getValue() + ")" +
                ", ignoreCancelled=" + ignoreCancelled +
                '}';
    }

    private static class AsyncExecutor implements Executor {

        /**
         * Executes the given command at some time in the future.
         * The command
         * may execute in a new thread, in a pooled thread, or in the calling
         * thread, at the discretion of the {@code Executor} implementation.
         *
         * @param runnable the runnable task
         * @throws java.util.concurrent.RejectedExecutionException if this task cannot be
         *                                                         accepted for execution
         * @throws NullPointerException                            if command is null
         */
        @Override
        public void execute(@NotNull Runnable runnable) {
            new Thread(runnable).start();
        }
    }

    private static class SyncExecutor implements Executor {

        /**
         * Executes the given command at some time in the future.
         * The command
         * may execute in a new thread, in a pooled thread, or in the calling
         * thread, at the discretion of the {@code Executor} implementation.
         *
         * @param runnable the runnable task
         * @throws java.util.concurrent.RejectedExecutionException if this task cannot be
         *                                                         accepted for execution
         * @throws NullPointerException                            if command is null
         */
        @Override
        public void execute(@NotNull Runnable runnable) {
            runnable.run();
        }
    }
}
