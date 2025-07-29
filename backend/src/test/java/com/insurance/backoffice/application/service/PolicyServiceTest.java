package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PolicyService.
 * Clean Code: Comprehensive test coverage with descriptive test names and AAA pattern.
 */
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {
    
    @Mock
    private PolicyRepository policyRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private RatingService ratingService;
    
    @InjectMocks
    private PolicyService policyService;
    
    private Client testClient;
    private Vehicle testVehicle;
    private Policy testPolicy;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .fullName("John Doe")
                .pesel("12345678901")
                .address("123 Main St")
                .email("john.doe@example.com")
                .phoneNumber("123456789")
                .build();
        
        testVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Camry")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1234567890ABCDEFG")
                .engineCapacity(2000)
                .power(150)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusYears(1);
        
        testPolicy = Policy.builder()
                .policyNumber("OC-12345678")
                .issueDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1200.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();
    }
    
    @Test
    void shouldCreatePolicySuccessfully() {
        // Given
        Long clientId = 1L;
        Long vehicleId = 1L;
        BigDecimal expectedPremium = new BigDecimal("1200.00");
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(ratingService.calculatePremium(InsuranceType.OC, testVehicle, startDate))
                .thenReturn(expectedPremium);
        when(policyRepository.existsByPolicyNumber(anyString())).thenReturn(false);
        when(policyRepository.save(any(Policy.class))).thenReturn(testPolicy);
        
        // When
        Policy result = policyService.createPolicy(clientId, vehicleId, InsuranceType.OC, 
                startDate, endDate, null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getInsuranceType()).isEqualTo(InsuranceType.OC);
        assertThat(result.getPremium()).isEqualTo(expectedPremium);
        assertThat(result.getClient()).isEqualTo(testClient);
        assertThat(result.getVehicle()).isEqualTo(testVehicle);
        
        verify(clientRepository).findById(clientId);
        verify(vehicleRepository).findById(vehicleId);
        verify(ratingService).calculatePremium(InsuranceType.OC, testVehicle, startDate);
        verify(policyRepository).save(any(Policy.class));
    }
    
    @Test
    void shouldThrowExceptionWhenClientNotFoundForPolicyCreation() {
        // Given
        Long clientId = 999L;
        Long vehicleId = 1L;
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> policyService.createPolicy(clientId, vehicleId, 
                InsuranceType.OC, startDate, endDate, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Client not found with ID: " + clientId);
        
        verify(clientRepository).findById(clientId);
        verify(vehicleRepository, never()).findById(anyLong());
        verify(policyRepository, never()).save(any(Policy.class));
    }
    
    @Test
    void shouldThrowExceptionWhenVehicleNotFoundForPolicyCreation() {
        // Given
        Long clientId = 1L;
        Long vehicleId = 999L;
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> policyService.createPolicy(clientId, vehicleId, 
                InsuranceType.OC, startDate, endDate, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vehicle not found with ID: " + vehicleId);
        
        verify(clientRepository).findById(clientId);
        verify(vehicleRepository).findById(vehicleId);
        verify(policyRepository, never()).save(any(Policy.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingPolicyWithNullClientId() {
        // When & Then
        assertThatThrownBy(() -> policyService.createPolicy(null, 1L, 
                InsuranceType.OC, startDate, endDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client ID is required");
        
        verifyNoInteractions(clientRepository, vehicleRepository, policyRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingPolicyWithInvalidDates() {
        // Given
        LocalDate invalidEndDate = LocalDate.now().minusDays(1);
        
        // When & Then
        assertThatThrownBy(() -> policyService.createPolicy(1L, 1L, 
                InsuranceType.OC, startDate, invalidEndDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start date must be before end date");
        
        verifyNoInteractions(clientRepository, vehicleRepository, policyRepository);
    }
    
    @Test
    void shouldUpdatePolicySuccessfully() {
        // Given
        Long policyId = 1L;
        LocalDate newStartDate = LocalDate.now().plusDays(2);
        LocalDate newEndDate = LocalDate.now().plusYears(1).plusDays(2);
        BigDecimal newDiscount = new BigDecimal("100.00");
        BigDecimal newPremium = new BigDecimal("1100.00");
        
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(testPolicy));
        when(ratingService.calculatePremium(testPolicy.getInsuranceType(), 
                testPolicy.getVehicle(), newStartDate)).thenReturn(newPremium);
        when(policyRepository.save(any(Policy.class))).thenReturn(testPolicy);
        
        // When
        Policy result = policyService.updatePolicy(policyId, newStartDate, newEndDate, newDiscount);
        
        // Then
        assertThat(result).isNotNull();
        verify(policyRepository).findById(policyId);
        verify(ratingService).calculatePremium(testPolicy.getInsuranceType(), 
                testPolicy.getVehicle(), newStartDate);
        verify(policyRepository).save(testPolicy);
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingCanceledPolicy() {
        // Given
        Long policyId = 1L;
        Policy canceledPolicy = Policy.builder()
                .policyNumber("OC-12345678")
                .issueDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .status(PolicyStatus.CANCELED)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1200.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();
        
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(canceledPolicy));
        
        // When & Then
        assertThatThrownBy(() -> policyService.updatePolicy(policyId, startDate, endDate, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update a canceled policy");
        
        verify(policyRepository).findById(policyId);
        verify(policyRepository, never()).save(any(Policy.class));
    }
    
    @Test
    void shouldCancelPolicySuccessfully() {
        // Given
        Long policyId = 1L;
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(testPolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(testPolicy);
        
        // When
        Policy result = policyService.cancelPolicy(policyId);
        
        // Then
        assertThat(result).isNotNull();
        verify(policyRepository).findById(policyId);
        verify(policyRepository).save(testPolicy);
    }
    
    @Test
    void shouldFindPolicyByIdSuccessfully() {
        // Given
        Long policyId = 1L;
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(testPolicy));
        
        // When
        Policy result = policyService.findPolicyById(policyId);
        
        // Then
        assertThat(result).isEqualTo(testPolicy);
        verify(policyRepository).findById(policyId);
    }
    
    @Test
    void shouldThrowExceptionWhenPolicyNotFoundById() {
        // Given
        Long policyId = 999L;
        when(policyRepository.findById(policyId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> policyService.findPolicyById(policyId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Policy not found with ID: " + policyId);
        
        verify(policyRepository).findById(policyId);
    }
    
    @Test
    void shouldFindPolicyByNumberSuccessfully() {
        // Given
        String policyNumber = "OC-12345678";
        when(policyRepository.findByPolicyNumber(policyNumber)).thenReturn(Optional.of(testPolicy));
        
        // When
        Policy result = policyService.findPolicyByNumber(policyNumber);
        
        // Then
        assertThat(result).isEqualTo(testPolicy);
        verify(policyRepository).findByPolicyNumber(policyNumber);
    }
    
    @Test
    void shouldFindPoliciesByClientSuccessfully() {
        // Given
        Long clientId = 1L;
        List<Policy> policies = Arrays.asList(testPolicy);
        when(policyRepository.findByClientIdOrderByIssueDateDesc(clientId)).thenReturn(policies);
        
        // When
        List<Policy> result = policyService.findPoliciesByClient(clientId);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findByClientIdOrderByIssueDateDesc(clientId);
    }
    
    @Test
    void shouldThrowExceptionWhenFindingPoliciesByNullClientId() {
        // When & Then
        assertThatThrownBy(() -> policyService.findPoliciesByClient(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client ID cannot be null");
        
        verifyNoInteractions(policyRepository);
    }
    
    @Test
    void shouldFindPoliciesByVehicleSuccessfully() {
        // Given
        Long vehicleId = 1L;
        List<Policy> policies = Arrays.asList(testPolicy);
        when(policyRepository.findByVehicleId(vehicleId)).thenReturn(policies);
        
        // When
        List<Policy> result = policyService.findPoliciesByVehicle(vehicleId);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findByVehicleId(vehicleId);
    }
    
    @Test
    void shouldFindPoliciesByStatusSuccessfully() {
        // Given
        List<Policy> activePolicies = Arrays.asList(testPolicy);
        when(policyRepository.findByStatus(PolicyStatus.ACTIVE)).thenReturn(activePolicies);
        
        // When
        List<Policy> result = policyService.findPoliciesByStatus(PolicyStatus.ACTIVE);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findByStatus(PolicyStatus.ACTIVE);
    }
    
    @Test
    void shouldFindPoliciesByInsuranceTypeSuccessfully() {
        // Given
        List<Policy> ocPolicies = Arrays.asList(testPolicy);
        when(policyRepository.findByInsuranceType(InsuranceType.OC)).thenReturn(ocPolicies);
        
        // When
        List<Policy> result = policyService.findPoliciesByInsuranceType(InsuranceType.OC);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findByInsuranceType(InsuranceType.OC);
    }
    
    @Test
    void shouldSearchPoliciesByClientNameSuccessfully() {
        // Given
        String clientName = "John";
        List<Policy> policies = Arrays.asList(testPolicy);
        when(policyRepository.findByClientNameContainingIgnoreCase(clientName)).thenReturn(policies);
        
        // When
        List<Policy> result = policyService.searchPoliciesByClientName(clientName);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findByClientNameContainingIgnoreCase(clientName);
    }
    
    @Test
    void shouldReturnEmptyListWhenSearchingWithEmptyClientName() {
        // When
        List<Policy> result = policyService.searchPoliciesByClientName("");
        
        // Then
        assertThat(result).isEmpty();
        verifyNoInteractions(policyRepository);
    }
    
    @Test
    void shouldFindCurrentlyActivePoliciesSuccessfully() {
        // Given
        List<Policy> activePolicies = Arrays.asList(testPolicy);
        when(policyRepository.findCurrentlyActivePolicies(any(LocalDate.class))).thenReturn(activePolicies);
        
        // When
        List<Policy> result = policyService.findCurrentlyActivePolicies();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findCurrentlyActivePolicies(any(LocalDate.class));
    }
    
    @Test
    void shouldFindPoliciesExpiringWithinDaysSuccessfully() {
        // Given
        int days = 30;
        List<Policy> expiringPolicies = Arrays.asList(testPolicy);
        when(policyRepository.findPoliciesExpiringWithinDays(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expiringPolicies);
        
        // When
        List<Policy> result = policyService.findPoliciesExpiringWithinDays(days);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testPolicy);
        verify(policyRepository).findPoliciesExpiringWithinDays(any(LocalDate.class), any(LocalDate.class));
    }
    
    @Test
    void shouldThrowExceptionWhenFindingPoliciesExpiringWithinNegativeDays() {
        // When & Then
        assertThatThrownBy(() -> policyService.findPoliciesExpiringWithinDays(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Days must be non-negative");
        
        verifyNoInteractions(policyRepository);
    }
}