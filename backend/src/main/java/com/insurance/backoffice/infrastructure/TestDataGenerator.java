package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating test data programmatically.
 * Useful for integration tests and development scenarios where specific test data is needed.
 * 
 * Clean Code Principles Applied:
 * - Single Responsibility: Focused only on test data generation
 * - Pure Functions: Methods don't have side effects
 * - Meaningful Names: Clear method names that describe what they generate
 * - Parameterized: Allows customization of generated data
 */
@Component
public class TestDataGenerator {

    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;

    // Sample data arrays for realistic test data generation
    private static final String[] FIRST_NAMES = {
        "Jan", "Anna", "Piotr", "Maria", "Tomasz", "Katarzyna", "Michał", "Agnieszka",
        "Jakub", "Natalia", "Stanisław", "Halina", "Robert", "Magdalena", "Paweł"
    };

    private static final String[] LAST_NAMES = {
        "Kowalski", "Nowak", "Wiśniewski", "Wójcik", "Kowalczyk", "Lewandowski",
        "Zieliński", "Szymański", "Dąbrowski", "Kamiński", "Jankowski", "Mazur"
    };

    private static final String[] CAR_MAKES = {
        "Toyota", "Volkswagen", "Skoda", "Ford", "Opel", "BMW", "Mercedes-Benz",
        "Audi", "Fiat", "Peugeot", "Renault", "Hyundai", "Kia", "Nissan", "Mazda"
    };

    private static final String[] CAR_MODELS = {
        "Corolla", "Golf", "Octavia", "Focus", "Astra", "320d", "C220", "A4",
        "500", "208", "Clio", "i30", "Ceed", "Qashqai", "CX-5"
    };

    private static final String[] CITIES = {
        "Warszawa", "Kraków", "Gdańsk", "Wrocław", "Poznań", "Łódź", "Szczecin",
        "Bydgoszcz", "Lublin", "Białystok", "Rzeszów", "Kielce", "Katowice"
    };

    public TestDataGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate a test user with specified role
     */
    public User generateUser(UserRole role) {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);
        String email = generateEmail(firstName, lastName);
        
