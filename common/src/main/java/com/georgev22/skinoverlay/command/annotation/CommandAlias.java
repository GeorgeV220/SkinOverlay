package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares one or more aliases for a command class.
 * <p>
 * This annotation is placed on a class that extends {@link com.georgev22.skinoverlay.command.BaseCommand}
 * to define the main command label(s) that can be used to invoke it.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias({"home", "homes"})
 * public class HomeCommand extends BaseCommand {
 *     // Command implementation here
 * }
 * }</pre>
 *
 * <p>In the above example, both {@code /home} and {@code /homes} would
 * execute the {@code HomeCommand}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAlias {

    /**
     * One or more aliases that can be used to invoke the command.
     *
     * @return an array of command aliases
     */
    String[] value();
}
