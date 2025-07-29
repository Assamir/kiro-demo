package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.RatingTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for premium calculations using rating tables.
 * Implements business logic for insurance premium calculations based on rating factors.
 * Clean Code: Single Responsibility - handles only rating and premium calculation logic.
 */
@Service
@Transactional(readOnly = true)
public class RatingService {
    
    private final RatingTableRepository ratingTableRepository;
    
    // Base premium amounts for different insurance types
    private static final Map<InsuranceType, BigDecimal> BASE_PREMIUMS = Map.of(
            InsuranceType.OC, new BigDecimal("800.00"),
            InsuranceType.AC, new BigDecimal("1200.00"),
            InsuranceType.NNW, new BigDecimal("300.00")
    );
    
    @Autowired
    public RatingService(RatingTableRepository ratingTableRepository) {
        this.ratingTableRepository = ratingTableRepository;
    }
    
    /**
     * Calculates premium for a given insurance type, vehicle, and policy date.
     * Clean Code: Main business method with clear purpose and parameters.
     * 
     * @param insuranceType the type of insurance
     * @param vehicle the vehicle to be insured
     * @param policyDate the policy effective date
     * @return calculated premium amount
     * @throws IllegalArgumentException if parameters are invalid
     * @throws PremiumCalculationException if calculation fails
     */
    public BigDecimal calculatePremium(InsuranceType insuranceType, Vehicle vehicle, LocalDate policyDate) {
        validateCalculationParameters(insuranceType, vehicle, policyDate);
        
        try {
            // Get base premium for insurance type
            BigDecimal basePremium = getBasePremium(insuranceType);
            
            // Calculate rating factors
            Map<String, BigDecimal> ratingFactors = calculateRatingFactors(insuranceType, vehicle, policyDate);
            
            // Apply rating factors to base premium
            BigDecimal calculatedPremium = applyRatingFactors(basePremium, ratingFactors);
            
            // Round to 2 decimal places
            return calculatedPremium.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            throw new PremiumCalculationException(
                    "Failed to calculate premium for " + insuranceType + " insurance", e);
        }
    }
    
    /**
     * Calculates premium breakdown showing base premium and applied factors.
     * Clean Code: Provides detailed calculation information for transparency.
     * 
     * @param insuranceType the type of insurance
     * @param vehicle the vehicle to be insured
     * @param policyDate the policy effective date
     * @return premium breakdown with factors
     */
    public PremiumBreakdown calculatePremiumBreakdown(InsuranceType insuranceType, Vehicle vehicle, LocalDate policyDate) {
        validateCalculationParameters(insuranceType, vehicle, policyDate);
        
        BigDecimal basePremium = getBasePremium(insuranceType);
        Map<String, BigDecimal> ratingFactors = calculateRatingFactors(insuranceType, vehicle, policyDate);
        BigDecimal finalPremium = applyRatingFactors(basePremium, ratingFactors);
        
        return new PremiumBreakdown(basePremium, ratingFactors, finalPremium.setScale(2, RoundingMode.HALF_UP));
    }
    
    /**
     * Retrieves all rating tables for a specific insurance type and date.
     * Clean Code: Data access method with clear purpose.
     * 
     * @param insuranceType the insurance type
     * @param date the date to check validity against
     * @return list of valid rating tables
     */
    public List<RatingTable> getRatingTablesForDate(InsuranceType insuranceType, LocalDate date) {
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        return ratingTableRepository.findByInsuranceTypeValidForDate(insuranceType, date);
    }
    
    /**
     * Retrieves currently valid rating tables for an insurance type.
     * Clean Code: Convenience method for current date calculations.
     * 
     * @param insuranceType the insurance type
     * @return list of currently valid rating tables
     */
    public List<RatingTable> getCurrentRatingTables(InsuranceType insuranceType) {
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type cannot be null");
        }
        
