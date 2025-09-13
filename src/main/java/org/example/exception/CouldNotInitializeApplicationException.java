package org.example.exception;

public class CouldNotInitializeApplicationException extends RuntimeException {

    public CouldNotInitializeApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
