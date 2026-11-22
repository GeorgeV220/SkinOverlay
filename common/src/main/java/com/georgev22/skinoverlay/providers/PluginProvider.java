package com.georgev22.skinoverlay.providers;

@FunctionalInterface
public interface PluginProvider {

    /**
     * Checks if a plugin is enabled.
     *
     * @param pluginName the name of the plugin
     * @return true if the plugin is enabled, false otherwise
     */
    boolean isPluginEnabled(String pluginName);

}
