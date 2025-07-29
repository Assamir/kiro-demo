package com.insurance.backoffice.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for policy management operations.
 * Clean Code: Thin controller focused on HTTP concerns with role-based security.
 * Accessible by both Operators and Admins.
 */
@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policy Management", description = "Policy management endpoints (Operator/Admin)")
public class PolicyController {
    
    /**
     * Retrieves all policies.
     * Clean Code: Simple endpoint with role-based authorization.
     * 
     * @return list of all policies
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(summary = "Get all policies", description = "Retrieve all policies (Operator/Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policies retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
    })
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Retrieves policies for a specific client.
     * Clean Code: RESTful endpoint with path variable.
     * 
     * @param clientId client ID
     * @return list of client policies
     */
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(summary = "Get policies by client", description = "Retrieve policies for specific client (Operator/Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client policies retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
    })
    public ResponseEntity<List<PolicyResponse>> getPoliciesByClient(@PathVariable Long clientId) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Creates a new policy.
     * Clean Code: POST endpoint with request body validation.
     * 
     * @param request policy creation request
     * @return created policy details
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(summary = "Create policy", description = "Create a new policy (Operator/Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Policy created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
    })
    public ResponseEntity<PolicyResponse> createPolicy(@RequestBody CreatePolicyRequest request) {
        // TODO: Implement in future task - service layer integration
        return ResponseEntity.status(201).build();
    }
    
    /**
     * Generates PDF for a policy.
     * Clean Code: POST endpoint for PDF generation action.
     * 
     * @param id policy ID
     * @return PDF generation response
     */
    @PostMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(summary = "Generate policy PDF", description = "Generate PDF document for policy (Operator/Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
        @ApiResponse(responseCode = "404", description = "Policy not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
    })
    public ResponseEntity<byte[]> generatePolicyPdf(@PathVariable Long id) {
        // TODO: Implement in future task - PDF service integration
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Data Transfer Object for policy response.
     * Clean Code: Inner record for response data structure.
     */
    public record PolicyResponse(
        Long id,
        String policyNumber,
        String clientName,
        String vehicleRegistration,
        com.insurance.backoffice.domain.InsuranceType insuranceType,
        java.time.LocalDate startDate,
        java.time.LocalDate endDate,
        java.math.BigDecimal premium,
        com.insurance.backoffice.domain.PolicyStatus status
    ) {}
    
    /**
     * Data Transfer Object for policy creation request.
     * Clean Code: Inner record for request data structure.
     */
    public record CreatePolicyRequest(
        Long clientId,
        Long vehicleId,
        com.insurance.backoffice.domain.InsuranceType insuranceType,
        java.time.LocalDate startDate,
        java.time.LocalDate endDate,
        java.math.BigDecimal discountSurcharge
    ) {}
}