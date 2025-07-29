-- Create rating_tables table
-- This table stores rating factors and multipliers for premium calculations

CREATE TABLE rating_tables (
    id BIGSERIAL PRIMARY KEY,
    insurance_type VARCHAR(10) NOT NULL CHECK (insurance_type IN ('OC', 'AC', 'NNW')),
    rating_key VARCHAR(100) NOT NULL,
    multiplier DECIMAL(5,4) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE
);

-- Create indexes for performance
CREATE INDEX idx_rating_tables_insurance_type ON rating_tables(insurance_type);
CREATE INDEX idx_rating_tables_rating_key ON rating_tables(rating_key);
CREATE INDEX idx_rating_tables_valid_from ON rating_tables(valid_from);
CREATE INDEX idx_rating_tables_valid_to ON rating_tables(valid_to);
CREATE INDEX idx_rating_tables_composite ON rating_tables(insurance_type, rating_key, valid_from);

-- Add constraints for data validation
ALTER TABLE rating_tables ADD CONSTRAINT chk_rating_tables_multiplier_positive 
    CHECK (multiplier > 0);
ALTER TABLE rating_tables ADD CONSTRAINT chk_rating_tables_dates_valid 
    CHECK (valid_to IS NULL OR valid_from <= valid_to);
ALTER TABLE rating_tables ADD CONSTRAINT chk_rating_tables_rating_key_not_empty 
    CHECK (LENGTH(TRIM(rating_key)) > 0);

-- Create unique constraint to prevent duplicate active rating entries
CREATE UNIQUE INDEX idx_rating_tables_unique_active 
    ON rating_tables(insurance_type, rating_key, valid_from) 
    WHERE valid_to IS NULL;

-- Add comments for documentation
COMMENT ON TABLE rating_tables IS 'Rating factors and multipliers for premium calculations';
COMMENT ON COLUMN rating_tables.insurance_type IS 'Type of insurance this rating applies to';
COMMENT ON COLUMN rating_tables.rating_key IS 'Identifier for the rating factor (e.g., driver_age_25_30, vehicle_age_0_3)';
COMMENT ON COLUMN rating_tables.multiplier IS 'Multiplier factor applied to base premium (e.g., 1.2000 for 20% increase)';
COMMENT ON COLUMN rating_tables.valid_from IS 'Date from which this rating is effective';
COMMENT ON COLUMN rating_tables.valid_to IS 'Date until which this rating is effective (NULL for indefinite)';