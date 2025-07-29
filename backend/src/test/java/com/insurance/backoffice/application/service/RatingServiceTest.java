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
 * Unit tests for RatingService.
 * Clean Code: Comprehensive test coverage with descriptive test names and AAA pattern.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    
    @Mock
    private RatingTableRepository ratingTableRepository;
    
    @InjectMocks
    private RatingService ratingService;
    
    private Vehicle testVehicle;
    private LocalDate policyDate;
    private RatingTable vehicleAgeRating;
    private RatingTable engineCapacityRating;
    private RatingTable powerRating;
    
    @BeforeEach
    void setUp() {
        testVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Camry")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1234567890ABCDEFG")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        policyDate = LocalDate.now();
        
        vehicleAgeRating = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("VEHICLE_AGE_4")
                .multiplier(new BigDecimal("1.1"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        engineCapacityRating = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("ENGINE_MEDIUM")
                .multiplier(new BigDecimal("1.2"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        powerRating = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("POWER_MEDIUM")
                .multiplier(new BigDecimal("1.15"))
                .validFrom(LocalDate.now().minusYears(1))
                .build();
    }
    
    @Test
    void shouldCalculatePremiumForOCInsuranceSuccessfully() {
        // Given
        BigDecimal expectedBasePremium = new BigDecimal("800.00");
        
        // Calculate the actual vehicle age for the test vehicle
        // Vehicle first registered on 2020-01-01, current date is around 2025
        int vehicleAge = java.time.Period.between(testVehicle.getFirstRegistrationDate(), policyDate).getYears();
        String expectedAgeKey = "VEHICLE_AGE_" + Math.min(vehicleAge, 10);
        
        // Mock all possible rating key lookups to return empty lists by default
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // Then override specific ones we want to return values
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq(expectedAgeKey), eq(policyDate)))
                .thenReturn(Arrays.asList(vehicleAgeRating));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("ENGINE_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(engineCapacityRating));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("POWER_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(powerRating));
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.OC, testVehicle, policyDate);
        
        // Then
        // Expected calculation: 800.00 * 1.1 * 1.2 * 1.15 = 1,212.00
        BigDecimal expectedPremium = expectedBasePremium
                .multiply(vehicleAgeRating.getMultiplier())
                .multiply(engineCapacityRating.getMultiplier())
                .multiply(powerRating.getMultiplier())
                .setScale(2, java.math.RoundingMode.HALF_UP);
        
        assertThat(result).isEqualTo(expectedPremium);
    }
    
    @Test
    void shouldCalculatePremiumForACInsuranceSuccessfully() {
        // Given
        BigDecimal expectedBasePremium = new BigDecimal("1200.00");
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.AC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.AC, testVehicle, policyDate);
        
        // Then
        // With no rating factors, should return base premium
        assertThat(result).isEqualTo(expectedBasePremium);
    }
    
    @Test
    void shouldCalculatePremiumForNNWInsuranceSuccessfully() {
        // Given
        BigDecimal expectedBasePremium = new BigDecimal("300.00");
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.NNW), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.NNW, testVehicle, policyDate);
        
        // Then
        assertThat(result).isEqualTo(expectedBasePremium);
    }
    
    @Test
    void shouldThrowExceptionWhenInsuranceTypeIsNull() {
        // When & Then
        assertThatThrownBy(() -> ratingService.calculatePremium(null, testVehicle, policyDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insurance type cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenVehicleIsNull() {
        // When & Then
        assertThatThrownBy(() -> ratingService.calculatePremium(InsuranceType.OC, null, policyDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenPolicyDateIsNull() {
        // When & Then
        assertThatThrownBy(() -> ratingService.calculatePremium(InsuranceType.OC, testVehicle, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy date cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldCalculatePremiumBreakdownSuccessfully() {
        // Given
        // Calculate the actual vehicle age for the test vehicle
        int vehicleAge = java.time.Period.between(testVehicle.getFirstRegistrationDate(), policyDate).getYears();
        String expectedAgeKey = "VEHICLE_AGE_" + Math.min(vehicleAge, 10);
        
        // Mock all possible rating key lookups to return empty lists by default
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // Then override specific ones we want to return values
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq(expectedAgeKey), eq(policyDate)))
                .thenReturn(Arrays.asList(vehicleAgeRating));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("ENGINE_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(engineCapacityRating));
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("POWER_MEDIUM"), eq(policyDate)))
                .thenReturn(Arrays.asList(powerRating));
        
        // When
        RatingService.PremiumBreakdown result = ratingService.calculatePremiumBreakdown(
                InsuranceType.OC, testVehicle, policyDate);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBasePremium()).isEqualTo(new BigDecimal("800.00"));
        assertThat(result.getRatingFactors()).isNotEmpty();
        assertThat(result.getRatingFactors()).containsKey("VEHICLE_AGE");
        assertThat(result.getRatingFactors()).containsKey("ENGINE_CAPACITY");
        assertThat(result.getRatingFactors()).containsKey("POWER");
        assertThat(result.getFinalPremium()).isGreaterThan(result.getBasePremium());
    }
    
    @Test
    void shouldGetRatingTablesForDateSuccessfully() {
        // Given
        List<RatingTable> expectedTables = Arrays.asList(vehicleAgeRating, engineCapacityRating);
        when(ratingTableRepository.findByInsuranceTypeValidForDate(InsuranceType.OC, policyDate))
                .thenReturn(expectedTables);
        
        // When
        List<RatingTable> result = ratingService.getRatingTablesForDate(InsuranceType.OC, policyDate);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(vehicleAgeRating, engineCapacityRating);
        verify(ratingTableRepository).findByInsuranceTypeValidForDate(InsuranceType.OC, policyDate);
    }
    
    @Test
    void shouldThrowExceptionWhenGettingRatingTablesWithNullInsuranceType() {
        // When & Then
        assertThatThrownBy(() -> ratingService.getRatingTablesForDate(null, policyDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insurance type cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenGettingRatingTablesWithNullDate() {
        // When & Then
        assertThatThrownBy(() -> ratingService.getRatingTablesForDate(InsuranceType.OC, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldGetCurrentRatingTablesSuccessfully() {
        // Given
        List<RatingTable> expectedTables = Arrays.asList(vehicleAgeRating, engineCapacityRating);
        when(ratingTableRepository.findCurrentlyValidByInsuranceType(InsuranceType.OC))
                .thenReturn(expectedTables);
        
        // When
        List<RatingTable> result = ratingService.getCurrentRatingTables(InsuranceType.OC);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(vehicleAgeRating, engineCapacityRating);
        verify(ratingTableRepository).findCurrentlyValidByInsuranceType(InsuranceType.OC);
    }
    
    @Test
    void shouldThrowExceptionWhenGettingCurrentRatingTablesWithNullInsuranceType() {
        // When & Then
        assertThatThrownBy(() -> ratingService.getCurrentRatingTables(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insurance type cannot be null");
        
        verifyNoInteractions(ratingTableRepository);
    }
    
    @Test
    void shouldUseNeutralMultiplierWhenNoRatingTableFound() {
        // Given
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.OC, testVehicle, policyDate);
        
        // Then
        // Should return base premium (800.00) since all rating factors default to 1.0
        assertThat(result).isEqualTo(new BigDecimal("800.00"));
    }
    
    @Test
    void shouldHandleSmallEngineCapacityCorrectly() {
        // Given
        Vehicle smallEngineVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Yaris")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1234567890ABCDEFG")
                .engineCapacity(900) // Small engine
                .power(70)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        // Mock all possible rating key lookups to return empty lists by default
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // Then override the specific one we want to test
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("ENGINE_SMALL"), eq(policyDate)))
                .thenReturn(Arrays.asList(engineCapacityRating));
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.OC, smallEngineVehicle, policyDate);
        
        // Then
        assertThat(result).isNotNull();
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
                InsuranceType.OC, "ENGINE_SMALL", policyDate);
    }
    
    @Test
    void shouldHandleLargeEngineCapacityCorrectly() {
        // Given
        Vehicle largeEngineVehicle = Vehicle.builder()
                .make("BMW")
                .model("X5")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("1234567890ABCDEFG")
                .engineCapacity(3000) // Large engine
                .power(300)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        // Mock all possible rating key lookups to return empty lists by default
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), anyString(), eq(policyDate)))
                .thenReturn(Arrays.asList());
        
        // Then override the specific one we want to test
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                eq(InsuranceType.OC), eq("ENGINE_XLARGE"), eq(policyDate)))
                .thenReturn(Arrays.asList(engineCapacityRating));
        
        // When
        BigDecimal result = ratingService.calculatePremium(InsuranceType.OC, largeEngineVehicle, policyDate);
        
        // Then
        assertThat(result).isNotNull();
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
                InsuranceType.OC, "ENGINE_XLARGE", policyDate);
    }
    
    @Test
    void shouldThrowPremiumCalculationExceptionWhenRepositoryFails() {
        // Given
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                any(), anyString(), any()))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThatThrownBy(() -> ratingService.calculatePremium(InsuranceType.OC, testVehicle, policyDate))
                .isInstanceOf(PremiumCalculationException.class)
                .hasMessage("Failed to calculate premium for OC insurance")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}