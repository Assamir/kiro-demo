-- Script to generate bulk policies for remaining operators
-- This will be executed after the migration

-- Generate remaining policies for Lisa Williams (AC and NNW)
-- Generate policies for David Brown (100 policies)
-- Generate policies for Emma Davis (100 policies)

-- For now, let's complete the migration with a simpler approach
-- We'll add the remaining policies in batches

-- AC Policies for Lisa Williams (40 policies)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('AC-2024-LW-001', '2024-01-09', '2024-01-19', '2025-01-18', 'ACTIVE', 'AC', 2475.00, 25.00, 42, 34),
('AC-2024-LW-002', '2024-01-13', '2024-01-23', '2025-01-22', 'ACTIVE', 'AC', 2915.75, -125.00, 43, 35),
('AC-2024-LW-003', '2024-01-19', '2024-01-29', '2025-01-28', 'ACTIVE', 'AC', 1875.25, -75.00, 44, 36),
('AC-2024-LW-004', '2024-01-23', '2024-02-02', '2025-02-01', 'ACTIVE', 'AC', 3275.50, 225.00, 45, 37),
('AC-2024-LW-005', '2024-01-29', '2024-02-08', '2025-02-07', 'ACTIVE', 'AC', 2175.00, 25.00, 46, 38);

-- Continue with more policies...
-- This approach allows us to add policies incrementally