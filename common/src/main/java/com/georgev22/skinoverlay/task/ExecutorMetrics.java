package com.georgev22.skinoverlay.task;

/**
 * Represents runtime metrics of an executor.
 */
public record ExecutorMetrics(int poolSize, int activeThreads, int queuedTasks, long completedTasks) {

}