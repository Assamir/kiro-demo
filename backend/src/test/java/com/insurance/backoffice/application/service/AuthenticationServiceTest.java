package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import com.insurance.backoffice.infrastructure.security.JwtUtil;
import com.insurance.backoffice.interfaces.dto.LoginRequest;
import com.insurance.backoffice.interfaces.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService class.
 * Clean Code: Comprehensive test coverage with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private UserDetails userDetails;
    
    private AuthenticationService authenticationService;
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(authenticationManager, userRepository, jwtUtil);
        ReflectionTestUtils.setField(authenticationService, "jwtExpiration", 86400000L);
        
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(UserRole.OPERATOR)
                .build();
        
        loginRequest = new LoginRequest("john.doe@example.com", "password");
    }
    
    @Test
    void shouldAuthenticateUserSuccessfully() {
        // Given
        String expectedToken = "jwt.token.here";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(userDetails)).thenReturn(expectedToken);
        
        // When
        LoginResponse response = authenticationService.authenticate(loginRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(expectedToken);
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.role()).isEqualTo(UserRole.OPERATOR);
        assertThat(response.expiresIn()).isEqualTo(86400000L);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(jwtUtil).generateToken(userDetails);
    }
    
    @Test
    void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository, jwtUtil);
    }
    
    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("john.doe@example.com");
        verifyNoInteractions(jwtUtil);
    }
    
    @Test
    void shouldValidateTokenSuccessfully() {
        // Given
        String token = "valid.jwt.token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(true);
        
        // When
        User result = authenticationService.validateToken(token);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getRole()).isEqualTo(UserRole.OPERATOR);
        
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(jwtUtil).validateToken(eq(token), any(UserDetails.class));
    }
    
    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenValidationFails() {
        // Given
        String token = "invalid.jwt.token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authenticationService.validateToken(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid or expired token");
        
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(jwtUtil).validateToken(eq(token), any(UserDetails.class));
    }
    
    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenExtractionFails() {
        // Given
        String token = "malformed.jwt.token";
        
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Token parsing failed"));
        
        // When & Then
        assertThatThrownBy(() -> authenticationService.validateToken(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token validation failed");
        
        verify(jwtUtil).extractUsername(token);
        verifyNoInteractions(userRepository);
    }
    
    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFoundDuringTokenValidation() {
        // Given
        String token = "valid.jwt.token";
        
        when(jwtUtil.extractUsername(token)).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authenticationService.validateToken(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Token validation failed");
        
        verify(jwtUtil).extractUsername(token);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void shouldAuthenticateAdminUserCorrectly() {
        // Given
        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("adminPassword")
                .role(UserRole.ADMIN)
                .build();
        
        LoginRequest adminLoginRequest = new LoginRequest("admin@example.com", "adminPassword");
        String expectedToken = "admin.jwt.token";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(adminUser));
        when(jwtUtil.generateToken(userDetails)).thenReturn(expectedToken);
        
        // When
        LoginResponse response = authenticationService.authenticate(adminLoginRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(expectedToken);
        assertThat(response.email()).isEqualTo("admin@example.com");
        assertThat(response.fullName()).isEqualTo("Admin User");
        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("admin@example.com");
        verify(jwtUtil).generateToken(userDetails);
    }
}