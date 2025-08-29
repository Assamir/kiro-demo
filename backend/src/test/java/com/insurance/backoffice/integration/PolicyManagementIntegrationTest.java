package com.insurance.backoffice.integration;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.ClientRepository;
import com.insurance.backoffice.infrastructure.repository.PolicyRepository;
import com.insurance.backoffice.infrastructure.repository.VehicleRepository;
import com.insurance.backoffice.interfaces.dto.CreatePolicyRequest;
import com.insurance.backoffice.interfaces.dto.PolicyResponse;
import com.insurance.backoffice.interfaces.dto.UpdatePolicyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for policy management endpoints.
 * Tests complete policy lifecycle operations with proper authorization and data validation.
 */
class PolicyManagementIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private Client testClient;
    private Vehicle testVehicle;

    @BeforeEach
    void setUpPolicyTestData() {
        // Create test client
        testClient = Client.builder()
                .fullName("John Doe")
                .pesel("12345678901")
                .address("123 Test Street, Test City")
                .email("john.doe@test.com")
                .phoneNumber("+48123456789")
                .build();
        testClient = clientRepository.save(testClient);

        // Create test vehicle
        testVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1HGBH41JXMN109186")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 1, 15))
                .build();
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    void shouldAllowOperatorToCreateOCPolicy() {
        // Given
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .clientId(testClient.getId())
                .vehicleId(testVehicle.getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                PolicyResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPolicyNumber()).isNotBlank();
        assertThat(response.getBody().getInsuranceType()).isEqualTo("OC");
        assertThat(response.getBody().getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getBody().getClientName()).isEqualTo("John Doe");
        assertThat(response.getBody().getVehicleRegistration()).isEqualTo("ABC123");
    }

    @Test
    void shouldAllowOperatorToCreateACPolicy() {
        // Given
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .clientId(testClient.getId())
                .vehicleId(testVehicle.getId())
                .insuranceType(InsuranceType.AC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .acVariant(ACVariant.STANDARD)
                .sumInsured(new BigDecimal("50000"))
                .coverageScope("Comprehensive")
                .deductible(new BigDecimal("500"))
                .workshopType("Authorized")
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                PolicyResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInsuranceType()).isEqualTo("AC");
        assertThat(response.getBody().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldAllowOperatorToCreateNNWPolicy() {
        // Given
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .clientId(testClient.getId())
                .vehicleId(testVehicle.getId())
                .insuranceType(InsuranceType.NNW)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .sumInsured(new BigDecimal("100000"))
                .coveredPersons("Driver and passengers")
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                PolicyResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInsuranceType()).isEqualTo("NNW");
        assertThat(response.getBody().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldPreventAdminFromCreatingPolicy() {
        // Given
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .clientId(testClient.getId())
                .vehicleId(testVehicle.getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createAdminHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllowOperatorToListPolicies() {
        // Given - create a test policy first
        Policy testPolicy = createTestPolicy();
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyNumber()).isEqualTo(testPolicy.getPolicyNumber());
    }

    @Test
    void shouldAllowOperatorToGetPolicyById() {
        // Given
        Policy testPolicy = createTestPolicy();
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + testPolicy.getId(),
                HttpMethod.GET,
                request,
                PolicyResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPolicyNumber()).isEqualTo(testPolicy.getPolicyNumber());
    }

    @Test
    void shouldAllowOperatorToUpdatePolicy() {
        // Given
        Policy testPolicy = createTestPolicy();
        UpdatePolicyRequest updateRequest = UpdatePolicyRequest.builder()
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusYears(1).plusDays(1))
                .discountSurcharge(new BigDecimal("100"))
                .build();
        
        HttpEntity<UpdatePolicyRequest> request = new HttpEntity<>(updateRequest, createOperatorHeaders());

        // When
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + testPolicy.getId(),
                HttpMethod.PUT,
                request,
                PolicyResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDiscountSurcharge()).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void shouldAllowOperatorToCancelPolicy() {
        // Given
        Policy testPolicy = createTestPolicy();
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/policies/" + testPolicy.getId() + "/cancel",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify policy is canceled
        ResponseEntity<PolicyResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + testPolicy.getId(),
                HttpMethod.GET,
                request,
                PolicyResponse.class
        );
        assertThat(getResponse.getBody().getStatus()).isEqualTo("CANCELED");
    }

    @Test
    void shouldFindPoliciesByClient() {
        // Given
        Policy testPolicy = createTestPolicy();
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/policies/client/" + testClient.getId(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyNumber()).isEqualTo(testPolicy.getPolicyNumber());
    }

    @Test
    void shouldValidateRequiredFieldsWhenCreatingPolicy() {
        // Given - request with missing required fields
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .insuranceType(InsuranceType.OC)
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldRejectPolicyWithInvalidDateRange() {
        // Given - end date before start date
        CreatePolicyRequest createRequest = CreatePolicyRequest.builder()
                .clientId(testClient.getId())
                .vehicleId(testVehicle.getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(5))
                .build();
        
        HttpEntity<CreatePolicyRequest> request = new HttpEntity<>(createRequest, createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn404WhenPolicyNotFound() {
        // Given
        HttpEntity<Void> request = new HttpEntity<>(createOperatorHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies/99999",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Policy createTestPolicy() {
        Policy policy = Policy.builder()
                .policyNumber("TEST-POL-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1200.00"))
                .client(testClient)
                .vehicle(testVehicle)
                .build();
        
        PolicyDetails details = PolicyDetails.builder()
                .policy(policy)
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        policy.setPolicyDetails(details);
        return policyRepository.save(policy);
    }
}