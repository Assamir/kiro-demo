-- Create policies table
-- This table stores insurance policy core information

CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'CANCELED', 'EXPIRED')),
    insurance_type VARCHAR(10) NOT NULL CHECK (insurance_type IN ('OC', 'AC', 'NNW')),
    premium DECIMAL(10,2) NOT NULL,
    discount_surcharge DECIMAL(10,2),
    client_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    CONSTRAINT fk_policies_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_policies_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE RESTRICT
);

-- Create indexes for performance
CREATE UNIQUE INDEX idx_policies_policy_number ON policies(policy_number);
CREATE INDEX idx_policies_client_id ON policies(client_id);
CREATE INDEX idx_policies_vehicle_id ON policies(vehicle_id);
CREATE INDEX idx_policies_status ON policies(status);
CREATE INDEX idx_policies_insurance_type ON policies(insurance_type);
CREATE INDEX idx_policies_start_date ON policies(start_date);
CREATE INDEX idx_policies_end_date ON policies(end_date);
CREATE INDEX idx_policies_issue_date ON policies(issue_date);

-- Add constraints for data validation
ALTER TABLE policies ADD CONSTRAINT chk_policies_dates_valid 
    CHECK (start_date <= end_date);
ALTER TABLE policies ADD CONSTRAINT chk_policies_issue_date_valid 
    CHECK (issue_date <= start_date);
ALTER TABLE policies ADD CONSTRAINT chk_policies_premium_positive 
    CHECK (premium >= 0);
ALTER TABLE policies ADD CONSTRAINT chk_policies_policy_number_not_empty 
    CHECK (LENGTH(TRIM(policy_number)) > 0);

-- Add comments for documentation
COMMENT ON TABLE policies IS 'Insurance policy core information';
COMMENT ON COLUMN policies.policy_number IS 'Unique policy identifier';
COMMENT ON COLUMN policies.issue_date IS 'Date when policy was issued';
COMMENT ON COLUMN policies.start_date IS 'Policy coverage start date';
COMMENT ON COLUMN policies.end_date IS 'Policy coverage end date';
COMMENT ON COLUMN policies.status IS 'Policy status: ACTIVE, CANCELED, or EXPIRED';
COMMENT ON COLUMN policies.insurance_type IS 'Type of insurance: OC (liability), AC (comprehensive), NNW (accident)';
COMMENT ON COLUMN policies.premium IS 'Base premium amount in currency units';
COMMENT ON COLUMN policies.discount_surcharge IS 'Additional discount (negative) or surcharge (positive) amount';