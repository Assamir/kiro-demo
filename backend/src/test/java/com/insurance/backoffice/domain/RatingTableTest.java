package com.insurance.backoffice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for RatingTable entity following clean code testing principles.
 * Tests the Builder pattern implementation and date validation logic.
 */
class RatingTableTest {
    
    @Test
    void shouldCreateRatingTableWithBuilder() {
        // Given
        InsuranceType insuranceType = InsuranceType.OC;
        String ratingKey = "driver_age_25_30";
        BigDecimal multiplier = BigDecimal.valueOf(1.2000);
        LocalDate validFrom = LocalDate.now();
        
        // When
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(insuranceType)
                .ratingKey(ratingKey)
                .multiplier(multiplier)
                .validFrom(validFrom)
                .build();
        
        // Then
        assertThat(ratingTable.getInsuranceType()).isEqualTo(insuranceType);
        assertThat(ratingTable.getRatingKey()).isEqualTo(ratingKey);
        assertThat(ratingTable.getMultiplier()).isEqualTo(multiplier);
        assertThat(ratingTable.getValidFrom()).isEqualTo(validFrom);
    }
    
    @Test
    void shouldReturnTrueForValidDate() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now().minusDays(1))
                .validTo(LocalDate.now().plusDays(1))
                .build();
        
        // When & Then
        assertThat(ratingTable.isValidForDate(LocalDate.now())).isTrue();
    }
    
    @Test
    void shouldReturnFalseForDateBeforeValidFrom() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now())
                .build();
        
        // When & Then
        assertThat(ratingTable.isValidForDate(LocalDate.now().minusDays(1))).isFalse();
    }
    
    @Test
    void shouldReturnFalseForDateAfterValidTo() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now().minusDays(10))
                .validTo(LocalDate.now().minusDays(1))
                .build();
        
        // When & Then
        assertThat(ratingTable.isValidForDate(LocalDate.now())).isFalse();
        assertThat(ratingTable.isExpired()).isTrue();
    }
    
    @Test
    void shouldReturnTrueForCurrentlyValidWithNoEndDate() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now().minusDays(1))
                .build(); // No validTo date
        
        // When & Then
        assertThat(ratingTable.isCurrentlyValid()).isTrue();
        assertThat(ratingTable.isExpired()).isFalse();
    }
    
    @Test
    void shouldReturnTrueForFutureEffective() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now().plusDays(1))
                .build();
        
        // When & Then
        assertThat(ratingTable.isFutureEffective()).isTrue();
        assertThat(ratingTable.isCurrentlyValid()).isFalse();
    }
    
    @Test
    void shouldReturnTrueForAppliesTo() {
        // Given
        RatingTable ratingTable = RatingTable.builder()
                .insuranceType(InsuranceType.AC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now())
                .build();
        
        // When & Then
        assertThat(ratingTable.appliesTo(InsuranceType.AC)).isTrue();
        assertThat(ratingTable.appliesTo(InsuranceType.OC)).isFalse();
    }
    
    @Test
    void shouldThrowExceptionWhenMultiplierIsZero() {
        // When & Then
        assertThatThrownBy(() -> RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.ZERO)
                .validFrom(LocalDate.now())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multiplier must be positive");
    }
    
    @Test
    void shouldThrowExceptionWhenValidFromAfterValidTo() {
        // When & Then
        assertThatThrownBy(() -> RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("test_key")
                .multiplier(BigDecimal.valueOf(1.0000))
                .validFrom(LocalDate.now().plusDays(10))
                .validTo(LocalDate.now())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valid from date must be before valid to date");
    }
}