package com.georgev22.skinoverlay.scheduler;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class VelocityMinecraftScheduler<Plugin, Location, World, Chunk, Entity> implements MinecraftScheduler<Plugin, Location, World, Chunk, Entity> {

    private final ProxyServer proxyServer;

    public VelocityMinecraftScheduler(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    private static final List<SchedulerTask> tasks = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask runTask(Plugin o, Runnable task) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    @Override
    public <T> CompletableFuture<T> runTask(Plugin o, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runTask(o, () -> {
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
    public SchedulerTask runAsyncTask(Plugin o, Runnable task) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> runAsyncTask(Plugin o, Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsyncTask(o, () -> {
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
    public SchedulerTask createDelayedTask(Plugin o, Runnable task, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTask(Plugin o, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createDelayedTask(o, () -> {
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
    public SchedulerTask createRepeatingTask(Plugin o, Runnable task, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createAsyncDelayedTask(Plugin o, Runnable task, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createAsyncDelayedTask(Plugin o, Supplier<T> task, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createAsyncDelayedTask(o, () -> {
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
    public SchedulerTask createAsyncRepeatingTask(Plugin o, Runnable task, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedTaskForWorld(Plugin o, Runnable task, World world, @NotNull Chunk chunk, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedTaskForWorld(Plugin o, Supplier<T> task, World world, @NotNull Chunk chunk, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createDelayedTaskForWorld(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, world, chunk, delay);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedForLocation(Plugin o, Runnable task, Location location, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForLocation(Plugin o, Supplier<T> task, Location location, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createDelayedForLocation(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, location, delay);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createDelayedForEntity(Plugin o, Runnable task, Runnable retired, Entity entity, long delay) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createDelayedForEntity(Plugin o, Supplier<T> task, Runnable retired, Entity entity, long delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createDelayedForEntity(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, retired, entity, delay);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForWorld(Plugin o, Runnable task, World world, @NotNull Chunk chunk) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForWorld(Plugin o, Supplier<T> task, World world, @NotNull Chunk chunk) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createTaskForWorld(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, world, chunk);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForLocation(Plugin o, Runnable task, Location location) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForLocation(Plugin o, Supplier<T> task, Location location) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createTaskForLocation(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, location);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createTaskForEntity(Plugin o, Runnable task, Runnable retired, Entity entity) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> createTaskForEntity(Plugin o, Supplier<T> task, Runnable retired, Entity entity) {
        CompletableFuture<T> future = new CompletableFuture<>();
        createTaskForEntity(o, () -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, retired, entity);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForWorld(Plugin o, Runnable task, World world, @NotNull Chunk chunk, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForLocation(Plugin o, Runnable task, Location location, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchedulerTask createRepeatingTaskForEntity(Plugin o, Runnable task, Runnable retired, Entity entity, long delay, long period) {
        return new VelocitySchedulerTask(proxyServer.getScheduler().buildTask(o, task).delay((delay / 20), TimeUnit.SECONDS).repeat((period / 20), TimeUnit.SECONDS).schedule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelTasks(Plugin o) {
        new ArrayList<>(tasks).forEach(schedulerTask -> {
            tasks.remove(schedulerTask);
            if (schedulerTask.isCancelled()) {
                return;
            }
            schedulerTask.cancel();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinecraftScheduler<Plugin, Location, World, Chunk, Entity> getScheduler() {
        return this;
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class VelocitySchedulerTask implements SchedulerTask {

        private final ScheduledTask task;

        public VelocitySchedulerTask(ScheduledTask task) {
            this.task = task;
            tasks.add(this);
        }


        @Override
        public void cancel() {
            this.task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return this.task.status().equals(TaskStatus.CANCELLED) || this.task.status().equals(TaskStatus.FINISHED);
        }

        @Override
        public int getTaskId() {
            return 0;
        }

        @Override
        public boolean isRunning() {
            return this.task.status().equals(TaskStatus.SCHEDULED);
        }
    }
}