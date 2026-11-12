package com.georgev22.skinoverlay.datastructures.lists;

/**
 * Interface for listeners to be notified of changes in the list.
 *
 * @param <E> the type of elements in the list
 */
public interface ListListener<E> {
    enum EventType {
        ADD, REMOVE, SET
    }

    void onListChanged(int index, E oldValue, E newValue, EventType eventType);
}