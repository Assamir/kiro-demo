package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.application.service.AuthenticationService;
import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Clean Code: Thin controller focused on HTTP concerns.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Authenticates user and returns JWT token.
     * Clean Code: Simple endpoint with clear purpose and error handling.
     * 
     * @param loginRequest login credentials
     * @return login response with JWT token
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login", 
        description = "Authenticate user with email and password, returns JWT token for subsequent API calls.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "Login Request",
                    value = """
                    {
                      "email": "admin@example.com",
                      "password": "password123"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Login successful",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(
                        name = "Login Success",
                        value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "email": "admin@example.com",
                          "fullName": "Admin User",
                          "role": "ADMIN",
                          "expiresIn": 86400000
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        }
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("LOGIN ATTEMPT: " + loginRequest.email());
        try {
            LoginResponse response = authenticationService.authenticate(loginRequest);
            System.out.println("LOGIN SUCCESS: " + loginRequest.email());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("LOGIN FAILED: " + loginRequest.email() + " - " + e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }
    
    /**
     * Validates current JWT token and returns user information.
     * Clean Code: Token validation endpoint with clear business logic.
     * 
     * @param authorizationHeader Authorization header with Bearer token
     * @return current user information
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
        summary = "Get current user", 
        description = "Get current authenticated user information using JWT token.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User information retrieved",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoResponse.class),
                    examples = @ExampleObject(
                        name = "User Info",
                        value = """
                        {
                          "id": 1,
                          "email": "admin@example.com",
                          "fullName": "Admin User",
                          "role": "ADMIN"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
        }
    )
    public ResponseEntity<UserInfoResponse> getCurrentUser(
            @Parameter(description = "Authorization header with Bearer token", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authorizationHeader.substring(7);
            User user = authenticationService.validateToken(token);
            
            UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            );
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    /**
     * Logs out the current user.
     * Clean Code: Simple logout endpoint (stateless JWT doesn't require server-side logout).
     * 
     * @return success response
     */
    @PostMapping("/logout")
    @Operation(
        summary = "User logout", 
        description = "Logout current user. For JWT-based authentication, client should discard the token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
        }
    )
    public ResponseEntity<Void> logout() {
        // For JWT-based authentication, logout is handled client-side by discarding the token
        // Clear security context for current request
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
    
    /**
     * Data Transfer Object for user information response.
     * Clean Code: Inner record for response data structure.
     */
    @Schema(description = "Current user information response")
    public record UserInfoResponse(
        @Schema(description = "User ID", example = "1")
        Long id,
        
        @Schema(description = "User email address", example = "admin@example.com")
        String email,
        
        @Schema(description = "User full name", example = "Admin User")
        String fullName,
        
        @Schema(description = "User role", example = "ADMIN")
        com.insurance.backoffice.domain.UserRole role
    ) {}
}