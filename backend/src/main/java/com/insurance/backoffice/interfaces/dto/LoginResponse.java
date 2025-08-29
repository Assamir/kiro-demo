package com.insurance.backoffice.interfaces.dto;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;

/**
 * Data Transfer Object for login responses.
 * Clean Code: Immutable record with clear data structure.
 */
public record LoginResponse(
    String token,
    UserInfo user,
    long expiresIn
) {
    /**
     * User information nested record.
     */
    public record UserInfo(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
    ) {
        public static UserInfo fromUser(User user) {
            return new UserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
            );
        }
    }

    /**
     * Creates a login response with token expiration time.
     * Clean Code: Factory method with clear purpose.
     * 
     * @param token JWT token
     * @param user user information
     * @param expirationMillis token expiration in milliseconds
     * @return LoginResponse instance
     */
    public static LoginResponse of(String token, User user, long expirationMillis) {
        return new LoginResponse(token, UserInfo.fromUser(user), expirationMillis);
    }

    // Convenience methods for backward compatibility
    public String getToken() {
        return token;
    }

    public UserInfo getUser() {
        return user;
    }
}