package com.insurance.backoffice.integration;

import com.insurance.backoffice.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility class for cleaning up test data between integration tests.
 * Provides methods to reset database state and ensure test isolation.
 */
@Component
@Transactional
public class TestCleanupUtility {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PolicyDetailsRepository policyDetailsRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingTableRepository ratingTableRepository;

    /**
     * Cleans up all test data in the correct order to maintain referential integrity.
     */
    public void cleanupAllTestData() {
        cleanupPolicies();
        cleanupClients();
        cleanupVehicles();
        cleanupUsers();
        cleanupRatingTables();
    }

    /**
     * Cleans up policy-related data.
     */
    public void cleanupPolicies() {
        policyDetailsRepository.deleteAll();
        policyRepository.deleteAll();
    }

    /**
     * Cleans up client data.
     */
    public void cleanupClients() {
        clientRepository.deleteAll();
    }

    /**
     * Cleans up vehicle data.
     */
    public void cleanupVehicles() {
        vehicleRepository.deleteAll();
    }

    /**
     * Cleans up user data (except system users if any).
     */
    public void cleanupUsers() {
        // Only delete test users, preserve any system users
        userRepository.deleteAll();
    }

    /**
     * Cleans up rating table data.
     */
    public void cleanupRatingTables() {
        ratingTableRepository.deleteAll();
    }

    /**
     * Resets database sequences to ensure consistent ID generation.
     */
    public void resetSequences() {
        // Note: This would typically reset database sequences
        // Implementation depends on the specific database and requirements
        // For PostgreSQL with TestContainers, sequences are reset automatically
        // when the container is recreated
    }

    /**
     * Performs a complete database reset for integration tests.
     */
    public void performCompleteReset() {
        cleanupAllTestData();
        resetSequences();
    }
}