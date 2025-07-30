package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.RatingTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RatingValidationService.
 * Clean Code: Comprehensive test coverage with descriptive test names and AAA pattern.
 */
@ExtendWith(MockitoExtension.class)
class RatingValidationServiceTest {
    
    @Mock
    private RatingTableRepository ratingTableRepository;
    
    @InjectMocks
    private RatingValidationService ratingValidationService;
    
    private Vehicle validVehicle;
    private Vehicle invalidVehicle;
    private LocalDate policyDate;
    private RatingTable validRatingTable;
    private RatingTable invalidRatingTable;
    
    @BeforeEach
    void setUp() {
        validVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Camry")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1234567890ABCDEFG")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        // Create invalid vehicle using setters to bypass builder validation
        invalidVehicle = new Vehicle();
        invalidVehicle.setMake("Invalid");
        invalidVehicle.setModel("Vehicle");
        invalidVehicle.setYearOfManufacture(2020);
        invalidVehicle.setRegistrationNumber("INV123");
        invalidVehicle.setVin("INVALID123456789");
        invalidVehicle.setEngineCapacity(10000); // Invalid - too large
        invalidVehicle.setPower(2000); // Invalid - too high
        invalidVehicle.setFirstRegistrationDate(LocalDate.now().plusDays(1)); // Invalid - future date
        
        policyDate = LocalDate.now();
        
        validRatingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("VEHICLE_AGE_4")
                .multiplier(new BigDecimal("1.1000"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        // Create invalid rating table using setters to bypass builder validation
        invalidRatingTable = new RatingTable();
        invalidRatingTable.setInsuranceType(InsuranceType.OC);
        invalidRatingTable.setRatingKey("INVALID_KEY");
        invalidRatingTable.setMultiplier(new BigDecimal("10.0000")); // Invalid - too high
        invalidRatingTable.setValidFrom(LocalDate.now().plusDays(1)); // Future date
        invalidRatingTable.setValidTo(LocalDate.now()); // Invalid - validTo before validFrom
    }
    
    @Test
    void shouldValidateRatingFactorsSuccessfullyForValidVehicle() {
        // Given
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).isEmpty();
    }
    
