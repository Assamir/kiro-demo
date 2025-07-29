package com.insurance.backoffice.infrastructure.repository.exception;

/**
 * Exception thrown when a requested entity is not found in the database.
 * Used for consistent error handling across repository operations.
 */
public class EntityNotFoundException extends DataAccessException {
    
    private final String entityType;
    private final Object identifier;
    
    public EntityNotFoundException(String entityType, Object identifier) {
        super(String.format("%s with identifier '%s' not found", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public EntityNotFoundException(String entityType, Object identifier, Throwable cause) {
        super(String.format("%s with identifier '%s' not found", entityType, identifier), cause);
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getIdentifier() {
        return identifier;
    }
}