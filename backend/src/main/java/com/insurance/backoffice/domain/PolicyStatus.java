package com.insurance.backoffice.domain;

/**
 * Enumeration representing the status of an insurance policy.
 */
public enum PolicyStatus {
    /**
     * Policy is currently active and providing coverage.
     */
    ACTIVE,
    
    /**
     * Policy has been canceled before its natural expiration.
     */
    CANCELED,
    
    /**
     * Policy has reached its natural expiration date.
     */
    EXPIRED
}