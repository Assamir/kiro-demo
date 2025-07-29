package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.Client;
import com.insurance.backoffice.domain.Policy;
import com.insurance.backoffice.domain.PolicyStatus;
import com.insurance.backoffice.domain.InsuranceType;
import com.insurance.backoffice.domain.Vehicle;
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
 * Integration tests for ClientRepository using H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ClientRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ClientRepository clientRepository;
    
    private Client client1;
    private Client client2;
    private Vehicle vehicle;
    
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
        
        // Create test vehicle
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
        
        entityManager.persistAndFlush(client1);
        entityManager.persistAndFlush(client2);
        entityManager.persistAndFlush(vehicle);
        
        // Create an active policy for client1
        Policy activePolicy = Policy.builder()
                .policyNumber("POL-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1200.00))
                .client(client1)
                .vehicle(vehicle)
                .build();
        
        entityManager.persistAndFlush(activePolicy);
        entityManager.clear();
    }
    
    @Test
    void shouldFindClientByPesel() {
        // When
        Optional<Client> found = clientRepository.findByPesel("12345678901");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("John Kowalski");
        assertThat(found.get().getEmail()).isEqualTo("john.kowalski@example.com");
    }
    
    @Test
    void shouldReturnEmptyWhenClientNotFoundByPesel() {
        // When
        Optional<Client> found = clientRepository.findByPesel("00000000000");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldCheckIfClientExistsByPesel() {
        // When & Then
        assertThat(clientRepository.existsByPesel("12345678901")).isTrue();
        assertThat(clientRepository.existsByPesel("00000000000")).isFalse();
    }
    
    @Test
    void shouldFindClientByEmail() {
        // When
        Optional<Client> found = clientRepository.findByEmail("anna.nowak@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Anna Nowak");
        assertThat(found.get().getPesel()).isEqualTo("98765432109");
    }
    
    @Test
    void shouldFindClientsByFullNameContainingIgnoreCase() {
        // When
        List<Client> clients = clientRepository.findByFullNameContainingIgnoreCase("john");
        
        // Then
        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getFullName()).isEqualTo("John Kowalski");
    }
    
    @Test
    void shouldFindClientsByPartialNameIgnoreCase() {
        // When
        List<Client> clients = clientRepository.findByFullNameContainingIgnoreCase("kowal");
        
        // Then
        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getPesel()).isEqualTo("12345678901");
    }
    
    @Test
    void shouldFindClientsByPhoneNumber() {
        // When
        List<Client> clients = clientRepository.findByPhoneNumber("+48123456789");
        
        // Then
        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getFullName()).isEqualTo("John Kowalski");
    }
    
    @Test
    void shouldFindClientsWithActivePolicies() {
        // When
        List<Client> clientsWithActivePolicies = clientRepository.findClientsWithActivePolicies();
        
        // Then
        assertThat(clientsWithActivePolicies).hasSize(1);
        assertThat(clientsWithActivePolicies.get(0).getFullName()).isEqualTo("John Kowalski");
    }
    
    @Test
    void shouldReturnEmptyListWhenNoClientsMatchNameSearch() {
        // When
        List<Client> clients = clientRepository.findByFullNameContainingIgnoreCase("nonexistent");
        
        // Then
        assertThat(clients).isEmpty();
    }
    
    @Test
    void shouldSaveAndRetrieveClient() {
        // Given
        Client newClient = Client.builder()
                .fullName("Test Client")
                .pesel("11111111111")
                .address("ul. Testowa 3, 00-003 Gdańsk")
                .email("test.client@example.com")
                .phoneNumber("+48111111111")
                .build();
        
        // When
        Client saved = clientRepository.save(newClient);
        Optional<Client> retrieved = clientRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFullName()).isEqualTo("Test Client");
        assertThat(retrieved.get().getPesel()).isEqualTo("11111111111");
    }
    
    @Test
    void shouldDeleteClient() {
        // Given
        Long clientId = client2.getId();
        
        // When
        clientRepository.deleteById(clientId);
        Optional<Client> deleted = clientRepository.findById(clientId);
        
        // Then
        assertThat(deleted).isEmpty();
    }
    
    @Test
    void shouldFindAllClients() {
        // When
        List<Client> allClients = clientRepository.findAll();
        
        // Then
        assertThat(allClients).hasSize(2);
        assertThat(allClients).extracting(Client::getFullName)
                .containsExactlyInAnyOrder("John Kowalski", "Anna Nowak");
    }
}