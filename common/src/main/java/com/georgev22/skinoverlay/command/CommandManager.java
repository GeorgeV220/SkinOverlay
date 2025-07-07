package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.CommandAlias;
import com.georgev22.skinoverlay.command.annotation.Subcommand;
import com.georgev22.skinoverlay.command.resolvers.ArgumentResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class CommandManager {

    protected final SkinOverlay skinOverlay = SkinOverlay.getInstance();
    private final List<Consumer<CommandIssuer>> globalPreprocessors = new ArrayList<>();
    private final List<BiConsumer<CommandIssuer, CommandContext>> globalPostprocessors = new ArrayList<>();
    private final Map<String, ArgumentResolver> resolvers = new HashMap<>();

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
            }
        } catch (Exception e) {
            this.skinOverlay.getLogger().log(Level.SEVERE, "Failed to register command: " + command.getClass().getName(), e);
        }
    }

    protected abstract void registerCommand0(@NotNull BaseCommand command) throws ReflectiveOperationException;

    public void addGlobalPreprocessor(Consumer<CommandIssuer> preprocessor) {
        globalPreprocessors.add(preprocessor);
    }

    public void addGlobalPostprocessor(BiConsumer<CommandIssuer, CommandContext> postprocessor) {
        globalPostprocessors.add(postprocessor);
    }

    public List<BiConsumer<CommandIssuer, CommandContext>> getGlobalPostprocessors() {
        return globalPostprocessors;
    }

    public List<Consumer<CommandIssuer>> getGlobalPreprocessors() {
        return globalPreprocessors;
    }

    public ArgumentResolver getResolver(@NotNull String key) {
        return resolvers.get(key.toLowerCase());
    }

    public void addResolver(@NotNull String key, ArgumentResolver resolver) {
        resolvers.put(key.toLowerCase(), resolver);
    }
}