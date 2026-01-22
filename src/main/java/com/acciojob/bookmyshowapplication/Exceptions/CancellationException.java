package com.acciojob.bookmyshowapplication.Exceptions;

/**
 * Exception thrown for ticket cancellation errors
 */
public class CancellationException extends RuntimeException {
    
    public CancellationException(String message) {
        super(message);
    }
    
    public CancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}
