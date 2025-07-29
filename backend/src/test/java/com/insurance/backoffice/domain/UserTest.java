package com.insurance.backoffice.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for User entity following clean code testing principles.
 * Tests the Builder pattern implementation and business logic methods.
 */
class UserTest {
    
    @Test
    void shouldCreateUserWithBuilder() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String password = "password123";
        UserRole role = UserRole.ADMIN;
        
        // When
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .role(role)
                .build();
        
        // Then
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
    }
    
    @Test
    void shouldReturnFullName() {
        // Given
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.ADMIN)
                .build();
        
        // When
        String fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("John Doe");
    }
    
    @Test
    void shouldReturnTrueForAdminRole() {
        // Given
        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("password123")
                .role(UserRole.ADMIN)
                .build();
        
        // When & Then
        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(adminUser.canManageUsers()).isTrue();
        assertThat(adminUser.canIssuePolicies()).isFalse();
    }
    
    @Test
    void shouldReturnTrueForOperatorRole() {
        // Given
        User operatorUser = User.builder()
                .firstName("Operator")
                .lastName("User")
                .email("operator@example.com")
                .password("password123")
                .role(UserRole.OPERATOR)
                .build();
        
        // When & Then
        assertThat(operatorUser.isAdmin()).isFalse();
        assertThat(operatorUser.canManageUsers()).isFalse();
        assertThat(operatorUser.canIssuePolicies()).isTrue();
    }
    
    @Test
    void shouldThrowExceptionWhenRequiredFieldMissing() {
        // When & Then
        assertThatThrownBy(() -> User.builder()
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.ADMIN)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required");
    }
    
    @Test
    void shouldThrowExceptionWhenEmailMissing() {
        // When & Then
        assertThatThrownBy(() -> User.builder()
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .role(UserRole.ADMIN)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is required");
    }
}