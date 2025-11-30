package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.utilities.CustomData;

/**
 * Represents the context of a command execution.
 * <p>
 * Provides a place to store custom data related to the current command execution,
 * which can be used by preprocessors, postprocessors, or the command itself.
 */
public class CommandContext {

    /**
     * Stores custom data associated with this command execution.
     */
    private final CustomData customData = new CustomData();

    /**
     * Returns the custom data container for this command execution.
     *
     * @return the {@link CustomData} object
     */
    public CustomData getData() {
        return customData;
    }
}
