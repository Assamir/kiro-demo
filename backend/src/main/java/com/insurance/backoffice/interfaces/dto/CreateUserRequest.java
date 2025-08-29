package com.insurance.backoffice.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new user.
 */
public record CreateUserRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password,

        @NotBlank(message = "Role is required")
        String role
) {
}