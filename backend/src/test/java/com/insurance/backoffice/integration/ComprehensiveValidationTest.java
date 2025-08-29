package com.insurance.backoffice.integration;

import com.insurance.backoffice.domain.InsuranceType;
import com.insurance.backoffice.domain.PolicyStatus;
import com.insurance.backoffice.interfaces.dto.*;
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
 * Comprehensive validation tests for business requirements and system integrity.
 * Validates all business rules, data integrity, and requirement compliance.
 */
class ComprehensiveValidationTest extends BaseIntegrationTest {

    @Autowired
    private TestDataFixtures testDataFixtures;

    @Test
    void validateBusinessRules_PremiumCalculationAccuracy() {
        // Test premium calculation for all insurance types
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Test OC Insurance Premium Calculation
        CreatePolicyRequest ocRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> ocEntity = new HttpEntity<>(ocRequest, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> ocResponse = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, ocEntity, PolicyResponse.class
        );
        
        assertThat(ocResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(ocResponse.getBody().getPremium()).isGreaterThan(BigDecimal.ZERO);
        assertThat(ocResponse.getBody().getInsuranceType()).isEqualTo("OC");
        
        // Test AC Insurance Premium Calculation
        CreatePolicyRequest acRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(1).getId())
                .vehicleId(testData.vehicles.get(1).getId())
                .insuranceType(InsuranceType.AC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .sumInsured(new BigDecimal("70000"))
                .coverageScope("Comprehensive")
                .deductible(new BigDecimal("1000"))
                .workshopType("Authorized")
                .build();
        
        HttpEntity<CreatePolicyRequest> acEntity = new HttpEntity<>(acRequest, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> acResponse = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, acEntity, PolicyResponse.class
        );
        
        assertThat(acResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(acResponse.getBody().getPremium()).isGreaterThan(BigDecimal.ZERO);
        assertThat(acResponse.getBody().getInsuranceType()).isEqualTo("AC");
        
        // Test NNW Insurance Premium Calculation
        CreatePolicyRequest nnwRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(2).getId())
                .vehicleId(testData.vehicles.get(2).getId())
                .insuranceType(InsuranceType.NNW)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .sumInsured(new BigDecimal("120000"))
                .coveredPersons("Driver and up to 4 passengers")
                .build();
        
        HttpEntity<CreatePolicyRequest> nnwEntity = new HttpEntity<>(nnwRequest, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> nnwResponse = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, nnwEntity, PolicyResponse.class
        );
        
        assertThat(nnwResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(nnwResponse.getBody().getPremium()).isGreaterThan(BigDecimal.ZERO);
        assertThat(nnwResponse.getBody().getInsuranceType()).isEqualTo("NNW");
        
        // Verify premium calculations are consistent and logical
        // AC insurance should generally be more expensive than OC for similar vehicles
        // This is a business rule validation
        assertThat(acResponse.getBody().getPremium()).isGreaterThan(ocResponse.getBody().getPremium());
    }

    @Test
    void validateDataIntegrity_PolicyClientVehicleRelationships() {
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create policy with specific client and vehicle
        Long clientId = testData.clients.get(0).getId();
        Long vehicleId = testData.vehicles.get(0).getId();
        
        CreatePolicyRequest request = CreatePolicyRequest.builder()
                .clientId(clientId)
                .vehicleId(vehicleId)
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(request, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, entity, PolicyResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long policyId = response.getBody().getId();
        
        // Verify policy-client relationship
        HttpEntity<Void> getEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<List<PolicyResponse>> clientPoliciesResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/client/" + clientId,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        assertThat(clientPoliciesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(clientPoliciesResponse.getBody()).isNotEmpty();
        
        boolean policyFound = clientPoliciesResponse.getBody().stream()
                .anyMatch(p -> p.getId().equals(policyId));
        assertThat(policyFound).isTrue();
        
        // Verify policy details contain correct relationships
        ResponseEntity<PolicyResponse> policyDetailsResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.GET,
                getEntity,
                PolicyResponse.class
        );
        
        assertThat(policyDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PolicyResponse policyDetails = policyDetailsResponse.getBody();
        assertThat(policyDetails.getClientName()).isNotNull();
        assertThat(policyDetails.getVehicleRegistration()).isNotNull();
    }

    @Test
    void validateBusinessRules_PolicyStatusTransitions() {
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create a new policy (should start as ACTIVE)
        CreatePolicyRequest request = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(request, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, entity, PolicyResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatus()).isEqualTo(PolicyStatus.ACTIVE.name());
        
        Long policyId = response.getBody().getId();
        
        // Cancel the policy (ACTIVE -> CANCELED)
        HttpEntity<Void> cancelEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<Void> cancelResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId + "/cancel",
                HttpMethod.POST,
                cancelEntity,
                Void.class
        );
        
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify policy is now canceled
        HttpEntity<Void> getEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> canceledPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.GET,
                getEntity,
                PolicyResponse.class
        );
        
        assertThat(canceledPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(canceledPolicyResponse.getBody().getStatus()).isEqualTo(PolicyStatus.CANCELED.name());
        
        // Verify canceled policy cannot be modified
        UpdatePolicyRequest updateRequest = UpdatePolicyRequest.builder()
                .discountSurcharge(new BigDecimal("100"))
                .build();
        
        HttpEntity<UpdatePolicyRequest> updateEntity = new HttpEntity<>(updateRequest, createAuthHeaders(operatorToken));
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.PUT,
                updateEntity,
                String.class
        );
        
        // Should not allow modification of canceled policy
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateSecurityRequirements_ComprehensiveAccessControl() {
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        
        // Test 1: Unauthenticated access should be denied
        ResponseEntity<String> unauthenticatedResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                null,
                String.class
        );
        assertThat(unauthenticatedResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        // Test 2: Admin cannot create policies
        CreatePolicyRequest policyRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .build();
        
        HttpEntity<CreatePolicyRequest> adminPolicyEntity = new HttpEntity<>(policyRequest, createAdminHeaders());
        ResponseEntity<String> adminPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                adminPolicyEntity,
                String.class
        );
        assertThat(adminPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        
        // Test 3: Operator cannot manage users
        CreateUserRequest userRequest = new CreateUserRequest(
                "Test", "User", "test@example.com", "password", "OPERATOR"
        );
        
        HttpEntity<CreateUserRequest> operatorUserEntity = new HttpEntity<>(userRequest, createOperatorHeaders());
        ResponseEntity<String> operatorUserResponse = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                operatorUserEntity,
                String.class
        );
        assertThat(operatorUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        
        // Test 4: Invalid token should be rejected
        HttpEntity<Void> invalidTokenEntity = new HttpEntity<>(createAuthHeaders("invalid-token-12345"));
        ResponseEntity<String> invalidTokenResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/me",
                HttpMethod.GET,
                invalidTokenEntity,
                String.class
        );
        assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        // Test 5: Expired token should be rejected (simulated)
        HttpEntity<Void> expiredTokenEntity = new HttpEntity<>(createAuthHeaders("expired.token.here"));
        ResponseEntity<String> expiredTokenResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                expiredTokenEntity,
                String.class
        );
        assertThat(expiredTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void validatePdfGeneration_AllInsuranceTypes() {
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create and test PDF generation for OC policy
        CreatePolicyRequest ocRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        Long ocPolicyId = createPolicyAndGetId(ocRequest, operatorToken);
        validatePdfGeneration(ocPolicyId, operatorToken, "OC");
        
        // Create and test PDF generation for AC policy
        CreatePolicyRequest acRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(1).getId())
                .vehicleId(testData.vehicles.get(1).getId())
                .insuranceType(InsuranceType.AC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .sumInsured(new BigDecimal("70000"))
                .coverageScope("Comprehensive")
                .deductible(new BigDecimal("1000"))
                .workshopType("Authorized")
                .build();
        
        Long acPolicyId = createPolicyAndGetId(acRequest, operatorToken);
        validatePdfGeneration(acPolicyId, operatorToken, "AC");
        
        // Create and test PDF generation for NNW policy
        CreatePolicyRequest nnwRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(2).getId())
                .vehicleId(testData.vehicles.get(2).getId())
                .insuranceType(InsuranceType.NNW)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .sumInsured(new BigDecimal("120000"))
                .coveredPersons("Driver and up to 4 passengers")
                .build();
        
        Long nnwPolicyId = createPolicyAndGetId(nnwRequest, operatorToken);
        validatePdfGeneration(nnwPolicyId, operatorToken, "NNW");
    }

    @Test
    void validateSystemIntegration_DatabaseTransactionIntegrity() {
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Test transaction rollback on validation failure
        CreatePolicyRequest invalidRequest = CreatePolicyRequest.builder()
                .clientId(999999L) // Non-existent client
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(invalidRequest, createAuthHeaders(operatorToken));
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, entity, String.class
        );
        
        // Should fail due to invalid client ID
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Verify no partial data was created
        HttpEntity<Void> listEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<List<PolicyResponse>> listResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                listEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        // Should not contain any policy with the invalid client
        assertThat(listResponse.getBody().stream()
                .noneMatch(p -> p.getClientName() == null || p.getClientName().isEmpty())).isTrue();
    }

