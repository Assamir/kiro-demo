-- Add additional performance indexes and constraints for data integrity
-- This migration adds composite indexes and additional constraints for optimal query performance

-- Composite indexes for common query patterns
CREATE INDEX idx_policies_client_status ON policies(client_id, status);
CREATE INDEX idx_policies_vehicle_status ON policies(vehicle_id, status);
CREATE INDEX idx_policies_type_status ON policies(insurance_type, status);
CREATE INDEX idx_policies_date_range ON policies(start_date, end_date);

-- Indexes for policy search by client (common use case)
CREATE INDEX idx_policies_client_type ON policies(client_id, insurance_type);
CREATE INDEX idx_policies_client_dates ON policies(client_id, start_date, end_date);

-- Indexes for rating table lookups (performance critical)
CREATE INDEX idx_rating_tables_lookup ON rating_tables(insurance_type, rating_key);
CREATE INDEX idx_rating_tables_validity ON rating_tables(valid_from, valid_to);

-- Additional constraints for business rules
ALTER TABLE policies ADD CONSTRAINT chk_policies_coverage_period_reasonable 
    CHECK (end_date <= start_date + INTERVAL '5 years');

-- Constraint to ensure policy details exist for all policies
-- This will be enforced at application level, but documented here
COMMENT ON TABLE policy_details IS 'Every policy must have corresponding policy details. This is enforced at application level.';

-- Add trigger to automatically update updated_at timestamp for users
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for business rules documentation
COMMENT ON CONSTRAINT chk_policies_coverage_period_reasonable ON policies IS 
    'Ensures policy coverage period does not exceed 5 years, which is reasonable for car insurance';

COMMENT ON INDEX idx_policies_client_status IS 
    'Optimizes queries for finding client policies by status (common in policy management)';

COMMENT ON INDEX idx_rating_tables_lookup IS 
    'Optimizes rating table lookups for performance critical operations';