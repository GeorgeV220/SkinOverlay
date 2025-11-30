package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a human-readable description for a command class.
 * <p>
 * This annotation is placed on a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand} to define
 * a description that can be used in help menus, logs, or usage messages.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("home")
 * @Description("Allows players to manage and teleport to their homes")
 * public class HomeCommand extends BaseCommand {
 *     // Command implementation
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Description {

    /**
     * The description text for the command.
     *
     * @return the command description
     */
    String value();
}
