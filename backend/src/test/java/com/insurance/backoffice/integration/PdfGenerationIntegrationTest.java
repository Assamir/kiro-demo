package com.insurance.backoffice.integration;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.ClientRepository;
import com.insurance.backoffice.infrastructure.repository.PolicyRepository;
import com.insurance.backoffice.infrastructure.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PDF generation functionality.
 * Tests the complete PDF generation workflow for different policy types.
 */
class PdfGenerationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private Client testClient;
    private Vehicle testVehicle;
    private Policy ocPolicy;
    private Policy acPolicy;
    private Policy nnwPolicy;

    @BeforeEach
    void setUpPdfTestData() {
        // Create test client
        testClient = Client.builder()
                .fullName("Jane Smith")
                .pesel("98765432109")
                .address("456 PDF Street, Document City")
                .email("jane.smith@test.com")
                .phoneNumber("+48987654321")
                .build();
        testClient = clientRepository.save(testClient);

        // Create test vehicle
        testVehicle = Vehicle.builder()
                .make("BMW")
                .model("X5")
                .yearOfManufacture(2022)
                .registrationNumber("PDF123")
                .vin("WBAFR9C50DD123456")
                .engineCapacity(3000)
                .power(265)
                .firstRegistrationDate(LocalDate.of(2022, 3, 15))
                .build();
        testVehicle = vehicleRepository.save(testVehicle);

        // Create test policies
        createTestPolicies();
    }

    @Test
    void shouldGeneratePdfForOCPolicy() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<byte[]> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + ocPolicy.getId() + "/pdf",
                HttpMethod.POST,
                request,
                byte[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
        
        // Verify PDF content type header
        assertThat(response.getHeaders().getContentType().toString()).contains("application/pdf");
        
        // Verify PDF starts with PDF signature
        byte[] pdfSignature = {0x25, 0x50, 0x44, 0x46}; // %PDF
        byte[] responseStart = new byte[4];
        System.arraycopy(response.getBody(), 0, responseStart, 0, 4);
        assertThat(responseStart).isEqualTo(pdfSignature);
    }

    @Test
    void shouldGeneratePdfForACPolicy() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<byte[]> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + acPolicy.getId() + "/pdf",
                HttpMethod.POST,
                request,
                byte[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
        
        // Verify PDF content type
        assertThat(response.getHeaders().getContentType().toString()).contains("application/pdf");
    }

    @Test
    void shouldGeneratePdfForNNWPolicy() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<byte[]> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + nnwPolicy.getId() + "/pdf",
                HttpMethod.POST,
                request,
                byte[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
        
        // Verify PDF content type
        assertThat(response.getHeaders().getContentType().toString()).contains("application/pdf");
    }

    @Test
    void shouldPreventAdminFromGeneratingPdf() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createAdminHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + ocPolicy.getId() + "/pdf",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn404WhenGeneratingPdfForNonExistentPolicy() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies/99999/pdf",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400WhenGeneratingPdfForCanceledPolicy() {
        // Given - cancel the policy first
        ocPolicy.setStatus(PolicyStatus.CANCELED);
        policyRepository.save(ocPolicy);
        
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + ocPolicy.getId() + "/pdf",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRequireAuthenticationForPdfGeneration() {
        // When - request without authentication
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + ocPolicy.getId() + "/pdf",
                HttpMethod.POST,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private void createTestPolicies() {
        // Create OC Policy
        ocPolicy = Policy.builder()
                .policyNumber("PDF-OC-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1500.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();

        PolicyDetails ocDetails = PolicyDetails.builder()
                .policy(ocPolicy)
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();

        ocPolicy.setPolicyDetails(ocDetails);
        ocPolicy = policyRepository.save(ocPolicy);

        // Create AC Policy
        acPolicy = Policy.builder()
                .policyNumber("PDF-AC-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.AC)
                .premium(new BigDecimal("2500.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();

        PolicyDetails acDetails = PolicyDetails.builder()
                .policy(acPolicy)
                .acVariant(ACVariant.MAXIMUM)
                .sumInsured(new BigDecimal("80000"))
                .coverageScope("Comprehensive")
                .deductible(new BigDecimal("1000"))
                .workshopType("Authorized")
                .build();

        acPolicy.setPolicyDetails(acDetails);
        acPolicy = policyRepository.save(acPolicy);

        // Create NNW Policy
        nnwPolicy = Policy.builder()
                .policyNumber("PDF-NNW-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.NNW)
                .premium(new BigDecimal("800.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();

        PolicyDetails nnwDetails = PolicyDetails.builder()
                .policy(nnwPolicy)
                .sumInsured(new BigDecimal("150000"))
                .coveredPersons("Driver and up to 4 passengers")
                .build();

        nnwPolicy.setPolicyDetails(nnwDetails);
        nnwPolicy = policyRepository.save(nnwPolicy);
    }
}