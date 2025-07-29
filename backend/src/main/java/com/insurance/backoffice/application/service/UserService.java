package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user management operations.
 * Implements business logic for user CRUD operations and role-based access control.
 * Clean Code: Single Responsibility - handles only user-related business logic.
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Creates a new user with encrypted password.
     * Clean Code: Intention-revealing method name with clear business purpose.
     * 
     * @param user the user to create
     * @return the created user with generated ID
     * @throws IllegalArgumentException if user data is invalid or email already exists
     */
    public User createUser(User user) {
        validateUserForCreation(user);
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        // Encrypt password before saving
        User userToSave = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .build();
        
        return userRepository.save(userToSave);
    }
    
    /**
     * Updates an existing user.
     * Clean Code: Business logic encapsulated with proper validation.
     * 
     * @param id the ID of the user to update
     * @param updatedUser the updated user data
     * @return the updated user
     * @throws EntityNotFoundException if user not found
     * @throws IllegalArgumentException if update data is invalid
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = findUserById(id);
        validateUserForUpdate(updatedUser, existingUser);
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new IllegalArgumentException("User with email " + updatedUser.getEmail() + " already exists");
        }
        
        // Update mutable fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        
        // Only update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Finds a user by ID.
     * Clean Code: Simple, focused method with clear purpose.
     * 
     * @param id the user ID
     * @return the user
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }
    
    /**
     * Finds a user by email address.
     * Clean Code: Intention-revealing method name.
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }
    
    /**
     * Retrieves all users in the system.
     * Clean Code: Simple method with clear purpose.
     * 
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Finds users by role.
     * Clean Code: Focused method for role-based queries.
     * 
     * @param role the user role to filter by
     * @return list of users with the specified role
     */
    @Transactional(readOnly = true)
    public List<User> findUsersByRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return userRepository.findByRole(role);
    }
    
    /**
     * Searches users by name.
     * Clean Code: Intention-revealing method name with clear business purpose.
     * 
     * @param searchTerm the search term to look for in user names
     * @return list of users whose names contain the search term
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllUsers();
        }
        return userRepository.findByFullNameContainingIgnoreCase(searchTerm.trim());
    }
    
    /**
     * Deletes a user by ID.
     * Clean Code: Simple method with clear business purpose.
     * 
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if user not found
     */
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }
    
    /**
     * Checks if a user can manage other users based on their role.
     * Clean Code: Business rule encapsulated in service layer.
     * 
     * @param userId the ID of the user to check
     * @return true if user can manage other users, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean canUserManageUsers(Long userId) {
        User user = findUserById(userId);
        return user.canManageUsers();
    }
    
    /**
     * Checks if a user can issue policies based on their role.
     * Clean Code: Business rule encapsulated in service layer.
     * 
     * @param userId the ID of the user to check
     * @return true if user can issue policies, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean canUserIssuePolicies(Long userId) {
        User user = findUserById(userId);
        return user.canIssuePolicies();
    }
    
    /**
     * Validates user data for creation.
     * Clean Code: Extracted validation logic for reusability.
     */
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
    
    /**
     * Validates user data for update.
     * Clean Code: Extracted validation logic with specific update rules.
     */
    private void validateUserForUpdate(User updatedUser, User existingUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("Updated user data cannot be null");
        }
        if (updatedUser.getFirstName() == null || updatedUser.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (updatedUser.getLastName() == null || updatedUser.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (updatedUser.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}