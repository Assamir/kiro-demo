package com.insurance.backoffice.interfaces.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing policy.
 */
public record UpdatePolicyRequest(
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @FutureOrPresent(message = "End date must be today or in the future")
        @NotNull(message = "End date is required")
        LocalDate endDate,

        BigDecimal discountSurcharge
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal discountSurcharge;

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

        public UpdatePolicyRequest build() {
            return new UpdatePolicyRequest(startDate, endDate, discountSurcharge);
        }
    }
}