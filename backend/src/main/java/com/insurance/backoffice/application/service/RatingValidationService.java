package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.RatingTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Service class for validating rating factors and business rules.
 * Clean Code: Single Responsibility - handles only rating validation logic.
 */
@Service
@Transactional(readOnly = true)
public class RatingValidationService {
    
    private final RatingTableRepository ratingTableRepository;
    
    // Business rule constants
    private static final BigDecimal MIN_MULTIPLIER = new BigDecimal("0.1000");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("5.0000");
    private static final int MAX_VEHICLE_AGE_FOR_AC = 15; // AC insurance not available for very old vehicles
    private static final int MIN_ENGINE_CAPACITY = 50; // Minimum engine capacity in cc
    private static final int MAX_ENGINE_CAPACITY = 8000; // Maximum engine capacity in cc
    private static final int MIN_POWER = 10; // Minimum power in HP
    private static final int MAX_POWER = 1000; // Maximum power in HP
    
    @Autowired
    public RatingValidationService(RatingTableRepository ratingTableRepository) {
        this.ratingTableRepository = ratingTableRepository;
    }
    
    /**
     * Validates rating factors for a given insurance type and vehicle.
     * Clean Code: Main validation method with clear purpose.
     * 
     * @param insuranceType the type of insurance
     * @param vehicle the vehicle to validate
     * @param policyDate the policy effective date
     * @return validation result with any errors found
     */
    public RatingValidationResult validateRatingFactors(InsuranceType insuranceType, 
                                                       Vehicle vehicle, 
                                                       LocalDate policyDate) {
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (policyDate == null) {
            throw new IllegalArgumentException("Policy date cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate vehicle characteristics
        validateVehicleCharacteristics(vehicle, errors, warnings);
        
        // Validate insurance type specific rules
        validateInsuranceTypeRules(insuranceType, vehicle, policyDate, errors, warnings);
        
        // Validate rating table availability
        validateRatingTableAvailability(insuranceType, vehicle, policyDate, errors, warnings);
        
        // Validate business rules
        validateBusinessRules(insuranceType, vehicle, policyDate, errors, warnings);
        
        return new RatingValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * Validates a rating table entry for business rule compliance.
     * Clean Code: Specific validation method for rating tables.
     * 
     * @param ratingTable the rating table to validate
     * @return validation result
     */
    public RatingValidationResult validateRatingTable(RatingTable ratingTable) {
        if (ratingTable == null) {
            throw new IllegalArgumentException("Rating table cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate multiplier range
        if (ratingTable.getMultiplier().compareTo(MIN_MULTIPLIER) < 0) {
            errors.add("Multiplier " + ratingTable.getMultiplier() + " is below minimum allowed value " + MIN_MULTIPLIER);
        }
        if (ratingTable.getMultiplier().compareTo(MAX_MULTIPLIER) > 0) {
            errors.add("Multiplier " + ratingTable.getMultiplier() + " exceeds maximum allowed value " + MAX_MULTIPLIER);
        }
        
        // Validate date ranges
        if (ratingTable.getValidTo() != null && 
            ratingTable.getValidFrom().isAfter(ratingTable.getValidTo())) {
            errors.add("Valid from date must be before valid to date");
        }
        
        // Check for overlapping periods
        List<RatingTable> overlapping = ratingTableRepository.findOverlappingValidityPeriods(
            ratingTable.getInsuranceType(),
            ratingTable.getRatingKey(),
            ratingTable.getValidFrom(),
            ratingTable.getValidTo()
        );
        
        if (!overlapping.isEmpty()) {
            warnings.add("Rating table has overlapping validity periods with " + overlapping.size() + " other entries");
        }
        
        // Validate rating key format
        validateRatingKeyFormat(ratingTable.getRatingKey(), ratingTable.getInsuranceType(), errors, warnings);
        
        return new RatingValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * Checks if premium calculation is possible for given parameters.
     * Clean Code: Intention-revealing method name.
     * 
     * @param insuranceType the insurance type
     * @param vehicle the vehicle
     * @param policyDate the policy date
     * @return true if calculation is possible, false otherwise
     */
    public boolean canCalculatePremium(InsuranceType insuranceType, Vehicle vehicle, LocalDate policyDate) {
        RatingValidationResult result = validateRatingFactors(insuranceType, vehicle, policyDate);
        return result.isValid();
    }
    
    /**
     * Gets missing rating factors for a given scenario.
     * Clean Code: Diagnostic method for troubleshooting.
     * 
     * @param insuranceType the insurance type
     * @param vehicle the vehicle
     * @param policyDate the policy date
     * @return list of missing rating keys
     */
    public List<String> getMissingRatingFactors(InsuranceType insuranceType, 
                                               Vehicle vehicle, 
                                               LocalDate policyDate) {
        List<String> missingFactors = new ArrayList<>();
        
        // Check for required rating factors
        String[] requiredFactors = getRequiredRatingFactors(insuranceType, vehicle);
        
        for (String factor : requiredFactors) {
            List<RatingTable> tables = ratingTableRepository
                .findByInsuranceTypeAndRatingKeyValidForDate(insuranceType, factor, policyDate);
            if (tables.isEmpty()) {
                missingFactors.add(factor);
            }
        }
        
        return missingFactors;
    }
    
    /**
     * Validates vehicle characteristics against business rules.
     * Clean Code: Extracted validation logic.
     */
    private void validateVehicleCharacteristics(Vehicle vehicle, List<String> errors, List<String> warnings) {
        // Validate engine capacity
        if (vehicle.getEngineCapacity() < MIN_ENGINE_CAPACITY) {
            errors.add("Engine capacity " + vehicle.getEngineCapacity() + "cc is below minimum " + MIN_ENGINE_CAPACITY + "cc");
        }
        if (vehicle.getEngineCapacity() > MAX_ENGINE_CAPACITY) {
            errors.add("Engine capacity " + vehicle.getEngineCapacity() + "cc exceeds maximum " + MAX_ENGINE_CAPACITY + "cc");
        }
        
        // Validate power
        if (vehicle.getPower() < MIN_POWER) {
            errors.add("Power " + vehicle.getPower() + "HP is below minimum " + MIN_POWER + "HP");
        }
        if (vehicle.getPower() > MAX_POWER) {
            errors.add("Power " + vehicle.getPower() + "HP exceeds maximum " + MAX_POWER + "HP");
        }
        
        // Validate vehicle age
        LocalDate currentDate = LocalDate.now();
        if (vehicle.getFirstRegistrationDate().isAfter(currentDate)) {
            errors.add("First registration date cannot be in the future");
        }
        
        int vehicleAge = java.time.Period.between(vehicle.getFirstRegistrationDate(), currentDate).getYears();
        if (vehicleAge > 50) {
            warnings.add("Vehicle is very old (" + vehicleAge + " years), premium calculation may not be accurate");
        }
    }
    
    /**
     * Validates insurance type specific business rules.
     * Clean Code: Type-specific validation logic.
     */
    private void validateInsuranceTypeRules(InsuranceType insuranceType, 
                                          Vehicle vehicle, 
                                          LocalDate policyDate, 
                                          List<String> errors, 
                                          List<String> warnings) {
        int vehicleAge = java.time.Period.between(vehicle.getFirstRegistrationDate(), policyDate).getYears();
        
        switch (insuranceType) {
            case AC:
                if (vehicleAge > MAX_VEHICLE_AGE_FOR_AC) {
                    errors.add("AC insurance is not available for vehicles older than " + MAX_VEHICLE_AGE_FOR_AC + " years");
                }
                if (vehicle.getEngineCapacity() < 800) {
                    warnings.add("AC insurance for very small engines may have limited coverage options");
                }
                break;
                
            case OC:
                // OC is mandatory, so no age restrictions
                if (vehicleAge > 30) {
                    warnings.add("Very old vehicles may have limited OC coverage options");
                }
                break;
                
            case NNW:
                // NNW has minimal vehicle-related restrictions
                if (vehicleAge > 25) {
                    warnings.add("NNW insurance for very old vehicles may have different terms");
                }
                break;
        }
    }
    
    /**
     * Validates rating table availability for calculation.
     * Clean Code: Data availability validation.
     */
    private void validateRatingTableAvailability(InsuranceType insuranceType, 
                                               Vehicle vehicle, 
                                               LocalDate policyDate, 
                                               List<String> errors, 
                                               List<String> warnings) {
        String[] requiredFactors = getRequiredRatingFactors(insuranceType, vehicle);
        
        for (String factor : requiredFactors) {
            List<RatingTable> tables = ratingTableRepository
                .findByInsuranceTypeAndRatingKeyValidForDate(insuranceType, factor, policyDate);
            
            if (tables.isEmpty()) {
                errors.add("Missing rating factor: " + factor + " for " + insuranceType + " insurance on " + policyDate);
            } else if (tables.size() > 1) {
                warnings.add("Multiple rating entries found for factor: " + factor + ", using first one");
            }
        }
    }
    
    /**
     * Validates general business rules.
     * Clean Code: Business rule validation.
     */
    private void validateBusinessRules(InsuranceType insuranceType, 
                                     Vehicle vehicle, 
                                     LocalDate policyDate, 
                                     List<String> errors, 
                                     List<String> warnings) {
        // Policy date should not be too far in the future
        if (policyDate.isAfter(LocalDate.now().plusYears(1))) {
            warnings.add("Policy date is more than 1 year in the future, rating factors may not be accurate");
        }
        
        // Policy date should not be too far in the past
        if (policyDate.isBefore(LocalDate.now().minusYears(2))) {
            warnings.add("Policy date is more than 2 years in the past, using historical rating factors");
        }
        
        // Check for unusual vehicle characteristics combinations
        if (vehicle.getEngineCapacity() > 3000 && vehicle.getPower() < 150) {
            warnings.add("Unusual combination: large engine capacity with low power output");
        }
        
        if (vehicle.getEngineCapacity() < 1000 && vehicle.getPower() > 200) {
            warnings.add("Unusual combination: small engine capacity with high power output");
        }
    }
    
    /**
     * Validates rating key format for consistency.
     * Clean Code: Format validation logic.
     */
    private void validateRatingKeyFormat(String ratingKey, InsuranceType insuranceType, 
                                       List<String> errors, List<String> warnings) {
        if (ratingKey == null || ratingKey.trim().isEmpty()) {
            errors.add("Rating key cannot be empty");
            return;
        }
        
        // Check for valid rating key patterns
        String[] validPrefixes = {
            "VEHICLE_AGE_", "ENGINE_", "POWER_", "REGION_", "SEASONAL_",
            "OC_", "AC_", "NNW_", "HISTORICAL_", "FUTURE_"
        };
        
        boolean hasValidPrefix = false;
        for (String prefix : validPrefixes) {
            if (ratingKey.startsWith(prefix)) {
                hasValidPrefix = true;
                break;
            }
        }
        
        if (!hasValidPrefix) {
            warnings.add("Rating key '" + ratingKey + "' does not follow standard naming conventions");
        }
        
        // Check for insurance type consistency
        if (insuranceType == InsuranceType.OC && ratingKey.startsWith("AC_")) {
            warnings.add("Rating key '" + ratingKey + "' seems inconsistent with insurance type " + insuranceType);
        }
        if (insuranceType == InsuranceType.AC && ratingKey.startsWith("OC_")) {
            warnings.add("Rating key '" + ratingKey + "' seems inconsistent with insurance type " + insuranceType);
        }
        if (insuranceType == InsuranceType.NNW && (ratingKey.startsWith("OC_") || ratingKey.startsWith("AC_"))) {
            warnings.add("Rating key '" + ratingKey + "' seems inconsistent with insurance type " + insuranceType);
        }
    }
    
    /**
     * Gets required rating factors for a given scenario.
     * Clean Code: Configuration method for required factors.
     */
    private String[] getRequiredRatingFactors(InsuranceType insuranceType, Vehicle vehicle) {
        int vehicleAge = java.time.Period.between(vehicle.getFirstRegistrationDate(), LocalDate.now()).getYears();
        String ageKey = "VEHICLE_AGE_" + Math.min(vehicleAge, 10);
        
        String engineKey;
        if (vehicle.getEngineCapacity() <= 1000) {
            engineKey = "ENGINE_SMALL";
        } else if (vehicle.getEngineCapacity() <= 1600) {
            engineKey = "ENGINE_MEDIUM";
        } else if (vehicle.getEngineCapacity() <= 2000) {
            engineKey = "ENGINE_LARGE";
        } else {
            engineKey = "ENGINE_XLARGE";
        }
        
        String powerKey;
        if (vehicle.getPower() <= 75) {
            powerKey = "POWER_LOW";
        } else if (vehicle.getPower() <= 150) {
            powerKey = "POWER_MEDIUM";
        } else if (vehicle.getPower() <= 250) {
            powerKey = "POWER_HIGH";
        } else {
            powerKey = "POWER_VERY_HIGH";
        }
        
        // Base factors required for all insurance types
        List<String> factors = new ArrayList<>();
        factors.add(ageKey);
        factors.add(engineKey);
        factors.add(powerKey);
        
        // Add insurance type specific factors
        switch (insuranceType) {
            case OC:
                factors.add("OC_STANDARD");
                break;
            case AC:
                factors.add("AC_COMPREHENSIVE");
                break;
            case NNW:
                factors.add("NNW_STANDARD");
                break;
        }
        
        return factors.toArray(new String[0]);
    }
    
    /**
     * Inner class representing validation results.
     * Clean Code: Value object for structured validation results.
     */
    public static class RatingValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        
        public RatingValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        public boolean hasErrors() { return !errors.isEmpty(); }
        
        @Override
        public String toString() {
            return "RatingValidationResult{" +
                    "valid=" + valid +
                    ", errors=" + errors +
                    ", warnings=" + warnings +
                    '}';
        }
    }
}