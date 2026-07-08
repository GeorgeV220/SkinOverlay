package com.georgev22.skinoverlay.command;

import com.georgev22.skinoverlay.SkinOverlay;
import com.georgev22.skinoverlay.command.annotation.*;
import com.georgev22.skinoverlay.message.MessageBuilder;
import com.georgev22.skinoverlay.message.Placeholder;
import com.georgev22.skinoverlay.message.messages.CommandMessages;
import com.georgev22.skinoverlay.player.SPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class MethodCommand extends BaseCommand {

    private final BaseCommand parent;
    protected final Method method;
    private final boolean isDefault;

    public MethodCommand(@NotNull BaseCommand parent, @NotNull Method method) {
        this.parent = parent;
        this.method = method;
        this.isDefault = method.isAnnotationPresent(Default.class);

        this.subCommandAliases = Optional.ofNullable(getAnnotation(Subcommand.class, method, parent))
                .map(Subcommand::value)
                .orElse(new String[0]);

        this.permission = Optional.ofNullable(getAnnotation(Permission.class, method, parent))
                .map(Permission::value)
                .orElse("");

        this.description = Optional.ofNullable(getAnnotation(Description.class, method, parent))
                .map(Description::value)
                .orElse("");

        this.usage = Optional.ofNullable(getAnnotation(Usage.class, method, parent))
                .map(Usage::value)
                .orElse("");

        this.targets = Optional.ofNullable(getAnnotation(CommandTarget.class, method, parent))
                .map(CommandTarget::value)
                .orElse(new String[]{"any"});
    }

    @Override
    public void execute(CommandIssuer sender, String @NotNull [] args, CommandContext context) {
        try {
            method.setAccessible(true);
            invokeHandler(sender, args, context);
        } catch (Exception e) {
            MessageBuilder.builder().appendMiniMessage("&cError executing command.").send(sender.audience());
            SkinOverlay.getInstance().getLogger()
                    .log(Level.SEVERE, "Error while executing command", e);
        }
    }

    public BaseCommand getParent() {
        return parent;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Collection<String> tabComplete(CommandIssuer sender, String @NotNull [] args) {
        Collection<String> completions = CompletionEngine.resolveCompletions(method, sender, args);
        if (!completions.isEmpty()) {
            return completions;
        }

        return CompletionEngine.resolveCompletions(method.getDeclaringClass(), sender, args);
    }

    private void invokeHandler(@NotNull CommandIssuer sender, String[] args, CommandContext context) throws Exception {
        List<Object> resolved = new ArrayList<>();
        int argIndex = 0;

        for (Parameter param : method.getParameters()) {
            Class<?> type = param.getType();

            if (type.equals(CommandIssuer.class)) {
                resolved.add(sender);
            } else if (type.equals(CommandContext.class)) {
                resolved.add(context);
            } else if (type.equals(String[].class)) {
                resolved.add(args);
            } else if (param.isAnnotationPresent(Argument.class)) {
                Argument argAnno = param.getAnnotation(Argument.class);
                String argName = !argAnno.name().isEmpty() ? argAnno.name() : param.getName();
                String raw = argIndex < args.length ? args[argIndex] : null;

                if (raw == null || raw.isEmpty()) {
                    if (!argAnno.optional() && argAnno.defaultValue().isEmpty()) {
                        CommandMessages.COMMAND_MISSING_ARGUMENT.msg(sender,
                                Placeholder.builder(sender.audience())
                                        .placeholder("%command%", context.getData().getOr("command", ""))
                                        .placeholder("%arg%", argName).build());
                        return;
                    }
                    raw = argAnno.defaultValue();
                }

                Object value = convertArgument(raw, type);

                if (value == null) {
                    CommandMessages.COMMAND_INVALID_ARGUMENT.msg(sender,
                            Placeholder.builder(sender.audience())
                                    .placeholder("%command%", context.getData().getOr("command", ""))
                                    .placeholder("%arg%", argName).build());
                    return;
                }

                // Resolve with completion key if present
//                if (!argAnno.completion().isEmpty()) {
//                    var resolver = CompletionEngine.resolve(argAnno.completion());
//                    if (resolver != null) value = resolver.resolveValue(sender, raw);
//                }

                resolved.add(value);
                argIndex++;
            } else {
                // fallback: raw string
                resolved.add(argIndex < args.length ? args[argIndex++] : null);
            }
        }

        method.invoke(parent, resolved.toArray());
    }

    /**
     * Converts a raw string argument into a typed object.
     *
     * @param raw  the raw argument
     * @param type the target type
     * @return the converted object or null if conversion failed
     */
    protected @Nullable Object convertArgument(@NotNull String raw, @NotNull Class<?> type) {
        try {
            // primitives & String
            if (type.equals(String.class)) return raw;
            if (type.equals(int.class) || type.equals(Integer.class)) return Integer.parseInt(raw);
            if (type.equals(double.class) || type.equals(Double.class)) return Double.parseDouble(raw);
            if (type.equals(boolean.class) || type.equals(Boolean.class)) return Boolean.parseBoolean(raw);
            if (type.equals(BigInteger.class)) return new BigInteger(raw);
            if (type.equals(BigDecimal.class)) return new BigDecimal(raw);

            if (type.equals(SPlayer.class)) return SkinOverlay.getInstance().getPlayerProvider().getSPlayer(raw);

            // Fallback: raw string
            return raw;
        } catch (Exception e) {
            return null;
        }
    }
}
