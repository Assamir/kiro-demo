package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 * Clean Code: Thin controller focused on HTTP concerns with role-based security.
 * Admin-only access enforced through @PreAuthorize annotations.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management endpoints (Admin only)")
public class UserController {
    
    /**
     * Retrieves all users in the system.
     * Clean Code: Simple endpoint with clear authorization.
     * 
     * @return list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Retrieves a specific user by ID.
     * Clean Code: RESTful endpoint with path variable.
     * 
     * @param id user ID
     * @return user details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve specific user by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Creates a new user.
     * Clean Code: POST endpoint with request body validation.
     * 
     * @param request user creation request
     * @return created user details
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.status(201).build();
    }
    
    /**
     * Updates an existing user.
     * Clean Code: PUT endpoint for full resource update.
     * 
     * @param id user ID
     * @param request user update request
     * @return updated user details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update existing user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, 
                                                 @RequestBody UpdateUserRequest request) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Deletes a user.
     * Clean Code: DELETE endpoint with clear purpose.
     * 
     * @param id user ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Data Transfer Object for user response.
     * Clean Code: Inner record for response data structure.
     */
    public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        com.insurance.backoffice.domain.UserRole role
    ) {}
    
    /**
     * Data Transfer Object for user creation request.
     * Clean Code: Inner record for request data structure.
     */
    public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        com.insurance.backoffice.domain.UserRole role
    ) {}
    
    /**
     * Data Transfer Object for user update request.
     * Clean Code: Inner record for update data structure.
     */
    public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        com.insurance.backoffice.domain.UserRole role
    ) {}
}