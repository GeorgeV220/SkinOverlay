package com.georgev22.skinoverlay.hooks.placeholder;

import net.kyori.adventure.audience.Audience;

/**
 * Represents a placeholder provider that can integrate with external
 * placeholder systems and resolve placeholders in text.
 * <p>
 * Implementations are responsible for handling registration and
 * unregistration with the underlying placeholder service, as well as
 * parsing and replacing placeholders for a given audience.
 */
public interface PlaceholderHook {

    /**
     * Registers this placeholder hook with its underlying placeholder system.
     *
     * @return {@code true} if the hook was successfully registered,
     *         otherwise {@code false}
     */
    boolean register();

    /**
     * Unregisters this placeholder hook from its underlying placeholder system.
     *
     * @return {@code true} if the hook was successfully unregistered,
     *         otherwise {@code false}
     */
    boolean unregister();

    /**
     * Checks whether this placeholder hook is currently registered.
     *
     * @return {@code true} if the hook is registered, otherwise {@code false}
     */
    boolean isRegistered();

    /**
     * Resolves placeholders contained within the specified input string
     * for the provided audience.
     * <p>
     * Implementations may use audience-specific data when replacing
     * placeholders. If no placeholders are found, the original input
     * may be returned unchanged.
     *
     * @param audience the audience for which placeholders should be resolved
     * @param input the input text containing placeholders
     * @return the input text with all applicable placeholders resolved
     */
    String resolve(Audience audience, String input);

}