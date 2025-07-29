package com.insurance.backoffice.domain;

/**
 * Enumeration representing user roles in the insurance backoffice system.
 * Defines the access levels and permissions for different types of users.
 */
public enum UserRole {
    /**
     * Administrator role with full system access including user management.
     */
    ADMIN,
    
    /**
     * Operator role with access to policy management functions.
     */
    OPERATOR
}