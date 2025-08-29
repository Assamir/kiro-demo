package com.insurance.backoffice.interfaces.dto;

import com.insurance.backoffice.domain.User;

/**
 * Response DTO for user information.
 */
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}