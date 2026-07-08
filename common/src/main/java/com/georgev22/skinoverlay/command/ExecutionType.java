package com.georgev22.skinoverlay.command;

/**
 * Represents how a command should be executed.
 * <ul>
 *     <li>{@link #SYNC} — the command runs on the main server thread immediately.</li>
 *     <li>{@link #ASYNC} — the command runs asynchronously, off the main server thread.</li>
 * </ul>
 */
public enum ExecutionType {
    /**
     * Execute the command on the main server thread (synchronously).
     */
    SYNC,

    /**
     * Execute the command asynchronously on a separate thread.
     */
    ASYNC
}
