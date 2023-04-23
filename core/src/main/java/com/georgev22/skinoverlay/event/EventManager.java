package com.georgev22.skinoverlay.event;

import com.georgev22.library.maps.HashObjectMap;
import com.georgev22.library.maps.ObjectMap;
import com.georgev22.skinoverlay.event.annotations.EventHandler;
import com.georgev22.skinoverlay.event.interfaces.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class that manages events and listeners.
 */
public class EventManager {

    /**
     * The logger used for this event manager.
     */
    private final Logger logger;

    /**
     * The class that owns this event manager.
     */
    private final Class<?> clazz;

    /**
     * Constructs a new EventManager with the given logger and class.
     *
     * @param logger the logger to use
     * @param clazz  the class that owns this event manager
     */
    public EventManager(Logger logger, Class<?> clazz) {
        this.logger = logger;
        this.clazz = clazz;
    }

    /**
     * Registers one or more listeners to this event manager.
     *
     * @param listeners the listeners to register
     */
    public void register(EventListener @NotNull ... listeners) {
        for (EventListener listener : listeners) {
            for (Map.Entry<Class<? extends Event>, Set<ListenerWrapper>> entry : createRegisteredListeners(listener).entrySet()) {
                getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
            }
        }
    }

    /**
     * Unregisters one or more listeners from this event manager.
     *
     * @param listeners the listeners to unregister
     */
    public void unregister(EventListener @NotNull ... listeners) {
        for (EventListener listener : listeners) {
            HandlerList.unregisterAll(listener.getClass());
        }
    }

    /**
     * Calls the given event either synchronously or asynchronously,
     * depending on the event's {@link Event#isAsynchronous()}
     * value.
     * Asynchronous event handling is recommended for long-running or potentially blocking tasks, as it allows the
     * main thread to continue processing other tasks while the event is being handled.
     * Synchronous event handling is recommended
     * for quick, lightweight tasks that can be handled quickly without blocking the main thread.
     *
     * @param event the event to call
     */
    public Event callEvent(@NotNull Event event) {
        for (ListenerWrapper listenerWrapper : event.getHandlers().getListenerWrappers()) {
            listenerWrapper.callEvent(event);
        }
        return event;
    }

    /**
     * Creates a map of event classes to listener wrappers for the given listener.
     *
     * @param listener the listener to create the map for
     * @return the map of event classes to listener wrappers
     */
    @NotNull
    public ObjectMap<Class<? extends Event>, Set<ListenerWrapper>> createRegisteredListeners(@NotNull EventListener listener) {
        ObjectMap<Class<? extends Event>, Set<ListenerWrapper>> ret = new HashObjectMap<>();
        List<Method> methods;
        try {
            methods = new ArrayList<>(Arrays.asList(listener.getClass().getDeclaredMethods()));
        } catch (NoClassDefFoundError e) {
            logger.severe("Failed to register events for " + listener.getClass() + " in EventManager of " + clazz.getName() + ". The required class or resource (" + e.getMessage() + ") could not be found.");
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                logger.severe("Failed to register events for " + listener + " in EventManager of " + clazz.getName() + ". The method " + method.getName() + " must have a single parameter of type Event.");
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<ListenerWrapper> eventSet = ret.computeIfAbsent(eventClass, k -> new HashSet<>());

            eventSet.add(new ListenerWrapper(clazz, listener, method, eh.priority(), eh.ignoreCancelled()));

        }
        return ret;
    }

    /**
     * Gets the handler list for the given event class.
     *
     * @param type the event class to get the handler list for
     * @return the handler list for the given event class
     */
    @NotNull
    public HandlerList getEventListeners(@NotNull Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the registration class for the given event class.
     *
     * @param clazz the event class to get the registration class for
     * @return the registration class for the given event class
     */
    @NotNull
    public Class<? extends Event> getRegistrationClass(@NotNull Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new RuntimeException("Unable to locate the handler list for event " + clazz.getName() + ". Please ensure that a static getHandlerList method is defined.");
            }
        }
    }
}