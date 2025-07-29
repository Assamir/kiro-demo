package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.PolicyDetails;
import com.insurance.backoffice.domain.ACVariant;
import com.insurance.backoffice.domain.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PolicyDetails entity operations.
 * Provides data access methods for policy details management.
 */
@Repository
public interface PolicyDetailsRepository extends JpaRepository<PolicyDetails, Long> {
    
    /**
     * Finds policy details by policy ID.
     * Used to retrieve details for a specific policy.
     * 
     * @param policyId the ID of the policy
     * @return Optional containing the policy details if found, empty otherwise
     */
    Optional<PolicyDetails> findByPolicyId(Long policyId);
    
    /**
     * Finds policy details by policy number.
     * Used for policy details lookup by policy identifier.
     * 
     * @param policyNumber the policy number
     * @return Optional containing the policy details if found, empty otherwise
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.policyNumber = :policyNumber")
    Optional<PolicyDetails> findByPolicyNumber(@Param("policyNumber") String policyNumber);
    
    /**
     * Finds all policy details for AC insurance with a specific variant.
     * Used for AC insurance analysis and reporting.
     * 
     * @param acVariant the AC variant to filter by
     * @return list of policy details with the specified AC variant
     */
    List<PolicyDetails> findByAcVariant(ACVariant acVariant);
    
    /**
     * Finds policy details for OC insurance with guaranteed sum within a range.
     * Used for OC insurance analysis and reporting.
     * 
     * @param minSum the minimum guaranteed sum
     * @param maxSum the maximum guaranteed sum
     * @return list of OC policy details within the sum range
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'OC' " +
           "AND pd.guaranteedSum BETWEEN :minSum AND :maxSum")
    List<PolicyDetails> findOCPoliciesWithGuaranteedSumBetween(
        @Param("minSum") BigDecimal minSum, 
        @Param("maxSum") BigDecimal maxSum
    );
    
    /**
     * Finds policy details for AC insurance with sum insured within a range.
     * Used for AC insurance analysis and reporting.
     * 
     * @param minSum the minimum sum insured
     * @param maxSum the maximum sum insured
     * @return list of AC policy details within the sum range
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'AC' " +
           "AND pd.sumInsured BETWEEN :minSum AND :maxSum")
    List<PolicyDetails> findACPoliciesWithSumInsuredBetween(
        @Param("minSum") BigDecimal minSum, 
        @Param("maxSum") BigDecimal maxSum
    );
    
    /**
     * Finds policy details for NNW insurance with sum insured within a range.
     * Used for NNW insurance analysis and reporting.
     * 
     * @param minSum the minimum sum insured
     * @param maxSum the maximum sum insured
     * @return list of NNW policy details within the sum range
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'NNW' " +
           "AND pd.sumInsured BETWEEN :minSum AND :maxSum")
    List<PolicyDetails> findNNWPoliciesWithSumInsuredBetween(
        @Param("minSum") BigDecimal minSum, 
        @Param("maxSum") BigDecimal maxSum
    );
    
    /**
     * Finds policy details by coverage area for OC insurance.
     * Used for OC insurance geographical analysis.
     * 
     * @param coverageArea the coverage area to search for
     * @return list of OC policy details with the specified coverage area
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'OC' " +
           "AND LOWER(pd.coverageArea) = LOWER(:coverageArea)")
    List<PolicyDetails> findOCPoliciesByCoverageArea(@Param("coverageArea") String coverageArea);
    
    /**
     * Finds policy details by workshop type for AC insurance.
     * Used for AC insurance workshop analysis.
     * 
     * @param workshopType the workshop type to search for
     * @return list of AC policy details with the specified workshop type
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'AC' " +
           "AND LOWER(pd.workshopType) = LOWER(:workshopType)")
    List<PolicyDetails> findACPoliciesByWorkshopType(@Param("workshopType") String workshopType);
    
    /**
     * Finds policy details with deductible within a range for AC insurance.
     * Used for AC insurance deductible analysis.
     * 
     * @param minDeductible the minimum deductible amount
     * @param maxDeductible the maximum deductible amount
     * @return list of AC policy details within the deductible range
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = 'AC' " +
           "AND pd.deductible BETWEEN :minDeductible AND :maxDeductible")
    List<PolicyDetails> findACPoliciesWithDeductibleBetween(
        @Param("minDeductible") BigDecimal minDeductible, 
        @Param("maxDeductible") BigDecimal maxDeductible
    );
    
    /**
     * Finds all policy details for a specific insurance type.
     * Used for insurance type specific analysis and reporting.
     * 
     * @param insuranceType the insurance type to filter by
     * @return list of policy details for the specified insurance type
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.insuranceType = :insuranceType")
    List<PolicyDetails> findByInsuranceType(@Param("insuranceType") InsuranceType insuranceType);
    
    /**
     * Finds policy details for active policies only.
     * Used for current policy details analysis.
     * 
     * @return list of policy details for active policies
     */
    @Query("SELECT pd FROM PolicyDetails pd WHERE pd.policy.status = 'ACTIVE'")
    List<PolicyDetails> findForActivePolicies();
}