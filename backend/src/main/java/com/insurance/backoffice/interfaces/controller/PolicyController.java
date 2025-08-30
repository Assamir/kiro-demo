package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.interfaces.dto.UpdatePolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for policy management operations.
 * Clean Code: Thin controller focused on HTTP concerns with role-based security.
 * Accessible by both Operators and Admins.
 */
@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policy Management", description = "Policy management endpoints (Operator/Admin)")
@SecurityRequirement(name = "Bearer Authentication")
public class PolicyController {
    
    private final com.insurance.backoffice.application.service.PolicyService policyService;
    private final com.insurance.backoffice.application.service.PdfService pdfService;
    
    public PolicyController(com.insurance.backoffice.application.service.PolicyService policyService,
                           com.insurance.backoffice.application.service.PdfService pdfService) {
        this.policyService = policyService;
        this.pdfService = pdfService;
    }
    
    /**
     * Retrieves all policies.
     * Clean Code: Simple endpoint with role-based authorization.
     * 
     * @return list of all policies
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get all policies", 
        description = "Retrieve all policies in the system. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Policies retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PolicyResponse[].class),
                    examples = @ExampleObject(
                        name = "Policies List",
                        value = """
                        [
                          {
                            "id": 1,
                            "policyNumber": "POL-2024-001",
                            "clientName": "John Doe",
                            "vehicleRegistration": "ABC123",
                            "insuranceType": "OC",
                            "startDate": "2024-01-01",
                            "endDate": "2024-12-31",
                            "premium": 1200.00,
                            "status": "ACTIVE"
                          },
                          {
                            "id": 2,
                            "policyNumber": "POL-2024-002",
                            "clientName": "Jane Smith",
                            "vehicleRegistration": "XYZ789",
                            "insuranceType": "AC",
                            "startDate": "2024-02-01",
                            "endDate": "2025-01-31",
                            "premium": 2500.00,
                            "status": "ACTIVE"
                          }
                        ]
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        // For now, return active policies - this can be enhanced later with filtering
        List<com.insurance.backoffice.domain.Policy> policies = policyService.findPoliciesByStatus(com.insurance.backoffice.domain.PolicyStatus.ACTIVE);
        List<PolicyResponse> policyResponses = policies.stream()
                .map(this::mapToPolicyResponse)
                .toList();
        return ResponseEntity.ok(policyResponses);
    }
    
    /**
     * Retrieves a single policy by ID.
     * Clean Code: RESTful endpoint for single resource retrieval.
     * 
     * @param id policy ID
     * @return policy details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get policy by ID", 
        description = "Retrieve a specific policy by its ID. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Policy retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PolicyResponse.class),
                    examples = @ExampleObject(
                        name = "Policy Details",
                        value = """
                        {
                          "id": 1,
                          "policyNumber": "POL-2024-001",
                          "clientName": "John Doe",
                          "vehicleRegistration": "ABC123",
                          "insuranceType": "OC",
                          "startDate": "2024-01-01",
                          "endDate": "2024-12-31",
                          "premium": 1200.00,
                          "status": "ACTIVE"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<PolicyResponse> getPolicyById(
            @Parameter(description = "Policy ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            com.insurance.backoffice.domain.Policy policy = policyService.findPolicyById(id);
            PolicyResponse policyResponse = mapToPolicyResponse(policy);
            return ResponseEntity.ok(policyResponse);
        } catch (com.insurance.backoffice.application.service.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(
        summary = "Get policies by client", 
        description = "Retrieve all policies for a specific client. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Client policies retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PolicyResponse[].class),
                    examples = @ExampleObject(
                        name = "Client Policies",
                        value = """
                        [
                          {
                            "id": 1,
                            "policyNumber": "POL-2024-001",
                            "clientName": "John Doe",
                            "vehicleRegistration": "ABC123",
                            "insuranceType": "OC",
                            "startDate": "2024-01-01",
                            "endDate": "2024-12-31",
                            "premium": 1200.00,
                            "status": "ACTIVE"
                          }
                        ]
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<List<PolicyResponse>> getPoliciesByClient(
            @Parameter(description = "Client ID", example = "1", required = true)
            @PathVariable Long clientId) {
        List<com.insurance.backoffice.domain.Policy> policies = policyService.findPoliciesByClient(clientId);
        List<PolicyResponse> policyResponses = policies.stream()
                .map(this::mapToPolicyResponse)
                .toList();
        return ResponseEntity.ok(policyResponses);
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
    @Operation(
        summary = "Create policy", 
        description = "Create a new insurance policy. Accessible by Operators and Admins.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Policy creation data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreatePolicyRequest.class),
                examples = {
                    @ExampleObject(
                        name = "OC Policy",
                        value = """
                        {
                          "clientId": 1,
                          "vehicleId": 1,
                          "insuranceType": "OC",
                          "startDate": "2024-01-01",
                          "endDate": "2024-12-31",
                          "discountSurcharge": 0.00
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "AC Policy",
                        value = """
                        {
                          "clientId": 2,
                          "vehicleId": 2,
                          "insuranceType": "AC",
                          "startDate": "2024-02-01",
                          "endDate": "2025-01-31",
                          "discountSurcharge": -100.00
                        }
                        """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "Policy created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PolicyResponse.class),
                    examples = @ExampleObject(
                        name = "Created Policy",
                        value = """
                        {
                          "id": 1,
                          "policyNumber": "POL-2024-001",
                          "clientName": "John Doe",
                          "vehicleRegistration": "ABC123",
                          "insuranceType": "OC",
                          "startDate": "2024-01-01",
                          "endDate": "2024-12-31",
                          "premium": 1200.00,
                          "status": "ACTIVE"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        com.insurance.backoffice.domain.Policy createdPolicy = policyService.createPolicy(
            request.clientId(),
            request.vehicleId(),
            request.insuranceType(),
            request.startDate(),
            request.endDate(),
            request.discountSurcharge()
        );
        PolicyResponse policyResponse = mapToPolicyResponse(createdPolicy);
        return ResponseEntity.status(201).body(policyResponse);
    }
    
    /**
     * Updates an existing policy.
     * Clean Code: PUT endpoint for policy updates.
     * 
     * @param id policy ID
     * @param request policy update request
     * @return updated policy details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Update policy", 
        description = "Update an existing insurance policy. Accessible by Operators and Admins.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Policy update data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdatePolicyRequest.class),
                examples = @ExampleObject(
                    name = "Update Policy",
                    value = """
                    {
                      "startDate": "2024-02-01",
                      "endDate": "2025-01-31",
                      "discountSurcharge": -50.00
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Policy updated successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PolicyResponse.class),
                    examples = @ExampleObject(
                        name = "Updated Policy",
                        value = """
                        {
                          "id": 1,
                          "policyNumber": "POL-2024-001",
                          "clientName": "John Doe",
                          "vehicleRegistration": "ABC123",
                          "insuranceType": "OC",
                          "startDate": "2024-02-01",
                          "endDate": "2025-01-31",
                          "premium": 1150.00,
                          "status": "ACTIVE"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<PolicyResponse> updatePolicy(
            @Parameter(description = "Policy ID", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdatePolicyRequest request) {
        try {
            com.insurance.backoffice.domain.Policy updatedPolicy = policyService.updatePolicy(
                id,
                request.startDate(),
                request.endDate(),
                request.discountSurcharge(),
                request.amountGuaranteed(),
                request.coverageArea()
            );
            PolicyResponse policyResponse = mapToPolicyResponse(updatedPolicy);
            return ResponseEntity.ok(policyResponse);
        } catch (com.insurance.backoffice.application.service.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
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
    @Operation(
        summary = "Generate policy PDF", 
        description = "Generate PDF document for a policy. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "PDF generated successfully",
                content = @Content(
                    mediaType = "application/pdf",
                    schema = @Schema(type = "string", format = "binary")
                )
            ),
            @ApiResponse(responseCode = "404", description = "Policy not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required"),
            @ApiResponse(responseCode = "500", description = "PDF generation failed")
        }
    )
    public ResponseEntity<byte[]> generatePolicyPdf(
            @Parameter(description = "Policy ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            com.insurance.backoffice.domain.Policy policy = policyService.findPolicyById(id);
            byte[] pdfBytes = pdfService.generatePolicyPdf(policy);
            
            String filename = "policy_" + policy.getPolicyNumber() + ".pdf";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(pdfBytes);
                    
        } catch (com.insurance.backoffice.application.service.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (com.insurance.backoffice.application.service.PdfGenerationException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Retrieves all clients for policy form dropdowns.
     * Clean Code: Simple endpoint for form data population.
     * 
     * @return list of all clients
     */
    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get all clients", 
        description = "Retrieve all clients for policy form dropdowns. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Clients retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientResponse[].class)
                )
            ),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<com.insurance.backoffice.domain.Client> clients = policyService.findAllClients();
        List<ClientResponse> clientResponses = clients.stream()
                .map(this::mapToClientResponse)
                .toList();
        return ResponseEntity.ok(clientResponses);
    }
    
    /**
     * Retrieves all vehicles for policy form dropdowns.
     * Clean Code: Simple endpoint for form data population.
     * 
     * @return list of all vehicles
     */
    @GetMapping("/vehicles")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get all vehicles", 
        description = "Retrieve all vehicles for policy form dropdowns. Accessible by Operators and Admins.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Vehicles retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VehicleResponse[].class)
                )
            ),
            @ApiResponse(responseCode = "403", description = "Access denied - Operator or Admin role required")
        }
    )
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        List<com.insurance.backoffice.domain.Vehicle> vehicles = policyService.findAllVehicles();
        List<VehicleResponse> vehicleResponses = vehicles.stream()
                .map(this::mapToVehicleResponse)
                .toList();
        return ResponseEntity.ok(vehicleResponses);
    }
    
    /**
     * Data Transfer Object for policy response.
     * Clean Code: Inner record for response data structure.
     */
    @Schema(description = "Policy information response")
    public record PolicyResponse(
        @Schema(description = "Policy ID", example = "1")
        Long id,
        
        @Schema(description = "Policy number", example = "POL-2024-001")
        String policyNumber,
        
        @Schema(description = "Client full name", example = "John Doe")
        String clientName,
        
        @Schema(description = "Vehicle registration number", example = "ABC123")
        String vehicleRegistration,
        
        @Schema(description = "Insurance type", example = "OC")
        com.insurance.backoffice.domain.InsuranceType insuranceType,
        
        @Schema(description = "Policy start date", example = "2024-01-01")
        LocalDate startDate,
        
        @Schema(description = "Policy end date", example = "2024-12-31")
        LocalDate endDate,
        
        @Schema(description = "Policy premium amount", example = "1200.00")
        BigDecimal premium,
        
        @Schema(description = "Discount or surcharge amount", example = "100.00")
        BigDecimal discountSurcharge,
        
        @Schema(description = "Amount guaranteed for coverage", example = "50000.00")
        BigDecimal amountGuaranteed,
        
        @Schema(description = "Coverage area", example = "Europe")
        String coverageArea,
        
        @Schema(description = "Policy status", example = "ACTIVE")
        com.insurance.backoffice.domain.PolicyStatus status
    ) {}
    
    /**
     * Data Transfer Object for policy creation request.
     * Clean Code: Inner record for request data structure.
     */
    @Schema(description = "Policy creation request")
    public record CreatePolicyRequest(
        @Schema(description = "Client ID", example = "1", required = true)
        @NotNull(message = "Client ID is required")
        Long clientId,
        
        @Schema(description = "Vehicle ID", example = "1", required = true)
        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,
        
        @Schema(description = "Insurance type", example = "OC", required = true)
        @NotNull(message = "Insurance type is required")
        com.insurance.backoffice.domain.InsuranceType insuranceType,
        
        @Schema(description = "Policy start date", example = "2024-01-01", required = true)
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        
        @Schema(description = "Policy end date", example = "2024-12-31", required = true)
        @NotNull(message = "End date is required")
        LocalDate endDate,
        
        @Schema(description = "Discount or surcharge amount", example = "0.00")
        BigDecimal discountSurcharge
    ) {}
    
    /**
     * Data Transfer Object for client response.
     * Clean Code: Inner record for client data structure.
     */
    @Schema(description = "Client information response")
    public record ClientResponse(
        @Schema(description = "Client ID", example = "1")
        Long id,
        
        @Schema(description = "Client full name", example = "John Doe")
        String fullName,
        
        @Schema(description = "Client PESEL", example = "12345678901")
        String pesel,
        
        @Schema(description = "Client email", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "Client phone number", example = "+48123456789")
        String phoneNumber
    ) {}
    
    /**
     * Data Transfer Object for vehicle response.
     * Clean Code: Inner record for vehicle data structure.
     */
    @Schema(description = "Vehicle information response")
    public record VehicleResponse(
        @Schema(description = "Vehicle ID", example = "1")
        Long id,
        
        @Schema(description = "Vehicle make", example = "Toyota")
        String make,
        
        @Schema(description = "Vehicle model", example = "Corolla")
        String model,
        
        @Schema(description = "Vehicle registration number", example = "ABC123")
        String registrationNumber,
        
        @Schema(description = "Vehicle VIN", example = "1HGBH41JXMN109186")
        String vin,
        
        @Schema(description = "Year of manufacture", example = "2020")
        Integer yearOfManufacture,
        
        @Schema(description = "Engine capacity in cc", example = "1600")
        Integer engineCapacity,
        
        @Schema(description = "Engine power in HP", example = "120")
        Integer power
    ) {}
    
    /**
     * Maps Policy entity to PolicyResponse DTO.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private PolicyResponse mapToPolicyResponse(com.insurance.backoffice.domain.Policy policy) {
        return new PolicyResponse(
            policy.getId(),
            policy.getPolicyNumber(),
            policy.getClient().getFullName(),
            policy.getVehicle().getRegistrationNumber(),
            policy.getInsuranceType(),
            policy.getStartDate(),
            policy.getEndDate(),
            policy.getPremium(),
            policy.getDiscountSurcharge(),
            policy.getAmountGuaranteed(),
            policy.getCoverageArea(),
            policy.getStatus()
        );
    }
    
    /**
     * Maps Client entity to ClientResponse DTO.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private ClientResponse mapToClientResponse(com.insurance.backoffice.domain.Client client) {
        return new ClientResponse(
            client.getId(),
            client.getFullName(),
            client.getPesel(),
            client.getEmail(),
            client.getPhoneNumber()
        );
    }
    
    /**
     * Maps Vehicle entity to VehicleResponse DTO.
     * Clean Code: Extracted mapping logic for reusability.
     */
    private VehicleResponse mapToVehicleResponse(com.insurance.backoffice.domain.Vehicle vehicle) {
        return new VehicleResponse(
            vehicle.getId(),
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getRegistrationNumber(),
            vehicle.getVin(),
            vehicle.getYearOfManufacture(),
            vehicle.getEngineCapacity(),
            vehicle.getPower()
        );
    }
}