package com.georgev22.skinoverlay.utilities;

import com.georgev22.library.maps.ConcurrentObjectMap;
import com.georgev22.library.maps.ObjectMap;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CompletableFutureManager<T> {
    private final ObjectMap<Consumer<CompletableFuture<T>>, ObjectMap.Pair<Boolean, CompletableFuture<T>>> futures = new ConcurrentObjectMap<>();


    public void add(Consumer<CompletableFuture<T>> consumer, CompletableFuture<T> future) {
        futures.append(consumer, ObjectMap.Pair.create(false, future));
        future.whenComplete((result, throwable) -> {
            consumer.accept(future);
            futures.append(consumer, ObjectMap.Pair.create(true, future));
        });
    }

    public ObjectMap<Consumer<CompletableFuture<T>>, ObjectMap.Pair<Boolean, CompletableFuture<T>>> getFutures() {
        return futures;
    }
}
