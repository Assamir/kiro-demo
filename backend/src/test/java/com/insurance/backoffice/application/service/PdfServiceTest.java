package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for PdfService.
 * Clean Code: Comprehensive testing with descriptive test names and AAA pattern.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PdfService Tests")
class PdfServiceTest {
    
    private PdfService pdfService;
    private Policy testPolicy;
    private Client testClient;
    private Vehicle testVehicle;
    private PolicyDetails testPolicyDetails;
    
    @BeforeEach
    void setUp() {
        pdfService = new PdfService();
        setupTestData();
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for OC policy")
    void shouldGeneratePdfSuccessfullyForOCPolicy() {
        // Given
        testPolicy.setInsuranceType(InsuranceType.OC);
        testPolicyDetails = PolicyDetails.builder()
                .policy(testPolicy)
                .guaranteedSum(new BigDecimal("1000000.00"))
                .coverageArea("Europe")
                .build();
        testPolicy.setPolicyDetails(testPolicyDetails);
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        // PDF should start with PDF header
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for AC policy")
    void shouldGeneratePdfSuccessfullyForACPolicy() {
        // Given
        testPolicy.setInsuranceType(InsuranceType.AC);
        testPolicyDetails = PolicyDetails.builder()
                .policy(testPolicy)
                .acVariant(ACVariant.STANDARD)
                .sumInsured(new BigDecimal("50000.00"))
                .coverageScope("Comprehensive coverage")
                .deductible(new BigDecimal("500.00"))
                .workshopType("Authorized")
                .build();
        testPolicy.setPolicyDetails(testPolicyDetails);
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for NNW policy")
    void shouldGeneratePdfSuccessfullyForNNWPolicy() {
        // Given
        testPolicy.setInsuranceType(InsuranceType.NNW);
        testPolicyDetails = PolicyDetails.builder()
                .policy(testPolicy)
                .sumInsured(new BigDecimal("100000.00"))
                .coveredPersons("Driver and passengers")
                .build();
        testPolicy.setPolicyDetails(testPolicyDetails);
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for policy without details")
    void shouldGeneratePdfSuccessfullyForPolicyWithoutDetails() {
        // Given
        testPolicy.setPolicyDetails(null);
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for policy with discount")
    void shouldGeneratePdfSuccessfullyForPolicyWithDiscount() {
        // Given
        testPolicy.setDiscountSurcharge(new BigDecimal("-100.00")); // Discount
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF successfully for policy with surcharge")
    void shouldGeneratePdfSuccessfullyForPolicyWithSurcharge() {
        // Given
        testPolicy.setDiscountSurcharge(new BigDecimal("150.00")); // Surcharge
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should throw exception when policy is null")
    void shouldThrowExceptionWhenPolicyIsNull() {
        // Given
        Policy nullPolicy = null;
        
        // When & Then
        assertThatThrownBy(() -> pdfService.generatePolicyPdf(nullPolicy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy cannot be null");
    }
    
    @Test
    @DisplayName("Should throw exception when policy has no client")
    void shouldThrowExceptionWhenPolicyHasNoClient() {
        // Given
        testPolicy.setClient(null);
        
        // When & Then
        assertThatThrownBy(() -> pdfService.generatePolicyPdf(testPolicy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy must have a client");
    }
    
    @Test
    @DisplayName("Should throw exception when policy has no vehicle")
    void shouldThrowExceptionWhenPolicyHasNoVehicle() {
        // Given
        testPolicy.setVehicle(null);
        
        // When & Then
        assertThatThrownBy(() -> pdfService.generatePolicyPdf(testPolicy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy must have a vehicle");
    }
    
    @Test
    @DisplayName("Should throw exception when policy has no policy number")
    void shouldThrowExceptionWhenPolicyHasNoPolicyNumber() {
        // Given
        testPolicy.setPolicyNumber(null);
        
        // When & Then
        assertThatThrownBy(() -> pdfService.generatePolicyPdf(testPolicy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy must have a policy number");
    }
    
    @Test
    @DisplayName("Should throw exception when policy has empty policy number")
    void shouldThrowExceptionWhenPolicyHasEmptyPolicyNumber() {
        // Given
        testPolicy.setPolicyNumber("   ");
        
        // When & Then
        assertThatThrownBy(() -> pdfService.generatePolicyPdf(testPolicy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Policy must have a policy number");
    }
    
    @Test
    @DisplayName("Should handle policy with null values gracefully")
    void shouldHandlePolicyWithNullValuesGracefully() {
        // Given
        testPolicy.setDiscountSurcharge(null);
        testClient.setEmail(null);
        testClient.setPhoneNumber(null);
        
        // When
        byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
        
        // Then
        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
    }
    
    @Test
    @DisplayName("Should generate PDF with all insurance types")
    void shouldGeneratePdfWithAllInsuranceTypes() {
        // Test all insurance types to ensure comprehensive coverage
        for (InsuranceType insuranceType : InsuranceType.values()) {
            // Given
            testPolicy.setInsuranceType(insuranceType);
            setupPolicyDetailsForInsuranceType(insuranceType);
            
            // When
            byte[] pdfBytes = pdfService.generatePolicyPdf(testPolicy);
            
            // Then
            assertThat(pdfBytes).isNotNull();
            assertThat(pdfBytes.length).isGreaterThan(0);
            assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
        }
    }
    
    /**
     * Sets up test data for policy, client, and vehicle.
     * Clean Code: Extracted setup method for test data preparation.
     */
    private void setupTestData() {
        testClient = Client.builder()
                .id(1L)
                .fullName("John Doe")
                .pesel("12345678901")
                .address("123 Main Street, Warsaw, Poland")
                .email("john.doe@example.com")
                .phoneNumber("+48 123 456 789")
                .build();
        
        testVehicle = Vehicle.builder()
                .id(1L)
                .make("Toyota")
                .model("Corolla")
                .yearOfManufacture(2020)
                .registrationNumber("WA12345")
                .vin("1HGBH41JXMN109186")
                .engineCapacity(1600)
                .power(132)
                .firstRegistrationDate(LocalDate.of(2020, 3, 15))
                .build();
        
        testPolicy = Policy.builder()
                .id(1L)
                .policyNumber("POL-2024-001")
                .issueDate(LocalDate.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.ACTIVE)
                .insuranceType(InsuranceType.OC)
                .premium(new BigDecimal("1200.00"))
                .discountSurcharge(BigDecimal.ZERO)
                .client(testClient)
                .vehicle(testVehicle)
                .build();
    }
    
    /**
     * Sets up policy details based on insurance type for comprehensive testing.
     * Clean Code: Helper method for test data setup.
     */
    private void setupPolicyDetailsForInsuranceType(InsuranceType insuranceType) {
        PolicyDetails.Builder builder = PolicyDetails.builder().policy(testPolicy);
        
        switch (insuranceType) {
            case OC:
                testPolicyDetails = builder
                        .guaranteedSum(new BigDecimal("1000000.00"))
                        .coverageArea("Europe")
                        .build();
                break;
            case AC:
                testPolicyDetails = builder
                        .acVariant(ACVariant.STANDARD)
                        .sumInsured(new BigDecimal("50000.00"))
                        .coverageScope("Comprehensive coverage")
                        .deductible(new BigDecimal("500.00"))
                        .workshopType("Authorized")
                        .build();
                break;
            case NNW:
                testPolicyDetails = builder
                        .sumInsured(new BigDecimal("100000.00"))
                        .coveredPersons("Driver and passengers")
                        .build();
                break;
        }
        
        testPolicy.setPolicyDetails(testPolicyDetails);
    }
}