package com.insurance.backoffice.testdata;

import com.insurance.backoffice.domain.InsuranceType;
import com.insurance.backoffice.domain.RatingTable;
import com.insurance.backoffice.infrastructure.repository.RatingTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data setup for rating tables.
 * Creates the same rating data as the production seed data for integration tests.
 */
@Component
public class RatingTableTestDataSetup {
    
    @Autowired
    private RatingTableRepository ratingTableRepository;
    
    /**
     * Sets up rating table test data.
     * This mirrors the production seed data from V8__Insert_rating_table_seed_data.sql
     */
    public void setupRatingTableData() {
        List<RatingTable> ratingTables = new ArrayList<>();
        
        // OC Insurance Rating Factors
        ratingTables.addAll(createOCRatingTables());
        
        // AC Insurance Rating Factors
        ratingTables.addAll(createACRatingTables());
        
        // NNW Insurance Rating Factors
        ratingTables.addAll(createNNWRatingTables());
        
        // Save all rating tables
        ratingTableRepository.saveAll(ratingTables);
    }
    
    private List<RatingTable> createOCRatingTables() {
        List<RatingTable> tables = new ArrayList<>();
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        
        // Vehicle Age Factors (0-10 years)
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_0", "0.9000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_1", "0.9500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_2", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_3", "1.0500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_4", "1.1000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_5", "1.1500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_6", "1.2000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_7", "1.2500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_8", "1.3000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_9", "1.3500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "VEHICLE_AGE_10", "1.4000", validFrom));
        
        // Engine Capacity Factors
        tables.add(createRatingTable(InsuranceType.OC, "ENGINE_SMALL", "0.8500", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "ENGINE_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "ENGINE_LARGE", "1.2000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "ENGINE_XLARGE", "1.5000", validFrom));
        
        // Power Factors
        tables.add(createRatingTable(InsuranceType.OC, "POWER_LOW", "0.9000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "POWER_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "POWER_HIGH", "1.3000", validFrom));
        tables.add(createRatingTable(InsuranceType.OC, "POWER_VERY_HIGH", "1.6000", validFrom));
        
        // Coverage Factor
        tables.add(createRatingTable(InsuranceType.OC, "OC_STANDARD", "1.0000", validFrom));
        
        return tables;
    }
    
    private List<RatingTable> createACRatingTables() {
        List<RatingTable> tables = new ArrayList<>();
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        
        // Vehicle Age Factors (different from OC due to depreciation)
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_0", "1.2000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_1", "1.1500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_2", "1.1000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_3", "1.0500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_4", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_5", "0.9500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_6", "0.9000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_7", "0.8500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_8", "0.8000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_9", "0.7500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "VEHICLE_AGE_10", "0.7000", validFrom));
        
        // Engine Capacity Factors
        tables.add(createRatingTable(InsuranceType.AC, "ENGINE_SMALL", "0.9000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "ENGINE_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "ENGINE_LARGE", "1.1500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "ENGINE_XLARGE", "1.3000", validFrom));
        
        // Power Factors
        tables.add(createRatingTable(InsuranceType.AC, "POWER_LOW", "0.9500", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "POWER_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "POWER_HIGH", "1.2000", validFrom));
        tables.add(createRatingTable(InsuranceType.AC, "POWER_VERY_HIGH", "1.4000", validFrom));
        
        // Coverage Factor
        tables.add(createRatingTable(InsuranceType.AC, "AC_COMPREHENSIVE", "1.0000", validFrom));
        
        return tables;
    }
    
    private List<RatingTable> createNNWRatingTables() {
        List<RatingTable> tables = new ArrayList<>();
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        
        // Vehicle Age Factors (minimal impact for NNW)
        for (int age = 0; age <= 10; age++) {
            tables.add(createRatingTable(InsuranceType.NNW, "VEHICLE_AGE_" + age, "1.0000", validFrom));
        }
        
        // Engine Capacity Factors (minimal impact)
        tables.add(createRatingTable(InsuranceType.NNW, "ENGINE_SMALL", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "ENGINE_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "ENGINE_LARGE", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "ENGINE_XLARGE", "1.0000", validFrom));
        
        // Power Factors (minimal impact)
        tables.add(createRatingTable(InsuranceType.NNW, "POWER_LOW", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "POWER_MEDIUM", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "POWER_HIGH", "1.0000", validFrom));
        tables.add(createRatingTable(InsuranceType.NNW, "POWER_VERY_HIGH", "1.0000", validFrom));
        
        // Coverage Factor
        tables.add(createRatingTable(InsuranceType.NNW, "NNW_STANDARD", "1.0000", validFrom));
        
        return tables;
    }
    
    private RatingTable createRatingTable(InsuranceType insuranceType, String ratingKey, 
                                         String multiplier, LocalDate validFrom) {
        return RatingTable.builder()
                .insuranceType(insuranceType)
                .ratingKey(ratingKey)
                .multiplier(new BigDecimal(multiplier))
                .validFrom(validFrom)
                .build();
    }
}