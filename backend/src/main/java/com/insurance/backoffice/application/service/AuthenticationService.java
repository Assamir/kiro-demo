package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import com.insurance.backoffice.infrastructure.security.JwtUtil;
import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service for handling user authentication operations.
 * Clean Code: Single responsibility for authentication business logic.
 */
@Service
public class AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long jwtExpiration;
    
    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                               UserRepository userRepository,
                               JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Authenticates user and generates JWT token.
     * Clean Code: Main business method with clear flow and error handling.
     * 
     * @param loginRequest login credentials
     * @return login response with JWT token
     * @throws BadCredentialsException if credentials are invalid
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
                )
            );
            
            // Load user details and generate token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = findUserByEmail(loginRequest.email());
            String token = jwtUtil.generateToken(userDetails);
            
            return createLoginResponse(token, user);
            
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }
    
    /**
     * Validates JWT token and returns user information.
     * Clean Code: Token validation with clear business logic.
     * 
     * @param token JWT token to validate
     * @return user information if token is valid
     * @throws BadCredentialsException if token is invalid
     */
    public User validateToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = findUserByEmail(email);
            
            // Create UserDetails for token validation
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                    .build();
            
            if (jwtUtil.validateToken(token, userDetails)) {
                return user;
            } else {
                throw new BadCredentialsException("Invalid or expired token");
            }
        } catch (BadCredentialsException e) {
            throw e; // Re-throw BadCredentialsException as-is
        } catch (Exception e) {
            throw new BadCredentialsException("Token validation failed", e);
        }
    }
    
    /**
     * Finds user by email address.
     * Clean Code: Private helper method with clear error handling.
     * 
     * @param email user email
     * @return user entity
     * @throws UsernameNotFoundException if user not found
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
    
    /**
     * Creates login response with user information and token.
     * Clean Code: Response creation with clear data mapping.
     * 
     * @param token JWT token
     * @param user user entity
     * @return login response
     */
    private LoginResponse createLoginResponse(String token, User user) {
        return LoginResponse.of(
            token,
            user,
            jwtExpiration
        );
    }
}