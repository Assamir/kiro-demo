package com.insurance.backoffice.application.service;

/**
 * Exception thrown when a requested entity is not found.
 * Clean Code: Specific exception type for clear error handling.
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}