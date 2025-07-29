package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.Policy;
import com.insurance.backoffice.domain.PolicyStatus;
import com.insurance.backoffice.domain.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Policy entity operations.
 * Provides data access methods for policy management functionality including
 * custom query methods for policy search by client and complex filtering.
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    /**
     * Finds a policy by its policy number.
     * Policy number is unique identifier for policies.
     * 
     * @param policyNumber the policy number to search for
     * @return Optional containing the policy if found, empty otherwise
     */
    Optional<Policy> findByPolicyNumber(String policyNumber);
    
    /**
     * Checks if a policy exists with the given policy number.
     * Used for validation during policy creation.
     * 
     * @param policyNumber the policy number to check
     * @return true if a policy exists with this number, false otherwise
     */
    boolean existsByPolicyNumber(String policyNumber);
    
    /**
     * Finds all policies for a specific client by client ID.
     * This is a key requirement for policy search by client functionality.
     * 
     * @param clientId the ID of the client
     * @return list of policies belonging to the specified client
     */
    List<Policy> findByClientId(Long clientId);
    
    /**
     * Finds all policies for a specific client by client ID, ordered by issue date descending.
     * Used to show most recent policies first.
     * 
     * @param clientId the ID of the client
     * @return list of policies belonging to the specified client, ordered by issue date
     */
    List<Policy> findByClientIdOrderByIssueDateDesc(Long clientId);
    
    /**
     * Finds all policies for a specific vehicle by vehicle ID.
     * Used for vehicle history and policy management.
     * 
     * @param vehicleId the ID of the vehicle
     * @return list of policies for the specified vehicle
     */
    List<Policy> findByVehicleId(Long vehicleId);
    
    /**
     * Finds policies by status.
     * Used for policy filtering and management.
     * 
     * @param status the policy status to filter by
     * @return list of policies with the specified status
     */
    List<Policy> findByStatus(PolicyStatus status);
    
    /**
     * Finds policies by insurance type.
     * Used for policy filtering and reporting.
     * 
     * @param insuranceType the insurance type to filter by
     * @return list of policies with the specified insurance type
     */
    List<Policy> findByInsuranceType(InsuranceType insuranceType);
    
    /**
     * Finds policies by client ID and status.
     * Used for complex filtering combining client and status criteria.
     * 
     * @param clientId the ID of the client
     * @param status the policy status to filter by
     * @return list of policies matching both criteria
     */
    List<Policy> findByClientIdAndStatus(Long clientId, PolicyStatus status);
    
    /**
     * Finds policies by client ID and insurance type.
     * Used for complex filtering combining client and insurance type criteria.
     * 
     * @param clientId the ID of the client
     * @param insuranceType the insurance type to filter by
     * @return list of policies matching both criteria
     */
    List<Policy> findByClientIdAndInsuranceType(Long clientId, InsuranceType insuranceType);
    
    /**
     * Finds policies issued within a date range.
     * Used for reporting and policy management by date.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of policies issued within the specified date range
     */
    List<Policy> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds policies that are currently active (status = ACTIVE and within coverage period).
     * Used for reporting and active policy management.
     * 
     * @param currentDate the current date to check against coverage period
     * @return list of currently active policies
     */
    @Query("SELECT p FROM Policy p WHERE p.status = 'ACTIVE' AND :currentDate >= p.startDate AND :currentDate <= p.endDate")
    List<Policy> findCurrentlyActivePolicies(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Finds policies that are expiring within a specified number of days.
     * Used for policy renewal notifications.
     * 
     * @param currentDate the current date
     * @param daysAhead the number of days to look ahead
     * @return list of policies expiring within the specified timeframe
     */
    @Query("SELECT p FROM Policy p WHERE p.status = 'ACTIVE' AND p.endDate BETWEEN :currentDate AND :expirationDate")
    List<Policy> findPoliciesExpiringWithinDays(@Param("currentDate") LocalDate currentDate, @Param("expirationDate") LocalDate expirationDate);
    
    /**
     * Complex search for policies with multiple optional criteria.
     * Used for advanced policy search and filtering functionality.
     * 
     * @param clientId optional client ID filter
     * @param vehicleId optional vehicle ID filter
     * @param status optional status filter
     * @param insuranceType optional insurance type filter
     * @param startDate optional start date for issue date range
     * @param endDate optional end date for issue date range
     * @return list of policies matching the specified criteria
     */
    @Query("SELECT p FROM Policy p WHERE " +
           "(:clientId IS NULL OR p.client.id = :clientId) AND " +
           "(:vehicleId IS NULL OR p.vehicle.id = :vehicleId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:insuranceType IS NULL OR p.insuranceType = :insuranceType) AND " +
           "(:startDate IS NULL OR p.issueDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.issueDate <= :endDate)")
    List<Policy> findPoliciesWithCriteria(
        @Param("clientId") Long clientId,
        @Param("vehicleId") Long vehicleId,
        @Param("status") PolicyStatus status,
        @Param("insuranceType") InsuranceType insuranceType,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Finds policies by client name (case-insensitive search).
     * Used for policy search when client ID is not known.
     * 
     * @param clientName the client name to search for
     * @return list of policies for clients whose names contain the search term
     */
    @Query("SELECT p FROM Policy p WHERE LOWER(p.client.fullName) LIKE LOWER(CONCAT('%', :clientName, '%'))")
    List<Policy> findByClientNameContainingIgnoreCase(@Param("clientName") String clientName);
    
    /**
     * Finds policies by vehicle registration number.
     * Used for policy search by vehicle identification.
     * 
     * @param registrationNumber the vehicle registration number
     * @return list of policies for the vehicle with the specified registration
     */
    @Query("SELECT p FROM Policy p WHERE p.vehicle.registrationNumber = :registrationNumber")
    List<Policy> findByVehicleRegistrationNumber(@Param("registrationNumber") String registrationNumber);
}