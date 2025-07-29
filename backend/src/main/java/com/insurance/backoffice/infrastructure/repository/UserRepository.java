package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     * Used for authentication and user lookup.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user exists with the given email address.
     * Used for validation during user creation.
     * 
     * @param email the email address to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds all users with the specified role.
     * Used for role-based user management.
     * 
     * @param role the user role to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Finds users by first name and last name (case-insensitive).
     * Used for user search functionality.
     * 
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return list of users matching the name criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(:firstName) AND LOWER(u.lastName) = LOWER(:lastName)")
    List<User> findByFirstNameAndLastNameIgnoreCase(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    /**
     * Finds users whose full name contains the search term (case-insensitive).
     * Used for flexible user search.
     * 
     * @param searchTerm the term to search for in user names
     * @return list of users whose names contain the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}