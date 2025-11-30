package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.command.processors.PostProcessor;
import com.georgev22.skinoverlay.command.processors.PreProcessor;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.registry.CommandTargetRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Base class for all commands in the Core plugin.
 * <p>
 * Supports:
 * <ul>
 *     <li>Subcommands (nested BaseCommand or method-based)</li>
 *     <li>Pre- and post-processing hooks</li>
 *     <li>Permission checks</li>
 *     <li>Player-only enforcement</li>
 *     <li>Automatic argument conversion (primitives, String, Bukkit types, custom resolvers)</li>
 *     <li>Tab-completion using ArgumentResolvers or default completion engine</li>
 * </ul>
 */
@SuppressWarnings("unused")
public abstract class BaseCommand {

    private String[] aliases;
    protected String[] subCommandAliases;
    protected String description;
    protected String usage;
    protected String[] targets;
    protected String permission;
    private ExecutionType executionType;

    private final List<PreProcessor> preprocessors = new ArrayList<>();
    private final List<PostProcessor> postprocessors = new ArrayList<>();
    private final Map<String, BaseCommand> subcommands = new HashMap<>();

    /**
     * Constructs a new BaseCommand, scanning annotations for metadata and subcommands.
     */
    public BaseCommand() {
        Class<?> clazz = getClass();

        CommandAlias alias = getAnnotation(CommandAlias.class, null, clazz);
        if (alias != null) aliases = alias.value();

        Subcommand sub = getAnnotation(Subcommand.class, null, clazz);
        if (sub != null) subCommandAliases = sub.value();

        Description desc = getAnnotation(Description.class, null, clazz);
        if (desc != null) description = desc.value();

        Usage usageAnn = getAnnotation(Usage.class, null, clazz);
        if (usageAnn != null) usage = usageAnn.value();

        CommandTarget targetAnn = getAnnotation(CommandTarget.class, null, clazz);
        targets = (targetAnn != null) ? targetAnn.value() : new String[]{"any"};

        Permission perm = getAnnotation(Permission.class, null, clazz);
        if (perm != null) permission = perm.value();

        Execution exec = getAnnotation(Execution.class, null, clazz);
        if (exec != null) executionType = exec.value();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                this.addSubcommand(new MethodCommand(this, method));
            } else if (method.isAnnotationPresent(Default.class)) {
                // Default handler (root or inside a subcommand)
                MethodCommand cmd = new MethodCommand(this, method);
                subcommands.put("__default__", cmd);
            }
        }
    }

    /**
     * Executes this command with the given sender and arguments.
     * Handles pre-processing, permission checks, async execution, and subcommand delegation.
     *
     * @param sender the command issuer
     * @param args   the command arguments
     */
    public void execute(CommandIssuer sender, String @NotNull [] args, CommandContext context) {
        // Run global and local preprocessors
        SkinOverlay.getInstance().getCommandManager().getGlobalPreprocessors().forEach(pre -> pre.process(() -> this, sender, context));
        preprocessors.forEach(pre -> pre.process(() -> this, sender, context));

        // Player-only check
        if (!CommandTargetRegistry.getInstance().matches(targets, sender)) {
            CommandMessages.COMMAND_TARGET_DENIED.msg(sender,
                    new HashObjectMap<String, String>()
                            .append("%command%", context.getData().getOr("command", ""))
                            .append("%targets%", String.join(", ", targets)),
                    true);
            return;
        }

        // Permission check
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            CommandMessages.COMMAND_PERMISSION_DENIED.msg(sender,
                    new HashObjectMap<String, String>()
                            .append("%command%", context.getData().getOr("command", "")),
                    true);
            return;
        }

        Runnable task = () -> {
            try {
                if (!executeSubcommand(sender, args, context)) {
                    // If no subcommand found, run "default" (if any)
                    BaseCommand def = subcommands.get("__default__");
                    if (def != null) {
                        def.execute(sender, args, context);
                    } else if (!handle(sender, args, context)) {
                        CommandMessages.COMMAND_USAGE.msg(sender,
                                new HashObjectMap<String, String>()
                                        .append("%command%", context.getData().getOr("command", ""))
                                        .append("%usage%", usage),
                                true);
                    }
                }

                // Run postprocessors
                runPostprocessors(sender, context);
            } catch (Exception e) {
                CommandMessages.COMMAND_ERROR.msg(sender,
                        new HashObjectMap<String, String>()
                                .append("%command%", context.getData().getOr("command", "")),
                        true);
                SkinOverlay.getInstance().getLogger()
                        .log(Level.WARNING, "Error while executing command", e);
            }
        };

        if (executionType == ExecutionType.ASYNC) {
            SkinOverlay.getInstance().getScheduler()
                    .runAsyncTask(SkinOverlay.getInstance(), task);
        } else {
            task.run();
        }
    }

    /**
     * Executes a subcommand (nested BaseCommand or method-based) if it exists.
     *
     * @param sender the command issuer
     * @param args   the arguments
     * @return true if a subcommand was executed, false otherwise
     */
    private boolean executeSubcommand(CommandIssuer sender, String[] args, CommandContext context) {
        if (args.length == 0) return false;

        BaseCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub != null) {
            sub.execute(sender, Arrays.copyOfRange(args, 1, args.length), context);
            return true;
        }
        return false;
    }

    /**
     * Runs post-processing hooks after command execution.
     *
     * @param sender  the command issuer
     * @param context the command context
     */
    private void runPostprocessors(CommandIssuer sender, CommandContext context) {
        SkinOverlay.getInstance().getCommandManager().getGlobalPostprocessors().forEach(post -> post.process(() -> this, sender, context));
        postprocessors.forEach(post -> post.process(() -> this, sender, context));
    }

    /**
     * Provides tab-completion suggestions for this command.
     *
     * @param sender the command issuer
     * @param args   the current arguments
     * @return a collection of completion strings
     */
    public Collection<String> tabComplete(CommandIssuer sender, String @NotNull [] args) {
        if (args.length == 0) {
            // Try default handler if there’s no explicit subcommand
            MethodCommand defaultCmd = (MethodCommand) subcommands.get("__default__");
            if (defaultCmd != null) {
                return defaultCmd.tabComplete(sender, args);
            }
            return getAvailableSubcommands(sender);
        }

        if (args.length == 1) {
            String current = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();

            for (String alias : subcommands.keySet()) {
                if (!alias.equalsIgnoreCase("__default__") && alias.startsWith(current)) {
                    BaseCommand sub = subcommands.get(alias);
                    if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
                        matches.add(alias);
                    }
                }
            }

            if (!matches.isEmpty()) {
                return matches;
            }

            MethodCommand defaultCmd = (MethodCommand) subcommands.get("__default__");
            if (defaultCmd != null) {
                return defaultCmd.tabComplete(sender, args);
            }

            return getAvailableSubcommands(sender);
        }

        BaseCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return sub.tabComplete(sender, subArgs);
        }

        if (this instanceof MethodCommand methodCmd) {
            return methodCmd.tabComplete(sender, args);
        }

        MethodCommand defaultCmd = (MethodCommand) subcommands.get("__default__");
        if (defaultCmd != null) {
            return defaultCmd.tabComplete(sender, args);
        }

        return CompletionEngine.resolveCompletions(getClass(), sender, args);
    }

    /**
     * Lists all available subcommands for this command for tab-completion.
     */
    private @NotNull Collection<String> getAvailableSubcommands(CommandIssuer sender) {
        List<String> available = new ArrayList<>();
        for (Map.Entry<String, BaseCommand> entry : subcommands.entrySet()) {
            String alias = entry.getKey();
            BaseCommand sub = entry.getValue();

            if ("__default__".equals(alias)) continue;
            if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                available.add(alias);
            }
        }
        return available;
    }

    /**
     * Adds a nested BaseCommand as a subcommand.
     *
     * @param subCommand the subcommand to add
     */
    public void addSubcommand(@NotNull BaseCommand subCommand) {
        if (subCommand.subCommandAliases.length == 0 && !(subCommand instanceof MethodCommand)) {
            throw new IllegalArgumentException("Subcommand must define at least one alias");
        }
        for (String alias : subCommand.subCommandAliases) {
            subcommands.put(alias.toLowerCase(), subCommand);
        }
        if (subCommand instanceof MethodCommand && ((MethodCommand) subCommand).isDefault()) {
            subCommand.subcommands.put("__default__", subCommand);
        }
        if (subCommand.aliases != null && subCommand.aliases.length != 0)
            SkinOverlay.getInstance().getCommandManager().registerCommand(subCommand);
    }

    /**
     * Adds a preprocessor for this command.
     *
     * @param preprocessor the preprocessor to add
     */
    public void addPreprocessor(PreProcessor preprocessor) {
        if (!preprocessors.contains(preprocessor)) preprocessors.add(preprocessor);
    }

    /**
     * Adds a postprocessor for this command.
     *
     * @param postprocessor the postprocessor to add
     */
    public void addPostprocessor(PostProcessor postprocessor) {
        if (!postprocessors.contains(postprocessor)) postprocessors.add(postprocessor);
    }

    /**
     * Returns aliases for this command or subcommand.
     *
     * @param isSubcommand true if the aliases are for a subcommand
     */
    public @NotNull String[] getAliases(boolean isSubcommand) {
        return isSubcommand ? subCommandAliases == null ? new String[0] : subCommandAliases : aliases == null ? new String[0] : aliases;
    }

    /**
     * Returns usage string, if defined.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Returns permission string, if defined.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Returns command description, if defined.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the command execution type (SYNC or ASYNC).
     */
    public ExecutionType getExecutionType() {
        return executionType;
    }

    /**
     * Handles the execution of this command.
     * <p>
     * This method is invoked when the command is executed and there is no specific method
     * annotated with {@link Default} to handle it. Implementations of this method should
     * process the provided command arguments and perform the desired action.
     * </p>
     * <p>
     * Use this method for general command handling logic when a dedicated default handler
     * method is not present.
     * </p>
     *
     * @param commandIssuer the sender of the command, representing either a player or console
     * @param args          the arguments passed to the command
     * @param context       the {@link CommandContext} containing custom data or state for this execution
     * @deprecated Annotate a method with {@link Default} instead.
     */
    @Deprecated
    protected boolean handle(@NotNull CommandIssuer commandIssuer, String @NotNull [] args, @NotNull CommandContext context) {
        if (getUsage() != null) {
            CommandMessages.COMMAND_USAGE.msg(commandIssuer,
                    new HashObjectMap<String, String>()
                            .append("%command%", context.getData().getOr("command", ""))
                            .append("%usage%", usage),
                    true);
        }
        return false;
    }

    static <A extends Annotation> A getAnnotation(Class<A> annotationClass, @Nullable Method method, Object parent) {
        A ann = method == null ? null : method.getAnnotation(annotationClass);
        if (ann == null) {
            if (parent instanceof Class<?>) {
                ann = ((Class<?>) parent).getAnnotation(annotationClass);
            } else {
                ann = parent.getClass().getAnnotation(annotationClass);
            }
        }
        return ann;
    }

}