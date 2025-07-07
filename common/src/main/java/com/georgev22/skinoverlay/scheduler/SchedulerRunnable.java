package com.georgev22.skinoverlay.scheduler;

import org.jetbrains.annotations.NotNull;

public abstract class SchedulerRunnable<Plugin, Location, World, Chunk, Entity> implements Runnable {

    private SchedulerTask task;

    private final MinecraftScheduler<Plugin, Location, World, Chunk, Entity> minecraftScheduler;

    public SchedulerRunnable(MinecraftScheduler<Plugin, Location, World, Chunk, Entity> minecraftScheduler) {
        this.minecraftScheduler = minecraftScheduler;
    }

    /**
     * Returns true if this task has been cancelled.
     *
     * @return true if the task has been cancelled
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized boolean isCancelled() throws IllegalStateException {
        checkScheduled();
        return task.isCancelled();
    }

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        task.cancel();
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @param plugin The Plugin associated with this task.
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.runTask(plugin, this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @param plugin The Plugin associated with this task.
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.runAsyncTask(plugin, this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param plugin The Plugin associated with this task.
     * @param delay  the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createDelayedTask(plugin, this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param plugin The Plugin associated with this task.
     * @param delay  the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createAsyncDelayedTask(plugin, this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param plugin The Plugin associated with this task.
     * @param delay  the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createRepeatingTask(plugin, this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param plugin The Plugin associated with this task.
     * @param delay  the ticks to wait before running the task for the first
     *               time
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if clazz, is null
     * @throws IllegalStateException    if this was already scheduled
     */
    @NotNull
    public synchronized SchedulerTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(this.minecraftScheduler.createAsyncRepeatingTask(plugin, this, delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled();
        return task.getTaskId();
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (task != null) {
            throw new IllegalStateException("Already scheduled as " + task.getTaskId());
        }
    }

    @NotNull
    private SchedulerTask setupTask(@NotNull final SchedulerTask task) {
        this.task = task;
        return task;
    }

}
