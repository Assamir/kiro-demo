package com.insurance.backoffice.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for login requests.
 * Clean Code: Immutable record with validation annotations.
 */
public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {
    /**
     * Constructor with validation.
     * Clean Code: Fail-fast validation in constructor.
     */
    public LoginRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}