    @Test
    void validateErrorHandling_GracefulDegradation() {
        String operatorToken = getOperatorToken();
        
        // Test handling of malformed requests
        String malformedJson = "{\"clientId\": \"not-a-number\", \"insuranceType\": \"INVALID\"}";
        
        HttpEntity<String> malformedEntity = new HttpEntity<>(malformedJson, createAuthHeaders(operatorToken));
        ResponseEntity<String> malformedResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                malformedEntity,
                String.class
        );
        
        assertThat(malformedResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Test handling of missing required fields
        CreatePolicyRequest incompleteRequest = CreatePolicyRequest.builder()
                .insuranceType(InsuranceType.OC)
                // Missing required fields
                .build();
        
        HttpEntity<CreatePolicyRequest> incompleteEntity = new HttpEntity<>(incompleteRequest, createAuthHeaders(operatorToken));
        ResponseEntity<String> incompleteResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                incompleteEntity,
                String.class
        );
        
        assertThat(incompleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Test handling of business rule violations
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        
        CreatePolicyRequest invalidDateRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusYears(1)) // Start date after end date
                .endDate(LocalDate.now().plusDays(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> invalidDateEntity = new HttpEntity<>(invalidDateRequest, createAuthHeaders(operatorToken));
        ResponseEntity<String> invalidDateResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                invalidDateEntity,
                String.class
        );
        
        assertThat(invalidDateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private Long createPolicyAndGetId(CreatePolicyRequest request, String token) {
        HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(request, createAuthHeaders(token));
        ResponseEntity<PolicyResponse> response = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, entity, PolicyResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody().getId();
    }

    private void validatePdfGeneration(Long policyId, String token, String insuranceType) {
        HttpEntity<Void> pdfEntity = new HttpEntity<>(createAuthHeaders(token));
        ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId + "/pdf",
                HttpMethod.POST,
                pdfEntity,
                byte[].class
        );
        
        assertThat(pdfResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pdfResponse.getBody()).isNotNull();
        assertThat(pdfResponse.getBody().length).isGreaterThan(1000); // PDF should have reasonable size
        
        // Verify PDF content type header
        assertThat(pdfResponse.getHeaders().getContentType().toString()).contains("application/pdf");
    }

    private String getOperatorToken() {
        LoginRequest loginRequest = new LoginRequest("operator@test.com", "operator123");
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest);
        
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                loginEntity,
                LoginResponse.class
        );
        
        return loginResponse.getBody().getToken();
    }
}