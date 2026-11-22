package com.georgev22.skinoverlay.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A non-extendable interface representing a scheduler for task scheduling and cancellation.
 * Tasks can be scheduled synchronously (on the main server thread) or asynchronously,
 * with optional delays, repetition, and context-specific execution (world, chunk, location, entity).
 */
public interface MinecraftScheduler<Plugin, Location, World, Chunk, Entity> {

    /**
     * Schedules a synchronous task to run on the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask runTask(Plugin plugin, Runnable task);

    /**
     * Schedules a synchronous task that returns a result via a {@link CompletableFuture}.
     * The supplier runs on the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> runTask(Plugin plugin, Supplier<T> task);

    /**
     * Schedules an asynchronous task to run off the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask runAsyncTask(Plugin plugin, Runnable task);

    /**
     * Schedules an asynchronous task that returns a result via a {@link CompletableFuture}.
     * The supplier runs off the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> runAsyncTask(Plugin plugin, Supplier<T> task);

    /**
     * Schedules a synchronous delayed task to run after the specified delay.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param delay  the delay in ticks before the task executes
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createDelayedTask(Plugin plugin, Runnable task, long delay);

    /**
     * Schedules a synchronous delayed task that returns a result via a {@link CompletableFuture}.
     * The supplier runs after the delay on the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param delay  the delay in ticks before the supplier executes
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createDelayedTask(Plugin plugin, Supplier<T> task, long delay);

    /**
     * Schedules a synchronous repeating task with an initial delay and subsequent periods.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param delay  the delay in ticks before the first execution
     * @param period the period in ticks between subsequent executions
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

    /**
     * Schedules an asynchronous delayed task to run after the specified delay.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param delay  the delay in ticks before the task executes
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createAsyncDelayedTask(Plugin plugin, Runnable task, long delay);

    /**
     * Schedules an asynchronous delayed task that returns a result via a {@link CompletableFuture}.
     * The supplier runs after the delay off the main server thread.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param delay  the delay in ticks before the supplier executes
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createAsyncDelayedTask(Plugin plugin, Supplier<T> task, long delay);

    /**
     * Schedules an asynchronous repeating task with an initial delay and subsequent periods.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param delay  the delay in ticks before the first execution
     * @param period the period in ticks between subsequent executions
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

    /**
     * Schedules a synchronous delayed task to run in the specified world and chunk context.
     * The task will only execute if the chunk is loaded.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param world  the world associated with the task
     * @param chunk  the chunk associated with the task (must not be null)
     * @param delay  the delay in ticks before the task executes
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createDelayedTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay);

    /**
     * Schedules a synchronous delayed task in the specified world and chunk context that returns a result.
     * The supplier runs on the main server thread if the chunk is loaded.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param world  the world associated with the task
     * @param chunk  the chunk associated with the task (must not be null)
     * @param delay  the delay in ticks before the supplier executes
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createDelayedTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk, long delay);

    /**
     * Schedules a synchronous delayed task associated with a specific location.
     * The task runs on the main thread when the location's chunk is loaded.
     *
     * @param plugin   the plugin scheduling the task
     * @param task     the task to run
     * @param location the location associated with the task
     * @param delay    the delay in ticks before the task executes
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createDelayedForLocation(Plugin plugin, Runnable task, Location location, long delay);

    /**
     * Schedules a synchronous delayed task associated with a location that returns a result.
     * The supplier runs on the main server thread when the location's chunk is loaded.
     *
     * @param plugin   the plugin scheduling the task
     * @param task     the supplier providing the result
     * @param location the location associated with the task
     * @param delay    the delay in ticks before the supplier executes
     * @param <T>      the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createDelayedForLocation(Plugin plugin, Supplier<T> task, Location location, long delay);

    /**
     * Schedules a synchronous delayed task associated with an entity. If the entity is retired (removed),
     * the retired runnable is executed.
     *
     * @param plugin  the plugin scheduling the task
     * @param task    the task to run
     * @param retired the runnable to execute if the entity is retired before the task runs
     * @param entity  the entity associated with the task
     * @param delay   the delay in ticks before the task executes
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createDelayedForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity, long delay);

    /**
     * Schedules a synchronous delayed task associated with an entity that returns a result.
     * If the entity is retired, the retired runnable is executed.
     *
     * @param plugin  the plugin scheduling the task
     * @param task    the supplier providing the result
     * @param retired the runnable to execute if the entity is retired
     * @param entity  the entity associated with the task
     * @param delay   the delay in ticks before the supplier executes
     * @param <T>     the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createDelayedForEntity(Plugin plugin, Supplier<T> task, Runnable retired, Entity entity, long delay);

    /**
     * Schedules a synchronous task to run in the specified world and chunk context.
     * The task executes immediately if the chunk is loaded.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param world  the world associated with the task
     * @param chunk  the chunk associated with the task (must not be null)
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk);

    /**
     * Schedules a synchronous task in the specified world and chunk context that returns a result.
     * The supplier runs on the main server thread if the chunk is loaded.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the supplier providing the result
     * @param world  the world associated with the task
     * @param chunk  the chunk associated with the task (must not be null)
     * @param <T>    the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk);

    /**
     * Schedules a synchronous task associated with a specific location.
     * The task runs on the main thread when the location's chunk is loaded.
     *
     * @param plugin   the plugin scheduling the task
     * @param task     the task to run
     * @param location the location associated with the task
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createTaskForLocation(Plugin plugin, Runnable task, Location location);

    /**
     * Schedules a synchronous task associated with a location that returns a result.
     * The supplier runs on the main server thread when the location's chunk is loaded.
     *
     * @param plugin   the plugin scheduling the task
     * @param task     the supplier providing the result
     * @param location the location associated with the task
     * @param <T>      the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createTaskForLocation(Plugin plugin, Supplier<T> task, Location location);

    /**
     * Schedules a synchronous task associated with an entity. If the entity is retired,
     * the retired runnable is executed.
     *
     * @param plugin  the plugin scheduling the task
     * @param task    the task to run
     * @param retired the runnable to execute if the entity is retired
     * @param entity  the entity associated with the task
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createTaskForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity);

    /**
     * Schedules a synchronous task associated with an entity that returns a result.
     * If the entity is retired, the retired runnable is executed.
     *
     * @param plugin  the plugin scheduling the task
     * @param task    the supplier providing the result
     * @param retired the runnable to execute if the entity is retired
     * @param entity  the entity associated with the task
     * @param <T>     the type of the result
     * @return a {@link CompletableFuture} that will be completed with the supplier's result
     */
    <T> CompletableFuture<T> createTaskForEntity(Plugin plugin, Supplier<T> task, Runnable retired, Entity entity);

