package com.example.stockeasy.exception;

public class InsufficientSharesException extends RuntimeException {

    public InsufficientSharesException(String message) {
        super(message);
    }

    public InsufficientSharesException(String message, Throwable cause) {
        super(message, cause);
    }
}
