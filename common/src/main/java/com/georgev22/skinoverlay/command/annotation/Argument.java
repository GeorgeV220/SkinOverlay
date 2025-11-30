package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.*;

/**
 * Marks a method parameter as a command argument with optional metadata.
 * <p>
 * This annotation is placed on parameters of methods inside a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand}. It allows specifying
 * tab-completion hints, optional arguments, default values, and an explicit
 * argument name.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @Subcommand("teleport")
 * public void onTeleport(
 *         CommandIssuer sender,
 *         @Argument(name = "target", completion = "@players") String target,
 *         @Argument(name = "world", completion = "@worlds", optional = true, defaultValue = "world") String worldName
 * ) {
 *     sender.sendMessage("Teleporting " + target + " to " + worldName + "!");
 * }
 * }</pre>
 *
 * <p>Notes:</p>
 * <ul>
 *     <li>{@code name}: the display name for this argument (used in error messages, usage, etc).</li>
 *     <li>{@code completion}: the key used by the {@code CompletionEngine} to suggest values.</li>
 *     <li>{@code optional}: whether this argument can be omitted.</li>
 *     <li>{@code defaultValue}: the value to use if the argument is optional and not provided.</li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Argument {

    /**
     * The explicit name of the argument, used for display (e.g., in usage messages).
     * If left empty, frameworks may fall back to reflection (param.getName()).
     *
     * @return the argument name
     */
    String name() default "";

    /**
     * The completion key for this argument (e.g., "@players", "@worlds").
     *
     * @return the completion key string
     */
    String completion() default "";

    /**
     * Whether this argument is optional.
     *
     * @return true if the argument is optional, false otherwise
     */
    boolean optional() default false;

    /**
     * The default value to use if the argument is optional and not provided.
     *
     * @return the default value string
     */
    String defaultValue() default "";
}
