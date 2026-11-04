package com.georgev22.skinoverlay.fabric.command;

import com.georgev22.skinoverlay.command.BaseCommand;
import com.georgev22.skinoverlay.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FabricCommandManager extends CommandManager {

    private final List<BaseCommand> pendingCommands = new ArrayList<>();
    private CommandDispatcher<CommandSourceStack> dispatcher;

    public void bind(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        if (!pendingCommands.isEmpty()) {
            List<BaseCommand> commands = new ArrayList<>(pendingCommands);
            pendingCommands.clear();
            commands.forEach(this::registerCommandNow);
        }
    }

    public void unbind() {
        this.dispatcher = null;
    }

    @Override
    protected void registerCommand0(@NotNull BaseCommand command) {
        if (dispatcher == null) {
            pendingCommands.add(command);
            return;
        }
        registerCommandNow(command);
    }

    private void registerCommandNow(@NotNull BaseCommand command) {
        for (String alias : command.getAliases(false)) {
            if (alias.isBlank()) {
                continue;
            }

            try {
                LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(alias.toLowerCase(Locale.ROOT))
                        .requires(source -> hasPermission(source, command.getPermission()))
                        .executes(context -> execute(command, context, new String[0], alias));

                root.then(Commands.argument("arguments", StringArgumentType.greedyString())
                        .suggests((context, builder) -> suggest(command, context, builder, alias))
                        .executes(context -> execute(
                                command,
                                context,
                                splitArguments(StringArgumentType.getString(context, "arguments")),
                                alias
                        )));

                dispatcher.register(root);
            } catch (RuntimeException exception) {
                skinOverlay.getLogger().log(Level.SEVERE, "Failed to register Fabric command: " + alias, exception);
            }
        }
    }

    private int execute(
            @NotNull BaseCommand command,
            @NotNull CommandContext<CommandSourceStack> context,
            String[] args,
            @NotNull String alias
    ) {
        com.georgev22.skinoverlay.command.CommandContext commandContext =
                new com.georgev22.skinoverlay.command.CommandContext();
        commandContext.getData().set("command", alias);
        command.execute(new FabricCommandIssuer(context.getSource()), args, commandContext);
        return 1;
    }

    private CompletableFuture<Suggestions> suggest(
            @NotNull BaseCommand command,
            @NotNull CommandContext<CommandSourceStack> context,
            @NotNull SuggestionsBuilder builder,
            @NotNull String alias
    ) {
        try {
            return CommandSuggestions.suggest(builder, args -> command.tabComplete(
                    new FabricCommandIssuer(context.getSource()), args));
        } catch (RuntimeException exception) {
            skinOverlay.getLogger().log(Level.WARNING, "Failed to complete Fabric command: " + alias, exception);
        }
        return builder.buildFuture();
    }

    private static String[] splitArguments(String input) {
        if (input == null || input.isBlank()) {
            return new String[0];
        }
        return Arrays.stream(input.trim().split("\\s+"))
                .filter(argument -> !argument.isEmpty())
                .toArray(String[]::new);
    }

    private static boolean hasPermission(CommandSourceStack source, String permission) {
        return permission == null || permission.isBlank() || new FabricCommandIssuer(source).isOp();
    }
}
