package com.insurance.backoffice.interfaces.dto;

import com.insurance.backoffice.domain.Policy;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for policy information.
 */
public record PolicyResponse(
        Long id,
        String policyNumber,
        String clientName,
        String vehicleRegistration,
        String insuranceType,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal premium,
        BigDecimal discountSurcharge,
        String status
) {
    public static PolicyResponse fromPolicy(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getClient().getFullName(),
                policy.getVehicle().getRegistrationNumber(),
                policy.getInsuranceType().name(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getPremium(),
                policy.getDiscountSurcharge(),
                policy.getStatus().name()
        );
    }
}