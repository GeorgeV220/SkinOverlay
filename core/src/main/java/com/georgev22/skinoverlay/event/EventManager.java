package com.georgev22.skinoverlay.event;

import com.georgev22.library.maps.ObjectMap;
import com.georgev22.library.maps.ObservableObjectMap;
import com.georgev22.library.scheduler.SchedulerManager;
import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.utilities.Utilities;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The EventManager class is responsible for managing listeners and events.
 */
public class EventManager {

    /**
     * A map that stores the event listeners by their class name.
     */
    private final ObservableObjectMap<String, EventListener> eventListenerObservableObjectMap = new ObservableObjectMap<>();

    /**
     * A map that stores the event listener methods along with their priority.
     */
    private final ObservableObjectMap<ObjectMap.Pair<EventListener, Method>, EventPriority> priorityObservableObjectMap = new ObservableObjectMap<>();

    /**
     * Registers one or more event listeners.
     *
     * @param listeners The event listeners to register.
     */
    public void registerListeners(EventListener @NotNull ... listeners) {
        for (EventListener listener : listeners) {
            eventListenerObservableObjectMap.append(listener.getClass().getSimpleName(), listener);
        }
    }

    /**
     * Fires an event to all registered listeners.
     *
     * @param event The event to be fired.
     */
    public void fireEvent(@NotNull Event event) {
        if (event.runAsync()) {
            SchedulerManager.getScheduler().runTaskAsynchronously(SkinOverlay.getInstance().getClass(), () -> fireEvent0(event));
        } else {
            fireEvent0(event);
        }
    }

    /**
     * Helper method that is called by the fireEvent method to actually fire the event.
     *
     * @param event The event to be fired.
     */
    private void fireEvent0(Event event) {
        eventListenerObservableObjectMap.forEach((s, listener) -> {
            Class<?> listenerClass = listener.getClass();
            Utilities.getMethodsAnnotatedWith(listenerClass, Handler.class)
                    .stream()
                    .filter(method -> method.getParameterTypes()[0] == event.getClass())
                    .forEach(method -> {
                        Handler handler = method.getAnnotation(Handler.class);
                        int priority = handler.priority().ordinal();

                        priorityObservableObjectMap.append(ObjectMap.Pair.create(listener, method), EventPriority.getPriority(priority));
                    });
        });

        priorityObservableObjectMap.entrySet().stream()
                .filter(entry -> entry.getValue().ordinal() == EventPriority.LOW.ordinal())
                .filter(entry -> entry.getKey().value().getParameterTypes()[0] == event.getClass())
                .forEach(entry -> invoke(entry.getKey().value(), entry.getKey().key(), event));

        priorityObservableObjectMap.entrySet().stream()
                .filter(entry -> entry.getValue().ordinal() == EventPriority.NORMAL.ordinal())
                .filter(entry -> entry.getKey().value().getParameterTypes()[0] == event.getClass())
                .forEach(entry -> invoke(entry.getKey().value(), entry.getKey().key(), event));

        priorityObservableObjectMap.entrySet().stream()
                .filter(entry -> entry.getValue().ordinal() == EventPriority.HIGH.ordinal())
                .filter(entry -> entry.getKey().value().getParameterTypes()[0] == event.getClass())
                .forEach(entry -> invoke(entry.getKey().value(), entry.getKey().key(), event));

        priorityObservableObjectMap.entrySet().stream()
                .filter(entry -> entry.getValue().ordinal() == EventPriority.HIGHEST.ordinal())
                .filter(entry -> entry.getKey().value().getParameterTypes()[0] == event.getClass())
                .forEach(entry -> invoke(entry.getKey().value(), entry.getKey().key(), event));
    }

    /**
     * Invokes the given listener method with the given event.
     *
     * @param method   The method to be invoked.
     * @param listener The listener instance on which the method will be invoked.
     * @param event    The event to be passed as an argument to the method.
     */
    private void invoke(@NotNull Method method, EventListener listener, Event event) {
        try {
            method.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the map of registered event listeners.
     *
     * @return The map of registered event listeners.
     */
    public ObservableObjectMap<String, EventListener> getEventListeners() {
        return eventListenerObservableObjectMap;
    }
}
