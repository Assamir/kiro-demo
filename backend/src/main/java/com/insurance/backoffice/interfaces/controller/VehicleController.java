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
 * REST controller for vehicle management operations.
 * Clean Code: Thin controller focused on HTTP concerns with role-based security.
 * Accessible by both Operators and Admins.
 */
@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle Management", description = "Vehicle management endpoints (Operator/Admin)")
@SecurityRequirement(name = "Bearer Authentication")
public class VehicleController {
    
    private final com.insurance.backoffice.application.service.PolicyService policyService;
    
    public VehicleController(com.insurance.backoffice.application.service.PolicyService policyService) {
        this.policyService = policyService;
    }
    
    /**
     * Retrieves all vehicles.
     * Clean Code: Simple endpoint with role-based authorization.
     * 
     * @return list of all vehicles
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    @Operation(
        summary = "Get all vehicles", 
        description = "Retrieve all vehicles in the system. Accessible by Operators and Admins.",
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