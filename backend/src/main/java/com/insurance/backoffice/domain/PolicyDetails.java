package com.insurance.backoffice.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity representing insurance type specific details for a policy.
 * Contains fields that vary based on the type of insurance (OC, AC, NNW).
 */
@Entity
@Table(name = "policy_details")
public class PolicyDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    // OC Insurance fields
    @Column(name = "guaranteed_sum", precision = 12, scale = 2)
    private BigDecimal guaranteedSum;
    
    @Column(name = "coverage_area", length = 100)
    private String coverageArea;
    
    // AC Insurance fields
    @Enumerated(EnumType.STRING)
    @Column(name = "ac_variant", length = 20)
    private ACVariant acVariant;
    
    @Column(name = "sum_insured", precision = 12, scale = 2)
    private BigDecimal sumInsured;
    
    @Column(name = "coverage_scope", columnDefinition = "TEXT")
    private String coverageScope;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal deductible;
    
    @Column(name = "workshop_type", length = 50)
    private String workshopType;
    
    // NNW Insurance fields
    @Column(name = "covered_persons", columnDefinition = "TEXT")
    private String coveredPersons;
    
    // Default constructor for JPA
    protected PolicyDetails() {}
    
    // Private constructor for Builder pattern
    private PolicyDetails(Builder builder) {
        this.policy = builder.policy;
        this.guaranteedSum = builder.guaranteedSum;
        this.coverageArea = builder.coverageArea;
        this.acVariant = builder.acVariant;
        this.sumInsured = builder.sumInsured;
        this.coverageScope = builder.coverageScope;
        this.deductible = builder.deductible;
        this.workshopType = builder.workshopType;
        this.coveredPersons = builder.coveredPersons;
    }
    
    /**
     * Checks if this policy details is for OC insurance.
     * Clean Code: Intention-revealing method name.
     */
    public boolean isOCInsurance() {
        return policy != null && InsuranceType.OC.equals(policy.getInsuranceType());
    }
    
    /**
     * Checks if this policy details is for AC insurance.
     * Clean Code: Business logic encapsulated in domain object.
     */
    public boolean isACInsurance() {
        return policy != null && InsuranceType.AC.equals(policy.getInsuranceType());
    }
    
    /**
     * Checks if this policy details is for NNW insurance.
     * Clean Code: Tell, don't ask principle.
     */
    public boolean isNNWInsurance() {
        return policy != null && InsuranceType.NNW.equals(policy.getInsuranceType());
    }
    
    /**
     * Returns the appropriate sum for the insurance type.
     * Clean Code: Encapsulates business logic for different insurance types.
     */
    public BigDecimal getApplicableSum() {
        if (isOCInsurance()) {
            return guaranteedSum;
        } else if (isACInsurance() || isNNWInsurance()) {
            return sumInsured;
        }
        return null;
    }
    
    /**
     * Validates that the policy details are appropriate for the insurance type.
     * Clean Code: Domain validation encapsulated in the entity.
     */
    public boolean isValidForInsuranceType() {
        if (policy == null) {
            return false;
        }
        
        switch (policy.getInsuranceType()) {
            case OC:
                return guaranteedSum != null && coverageArea != null;
            case AC:
                return acVariant != null && sumInsured != null && coverageScope != null;
            case NNW:
                return sumInsured != null && coveredPersons != null;
            default:
                return false;
        }
    }
    
    // Getters
    public Long getId() { return id; }
    public Policy getPolicy() { return policy; }
    public BigDecimal getGuaranteedSum() { return guaranteedSum; }
    public String getCoverageArea() { return coverageArea; }
    public ACVariant getAcVariant() { return acVariant; }
    public BigDecimal getSumInsured() { return sumInsured; }
    public String getCoverageScope() { return coverageScope; }
    public BigDecimal getDeductible() { return deductible; }
    public String getWorkshopType() { return workshopType; }
    public String getCoveredPersons() { return coveredPersons; }
    
    // Setters
    public void setPolicy(Policy policy) { this.policy = policy; }
    public void setGuaranteedSum(BigDecimal guaranteedSum) { this.guaranteedSum = guaranteedSum; }
    public void setCoverageArea(String coverageArea) { this.coverageArea = coverageArea; }
    public void setAcVariant(ACVariant acVariant) { this.acVariant = acVariant; }
    public void setSumInsured(BigDecimal sumInsured) { this.sumInsured = sumInsured; }
    public void setCoverageScope(String coverageScope) { this.coverageScope = coverageScope; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    public void setWorkshopType(String workshopType) { this.workshopType = workshopType; }
    public void setCoveredPersons(String coveredPersons) { this.coveredPersons = coveredPersons; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyDetails that = (PolicyDetails) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PolicyDetails{" +
                "id=" + id +
                ", guaranteedSum=" + guaranteedSum +
                ", sumInsured=" + sumInsured +
                ", acVariant=" + acVariant +
                '}';
    }
    
    /**
     * Builder pattern implementation for clean object creation.
     */
    public static class Builder {
        private Policy policy;
        private BigDecimal guaranteedSum;
        private String coverageArea;
        private ACVariant acVariant;
        private BigDecimal sumInsured;
        private String coverageScope;
        private BigDecimal deductible;
        private String workshopType;
        private String coveredPersons;
        
        public Builder policy(Policy policy) {
            this.policy = policy;
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
        
        public PolicyDetails build() {
            return new PolicyDetails(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}