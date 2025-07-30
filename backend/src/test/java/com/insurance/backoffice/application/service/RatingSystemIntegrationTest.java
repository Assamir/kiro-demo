package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.RatingTableRepository;
import com.insurance.backoffice.testdata.RatingTableTestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the rating system with real database.
 * Tests the complete rating system functionality including seed data.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RatingSystemIntegrationTest {
    
    @Autowired
    private RatingService ratingService;
    
    @Autowired
    private RatingValidationService ratingValidationService;
    
    @Autowired
    private RatingTableRepository ratingTableRepository;
    
    @Autowired
    private RatingTableTestDataSetup testDataSetup;
    
    @BeforeEach
    void setUp() {
        // Set up rating table test data
        testDataSetup.setupRatingTableData();
    }
    
    @Test
    void shouldLoadRatingTableSeedData() {
        // When
        List<RatingTable> ocTables = ratingTableRepository.findByInsuranceType(InsuranceType.OC);
        List<RatingTable> acTables = ratingTableRepository.findByInsuranceType(InsuranceType.AC);
        List<RatingTable> nnwTables = ratingTableRepository.findByInsuranceType(InsuranceType.NNW);
        
        // Then
        assertThat(ocTables).isNotEmpty();
        assertThat(acTables).isNotEmpty();
        assertThat(nnwTables).isNotEmpty();
        
        // Verify specific rating factors exist
        assertThat(ocTables).anyMatch(rt -> rt.getRatingKey().equals("VEHICLE_AGE_0"));
        assertThat(ocTables).anyMatch(rt -> rt.getRatingKey().equals("ENGINE_SMALL"));
        assertThat(ocTables).anyMatch(rt -> rt.getRatingKey().equals("POWER_LOW"));
    }
    
    @Test
    void shouldCalculatePremiumWithRealData() {
        // Given - Create a test vehicle
        Vehicle testVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Camry")
                .yearOfManufacture(2020)
                .registrationNumber("TEST123")
                .vin("TEST1234567890123")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
        
        LocalDate policyDate = LocalDate.now();
        
        // When
        BigDecimal ocPremium = ratingService.calculatePremium(InsuranceType.OC, testVehicle, policyDate);
        BigDecimal acPremium = ratingService.calculatePremium(InsuranceType.AC, testVehicle, policyDate);
        BigDecimal nnwPremium = ratingService.calculatePremium(InsuranceType.NNW, testVehicle, policyDate);
        
        // Then
        assertThat(ocPremium).isGreaterThan(BigDecimal.ZERO);
        assertThat(acPremium).isGreaterThan(BigDecimal.ZERO);
        assertThat(nnwPremium).isGreaterThan(BigDecimal.ZERO);
        
        // AC should generally be more expensive than OC
        assertThat(acPremium).isGreaterThan(ocPremium);
        
        // NNW should be the least expensive
        assertThat(nnwPremium).isLessThan(ocPremium);
        assertThat(nnwPremium).isLessThan(acPremium);
    }
    
    @Test
    void shouldValidateVehicleSuccessfully() {
        // Given
        Vehicle validVehicle = Vehicle.builder()
                .make("Honda")
                .model("Civic")
                .yearOfManufacture(2021)
                .registrationNumber("VALID123")
                .vin("VALID1234567890123")
                .engineCapacity(1500)
                .power(110)
                .firstRegistrationDate(LocalDate.of(2021, 1, 1))
                .build();
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.OC, validVehicle, LocalDate.now());
        
        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }
    
    @Test
    void shouldCalculatePremiumBreakdownWithRealData() {
        // Given
        Vehicle testVehicle = Vehicle.builder()
                .make("BMW")
                .model("320i")
                .yearOfManufacture(2019)
                .registrationNumber("BMW123")
                .vin("BMW1234567890123")
                .engineCapacity(2000)
                .power(180)
                .firstRegistrationDate(LocalDate.of(2019, 1, 1))
                .build();
        
        // When
        RatingService.PremiumBreakdown breakdown = 
            ratingService.calculatePremiumBreakdown(InsuranceType.OC, testVehicle, LocalDate.now());
        
        // Then
        assertThat(breakdown).isNotNull();
        assertThat(breakdown.getBasePremium()).isEqualTo(new BigDecimal("800.00"));
        assertThat(breakdown.getRatingFactors()).isNotEmpty();
        assertThat(breakdown.getFinalPremium()).isGreaterThan(breakdown.getBasePremium());
        
        // Should have vehicle age, engine, and power factors
        assertThat(breakdown.getRatingFactors()).containsKey("VEHICLE_AGE");
        assertThat(breakdown.getRatingFactors()).containsKey("ENGINE_CAPACITY");
        assertThat(breakdown.getRatingFactors()).containsKey("POWER");
    }
    
    @Test
    void shouldFindCurrentlyValidRatingTables() {
        // When
        List<RatingTable> currentOCTables = ratingService.getCurrentRatingTables(InsuranceType.OC);
        
        // Then
        assertThat(currentOCTables).isNotEmpty();
        assertThat(currentOCTables).allMatch(RatingTable::isCurrentlyValid);
    }
    
    @Test
    void shouldHandleDifferentVehicleAges() {
        // Test vehicles of different ages
        LocalDate policyDate = LocalDate.now();
        Vehicle newVehicle = createTestVehicle(2024, policyDate.minusYears(1)); // 1 year old
        Vehicle oldVehicle = createTestVehicle(2019, policyDate.minusYears(6)); // 6 years old
        Vehicle veryOldVehicle = createTestVehicle(2014, policyDate.minusYears(11)); // 11 years old (capped at 10)
        
        // When
        BigDecimal newPremium = ratingService.calculatePremium(InsuranceType.OC, newVehicle, policyDate);
        BigDecimal oldPremium = ratingService.calculatePremium(InsuranceType.OC, oldVehicle, policyDate);
        BigDecimal veryOldPremium = ratingService.calculatePremium(InsuranceType.OC, veryOldVehicle, policyDate);
        
        // Then - Older vehicles should generally have higher premiums for OC
        assertThat(oldPremium).isGreaterThan(newPremium);
        assertThat(veryOldPremium).isGreaterThan(oldPremium);
    }
    
    @Test
    void shouldHandleDifferentEngineSizes() {
        // Test vehicles with different engine sizes
        Vehicle smallEngine = createTestVehicleWithEngine(1000);
        Vehicle mediumEngine = createTestVehicleWithEngine(1600);
        Vehicle largeEngine = createTestVehicleWithEngine(2500);
        
        LocalDate policyDate = LocalDate.now();
        
        // When
        BigDecimal smallPremium = ratingService.calculatePremium(InsuranceType.OC, smallEngine, policyDate);
        BigDecimal mediumPremium = ratingService.calculatePremium(InsuranceType.OC, mediumEngine, policyDate);
        BigDecimal largePremium = ratingService.calculatePremium(InsuranceType.OC, largeEngine, policyDate);
        
        // Then - Larger engines should have higher premiums
        assertThat(mediumPremium).isGreaterThan(smallPremium);
        assertThat(largePremium).isGreaterThan(mediumPremium);
    }
    
    @Test
    void shouldValidateBusinessRulesForACInsurance() {
        // Given - Very old vehicle (should fail AC validation)
        Vehicle veryOldVehicle = createTestVehicle(2000, LocalDate.of(2000, 1, 1));
        
        // When
        RatingValidationService.RatingValidationResult result = 
            ratingValidationService.validateRatingFactors(InsuranceType.AC, veryOldVehicle, LocalDate.now());
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("AC insurance is not available"));
    }
    
    /**
     * Helper method to create a test vehicle with specific year and registration date.
     */
    private Vehicle createTestVehicle(int year, LocalDate registrationDate) {
        return Vehicle.builder()
                .make("Test")
                .model("Vehicle")
                .yearOfManufacture(year)
                .registrationNumber("TEST" + year)
                .vin("TEST" + year + "1234567890")
                .engineCapacity(1600)
                .power(120)
                .firstRegistrationDate(registrationDate)
                .build();
    }
    
    /**
     * Helper method to create a test vehicle with specific engine capacity.
     */
    private Vehicle createTestVehicleWithEngine(int engineCapacity) {
        return Vehicle.builder()
                .make("Test")
                .model("Engine" + engineCapacity)
                .yearOfManufacture(2020)
                .registrationNumber("ENG" + engineCapacity)
                .vin("ENG" + engineCapacity + "1234567890")
                .engineCapacity(engineCapacity)
                .power(120)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
    }
}