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
 * End-to-end integration tests for critical user workflows.
 * Tests complete business scenarios from authentication through policy management.
 */
class EndToEndWorkflowTest extends BaseIntegrationTest {

    @Autowired
    private TestDataFixtures testDataFixtures;

    @Test
    void completeAdminWorkflow_CreateUserAndManageSystem() {
        // Step 1: Admin logs in
        LoginRequest loginRequest = new LoginRequest("admin@test.com", "admin123");
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest);
        
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                loginEntity,
                LoginResponse.class
        );
        
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String adminToken = loginResponse.getBody().getToken();
        
        // Step 2: Admin creates a new operator user
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "New", "Operator", "newoperator@test.com", "password123", "OPERATOR"
        );
        HttpEntity<CreateUserRequest> createUserEntity = new HttpEntity<>(createUserRequest, createAuthHeaders(adminToken));
        
        ResponseEntity<UserResponse> createUserResponse = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.POST,
                createUserEntity,
                UserResponse.class
        );
        
        assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long newUserId = createUserResponse.getBody().getId();
        
        // Step 3: Admin lists all users to verify creation
        HttpEntity<Void> listUsersEntity = new HttpEntity<>(createAuthHeaders(adminToken));
        
        ResponseEntity<List<UserResponse>> listUsersResponse = restTemplate.exchange(
                getBaseUrl() + "/users",
                HttpMethod.GET,
                listUsersEntity,
                new ParameterizedTypeReference<List<UserResponse>>() {}
        );
        
        assertThat(listUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listUsersResponse.getBody()).hasSize(3); // Original admin, operator, and new operator
        
        // Step 4: Admin updates the new user
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "Updated", "Operator", "updatedoperator@test.com", "OPERATOR"
        );
        HttpEntity<UpdateUserRequest> updateUserEntity = new HttpEntity<>(updateUserRequest, createAuthHeaders(adminToken));
        
        ResponseEntity<UserResponse> updateUserResponse = restTemplate.exchange(
                getBaseUrl() + "/users/" + newUserId,
                HttpMethod.PUT,
                updateUserEntity,
                UserResponse.class
        );
        
        assertThat(updateUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateUserResponse.getBody().getFirstName()).isEqualTo("Updated");
        
        // Step 5: Admin deletes the user
        HttpEntity<Void> deleteUserEntity = new HttpEntity<>(createAuthHeaders(adminToken));
        
        ResponseEntity<Void> deleteUserResponse = restTemplate.exchange(
                getBaseUrl() + "/users/" + newUserId,
                HttpMethod.DELETE,
                deleteUserEntity,
                Void.class
        );
        
        assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Step 6: Verify user is deleted
        ResponseEntity<String> getDeletedUserResponse = restTemplate.exchange(
                getBaseUrl() + "/users/" + newUserId,
                HttpMethod.GET,
                listUsersEntity,
                String.class
        );
        
        assertThat(getDeletedUserResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void completeOperatorWorkflow_PolicyLifecycleManagement() {
        // Setup test data
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        
        // Step 1: Operator logs in
        LoginRequest loginRequest = new LoginRequest("operator@test.com", "operator123");
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest);
        
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                loginEntity,
                LoginResponse.class
        );
        
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String operatorToken = loginResponse.getBody().getToken();
        
        // Step 2: Operator creates a new OC policy
        CreatePolicyRequest createPolicyRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> createPolicyEntity = new HttpEntity<>(createPolicyRequest, createAuthHeaders(operatorToken));
        
        ResponseEntity<PolicyResponse> createPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.POST,
                createPolicyEntity,
                PolicyResponse.class
        );
        
        assertThat(createPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long policyId = createPolicyResponse.getBody().getId();
        String policyNumber = createPolicyResponse.getBody().getPolicyNumber();
        
        // Step 3: Operator lists all policies to verify creation
        HttpEntity<Void> listPoliciesEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        
        ResponseEntity<List<PolicyResponse>> listPoliciesResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                listPoliciesEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        assertThat(listPoliciesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listPoliciesResponse.getBody().size()).isGreaterThan(0);
        
        // Step 4: Operator searches for policies by client
        ResponseEntity<List<PolicyResponse>> clientPoliciesResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/client/" + testData.clients.get(0).getId(),
                HttpMethod.GET,
                listPoliciesEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        assertThat(clientPoliciesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(clientPoliciesResponse.getBody()).isNotEmpty();
        
        // Step 5: Operator gets specific policy details
        ResponseEntity<PolicyResponse> getPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.GET,
                listPoliciesEntity,
                PolicyResponse.class
        );
        
        assertThat(getPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getPolicyResponse.getBody().getPolicyNumber()).isEqualTo(policyNumber);
        
        // Step 6: Operator updates the policy
        UpdatePolicyRequest updatePolicyRequest = UpdatePolicyRequest.builder()
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusYears(1).plusDays(1))
                .discountSurcharge(new BigDecimal("150"))
                .build();
        
        HttpEntity<UpdatePolicyRequest> updatePolicyEntity = new HttpEntity<>(updatePolicyRequest, createAuthHeaders(operatorToken));
        
        ResponseEntity<PolicyResponse> updatePolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.PUT,
                updatePolicyEntity,
                PolicyResponse.class
        );
        
        assertThat(updatePolicyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatePolicyResponse.getBody().getDiscountSurcharge()).isEqualTo(new BigDecimal("150"));
        
        // Step 7: Operator generates PDF for the policy
        ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId + "/pdf",
                HttpMethod.POST,
                listPoliciesEntity,
                byte[].class
        );
        
        assertThat(pdfResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pdfResponse.getBody()).isNotNull();
        assertThat(pdfResponse.getBody().length).isGreaterThan(0);
        
        // Step 8: Operator cancels the policy
        ResponseEntity<Void> cancelPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId + "/cancel",
                HttpMethod.POST,
                listPoliciesEntity,
                Void.class
        );
        
        assertThat(cancelPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Step 9: Verify policy is canceled
        ResponseEntity<PolicyResponse> canceledPolicyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies/" + policyId,
                HttpMethod.GET,
                listPoliciesEntity,
                PolicyResponse.class
        );
        
        assertThat(canceledPolicyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(canceledPolicyResponse.getBody().getStatus()).isEqualTo(PolicyStatus.CANCELED.name());
    }

    @Test
    void completeMultiInsuranceTypeWorkflow_CreateAllPolicyTypes() {
        // Setup test data
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        
        // Operator logs in
        LoginRequest loginRequest = new LoginRequest("operator@test.com", "operator123");
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest);
        
        ResponseEntity<LoginResponse> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/login",
                HttpMethod.POST,
                loginEntity,
                LoginResponse.class
        );
        
        String operatorToken = loginResponse.getBody().getToken();
        
        // Create OC Policy
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
        assertThat(ocResponse.getBody().getInsuranceType()).isEqualTo("OC");
        
        // Create AC Policy
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
        assertThat(acResponse.getBody().getInsuranceType()).isEqualTo("AC");
        
        // Create NNW Policy
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
        assertThat(nnwResponse.getBody().getInsuranceType()).isEqualTo("NNW");
        
        // Verify all policies are created and can be listed
        HttpEntity<Void> listEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<List<PolicyResponse>> listResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                listEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> insuranceTypes = listResponse.getBody().stream()
                .map(PolicyResponse::getInsuranceType)
                .distinct()
                .toList();
        
        assertThat(insuranceTypes).containsExactlyInAnyOrder("OC", "AC", "NNW");
    }

    @Test
    void securityWorkflow_RoleBasedAccessControl() {
        // Setup test data
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        
        // Test Admin trying to create policy (should fail)
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
        
        // Test Operator trying to create user (should fail)
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
        
        // Test unauthenticated access (should fail)
        ResponseEntity<String> unauthenticatedResponse = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                null,
                String.class
        );
        
        assertThat(unauthenticatedResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        // Test invalid token (should fail)
        HttpEntity<Void> invalidTokenEntity = new HttpEntity<>(createAuthHeaders("invalid-token"));
        ResponseEntity<String> invalidTokenResponse = restTemplate.exchange(
                getBaseUrl() + "/auth/me",
                HttpMethod.GET,
                invalidTokenEntity,
                String.class
        );
        
        assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}