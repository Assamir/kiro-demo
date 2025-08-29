package com.insurance.backoffice.integration;

import com.insurance.backoffice.domain.InsuranceType;
import com.insurance.backoffice.interfaces.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance and load testing for the Insurance Backoffice System.
 * Tests system behavior under various load conditions and validates performance requirements.
 */
@ActiveProfiles("test")
class SystemPerformanceTest extends BaseIntegrationTest {

    @Autowired
    private TestDataFixtures testDataFixtures;

    @Test
    void performanceTest_ConcurrentPolicyCreation() throws Exception {
        // Setup test data
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Test concurrent policy creation
        int numberOfConcurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfConcurrentRequests);
        List<CompletableFuture<ResponseEntity<PolicyResponse>>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfConcurrentRequests; i++) {
            final int index = i;
            CompletableFuture<ResponseEntity<PolicyResponse>> future = CompletableFuture.supplyAsync(() -> {
                CreatePolicyRequest request = CreatePolicyRequest.builder()
                        .clientId(testData.clients.get(index % testData.clients.size()).getId())
                        .vehicleId(testData.vehicles.get(index % testData.vehicles.size()).getId())
                        .insuranceType(InsuranceType.OC)
                        .startDate(LocalDate.now().plusDays(1))
                        .endDate(LocalDate.now().plusYears(1))
                        .guaranteedSum(new BigDecimal("5000000"))
                        .coverageArea("Europe")
                        .build();
                
                HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(request, createAuthHeaders(operatorToken));
                
                return restTemplate.exchange(
                        getBaseUrl() + "/policies",
                        HttpMethod.POST,
                        entity,
                        PolicyResponse.class
                );
            }, executor);
            
            futures.add(future);
        }
        
        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Verify all requests succeeded
        for (CompletableFuture<ResponseEntity<PolicyResponse>> future : futures) {
            ResponseEntity<PolicyResponse> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getPolicyNumber()).isNotNull();
        }
        
        // Performance assertions
        assertThat(totalTime).isLessThan(10000); // Should complete within 10 seconds
        double averageResponseTime = (double) totalTime / numberOfConcurrentRequests;
        assertThat(averageResponseTime).isLessThan(1000); // Average response time should be under 1 second
        
        executor.shutdown();
    }

    @Test
    void performanceTest_BulkPolicyRetrieval() {
        // Create multiple policies for testing
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create 50 policies
        for (int i = 0; i < 50; i++) {
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .clientId(testData.clients.get(i % testData.clients.size()).getId())
                    .vehicleId(testData.vehicles.get(i % testData.vehicles.size()).getId())
                    .insuranceType(InsuranceType.values()[i % InsuranceType.values().length])
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusYears(1))
                    .guaranteedSum(new BigDecimal("5000000"))
                    .coverageArea("Europe")
                    .build();
            
            HttpEntity<CreatePolicyRequest> entity = new HttpEntity<>(request, createAuthHeaders(operatorToken));
            restTemplate.exchange(getBaseUrl() + "/policies", HttpMethod.POST, entity, PolicyResponse.class);
        }
        
        // Test bulk retrieval performance
        HttpEntity<Void> listEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                listEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(50);
        
        // Performance assertion - should retrieve 50+ policies within 2 seconds
        assertThat(responseTime).isLessThan(2000);
    }

    @Test
    void performanceTest_PdfGenerationLoad() throws Exception {
        // Setup test data
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create a policy for PDF generation
        CreatePolicyRequest policyRequest = CreatePolicyRequest.builder()
                .clientId(testData.clients.get(0).getId())
                .vehicleId(testData.vehicles.get(0).getId())
                .insuranceType(InsuranceType.OC)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .guaranteedSum(new BigDecimal("5000000"))
                .coverageArea("Europe")
                .build();
        
        HttpEntity<CreatePolicyRequest> policyEntity = new HttpEntity<>(policyRequest, createAuthHeaders(operatorToken));
        ResponseEntity<PolicyResponse> policyResponse = restTemplate.exchange(
                getBaseUrl() + "/policies", HttpMethod.POST, policyEntity, PolicyResponse.class
        );
        
        Long policyId = policyResponse.getBody().getId();
        
        // Test concurrent PDF generation
        int numberOfConcurrentPdfRequests = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfConcurrentPdfRequests);
        List<CompletableFuture<ResponseEntity<byte[]>>> futures = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfConcurrentPdfRequests; i++) {
            CompletableFuture<ResponseEntity<byte[]>> future = CompletableFuture.supplyAsync(() -> {
                HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders(operatorToken));
                
                return restTemplate.exchange(
                        getBaseUrl() + "/policies/" + policyId + "/pdf",
                        HttpMethod.POST,
                        entity,
                        byte[].class
                );
            }, executor);
            
            futures.add(future);
        }
        
        // Wait for all PDF generation requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Verify all PDF generations succeeded
        for (CompletableFuture<ResponseEntity<byte[]>> future : futures) {
            ResponseEntity<byte[]> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().length).isGreaterThan(0);
        }
        
        // Performance assertions - PDF generation should complete within reasonable time
        assertThat(totalTime).isLessThan(30000); // Should complete within 30 seconds
        double averageResponseTime = (double) totalTime / numberOfConcurrentPdfRequests;
        assertThat(averageResponseTime).isLessThan(6000); // Average PDF generation should be under 6 seconds
        
        executor.shutdown();
    }

    @Test
    void performanceTest_DatabaseConnectionPooling() {
        // Test database connection handling under load
        String operatorToken = getOperatorToken();
        HttpEntity<Void> listEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        
        // Make multiple rapid database queries
        int numberOfQueries = 20;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfQueries; i++) {
            ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                    getBaseUrl() + "/policies",
                    HttpMethod.GET,
                    listEntity,
                    new ParameterizedTypeReference<List<PolicyResponse>>() {}
            );
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Performance assertion - should handle rapid queries efficiently
        assertThat(totalTime).isLessThan(5000); // Should complete 20 queries within 5 seconds
        double averageQueryTime = (double) totalTime / numberOfQueries;
        assertThat(averageQueryTime).isLessThan(250); // Average query time should be under 250ms
    }

    @Test
    void memoryUsageTest_LargePolicyDataSet() {
        // Test system behavior with large data sets
        TestDataFixtures.TestDataSet testData = testDataFixtures.createCompleteTestDataSet();
        String operatorToken = getOperatorToken();
        
        // Create a large number of policies to test memory usage
        int numberOfPolicies = 100;
        
        for (int i = 0; i < numberOfPolicies; i++) {
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .clientId(testData.clients.get(i % testData.clients.size()).getId())
                    .vehicleId(testData.vehicles.get(i % testData.vehicles.size()).getId())
                    .insuranceType(InsuranceType.values()[i % InsuranceType.values().length])
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
        }
        
        // Retrieve all policies to test memory handling
        HttpEntity<Void> listEntity = new HttpEntity<>(createAuthHeaders(operatorToken));
        ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/policies",
                HttpMethod.GET,
                listEntity,
                new ParameterizedTypeReference<List<PolicyResponse>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(numberOfPolicies);
        
        // System should handle large data sets without memory issues
        // This test verifies the system doesn't crash or timeout with large data sets
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