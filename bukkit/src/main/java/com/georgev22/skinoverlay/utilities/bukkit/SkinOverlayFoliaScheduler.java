package com.georgev22.skinoverlay.utilities.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SkinOverlayFoliaScheduler extends SkinOverlayBukkitScheduler {

    @Override
    public void createDelayedTask(Plugin plugin, Runnable task, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay);
    }

    @Override
    public void createRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delay, period);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
    }
}
