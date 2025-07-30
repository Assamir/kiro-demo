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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for RatingService premium calculation scenarios.
 * Clean Code: Focused test class for specific premium calculation business logic.
 */
@ExtendWith(MockitoExtension.class)
class RatingServicePremiumCalculationTest {
    
    @Mock
    private RatingTableRepository ratingTableRepository;
    
    @InjectMocks
    private RatingService ratingService;
    
    private LocalDate policyDate;
    
    @BeforeEach
    void setUp() {
        policyDate = LocalDate.now();
    }
    
    @Test
    void shouldCalculateOCPremiumForNewSmallCar() {
        // Given - New small car (0 years, 1000cc, 75HP)
        Vehicle newSmallCar = createVehicle(2025, 1000, 75, LocalDate.of(2025, 1, 1));
        
        // Mock rating factors for new small car
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_0", new BigDecimal("0.9000"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_SMALL", new BigDecimal("0.8500"));
        mockRatingFactor(InsuranceType.OC, "POWER_LOW", new BigDecimal("0.9000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, newSmallCar, policyDate);
        
        // Then
        // Expected: 800.00 * 0.9 * 0.85 * 0.9 * 1.0 = 550.80
        BigDecimal expected = new BigDecimal("800.00")
                .multiply(new BigDecimal("0.9000"))
                .multiply(new BigDecimal("0.8500"))
                .multiply(new BigDecimal("0.9000"))
                .multiply(new BigDecimal("1.0000"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculateOCPremiumForOldLargeCar() {
        // Given - Old large car (10 years, 3000cc, 300HP)
        Vehicle oldLargeCar = createVehicle(2015, 3000, 300, LocalDate.of(2015, 1, 1));
        
        // Mock rating factors for old large car
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_10", new BigDecimal("1.4000"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_XLARGE", new BigDecimal("1.5000"));
        mockRatingFactor(InsuranceType.OC, "POWER_VERY_HIGH", new BigDecimal("1.6000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, oldLargeCar, policyDate);
        
        // Then
        // Expected: 800.00 * 1.4 * 1.5 * 1.6 * 1.0 = 2,688.00
        BigDecimal expected = new BigDecimal("800.00")
                .multiply(new BigDecimal("1.4000"))
                .multiply(new BigDecimal("1.5000"))
                .multiply(new BigDecimal("1.6000"))
                .multiply(new BigDecimal("1.0000"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculateACPremiumForNewCar() {
        // Given - New car for AC insurance (0 years, 1600cc, 120HP)
        Vehicle newCar = createVehicle(2025, 1600, 120, LocalDate.of(2025, 1, 1));
        
        // Mock rating factors for AC insurance
        mockRatingFactor(InsuranceType.AC, "VEHICLE_AGE_0", new BigDecimal("1.2000"));
        mockRatingFactor(InsuranceType.AC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.AC, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.AC, "AC_COMPREHENSIVE", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.AC, newCar, policyDate);
        
        // Then
        // Expected: 1200.00 * 1.2 * 1.0 * 1.0 * 1.0 = 1,440.00
        BigDecimal expected = new BigDecimal("1200.00")
                .multiply(new BigDecimal("1.2000"))
                .multiply(new BigDecimal("1.0000"))
                .multiply(new BigDecimal("1.0000"))
                .multiply(new BigDecimal("1.0000"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculateACPremiumForOldCar() {
        // Given - Old car for AC insurance (8 years, 2000cc, 180HP)
        Vehicle oldCar = createVehicle(2017, 2000, 180, LocalDate.of(2017, 1, 1));
        
        // Mock rating factors for AC insurance on old car
        mockRatingFactor(InsuranceType.AC, "VEHICLE_AGE_8", new BigDecimal("0.8000"));
        mockRatingFactor(InsuranceType.AC, "ENGINE_LARGE", new BigDecimal("1.1500"));
        mockRatingFactor(InsuranceType.AC, "POWER_HIGH", new BigDecimal("1.2000"));
        mockRatingFactor(InsuranceType.AC, "AC_COMPREHENSIVE", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.AC, oldCar, policyDate);
        
        // Then
        // Expected: 1200.00 * 0.8 * 1.15 * 1.2 * 1.0 = 1,324.80
        BigDecimal expected = new BigDecimal("1200.00")
                .multiply(new BigDecimal("0.8000"))
                .multiply(new BigDecimal("1.1500"))
                .multiply(new BigDecimal("1.2000"))
                .multiply(new BigDecimal("1.0000"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculateNNWPremiumWithMinimalFactors() {
        // Given - Any car for NNW insurance (factors have minimal impact)
        Vehicle anyCar = createVehicle(2020, 1600, 120, LocalDate.of(2020, 1, 1));
        
        // Mock rating factors for NNW insurance (all neutral)
        mockRatingFactor(InsuranceType.NNW, "VEHICLE_AGE_5", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.NNW, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.NNW, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.NNW, "NNW_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.NNW, anyCar, policyDate);
        
        // Then
        // Expected: 300.00 * 1.0 * 1.0 * 1.0 * 1.0 = 300.00
        BigDecimal expected = new BigDecimal("300.00");
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculatePremiumWithMissingRatingFactors() {
        // Given - Car with some missing rating factors
        Vehicle car = createVehicle(2020, 1600, 120, LocalDate.of(2020, 1, 1));
        
        // Mock only some rating factors (others will default to 1.0)
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_5", new BigDecimal("1.1500"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        // Missing POWER_MEDIUM and OC_STANDARD factors will return empty lists (default to 1.0)
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, car, policyDate);
        
        // Then
        // Expected: 800.00 * 1.15 * 1.0 * 1.0 * 1.0 = 920.00 (missing factors default to 1.0)
        BigDecimal expected = new BigDecimal("800.00")
                .multiply(new BigDecimal("1.1500"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculatePremiumBreakdownWithAllFactors() {
        // Given
        Vehicle car = createVehicle(2020, 1600, 120, LocalDate.of(2020, 1, 1));
        
        // Mock all rating factors
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_5", new BigDecimal("1.1500"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        RatingService.PremiumBreakdown breakdown = 
            ratingService.calculatePremiumBreakdown(InsuranceType.OC, car, policyDate);
        
        // Then
        assertThat(breakdown.getBasePremium()).isEqualTo(new BigDecimal("800.00"));
        assertThat(breakdown.getRatingFactors()).hasSize(4);
        assertThat(breakdown.getRatingFactors()).containsEntry("VEHICLE_AGE", new BigDecimal("1.1500"));
        assertThat(breakdown.getRatingFactors()).containsEntry("ENGINE_CAPACITY", new BigDecimal("1.0000"));
        assertThat(breakdown.getRatingFactors()).containsEntry("POWER", new BigDecimal("1.0000"));
        assertThat(breakdown.getRatingFactors()).containsEntry("OC_COVERAGE", new BigDecimal("1.0000"));
        assertThat(breakdown.getFinalPremium()).isEqualTo(new BigDecimal("920.00"));
    }
    
    @Test
    void shouldHandleVehicleAgeCapAt10Years() {
        // Given - Very old car (20 years old)
        Vehicle veryOldCar = createVehicle(2005, 1600, 120, LocalDate.of(2005, 1, 1));
        
        // Mock rating factor for capped age (should use VEHICLE_AGE_10)
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_10", new BigDecimal("1.4000"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, veryOldCar, policyDate);
        
        // Then
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
            InsuranceType.OC, "VEHICLE_AGE_10", policyDate);
        
        BigDecimal expected = new BigDecimal("800.00")
                .multiply(new BigDecimal("1.4000"))
                .setScale(2, RoundingMode.HALF_UP);
        
        assertThat(premium).isEqualTo(expected);
    }
    
    @Test
    void shouldCalculatePremiumForDifferentEngineSizes() {
        // Test all engine size categories
        testEngineCategory(900, "ENGINE_SMALL");
        testEngineCategory(1200, "ENGINE_MEDIUM");
        testEngineCategory(1800, "ENGINE_LARGE");
        testEngineCategory(2500, "ENGINE_XLARGE");
    }
    
    @Test
    void shouldCalculatePremiumForDifferentPowerRanges() {
        // Test all power categories
        testPowerCategory(60, "POWER_LOW");
        testPowerCategory(100, "POWER_MEDIUM");
        testPowerCategory(200, "POWER_HIGH");
        testPowerCategory(350, "POWER_VERY_HIGH");
    }
    
    @Test
    void shouldRoundPremiumToTwoDecimalPlaces() {
        // Given - Factors that result in many decimal places
        Vehicle car = createVehicle(2020, 1600, 120, LocalDate.of(2020, 1, 1));
        
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_5", new BigDecimal("1.1111"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.2222"));
        mockRatingFactor(InsuranceType.OC, "POWER_MEDIUM", new BigDecimal("1.3333"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, car, policyDate);
        
        // Then
        assertThat(premium.scale()).isEqualTo(2);
        // Verify it's properly rounded
        BigDecimal calculated = new BigDecimal("800.00")
                .multiply(new BigDecimal("1.1111"))
                .multiply(new BigDecimal("1.2222"))
                .multiply(new BigDecimal("1.3333"))
                .setScale(2, RoundingMode.HALF_UP);
        assertThat(premium).isEqualTo(calculated);
    }
    
    @Test
    void shouldHandleZeroVehicleAge() {
        // Given - Brand new car (0 years old)
        Vehicle brandNewCar = createVehicle(2025, 1600, 120, LocalDate.now());
        
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_0", new BigDecimal("0.9000"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        // When
        BigDecimal premium = ratingService.calculatePremium(InsuranceType.OC, brandNewCar, policyDate);
        
        // Then
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
            InsuranceType.OC, "VEHICLE_AGE_0", policyDate);
        assertThat(premium).isEqualTo(new BigDecimal("720.00"));
    }
    
    /**
     * Helper method to create a vehicle with specific characteristics.
     * Clean Code: Extracted vehicle creation for test readability.
     */
    private Vehicle createVehicle(int year, int engineCapacity, int power, LocalDate firstRegistration) {
        return Vehicle.builder()
                .make("Test")
                .model("Car")
                .yearOfManufacture(year)
                .registrationNumber("TEST123")
                .vin("TEST1234567890123")
                .engineCapacity(engineCapacity)
                .power(power)
                .firstRegistrationDate(firstRegistration)
                .build();
    }
    
    /**
     * Helper method to mock a specific rating factor.
     * Clean Code: Extracted mocking for test readability.
     */
    private void mockRatingFactor(InsuranceType insuranceType, String ratingKey, BigDecimal multiplier) {
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(insuranceType)
                .ratingKey(ratingKey)
                .multiplier(multiplier)
                .validFrom(LocalDate.now().minusYears(1))
                .build();
        
        when(ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                insuranceType, ratingKey, policyDate))
                .thenReturn(Arrays.asList(ratingTable));
    }
    

    
    /**
     * Helper method to test engine category mapping.
     * Clean Code: Parameterized test helper.
     */
    private void testEngineCategory(int engineCapacity, String expectedKey) {
        Vehicle car = createVehicle(2020, engineCapacity, 120, LocalDate.of(2020, 1, 1));
        
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_5", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, expectedKey, new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "POWER_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        ratingService.calculatePremium(InsuranceType.OC, car, policyDate);
        
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
            InsuranceType.OC, expectedKey, policyDate);
    }
    
    /**
     * Helper method to test power category mapping.
     * Clean Code: Parameterized test helper.
     */
    private void testPowerCategory(int power, String expectedKey) {
        Vehicle car = createVehicle(2020, 1600, power, LocalDate.of(2020, 1, 1));
        
        mockRatingFactor(InsuranceType.OC, "VEHICLE_AGE_5", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "ENGINE_MEDIUM", new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, expectedKey, new BigDecimal("1.0000"));
        mockRatingFactor(InsuranceType.OC, "OC_STANDARD", new BigDecimal("1.0000"));
        
        ratingService.calculatePremium(InsuranceType.OC, car, policyDate);
        
        verify(ratingTableRepository).findByInsuranceTypeAndRatingKeyValidForDate(
            InsuranceType.OC, expectedKey, policyDate);
    }
}