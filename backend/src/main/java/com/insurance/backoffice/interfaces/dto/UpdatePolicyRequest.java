package com.insurance.backoffice.interfaces.dto;

import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing policy.
 */
public record UpdatePolicyRequest(
        @Future(message = "Start date must be in the future")
        LocalDate startDate,

        @Future(message = "End date must be in the future")
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