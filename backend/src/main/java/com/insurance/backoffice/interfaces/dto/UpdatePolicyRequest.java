package com.insurance.backoffice.interfaces.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing policy.
 */
public record UpdatePolicyRequest(
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        BigDecimal discountSurcharge,
        
        // Additional fields for policy coverage details
        BigDecimal amountGuaranteed,
        String coverageArea
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal discountSurcharge;
        private BigDecimal amountGuaranteed;
        private String coverageArea;

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder discountSurcharge(BigDecimal discountSurcharge) {
            this.discountSurcharge = discountSurcharge;
            return this;
        }

        public Builder amountGuaranteed(BigDecimal amountGuaranteed) {
            this.amountGuaranteed = amountGuaranteed;
            return this;
        }

        public Builder coverageArea(String coverageArea) {
            this.coverageArea = coverageArea;
            return this;
        }

        public UpdatePolicyRequest build() {
            return new UpdatePolicyRequest(startDate, endDate, discountSurcharge, amountGuaranteed, coverageArea);
        }
    }
}