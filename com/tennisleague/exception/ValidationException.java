package com.tennisleague.exception;

/**
 * Exception thrown when input validation fails.
 * Indicates that user-provided data does not meet validation requirements.
 */
public class ValidationException extends Exception {
    
    /**
     * Constructs a new ValidationException with the specified detail message.
     * 
     * @param message the detail message describing the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     * 
     * @param message the detail message describing the validation failure
     * @param cause the cause of this exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
