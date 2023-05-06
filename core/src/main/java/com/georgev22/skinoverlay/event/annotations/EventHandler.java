package com.georgev22.skinoverlay.event.annotations;

import com.georgev22.skinoverlay.event.Event;
import com.georgev22.skinoverlay.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as an event handler.
 * The method must have a single parameter that is a subclass of
 * the {@link Event} class.
 * By default, the priority is set to {@link EventPriority#NORMAL} and cancellation of the
 * event is not ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * The priority of the event handler.
     * Events are processed in order from lowest to highest priority.
     * By default, the
     * priority is set to {@link EventPriority#NORMAL}.
     *
     * @return the priority of the event handler.
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Define if the handler ignores a cancelled event.
     * <p>
     * If ignoreCancelled is true and the event is cancelled, the method is
     * not called.
     * Otherwise, the method is always called.
     *
     * @return whether cancelled events should be ignored
     */
    boolean ignoreCancelled() default false;
}
