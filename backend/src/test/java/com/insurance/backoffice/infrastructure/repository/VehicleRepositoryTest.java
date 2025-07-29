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
 * Integration tests for VehicleRepository using H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class VehicleRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    private Vehicle toyotaCorolla;
    private Vehicle bmwX5;
    private Vehicle audiA4;
    private Client client;
    
    @BeforeEach
    void setUp() {
        // Create test client
        client = Client.builder()
                .fullName("John Kowalski")
                .pesel("12345678901")
                .address("ul. Testowa 1, 00-001 Warszawa")
                .email("john.kowalski@example.com")
                .phoneNumber("+48123456789")
                .build();
        
        // Create test vehicles
        toyotaCorolla = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("WA12345")
                .vin("JT123456789012345")
                .engineCapacity(1600)
                .power(132)
                .firstRegistrationDate(LocalDate.of(2020, 5, 15))
                .build();
        
        bmwX5 = Vehicle.builder()
                .make("BMW")
                .model("X5")
                .yearOfManufacture(2019)
                .registrationNumber("KR67890")
                .vin("WBA12345678901234")
                .engineCapacity(3000)
                .power(265)
                .firstRegistrationDate(LocalDate.of(2019, 8, 20))
                .build();
        
        audiA4 = Vehicle.builder()
                .make("Audi")
                .model("A4")
                .yearOfManufacture(2021)
                .registrationNumber("GD11111")
                .vin("WAU12345678901234")
                .engineCapacity(2000)
                .power(190)
                .firstRegistrationDate(LocalDate.of(2021, 3, 10))
                .build();
        
        entityManager.persistAndFlush(client);
        entityManager.persistAndFlush(toyotaCorolla);
        entityManager.persistAndFlush(bmwX5);
        entityManager.persistAndFlush(audiA4);
        
        // Create an active policy for toyotaCorolla
        Policy activePolicy = Policy.builder()
                .policyNumber("POL-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1200.00))
                .client(client)
                .vehicle(toyotaCorolla)
                .build();
        
        entityManager.persistAndFlush(activePolicy);
        entityManager.clear();
    }
    
    @Test
    void shouldFindVehicleByRegistrationNumber() {
        // When
        Optional<Vehicle> found = vehicleRepository.findByRegistrationNumber("WA12345");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMake()).isEqualTo("Toyota");
        assertThat(found.get().getModel()).isEqualTo("Corolla");
    }
    
    @Test
    void shouldFindVehicleByVin() {
        // When
        Optional<Vehicle> found = vehicleRepository.findByVin("WBA12345678901234");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMake()).isEqualTo("BMW");
        assertThat(found.get().getModel()).isEqualTo("X5");
    }
    
    @Test
    void shouldCheckIfVehicleExistsByRegistrationNumber() {
        // When & Then
        assertThat(vehicleRepository.existsByRegistrationNumber("WA12345")).isTrue();
        assertThat(vehicleRepository.existsByRegistrationNumber("XX99999")).isFalse();
    }
    
    @Test
    void shouldCheckIfVehicleExistsByVin() {
        // When & Then
        assertThat(vehicleRepository.existsByVin("JT123456789012345")).isTrue();
        assertThat(vehicleRepository.existsByVin("NONEXISTENT123456")).isFalse();
    }
    
    @Test
    void shouldFindVehiclesByMakeAndModelIgnoreCase() {
        // When
        List<Vehicle> vehicles = vehicleRepository.findByMakeAndModelIgnoreCase("TOYOTA", "COROLLA");
        
        // Then
        assertThat(vehicles).hasSize(1);
        assertThat(vehicles.get(0).getRegistrationNumber()).isEqualTo("WA12345");
    }
    
    @Test
    void shouldFindVehiclesByMakeIgnoreCase() {
        // When
        List<Vehicle> vehicles = vehicleRepository.findByMakeIgnoreCase("bmw");
        
        // Then
        assertThat(vehicles).hasSize(1);
        assertThat(vehicles.get(0).getModel()).isEqualTo("X5");
    }
    
    @Test
    void shouldFindVehiclesByYearOfManufacture() {
        // When
        List<Vehicle> vehicles2020 = vehicleRepository.findByYearOfManufacture(2020);
        List<Vehicle> vehicles2019 = vehicleRepository.findByYearOfManufacture(2019);
        
        // Then
        assertThat(vehicles2020).hasSize(1);
        assertThat(vehicles2020.get(0).getMake()).isEqualTo("Toyota");
        
        assertThat(vehicles2019).hasSize(1);
        assertThat(vehicles2019.get(0).getMake()).isEqualTo("BMW");
    }
    
    @Test
    void shouldFindVehiclesByYearOfManufactureBetween() {
        // When
        List<Vehicle> vehicles = vehicleRepository.findByYearOfManufactureBetween(2019, 2020);
        
        // Then
        assertThat(vehicles).hasSize(2);
        assertThat(vehicles).extracting(Vehicle::getMake)
                .containsExactlyInAnyOrder("Toyota", "BMW");
    }
    
    @Test
    void shouldFindVehiclesByFirstRegistrationDateAfter() {
        // When
        LocalDate cutoffDate = LocalDate.of(2020, 1, 1);
        List<Vehicle> vehicles = vehicleRepository.findByFirstRegistrationDateAfter(cutoffDate);
        
        // Then
        assertThat(vehicles).hasSize(2);
        assertThat(vehicles).extracting(Vehicle::getMake)
                .containsExactlyInAnyOrder("Toyota", "Audi");
    }
    
    @Test
    void shouldFindVehiclesWithActivePolicies() {
        // When
        List<Vehicle> vehiclesWithActivePolicies = vehicleRepository.findVehiclesWithActivePolicies();
        
        // Then
        assertThat(vehiclesWithActivePolicies).hasSize(1);
        assertThat(vehiclesWithActivePolicies.get(0).getRegistrationNumber()).isEqualTo("WA12345");
    }
    
    @Test
    void shouldReturnEmptyWhenVehicleNotFoundByRegistration() {
        // When
        Optional<Vehicle> found = vehicleRepository.findByRegistrationNumber("NONEXISTENT");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldReturnEmptyWhenVehicleNotFoundByVin() {
        // When
        Optional<Vehicle> found = vehicleRepository.findByVin("NONEXISTENT123456789");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldReturnEmptyListWhenNoVehiclesMatchMakeAndModel() {
        // When
        List<Vehicle> vehicles = vehicleRepository.findByMakeAndModelIgnoreCase("Ford", "Focus");
        
        // Then
        assertThat(vehicles).isEmpty();
    }
    
    @Test
    void shouldSaveAndRetrieveVehicle() {
        // Given
        Vehicle newVehicle = Vehicle.builder()
                .make("Volkswagen")
                .model("Golf")
                .yearOfManufacture(2022)
                .registrationNumber("PO22222")
                .vin("WVW12345678901234")
                .engineCapacity(1400)
                .power(150)
                .firstRegistrationDate(LocalDate.of(2022, 6, 1))
                .build();
        
        // When
        Vehicle saved = vehicleRepository.save(newVehicle);
        Optional<Vehicle> retrieved = vehicleRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getMake()).isEqualTo("Volkswagen");
        assertThat(retrieved.get().getModel()).isEqualTo("Golf");
        assertThat(retrieved.get().getRegistrationNumber()).isEqualTo("PO22222");
    }
    
    @Test
    void shouldDeleteVehicle() {
        // Given
        Long vehicleId = audiA4.getId();
        
        // When
        vehicleRepository.deleteById(vehicleId);
        Optional<Vehicle> deleted = vehicleRepository.findById(vehicleId);
        
        // Then
        assertThat(deleted).isEmpty();
    }
    
    @Test
    void shouldFindAllVehicles() {
        // When
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        
        // Then
        assertThat(allVehicles).hasSize(3);
        assertThat(allVehicles).extracting(Vehicle::getMake)
                .containsExactlyInAnyOrder("Toyota", "BMW", "Audi");
    }
}