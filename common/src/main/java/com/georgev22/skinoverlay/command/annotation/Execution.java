package com.georgev22.skinoverlay.command.annotation;

import com.georgev22.skinoverlay.command.ExecutionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the execution type of the command.
 * <p>
 * This annotation is placed on a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand} to indicate
 * whether the command should run synchronously (SYNC) or asynchronously (ASYNC).
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("backup")
 * @Execution(ExecutionType.ASYNC)
 * public class BackupCommand extends BaseCommand {
 *     // Command implementation
 * }
 * }</pre>
 *
 * <p>By default, if this annotation is not present, the command runs synchronously.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Execution {

    /**
     * The execution type of the command.
     *
     * @return {@link ExecutionType#SYNC} or {@link ExecutionType#ASYNC}
     */
    ExecutionType value() default ExecutionType.SYNC;
}
