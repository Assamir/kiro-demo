-- Create policy_details table
-- This table stores insurance type specific details for policies

CREATE TABLE policy_details (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    
    -- OC Insurance specific fields
    guaranteed_sum DECIMAL(12,2),
    coverage_area VARCHAR(100),
    
    -- AC Insurance specific fields
    ac_variant VARCHAR(20) CHECK (ac_variant IN ('STANDARD', 'MAXIMUM')),
    sum_insured DECIMAL(12,2),
    coverage_scope TEXT,
    deductible DECIMAL(10,2),
    workshop_type VARCHAR(50),
    
    -- NNW Insurance specific fields
    covered_persons TEXT,
    
    CONSTRAINT fk_policy_details_policy FOREIGN KEY (policy_id) REFERENCES policies(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE UNIQUE INDEX idx_policy_details_policy_id ON policy_details(policy_id);
CREATE INDEX idx_policy_details_ac_variant ON policy_details(ac_variant);

-- Add constraints for data validation
ALTER TABLE policy_details ADD CONSTRAINT chk_policy_details_sums_positive 
    CHECK (
        (guaranteed_sum IS NULL OR guaranteed_sum > 0) AND
        (sum_insured IS NULL OR sum_insured > 0) AND
        (deductible IS NULL OR deductible >= 0)
    );

-- Add comments for documentation
COMMENT ON TABLE policy_details IS 'Insurance type specific details for policies';
COMMENT ON COLUMN policy_details.policy_id IS 'Reference to the main policy record';
COMMENT ON COLUMN policy_details.guaranteed_sum IS 'OC Insurance: Guaranteed coverage amount';
COMMENT ON COLUMN policy_details.coverage_area IS 'OC Insurance: Geographic coverage area';
COMMENT ON COLUMN policy_details.ac_variant IS 'AC Insurance: Coverage variant (STANDARD or MAXIMUM)';
COMMENT ON COLUMN policy_details.sum_insured IS 'AC/NNW Insurance: Maximum coverage amount';
COMMENT ON COLUMN policy_details.coverage_scope IS 'AC Insurance: Detailed coverage description';
COMMENT ON COLUMN policy_details.deductible IS 'AC Insurance: Deductible amount for claims';
COMMENT ON COLUMN policy_details.workshop_type IS 'AC Insurance: Authorized workshop type for repairs';
COMMENT ON COLUMN policy_details.covered_persons IS 'NNW Insurance: Description of covered persons';