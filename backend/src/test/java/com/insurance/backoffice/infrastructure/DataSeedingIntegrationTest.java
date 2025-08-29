package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.config.DataSeedingConfig;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for data seeding functionality.
 * Tests the actual seeding process with a real database.
 * 
 * Clean Code Principles Applied:
 * - Integration Testing: Tests the complete seeding workflow
 * - Isolated Environment: Uses test profile and properties
 * - Clear Assertions: Verifies expected data is created
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.data-seeding.enabled=true",
    "app.data-seeding.seed-users=true",
    "app.data-seeding.seed-clients=true",
    "app.data-seeding.seed-vehicles=true",
    "app.data-seeding.seed-policies=true",
    "app.data-seeding.force-seeding=true",
    "app.data-seeding.sample-size=5"
})
@Transactional
class DataSeedingIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DataSeedingConfig config;

    @Test
    void shouldSeedDataSuccessfully() {
        // Given
        DataSeedingService dataSeedingService = new DataSeedingService(
            userRepository,
            clientRepository,
            vehicleRepository,
            policyRepository,
            null, // ratingTableRepository not needed for this test
            passwordEncoder,
            config
        );

        // Clear any existing data
        policyRepository.deleteAll();
        vehicleRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        // When
        dataSeedingService.run();

        // Then
        assertThat(userRepository.count()).isGreaterThan(0);
        assertThat(clientRepository.count()).isGreaterThan(0);
        assertThat(vehicleRepository.count()).isGreaterThan(0);
        assertThat(policyRepository.count()).isGreaterThan(0);

        // Verify user roles are seeded correctly
        long adminCount = userRepository.countByRole(UserRole.ADMIN);
        long operatorCount = userRepository.countByRole(UserRole.OPERATOR);
        
        assertThat(adminCount).isGreaterThan(0);
        assertThat(operatorCount).isGreaterThan(0);
    }

    @Test
    void shouldRespectConfigurationSettings() {
        // Given - configuration should be loaded from test properties
        
        // Then
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isSeedUsers()).isTrue();
        assertThat(config.isSeedClients()).isTrue();
        assertThat(config.isSeedVehicles()).isTrue();
        assertThat(config.isSeedPolicies()).isTrue();
        assertThat(config.isForceSeeding()).isTrue();
        assertThat(config.getSampleSize()).isEqualTo(5);
    }
}