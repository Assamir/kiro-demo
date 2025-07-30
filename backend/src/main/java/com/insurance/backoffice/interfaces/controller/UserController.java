package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.application.service.EntityNotFoundException;
import com.insurance.backoffice.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final com.insurance.backoffice.application.service.UserService userService;
    
    public UserController(com.insurance.backoffice.application.service.UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Retrieves all users in the system.
     * Clean Code: Simple endpoint with clear authorization.
     * 
     * @return list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users", 
        description = "Retrieve all users in the system. Only accessible by Admin users.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Users retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse[].class),
                    examples = @ExampleObject(
                        name = "Users List",
                        value = """
                        [
                          {
                            "id": 1,
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@example.com",
                            "role": "OPERATOR"
                          },
                          {
                            "id": 2,
                            "firstName": "Jane",
                            "lastName": "Smith",
                            "email": "jane.smith@example.com",
                            "role": "ADMIN"
                          }
                        ]
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        }
    )
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .toList();
        return ResponseEntity.ok(userResponses);
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
    @Operation(
        summary = "Get user by ID", 
        description = "Retrieve specific user by ID. Only accessible by Admin users.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject(
                        name = "User Details",
                        value = """
                        {
                          "id": 1,
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "role": "OPERATOR"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        }
    )
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            User user = userService.findUserById(id);
            UserResponse userResponse = mapToUserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(
        summary = "Create user", 
        description = "Create a new user in the system. Only accessible by Admin users.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User creation data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateUserRequest.class),
                examples = @ExampleObject(
                    name = "Create User Request",
                    value = """
                    {
                      "firstName": "John",
                      "lastName": "Doe",
                      "email": "john.doe@example.com",
                      "password": "SecurePassword123!",
                      "role": "OPERATOR"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "User created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject(
                        name = "Created User",
                        value = """
                        {
                          "id": 1,
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "role": "OPERATOR"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        }
    )
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = mapToUser(request);
        User createdUser = userService.createUser(user);
        UserResponse userResponse = mapToUserResponse(createdUser);
        return ResponseEntity.status(201).body(userResponse);
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
    @Operation(
        summary = "Update user", 
        description = "Update existing user information. Only accessible by Admin users.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User update data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateUserRequest.class),
                examples = @ExampleObject(
                    name = "Update User Request",
                    value = """
                    {
                      "firstName": "John",
                      "lastName": "Smith",
                      "email": "john.smith@example.com",
                      "role": "ADMIN"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User updated successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject(
                        name = "Updated User",
                        value = """
                        {
                          "id": 1,
                          "firstName": "John",
                          "lastName": "Smith",
                          "email": "john.smith@example.com",
                          "role": "ADMIN"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        }
    )
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User user = mapToUserForUpdate(request);
            User updatedUser = userService.updateUser(id, user);
            UserResponse userResponse = mapToUserResponse(updatedUser);
            return ResponseEntity.ok(userResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(
        summary = "Delete user", 
        description = "Delete user from the system. Only accessible by Admin users.",
        responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Data Transfer Object for user response.
     * Clean Code: Inner record for response data structure.
     */
    @Schema(description = "User information response")
    public record UserResponse(
        @Schema(description = "User ID", example = "1")
        Long id,
        
        @Schema(description = "User first name", example = "John")
        String firstName,
        
        @Schema(description = "User last name", example = "Doe")
        String lastName,
        
        @Schema(description = "User email address", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "User role", example = "OPERATOR")
        com.insurance.backoffice.domain.UserRole role
    ) {}
    
    /**
     * Data Transfer Object for user creation request.
     * Clean Code: Inner record for request data structure.
     */
    @Schema(description = "User creation request")
    public record CreateUserRequest(
        @Schema(description = "User first name", example = "John", required = true)
        @NotBlank(message = "First name is required")
        String firstName,
        
        @Schema(description = "User last name", example = "Doe", required = true)
        @NotBlank(message = "Last name is required")
        String lastName,
        
        @Schema(description = "User email address", example = "john.doe@example.com", required = true)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @Schema(description = "User password", example = "SecurePassword123!", required = true)
        @NotBlank(message = "Password is required")
        String password,
        
        @Schema(description = "User role", example = "OPERATOR", required = true)
        @NotNull(message = "Role is required")
        com.insurance.backoffice.domain.UserRole role
    ) {}
    
    /**
     * Data Transfer Object for user update request.
     * Clean Code: Inner record for update data structure.
     */
    @Schema(description = "User update request")
    public record UpdateUserRequest(
        @Schema(description = "User first name", example = "John", required = true)
        @NotBlank(message = "First name is required")
        String firstName,
        
        @Schema(description = "User last name", example = "Smith", required = true)
        @NotBlank(message = "Last name is required")
        String lastName,
        
        @Schema(description = "User email address", example = "john.smith@example.com", required = true)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @Schema(description = "User role", example = "ADMIN", required = true)
        @NotNull(message = "Role is required")
        com.insurance.backoffice.domain.UserRole role
    ) {}
    
    /**
     * Maps User entity to UserResponse DTO.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole()
        );
    }
    
    /**
     * Maps CreateUserRequest DTO to User entity.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private User mapToUser(CreateUserRequest request) {
        return User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(request.password())
            .role(request.role())
            .build();
    }
    
    /**
     * Maps UpdateUserRequest DTO to User entity.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private User mapToUserForUpdate(UpdateUserRequest request) {
        return User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .role(request.role())
            .build();
    }
}