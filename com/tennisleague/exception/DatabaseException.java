package com.tennisleague.exception;

/**
 * Exception thrown when database operations fail.
 * Wraps SQLException with user-friendly messages.
 */
public class DatabaseException extends Exception {
    
    /**
     * Constructs a new DatabaseException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
