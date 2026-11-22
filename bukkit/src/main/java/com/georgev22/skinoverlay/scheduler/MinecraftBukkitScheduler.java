package com.georgev22.skinoverlay.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public class MinecraftBukkitScheduler implements MinecraftScheduler<Plugin, Location, World, Chunk, Entity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask runTask(Plugin plugin, Runnable task) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> runTask(Plugin plugin, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask runAsyncTask(Plugin plugin, Runnable task) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> runAsyncTask(Plugin plugin, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedTask(Plugin plugin, Runnable task, long delay) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTask(Plugin plugin, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay);

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createAsyncDelayedTask(Plugin plugin, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay);

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk, long delay) {
        return this.createDelayedTask(plugin, task, delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedForLocation(Plugin plugin, Runnable task, Location location, long delay) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForLocation(Plugin plugin, Supplier<T> task, Location location, long delay) {
        return this.createDelayedTask(plugin, task, delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity, long delay) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForEntity(Plugin plugin, Supplier<T> task, Runnable retired, Entity entity, long delay) {
        return this.createDelayedTask(plugin, task, delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk) {
        return this.runTask(plugin, task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForLocation(Plugin plugin, Runnable task, Location location) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForLocation(Plugin plugin, Supplier<T> task, Location location) {
        return this.runTask(plugin, task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForEntity(Plugin plugin, Supplier<T> task, Runnable retired, Entity entity) {
        return this.runTask(plugin, task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForLocation(Plugin plugin, Runnable task, Location location, long delay, long period) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForEntity(Plugin plugin, Runnable task, Runnable retired, Entity entity, long delay, long period) {
        return new BukkitSchedulerTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler() {
        return this;
    }

    public static class BukkitSchedulerTask implements SchedulerTask {

        private final BukkitTask bukkitTask;

        public BukkitSchedulerTask(BukkitTask bukkitTask) {
            this.bukkitTask = bukkitTask;
        }

        @Override
        public void cancel() {
            bukkitTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            return bukkitTask.isCancelled();
        }

        @Override
        public int getTaskId() {
            return bukkitTask.getTaskId();
        }

        @Override
        public boolean isRunning() {
            return Bukkit.getScheduler().isCurrentlyRunning(bukkitTask.getTaskId());
        }
    }
}
