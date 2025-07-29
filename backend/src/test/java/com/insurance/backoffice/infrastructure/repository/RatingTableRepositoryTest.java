package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.RatingTable;
import com.insurance.backoffice.domain.InsuranceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for RatingTableRepository using H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RatingTableRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private RatingTableRepository ratingTableRepository;
    
    private RatingTable ocDriverAge;
    private RatingTable ocVehicleAge;
    private RatingTable acDriverAge;
    private RatingTable expiredRating;
    private RatingTable futureRating;
    
    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();
        
        // Create current rating tables
        ocDriverAge = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("DRIVER_AGE_25_35")
                .multiplier(BigDecimal.valueOf(1.2))
                .validFrom(now.minusYears(1))
                .validTo(now.plusYears(1))
                .build();
        
        ocVehicleAge = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("VEHICLE_AGE_0_3")
                .multiplier(BigDecimal.valueOf(0.9))
                .validFrom(now.minusYears(1))
                .validTo(null) // No end date
                .build();
        
        acDriverAge = RatingTable.builder()
                .insuranceType(InsuranceType.AC)
                .ratingKey("DRIVER_AGE_25_35")
                .multiplier(BigDecimal.valueOf(1.1))
                .validFrom(now.minusYears(1))
                .validTo(now.plusYears(1))
                .build();
        
        // Create expired rating table
        expiredRating = RatingTable.builder()
                .insuranceType(InsuranceType.OC)
                .ratingKey("OLD_RATING")
                .multiplier(BigDecimal.valueOf(1.5))
                .validFrom(now.minusYears(2))
                .validTo(now.minusDays(1))
                .build();
        
        // Create future rating table
        futureRating = RatingTable.builder()
                .insuranceType(InsuranceType.NNW)
                .ratingKey("FUTURE_RATING")
                .multiplier(BigDecimal.valueOf(1.3))
                .validFrom(now.plusDays(1))
                .validTo(now.plusYears(1))
                .build();
        
        entityManager.persistAndFlush(ocDriverAge);
        entityManager.persistAndFlush(ocVehicleAge);
        entityManager.persistAndFlush(acDriverAge);
        entityManager.persistAndFlush(expiredRating);
        entityManager.persistAndFlush(futureRating);
        entityManager.clear();
    }
    
    @Test
    void shouldFindRatingTablesByInsuranceType() {
        // When
        List<RatingTable> ocRatings = ratingTableRepository.findByInsuranceType(InsuranceType.OC);
        List<RatingTable> acRatings = ratingTableRepository.findByInsuranceType(InsuranceType.AC);
        List<RatingTable> nnwRatings = ratingTableRepository.findByInsuranceType(InsuranceType.NNW);
        
        // Then
        assertThat(ocRatings).hasSize(3); // ocDriverAge, ocVehicleAge, expiredRating
        assertThat(acRatings).hasSize(1); // acDriverAge
        assertThat(nnwRatings).hasSize(1); // futureRating
    }
    
    @Test
    void shouldFindRatingTableByInsuranceTypeAndRatingKey() {
        // When
        Optional<RatingTable> found = ratingTableRepository.findByInsuranceTypeAndRatingKey(
                InsuranceType.OC, "DRIVER_AGE_25_35");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMultiplier()).isEqualByComparingTo(BigDecimal.valueOf(1.2));
    }
    
    @Test
    void shouldFindRatingTablesByInsuranceTypeAndRatingKeyValidForDate() {
        // When
        LocalDate testDate = LocalDate.now();
        List<RatingTable> validRatings = ratingTableRepository.findByInsuranceTypeAndRatingKeyValidForDate(
                InsuranceType.OC, "DRIVER_AGE_25_35", testDate);
        
        // Then
        assertThat(validRatings).hasSize(1);
        assertThat(validRatings.get(0).getMultiplier()).isEqualByComparingTo(BigDecimal.valueOf(1.2));
    }
    
    @Test
    void shouldFindRatingTablesByInsuranceTypeValidForDate() {
        // When
        LocalDate testDate = LocalDate.now();
        List<RatingTable> validOcRatings = ratingTableRepository.findByInsuranceTypeValidForDate(
                InsuranceType.OC, testDate);
        
        // Then
        assertThat(validOcRatings).hasSize(2); // ocDriverAge and ocVehicleAge (not expired)
        assertThat(validOcRatings).extracting(RatingTable::getRatingKey)
                .containsExactlyInAnyOrder("DRIVER_AGE_25_35", "VEHICLE_AGE_0_3");
    }
    
    @Test
    void shouldFindCurrentlyValidRatingTablesByInsuranceType() {
        // When
        List<RatingTable> currentlyValidOc = ratingTableRepository.findCurrentlyValidByInsuranceType(InsuranceType.OC);
        List<RatingTable> currentlyValidAc = ratingTableRepository.findCurrentlyValidByInsuranceType(InsuranceType.AC);
        List<RatingTable> currentlyValidNnw = ratingTableRepository.findCurrentlyValidByInsuranceType(InsuranceType.NNW);
        
        // Then
        assertThat(currentlyValidOc).hasSize(2); // ocDriverAge and ocVehicleAge
        assertThat(currentlyValidAc).hasSize(1); // acDriverAge
        assertThat(currentlyValidNnw).isEmpty(); // futureRating is not yet valid
    }
    
    @Test
    void shouldFindRatingTablesByRatingKey() {
        // When
        List<RatingTable> driverAgeRatings = ratingTableRepository.findByRatingKey("DRIVER_AGE_25_35");
        
        // Then
        assertThat(driverAgeRatings).hasSize(2); // ocDriverAge and acDriverAge
        assertThat(driverAgeRatings).extracting(RatingTable::getInsuranceType)
                .containsExactlyInAnyOrder(InsuranceType.OC, InsuranceType.AC);
    }
    
    @Test
    void shouldFindCurrentlyValidRatingTables() {
        // When
        List<RatingTable> currentlyValid = ratingTableRepository.findCurrentlyValid();
        
        // Then
        assertThat(currentlyValid).hasSize(3); // ocDriverAge, ocVehicleAge, acDriverAge
        assertThat(currentlyValid).extracting(RatingTable::getRatingKey)
                .containsExactlyInAnyOrder("DRIVER_AGE_25_35", "VEHICLE_AGE_0_3", "DRIVER_AGE_25_35");
    }
    
    @Test
    void shouldFindExpiredRatingTables() {
        // When
        List<RatingTable> expired = ratingTableRepository.findExpired();
        
        // Then
        assertThat(expired).hasSize(1);
        assertThat(expired.get(0).getRatingKey()).isEqualTo("OLD_RATING");
    }
    
    @Test
    void shouldFindFutureEffectiveRatingTables() {
        // When
        List<RatingTable> futureEffective = ratingTableRepository.findFutureEffective();
        
        // Then
        assertThat(futureEffective).hasSize(1);
        assertThat(futureEffective.get(0).getRatingKey()).isEqualTo("FUTURE_RATING");
    }
    
    @Test
    void shouldFindOverlappingValidityPeriods() {
        // When - check for overlaps with a new rating that would overlap with ocDriverAge
        LocalDate newValidFrom = LocalDate.now().minusMonths(6);
        LocalDate newValidTo = LocalDate.now().plusMonths(6);
        
        List<RatingTable> overlapping = ratingTableRepository.findOverlappingValidityPeriods(
                InsuranceType.OC, "DRIVER_AGE_25_35", newValidFrom, newValidTo);
        
        // Then
        assertThat(overlapping).hasSize(1);
        assertThat(overlapping.get(0).getRatingKey()).isEqualTo("DRIVER_AGE_25_35");
    }
    
    @Test
    void shouldNotFindOverlappingWhenNoOverlap() {
        // When - check for overlaps with a future period that doesn't overlap
        LocalDate newValidFrom = LocalDate.now().plusYears(2);
        LocalDate newValidTo = LocalDate.now().plusYears(3);
        
        List<RatingTable> overlapping = ratingTableRepository.findOverlappingValidityPeriods(
                InsuranceType.OC, "DRIVER_AGE_25_35", newValidFrom, newValidTo);
        
        // Then
        assertThat(overlapping).isEmpty();
    }
    
    @Test
    void shouldReturnEmptyWhenRatingTableNotFound() {
        // When
        Optional<RatingTable> found = ratingTableRepository.findByInsuranceTypeAndRatingKey(
                InsuranceType.OC, "NONEXISTENT_KEY");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldSaveAndRetrieveRatingTable() {
        // Given
        RatingTable newRating = RatingTable.builder()
                .insuranceType(InsuranceType.NNW)
                .ratingKey("NEW_RATING_KEY")
                .multiplier(BigDecimal.valueOf(1.4))
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusYears(1))
                .build();
        
        // When
        RatingTable saved = ratingTableRepository.save(newRating);
        Optional<RatingTable> retrieved = ratingTableRepository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getRatingKey()).isEqualTo("NEW_RATING_KEY");
        assertThat(retrieved.get().getMultiplier()).isEqualByComparingTo(BigDecimal.valueOf(1.4));
    }
    
    @Test
    void shouldDeleteRatingTable() {
        // Given
        Long ratingId = futureRating.getId();
        
        // When
        ratingTableRepository.deleteById(ratingId);
        Optional<RatingTable> deleted = ratingTableRepository.findById(ratingId);
        
        // Then
        assertThat(deleted).isEmpty();
    }
    
    @Test
    void shouldFindAllRatingTables() {
        // When
        List<RatingTable> allRatings = ratingTableRepository.findAll();
        
        // Then
        assertThat(allRatings).hasSize(5);
    }
}