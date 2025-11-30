package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command class or method as a subcommand of a parent command.
 * <p>
 * This annotation is placed on a class that extends {@link com.georgev22.skinoverlay.command.BaseCommand}
 * or on a method inside a command class to indicate that it is a subcommand.
 * </p>
 *
 * <p>Example usage on a class:</p>
 * <pre>{@code
 * @Subcommand("list")
 * public class ListSubCommand extends BaseCommand {
 *     // Subcommand implementation
 * }
 * }</pre>
 *
 * <p>Example usage on a method:</p>
 * <pre>{@code
 * @Subcommand("set")
 * public void onSet(CommandIssuer sender, String key, String value) {
 *     // Subcommand logic
 * }
 * }</pre>
 *
 * <p>The {@code value()} array defines one or more aliases for the subcommand.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Subcommand {

    /**
     * One or more aliases for the subcommand.
     *
     * @return an array of subcommand names
     */
    String[] value();
}
