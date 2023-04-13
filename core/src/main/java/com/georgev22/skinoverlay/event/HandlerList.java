package com.georgev22.skinoverlay.event;

import com.georgev22.library.maps.ConcurrentObjectMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.EventListener;

/**
 * This class represents a list of event handlers, sorted by priority.
 * <p>
 * It provides methods for registering and unregistering listeners, as well as
 * for retrieving a list of all registered listeners.
 */
public class HandlerList {

    /**
     * An array of ListenerWrapper objects that represents the registered listeners.
     * This array is sorted by priority, with higher-priority listeners appearing
     * first in the array.
     */
    private volatile ListenerWrapper[] handlers = null;

    /**
     * A map that associates each event priority with a list of listeners that
     * have that priority.
     */
    private final EnumMap<EventPriority, ArrayList<ListenerWrapper>> handlerSlots;

    /**
     * A static list of all HandlerList objects.
     */
    private static final ArrayList<HandlerList> allLists = new ArrayList<>();

    /**
     * A set of all event types for which there is at least one registered listener.
     */
    private static final Set<String> EVENT_TYPES = ConcurrentObjectMap.newKeySet();

    /**
     * Bakes the handler array for all registered HandlerList objects. This method
     * iterates over all HandlerList objects stored in the allLists list and calls the
     * bake method on each one in a synchronized block.
     * <p>This method should be called after all listeners have been registered to ensure
     * that the handler array is up-to-date and can be used for efficient event dispatching.
     */
    public static void bakeAll() {
        synchronized (allLists) {
            for (HandlerList handlerList : allLists) {
                handlerList.bake();
            }
        }
    }

    /**
     * Unregisters all ListenerWrapper objects that are equal to the specified
     * object from all HandlerList objects.
     */
    public static void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList handlerList : allLists) {
                synchronized (handlerList) {
                    for (List<ListenerWrapper> list : handlerList.handlerSlots.values()) {
                        list.clear();
                    }
                    handlerList.handlers = null;
                }
            }
        }
    }

    /**
     * Unregisters all ListenerWrapper objects that are equal to the specified
     * object from all HandlerList objects.
     *
     * @param clazz the Class object to unregister
     */
    public static void unregisterAll(@NotNull Class<?> clazz) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(clazz);
            }
        }
    }

    /**
     * Unregisters all ListenerWrapper objects that are equal to the specified
     * object from all HandlerList objects.
     *
     * @param listener the ListenerWrapper object to unregister
     */
    public static void unregisterAll(@NotNull EventListener listener) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(listener);
            }
        }
    }

    /**
     * Constructor that initializes the HandlerList object.
     */
    public HandlerList() {
        StackWalker.getInstance(EnumSet.of(StackWalker.Option.RETAIN_CLASS_REFERENCE), 4)
                .walk(s -> s.filter(f -> Event.class.isAssignableFrom(f.getDeclaringClass())).findFirst())
                .map(f -> f.getDeclaringClass().getName())
                .ifPresent(EVENT_TYPES::add);
        handlerSlots = new EnumMap<>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerSlots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Registers a ListenerWrapper object with this HandlerList object.
     *
     * @param listener the ListenerWrapper object to register
     * @throws IllegalStateException if the listener is already registered to
     *                               the specified priority
     */
    public synchronized void register(@NotNull ListenerWrapper listener) {
        if (handlerSlots.get(listener.eventPriority()).contains(listener))
            throw new IllegalStateException("This listener is already registered to priority " + listener.eventPriority().toString());
        handlers = null;
        handlerSlots.get(listener.eventPriority()).add(listener);
    }

    /**
     * Registers all ListenerWrapper objects in the specified collection with
     * this HandlerList object.
     *
     * @param listeners a collection of ListenerWrapper objects to register
     */
    public void registerAll(@NotNull Collection<ListenerWrapper> listeners) {
        for (ListenerWrapper listener : listeners) {
            register(listener);
        }
    }

    /**
     * Unregisters a ListenerWrapper object from this HandlerList object.
     *
     * @param listener the ListenerWrapper object to unregister
     */
    public synchronized void unregister(@NotNull ListenerWrapper listener) {
        if (handlerSlots.get(listener.eventPriority()).remove(listener)) {
            handlers = null;
        }
    }

    /**
     * Unregisters all ListenerWrapper objects whose associated event type is
     * equal to the specified class from all HandlerList objects.
     *
     * @param clazz the class that represents the event type
     */
    public synchronized void unregister(@NotNull Class<?> clazz) {
        boolean changed = false;
        for (List<ListenerWrapper> list : handlerSlots.values()) {
            for (ListIterator<ListenerWrapper> listenerWrapperListIterator = list.listIterator(); listenerWrapperListIterator.hasNext(); ) {
                if (listenerWrapperListIterator.next().aClass().equals(clazz)) {
                    listenerWrapperListIterator.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }

    /**
     * Unregisters all ListenerWrapper objects whose associated event type is
     * equal to the specified class from all HandlerList objects.
     *
     * @param listener the EventListener that represents the event type
     */
    public synchronized void unregister(@NotNull EventListener listener) {
        boolean changed = false;
        for (List<ListenerWrapper> list : handlerSlots.values()) {
            for (ListIterator<ListenerWrapper> listenerWrapperListIterator = list.listIterator(); listenerWrapperListIterator.hasNext(); ) {
                if (listener.equals(listenerWrapperListIterator.next().listener())) {
                    listenerWrapperListIterator.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }

    /**
     * Bakes an array of ListenerWrapper objects by collecting all listeners from the
     * handlerSlots map and assigning them to the handler array, which can be used for
     * efficient event dispatching. This method does nothing if the handler array has
     * already been baked.
     * <p>The order of ListenerWrapper objects in the handler array corresponds to the
     * order of their associated EventPriority values, with lower priority values coming
     * before higher ones.
     */
    public synchronized void bake() {
        if (handlers != null) return;
        List<ListenerWrapper> entries = new ArrayList<>();
        for (Map.Entry<EventPriority, ArrayList<ListenerWrapper>> entry : handlerSlots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        handlers = entries.toArray(new ListenerWrapper[0]);
    }

    /**
     * Returns a list of all ListenerWrapper objects that are registered with
     * this HandlerList object.
     *
     * @return an array of all registered ListenerWrapper objects
     */
    @NotNull
    public ListenerWrapper[] getListenerWrappers() {
        ListenerWrapper[] handlers;
        while ((handlers = this.handlers) == null) bake();
        return handlers;
    }

    /**
     * Returns a list of all ListenerWrapper objects that are registered with
     * any HandlerList object and whose associated event type is equal to the
     * specified class.
     *
     * @param clazz the class that represents the event type
     * @return a list of all registered ListenerWrapper objects with the specified event type
     */
    @NotNull
    public static ArrayList<ListenerWrapper> getListenerWrappers(@NotNull Class<?> clazz) {
        ArrayList<ListenerWrapper> listeners = new ArrayList<>();
        synchronized (allLists) {
            for (HandlerList handlerList : allLists) {
                synchronized (handlerList) {
                    for (List<ListenerWrapper> list : handlerList.handlerSlots.values()) {
                        for (ListenerWrapper listener : list) {
                            if (listener.aClass().equals(clazz)) {
                                listeners.add(listener);
                            }
                        }
                    }
                }
            }
        }
        return listeners;
    }

    /**
     * Returns an unmodifiable list of all HandlerList objects.
     *
     * @return an unmodifiable list of all HandlerList objects
     */
    @Contract(pure = true)
    @NotNull
    public static @UnmodifiableView List<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return Collections.unmodifiableList(allLists);
        }
    }
}
