package com.insurance.backoffice.config;

import com.insurance.backoffice.infrastructure.security.CustomUserDetailsService;
import com.insurance.backoffice.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test to verify SecurityConfig beans are properly configured.
 * Clean Code: Basic configuration verification test.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityConfigTest {
    
    @Autowired
    private SecurityConfig securityConfig;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Test
    void shouldLoadSecurityConfiguration() {
        // Then
        assertThat(securityConfig).isNotNull();
    }
    
    @Test
    void shouldConfigurePasswordEncoder() {
        // When
        String encoded = passwordEncoder.encode("password");
        
        // Then
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo("password");
        assertThat(passwordEncoder.matches("password", encoded)).isTrue();
    }
    
    @Test
    void shouldConfigureAuthenticationManager() {
        // Then
        assertThat(authenticationManager).isNotNull();
    }
    
    @Test
    void shouldConfigureCustomUserDetailsService() {
        // Then
        assertThat(userDetailsService).isNotNull();
    }
    
    @Test
    void shouldConfigureJwtAuthenticationFilter() {
        // Then
        assertThat(jwtAuthenticationFilter).isNotNull();
    }
}