package com.insurance.backoffice.infrastructure.repository.exception;

/**
 * Exception thrown when attempting to create an entity that would violate uniqueness constraints.
 * Used for handling duplicate key violations and similar constraint violations.
 */
public class DuplicateEntityException extends DataAccessException {
    
    private final String entityType;
    private final String field;
    private final Object value;
    
    public DuplicateEntityException(String entityType, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", entityType, field, value));
        this.entityType = entityType;
        this.field = field;
        this.value = value;
    }
    
    public DuplicateEntityException(String entityType, String field, Object value, Throwable cause) {
        super(String.format("%s with %s '%s' already exists", entityType, field, value), cause);
        this.entityType = entityType;
        this.field = field;
        this.value = value;
    }
    
    public DuplicateEntityException(String message) {
        super(message);
        this.entityType = null;
        this.field = null;
        this.value = null;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
}