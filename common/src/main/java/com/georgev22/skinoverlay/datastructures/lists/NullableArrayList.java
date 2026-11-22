package com.georgev22.skinoverlay.datastructures.lists;

import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A custom ArrayList implementation that allows handling of potential IndexOutOfBoundsException by
 * returning null values instead of throwing exceptions.
 *
 * @param <E> the type of elements in this list
 */
public class NullableArrayList<E> extends ArrayList<E> {

    private final ArrayList<ListListener<E>> listeners = new ArrayList<>();

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public NullableArrayList() {
        super();
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public NullableArrayList(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a list containing the elements of the specified collection in the order they are returned by the
     * collection's iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public NullableArrayList(Collection<? extends E> c) {
        super(c);
    }

    /**
     * Returns the element at the specified position in this list or null if the index is out of bounds.
     *
     * @param index the index of the element to return
     * @return the element at the specified position in this list or null if the index is out of bounds
     */
    @Override
    public E get(int index) {
        try {
            return super.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * If the index is out of bounds, null elements will be added until the specified index is reached.
     *
     * @param index   the index at which to set the element
     * @param element the element to be set
     * @return the element previously at the specified position, or null if the index is out of bounds
     */
    @Override
    public E set(int index, E element) {
        try {
            E previousElement = super.set(index, element);
            notifyListeners(index, previousElement, element, ListListener.EventType.SET);
            return previousElement;
        } catch (IndexOutOfBoundsException e) {
            for (int i = size(); i <= index; i++) {
                super.add(null);
            }
            super.add(index, element);
            notifyListeners(index, null, element, ListListener.EventType.SET);
            return null;
        }
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * If the index is out of bounds, null elements will be added until the specified index is reached.
     *
     * @param index   the index at which to insert the element
     * @param element the element to be inserted
     */
    @Override
    public void add(int index, E element) {
        try {
            super.add(index, element);
            notifyListeners(index, null, element, ListListener.EventType.ADD);
        } catch (IndexOutOfBoundsException e) {
            for (int i = size(); i <= index; i++) {
                super.add(null);
            }
            super.add(index, element);
            notifyListeners(index, null, element, ListListener.EventType.ADD);
        }
    }

    /**
     * Removes the element at the specified position in this list and sets it to null.
     * If the index is out of bounds, returns null.
     *
     * @param index the index of the element to be removed and set to null
     * @return the element previously at the specified position, or null if the index is out of bounds
     */
    @Override
    public E remove(int index) {
        try {
            E removedElement = this.set(index, null);
            notifyListeners(index, removedElement, null, ListListener.EventType.REMOVE);
            return removedElement;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Register a listener to be notified of changes in the list.
     *
     * @param listener the listener to be registered
     */
    public void addListener(ListListener<E> listener) {
        listeners.add(listener);
    }

    /**
     * Unregister a listener from receiving notifications of changes in the list.
     *
     * @param listener the listener to be unregistered
     */
    public void removeListener(ListListener<E> listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all registered listeners of a change in the list.
     *
     * @param index     the index at which the change occurred
     * @param oldValue  the previous value at the index
     * @param newValue  the new value at the index
     * @param eventType the type of event that occurred
     */
    private void notifyListeners(int index, E oldValue, E newValue, ListListener.EventType eventType) {
        for (ListListener<E> listener : listeners) {
            listener.onListChanged(index, oldValue, newValue, eventType);
        }
    }
}
