package com.georgev22.skinoverlay.event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The EventBus manages event handlers and dispatches events to them
 * in priority order, supporting cancellation and monitor handlers.
 */
public class EventBus {

    /**
     * Internal wrapper to store handler and its priority.
     *
     * @param <T> the event type
     */
    private record HandlerWrapper<T extends Event>(EventHandler<T> handler, HandlerPriority priority) {
    }

    /**
     * Map storing handlers registered for each event class.
     */
    private final Map<Class<? extends Event>, List<HandlerWrapper<? extends Event>>> handlers = new ConcurrentHashMap<>();

    /**
     * Registers an event handler for a specific event class with a given priority.
     *
     * @param eventClass the event class
     * @param handler    the event handler
     * @param priority   the handler priority
     * @param <T>        the event type
     */
    public <T extends Event> void register(Class<T> eventClass, EventHandler<T> handler, HandlerPriority priority) {
        handlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(new HandlerWrapper<>(handler, priority));
        handlers.get(eventClass).sort(Comparator.comparing(h -> h.priority.ordinal()));
    }

    /**
     * Registers an event handler and returns a Registration for later unregistration.
     *
     * @param eventClass the event class
     * @param handler    the handler
     * @param priority   the priority
     * @param <T>        the event type
     * @return a Registration object to unregister this handler
     */
    public <T extends Event> Registration<T> registerWithReturn(Class<T> eventClass, EventHandler<T> handler, HandlerPriority priority) {
        register(eventClass, handler, priority);
        return new Registration<>(this, eventClass, handler);
    }

    /**
     * Unregisters an event handler from a specific event class.
     *
     * @param eventClass the event class
     * @param handler    the handler to remove
     * @param <T>        the event type
     */
    public <T extends Event> void unregister(Class<T> eventClass, EventHandler<T> handler) {
        List<HandlerWrapper<? extends Event>> list = handlers.get(eventClass);
        if (list != null) {
            list.removeIf(wrapper -> wrapper.handler.equals(handler));
        }
    }

    /**
     * Posts an event to all registered handlers.
     * Handlers are called in priority order from LOWEST to HIGHEST.
     * If the event implements {@link Cancellable} and is cancelled during propagation,
     * further handlers are skipped except MONITOR handlers.
     *
     * @param event the event instance to post
     * @param <T>   the event type
     */
    public <T extends Event> void post(@NotNull T event) {
        List<HandlerWrapper<? extends Event>> list = handlers.get(event.getClass());
        if (list != null) {
            for (HandlerWrapper<? extends Event> wrapper : list) {
                if (wrapper.priority == HandlerPriority.MONITOR) continue;

                @SuppressWarnings("unchecked")
                HandlerWrapper<T> w = (HandlerWrapper<T>) wrapper;
                w.handler.handle(event);

                if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
                    break;
                }
            }
            for (HandlerWrapper<? extends Event> wrapper : list) {
                if (wrapper.priority != HandlerPriority.MONITOR) continue;

                @SuppressWarnings("unchecked")
                HandlerWrapper<T> w = (HandlerWrapper<T>) wrapper;
                w.handler.handle(event);
            }
        }
    }


    /**
     * Represents a registration that can be unregistered.
     */
    public static class Registration<T extends Event> {
        private final EventBus bus;
        private final Class<T> eventClass;
        private final EventHandler<T> handler;

        private Registration(EventBus bus, Class<T> eventClass, EventHandler<T> handler) {
            this.bus = bus;
            this.eventClass = eventClass;
            this.handler = handler;
        }

        /**
         * Unregisters this handler from its event class.
         */
        public void unregister() {
            bus.unregister(eventClass, handler);
        }
    }
}
