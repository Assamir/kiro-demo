package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.RatingTable;
import com.insurance.backoffice.domain.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RatingTable entity operations.
 * Provides data access methods for rating table management and premium calculations.
 */
@Repository
public interface RatingTableRepository extends JpaRepository<RatingTable, Long> {
    
    /**
     * Finds all rating tables for a specific insurance type.
     * Used for premium calculation and rating management.
     * 
     * @param insuranceType the insurance type to filter by
     * @return list of rating tables for the specified insurance type
     */
    List<RatingTable> findByInsuranceType(InsuranceType insuranceType);
    
    /**
     * Finds a rating table by insurance type and rating key.
     * Used for specific rating factor lookup during premium calculation.
     * 
     * @param insuranceType the insurance type
     * @param ratingKey the rating key to search for
     * @return Optional containing the rating table if found, empty otherwise
     */
    Optional<RatingTable> findByInsuranceTypeAndRatingKey(InsuranceType insuranceType, String ratingKey);
    
    /**
     * Finds rating tables by insurance type and rating key that are valid for a specific date.
     * Used for premium calculation with date-specific rating factors.
     * 
     * @param insuranceType the insurance type
     * @param ratingKey the rating key
     * @param date the date to check validity against
     * @return list of rating tables valid for the specified date
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.insuranceType = :insuranceType AND rt.ratingKey = :ratingKey " +
           "AND rt.validFrom <= :date AND (rt.validTo IS NULL OR rt.validTo >= :date)")
    List<RatingTable> findByInsuranceTypeAndRatingKeyValidForDate(
        @Param("insuranceType") InsuranceType insuranceType,
        @Param("ratingKey") String ratingKey,
        @Param("date") LocalDate date
    );
    
    /**
     * Finds all rating tables for an insurance type that are valid for a specific date.
     * Used for comprehensive premium calculation.
     * 
     * @param insuranceType the insurance type
     * @param date the date to check validity against
     * @return list of rating tables valid for the specified date
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.insuranceType = :insuranceType " +
           "AND rt.validFrom <= :date AND (rt.validTo IS NULL OR rt.validTo >= :date)")
    List<RatingTable> findByInsuranceTypeValidForDate(
        @Param("insuranceType") InsuranceType insuranceType,
        @Param("date") LocalDate date
    );
    
    /**
     * Finds all currently valid rating tables for an insurance type.
     * Used for current premium calculations.
     * 
     * @param insuranceType the insurance type
     * @return list of currently valid rating tables
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.insuranceType = :insuranceType " +
           "AND rt.validFrom <= CURRENT_DATE AND (rt.validTo IS NULL OR rt.validTo >= CURRENT_DATE)")
    List<RatingTable> findCurrentlyValidByInsuranceType(@Param("insuranceType") InsuranceType insuranceType);
    
    /**
     * Finds rating tables by rating key across all insurance types.
     * Used for rating key management and analysis.
     * 
     * @param ratingKey the rating key to search for
     * @return list of rating tables with the specified rating key
     */
    List<RatingTable> findByRatingKey(String ratingKey);
    
    /**
     * Finds rating tables that are currently valid (not expired).
     * Used for active rating table management.
     * 
     * @return list of currently valid rating tables
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.validFrom <= CURRENT_DATE AND (rt.validTo IS NULL OR rt.validTo >= CURRENT_DATE)")
    List<RatingTable> findCurrentlyValid();
    
    /**
     * Finds rating tables that have expired.
     * Used for rating table cleanup and historical analysis.
     * 
     * @return list of expired rating tables
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.validTo IS NOT NULL AND rt.validTo < CURRENT_DATE")
    List<RatingTable> findExpired();
    
    /**
     * Finds rating tables that will become effective in the future.
     * Used for rating table planning and management.
     * 
     * @return list of future-effective rating tables
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.validFrom > CURRENT_DATE")
    List<RatingTable> findFutureEffective();
    
    /**
     * Finds rating tables with overlapping validity periods for the same insurance type and rating key.
     * Used for validation to prevent conflicting rating entries.
     * 
     * @param insuranceType the insurance type
     * @param ratingKey the rating key
     * @param validFrom the start date of the new period
     * @param validTo the end date of the new period (can be null)
     * @return list of overlapping rating tables
     */
    @Query("SELECT rt FROM RatingTable rt WHERE rt.insuranceType = :insuranceType AND rt.ratingKey = :ratingKey " +
           "AND ((rt.validFrom <= :validFrom AND (rt.validTo IS NULL OR rt.validTo >= :validFrom)) " +
           "OR (rt.validFrom <= :validTo AND (rt.validTo IS NULL OR rt.validTo >= :validTo)) " +
           "OR (rt.validFrom >= :validFrom AND (rt.validTo IS NULL OR rt.validTo <= :validTo)))")
    List<RatingTable> findOverlappingValidityPeriods(
        @Param("insuranceType") InsuranceType insuranceType,
        @Param("ratingKey") String ratingKey,
        @Param("validFrom") LocalDate validFrom,
        @Param("validTo") LocalDate validTo
    );
}