        return new User.Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode("test123"))
                .role(role)
                .build();
    }

    /**
     * Generate multiple test users
     */
    public List<User> generateUsers(int count, UserRole role) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateUser(role));
        }
        return users;
    }

    /**
     * Generate a test client with realistic Polish data
     */
    public Client generateClient() {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);
        String fullName = firstName + " " + lastName;
        
        return new Client.Builder()
                .fullName(fullName)
                .pesel(generatePesel())
                .address(generateAddress())
                .email(generateEmail(firstName, lastName))
                .phoneNumber(generatePhoneNumber())
                .build();
    }

    /**
     * Generate multiple test clients
     */
    public List<Client> generateClients(int count) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            clients.add(generateClient());
        }
        return clients;
    }

    /**
     * Generate a test vehicle with realistic specifications
     */
    public Vehicle generateVehicle() {
        String make = getRandomElement(CAR_MAKES);
        String model = getRandomElement(CAR_MODELS);
        int year = 2015 + random.nextInt(9); // 2015-2023
        
        return new Vehicle.Builder()
                .make(make)
                .model(model)
                .yearOfManufacture(year)
                .registrationNumber(generateRegistrationNumber())
                .vin(generateVin())
                .engineCapacity(generateEngineCapacity())
                .power(generatePower())
                .firstRegistrationDate(generateRegistrationDate(year))
                .build();
    }

    /**
     * Generate multiple test vehicles
     */
    public List<Vehicle> generateVehicles(int count) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vehicles.add(generateVehicle());
        }
        return vehicles;
    }

    /**
     * Generate a test policy for given client and vehicle
     */
    public Policy generatePolicy(Client client, Vehicle vehicle, InsuranceType insuranceType) {
        String policyNumber = generatePolicyNumber(insuranceType);
        LocalDate issueDate = LocalDate.now().minusDays(random.nextInt(90));
        LocalDate startDate = issueDate.plusDays(random.nextInt(30));
        LocalDate endDate = startDate.plusYears(1);
        
        Policy policy = new Policy.Builder()
                .policyNumber(policyNumber)
                .issueDate(issueDate)
                .startDate(startDate)
                .endDate(endDate)
                .status(PolicyStatus.ACTIVE)
                .insuranceType(insuranceType)
                .premium(generatePremium(insuranceType))
                .discountSurcharge(generateDiscountSurcharge())
                .client(client)
                .vehicle(vehicle)
                .build();

        // Add policy details based on insurance type
        PolicyDetails details = generatePolicyDetails(policy, insuranceType);
        policy.setPolicyDetails(details);

        return policy;
    }

    /**
     * Generate policy details based on insurance type
     */
    private PolicyDetails generatePolicyDetails(Policy policy, InsuranceType insuranceType) {
        PolicyDetails.Builder builder = new PolicyDetails.Builder().policy(policy);

        switch (insuranceType) {
            case OC:
                return builder
                        .guaranteedSum(BigDecimal.valueOf(5000000 + random.nextInt(2000000)))
                        .coverageArea(random.nextBoolean() ? "Europe" : "Poland")
                        .build();
            
            case AC:
                return builder
                        .acVariant(random.nextBoolean() ? ACVariant.STANDARD : ACVariant.MAXIMUM)
                        .sumInsured(BigDecimal.valueOf(100000 + random.nextInt(300000)))
                        .coverageScope("Comprehensive coverage including theft, fire, vandalism")
                        .deductible(BigDecimal.valueOf(200 + random.nextInt(800)))
                        .workshopType(random.nextBoolean() ? "Authorized" : "Any workshop")
                        .build();
            
            case NNW:
                return builder
                        .sumInsured(BigDecimal.valueOf(50000 + random.nextInt(150000)))
                        .coveredPersons("Driver and passengers (max " + (3 + random.nextInt(5)) + " persons)")
                        .build();
            
            default:
                throw new IllegalArgumentException("Unknown insurance type: " + insuranceType);
        }
    }

    // Helper methods for generating realistic test data

    private String getRandomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private String generateEmail(String firstName, String lastName) {
        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "email.com", "test.pl"};
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + getRandomElement(domains);
    }

    private String generatePesel() {
        // Generate a valid PESEL format (simplified for testing)
        StringBuilder pesel = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            pesel.append(random.nextInt(10));
        }
        return pesel.toString();
    }

    private String generateAddress() {
        String[] streetTypes = {"ul.", "al.", "os.", "pl."};
        String[] streetNames = {"Marszałkowska", "Krakowska", "Gdańska", "Wrocławska", "Poznańska"};
        String city = getRandomElement(CITIES);
        
        return getRandomElement(streetTypes) + " " + getRandomElement(streetNames) + " " +
               (1 + random.nextInt(999)) + "/" + (1 + random.nextInt(99)) + ", " +
               String.format("%02d-%03d", random.nextInt(100), random.nextInt(1000)) + " " + city;
    }

    private String generatePhoneNumber() {
        return "+48 " + (100 + random.nextInt(900)) + " " + 
               (100 + random.nextInt(900)) + " " + (100 + random.nextInt(900));
    }

    private String generateRegistrationNumber() {
        String[] prefixes = {"WA", "KR", "GD", "WR", "PO", "LD", "SZ", "BY", "LU", "BI"};
        return getRandomElement(prefixes) + String.format("%05d", random.nextInt(100000));
    }

    private String generateVin() {
        // Generate a simplified VIN for testing (17 characters)
        StringBuilder vin = new StringBuilder();
        String chars = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        for (int i = 0; i < 17; i++) {
            vin.append(chars.charAt(random.nextInt(chars.length())));
        }
        return vin.toString();
    }

    private Integer generateEngineCapacity() {
        int[] capacities = {900, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2500, 3000};
        return capacities[random.nextInt(capacities.length)];
    }

    private Integer generatePower() {
        return 75 + random.nextInt(400); // 75-475 HP
    }

    private LocalDate generateRegistrationDate(int year) {
        return LocalDate.of(year, 1 + random.nextInt(12), 1 + random.nextInt(28));
    }

    private String generatePolicyNumber(InsuranceType insuranceType) {
        return insuranceType.name() + "-2024-" + String.format("%06d", random.nextInt(1000000));
    }

    private BigDecimal generatePremium(InsuranceType insuranceType) {
        switch (insuranceType) {
            case OC:
                return BigDecimal.valueOf(600 + random.nextInt(800)); // 600-1400
            case AC:
                return BigDecimal.valueOf(1500 + random.nextInt(3000)); // 1500-4500
            case NNW:
                return BigDecimal.valueOf(100 + random.nextInt(200)); // 100-300
            default:
                return BigDecimal.valueOf(500);
        }
    }

    private BigDecimal generateDiscountSurcharge() {
        if (random.nextDouble() < 0.3) { // 30% chance of discount/surcharge
            return BigDecimal.valueOf(-200 + random.nextInt(400)); // -200 to +200
        }
        return BigDecimal.ZERO;
    }
}