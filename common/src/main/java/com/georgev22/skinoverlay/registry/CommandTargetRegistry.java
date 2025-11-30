package com.georgev22.skinoverlay.registry;

import com.georgev22.skinoverlay.command.CommandIssuer;
import com.georgev22.skinoverlay.maps.ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Registry for command executor targets.
 * <p>
 * This integrates with the core {@link Registry} system,
 * allowing plugins or modules to register custom sender types
 * (e.g., "discord", "webpanel", etc.).
 * </p>
 *
 * <p>Default targets:</p>
 * <ul>
 *     <li>{@code any} – allows all senders</li>
 *     <li>{@code player} – allows only players</li>
 *     <li>{@code console} – allows only console senders</li>
 *     <li>{@code block} – allows only block command senders</li>
 * </ul>
 */
public final class CommandTargetRegistry extends AbstractRegistry<String, Predicate<CommandIssuer>> {

    private static final CommandTargetRegistry INSTANCE = new CommandTargetRegistry();

    /**
     * Returns the singleton instance of the CommandTargetRegistry.
     */
    public static @NotNull CommandTargetRegistry getInstance() {
        return INSTANCE;
    }

    private CommandTargetRegistry() {
        register(DefaultTargets.ANY, sender -> true);
        register(DefaultTargets.PLAYER, CommandIssuer::isPlayer);
        register(DefaultTargets.CONSOLE, sender -> !sender.isPlayer());
    }

    /**
     * Checks if a sender matches the predicate associated with the given target name.
     *
     * @param targetNames the names of the target (case-insensitive)
     * @param sender      the command sender
     * @return true if the sender matches, false otherwise
     */
    public boolean matches(@NotNull String @NotNull [] targetNames, @NotNull CommandIssuer sender) {
        for (String targetName : targetNames) {
            if (get(targetName.toLowerCase())
                    .map(predicate -> predicate.test(sender))
                    .orElse(false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void register(@NotNull Predicate<CommandIssuer> value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Cannot register a value without a key");
    }

    @Override
    public boolean replaceOrRegister(@NotNull Predicate<CommandIssuer> value) {
        throw new UnsupportedOperationException("Cannot register a value without a key");
    }

    /**
     * @return an unmodifiable view of all registered command targets.
     */
    @Override
    public @NotNull ObjectMap<String, Predicate<CommandIssuer>> entries() {
        return super.entries();
    }

    /**
     * Default command target constants.
     */
    public interface DefaultTargets {
        String ANY = "any";
        String PLAYER = "player";
        String CONSOLE = "console";
    }
}
