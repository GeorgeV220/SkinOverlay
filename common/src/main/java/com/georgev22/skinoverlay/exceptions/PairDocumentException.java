package com.georgev22.skinoverlay.exceptions;

public class PairDocumentException extends RuntimeException {

    /**
     * Constructs a new PairDocumentException based on the given
     * Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public PairDocumentException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new PairDocumentException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public PairDocumentException(String message) {
        super(message);
    }

    /**
     * Constructs a new PairDocumentException based on the given
     * Exception
     *
     * @param message   Brief message explaining the cause of the exception
     * @param throwable Exception that triggered this Exception
     */
    public PairDocumentException(String message, Throwable throwable) {
        super(message, throwable);
    }


}
