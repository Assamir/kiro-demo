package com.insurance.backoffice.infrastructure.security;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService class.
 * Clean Code: Comprehensive test coverage with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    private CustomUserDetailsService userDetailsService;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
        
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(UserRole.OPERATOR)
                .build();
    }
    
    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe@example.com");
        
        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john.doe@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_OPERATOR");
        
        verify(userRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void shouldLoadAdminUserWithCorrectRole() {
        // Given
        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("adminPassword")
                .role(UserRole.ADMIN)
                .build();
        
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(adminUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");
        
        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_ADMIN");
        
        verify(userRepository).findByEmail("admin@example.com");
    }
    
    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: nonexistent@example.com");
        
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void shouldCreateUserDetailsWithCorrectAccountStatus() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe@example.com");
        
        // Then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }
    
    @Test
    void shouldHandleNullEmailGracefully() {
        // Given
        when(userRepository.findByEmail(null))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: null");
        
        verify(userRepository).findByEmail(null);
    }
    
    @Test
    void shouldHandleEmptyEmailGracefully() {
        // Given
        when(userRepository.findByEmail(""))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: ");
        
        verify(userRepository).findByEmail("");
    }
}