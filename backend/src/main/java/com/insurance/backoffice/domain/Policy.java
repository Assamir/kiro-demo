package com.insurance.backoffice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entity representing an insurance policy.
 * Central domain object that connects client, vehicle, and policy details.
 */
@Entity
@Table(name = "policies")
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_number", unique = true, nullable = false, length = 50)
    private String policyNumber;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false, length = 10)
    private InsuranceType insuranceType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal premium;
    
    @Column(name = "discount_surcharge", precision = 10, scale = 2)
    private BigDecimal discountSurcharge;
    
    @Column(name = "amount_guaranteed", precision = 12, scale = 2)
    private BigDecimal amountGuaranteed;
    
    @Column(name = "coverage_area", length = 500)
    private String coverageArea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @OneToOne(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PolicyDetails policyDetails;
    
    // Default constructor for JPA
    protected Policy() {}
    
    // Private constructor for Builder pattern
    private Policy(Builder builder) {
        this.id = builder.id;
        this.policyNumber = builder.policyNumber;
        this.issueDate = builder.issueDate;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.insuranceType = builder.insuranceType;
        this.premium = builder.premium;
        this.discountSurcharge = builder.discountSurcharge;
        this.client = builder.client;
        this.vehicle = builder.vehicle;
    }
    
    /**
     * Calculates the total premium including discount/surcharge.
     * Clean Code: Business logic encapsulated in domain object.
     */
    public BigDecimal getTotalPremium() {
        if (discountSurcharge == null) {
            return premium;
        }
        return premium.add(discountSurcharge);
    }
    
    /**
     * Calculates the duration of the policy in days.
     * Clean Code: Intention-revealing method name.
     */
    public int getDurationInDays() {
        return Period.between(startDate, endDate).getDays();
    }
    
    /**
     * Checks if the policy is currently active and within coverage period.
     * Clean Code: Tell, don't ask principle.
     */
    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return PolicyStatus.ACTIVE.equals(status) && 
               !today.isBefore(startDate) && 
               !today.isAfter(endDate);
    }
    
    /**
     * Checks if the policy has expired.
     * Clean Code: Business rule encapsulated in domain object.
     */
    public boolean isExpired() {
        return PolicyStatus.EXPIRED.equals(status) || 
               LocalDate.now().isAfter(endDate);
    }
    
    /**
     * Checks if the policy is canceled.
     * Clean Code: Intention-revealing method name.
     */
    public boolean isCanceled() {
        return PolicyStatus.CANCELED.equals(status);
    }
    
    /**
     * Cancels the policy by setting its status to CANCELED.
     * Clean Code: Domain behavior encapsulated in the entity.
     */
    public void cancel() {
        if (isCanceled()) {
            throw new IllegalStateException("Policy is already canceled");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot cancel an expired policy");
        }
        this.status = PolicyStatus.CANCELED;
    }
    
    /**
     * Activates the policy by setting its status to ACTIVE.
     * Clean Code: State transition logic in domain object.
     */
    public void activate() {
        if (isCurrentlyActive()) {
            throw new IllegalStateException("Policy is already active");
        }
        this.status = PolicyStatus.ACTIVE;
    }
    
    /**
     * Sets the policy details and establishes bidirectional relationship.
     * Clean Code: Encapsulates relationship management.
     */
    public void setPolicyDetails(PolicyDetails policyDetails) {
        this.policyDetails = policyDetails;
        if (policyDetails != null) {
            policyDetails.setPolicy(this);
        }
    }
    
    /**
     * Returns a description combining insurance type and policy number.
     * Clean Code: Meaningful method for display purposes.
     */
    public String getDescription() {
        return insuranceType + " Policy " + policyNumber;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getPolicyNumber() { return policyNumber; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public PolicyStatus getStatus() { return status; }
    public InsuranceType getInsuranceType() { return insuranceType; }
    public BigDecimal getPremium() { return premium; }
    public BigDecimal getDiscountSurcharge() { return discountSurcharge; }
    public BigDecimal getAmountGuaranteed() { return amountGuaranteed; }
    public String getCoverageArea() { return coverageArea; }
    public Client getClient() { return client; }
    public Vehicle getVehicle() { return vehicle; }
    public PolicyDetails getPolicyDetails() { return policyDetails; }
    
    // Setters for mutable fields
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType = insuranceType; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    public void setDiscountSurcharge(BigDecimal discountSurcharge) { this.discountSurcharge = discountSurcharge; }
    public void setAmountGuaranteed(BigDecimal amountGuaranteed) { this.amountGuaranteed = amountGuaranteed; }
    public void setCoverageArea(String coverageArea) { this.coverageArea = coverageArea; }
    public void setClient(Client client) { this.client = client; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return Objects.equals(id, policy.id) && Objects.equals(policyNumber, policy.policyNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, policyNumber);
    }
    
    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", policyNumber='" + policyNumber + '\'' +
                ", insuranceType=" + insuranceType +
                ", status=" + status +
                ", premium=" + premium +
                '}';
    }
    
    /**
     * Builder pattern implementation for clean object creation.
     */
    public static class Builder {
        private Long id;
        private String policyNumber;
        private LocalDate issueDate;
        private LocalDate startDate;
        private LocalDate endDate;
        private PolicyStatus status = PolicyStatus.ACTIVE;
        private InsuranceType insuranceType;
        private BigDecimal premium;
        private BigDecimal discountSurcharge;
        private Client client;
        private Vehicle vehicle;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder policyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
            return this;
        }
        
        public Builder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
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
        
        public Builder status(PolicyStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder insuranceType(InsuranceType insuranceType) {
            this.insuranceType = insuranceType;
            return this;
        }
        
        public Builder premium(BigDecimal premium) {
            this.premium = premium;
            return this;
        }
        
        public Builder discountSurcharge(BigDecimal discountSurcharge) {
            this.discountSurcharge = discountSurcharge;
            return this;
        }
        
        public Builder client(Client client) {
            this.client = client;
            return this;
        }
        
        public Builder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }
        
        public Policy build() {
            validateRequiredFields();
            validateBusinessRules();
            return new Policy(this);
        }
        
        private void validateRequiredFields() {
            if (policyNumber == null || policyNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Policy number is required");
            }
            if (issueDate == null) {
                throw new IllegalArgumentException("Issue date is required");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("Start date is required");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("End date is required");
            }
            if (insuranceType == null) {
                throw new IllegalArgumentException("Insurance type is required");
            }
            if (premium == null) {
                throw new IllegalArgumentException("Premium is required");
            }
            if (client == null) {
                throw new IllegalArgumentException("Client is required");
            }
            if (vehicle == null) {
                throw new IllegalArgumentException("Vehicle is required");
            }
        }
        
        private void validateBusinessRules() {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
            if (premium.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Premium cannot be negative");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}