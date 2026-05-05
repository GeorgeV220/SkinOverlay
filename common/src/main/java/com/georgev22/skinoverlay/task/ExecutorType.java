package com.georgev22.skinoverlay.task;

/**
 * Defines available executor types.
 */
public enum ExecutorType {

    /**
     * For blocking operations such as:
     * <ul>
     *     <li>Database queries</li>
     *     <li>File I/O</li>
     * </ul>
     */
    IO,

    /**
     * For CPU-intensive tasks.
     */
    COMPUTE,

    /**
     * For scheduled and repeating tasks.
     */
    SCHEDULED
}