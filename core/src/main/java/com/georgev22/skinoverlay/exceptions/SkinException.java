package com.georgev22.skinoverlay.exceptions;

public class SkinException extends RuntimeException {

    public SkinException(String message) {
        super(message);
    }

    public SkinException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkinException(Throwable cause) {
        super(cause);
    }

    public SkinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}