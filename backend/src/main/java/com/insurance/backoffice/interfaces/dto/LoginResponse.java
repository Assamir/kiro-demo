package com.insurance.backoffice.interfaces.dto;

import com.insurance.backoffice.domain.UserRole;

/**
 * Data Transfer Object for login responses.
 * Clean Code: Immutable record with clear data structure.
 */
public record LoginResponse(
    String token,
    String email,
    String fullName,
    UserRole role,
    long expiresIn
) {
    /**
     * Creates a login response with token expiration time.
     * Clean Code: Factory method with clear purpose.
     * 
     * @param token JWT token
     * @param email user email
     * @param fullName user full name
     * @param role user role
     * @param expirationMillis token expiration in milliseconds
     * @return LoginResponse instance
     */
    public static LoginResponse of(String token, String email, String fullName, 
                                 UserRole role, long expirationMillis) {
        return new LoginResponse(token, email, fullName, role, expirationMillis);
    }
}