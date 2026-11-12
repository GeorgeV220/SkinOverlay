package com.georgev22.skinoverlay.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom ThreadFactory that assigns meaningful names to threads.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger counter = new AtomicInteger();

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name + "-" + counter.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}