    /**
     * Schedules a synchronous repeating task in the specified world and chunk context.
     * The task repeats only if the chunk is loaded.
     *
     * @param plugin the plugin scheduling the task
     * @param task   the task to run
     * @param world  the world associated with the task
     * @param chunk  the chunk associated with the task (must not be null)
     * @param delay  the delay in ticks before the first execution
     * @param period the period in ticks between subsequent executions
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createRepeatingTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay, long period);

    /**
     * Schedules a synchronous repeating task associated with a specific location.
     * The task runs on the main thread when the location's chunk is loaded.
     *
     * @param plugin   the plugin scheduling the task
     * @param task     the task to run
     * @param location the location associated with the task
     * @param delay    the delay in ticks before the first execution
     * @param period   the period in ticks between subsequent executions
     * @return a {@link SchedulerTask} instance representing the scheduled task
     */
    SchedulerTask createRepeatingTaskForLocation(Plugin plugin, Runnable task, Location location, long delay, long period);

    /**
     * Schedules a synchronous repeating task associated with an entity. The task runs with an initial delay
     * and repeats with the specified period. If the entity is retired, the retired runnable is called.
     *
     * @param plugin  the plugin scheduling the task
     * @param task    the task to run
     * @param retired the runnable to execute if the entity is retired
     * @param entity  the entity associated with the task
     * @param delay   the delay in ticks before the first execution
     * @param period  the period in ticks between subsequent executions
     */
    SchedulerTask createRepeatingTaskForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity, long delay, long period);

    /**
     * Cancels all tasks associated with the given `plugin`.
     *
     * @param plugin The plugin whose tasks should be canceled.
     */
    void cancelTasks(Plugin plugin);

    /**
     * Gets the scheduler
     *
     * @return The scheduler
     */
    MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler();

}
