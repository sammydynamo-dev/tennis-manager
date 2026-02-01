package com.tennisleague.exception;

/**
 * Exception thrown when a requested entity does not exist in the database.
 * Used for operations that attempt to retrieve, update, or delete non-existent entities.
 */
public class EntityNotFoundException extends Exception {
    
    /**
     * Constructs a new EntityNotFoundException with the specified detail message.
     * 
     * @param message the detail message describing which entity was not found
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new EntityNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message describing which entity was not found
     * @param cause the cause of this exception
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
