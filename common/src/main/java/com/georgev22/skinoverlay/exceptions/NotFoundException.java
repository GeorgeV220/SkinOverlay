package com.georgev22.skinoverlay.exceptions;

public class NotFoundException extends Exception {

    /**
     * Constructs a new NotFoundException based on the given
     * Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public NotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new NotFoundException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotFoundException based on the given
     * Exception
     *
     * @param message   Brief message explaining the cause of the exception
     * @param throwable Exception that triggered this Exception
     */
    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
