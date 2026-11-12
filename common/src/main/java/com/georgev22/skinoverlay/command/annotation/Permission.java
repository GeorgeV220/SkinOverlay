package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the permission node required to execute a command.
 * <p>
 * This annotation is placed on a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand} to specify
 * which permission a user must have to run the command.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("fly")
 * @Permission("core.fly")
 * public class FlyCommand extends BaseCommand {
 *     // Command implementation
 * }
 * }</pre>
 *
 * <p>If the permission is not set or is an empty string, the command
 * will be available to all users.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Permission {

    /**
     * The permission node required to execute the command.
     *
     * @return the permission string
     */
    String value();
}
