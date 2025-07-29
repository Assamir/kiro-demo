package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository using H2 in-memory database.
 * Tests repository operations to ensure query correctness.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User adminUser;
    private User operatorUser;
    
    @BeforeEach
    void setUp() {
        // Create test users
        adminUser = User.builder()
                .firstName("John")
                .lastName("Admin")
                .email("john.admin@example.com")
                .password("password123")
                .role(UserRole.ADMIN)
                .build();
        
        operatorUser = User.builder()
                .firstName("Jane")
                .lastName("Operator")
                .email("jane.operator@example.com")
                .password("password456")
                .role(UserRole.OPERATOR)
                .build();
        
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(operatorUser);
        entityManager.clear();
    }
    
    @Test
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("john.admin@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Admin");
        assertThat(found.get().getRole()).isEqualTo(UserRole.ADMIN);
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldCheckIfUserExistsByEmail() {
        // When & Then
        assertThat(userRepository.existsByEmail("john.admin@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
    
    @Test
    void shouldFindUsersByRole() {
        // When
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        List<User> operators = userRepository.findByRole(UserRole.OPERATOR);
        
        // Then
        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getEmail()).isEqualTo("john.admin@example.com");
        
        assertThat(operators).hasSize(1);
        assertThat(operators.get(0).getEmail()).isEqualTo("jane.operator@example.com");
    }
    
    @Test
    void shouldFindUsersByFirstNameAndLastNameIgnoreCase() {
        // When
        List<User> users = userRepository.findByFirstNameAndLastNameIgnoreCase("JOHN", "ADMIN");
        
        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("john.admin@example.com");
    }
    
    @Test
    void shouldFindUsersByFullNameContainingIgnoreCase() {
        // When
        List<User> users = userRepository.findByFullNameContainingIgnoreCase("john");
        
        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("john.admin@example.com");
    }
    
    @Test
    void shouldFindUsersByPartialFullNameIgnoreCase() {
        // When
        List<User> users = userRepository.findByFullNameContainingIgnoreCase("oper");
        
        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("jane.operator@example.com");
    }
    
    @Test
    void shouldReturnEmptyListWhenNoUsersMatchFullNameSearch() {
        // When
        List<User> users = userRepository.findByFullNameContainingIgnoreCase("nonexistent");
        
        // Then
        assertThat(users).isEmpty();
    }
    
    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        User newUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test.user@example.com")
                .password("testpass")
                .role(UserRole.OPERATOR)
                .build();
        
        // When
        User saved = userRepository.save(newUser);
        Optional<User> retrieved = userRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("test.user@example.com");
        assertThat(retrieved.get().getFullName()).isEqualTo("Test User");
    }
    
    @Test
    void shouldDeleteUser() {
        // Given
        Long userId = adminUser.getId();
        
        // When
        userRepository.deleteById(userId);
        Optional<User> deleted = userRepository.findById(userId);
        
        // Then
        assertThat(deleted).isEmpty();
    }
    
    @Test
    void shouldCountUsersByRole() {
        // When
        long adminCount = userRepository.findByRole(UserRole.ADMIN).size();
        long operatorCount = userRepository.findByRole(UserRole.OPERATOR).size();
        
        // Then
        assertThat(adminCount).isEqualTo(1);
        assertThat(operatorCount).isEqualTo(1);
    }
}