package com.georgev22.skinoverlay.command.annotation;

import java.lang.annotation.*;

/**
 * Marks a command method as the default entry point for the command
 * when no subcommand or arguments are specified.
 * <p>
 * This annotation is placed on a method inside a class that extends
 * {@link com.georgev22.skinoverlay.command.BaseCommand}.
 * The annotated method will be executed if the command is run
 * without any subcommand or extra arguments.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @CommandAlias("home")
 * public class HomeCommand extends BaseCommand {
 *
 *     @Default
 *     public void onDefault(CommandIssuer sender) {
 *         sender.sendMessage("Usage: /home <set|go|list>");
 *     }
 *
 *     @Subcommand("set")
 *     public void onSet(CommandIssuer sender, String name) {
 *         // set home logic
 *     }
 * }
 * }</pre>
 *
 * <p>Notes:</p>
 * <ul>
 *     <li>There should typically be only one method annotated with {@code @Default} per command class.</li>
 *     <li>The method can accept a {@link com.georgev22.skinoverlay.command.CommandIssuer} parameter
 *     and/or a {@code String[]} parameter for raw arguments.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Default {
}
