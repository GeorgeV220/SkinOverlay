package com.georgev22.skinoverlay.exceptions;

public class SkinException extends RuntimeException {
    public SkinException(String message) {
        super(message);
    }

    public SkinException(Throwable throwable) {
        super(throwable);
    }
}
