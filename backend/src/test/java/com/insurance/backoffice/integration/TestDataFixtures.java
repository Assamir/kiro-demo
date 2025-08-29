package com.insurance.backoffice.integration;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data fixtures for integration tests.
 * Provides methods to create consistent test data across different test scenarios.
 */
@Component
public class TestDataFixtures {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private RatingTableRepository ratingTableRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a complete test dataset with users, clients, vehicles, policies, and rating tables.
     */
    public TestDataSet createCompleteTestDataSet() {
        TestDataSet dataSet = new TestDataSet();
        
        // Create users
        dataSet.adminUser = createAdminUser("admin@fixture.com", "Admin", "Fixture");
        dataSet.operatorUser = createOperatorUser("operator@fixture.com", "Operator", "Fixture");
        
        // Create clients
        dataSet.clients = createTestClients();
        
        // Create vehicles
        dataSet.vehicles = createTestVehicles();
        
        // Create rating tables
        dataSet.ratingTables = createTestRatingTables();
        
        // Create policies
        dataSet.policies = createTestPolicies(dataSet.clients, dataSet.vehicles);
        
        return dataSet;
    }

    public User createAdminUser(String email, String firstName, String lastName) {
        User admin = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();
        return userRepository.save(admin);
    }

    public User createOperatorUser(String email, String firstName, String lastName) {
        User operator = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode("operator123"))
                .role(UserRole.OPERATOR)
                .build();
        return userRepository.save(operator);
    }

    public List<Client> createTestClients() {
        List<Client> clients = new ArrayList<>();
        
        clients.add(clientRepository.save(Client.builder()
                .fullName("Alice Johnson")
                .pesel("85010112345")
                .address("123 Main Street, Warsaw, Poland")
                .email("alice.johnson@example.com")
                .phoneNumber("+48123456789")
                .build()));
        
        clients.add(clientRepository.save(Client.builder()
                .fullName("Bob Wilson")
                .pesel("90050567890")
                .address("456 Oak Avenue, Krakow, Poland")
                .email("bob.wilson@example.com")
                .phoneNumber("+48987654321")
                .build()));
        
        clients.add(clientRepository.save(Client.builder()
                .fullName("Carol Davis")
                .pesel("78121298765")
                .address("789 Pine Road, Gdansk, Poland")
                .email("carol.davis@example.com")
                .phoneNumber("+48555666777")
                .build()));
        
        return clients;
    }

    public List<Vehicle> createTestVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        
        vehicles.add(vehicleRepository.save(Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("WA12345")
                .vin("1HGBH41JXMN109186")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 3, 15))
                .build()));
        
        vehicles.add(vehicleRepository.save(Vehicle.builder()
                .make("BMW")
                .model("320i")
                .yearOfManufacture(2019)
                .registrationNumber("KR67890")
                .vin("WBAFR9C50DD234567")
                .engineCapacity(2000)
                .power(184)
                .firstRegistrationDate(LocalDate.of(2019, 7, 22))
                .build()));
        
        vehicles.add(vehicleRepository.save(Vehicle.builder()
                .make("Volkswagen")
                .model("Golf")
                .yearOfManufacture(2021)
                .registrationNumber("GD11111")
                .vin("WVWZZZ1JZYW345678")
                .engineCapacity(1400)
                .power(150)
                .firstRegistrationDate(LocalDate.of(2021, 1, 10))
                .build()));
        
        return vehicles;
    }

    public List<RatingTable> createTestRatingTables() {
        List<RatingTable> ratingTables = new ArrayList<>();
        
        // OC Rating Tables
        ratingTables.add(createRatingTable(InsuranceType.OC, "driver_age_18_25", new BigDecimal("1.5")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "driver_age_26_35", new BigDecimal("1.2")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "driver_age_36_50", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "driver_age_51_plus", new BigDecimal("1.1")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "vehicle_age_0_3", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "vehicle_age_4_7", new BigDecimal("1.1")));
        ratingTables.add(createRatingTable(InsuranceType.OC, "vehicle_age_8_plus", new BigDecimal("1.3")));
        
        // AC Rating Tables
        ratingTables.add(createRatingTable(InsuranceType.AC, "vehicle_value_0_50k", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.AC, "vehicle_value_50k_100k", new BigDecimal("1.2")));
        ratingTables.add(createRatingTable(InsuranceType.AC, "vehicle_value_100k_plus", new BigDecimal("1.5")));
        ratingTables.add(createRatingTable(InsuranceType.AC, "deductible_500", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.AC, "deductible_1000", new BigDecimal("0.9")));
        ratingTables.add(createRatingTable(InsuranceType.AC, "deductible_2000", new BigDecimal("0.8")));
        
        // NNW Rating Tables
        ratingTables.add(createRatingTable(InsuranceType.NNW, "sum_insured_50k", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.NNW, "sum_insured_100k", new BigDecimal("1.3")));
        ratingTables.add(createRatingTable(InsuranceType.NNW, "sum_insured_150k", new BigDecimal("1.6")));
        ratingTables.add(createRatingTable(InsuranceType.NNW, "covered_persons_1", new BigDecimal("1.0")));
        ratingTables.add(createRatingTable(InsuranceType.NNW, "covered_persons_2_4", new BigDecimal("1.2")));
        ratingTables.add(createRatingTable(InsuranceType.NNW, "covered_persons_5_plus", new BigDecimal("1.4")));
        
        return ratingTables;
    }

    private RatingTable createRatingTable(InsuranceType insuranceType, String ratingKey, BigDecimal multiplier) {
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(insuranceType)
                .ratingKey(ratingKey)
                .multiplier(multiplier)
                .validFrom(LocalDate.now().minusYears(1))
                .validTo(LocalDate.now().plusYears(1))
                .build();
        return ratingTableRepository.save(ratingTable);
    }

    public List<Policy> createTestPolicies(List<Client> clients, List<Vehicle> vehicles) {
        List<Policy> policies = new ArrayList<>();
        
        if (clients.size() >= 3 && vehicles.size() >= 3) {
            // Create OC Policy
            Policy ocPolicy = createOCPolicy(clients.get(0), vehicles.get(0), "FIX-OC-001");
            policies.add(ocPolicy);
            
            // Create AC Policy
            Policy acPolicy = createACPolicy(clients.get(1), vehicles.get(1), "FIX-AC-001");
            policies.add(acPolicy);
            
            // Create NNW Policy
            Policy nnwPolicy = createNNWPolicy(clients.get(2), vehicles.get(2), "FIX-NNW-001");
            policies.add(nnwPolicy);
        }
        
        return policies;
    }

    public Policy createOCPolicy(Client client, Vehicle vehicle, String policyNumber) {
        Policy policy = Policy.builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1200.00"))
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = PolicyDetails.builder()
                .policy(policy)
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();

        policy.setPolicyDetails(details);
        return policyRepository.save(policy);
    }

    public Policy createACPolicy(Client client, Vehicle vehicle, String policyNumber) {
        Policy policy = Policy.builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.AC)
                .premium(new BigDecimal("2400.00"))
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = PolicyDetails.builder()
                .policy(policy)
                .acVariant(ACVariant.STANDARD)
                .sumInsured(new BigDecimal("60000"))
                .coverageScope("Comprehensive")
                .deductible(new BigDecimal("1000"))
                .workshopType("Authorized")
                .build();

        policy.setPolicyDetails(details);
        return policyRepository.save(policy);
    }

    public Policy createNNWPolicy(Client client, Vehicle vehicle, String policyNumber) {
        Policy policy = Policy.builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.NNW)
                .premium(new BigDecimal("600.00"))
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = PolicyDetails.builder()
                .policy(policy)
                .sumInsured(new BigDecimal("100000"))
                .coveredPersons("Driver and passengers")
                .build();

        policy.setPolicyDetails(details);
        return policyRepository.save(policy);
    }

    /**
     * Data structure to hold a complete test dataset.
     */
    public static class TestDataSet {
        public User adminUser;
        public User operatorUser;
        public List<Client> clients;
        public List<Vehicle> vehicles;
        public List<Policy> policies;
        public List<RatingTable> ratingTables;
    }
}