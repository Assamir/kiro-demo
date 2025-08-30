-- Add coverage fields to policies table
-- Migration: V19__Add_policy_coverage_fields.sql
-- Description: Add amount_guaranteed and coverage_area fields to support additional policy details

ALTER TABLE policies 
ADD COLUMN amount_guaranteed DECIMAL(12,2),
ADD COLUMN coverage_area VARCHAR(500);

-- Add comments for documentation
COMMENT ON COLUMN policies.amount_guaranteed IS 'The guaranteed amount for policy coverage';
COMMENT ON COLUMN policies.coverage_area IS 'The geographical area covered by the policy';

-- Create index for coverage area searches (optional, for performance)
CREATE INDEX idx_policies_coverage_area ON policies(coverage_area) WHERE coverage_area IS NOT NULL;