package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Clean Code: Comprehensive test coverage with descriptive test names and AAA pattern.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private User testAdmin;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.OPERATOR)
                .build();
        
        testAdmin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("admin123")
                .role(UserRole.ADMIN)
                .build();
    }
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.createUser(testUser);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(passwordEncoder).encode(testUser.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email " + testUser.getEmail() + " already exists");
        
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingNullUser() {
        // When & Then
        assertThatThrownBy(() -> userService.createUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User cannot be null");
        
        verifyNoInteractions(userRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingUserWithEmptyFirstName() {
        // Given - Create user with empty first name, but bypass builder validation
        // by creating the user object and then testing service validation
        User invalidUser;
        try {
            invalidUser = User.builder()
                    .firstName("temp") // Temporary valid value to pass builder validation
                    .lastName("Doe")
                    .email("test@example.com")
                    .password("password")
                    .role(UserRole.OPERATOR)
                    .build();
            // Now set the invalid value directly
            invalidUser.setFirstName("");
        } catch (IllegalArgumentException e) {
            // If builder validation catches it, that's also acceptable
            assertThat(e.getMessage()).isEqualTo("First name is required");
            return;
        }
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required");
    }
    
    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        User existingUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(UserRole.OPERATOR)
                .build();
        
        User updatedUserData = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("newPassword")
                .role(UserRole.ADMIN)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updatedUserData.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(updatedUserData.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        // When
        User result = userService.updateUser(userId, updatedUserData);
        
        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(updatedUserData.getEmail());
        verify(passwordEncoder).encode(updatedUserData.getPassword());
        verify(userRepository).save(existingUser);
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, testUser))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found with ID: " + userId);
        
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldFindUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // When
        User result = userService.findUserById(userId);
        
        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findById(userId);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found with ID: " + userId);
        
        verify(userRepository).findById(userId);
    }
    
    @Test
    void shouldFindUserByEmailSuccessfully() {
        // Given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userService.findUserByEmail(email);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userService.findUserByEmail(email);
        
        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void shouldReturnEmptyWhenEmailIsNull() {
        // When
        Optional<User> result = userService.findUserByEmail(null);
        
        // Then
        assertThat(result).isEmpty();
        verifyNoInteractions(userRepository);
    }
    
    @Test
    void shouldFindAllUsersSuccessfully() {
        // Given
        List<User> users = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<User> result = userService.findAllUsers();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testUser, testAdmin);
        verify(userRepository).findAll();
    }
    
    @Test
    void shouldFindUsersByRoleSuccessfully() {
        // Given
        List<User> operators = Arrays.asList(testUser);
        when(userRepository.findByRole(UserRole.OPERATOR)).thenReturn(operators);
        
        // When
        List<User> result = userService.findUsersByRole(UserRole.OPERATOR);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testUser);
        verify(userRepository).findByRole(UserRole.OPERATOR);
    }
    
    @Test
    void shouldThrowExceptionWhenFindingUsersByNullRole() {
        // When & Then
        assertThatThrownBy(() -> userService.findUsersByRole(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role cannot be null");
        
        verifyNoInteractions(userRepository);
    }
    
    @Test
    void shouldSearchUsersByNameSuccessfully() {
        // Given
        String searchTerm = "John";
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByFullNameContainingIgnoreCase(searchTerm)).thenReturn(users);
        
        // When
        List<User> result = userService.searchUsersByName(searchTerm);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testUser);
        verify(userRepository).findByFullNameContainingIgnoreCase(searchTerm);
    }
    
    @Test
    void shouldReturnAllUsersWhenSearchTermIsEmpty() {
        // Given
        List<User> allUsers = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        // When
        List<User> result = userService.searchUsersByName("");
        
        // Then
        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
        verify(userRepository, never()).findByFullNameContainingIgnoreCase(anyString());
    }
    
    @Test
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // When
        userService.deleteUser(userId);
        
        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(testUser);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found with ID: " + userId);
        
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
    void shouldReturnTrueWhenAdminCanManageUsers() {
        // Given
        Long adminId = 1L;
        when(userRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));
        
        // When
        boolean result = userService.canUserManageUsers(adminId);
        
        // Then
        assertThat(result).isTrue();
        verify(userRepository).findById(adminId);
    }
    
    @Test
    void shouldReturnFalseWhenOperatorCannotManageUsers() {
        // Given
        Long operatorId = 1L;
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(testUser));
        
        // When
        boolean result = userService.canUserManageUsers(operatorId);
        
        // Then
        assertThat(result).isFalse();
        verify(userRepository).findById(operatorId);
    }
    
    @Test
    void shouldReturnTrueWhenOperatorCanIssuePolicies() {
        // Given
        Long operatorId = 1L;
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(testUser));
        
        // When
        boolean result = userService.canUserIssuePolicies(operatorId);
        
        // Then
        assertThat(result).isTrue();
        verify(userRepository).findById(operatorId);
    }
    
    @Test
    void shouldReturnFalseWhenAdminCannotIssuePolicies() {
        // Given
        Long adminId = 1L;
        when(userRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));
        
        // When
        boolean result = userService.canUserIssuePolicies(adminId);
        
        // Then
        assertThat(result).isFalse();
        verify(userRepository).findById(adminId);
    }
}