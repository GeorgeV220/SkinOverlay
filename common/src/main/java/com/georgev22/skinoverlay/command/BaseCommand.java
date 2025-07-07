package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.utilities.config.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseCommand {
    private String[] aliases;
    private String[] subCommandAliases;
    private String description;
    private String usage;
    private final boolean playerOnly;
    private String permission;
    private ExecutionType executionType;
    private final List<Consumer<CommandIssuer>> preprocessors = new ArrayList<>();
    private final List<BiConsumer<CommandIssuer, CommandContext>> postprocessors = new ArrayList<>();
    private final Map<String, BaseCommand> subcommands = new HashMap<>();

    public BaseCommand() {
        Class<?> clazz = getClass();
        if (clazz.isAnnotationPresent(CommandAlias.class)) {
            aliases = clazz.getAnnotation(CommandAlias.class).value();
        }
        if (clazz.isAnnotationPresent(Subcommand.class)) {
            subCommandAliases = clazz.getAnnotation(Subcommand.class).value();
        }
        if (clazz.isAnnotationPresent(Description.class)) {
            description = clazz.getAnnotation(Description.class).value();
        }
        if (clazz.isAnnotationPresent(Usage.class)) {
            usage = clazz.getAnnotation(Usage.class).value();
        }
        playerOnly = clazz.isAnnotationPresent(PlayerOnly.class);
        if (clazz.isAnnotationPresent(Permission.class)) {
            permission = clazz.getAnnotation(Permission.class).value();
        }
        if (clazz.isAnnotationPresent(Execution.class)) {
            executionType = clazz.getAnnotation(Execution.class).value();
        }
    }

    public void execute(CommandIssuer commandIssuer, String @NotNull [] args) {
        SkinOverlay.getInstance().getCommandManager().getGlobalPreprocessors().forEach(pre -> pre.accept(commandIssuer));
        preprocessors.forEach(pre -> pre.accept(commandIssuer));

        if (args.length > 0) {
            BaseCommand subcommand = subcommands.get(args[0].toLowerCase());
            if (subcommand != null) {
                subcommand.execute(commandIssuer, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        if (playerOnly && !commandIssuer.isPlayer()) {
            MessagesUtil.ONLY_PLAYER_COMMAND.msg(commandIssuer);
            return;
        }

        if (permission != null && !permission.isEmpty() && !commandIssuer.hasPermission(permission)) {
            MessagesUtil.NO_PERMISSION.msg(commandIssuer);
            return;
        }

        CommandContext context = new CommandContext();
        if (executionType == ExecutionType.ASYNC) {
            SkinOverlay.getInstance().getScheduler().runAsyncTask(
                    SkinOverlay.getInstance().getPlugin(),
                    () -> {
                        handle(commandIssuer, args, context);
                        SkinOverlay.getInstance().getCommandManager().getGlobalPostprocessors().forEach(post -> post.accept(commandIssuer, context));
                        postprocessors.forEach(post -> post.accept(commandIssuer, context));
                    }
            );
        } else {
            handle(commandIssuer, args, context);
            SkinOverlay.getInstance().getCommandManager().getGlobalPostprocessors().forEach(post -> post.accept(commandIssuer, context));
            postprocessors.forEach(post -> post.accept(commandIssuer, context));
        }
    }

    public Collection<String> tabComplete(CommandIssuer commandIssuer, String @NotNull [] args) {
        if (args.length > 1) {
            BaseCommand subcommand = subcommands.get(args[0].toLowerCase());
            if (subcommand != null) {
                if (commandIssuer.hasPermission(subcommand.getPermission()))
                    return subcommand.tabComplete(commandIssuer, Arrays.copyOfRange(args, 1, args.length));
                else
                    return Collections.emptyList();
            }
        }

        return CompletionEngine.resolveCompletions(getClass(), commandIssuer, args);
    }

    public void addSubcommand(@NotNull BaseCommand subcommand) {
        for (String alias : subcommand.getAliases(true)) {
            subcommands.put(alias.toLowerCase(), subcommand);
        }
    }

    public void addPreprocessor(Consumer<CommandIssuer> preprocessor) {
        if (preprocessors.contains(preprocessor)) return;
        preprocessors.add(preprocessor);
    }

    public void addPostprocessor(BiConsumer<CommandIssuer, CommandContext> postprocessor) {
        if (postprocessors.contains(postprocessor)) return;
        postprocessors.add(postprocessor);
    }

    public String[] getAliases(boolean isSubcommand) {
        if (isSubcommand) {
            return subCommandAliases;
        }
        return aliases;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    protected abstract void handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context);
}