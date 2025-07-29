package com.insurance.backoffice.application.service;

/**
 * Exception thrown when premium calculation fails.
 * Clean Code: Specific exception type for premium calculation errors.
 */
public class PremiumCalculationException extends RuntimeException {
    
    public PremiumCalculationException(String message) {
        super(message);
    }
    
    public PremiumCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}