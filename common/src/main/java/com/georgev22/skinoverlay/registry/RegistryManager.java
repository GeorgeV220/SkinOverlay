package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.hooks.SkinHook;
import com.georgev22.skinoverlay.hooks.SkinHookNoop;
import com.georgev22.skinoverlay.hooks.SkinsRestorerHook;
import com.georgev22.skinoverlay.utilities.Keyed;
import com.georgev22.skinoverlay.utilities.NamespacedKey;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class RegistryManager {

    private final SkinOverlay skinOverlay = SkinOverlay.getInstance();

    public void load() {
        registerSkinHooks();
    }

    public void reload() {
        skinOverlay.getLogger().info("Reloading registries...");
        clearRegistries();
        load();
        skinOverlay.getLogger().info("Registry reload complete!");
    }

    private void clearRegistries() {
        clearRegistry(Registries.SKIN_HOOK);
    }

    private <V extends Keyed> void clearRegistry(@NonNull KeyedRegistry<V> registry) {
        registry.entries().keySet().forEach(registry::unregister);
    }

    private void logRegistered(@SuppressWarnings("SameParameterValue") String name, int diff, @NonNull Map<String, NamespacedKey> plugins) {
        skinOverlay.getComponentLogger().info(
                Component.text("[" + name + "/BuiltIn] Registered " + diff + ".")
                        .color(NamedTextColor.GREEN)
        );

        for (Map.Entry<String, NamespacedKey> entry : plugins.entrySet()) {
            skinOverlay.getComponentLogger().info(
                    Component.text("[" + name + "] - ")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.text(entry.getKey())
                                    .color(NamedTextColor.WHITE))
                            .append(Component.text(" - "))
                            .append(Component.text(entry.getValue().toString())
                                    .color(NamedTextColor.AQUA))
            );
        }
    }

    private void registerSkinHooks() {
        SelectableKeyedRegistry<SkinHook> registry = Registries.SKIN_HOOK;
        int before = registry.entries().size();
        SkinHookNoop noop = new SkinHookNoop();
        registry.register(noop);
        if (skinOverlay.getPluginProvider().isPluginEnabled("SkinsRestorer"))
            registry.register(new SkinsRestorerHook());

        logRegistered(
                "Skin Hooks",
                registry.entries().size() - before,
                registry.entries().entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> e.getValue().getName(),
                                Map.Entry::getKey
                        ))
        );

        try {
            registry.setSelected(NamespacedKey.fromString(OptionsUtil.SKIN_HOOK.getStringValue()));
        } catch (Exception ignored) {
            registry.setSelected(noop);
        }

    }
}
