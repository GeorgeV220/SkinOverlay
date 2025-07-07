package com.georgev22.skinoverlay.exceptions;

public class ReflectionException extends RuntimeException {

    /**
     * Constructs a new ReflectionException based on the given
     * Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public ReflectionException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new ReflectionException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public ReflectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ReflectionException based on the given
     * Exception
     *
     * @param message   Brief message explaining the cause of the exception
     * @param throwable Exception that triggered this Exception
     */
    public ReflectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
