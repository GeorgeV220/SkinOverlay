package com.georgev22.skinoverlay.command.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies which executor type(s) can execute this command.
 * <p>
 * The value refers to a target name registered in the
 * {@link com.georgev22.skinoverlay.registry.CommandTargetRegistry}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandTarget {
    /**
     * The name of the target (e.g. "player", "console", "any", etc.).
     */
    String[] value() default {"any"};
}