    @Test
    void shouldFailValidationForInvalidVehicleCharacteristics() {
        // Given
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, invalidVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).isNotEmpty();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("Engine capacity"));
        assertThat(result.getErrors()).anyMatch(error -> error.contains("Power"));
        assertThat(result.getErrors()).anyMatch(error -> error.contains("future"));
    }
    
    @Test
    void shouldFailValidationForACInsuranceOnOldVehicle() {
        // Given
        Vehicle oldVehicle = Vehicle.builder()
                .make("Old")
                .model("Car")
                .yearOfManufacture(2000)
                .registrationNumber("OLD123")
                .vin("OLD1234567890123")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2000, 1, 1)) // 25 years old
                .build();
        
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.AC, oldVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("AC insurance is not available"));
    }
    
    @Test
    void shouldGenerateWarningsForUnusualVehicleCharacteristics() {
        // Given
        Vehicle unusualVehicle = Vehicle.builder()
                .make("Unusual")
                .model("Car")
                .yearOfManufacture(2020)
                .registrationNumber("UNU123")
                .vin("UNU1234567890123")
                .engineCapacity(4000) // Large engine
                .power(100) // Low power for large engine
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, unusualVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("Unusual combination"));
    }
    
    @Test
    void shouldFailValidationWhenRatingTablesAreMissing() {
        // Given
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                any(InsuranceType.class), anyString(), any(LocalDate.class)))
                .thenReturn(Arrays.asList()); // No rating tables found
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).isNotEmpty();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("Missing rating factor"));
    }
    
    @Test
    void shouldValidateRatingTableSuccessfully() {
        // Given
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList());
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(validRatingTable);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }
    
    @Test
    void shouldFailRatingTableValidationForInvalidMultiplier() {
        // Given
        RatingTable invalidMultiplierTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("TEST_KEY")
                .multiplier(new BigDecimal("0.05")) // Too low
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList());
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(invalidMultiplierTable);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("below minimum"));
    }
    
    @Test
    void shouldFailRatingTableValidationForInvalidDateRange() {
        // Given - Create invalid rating table using setters to bypass builder validation
        RatingTable invalidDateTable = new RatingTable();
        invalidDateTable.setInsuranceType(InsuranceType.OC);
        invalidDateTable.setRatingKey("TEST_KEY");
        invalidDateTable.setMultiplier(new BigDecimal("1.0000"));
        invalidDateTable.setValidFrom(LocalDate.now());
        invalidDateTable.setValidTo(LocalDate.now().minusDays(1)); // validTo before validFrom
        
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList());
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(invalidDateTable);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("Valid from date must be before"));
    }
    
    @Test
    void shouldGenerateWarningForOverlappingRatingTables() {
        // Given
        RatingTable overlappingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("OVERLAPPING_KEY")
                .multiplier(new BigDecimal("1.2000"))
                .validFrom(LocalDate.now().minusMonths(6))
                .build();
        
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList(overlappingTable));
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(validRatingTable);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("overlapping validity periods"));
    }
    
    @Test
    void shouldGenerateWarningForNonStandardRatingKey() {
        // Given
        RatingTable nonStandardKeyTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("CUSTOM_WEIRD_KEY") // Non-standard naming
                .multiplier(new BigDecimal("1.1000"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList());
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(nonStandardKeyTable);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("does not follow standard naming"));
    }
    
    @Test
    void shouldReturnTrueWhenCanCalculatePremium() {
        // Given
        mockValidRatingTableLookups();
        
        // When
        boolean canCalculate = ratingValidationService.canCalculatePremium(InsuranceType.OC, validVehicle, policyDate);
        
        // Then
        assertThat(canCalculate).isTrue();
    }
    
    @Test
    void shouldReturnFalseWhenCannotCalculatePremium() {
        // Given
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                any(InsuranceType.class), anyString(), any(LocalDate.class)))
                .thenReturn(Arrays.asList()); // No rating tables
        
        // When
        boolean canCalculate = ratingValidationService.canCalculatePremium(InsuranceType.OC, validVehicle, policyDate);
        
        // Then
        assertThat(canCalculate).isFalse();
    }
    
    @Test
    void shouldReturnMissingRatingFactors() {
        // Given - Mock specific missing factor
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("VEHICLE_AGE_5"), eq(policyDate)))
                .thenReturn(Arrays.asList()); // Missing this factor
        
        // Mock other factors as present
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("ENGINE_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(validRatingTable));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("POWER_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(validRatingTable));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("OC_STANDARD"), eq(policyDate)))
                .thenReturn(Arrays.asList(validRatingTable));
        
        // When
        List<String> missingFactors = ratingValidationService.getMissingRatingFactors(
                InsuranceType.OC, validVehicle, policyDate);
        
        // Then
        assertThat(missingFactors).isNotEmpty();
        assertThat(missingFactors).contains("VEHICLE_AGE_5");
    }
    
    @Test
    void shouldThrowExceptionForNullInsuranceType() {
        // When & Then
        assertThatThrownBy(() -> ratingValidationService.validateRatingFactors(null, validVehicle, policyDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insurance type cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullVehicle() {
        // When & Then
        assertThatThrownBy(() -> ratingValidationService.validateRatingFactors(InsuranceType.OC, null, policyDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullPolicyDate() {
        // When & Then
        assertThatThrownBy(() -> ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy date cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullRatingTable() {
        // When & Then
        assertThatThrownBy(() -> ratingValidationService.validateRatingTable(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rating table cannot be null");
    }
    
    @Test
    void shouldGenerateWarningForFuturePolicyDate() {
        // Given
        LocalDate futurePolicyDate = LocalDate.now().plusYears(2);
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, futurePolicyDate);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("more than 1 year in the future"));
    }
    
    @Test
    void shouldGenerateWarningForPastPolicyDate() {
        // Given
        LocalDate pastPolicyDate = LocalDate.now().minusYears(3);
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, pastPolicyDate);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("more than 2 years in the past"));
    }
    
    @Test
    void shouldGenerateWarningForVeryOldVehicle() {
        // Given
        Vehicle veryOldVehicle = Vehicle.builder()
                .make("Classic")
                .model("Car")
                .yearOfManufacture(1970)
                .registrationNumber("CLA123")
                .vin("CLA1234567890123")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(1970, 1, 1)) // Very old
                .build();
        
        mockValidRatingTableLookups();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, veryOldVehicle, policyDate);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("Vehicle is very old"));
    }
    
    @Test
    void shouldGenerateWarningForInconsistentRatingKey() {
        // Given
        RatingTable inconsistentTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("AC_COMPREHENSIVE") // AC key for OC insurance
                .multiplier(new BigDecimal("1.1000"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        when(ratingTableRepository.findOverlappingValidityPeriods(
                any(InsuranceType.class), anyString(), any(LocalDate.class), any()))
                .thenReturn(Arrays.asList());
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingTable(inconsistentTable);
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> warning.contains("seems inconsistent"));
    }
    
    /**
     * Helper method to mock valid rating table lookups.
     * Clean Code: Extracted common test setup.
     */
    private void mockValidRatingTableLookups() {
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                any(InsuranceType.class), anyString(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(validRatingTable));
    }
}