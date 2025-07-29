package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PolicyRepository using H2 in-memory database.
 * Tests the key requirement for policy search by client and complex filtering.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PolicyRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    private Client client1;
    private Client client2;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private Policy activePolicy;
    private Policy canceledPolicy;
    private Policy expiredPolicy;
    
    @BeforeEach
    void setUp() {
        // Create test clients
        client1 = Client.builder()
                .fullName("John Kowalski")
                .pesel("12345678901")
                .address("ul. Testowa 1, 00-001 Warszawa")
                .email("john.kowalski@example.com")
                .phoneNumber("+48123456789")
                .build();
        
        client2 = Client.builder()
                .fullName("Anna Nowak")
                .pesel("98765432109")
                .address("ul. Przykładowa 2, 00-002 Kraków")
                .email("anna.nowak@example.com")
                .phoneNumber("+48987654321")
                .build();
        
        // Create test vehicles
        vehicle1 = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("WA12345")
                .vin("JT123456789012345")
                .engineCapacity(1600)
                .power(132)
                .firstRegistrationDate(LocalDate.of(2020, 5, 15))
                .build();
        
        vehicle2 = Vehicle.builder()
                .make("BMW")
                .model("X5")
                .yearOfManufacture(2019)
                .registrationNumber("KR67890")
                .vin("WBA12345678901234")
                .engineCapacity(3000)
                .power(265)
                .firstRegistrationDate(LocalDate.of(2019, 8, 20))
                .build();
        
        entityManager.persistAndFlush(client1);
        entityManager.persistAndFlush(client2);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        
        // Create test policies
        activePolicy = Policy.builder()
                .policyNumber("POL-001")
                .issueDate(LocalDate.now().minusDays(30))
                .startDate(LocalDate.now().minusDays(30))
                .endDate(LocalDate.now().plusDays(335))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1200.00))
                .client(client1)
                .vehicle(vehicle1)
                .build();
        
        canceledPolicy = Policy.builder()
                .policyNumber("POL-002")
                .issueDate(LocalDate.now().minusDays(60))
                .startDate(LocalDate.now().minusDays(60))
                .endDate(LocalDate.now().plusDays(305))
                .status(PolicyStatus.CANCELED)
                .insuranceType(InsuranceType.AC)
                .premium(BigDecimal.valueOf(2500.00))
                .client(client1)
                .vehicle(vehicle2)
                .build();
        
        expiredPolicy = Policy.builder()
                .policyNumber("POL-003")
                .issueDate(LocalDate.now().minusDays(400))
                .startDate(LocalDate.now().minusDays(400))
                .endDate(LocalDate.now().minusDays(35))
                .status(PolicyStatus.EXPIRED)
                .insuranceType(InsuranceType.NNW)
                .premium(BigDecimal.valueOf(800.00))
                .client(client2)
                .vehicle(vehicle1)
                .build();
        
        entityManager.persistAndFlush(activePolicy);
        entityManager.persistAndFlush(canceledPolicy);
        entityManager.persistAndFlush(expiredPolicy);
        entityManager.clear();
    }
    
    @Test
    void shouldFindPolicyByPolicyNumber() {
        // When
        Optional<Policy> found = policyRepository.findByPolicyNumber("POL-001");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(PolicyStatus.ACTIVE);
        assertThat(found.get().getInsuranceType()).isEqualTo(InsuranceType.OC);
    }
    
    @Test
    void shouldCheckIfPolicyExistsByPolicyNumber() {
        // When & Then
        assertThat(policyRepository.existsByPolicyNumber("POL-001")).isTrue();
        assertThat(policyRepository.existsByPolicyNumber("POL-999")).isFalse();
    }
    
    @Test
    void shouldFindPoliciesByClientId() {
        // This tests the key requirement: policy search by client
        // When
        List<Policy> client1Policies = policyRepository.findByClientId(client1.getId());
        List<Policy> client2Policies = policyRepository.findByClientId(client2.getId());
        
        // Then
        assertThat(client1Policies).hasSize(2);
        assertThat(client1Policies).extracting(Policy::getPolicyNumber)
                .containsExactlyInAnyOrder("POL-001", "POL-002");
        
        assertThat(client2Policies).hasSize(1);
        assertThat(client2Policies.get(0).getPolicyNumber()).isEqualTo("POL-003");
    }
    
    @Test
    void shouldFindPoliciesByClientIdOrderedByIssueDate() {
        // When
        List<Policy> policies = policyRepository.findByClientIdOrderByIssueDateDesc(client1.getId());
        
        // Then
        assertThat(policies).hasSize(2);
        // Most recent first (POL-001 was issued 30 days ago, POL-002 was issued 60 days ago)
        assertThat(policies.get(0).getPolicyNumber()).isEqualTo("POL-001");
        assertThat(policies.get(1).getPolicyNumber()).isEqualTo("POL-002");
    }
    
    @Test
    void shouldFindPoliciesByVehicleId() {
        // When
        List<Policy> vehicle1Policies = policyRepository.findByVehicleId(vehicle1.getId());
        List<Policy> vehicle2Policies = policyRepository.findByVehicleId(vehicle2.getId());
        
        // Then
        assertThat(vehicle1Policies).hasSize(2);
        assertThat(vehicle1Policies).extracting(Policy::getPolicyNumber)
                .containsExactlyInAnyOrder("POL-001", "POL-003");
        
        assertThat(vehicle2Policies).hasSize(1);
        assertThat(vehicle2Policies.get(0).getPolicyNumber()).isEqualTo("POL-002");
    }
    
    @Test
    void shouldFindPoliciesByStatus() {
        // When
        List<Policy> activePolicies = policyRepository.findByStatus(PolicyStatus.ACTIVE);
        List<Policy> canceledPolicies = policyRepository.findByStatus(PolicyStatus.CANCELED);
        List<Policy> expiredPolicies = policyRepository.findByStatus(PolicyStatus.EXPIRED);
        
        // Then
        assertThat(activePolicies).hasSize(1);
        assertThat(activePolicies.get(0).getPolicyNumber()).isEqualTo("POL-001");
        
        assertThat(canceledPolicies).hasSize(1);
        assertThat(canceledPolicies.get(0).getPolicyNumber()).isEqualTo("POL-002");
        
        assertThat(expiredPolicies).hasSize(1);
        assertThat(expiredPolicies.get(0).getPolicyNumber()).isEqualTo("POL-003");
    }
    
    @Test
    void shouldFindPoliciesByInsuranceType() {
        // When
        List<Policy> ocPolicies = policyRepository.findByInsuranceType(InsuranceType.OC);
        List<Policy> acPolicies = policyRepository.findByInsuranceType(InsuranceType.AC);
        List<Policy> nnwPolicies = policyRepository.findByInsuranceType(InsuranceType.NNW);
        
        // Then
        assertThat(ocPolicies).hasSize(1);
        assertThat(ocPolicies.get(0).getPolicyNumber()).isEqualTo("POL-001");
        
        assertThat(acPolicies).hasSize(1);
        assertThat(acPolicies.get(0).getPolicyNumber()).isEqualTo("POL-002");
        
        assertThat(nnwPolicies).hasSize(1);
        assertThat(nnwPolicies.get(0).getPolicyNumber()).isEqualTo("POL-003");
    }
    
    @Test
    void shouldFindPoliciesByClientIdAndStatus() {
        // This tests complex filtering combining client and status criteria
        // When
        List<Policy> client1ActivePolicies = policyRepository.findByClientIdAndStatus(client1.getId(), PolicyStatus.ACTIVE);
        List<Policy> client1CanceledPolicies = policyRepository.findByClientIdAndStatus(client1.getId(), PolicyStatus.CANCELED);
        
        // Then
        assertThat(client1ActivePolicies).hasSize(1);
        assertThat(client1ActivePolicies.get(0).getPolicyNumber()).isEqualTo("POL-001");
        
        assertThat(client1CanceledPolicies).hasSize(1);
        assertThat(client1CanceledPolicies.get(0).getPolicyNumber()).isEqualTo("POL-002");
    }
    
    @Test
    void shouldFindPoliciesByClientIdAndInsuranceType() {
        // When
        List<Policy> client1OcPolicies = policyRepository.findByClientIdAndInsuranceType(client1.getId(), InsuranceType.OC);
        List<Policy> client1AcPolicies = policyRepository.findByClientIdAndInsuranceType(client1.getId(), InsuranceType.AC);
        
        // Then
        assertThat(client1OcPolicies).hasSize(1);
        assertThat(client1OcPolicies.get(0).getPolicyNumber()).isEqualTo("POL-001");
        
        assertThat(client1AcPolicies).hasSize(1);
        assertThat(client1AcPolicies.get(0).getPolicyNumber()).isEqualTo("POL-002");
    }
    
    @Test
    void shouldFindPoliciesByIssueDateBetween() {
        // When
        LocalDate startDate = LocalDate.now().minusDays(45);
        LocalDate endDate = LocalDate.now().minusDays(15);
        List<Policy> policies = policyRepository.findByIssueDateBetween(startDate, endDate);
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getPolicyNumber()).isEqualTo("POL-001");
    }
    
    @Test
    void shouldFindCurrentlyActivePolicies() {
        // When
        List<Policy> currentlyActive = policyRepository.findCurrentlyActivePolicies(LocalDate.now());
        
        // Then
        assertThat(currentlyActive).hasSize(1);
        assertThat(currentlyActive.get(0).getPolicyNumber()).isEqualTo("POL-001");
    }
    
    @Test
    void shouldFindPoliciesExpiringWithinDays() {
        // When
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = currentDate.plusDays(365);
        List<Policy> expiringPolicies = policyRepository.findPoliciesExpiringWithinDays(currentDate, expirationDate);
        
        // Then
        assertThat(expiringPolicies).hasSize(1);
        assertThat(expiringPolicies.get(0).getPolicyNumber()).isEqualTo("POL-001");
    }
    
    @Test
    void shouldFindPoliciesWithComplexCriteria() {
        // This tests the complex search functionality
        // When - search for client1's active OC policies
        List<Policy> policies = policyRepository.findPoliciesWithCriteria(
                client1.getId(), 
                null, 
                PolicyStatus.ACTIVE, 
                InsuranceType.OC, 
                null, 
                null
        );
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getPolicyNumber()).isEqualTo("POL-001");
    }
    
    @Test
    void shouldFindPoliciesWithCriteriaAllNull() {
        // When - search with all null criteria (should return all policies)
        List<Policy> policies = policyRepository.findPoliciesWithCriteria(null, null, null, null, null, null);
        
        // Then
        assertThat(policies).hasSize(3);
    }
    
    @Test
    void shouldFindPoliciesByClientNameContainingIgnoreCase() {
        // When
        List<Policy> policies = policyRepository.findByClientNameContainingIgnoreCase("kowal");
        
        // Then
        assertThat(policies).hasSize(2);
        assertThat(policies).extracting(Policy::getPolicyNumber)
                .containsExactlyInAnyOrder("POL-001", "POL-002");
    }
    
    @Test
    void shouldFindPoliciesByVehicleRegistrationNumber() {
        // When
        List<Policy> policies = policyRepository.findByVehicleRegistrationNumber("WA12345");
        
        // Then
        assertThat(policies).hasSize(2);
        assertThat(policies).extracting(Policy::getPolicyNumber)
                .containsExactlyInAnyOrder("POL-001", "POL-003");
    }
    
    @Test
    void shouldSaveAndRetrievePolicy() {
        // Given
        Policy newPolicy = Policy.builder()
                .policyNumber("POL-004")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1500.00))
                .client(client2)
                .vehicle(vehicle2)
                .build();
        
        // When
        Policy saved = policyRepository.save(newPolicy);
        Optional<Policy> retrieved = policyRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPolicyNumber()).isEqualTo("POL-004");
        assertThat(retrieved.get().getPremium()).isEqualTo(BigDecimal.valueOf(1500.00));
    }
}