        return ratingTableRepository.findCurrentlyValidByInsuranceType(insuranceType);
    }
    
    /**
     * Calculates rating factors based on vehicle characteristics and policy date.
     * Clean Code: Extracted calculation logic for different rating factors.
     */
    private Map<String, BigDecimal> calculateRatingFactors(InsuranceType insuranceType, Vehicle vehicle, LocalDate policyDate) {
        Map<String, BigDecimal> factors = new HashMap<>();
        
        // Vehicle age factor
        int vehicleAge = calculateVehicleAge(vehicle, policyDate);
        BigDecimal ageFactor = getVehicleAgeFactor(insuranceType, vehicleAge, policyDate);
        if (ageFactor != null) {
            factors.put("VEHICLE_AGE", ageFactor);
        }
        
        // Engine capacity factor
        BigDecimal engineFactor = getEngineCapacityFactor(insuranceType, vehicle.getEngineCapacity(), policyDate);
        if (engineFactor != null) {
            factors.put("ENGINE_CAPACITY", engineFactor);
        }
        
        // Power factor
        BigDecimal powerFactor = getPowerFactor(insuranceType, vehicle.getPower(), policyDate);
        if (powerFactor != null) {
            factors.put("POWER", powerFactor);
        }
        
        // Insurance type specific factors
        addInsuranceTypeSpecificFactors(factors, insuranceType, vehicle, policyDate);
        
        return factors;
    }
    
    /**
     * Gets vehicle age factor from rating tables.
     * Clean Code: Specific factor calculation method.
     */
    private BigDecimal getVehicleAgeFactor(InsuranceType insuranceType, int vehicleAge, LocalDate policyDate) {
        String ratingKey = "VEHICLE_AGE_" + Math.min(vehicleAge, 10); // Cap at 10 years
        return getRatingMultiplier(insuranceType, ratingKey, policyDate);
    }
    
    /**
     * Gets engine capacity factor from rating tables.
     * Clean Code: Specific factor calculation method.
     */
    private BigDecimal getEngineCapacityFactor(InsuranceType insuranceType, Integer engineCapacity, LocalDate policyDate) {
        String ratingKey;
        if (engineCapacity <= 1000) {
            ratingKey = "ENGINE_SMALL";
        } else if (engineCapacity <= 1600) {
            ratingKey = "ENGINE_MEDIUM";
        } else if (engineCapacity <= 2000) {
            ratingKey = "ENGINE_LARGE";
        } else {
            ratingKey = "ENGINE_XLARGE";
        }
        
        return getRatingMultiplier(insuranceType, ratingKey, policyDate);
    }
    
    /**
     * Gets power factor from rating tables.
     * Clean Code: Specific factor calculation method.
     */
    private BigDecimal getPowerFactor(InsuranceType insuranceType, Integer power, LocalDate policyDate) {
        String ratingKey;
        if (power <= 75) {
            ratingKey = "POWER_LOW";
        } else if (power <= 150) {
            ratingKey = "POWER_MEDIUM";
        } else if (power <= 250) {
            ratingKey = "POWER_HIGH";
        } else {
            ratingKey = "POWER_VERY_HIGH";
        }
        
        return getRatingMultiplier(insuranceType, ratingKey, policyDate);
    }
    
    /**
     * Adds insurance type specific rating factors.
     * Clean Code: Extensible method for type-specific calculations.
     */
    private void addInsuranceTypeSpecificFactors(Map<String, BigDecimal> factors, 
                                               InsuranceType insuranceType, 
                                               Vehicle vehicle, 
                                               LocalDate policyDate) {
        switch (insuranceType) {
            case OC:
                // OC specific factors (e.g., coverage area)
                BigDecimal ocFactor = getRatingMultiplier(insuranceType, "OC_STANDARD", policyDate);
                if (ocFactor != null) {
                    factors.put("OC_COVERAGE", ocFactor);
                }
                break;
                
            case AC:
                // AC specific factors (e.g., vehicle value)
                BigDecimal acFactor = getRatingMultiplier(insuranceType, "AC_COMPREHENSIVE", policyDate);
                if (acFactor != null) {
                    factors.put("AC_COVERAGE", acFactor);
                }
                break;
                
            case NNW:
                // NNW specific factors (e.g., coverage amount)
                BigDecimal nnwFactor = getRatingMultiplier(insuranceType, "NNW_STANDARD", policyDate);
                if (nnwFactor != null) {
                    factors.put("NNW_COVERAGE", nnwFactor);
                }
                break;
        }
    }
    
    /**
     * Retrieves rating multiplier from rating tables.
     * Clean Code: Centralized rating table lookup method.
     */
    private BigDecimal getRatingMultiplier(InsuranceType insuranceType, String ratingKey, LocalDate policyDate) {
        List<RatingTable> ratingTables = ratingTableRepository
                .findByInsuranceTypeAndRatingKeyValidForDate(insuranceType, ratingKey, policyDate);
        
        if (ratingTables.isEmpty()) {
            // Return neutral multiplier if no rating found
            return BigDecimal.ONE;
        }
        
        // Use the first valid rating table (should be only one for a given date)
        return ratingTables.get(0).getMultiplier();
    }
    
    /**
     * Applies rating factors to base premium.
     * Clean Code: Mathematical calculation with clear logic.
     */
    private BigDecimal applyRatingFactors(BigDecimal basePremium, Map<String, BigDecimal> ratingFactors) {
        BigDecimal result = basePremium;
        
        for (BigDecimal factor : ratingFactors.values()) {
            result = result.multiply(factor);
        }
        
        return result;
    }
    
    /**
     * Gets base premium for insurance type.
     * Clean Code: Simple lookup method with fallback.
     */
    private BigDecimal getBasePremium(InsuranceType insuranceType) {
        return BASE_PREMIUMS.getOrDefault(insuranceType, new BigDecimal("500.00"));
    }
    
    /**
     * Calculates vehicle age at policy date.
     * Clean Code: Utility method with clear calculation.
     */
    private int calculateVehicleAge(Vehicle vehicle, LocalDate policyDate) {
        return Period.between(vehicle.getFirstRegistrationDate(), policyDate).getYears();
    }
    
    /**
     * Validates calculation parameters.
     * Clean Code: Extracted validation logic.
     */
    private void validateCalculationParameters(InsuranceType insuranceType, Vehicle vehicle, LocalDate policyDate) {
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (policyDate == null) {
            throw new IllegalArgumentException("Policy date cannot be null");
        }
    }
    
    /**
     * Inner class representing premium calculation breakdown.
     * Clean Code: Value object for structured data return.
     */
    public static class PremiumBreakdown {
        private final BigDecimal basePremium;
        private final Map<String, BigDecimal> ratingFactors;
        private final BigDecimal finalPremium;
        
        public PremiumBreakdown(BigDecimal basePremium, Map<String, BigDecimal> ratingFactors, BigDecimal finalPremium) {
            this.basePremium = basePremium;
            this.ratingFactors = new HashMap<>(ratingFactors);
            this.finalPremium = finalPremium;
        }
        
        public BigDecimal getBasePremium() { return basePremium; }
        public Map<String, BigDecimal> getRatingFactors() { return new HashMap<>(ratingFactors); }
        public BigDecimal getFinalPremium() { return finalPremium; }
        
        @Override
        public String toString() {
            return "PremiumBreakdown{" +
                    "basePremium=" + basePremium +
                    ", ratingFactors=" + ratingFactors +
                    ", finalPremium=" + finalPremium +
                    '}';
        }
    }
}