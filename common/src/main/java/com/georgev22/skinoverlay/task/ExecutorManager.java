package com.georgev22.skinoverlay.task;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Centralized manager for handling all ExecutorServices in the system.
 *
 * <p>This manager ensures:
 * <ul>
 *     <li>Executors are lazily created and reused</li>
 *     <li>Idle executors are automatically cleaned up</li>
 *     <li>Thread usage is controlled</li>
 *     <li>Metrics can be retrieved for monitoring</li>
 * </ul>
 *
 * <p>Typical usage:
 * <pre>
 * ExecutorManager manager = new ExecutorManager();
 * manager.submit(ExecutorType.IO, () -> doWork());
 * </pre>
 */
public class ExecutorManager {

    private static ExecutorManager instance;

    public static synchronized ExecutorManager getInstance() {
        if (instance == null) {
            instance = new ExecutorManager();
        }
        return instance;
    }

    private final Map<ExecutorType, ManagedExecutor> executors = new ConcurrentHashMap<>();
    private volatile ScheduledExecutorService cleanupService;

    private final AtomicLong maxIdleTimeMillis = new AtomicLong();

    private ExecutorManager() {
        this.maxIdleTimeMillis.set(TimeUnit.MINUTES.toMillis(5));
        this.cleanupService = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("executor-cleanup")
        );

        scheduleCleanupTask();
    }

    /**
     * Returns the ExecutorService for a given type.
     *
     * @param type executor type
     * @return executor service
     */
    public ExecutorService getExecutor(@NonNull ExecutorType type) {
        ManagedExecutor executor = getOrCreate(type);
        executor.touch();
        return executor.executor;
    }

    /**
     * Submits a Runnable task to the specified executor type.
     *
     * @param type executor type
     * @param task task to execute
     * @return Future representing the task
     */
    public Future<?> submit(ExecutorType type, Runnable task) {
        ManagedExecutor executor = getOrCreate(type);
        executor.touch();
        return executor.executor.submit(task);
    }

    /**
     * Submits a Callable task to the specified executor type.
     *
     * @param type executor type
     * @param task task to execute
     * @param <T>  return type
     * @return Future representing the result
     */
    public <T> Future<T> submit(ExecutorType type, Callable<T> task) {
        ManagedExecutor executor = getOrCreate(type);
        executor.touch();
        return executor.executor.submit(task);
    }

    /**
     * Schedules a delayed task.
     *
     * @param delay delay time
     * @param unit  time unit
     * @param task  task to execute
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> schedule(long delay, TimeUnit unit, Runnable task) {
        return getScheduler().schedule(task, delay, unit);
    }

    /**
     * Schedules a repeating task.
     *
     * @param initialDelay initial delay
     * @param period       period
     * @param unit         time unit
     * @param task         task
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleAtFixedRate(long initialDelay, long period, TimeUnit unit, Runnable task) {
        return getScheduler().scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * Returns metrics for a given executor type.
     *
     * @param type executor type
     * @return metrics or null if not created
     */
    public ExecutorMetrics getMetrics(ExecutorType type) {
        ManagedExecutor executor = executors.get(type);
        if (executor == null) return null;

        ThreadPoolExecutor tpe = executor.threadPool;
        return new ExecutorMetrics(
                tpe.getPoolSize(),
                tpe.getActiveCount(),
                tpe.getQueue().size(),
                tpe.getCompletedTaskCount()
        );
    }

    /**
     * Shuts down all executors managed by this instance.
     */
    public void shutdown() {
        executors.values().forEach(m -> m.executor.shutdown());
        cleanupService.shutdown();
    }

    private ManagedExecutor getOrCreate(ExecutorType type) {
        return executors.computeIfAbsent(type, this::createExecutor);
    }

    @Contract("_ -> new")
    private @NonNull ManagedExecutor createExecutor(@NonNull ExecutorType type) {
        ThreadPoolExecutor executor = switch (type) {
            case IO -> new ThreadPoolExecutor(
                    2,
                    8,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new NamedThreadFactory("skinoverlay-io"),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            case COMPUTE -> {
                int totalCores = Runtime.getRuntime().availableProcessors();
                int pluginCores = Math.max(1, totalCores / 2);
                yield new ThreadPoolExecutor(
                        pluginCores,
                        pluginCores,
                        30L, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(),
                        new NamedThreadFactory("skinoverlay-compute"),
                        new ThreadPoolExecutor.CallerRunsPolicy()
                );
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };

        executor.allowCoreThreadTimeOut(true);

        return new ManagedExecutor(executor);
    }

    private ScheduledExecutorService getScheduler() {
        return executors
                .computeIfAbsent(ExecutorType.SCHEDULED, t -> {
                    ScheduledThreadPoolExecutor scheduler =
                            new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("scheduler"));

                    scheduler.setKeepAliveTime(60, TimeUnit.SECONDS);
                    scheduler.allowCoreThreadTimeOut(true);

                    return new ManagedExecutor(scheduler);
                }).scheduledExecutor;
    }

    /**
     * Updates the maximum idle time before executors are removed.
     * The cleanup task is restarted with the new timeout.
     *
     * @param timeout new idle timeout
     * @param unit    time unit
     */
    public void setMaxIdleTime(long timeout, @NonNull TimeUnit unit) {
        maxIdleTimeMillis.set(unit.toMillis(timeout));

        cleanupService.shutdownNow();

        cleanupService = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("executor-cleanup")
        );

        scheduleCleanupTask();
    }

    /**
     * Schedules the cleanup task on the current cleanupService.
     */
    private void scheduleCleanupTask() {
        cleanupService.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            executors.entrySet().removeIf(entry -> {
                ManagedExecutor managed = entry.getValue();
                if (managed.isIdle(now, maxIdleTimeMillis.get())) {
                    managed.executor.shutdown();
                    return true;
                }
                return false;
            });
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Internal wrapper for tracking executor usage.
     */
    private static class ManagedExecutor {
        final ExecutorService executor;
        final ThreadPoolExecutor threadPool;
        final ScheduledExecutorService scheduledExecutor;

        volatile long lastUsed;

        ManagedExecutor(ExecutorService executor) {
            this.executor = executor;
            this.lastUsed = System.currentTimeMillis();

            if (executor instanceof ThreadPoolExecutor) {
                this.threadPool = (ThreadPoolExecutor) executor;
            } else {
                this.threadPool = null;
            }

            if (executor instanceof ScheduledExecutorService) {
                this.scheduledExecutor = (ScheduledExecutorService) executor;
            } else {
                this.scheduledExecutor = null;
            }
        }

        void touch() {
            this.lastUsed = System.currentTimeMillis();
        }

        boolean isIdle(long now, long maxIdle) {
            if (threadPool == null) return false;
            return (now - lastUsed) > maxIdle
                    && threadPool.getActiveCount() == 0
                    && threadPool.getQueue().isEmpty();
        }
    }
}