package com.georgev22.skinoverlay.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods as event handlers for auto-registration.
 * Annotated methods must:
 * <ul>
 *   <li>Have exactly one parameter extending {@link Event}</li>
 *   <li>Return void</li>
 *   <li>Be public</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandlerAnnotation {

    /**
     * The priority of this event handler.
     *
     * @return the handler priority
     */
    HandlerPriority priority() default HandlerPriority.NORMAL;
}
