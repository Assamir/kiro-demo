package com.insurance.backoffice.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.backoffice.application.service.PolicyService;
import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.interfaces.controller.PolicyController.CreatePolicyRequest;
import com.insurance.backoffice.interfaces.controller.PolicyController.PolicyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PolicyController class.
 * Clean Code: Web layer testing with mocked service dependencies.
 */
@WebMvcTest(controllers = PolicyController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@TestPropertySource(locations = "classpath:application-test.properties")
class PolicyControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PolicyService policyService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldGetAllPoliciesSuccessfully() throws Exception {
        // Given
        List<Policy> policies = createMockPolicies();
        
        when(policyService.findPoliciesByStatus(PolicyStatus.ACTIVE)).thenReturn(policies);
        
        // When & Then
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].policyNumber").value("POL-2024-001"))
                .andExpect(jsonPath("$[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$[0].vehicleRegistration").value("ABC123"))
                .andExpect(jsonPath("$[0].insuranceType").value("OC"))
                .andExpect(jsonPath("$[0].premium").value(1200.00))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
        
        verify(policyService).findPoliciesByStatus(PolicyStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllPoliciesAsAdmin() throws Exception {
        // Given
        List<Policy> policies = List.of(createMockPolicy());
        
        when(policyService.findPoliciesByStatus(PolicyStatus.ACTIVE)).thenReturn(policies);
        
        // When & Then
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(policyService).findPoliciesByStatus(PolicyStatus.ACTIVE);
    }
    
    @Test
    void shouldReturnUnauthorizedWhenUnauthenticatedUserTriesToGetPolicies() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(policyService);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldGetPoliciesByClientSuccessfully() throws Exception {
        // Given
        List<Policy> policies = List.of(createMockPolicy());
        
        when(policyService.findPoliciesByClient(1L)).thenReturn(policies);
        
        // When & Then
        mockMvc.perform(get("/api/policies/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clientName").value("John Doe"));
        
        verify(policyService).findPoliciesByClient(1L);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnEmptyListWhenClientHasNoPolicies() throws Exception {
        // Given
        when(policyService.findPoliciesByClient(999L)).thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/policies/client/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        
        verify(policyService).findPoliciesByClient(999L);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldCreatePolicySuccessfully() throws Exception {
        // Given
        CreatePolicyRequest request = new CreatePolicyRequest(
            1L, 1L, InsuranceType.OC, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), 
            BigDecimal.ZERO
        );
        Policy createdPolicy = createMockPolicy();
        
        when(policyService.createPolicy(eq(1L), eq(1L), eq(InsuranceType.OC), 
            eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)), eq(BigDecimal.ZERO)))
            .thenReturn(createdPolicy);
        
        // When & Then
        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.policyNumber").value("POL-2024-001"))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.vehicleRegistration").value("ABC123"))
                .andExpect(jsonPath("$.insuranceType").value("OC"))
                .andExpect(jsonPath("$.premium").value(1200.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        
        verify(policyService).createPolicy(eq(1L), eq(1L), eq(InsuranceType.OC), 
            eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)), eq(BigDecimal.ZERO));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreatePolicyAsAdmin() throws Exception {
        // Given
        CreatePolicyRequest request = new CreatePolicyRequest(
            1L, 1L, InsuranceType.AC, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), 
            BigDecimal.valueOf(-100.00)
        );
        Policy createdPolicy = createMockACPolicy();
        
        when(policyService.createPolicy(eq(1L), eq(1L), eq(InsuranceType.AC), 
            eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)), eq(BigDecimal.valueOf(-100.00))))
            .thenReturn(createdPolicy);
        
        // When & Then
        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.insuranceType").value("AC"))
                .andExpect(jsonPath("$.premium").value(2400.00));
        
        verify(policyService).createPolicy(eq(1L), eq(1L), eq(InsuranceType.AC), 
            eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)), eq(BigDecimal.valueOf(-100.00)));
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnBadRequestWhenCreatePolicyDataIsInvalid() throws Exception {
        // Given
        CreatePolicyRequest invalidRequest = new CreatePolicyRequest(
            null, null, null, null, null, null
        );
        
        // When & Then
        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(policyService);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldGeneratePolicyPdfSuccessfully() throws Exception {
        // Given
        Policy policy = createMockPolicy();
        
        when(policyService.findPolicyById(1L)).thenReturn(policy);
        
        // When & Then
        mockMvc.perform(post("/api/policies/1/pdf")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(policyService).findPolicyById(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGeneratePolicyPdfAsAdmin() throws Exception {
        // Given
        Policy policy = createMockPolicy();
        
        when(policyService.findPolicyById(1L)).thenReturn(policy);
        
        // When & Then
        mockMvc.perform(post("/api/policies/1/pdf")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(policyService).findPolicyById(1L);
    }
    
    @Test
    @WithMockUser(roles = "OPERATOR")
    void shouldReturnNotFoundWhenGeneratingPdfForNonExistentPolicy() throws Exception {
        // Given
        when(policyService.findPolicyById(999L))
            .thenThrow(new com.insurance.backoffice.application.service.EntityNotFoundException("Policy not found"));
        
        // When & Then
        mockMvc.perform(post("/api/policies/999/pdf")
                .with(csrf()))
                .andExpect(status().isNotFound());
        
        verify(policyService).findPolicyById(999L);
    }
    
    // Helper methods for creating mock objects
    private List<Policy> createMockPolicies() {
        return List.of(
            createMockPolicy(),
            createMockACPolicy()
        );
    }
    
    private Policy createMockPolicy() {
        Client client = Client.builder()
            .id(1L)
            .fullName("John Doe")
            .build();
        
        Vehicle vehicle = Vehicle.builder()
            .id(1L)
            .registrationNumber("ABC123")
            .build();
        
        return Policy.builder()
            .id(1L)
            .policyNumber("POL-2024-001")
            .client(client)
            .vehicle(vehicle)
            .insuranceType(InsuranceType.OC)
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 12, 31))
            .premium(BigDecimal.valueOf(1200.00))
            .status(PolicyStatus.ACTIVE)
            .build();
    }
    
    private Policy createMockACPolicy() {
        Client client = Client.builder()
            .id(2L)
            .fullName("Jane Smith")
            .build();
        
        Vehicle vehicle = Vehicle.builder()
            .id(2L)
            .registrationNumber("XYZ789")
            .build();
        
        return Policy.builder()
            .id(2L)
            .policyNumber("POL-2024-002")
            .client(client)
            .vehicle(vehicle)
            .insuranceType(InsuranceType.AC)
            .startDate(LocalDate.of(2024, 2, 1))
            .endDate(LocalDate.of(2025, 1, 31))
            .premium(BigDecimal.valueOf(2400.00))
            .status(PolicyStatus.ACTIVE)
            .build();
    }
}