package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client entity operations.
 * Provides data access methods for client management functionality.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    /**
     * Finds a client by their PESEL number.
     * PESEL is unique identifier for Polish citizens.
     * 
     * @param pesel the PESEL number to search for
     * @return Optional containing the client if found, empty otherwise
     */
    Optional<Client> findByPesel(String pesel);
    
    /**
     * Checks if a client exists with the given PESEL number.
     * Used for validation during client creation.
     * 
     * @param pesel the PESEL number to check
     * @return true if a client exists with this PESEL, false otherwise
     */
    boolean existsByPesel(String pesel);
    
    /**
     * Finds a client by their email address.
     * Used for client lookup and communication.
     * 
     * @param email the email address to search for
     * @return Optional containing the client if found, empty otherwise
     */
    Optional<Client> findByEmail(String email);
    
    /**
     * Finds clients whose full name contains the search term (case-insensitive).
     * Used for client search functionality.
     * 
     * @param searchTerm the term to search for in client names
     * @return list of clients whose names contain the search term
     */
    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Client> findByFullNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Finds clients by phone number.
     * Used for client lookup by contact information.
     * 
     * @param phoneNumber the phone number to search for
     * @return list of clients with the specified phone number
     */
    List<Client> findByPhoneNumber(String phoneNumber);
    
    /**
     * Finds clients who have active policies.
     * Used for reporting and client management.
     * 
     * @return list of clients with at least one active policy
     */
    @Query("SELECT DISTINCT c FROM Client c JOIN c.policies p WHERE p.status = 'ACTIVE'")
    List<Client> findClientsWithActivePolicies();
}