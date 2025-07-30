package com.insurance.backoffice.interfaces.controller;

import com.insurance.backoffice.application.service.RatingService;
import com.insurance.backoffice.application.service.RatingValidationService;
import com.insurance.backoffice.domain.InsuranceType;
import com.insurance.backoffice.domain.RatingTable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for rating system operations.
 * Provides endpoints for rating table management and premium calculation support.
 */
@RestController
@RequestMapping("/api/rating")
@Tag(name = "Rating", description = "Rating system operations")
public class RatingController {
    
    private final RatingService ratingService;
    private final RatingValidationService ratingValidationService;
    
    @Autowired
    public RatingController(RatingService ratingService, RatingValidationService ratingValidationService) {
        this.ratingService = ratingService;
        this.ratingValidationService = ratingValidationService;
    }
    
    /**
     * Gets all rating tables for a specific insurance type.
     * Available to both Admin and Operator users.
     */
    @GetMapping("/tables/{insuranceType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Get rating tables by insurance type", 
               description = "Retrieves all currently valid rating tables for the specified insurance type")
    public ResponseEntity<List<RatingTable>> getRatingTables(
            @Parameter(description = "Insurance type (OC, AC, NNW)")
            @PathVariable InsuranceType insuranceType) {
        
        List<RatingTable> ratingTables = ratingService.getCurrentRatingTables(insuranceType);
        return ResponseEntity.ok(ratingTables);
    }
    
    /**
     * Gets rating tables for a specific insurance type and date.
     * Available to both Admin and Operator users.
     */
    @GetMapping("/tables/{insuranceType}/date/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Get rating tables by insurance type and date", 
               description = "Retrieves rating tables valid for the specified insurance type and date")
    public ResponseEntity<List<RatingTable>> getRatingTablesForDate(
            @Parameter(description = "Insurance type (OC, AC, NNW)")
            @PathVariable InsuranceType insuranceType,
            @Parameter(description = "Date in YYYY-MM-DD format")
            @PathVariable LocalDate date) {
        
        List<RatingTable> ratingTables = ratingService.getRatingTablesForDate(insuranceType, date);
        return ResponseEntity.ok(ratingTables);
    }
}