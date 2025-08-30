package com.insurance.backoffice.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for client management operations.
 * Clean Code: Thin controller focused on HTTP concerns with role-based security.
 * Accessible by both Operators and Admins.
 */
@RestController
@RequestMapping("/api/clients")
@Tag(name = "Client Management", description = "Client management endpoints (Operator/Admin)")
@SecurityRequirement(name = "Bearer Authentication")
public class ClientController {
    
    private final com.insurance.backoffice.application.service.PolicyService policyService;
    
    public ClientController(com.insurance.backoffice.application.service.PolicyService policyService) {
        this.policyService = policyService;
    }
    
    /**
     * Retrieves all clients.
     * Clean Code: Simple endpoint with role-based authorization.
     * 
     * @return list of all clients
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get all clients", 
        description = "Retrieve all clients in the system. Accessible by Operators and Admins.",
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
}