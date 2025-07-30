package com.insurance.backoffice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing rating table entries for premium calculations.
 * Contains multipliers and factors used in insurance premium calculations.
 */
@Entity
@Table(name = "rating_tables")
public class RatingTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false, length = 10)
    private InsuranceType insuranceType;
    
    @Column(name = "rating_key", nullable = false, length = 100)
    private String ratingKey;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal multiplier;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    // Default constructor for JPA and testing
    public RatingTable() {}
    
    // Private constructor for Builder pattern
    private RatingTable(Builder builder) {
        this.insuranceType = builder.insuranceType;
        this.ratingKey = builder.ratingKey;
        this.multiplier = builder.multiplier;
        this.validFrom = builder.validFrom;
        this.validTo = builder.validTo;
    }
    
    /**
     * Checks if this rating table entry is valid for the given date.
     * Clean Code: Business logic encapsulated in domain object.
     */
    public boolean isValidForDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        
        boolean afterValidFrom = !date.isBefore(validFrom);
        boolean beforeValidTo = validTo == null || !date.isAfter(validTo);
        
        return afterValidFrom && beforeValidTo;
    }
    
    /**
     * Checks if this rating table entry is currently valid.
     * Clean Code: Intention-revealing method name.
     */
    public boolean isCurrentlyValid() {
        return isValidForDate(LocalDate.now());
    }
    
    /**
     * Checks if this rating table entry has expired.
     * Clean Code: Tell, don't ask principle.
     */
    public boolean isExpired() {
        return validTo != null && LocalDate.now().isAfter(validTo);
    }
    
    /**
     * Checks if this rating table entry is not yet effective.
     * Clean Code: Business rule encapsulated in domain object.
     */
    public boolean isFutureEffective() {
        return LocalDate.now().isBefore(validFrom);
    }
    
    /**
     * Returns a description of this rating table entry.
     * Clean Code: Meaningful method for display purposes.
     */
    public String getDescription() {
        return insuranceType + " - " + ratingKey + " (x" + multiplier + ")";
    }
    
    /**
     * Checks if this rating table entry applies to the given insurance type.
     * Clean Code: Encapsulates type checking logic.
     */
    public boolean appliesTo(InsuranceType type) {
        return insuranceType.equals(type);
    }
    
    // Getters
    public Long getId() { return id; }
    public InsuranceType getInsuranceType() { return insuranceType; }
    public String getRatingKey() { return ratingKey; }
    public BigDecimal getMultiplier() { return multiplier; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    
    // Setters for mutable fields
    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType = insuranceType; }
    public void setRatingKey(String ratingKey) { this.ratingKey = ratingKey; }
    public void setMultiplier(BigDecimal multiplier) { this.multiplier = multiplier; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingTable that = (RatingTable) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(insuranceType, that.insuranceType) &&
               Objects.equals(ratingKey, that.ratingKey) &&
               Objects.equals(validFrom, that.validFrom);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, insuranceType, ratingKey, validFrom);
    }
    
    @Override
    public String toString() {
        return "RatingTable{" +
                "id=" + id +
                ", insuranceType=" + insuranceType +
                ", ratingKey='" + ratingKey + '\'' +
                ", multiplier=" + multiplier +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
    
    /**
     * Builder pattern implementation for clean object creation.
     */
    public static class Builder {
        private InsuranceType insuranceType;
        private String ratingKey;
        private BigDecimal multiplier;
        private LocalDate validFrom;
        private LocalDate validTo;
        
        public Builder insuranceType(InsuranceType insuranceType) {
            this.insuranceType = insuranceType;
            return this;
        }
        
        public Builder ratingKey(String ratingKey) {
            this.ratingKey = ratingKey;
            return this;
        }
        
        public Builder multiplier(BigDecimal multiplier) {
            this.multiplier = multiplier;
            return this;
        }
        
        public Builder validFrom(LocalDate validFrom) {
            this.validFrom = validFrom;
            return this;
        }
        
        public Builder validTo(LocalDate validTo) {
            this.validTo = validTo;
            return this;
        }
        
        public RatingTable build() {
            validateRequiredFields();
            validateBusinessRules();
            return new RatingTable(this);
        }
        
        private void validateRequiredFields() {
            if (insuranceType == null) {
                throw new IllegalArgumentException("Insurance type is required");
            }
            if (ratingKey == null || ratingKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Rating key is required");
            }
            if (multiplier == null) {
                throw new IllegalArgumentException("Multiplier is required");
            }
            if (validFrom == null) {
                throw new IllegalArgumentException("Valid from date is required");
            }
        }
        
        private void validateBusinessRules() {
            if (multiplier.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Multiplier must be positive");
            }
            if (validTo != null && validFrom.isAfter(validTo)) {
                throw new IllegalArgumentException("Valid from date must be before valid to date");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}