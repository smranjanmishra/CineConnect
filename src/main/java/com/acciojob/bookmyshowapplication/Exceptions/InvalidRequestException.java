package com.acciojob.bookmyshowapplication.Exceptions;

/**
 * Exception thrown when request data is invalid
 */
public class InvalidRequestException extends RuntimeException {
    
    public InvalidRequestException(String message) {
        super(message);
    }
    
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
