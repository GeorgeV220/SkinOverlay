package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a usage string for a command, showing the correct syntax.
 * <p>
 * This annotation is placed on a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand} to indicate
 * how the command should be executed. It is typically displayed
 * when a user runs the command incorrectly or requests help.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("home")
 * @Usage("/home <set|go|list> [name]")
 * public class HomeCommand extends BaseCommand {
 *     // Command implementation
 * }
 * }</pre>
 *
 * <p>The usage string is purely informational and does not enforce argument validation.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Usage {

    /**
     * The usage string describing the command syntax.
     *
     * @return the command usage
     */
    String value();
}
