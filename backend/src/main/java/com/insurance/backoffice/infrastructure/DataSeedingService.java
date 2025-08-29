package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.config.DataSeedingConfig;
import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for seeding the database with sample data for development and testing.
 * This service runs only in development and test profiles to avoid affecting production data.
 * 
 * Clean Code Principles Applied:
 * - Single Responsibility: Focused only on data seeding
 * - Dependency Inversion: Depends on repository abstractions
 * - Meaningful Names: Clear method and variable names
 * - Small Methods: Each seeding operation is in its own method
 */
@Service
@Profile({"dev", "test"}) // Only run in development and test environments
public class DataSeedingService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeedingService.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final PolicyRepository policyRepository;
    private final RatingTableRepository ratingTableRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSeedingConfig config;

    @Autowired
    public DataSeedingService(
            UserRepository userRepository,
            ClientRepository clientRepository,
            VehicleRepository vehicleRepository,
            PolicyRepository policyRepository,
            RatingTableRepository ratingTableRepository,
            PasswordEncoder passwordEncoder,
            DataSeedingConfig config) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.policyRepository = policyRepository;
        this.ratingTableRepository = ratingTableRepository;
        this.passwordEncoder = passwordEncoder;
        this.config = config;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!config.isEnabled()) {
            logger.info("Data seeding is disabled");
            return;
        }

        logger.info("Starting data seeding process...");
        config.logConfiguration();
        
        if (shouldSeedData()) {
            if (config.isSeedUsers()) {
                seedUsers();
            }
            if (config.isSeedClients()) {
                seedClients();
            }
            if (config.isSeedVehicles()) {
                seedVehicles();
            }
            if (config.isSeedPolicies() && clientRepository.count() > 0 && vehicleRepository.count() > 0) {
                seedPolicies();
            }
            logger.info("Data seeding completed successfully");
        } else {
            logger.info("Data already exists, skipping seeding process");
        }
    }

    /**
     * Check if data seeding should be performed.
     * Only seed if no users exist to avoid duplicate data, unless force seeding is enabled.
     */
    private boolean shouldSeedData() {
        if (config.isForceSeeding()) {
            logger.warn("Force seeding is enabled - existing data may be affected");
            return true;
        }
        return userRepository.count() == 0;
    }

    /**
     * Seed sample users for testing different roles and scenarios.
     */
    private void seedUsers() {
        logger.info("Seeding sample users...");
        
        List<User> users = List.of(
            createUser("John", "Administrator", "admin@insurance.com", "admin123", UserRole.ADMIN),
            createUser("Sarah", "Manager", "sarah.manager@insurance.com", "admin456", UserRole.ADMIN),
            createUser("Mike", "Johnson", "mike.johnson@insurance.com", "operator123", UserRole.OPERATOR),
            createUser("Lisa", "Williams", "lisa.williams@insurance.com", "operator456", UserRole.OPERATOR),
            createUser("David", "Brown", "david.brown@insurance.com", "operator789", UserRole.OPERATOR),
            createUser("Emma", "Davis", "emma.davis@insurance.com", "operator321", UserRole.OPERATOR)
        );
        
        userRepository.saveAll(users);
        logger.info("Seeded {} users", users.size());
    }

    /**
     * Seed sample clients with diverse data for testing.
     */
    private void seedClients() {
        logger.info("Seeding sample clients...");
        
        List<Client> clients = List.of(
            createClient("Jan Kowalski", "85010112345", "ul. Marszałkowska 123/45, 00-001 Warszawa", 
                        "jan.kowalski@email.com", "+48 123 456 789"),
            createClient("Anna Nowak", "90052298765", "ul. Krakowska 67/12, 31-066 Kraków", 
                        "anna.nowak@gmail.com", "+48 234 567 890"),
            createClient("Piotr Wiśniewski", "78111587654", "ul. Gdańska 89/3, 80-309 Gdańsk", 
                        "piotr.wisniewski@outlook.com", "+48 345 678 901"),
            createClient("Maria Wójcik", "92030445678", "ul. Wrocławska 234/56, 50-001 Wrocław", 
                        "maria.wojcik@yahoo.com", "+48 456 789 012"),
            createClient("Tomasz Kowalczyk", "87070723456", "ul. Poznańska 45/7, 60-001 Poznań", 
                        "tomasz.kowalczyk@email.com", "+48 567 890 123"),
            createClient("Katarzyna Lewandowska", "83041534567", "ul. Łódzka 178/23, 90-001 Łódź", 
                        "katarzyna.lewandowska@company.pl", "+48 678 901 234"),
            createClient("Michał Zieliński", "89122156789", "ul. Szczecińska 90/11, 70-001 Szczecin", 
                        "michal.zielinski@business.com", "+48 789 012 345"),
            createClient("Agnieszka Szymańska", "91081267890", "ul. Bydgoska 156/8, 85-001 Bydgoszcz", 
                        "agnieszka.szymanska@firm.pl", "+48 890 123 456"),
            createClient("Jakub Dąbrowski", "00030178901", "ul. Lubelska 67/14, 20-001 Lublin", 
                        "jakub.dabrowski@student.edu.pl", "+48 901 234 567"),
            createClient("Natalia Kamińska", "99121289012", "ul. Białostocka 123/9, 15-001 Białystok", 
                        "natalia.kaminska@young.com", "+48 012 345 678")
        );
        
        clientRepository.saveAll(clients);
        logger.info("Seeded {} clients", clients.size());
    }

    /**
     * Seed sample vehicles with different characteristics for rating testing.
     */
    private void seedVehicles() {
        logger.info("Seeding sample vehicles...");
        
        List<Vehicle> vehicles = List.of(
            createVehicle("Toyota", "Corolla", 2020, "WA12345", "JTDBL40E309012345", 1600, 132, LocalDate.of(2020, 3, 15)),
            createVehicle("Volkswagen", "Golf", 2019, "KR67890", "WVWZZZ1JZ3W123456", 1400, 125, LocalDate.of(2019, 7, 22)),
            createVehicle("Skoda", "Octavia", 2021, "GD11111", "TMBJF25J0C7123456", 2000, 190, LocalDate.of(2021, 1, 10)),
            createVehicle("Ford", "Focus", 2018, "WR22222", "1FADP3F23EL123456", 1500, 150, LocalDate.of(2018, 5, 18)),
            createVehicle("Opel", "Astra", 2017, "PO33333", "W0L0AHL08E4123456", 1600, 136, LocalDate.of(2017, 9, 12)),
            createVehicle("BMW", "320d", 2022, "WA44444", "WBA8E5G50JNU12345", 2000, 190, LocalDate.of(2022, 2, 28)),
            createVehicle("Mercedes-Benz", "C220", 2021, "KR55555", "WDD2050461F123456", 2200, 194, LocalDate.of(2021, 6, 14)),
            createVehicle("Audi", "A4", 2020, "GD66666", "WAUZZZ8K0DA123456", 2000, 204, LocalDate.of(2020, 11, 3)),
            createVehicle("Fiat", "500", 2019, "WR77777", "ZFA31200000123456", 900, 85, LocalDate.of(2019, 4, 25)),
            createVehicle("Peugeot", "208", 2020, "PO88888", "VF3C68HM0FS123456", 1200, 100, LocalDate.of(2020, 8, 17))
        );
        
        vehicleRepository.saveAll(vehicles);
        logger.info("Seeded {} vehicles", vehicles.size());
    }

    /**
     * Seed sample policies covering all insurance types and scenarios.
     */
    private void seedPolicies() {
        logger.info("Seeding sample policies...");
        
        List<Client> clients = clientRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        
        if (clients.size() < 10 || vehicles.size() < 10) {
            logger.warn("Insufficient clients or vehicles for policy seeding");
            return;
        }
        
        List<Policy> policies = List.of(
            createOCPolicy("OC-2024-001001", clients.get(0), vehicles.get(0), BigDecimal.valueOf(850.00), BigDecimal.ZERO),
            createOCPolicy("OC-2024-001002", clients.get(1), vehicles.get(1), BigDecimal.valueOf(920.50), BigDecimal.valueOf(-50.00)),
            createACPolicy("AC-2024-002001", clients.get(2), vehicles.get(5), BigDecimal.valueOf(2450.00), BigDecimal.ZERO, ACVariant.STANDARD),
            createACPolicy("AC-2024-002002", clients.get(3), vehicles.get(6), BigDecimal.valueOf(2890.75), BigDecimal.valueOf(-150.00), ACVariant.MAXIMUM),
            createNNWPolicy("NNW-2024-003001", clients.get(4), vehicles.get(8), BigDecimal.valueOf(180.00), BigDecimal.ZERO),
            createNNWPolicy("NNW-2024-003002", clients.get(5), vehicles.get(9), BigDecimal.valueOf(220.50), BigDecimal.valueOf(-20.00))
        );
        
        policyRepository.saveAll(policies);
        logger.info("Seeded {} policies", policies.size());
    }

    // Helper methods for creating entities with Builder pattern

    private User createUser(String firstName, String lastName, String email, String password, UserRole role) {
        return new User.Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
    }

    private Client createClient(String fullName, String pesel, String address, String email, String phoneNumber) {
        return new Client.Builder()
                .fullName(fullName)
                .pesel(pesel)
                .address(address)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }

    private Vehicle createVehicle(String make, String model, Integer year, String registration, 
                                 String vin, Integer engineCapacity, Integer power, LocalDate firstRegistration) {
        return new Vehicle.Builder()
                .make(make)
                .model(model)
                .yearOfManufacture(year)
                .registrationNumber(registration)
                .vin(vin)
                .engineCapacity(engineCapacity)
                .power(power)
                .firstRegistrationDate(firstRegistration)
                .build();
    }

    private Policy createOCPolicy(String policyNumber, Client client, Vehicle vehicle, 
                                 BigDecimal premium, BigDecimal discountSurcharge) {
        Policy policy = new Policy.Builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now().minusDays(30))
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now().plusDays(350))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(premium)
                .discountSurcharge(discountSurcharge)
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = new PolicyDetails.Builder()
                .policy(policy)
                .guaranteedSum(BigDecimal.valueOf(5000000))
                .coverageArea("Europe")
                .build();

        policy.setPolicyDetails(details);
        return policy;
    }

    private Policy createACPolicy(String policyNumber, Client client, Vehicle vehicle, 
                                 BigDecimal premium, BigDecimal discountSurcharge, ACVariant variant) {
        Policy policy = new Policy.Builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now().minusDays(25))
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().plusDays(355))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.AC)
                .premium(premium)
                .discountSurcharge(discountSurcharge)
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = new PolicyDetails.Builder()
                .policy(policy)
                .acVariant(variant)
                .sumInsured(BigDecimal.valueOf(150000))
                .coverageScope("Comprehensive coverage including theft, fire, vandalism")
                .deductible(BigDecimal.valueOf(500))
                .workshopType("Authorized")
                .build();

        policy.setPolicyDetails(details);
        return policy;
    }

    private Policy createNNWPolicy(String policyNumber, Client client, Vehicle vehicle, 
                                  BigDecimal premium, BigDecimal discountSurcharge) {
        Policy policy = new Policy.Builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now().minusDays(20))
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(360))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.NNW)
                .premium(premium)
                .discountSurcharge(discountSurcharge)
                .client(client)
                .vehicle(vehicle)
                .build();

        PolicyDetails details = new PolicyDetails.Builder()
                .policy(policy)
                .sumInsured(BigDecimal.valueOf(100000))
                .coveredPersons("Driver and passengers (max 5 persons)")
                .build();

        policy.setPolicyDetails(details);
        return policy;
    }
}