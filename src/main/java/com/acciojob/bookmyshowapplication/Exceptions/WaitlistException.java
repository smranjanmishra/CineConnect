package com.acciojob.bookmyshowapplication.Exceptions;

/**
 * Exception thrown for waitlist-related errors
 */
public class WaitlistException extends RuntimeException {
    
    public WaitlistException(String message) {
        super(message);
    }
    
    public WaitlistException(String message, Throwable cause) {
        super(message, cause);
    }
}
