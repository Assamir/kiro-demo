package com.insurance.backoffice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for the insurance backoffice application.
 * Clean Code: Focused configuration class with clear purpose.
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Provides a BCrypt password encoder bean.
     * Clean Code: Simple bean definition with clear purpose.
     * 
     * @return BCrypt password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}