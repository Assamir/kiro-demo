-- Create vehicles table
-- This table stores vehicle technical and registration information

CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year_of_manufacture INTEGER NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    vin VARCHAR(17) UNIQUE NOT NULL,
    engine_capacity INTEGER NOT NULL,
    power INTEGER NOT NULL,
    first_registration_date DATE NOT NULL
);

-- Create indexes for performance and data integrity
CREATE UNIQUE INDEX idx_vehicles_registration_number ON vehicles(registration_number);
CREATE UNIQUE INDEX idx_vehicles_vin ON vehicles(vin);
CREATE INDEX idx_vehicles_make_model ON vehicles(make, model);
CREATE INDEX idx_vehicles_year ON vehicles(year_of_manufacture);

-- Add constraints for data validation
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_year_valid 
    CHECK (year_of_manufacture >= 1900 AND year_of_manufacture <= EXTRACT(YEAR FROM CURRENT_DATE) + 1);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_vin_length 
    CHECK (LENGTH(vin) = 17 AND vin ~ '^[A-HJ-NPR-Z0-9]+$');
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_engine_capacity_positive 
    CHECK (engine_capacity > 0);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_power_positive 
    CHECK (power > 0);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_registration_date_valid 
    CHECK (first_registration_date <= CURRENT_DATE);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_make_not_empty 
    CHECK (LENGTH(TRIM(make)) > 0);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_model_not_empty 
    CHECK (LENGTH(TRIM(model)) > 0);

-- Add comments for documentation
COMMENT ON TABLE vehicles IS 'Vehicle technical and registration information';
COMMENT ON COLUMN vehicles.vin IS 'Vehicle Identification Number (17 characters, excluding I, O, Q)';
COMMENT ON COLUMN vehicles.registration_number IS 'Official vehicle registration number';
COMMENT ON COLUMN vehicles.engine_capacity IS 'Engine displacement in cubic centimeters (cc)';
COMMENT ON COLUMN vehicles.power IS 'Engine power in kilowatts (kW)';
COMMENT ON COLUMN vehicles.first_registration_date IS 'Date when vehicle was first registered';