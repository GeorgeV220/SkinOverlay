package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.processors.PostProcessor;
import com.georgev22.skinoverlay.command.processors.PreProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Central manager for registering and handling commands in the Core plugin.
 * <p>
 * Supports registration of BaseCommand instances, nested subcommands, global preprocessors
 * and postprocessors, and argument resolvers for tab completion and value parsing.
 */
public abstract class CommandManager {

    private final List<PreProcessor> globalPreprocessors = new ArrayList<>();
    private final List<PostProcessor> globalPostprocessors = new ArrayList<>();
    private final SkinOverlay plugin = SkinOverlay.getInstance();

    protected CommandManager() {
        // private constructor for singleton pattern
    }

    /**
     * Registers a {@link BaseCommand} and all its nested subcommands.
     *
     * @param command the command to register
     */
    public void registerCommand(@NotNull BaseCommand command) {
        try {
            registerCommand0(command);

            for (Class<?> innerClass : command.getClass().getDeclaredClasses()) {
                if (!BaseCommand.class.isAssignableFrom(innerClass)) continue;
                if (!innerClass.isAnnotationPresent(Subcommand.class)) continue;

                BaseCommand subcommand = (BaseCommand) innerClass.getDeclaredConstructor().newInstance();
                command.addSubcommand(subcommand);

                if (innerClass.isAnnotationPresent(CommandAlias.class)) {
                    registerCommand0(subcommand);
                }

                registerCommand(subcommand);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to register command: " + command.getClass().getName(), e);
        }
    }

    /**
     * Registers a single command.
     *
     * @param command the command to register
     */
    protected abstract void registerCommand0(@NotNull BaseCommand command);

    /**
     * Adds a global preprocessor that runs before any command execution.
     *
     * @param preprocessor the preprocessor to add
     */
    public void addGlobalPreprocessor(PreProcessor preprocessor) {
        globalPreprocessors.add(preprocessor);
    }

    /**
     * Adds a global postprocessor that runs after any command execution.
     *
     * @param postprocessor the postprocessor to add
     */
    public void addGlobalPostprocessor(PostProcessor postprocessor) {
        globalPostprocessors.add(postprocessor);
    }

    /**
     * Returns a list of all global postprocessors.
     *
     * @return the global postprocessors
     */
    public List<PostProcessor> getGlobalPostprocessors() {
        return globalPostprocessors;
    }

    /**
     * Returns a list of all global preprocessors.
     *
     * @return the global preprocessors
     */
    public List<PreProcessor> getGlobalPreprocessors() {
        return globalPreprocessors;
    }
}
