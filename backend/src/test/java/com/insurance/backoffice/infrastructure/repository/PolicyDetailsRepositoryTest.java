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
 * Integration tests for PolicyDetailsRepository using H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PolicyDetailsRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PolicyDetailsRepository policyDetailsRepository;
    
    private Client client;
    private Vehicle vehicle;
    private Policy ocPolicy;
    private Policy acPolicy;
    private Policy nnwPolicy;
    private Policy canceledPolicy;
    private PolicyDetails ocDetails;
    private PolicyDetails acDetails;
    private PolicyDetails nnwDetails;
    private PolicyDetails canceledDetails;
    
    @BeforeEach
    void setUp() {
        // Create test client and vehicle
        client = Client.builder()
                .fullName("John Kowalski")
                .pesel("12345678901")
                .address("ul. Testowa 1, 00-001 Warszawa")
                .email("john.kowalski@example.com")
                .phoneNumber("+48123456789")
                .build();
        
        vehicle = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("WA12345")
                .vin("JT123456789012345")
                .engineCapacity(1600)
                .power(132)
                .firstRegistrationDate(LocalDate.of(2020, 5, 15))
                .build();
        
        entityManager.persistAndFlush(client);
        entityManager.persistAndFlush(vehicle);
        
        // Create test policies
        ocPolicy = Policy.builder()
                .policyNumber("POL-OC-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1200.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        acPolicy = Policy.builder()
                .policyNumber("POL-AC-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.AC)
                .premium(BigDecimal.valueOf(2500.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        nnwPolicy = Policy.builder()
                .policyNumber("POL-NNW-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.NNW)
                .premium(BigDecimal.valueOf(800.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        canceledPolicy = Policy.builder()
                .policyNumber("POL-AC-002")
                .issueDate(LocalDate.now().minusDays(30))
                .startDate(LocalDate.now().minusDays(30))
                .endDate(LocalDate.now().plusDays(335))
                .status(PolicyStatus.CANCELED)
                .insuranceType(InsuranceType.AC)
                .premium(BigDecimal.valueOf(3000.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        entityManager.persistAndFlush(ocPolicy);
        entityManager.persistAndFlush(acPolicy);
        entityManager.persistAndFlush(nnwPolicy);
        entityManager.persistAndFlush(canceledPolicy);
        
        // Create policy details
        ocDetails = PolicyDetails.builder()
                .policy(ocPolicy)
                .guaranteedSum(BigDecimal.valueOf(1000000))
                .coverageArea("Europe")
                .build();
        
        acDetails = PolicyDetails.builder()
                .policy(acPolicy)
                .acVariant(ACVariant.STANDARD)
                .sumInsured(BigDecimal.valueOf(50000))
                .coverageScope("Comprehensive coverage including theft and damage")
                .deductible(BigDecimal.valueOf(500))
                .workshopType("Authorized")
                .build();
        
        nnwDetails = PolicyDetails.builder()
                .policy(nnwPolicy)
                .sumInsured(BigDecimal.valueOf(100000))
                .coveredPersons("Driver and passengers")
                .build();
        
        canceledDetails = PolicyDetails.builder()
                .policy(canceledPolicy)
                .acVariant(ACVariant.MAXIMUM)
                .sumInsured(BigDecimal.valueOf(80000))
                .coverageScope("Maximum coverage with all options")
                .deductible(BigDecimal.valueOf(300))
                .workshopType("Premium")
                .build();
        
        entityManager.persistAndFlush(ocDetails);
        entityManager.persistAndFlush(acDetails);
        entityManager.persistAndFlush(nnwDetails);
        entityManager.persistAndFlush(canceledDetails);
        entityManager.clear();
    }
    
    @Test
    void shouldFindPolicyDetailsByPolicyId() {
        // When
        Optional<PolicyDetails> found = policyDetailsRepository.findByPolicyId(ocPolicy.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getGuaranteedSum()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
        assertThat(found.get().getCoverageArea()).isEqualTo("Europe");
    }
    
    @Test
    void shouldFindPolicyDetailsByPolicyNumber() {
        // When
        Optional<PolicyDetails> found = policyDetailsRepository.findByPolicyNumber("POL-AC-001");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAcVariant()).isEqualTo(ACVariant.STANDARD);
        assertThat(found.get().getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    }
    
    @Test
    void shouldFindPolicyDetailsByAcVariant() {
        // When
        List<PolicyDetails> standardVariant = policyDetailsRepository.findByAcVariant(ACVariant.STANDARD);
        List<PolicyDetails> maximumVariant = policyDetailsRepository.findByAcVariant(ACVariant.MAXIMUM);
        
        // Then
        assertThat(standardVariant).hasSize(1);
        assertThat(standardVariant.get(0).getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        
        assertThat(maximumVariant).hasSize(1);
        assertThat(maximumVariant.get(0).getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(80000));
    }
    
    @Test
    void shouldFindOCPoliciesWithGuaranteedSumBetween() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findOCPoliciesWithGuaranteedSumBetween(
                BigDecimal.valueOf(500000), BigDecimal.valueOf(1500000));
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getGuaranteedSum()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
    }
    
    @Test
    void shouldFindACPoliciesWithSumInsuredBetween() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findACPoliciesWithSumInsuredBetween(
                BigDecimal.valueOf(40000), BigDecimal.valueOf(60000));
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(policies.get(0).getAcVariant()).isEqualTo(ACVariant.STANDARD);
    }
    
    @Test
    void shouldFindNNWPoliciesWithSumInsuredBetween() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findNNWPoliciesWithSumInsuredBetween(
                BigDecimal.valueOf(50000), BigDecimal.valueOf(150000));
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(100000));
        assertThat(policies.get(0).getCoveredPersons()).isEqualTo("Driver and passengers");
    }
    
    @Test
    void shouldFindOCPoliciesByCoverageArea() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findOCPoliciesByCoverageArea("Europe");
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getGuaranteedSum()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
    }
    
    @Test
    void shouldFindOCPoliciesByCoverageAreaIgnoreCase() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findOCPoliciesByCoverageArea("EUROPE");
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getCoverageArea()).isEqualTo("Europe");
    }
    
    @Test
    void shouldFindACPoliciesByWorkshopType() {
        // When
        List<PolicyDetails> authorizedPolicies = policyDetailsRepository.findACPoliciesByWorkshopType("Authorized");
        List<PolicyDetails> premiumPolicies = policyDetailsRepository.findACPoliciesByWorkshopType("Premium");
        
        // Then
        assertThat(authorizedPolicies).hasSize(1);
        assertThat(authorizedPolicies.get(0).getAcVariant()).isEqualTo(ACVariant.STANDARD);
        
        assertThat(premiumPolicies).hasSize(1);
        assertThat(premiumPolicies.get(0).getAcVariant()).isEqualTo(ACVariant.MAXIMUM);
    }
    
    @Test
    void shouldFindACPoliciesWithDeductibleBetween() {
        // When
        List<PolicyDetails> policies = policyDetailsRepository.findACPoliciesWithDeductibleBetween(
                BigDecimal.valueOf(200), BigDecimal.valueOf(600));
        
        // Then
        assertThat(policies).hasSize(2);
        assertThat(policies).extracting(PolicyDetails::getDeductible)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactlyInAnyOrder(BigDecimal.valueOf(500), BigDecimal.valueOf(300));
    }
    
    @Test
    void shouldFindPolicyDetailsByInsuranceType() {
        // When
        List<PolicyDetails> ocPolicies = policyDetailsRepository.findByInsuranceType(InsuranceType.OC);
        List<PolicyDetails> acPolicies = policyDetailsRepository.findByInsuranceType(InsuranceType.AC);
        List<PolicyDetails> nnwPolicies = policyDetailsRepository.findByInsuranceType(InsuranceType.NNW);
        
        // Then
        assertThat(ocPolicies).hasSize(1);
        assertThat(ocPolicies.get(0).getGuaranteedSum()).isNotNull();
        
        assertThat(acPolicies).hasSize(2); // One active, one canceled
        assertThat(acPolicies).allMatch(pd -> pd.getAcVariant() != null);
        
        assertThat(nnwPolicies).hasSize(1);
        assertThat(nnwPolicies.get(0).getCoveredPersons()).isNotNull();
    }
    
    @Test
    void shouldFindPolicyDetailsForActivePolicies() {
        // When
        List<PolicyDetails> activePolicyDetails = policyDetailsRepository.findForActivePolicies();
        
        // Then
        assertThat(activePolicyDetails).hasSize(3); // OC, AC, NNW (not the canceled one)
        assertThat(activePolicyDetails).extracting(pd -> pd.getPolicy().getStatus())
                .allMatch(status -> status == PolicyStatus.ACTIVE);
    }
    
    @Test
    void shouldReturnEmptyWhenPolicyDetailsNotFoundByPolicyId() {
        // When
        Optional<PolicyDetails> found = policyDetailsRepository.findByPolicyId(999L);
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldReturnEmptyWhenPolicyDetailsNotFoundByPolicyNumber() {
        // When
        Optional<PolicyDetails> found = policyDetailsRepository.findByPolicyNumber("NONEXISTENT");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldSaveAndRetrievePolicyDetails() {
        // Given
        Policy newPolicy = Policy.builder()
                .policyNumber("POL-TEST-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.AC)
                .premium(BigDecimal.valueOf(2000.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        entityManager.persistAndFlush(newPolicy);
        
        PolicyDetails newDetails = PolicyDetails.builder()
                .policy(newPolicy)
                .acVariant(ACVariant.MAXIMUM)
                .sumInsured(BigDecimal.valueOf(75000))
                .coverageScope("Test coverage")
                .deductible(BigDecimal.valueOf(400))
                .workshopType("Test workshop")
                .build();
        
        // When
        PolicyDetails saved = policyDetailsRepository.save(newDetails);
        Optional<PolicyDetails> retrieved = policyDetailsRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getAcVariant()).isEqualTo(ACVariant.MAXIMUM);
        assertThat(retrieved.get().getSumInsured()).isEqualByComparingTo(BigDecimal.valueOf(75000));
    }
    
    @Test
    void shouldDeletePolicyDetails() {
        // Given
        Long detailsId = nnwDetails.getId();
        
        // When
        policyDetailsRepository.deleteById(detailsId);
        Optional<PolicyDetails> deleted = policyDetailsRepository.findById(detailsId);
        
        // Then
        assertThat(deleted).isEmpty();
    }
}