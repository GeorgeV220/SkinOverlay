package com.georgev22.skinoverlay.utilities.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SkinOverlayBukkitScheduler {

    public void createDelayedTask(Plugin plugin, Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    public void createRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    public void cancelTasks(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

}
