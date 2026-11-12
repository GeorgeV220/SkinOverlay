package com.georgev22.skinoverlay.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public class MinecraftFoliaScheduler implements MinecraftScheduler<Plugin, Location, World, Chunk, Entity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask runTask(Plugin plugin, Runnable task) {
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().run(plugin, (scheduledTask) -> task.run()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> runTask(Plugin plugin, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> {
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
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> runAsyncTask(Plugin plugin, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
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
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTask(Plugin plugin, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> {
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
        return new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), (delay / 20), TimeUnit.SECONDS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createAsyncDelayedTask(Plugin plugin, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, (delay / 20), TimeUnit.SECONDS);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), (delay / 20), (period / 20), TimeUnit.SECONDS));
    }

    /**
     * {@inheritDoc}
     */
    public SchedulerTask createDelayedTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runDelayed(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run(), delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getRegionScheduler().runDelayed(
                plugin,
                world,
                chunk.getX(),
                chunk.getZ(),
                scheduledTask -> {
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
    public SchedulerTask createDelayedForLocation(Plugin plugin, Runnable task, Location location, long delay) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runDelayed(plugin, location, (scheduledTask) -> task.run(), delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForLocation(Plugin plugin, Supplier<T> task, Location location, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getRegionScheduler().runDelayed(plugin, location, scheduledTask -> {
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
    public SchedulerTask createDelayedForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity, long delay) {
        return new FoliaSchedulerTask(entity.getScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), retired, delay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForEntity(Plugin plugin, Supplier<T> task, Runnable retired, @NotNull Entity entity, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        entity.getScheduler().runDelayed(plugin, scheduledTask -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, retired, delay);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    public SchedulerTask createTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForWorld(Plugin plugin, Supplier<T> task, World world, @NotNull Chunk chunk) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getRegionScheduler().run(plugin, world, chunk.getX(), chunk.getZ(), scheduledTask -> {
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
    public SchedulerTask createTaskForLocation(Plugin plugin, Runnable task, Location location) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask) -> task.run()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForLocation(Plugin plugin, Supplier<T> task, Location location) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> {
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
    public SchedulerTask createTaskForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity) {
        return new FoliaSchedulerTask(entity.getScheduler().run(plugin, (scheduledTask) -> task.run(), retired));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForEntity(Plugin plugin, Supplier<T> task, Runnable retired, @NotNull Entity entity) {
        CompletableFuture<T> future = new CompletableFuture<>();
        entity.getScheduler().run(plugin, scheduledTask -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, retired);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    public SchedulerTask createRepeatingTaskForWorld(Plugin plugin, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, world, chunk.getX(), chunk.getZ(), (scheduledTask) -> task.run(), delay, period));
    }

    /**
     * {@inheritDoc}
     */
    public SchedulerTask createRepeatingTaskForLocation(Plugin plugin, Runnable task, Location location, long delay, long period) {
        return new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, (scheduledTask) -> task.run(), delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForEntity(Plugin plugin, Runnable task, Runnable retired, @NotNull Entity entity, long delay, long period) {
        return new FoliaSchedulerTask(entity.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), retired, delay, period));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler() {
        return this;
    }

    public static class FoliaSchedulerTask implements SchedulerTask {

        private final ScheduledTask scheduledTask;

        public FoliaSchedulerTask(ScheduledTask scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override
        public void cancel() {
            scheduledTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            return scheduledTask.isCancelled();
        }

        @Override
        public int getTaskId() {
            return 0;
        }

        @Override
        public boolean isRunning() {
            return switch (scheduledTask.getExecutionState()) {
                case IDLE, RUNNING -> true;
                default -> false;
            };
        }
    }
}
