package com.insurance.backoffice.infrastructure.repository.exception;

/**
 * Base exception class for data access operations.
 * Provides a common base for all repository-related exceptions.
 */
public class DataAccessException extends RuntimeException {
    
    public DataAccessException(String message) {
        super(message);
    }
    
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}