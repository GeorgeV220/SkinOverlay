package com.georgev22.skinoverlay.scheduler;

public interface SchedulerTask {

    void cancel();

    boolean isCancelled();

    int getTaskId();

    boolean isRunning();

}
