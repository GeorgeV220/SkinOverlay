package com.georgev22.skinoverlay.listeners;

import com.georgev22.library.maps.ObservableObjectMap;
import org.jetbrains.annotations.Nullable;

public abstract class ObservableListener<K, V> implements ObservableObjectMap.MapChangeListener<K, V> {

    @Override
    public abstract void entryAdded(K key, V value);

    @Override
    public abstract void entryRemoved(Object key, @Nullable Object value);
}
