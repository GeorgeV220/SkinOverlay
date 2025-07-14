package com.georgev22.skinoverlay.exceptions;

/**
 * Custom runtime exception indicating encryption or decryption failure during message processing.
 */
public class MessageEncryptionException extends RuntimeException {
    public MessageEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}