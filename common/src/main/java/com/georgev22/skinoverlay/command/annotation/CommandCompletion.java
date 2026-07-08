package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the tab-completion pattern for a command or subcommand.
 * <p>
 * This annotation is placed on a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand} to define
 * the default completion values shown when typing the command.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("teleport")
 * @CommandCompletion("@players @worlds")
 * public class TeleportCommand extends BaseCommand {
 *     // Command implementation
 * }
 * }</pre>
 *
 * <p>In the above example:</p>
 * <ul>
 *   <li>The first argument will suggest online players.</li>
 *   <li>The second argument will suggest available worlds.</li>
 * </ul>
 *
 * <p>
 * Completion values typically reference keys understood by your
 * {@code CompletionEngine}, e.g. {@code @players}, {@code @worlds}, or
 * custom registered completions.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandCompletion {

    /**
     * The completion pattern to apply to this command.
     * <p>
     * Each argument is separated by a space, and may reference a completion key
     * (e.g. {@code @players}) or a literal suggestion.
     * </p>
     *
     * @return the completion pattern string
     */
    String value();
}
