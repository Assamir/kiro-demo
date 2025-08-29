package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TestDataGenerator.
 * Verifies that generated test data meets expected criteria.
 * 
 * Clean Code Principles Applied:
 * - Single Responsibility: Each test verifies one aspect of data generation
 * - Meaningful Names: Test names clearly describe what they verify
 * - Assertions: Use AssertJ for readable assertions
 */
@ExtendWith(MockitoExtension.class)
class TestDataGeneratorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private TestDataGenerator testDataGenerator;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        testDataGenerator = new TestDataGenerator(passwordEncoder);
    }

    @Test
    void shouldGenerateUserWithCorrectRole() {
        // When
        User adminUser = testDataGenerator.generateUser(UserRole.ADMIN);
        User operatorUser = testDataGenerator.generateUser(UserRole.OPERATOR);

        // Then
        assertThat(adminUser.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(operatorUser.getRole()).isEqualTo(UserRole.OPERATOR);
        
        assertThat(adminUser.getFirstName()).isNotBlank();
        assertThat(adminUser.getLastName()).isNotBlank();
        assertThat(adminUser.getEmail()).contains("@");
        assertThat(adminUser.getPassword()).isEqualTo("encoded_password");
    }

    @Test
    void shouldGenerateMultipleUsers() {
        // When
        List<User> users = testDataGenerator.generateUsers(5, UserRole.OPERATOR);

        // Then
        assertThat(users).hasSize(5);
        assertThat(users).allMatch(user -> user.getRole() == UserRole.OPERATOR);
        assertThat(users).allMatch(user -> user.getFirstName() != null);
        assertThat(users).allMatch(user -> user.getEmail().contains("@"));
    }

    @Test
    void shouldGenerateClientWithValidData() {
        // When
        Client client = testDataGenerator.generateClient();

        // Then
        assertThat(client.getFullName()).isNotBlank();
        assertThat(client.getPesel()).hasSize(11);
        assertThat(client.getPesel()).matches("\\d{11}"); // 11 digits
        assertThat(client.getAddress()).isNotBlank();
        assertThat(client.getEmail()).contains("@");
        assertThat(client.getPhoneNumber()).startsWith("+48");
    }

    @Test
    void shouldGenerateMultipleClients() {
        // When
        List<Client> clients = testDataGenerator.generateClients(3);

        // Then
        assertThat(clients).hasSize(3);
        assertThat(clients).allMatch(client -> client.getFullName() != null);
        assertThat(clients).allMatch(client -> client.getPesel().length() == 11);
        
        // Verify uniqueness of generated data
        List<String> emails = clients.stream().map(Client::getEmail).toList();
        assertThat(emails).doesNotHaveDuplicates();
    }

    @Test
    void shouldGenerateVehicleWithValidSpecifications() {
        // When
        Vehicle vehicle = testDataGenerator.generateVehicle();

        // Then
        assertThat(vehicle.getMake()).isNotBlank();
        assertThat(vehicle.getModel()).isNotBlank();
        assertThat(vehicle.getYearOfManufacture()).isBetween(2015, 2023);
        assertThat(vehicle.getRegistrationNumber()).hasSize(7); // Format: XX12345
        assertThat(vehicle.getVin()).hasSize(17);
        assertThat(vehicle.getEngineCapacity()).isPositive();
        assertThat(vehicle.getPower()).isPositive();
        assertThat(vehicle.getFirstRegistrationDate()).isNotNull();
    }

    @Test
    void shouldGenerateMultipleVehicles() {
        // When
        List<Vehicle> vehicles = testDataGenerator.generateVehicles(4);

        // Then
        assertThat(vehicles).hasSize(4);
        assertThat(vehicles).allMatch(vehicle -> vehicle.getMake() != null);
        assertThat(vehicles).allMatch(vehicle -> vehicle.getVin().length() == 17);
        
        // Verify uniqueness of VINs and registration numbers
        List<String> vins = vehicles.stream().map(Vehicle::getVin).toList();
        List<String> registrations = vehicles.stream().map(Vehicle::getRegistrationNumber).toList();
        assertThat(vins).doesNotHaveDuplicates();
        assertThat(registrations).doesNotHaveDuplicates();
    }

    @Test
    void shouldGenerateOCPolicyWithCorrectDetails() {
        // Given
        Client client = testDataGenerator.generateClient();
        Vehicle vehicle = testDataGenerator.generateVehicle();

        // When
        Policy policy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.OC);

        // Then
        assertThat(policy.getInsuranceType()).isEqualTo(InsuranceType.OC);
        assertThat(policy.getClient()).isEqualTo(client);
        assertThat(policy.getVehicle()).isEqualTo(vehicle);
        assertThat(policy.getPolicyNumber()).startsWith("OC-");
        assertThat(policy.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
        assertThat(policy.getPremium()).isPositive();
        
        PolicyDetails details = policy.getPolicyDetails();
        assertThat(details).isNotNull();
        assertThat(details.getGuaranteedSum()).isPositive();
        assertThat(details.getCoverageArea()).isIn("Europe", "Poland");
    }

    @Test
    void shouldGenerateACPolicyWithCorrectDetails() {
        // Given
        Client client = testDataGenerator.generateClient();
        Vehicle vehicle = testDataGenerator.generateVehicle();

        // When
        Policy policy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.AC);

        // Then
        assertThat(policy.getInsuranceType()).isEqualTo(InsuranceType.AC);
        assertThat(policy.getPolicyNumber()).startsWith("AC-");
        
        PolicyDetails details = policy.getPolicyDetails();
        assertThat(details).isNotNull();
        assertThat(details.getAcVariant()).isIn(ACVariant.STANDARD, ACVariant.MAXIMUM);
        assertThat(details.getSumInsured()).isPositive();
        assertThat(details.getCoverageScope()).isNotBlank();
        assertThat(details.getDeductible()).isPositive();
        assertThat(details.getWorkshopType()).isIn("Authorized", "Any workshop");
    }

    @Test
    void shouldGenerateNNWPolicyWithCorrectDetails() {
        // Given
        Client client = testDataGenerator.generateClient();
        Vehicle vehicle = testDataGenerator.generateVehicle();

        // When
        Policy policy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.NNW);

        // Then
        assertThat(policy.getInsuranceType()).isEqualTo(InsuranceType.NNW);
        assertThat(policy.getPolicyNumber()).startsWith("NNW-");
        
        PolicyDetails details = policy.getPolicyDetails();
        assertThat(details).isNotNull();
        assertThat(details.getSumInsured()).isPositive();
        assertThat(details.getCoveredPersons()).contains("Driver");
    }

    @Test
    void shouldGenerateDifferentPremiumsForDifferentInsuranceTypes() {
        // Given
        Client client = testDataGenerator.generateClient();
        Vehicle vehicle = testDataGenerator.generateVehicle();

        // When
        Policy ocPolicy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.OC);
        Policy acPolicy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.AC);
        Policy nnwPolicy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.NNW);

        // Then
        // AC policies should generally be more expensive than OC
        assertThat(acPolicy.getPremium()).isGreaterThan(ocPolicy.getPremium());
        
        // NNW policies should generally be less expensive than OC
        assertThat(nnwPolicy.getPremium()).isLessThan(ocPolicy.getPremium());
        
        // Verify premium ranges are realistic
        assertThat(ocPolicy.getPremium().doubleValue()).isBetween(600.0, 1400.0);
        assertThat(acPolicy.getPremium().doubleValue()).isBetween(1500.0, 4500.0);
        assertThat(nnwPolicy.getPremium().doubleValue()).isBetween(100.0, 300.0);
    }
}