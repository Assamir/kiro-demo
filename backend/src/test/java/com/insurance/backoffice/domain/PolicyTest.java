package com.insurance.backoffice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Policy entity following clean code testing principles.
 * Tests the Builder pattern implementation and business logic methods.
 */
class PolicyTest {
    
    @Test
    void shouldCreatePolicyWithBuilder() {
        // Given
        Client client = createTestClient();
        Vehicle vehicle = createTestVehicle();
        
        // When
        Policy policy = Policy.builder()
                .policyNumber("POL-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1200.00))
                .client(client)
                .vehicle(vehicle)
                .build();
        
        // Then
        assertThat(policy.getPolicyNumber()).isEqualTo("POL-001");
        assertThat(policy.getInsuranceType()).isEqualTo(InsuranceType.OC);
        assertThat(policy.getPremium()).isEqualTo(BigDecimal.valueOf(1200.00));
        assertThat(policy.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
    }
    
    @Test
    void shouldCalculateTotalPremiumWithoutDiscount() {
        // Given
        Policy policy = createTestPolicy()
                .premium(BigDecimal.valueOf(1000.00))
                .build();
        
        // When
        BigDecimal totalPremium = policy.getTotalPremium();
        
        // Then
        assertThat(totalPremium).isEqualTo(BigDecimal.valueOf(1000.00));
    }
    
    @Test
    void shouldCalculateTotalPremiumWithDiscount() {
        // Given
        Policy policy = createTestPolicy()
                .premium(BigDecimal.valueOf(1000.00))
                .discountSurcharge(BigDecimal.valueOf(-100.00))
                .build();
        
        // When
        BigDecimal totalPremium = policy.getTotalPremium();
        
        // Then
        assertThat(totalPremium).isEqualTo(BigDecimal.valueOf(900.00));
    }
    
    @Test
    void shouldCalculateTotalPremiumWithSurcharge() {
        // Given
        Policy policy = createTestPolicy()
                .premium(BigDecimal.valueOf(1000.00))
                .discountSurcharge(BigDecimal.valueOf(200.00))
                .build();
        
        // When
        BigDecimal totalPremium = policy.getTotalPremium();
        
        // Then
        assertThat(totalPremium).isEqualTo(BigDecimal.valueOf(1200.00));
    }
    
    @Test
    void shouldReturnTrueForCurrentlyActivePolicy() {
        // Given
        Policy policy = createTestPolicy()
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .status(PolicyStatus.ACTIVE)
                .build();
        
        // When & Then
        assertThat(policy.isCurrentlyActive()).isTrue();
        assertThat(policy.isExpired()).isFalse();
        assertThat(policy.isCanceled()).isFalse();
    }
    
    @Test
    void shouldReturnFalseForExpiredPolicy() {
        // Given
        Policy policy = createTestPolicy()
                .startDate(LocalDate.now().minusYears(2))
                .endDate(LocalDate.now().minusDays(1))
                .status(PolicyStatus.EXPIRED)
                .build();
        
        // When & Then
        assertThat(policy.isCurrentlyActive()).isFalse();
        assertThat(policy.isExpired()).isTrue();
    }
    
    @Test
    void shouldCancelActivePolicy() {
        // Given
        Policy policy = createTestPolicy()
                .status(PolicyStatus.ACTIVE)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();
        
        // When
        policy.cancel();
        
        // Then
        assertThat(policy.getStatus()).isEqualTo(PolicyStatus.CANCELED);
        assertThat(policy.isCanceled()).isTrue();
    }
    
    @Test
    void shouldThrowExceptionWhenCancelingAlreadyCanceledPolicy() {
        // Given
        Policy policy = createTestPolicy()
                .status(PolicyStatus.CANCELED)
                .build();
        
        // When & Then
        assertThatThrownBy(policy::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Policy is already canceled");
    }
    
    @Test
    void shouldThrowExceptionWhenStartDateAfterEndDate() {
        // When & Then
        assertThatThrownBy(() -> createTestPolicy()
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start date must be before end date");
    }
    
    @Test
    void shouldThrowExceptionWhenNegativePremium() {
        // When & Then
        assertThatThrownBy(() -> createTestPolicy()
                .premium(BigDecimal.valueOf(-100.00))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Premium cannot be negative");
    }
    
    private Policy.Builder createTestPolicy() {
        return Policy.builder()
                .policyNumber("POL-TEST")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .insuranceType(InsuranceType.OC)
                .premium(BigDecimal.valueOf(1000.00))
                .client(createTestClient())
                .vehicle(createTestVehicle());
    }
    
    private Client createTestClient() {
        return Client.builder()
                .fullName("John Doe")
                .pesel("12345678901")
                .address("Test Address")
                .email("john@example.com")
                .phoneNumber("123456789")
                .build();
    }
    
    private Vehicle createTestVehicle() {
        return Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("ABC123")
                .vin("12345678901234567")
                .engineCapacity(1600)
                .power(100)
                .firstRegistrationDate(LocalDate.of(2020, 1, 1))
                .build();
    }
}