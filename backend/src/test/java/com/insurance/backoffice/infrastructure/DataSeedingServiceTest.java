package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.config.DataSeedingConfig;
import com.insurance.backoffice.domain.UserRole;
import com.insurance.backoffice.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DataSeedingService.
 * Tests the seeding logic without actual database operations.
 * 
 * Clean Code Principles Applied:
 * - Single Responsibility: Each test focuses on one aspect
 * - Meaningful Names: Test names describe what they verify
 * - Arrange-Act-Assert: Clear test structure
 */
@ExtendWith(MockitoExtension.class)
class DataSeedingServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private PolicyRepository policyRepository;
    
    @Mock
    private RatingTableRepository ratingTableRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private DataSeedingConfig config;

    private DataSeedingService dataSeedingService;

    @BeforeEach
    void setUp() {
        dataSeedingService = new DataSeedingService(
                userRepository,
                clientRepository,
                vehicleRepository,
                policyRepository,
                ratingTableRepository,
                passwordEncoder,
                config
        );
    }

    @Test
    void shouldSkipSeedingWhenDisabled() {
        // Given
        when(config.isEnabled()).thenReturn(false);

        // When
        dataSeedingService.run();

        // Then
        verify(config).isEnabled();
        verifyNoInteractions(userRepository, clientRepository, vehicleRepository, policyRepository);
    }

    @Test
    void shouldSkipSeedingWhenDataExists() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        when(config.isForceSeeding()).thenReturn(false);
        when(userRepository.count()).thenReturn(5L); // Data already exists

        // When
        dataSeedingService.run();

        // Then
        verify(userRepository).count();
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldSeedDataWhenEnabledAndNoDataExists() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        when(config.isForceSeeding()).thenReturn(false);
        when(config.isSeedUsers()).thenReturn(true);
        when(config.isSeedClients()).thenReturn(true);
        when(config.isSeedVehicles()).thenReturn(true);
        when(config.isSeedPolicies()).thenReturn(true);
        
        when(userRepository.count()).thenReturn(0L); // No data exists
        when(clientRepository.count()).thenReturn(10L); // Clients exist for policy seeding
        when(vehicleRepository.count()).thenReturn(10L); // Vehicles exist for policy seeding
        
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");
        when(clientRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(vehicleRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        // When
        dataSeedingService.run();

        // Then
        verify(config).logConfiguration();
        verify(userRepository).saveAll(anyList());
        verify(clientRepository).saveAll(anyList());
        verify(vehicleRepository).saveAll(anyList());
    }

    @Test
    void shouldSeedOnlyEnabledDataTypes() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        when(config.isForceSeeding()).thenReturn(false);
        when(config.isSeedUsers()).thenReturn(true);
        when(config.isSeedClients()).thenReturn(false); // Disabled
        when(config.isSeedVehicles()).thenReturn(true);
        when(config.isSeedPolicies()).thenReturn(false); // Disabled
        
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");

        // When
        dataSeedingService.run();

        // Then
        verify(userRepository).saveAll(anyList());
        verify(clientRepository, never()).saveAll(anyList());
        verify(vehicleRepository).saveAll(anyList());
        verify(policyRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldForceSeedWhenConfigured() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        when(config.isForceSeeding()).thenReturn(true); // Force seeding enabled
        when(config.isSeedUsers()).thenReturn(true);
        
        when(userRepository.count()).thenReturn(10L); // Data exists but should be ignored
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded_password");

        // When
        dataSeedingService.run();

        // Then
        verify(userRepository).saveAll(anyList()); // Should seed despite existing data
    }

    @Test
    void shouldSkipPolicySeedingWhenNoClientsOrVehicles() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        when(config.isForceSeeding()).thenReturn(false);
        when(config.isSeedUsers()).thenReturn(false);
        when(config.isSeedClients()).thenReturn(false);
        when(config.isSeedVehicles()).thenReturn(false);
        when(config.isSeedPolicies()).thenReturn(true);
        
        when(userRepository.count()).thenReturn(0L);
        when(clientRepository.count()).thenReturn(0L); // No clients
        when(vehicleRepository.count()).thenReturn(0L); // No vehicles

        // When
        dataSeedingService.run();

        // Then
        verify(policyRepository, never()).saveAll(anyList()); // Should not seed policies
    }
}