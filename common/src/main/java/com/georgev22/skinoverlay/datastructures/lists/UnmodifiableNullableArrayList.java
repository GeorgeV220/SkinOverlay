package com.georgev22.skinoverlay.datastructures.lists;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An unmodifiable version of the NullableArrayList class.
 *
 * @param <E> the type of elements in this list
 */
public class UnmodifiableNullableArrayList<E> extends NullableArrayList<E> {

    /**
     * Constructs an unmodifiable list backed by the specified list.
     *
     * @param list the list to be wrapped in an unmodifiable list
     * @throws NullPointerException if the specified list is null
     */
    public UnmodifiableNullableArrayList(NullableArrayList<E> list) {
        super(list);
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param index   the index at which to set the element
     * @param element the element to be set
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException always
     */
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param index   the index at which to insert the element
     * @param element the element to be inserted
     * @throws UnsupportedOperationException always
     */
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param index the index of the element to be removed and set to null
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException always
     */
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param listener the listener to be registered
     * @throws UnsupportedOperationException always
     */
    @Override
    public void addListener(ListListener<E> listener) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param listener the listener to be unregistered
     * @throws UnsupportedOperationException always
     */
    @Override
    public void removeListener(ListListener<E> listener) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param c the collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param index the index at which to insert the first element from the specified collection
     * @param c     the collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param c the collection containing elements to be removed from this list
     * @return true if this list changed as a result of the call
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param c the collection containing elements to be retained in this list
     * @return true if this list changed as a result of the call
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param operator the operator to apply to each element
     * @throws UnsupportedOperationException always
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param filter a predicate which returns true for elements to be removed
     * @return true if any elements were removed
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Returns an unmodifiable list iterator over the elements in this list.
     *
     * @return an unmodifiable list iterator
     */
    @Override
    public @NonNull ListIterator<E> listIterator() {
        return Collections.unmodifiableList(this).listIterator();
    }

    /**
     * Returns an unmodifiable list iterator over the elements in this list, starting at the specified position.
     *
     * @param index the starting position of the iterator
     * @return an unmodifiable list iterator
     */
    @Override
    public @NonNull ListIterator<E> listIterator(int index) {
        return Collections.unmodifiableList(this).listIterator(index);
    }

    /**
     * Returns an unmodifiable sublist of this list.
     *
     * @param fromIndex the starting index (inclusive)
     * @param toIndex   the ending index (exclusive)
     * @return an unmodifiable sublist
     */
    @Override
    public @NonNull List<E> subList(int fromIndex, int toIndex) {
        return Collections.unmodifiableList(super.subList(fromIndex, toIndex));
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param action the action to be performed for each element
     * @throws UnsupportedOperationException always
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        throw new UnsupportedOperationException("Unmodifiable list");
    }

    /**
     * Returns an unmodifiable view of the specified list.
     *
     * @param list the list to be wrapped in an unmodifiable list
     * @param <E>  the type of elements in the list
     * @return an unmodifiable view of the specified list
     * @throws NullPointerException if the specified list is null
     */
    @Contract("_ -> new")
    public static <E> @NonNull UnmodifiableNullableArrayList<E> unmodifiableList(NullableArrayList<E> list) {
        return new UnmodifiableNullableArrayList<>(list);
    }
}