package com.georgev22.skinoverlay.fabric.scheduler;

import com.georgev22.skinoverlay.scheduler.MinecraftScheduler;
import com.georgev22.skinoverlay.scheduler.SchedulerTask;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class FabricMinecraftScheduler
        implements MinecraftScheduler<Object, Object, Object, Object, Object> {

    private static final long TICK_MILLIS = 50L;
    private static final AtomicInteger NEXT_TASK_ID = new AtomicInteger();

    private final MinecraftServer server;
    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "SkinOverlay-Fabric-Async");
        thread.setDaemon(true);
        return thread;
    });
    private final ScheduledExecutorService timerExecutor = Executors.newScheduledThreadPool(1, runnable -> {
        Thread thread = new Thread(runnable, "SkinOverlay-Fabric-Timer");
        thread.setDaemon(true);
        return thread;
    });
    private final Set<FabricTask> tasks = ConcurrentHashMap.newKeySet();

    public FabricMinecraftScheduler(@NotNull MinecraftServer server) {
        this.server = server;
    }

    @Override
    public SchedulerTask runTask(Object plugin, Runnable task) {
        FabricTask scheduled = track(task);
        server.execute(scheduled::run);
        return scheduled;
    }

    @Override
    public <T> CompletableFuture<T> runTask(Object plugin, Supplier<T> task) {
        return submitOnServer(task, 0L);
    }

    @Override
    public SchedulerTask runAsyncTask(Object plugin, Runnable task) {
        FabricTask scheduled = track(task);
        scheduled.setFuture(asyncExecutor.submit(scheduled::run));
        return scheduled;
    }

    @Override
    public <T> CompletableFuture<T> runAsyncTask(Object plugin, Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, asyncExecutor);
    }

    @Override
    public SchedulerTask createDelayedTask(Object plugin, Runnable task, long delay) {
        return scheduleOnServer(task, delay, 0L);
    }

    @Override
    public <T> CompletableFuture<T> createDelayedTask(Object plugin, Supplier<T> task, long delay) {
        return submitOnServer(task, delay);
    }

    @Override
    public SchedulerTask createRepeatingTask(Object plugin, Runnable task, long delay, long period) {
        return scheduleOnServer(task, delay, period);
    }

    @Override
    public SchedulerTask createAsyncDelayedTask(Object plugin, Runnable task, long delay) {
        FabricTask scheduled = track(task);
        ScheduledFuture<?> future = timerExecutor.schedule(scheduled::run, ticksToMillis(delay), TimeUnit.MILLISECONDS);
        scheduled.setFuture(future);
        return scheduled;
    }

    @Override
    public <T> CompletableFuture<T> createAsyncDelayedTask(Object plugin, Supplier<T> task, long delay) {
        CompletableFuture<T> result = new CompletableFuture<>();
        timerExecutor.schedule(() -> {
            try {
                result.complete(task.get());
            } catch (Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        }, ticksToMillis(delay), TimeUnit.MILLISECONDS);
        return result;
    }

    @Override
    public SchedulerTask createAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period) {
        FabricTask scheduled = track(task);
        ScheduledFuture<?> future = timerExecutor.scheduleAtFixedRate(
                scheduled::run,
                ticksToMillis(delay),
                Math.max(1L, ticksToMillis(period)),
                TimeUnit.MILLISECONDS
        );
        scheduled.setFuture(future);
        return scheduled;
    }

    @Override
    public SchedulerTask createDelayedTaskForWorld(Object plugin, Runnable task, Object world, @NotNull Object chunk, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public <T> CompletableFuture<T> createDelayedTaskForWorld(Object plugin, Supplier<T> task, Object world, @NotNull Object chunk, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public SchedulerTask createDelayedForLocation(Object plugin, Runnable task, Object location, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public <T> CompletableFuture<T> createDelayedForLocation(Object plugin, Supplier<T> task, Object location, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public SchedulerTask createDelayedForEntity(Object plugin, Runnable task, Runnable retired, Object entity, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public <T> CompletableFuture<T> createDelayedForEntity(Object plugin, Supplier<T> task, Runnable retired, Object entity, long delay) {
        return createDelayedTask(plugin, task, delay);
    }

    @Override
    public SchedulerTask createTaskForWorld(Object plugin, Runnable task, Object world, @NotNull Object chunk) {
        return runTask(plugin, task);
    }

    @Override
    public <T> CompletableFuture<T> createTaskForWorld(Object plugin, Supplier<T> task, Object world, @NotNull Object chunk) {
        return runTask(plugin, task);
    }

    @Override
    public SchedulerTask createTaskForLocation(Object plugin, Runnable task, Object location) {
        return runTask(plugin, task);
    }

    @Override
    public <T> CompletableFuture<T> createTaskForLocation(Object plugin, Supplier<T> task, Object location) {
        return runTask(plugin, task);
    }

    @Override
    public SchedulerTask createTaskForEntity(Object plugin, Runnable task, Runnable retired, Object entity) {
        return runTask(plugin, task);
    }

    @Override
    public <T> CompletableFuture<T> createTaskForEntity(Object plugin, Supplier<T> task, Runnable retired, Object entity) {
        return runTask(plugin, task);
    }

    @Override
    public SchedulerTask createRepeatingTaskForWorld(Object plugin, Runnable task, Object world, @NotNull Object chunk, long delay, long period) {
        return createRepeatingTask(plugin, task, delay, period);
    }

    @Override
    public SchedulerTask createRepeatingTaskForLocation(Object plugin, Runnable task, Object location, long delay, long period) {
        return createRepeatingTask(plugin, task, delay, period);
    }

    @Override
    public SchedulerTask createRepeatingTaskForEntity(Object plugin, Runnable task, Runnable retired, Object entity, long delay, long period) {
        return createRepeatingTask(plugin, task, delay, period);
    }

    @Override
    public void cancelTasks(Object plugin) {
        tasks.forEach(FabricTask::cancel);
        tasks.clear();
    }

    public void shutdown() {
        cancelTasks(null);
        asyncExecutor.shutdownNow();
        timerExecutor.shutdownNow();
    }

    @Override
    public MinecraftScheduler<Object, Object, Object, Object, Object> getScheduler() {
        return this;
    }

    private FabricTask scheduleOnServer(Runnable task, long delay, long period) {
        FabricTask scheduled = track(task);
        Runnable dispatch = () -> server.execute(scheduled::run);
        if (delay <= 0L && period <= 0L) {
            dispatch.run();
        } else if (period <= 0L) {
            ScheduledFuture<?> future = timerExecutor.schedule(dispatch, ticksToMillis(delay), TimeUnit.MILLISECONDS);
            scheduled.setFuture(future);
        } else {
            ScheduledFuture<?> future = timerExecutor.scheduleAtFixedRate(
                    dispatch,
                    ticksToMillis(delay),
                    Math.max(1L, ticksToMillis(period)),
                    TimeUnit.MILLISECONDS
            );
            scheduled.setFuture(future);
        }
        return scheduled;
    }

    private <T> CompletableFuture<T> submitOnServer(Supplier<T> supplier, long delay) {
        CompletableFuture<T> result = new CompletableFuture<>();
        scheduleOnServer(() -> {
            try {
                result.complete(supplier.get());
            } catch (Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        }, delay, 0L);
        return result;
    }

    private FabricTask track(Runnable task) {
        FabricTask scheduled = new FabricTask(task);
        tasks.add(scheduled);
        return scheduled;
    }

    private static long ticksToMillis(long ticks) {
        return Math.max(0L, ticks) * TICK_MILLIS;
    }

    private final class FabricTask implements SchedulerTask {
        private final int id = NEXT_TASK_ID.incrementAndGet();
        private final Runnable task;
        private volatile Future<?> future;
        private volatile boolean cancelled;
        private volatile boolean running;

        private FabricTask(Runnable task) {
            this.task = task;
        }

        private void setFuture(Future<?> future) {
            this.future = future;
            if (cancelled) {
                future.cancel(false);
            }
        }

        private void run() {
            if (cancelled) {
                return;
            }
            running = true;
            try {
                task.run();
            } finally {
                running = false;
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
            Future<?> current = future;
            if (current != null) {
                current.cancel(false);
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public int getTaskId() {
            return id;
        }

        @Override
        public boolean isRunning() {
            return running;
        }
    }
}
