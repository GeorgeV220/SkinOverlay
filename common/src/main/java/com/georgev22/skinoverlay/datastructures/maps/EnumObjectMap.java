package com.georgev22.skinoverlay.datastructures.maps;

import java.util.EnumMap;
import java.util.Map;

public class EnumObjectMap<K extends Enum<K>, V> extends AbstractObjectMap<K, V> implements ObjectMap<K, V> {

    private final Class<K> kClass;

    /**
     * Creates an empty {@code EnumObjectMap} with a new {@link EnumMap}.
     */
    public EnumObjectMap(Class<K> kClass) {
        super(new EnumMap<>(kClass));
        this.kClass = kClass;
    }

    /**
     * Creates a new {@code EnumObjectMap} by copying all mappings
     * from the given {@link EnumMap} into a new {@link EnumMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public EnumObjectMap(Class<K> kClass, EnumMap<K, V> map) {
        super(new EnumMap<>(map));
        this.kClass = kClass;
    }

    /**
     * Creates a new {@code EnumObjectMap} by copying all mappings
     * from the given {@link ObjectMap} into a new {@link EnumMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public EnumObjectMap(Class<K> kClass, final ObjectMap<K, V> map) {
        super(new EnumMap<>(map));
        this.kClass = kClass;
    }

    /**
     * Creates a new {@code EnumObjectMap} by copying all mappings
     * from the given {@link Map} into a new {@link EnumMap}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public EnumObjectMap(Class<K> kClass, final Map<K, V> map) {
        super(new EnumMap<>(map));
        this.kClass = kClass;
    }

    @Override
    public EnumObjectMap<K, V> newObjectMap() {
        return new EnumObjectMap<>(kClass);
    }
}
