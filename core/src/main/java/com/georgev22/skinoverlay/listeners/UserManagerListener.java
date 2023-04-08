package com.georgev22.skinoverlay.listeners;

import com.georgev22.library.maps.ObservableObjectMap;

public abstract class UserManagerListener<K, V> implements ObservableObjectMap.MapChangeListener<K, V> {

    @Override
    public abstract void entryAdded(K key, V value);
}
