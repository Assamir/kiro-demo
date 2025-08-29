package com.insurance.backoffice.interfaces.dto;

import com.insurance.backoffice.domain.ACVariant;
import com.insurance.backoffice.domain.InsuranceType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new policy.
 */
public record CreatePolicyRequest(
        @NotNull(message = "Client ID is required")
        Long clientId,

        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,

        @NotNull(message = "Insurance type is required")
        InsuranceType insuranceType,

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDate endDate,

        // OC Insurance fields
        @Positive(message = "Guaranteed sum must be positive")
        BigDecimal guaranteedSum,

        String coverageArea,

        // AC Insurance fields
        ACVariant acVariant,

        @Positive(message = "Sum insured must be positive")
        BigDecimal sumInsured,

        String coverageScope,

        @Positive(message = "Deductible must be positive")
        BigDecimal deductible,

        String workshopType,

        // NNW Insurance fields
        String coveredPersons,

        // Common fields
        BigDecimal discountSurcharge
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long clientId;
        private Long vehicleId;
        private InsuranceType insuranceType;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal guaranteedSum;
        private String coverageArea;
        private ACVariant acVariant;
        private BigDecimal sumInsured;
        private String coverageScope;
        private BigDecimal deductible;
        private String workshopType;
        private String coveredPersons;
        private BigDecimal discountSurcharge;

        public Builder clientId(Long clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder vehicleId(Long vehicleId) {
            this.vehicleId = vehicleId;
            return this;
        }

        public Builder insuranceType(InsuranceType insuranceType) {
            this.insuranceType = insuranceType;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder guaranteedSum(BigDecimal guaranteedSum) {
            this.guaranteedSum = guaranteedSum;
            return this;
        }

        public Builder coverageArea(String coverageArea) {
            this.coverageArea = coverageArea;
            return this;
        }

        public Builder acVariant(ACVariant acVariant) {
            this.acVariant = acVariant;
            return this;
        }

        public Builder sumInsured(BigDecimal sumInsured) {
            this.sumInsured = sumInsured;
            return this;
        }

        public Builder coverageScope(String coverageScope) {
            this.coverageScope = coverageScope;
            return this;
        }

        public Builder deductible(BigDecimal deductible) {
            this.deductible = deductible;
            return this;
        }

        public Builder workshopType(String workshopType) {
            this.workshopType = workshopType;
            return this;
        }

        public Builder coveredPersons(String coveredPersons) {
            this.coveredPersons = coveredPersons;
            return this;
        }

        public Builder discountSurcharge(BigDecimal discountSurcharge) {
            this.discountSurcharge = discountSurcharge;
            return this;
        }

        public CreatePolicyRequest build() {
            return new CreatePolicyRequest(
                    clientId, vehicleId, insuranceType, startDate, endDate,
                    guaranteedSum, coverageArea, acVariant, sumInsured,
                    coverageScope, deductible, workshopType, coveredPersons,
                    discountSurcharge
            );
        }
    }
}