package com.example.oktausersvc.exception;

public class UpstreamAuthException extends RuntimeException {
    public UpstreamAuthException(String message) {
        super(message);
    }

    public UpstreamAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
