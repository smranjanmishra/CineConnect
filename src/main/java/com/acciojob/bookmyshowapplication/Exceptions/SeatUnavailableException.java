package com.acciojob.bookmyshowapplication.Exceptions;

/**
 * Exception thrown when requested seats are not available
 */
public class SeatUnavailableException extends RuntimeException {
    
    public SeatUnavailableException(String message) {
        super(message);
    }
    
    public SeatUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}