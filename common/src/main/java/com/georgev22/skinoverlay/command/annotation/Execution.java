package com.georgev22.skinoverlay.command.annotation;

import com.georgev22.skinoverlay.command.ExecutionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Execution {
    ExecutionType value() default ExecutionType.SYNC;
}