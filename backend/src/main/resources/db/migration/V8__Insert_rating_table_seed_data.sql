-- Insert seed data for rating tables
-- This migration populates the rating_tables with initial rating factors for all insurance types

-- OC Insurance Rating Factors
-- Vehicle Age Factors (0-10 years)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'VEHICLE_AGE_0', 0.9000, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_1', 0.9500, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_2', 1.0000, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_3', 1.0500, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_4', 1.1000, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_5', 1.1500, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_6', 1.2000, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_7', 1.2500, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_8', 1.3000, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_9', 1.3500, '2024-01-01', NULL),
('OC', 'VEHICLE_AGE_10', 1.4000, '2024-01-01', NULL);

-- Engine Capacity Factors for OC
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'ENGINE_SMALL', 0.8500, '2024-01-01', NULL),    -- <= 1000cc
('OC', 'ENGINE_MEDIUM', 1.0000, '2024-01-01', NULL),   -- 1001-1600cc
('OC', 'ENGINE_LARGE', 1.2000, '2024-01-01', NULL),    -- 1601-2000cc
('OC', 'ENGINE_XLARGE', 1.5000, '2024-01-01', NULL);   -- > 2000cc

-- Power Factors for OC
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'POWER_LOW', 0.9000, '2024-01-01', NULL),       -- <= 75 HP
('OC', 'POWER_MEDIUM', 1.0000, '2024-01-01', NULL),    -- 76-150 HP
('OC', 'POWER_HIGH', 1.3000, '2024-01-01', NULL),      -- 151-250 HP
('OC', 'POWER_VERY_HIGH', 1.6000, '2024-01-01', NULL); -- > 250 HP

-- OC Coverage Factor
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'OC_STANDARD', 1.0000, '2024-01-01', NULL);

-- AC Insurance Rating Factors
-- Vehicle Age Factors for AC (different from OC due to vehicle value depreciation)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('AC', 'VEHICLE_AGE_0', 1.2000, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_1', 1.1500, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_2', 1.1000, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_3', 1.0500, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_4', 1.0000, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_5', 0.9500, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_6', 0.9000, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_7', 0.8500, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_8', 0.8000, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_9', 0.7500, '2024-01-01', NULL),
('AC', 'VEHICLE_AGE_10', 0.7000, '2024-01-01', NULL);

-- Engine Capacity Factors for AC
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('AC', 'ENGINE_SMALL', 0.9000, '2024-01-01', NULL),    -- <= 1000cc
('AC', 'ENGINE_MEDIUM', 1.0000, '2024-01-01', NULL),   -- 1001-1600cc
('AC', 'ENGINE_LARGE', 1.1500, '2024-01-01', NULL),    -- 1601-2000cc
('AC', 'ENGINE_XLARGE', 1.3000, '2024-01-01', NULL);   -- > 2000cc

-- Power Factors for AC
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('AC', 'POWER_LOW', 0.9500, '2024-01-01', NULL),       -- <= 75 HP
('AC', 'POWER_MEDIUM', 1.0000, '2024-01-01', NULL),    -- 76-150 HP
('AC', 'POWER_HIGH', 1.2000, '2024-01-01', NULL),      -- 151-250 HP
('AC', 'POWER_VERY_HIGH', 1.4000, '2024-01-01', NULL); -- > 250 HP

-- AC Coverage Factor
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('AC', 'AC_COMPREHENSIVE', 1.0000, '2024-01-01', NULL);

-- NNW Insurance Rating Factors
-- Vehicle Age Factors for NNW (minimal impact as it's personal accident insurance)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('NNW', 'VEHICLE_AGE_0', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_1', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_2', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_3', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_4', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_5', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_6', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_7', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_8', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_9', 1.0000, '2024-01-01', NULL),
('NNW', 'VEHICLE_AGE_10', 1.0000, '2024-01-01', NULL);

-- Engine Capacity Factors for NNW (minimal impact)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('NNW', 'ENGINE_SMALL', 1.0000, '2024-01-01', NULL),
('NNW', 'ENGINE_MEDIUM', 1.0000, '2024-01-01', NULL),
('NNW', 'ENGINE_LARGE', 1.0000, '2024-01-01', NULL),
('NNW', 'ENGINE_XLARGE', 1.0000, '2024-01-01', NULL);

-- Power Factors for NNW (minimal impact)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('NNW', 'POWER_LOW', 1.0000, '2024-01-01', NULL),
('NNW', 'POWER_MEDIUM', 1.0000, '2024-01-01', NULL),
('NNW', 'POWER_HIGH', 1.0000, '2024-01-01', NULL),
('NNW', 'POWER_VERY_HIGH', 1.0000, '2024-01-01', NULL);

-- NNW Coverage Factor
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('NNW', 'NNW_STANDARD', 1.0000, '2024-01-01', NULL);

-- Additional rating factors for business rules validation

-- Seasonal factors (example of time-based rating)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'SEASONAL_WINTER', 1.1000, '2024-12-01', '2025-02-28'),
('AC', 'SEASONAL_WINTER', 1.2000, '2024-12-01', '2025-02-28'),
('NNW', 'SEASONAL_WINTER', 1.0500, '2024-12-01', '2025-02-28');

-- Regional factors (example of location-based rating)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'REGION_URBAN', 1.2000, '2024-01-01', NULL),
('OC', 'REGION_SUBURBAN', 1.0000, '2024-01-01', NULL),
('OC', 'REGION_RURAL', 0.8500, '2024-01-01', NULL),
('AC', 'REGION_URBAN', 1.3000, '2024-01-01', NULL),
('AC', 'REGION_SUBURBAN', 1.0000, '2024-01-01', NULL),
('AC', 'REGION_RURAL', 0.9000, '2024-01-01', NULL),
('NNW', 'REGION_URBAN', 1.1000, '2024-01-01', NULL),
('NNW', 'REGION_SUBURBAN', 1.0000, '2024-01-01', NULL),
('NNW', 'REGION_RURAL', 0.9500, '2024-01-01', NULL);

-- Historical rating factors (example of expired ratings for testing)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'HISTORICAL_FACTOR', 1.5000, '2023-01-01', '2023-12-31'),
('AC', 'HISTORICAL_FACTOR', 1.4000, '2023-01-01', '2023-12-31'),
('NNW', 'HISTORICAL_FACTOR', 1.2000, '2023-01-01', '2023-12-31');

-- Future rating factors (example of future-effective ratings for testing)
INSERT INTO rating_tables (insurance_type, rating_key, multiplier, valid_from, valid_to) VALUES
('OC', 'FUTURE_FACTOR', 1.1500, '2026-01-01', NULL),
('AC', 'FUTURE_FACTOR', 1.2500, '2026-01-01', NULL),
('NNW', 'FUTURE_FACTOR', 1.0800, '2026-01-01', NULL);

-- Comments for documentation
COMMENT ON TABLE rating_tables IS 'Rating factors and multipliers for premium calculations - populated with seed data';