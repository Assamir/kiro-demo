package com.insurance.backoffice.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtUtil class.
 * Clean Code: Comprehensive test coverage with descriptive test names.
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set test values using reflection to avoid @Value dependency
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForJwtTokenGenerationThatIsLongEnoughForHS512Algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L); // 24 hours
        
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }
    
    @Test
    void shouldGenerateValidJwtToken() {
        // When
        String token = jwtUtil.generateToken(userDetails);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots
    }
    
    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When
        String extractedUsername = jwtUtil.extractUsername(token);
        
        // Then
        assertThat(extractedUsername).isEqualTo("test@example.com");
    }
    
    @Test
    void shouldExtractExpirationFromToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When
        Date expiration = jwtUtil.extractExpiration(token);
        
        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }
    
    @Test
    void shouldValidateValidToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When
        Boolean isValid = jwtUtil.validateToken(token, userDetails);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void shouldRejectTokenWithWrongUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        
        // When
        Boolean isValid = jwtUtil.validateToken(token, differentUser);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    void shouldRejectExpiredToken() {
        // Given - Create JWT util with very short expiration
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", "testSecretKeyForJwtTokenGenerationThatIsLongEnoughForHS512Algorithm");
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", -1000L); // Expired
        
        String expiredToken = shortExpirationJwtUtil.generateToken(userDetails);
        
        // When & Then - Expired token should throw exception during validation
        assertThatThrownBy(() -> jwtUtil.validateToken(expiredToken, userDetails))
                .isInstanceOf(Exception.class);
    }
    
    @Test
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }
    
    @Test
    void shouldThrowExceptionForNullToken() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(null))
                .isInstanceOf(Exception.class);
    }
    
    @Test
    void shouldThrowExceptionForEmptyToken() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(""))
                .isInstanceOf(Exception.class);
    }
}