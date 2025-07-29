package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.application.service.AuthenticationService;
import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authenticationService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
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
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<UserInfoResponse> getCurrentUser(
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
    @Operation(summary = "User logout", description = "Logout current user (client should discard token)")
    @ApiResponse(responseCode = "200", description = "Logout successful")
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
    public record UserInfoResponse(
        Long id,
        String email,
        String fullName,
        com.insurance.backoffice.domain.UserRole role
    ) {}
}