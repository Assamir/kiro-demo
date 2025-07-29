package com.insurance.backoffice.infrastructure.repository.exception;

/**
 * Exception thrown when a repository operation fails due to database or infrastructure issues.
 * Used for wrapping low-level database exceptions with meaningful context.
 */
public class RepositoryOperationException extends DataAccessException {
    
    private final String operation;
    private final String entityType;
    
    public RepositoryOperationException(String operation, String entityType, String message) {
        super(String.format("Failed to %s %s: %s", operation, entityType, message));
        this.operation = operation;
        this.entityType = entityType;
    }
    
    public RepositoryOperationException(String operation, String entityType, String message, Throwable cause) {
        super(String.format("Failed to %s %s: %s", operation, entityType, message), cause);
        this.operation = operation;
        this.entityType = entityType;
    }
    
    public RepositoryOperationException(String operation, String entityType, Throwable cause) {
        super(String.format("Failed to %s %s", operation, entityType), cause);
        this.operation = operation;
        this.entityType = entityType;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getEntityType() {
        return entityType;
    }
}