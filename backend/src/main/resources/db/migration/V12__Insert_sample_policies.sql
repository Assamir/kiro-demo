-- Insert sample policies for testing and demonstration
-- This migration creates realistic policy data covering all insurance types and scenarios

-- Sample OC (Liability) Policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-001001', '2024-01-15', '2024-02-01', '2025-01-31', 'ACTIVE', 'OC', 850.00, 0.00, 1, 1),
('OC-2024-001002', '2024-01-20', '2024-02-15', '2025-02-14', 'ACTIVE', 'OC', 920.50, -50.00, 2, 2),
('OC-2024-001003', '2024-02-10', '2024-03-01', '2025-02-28', 'ACTIVE', 'OC', 1150.75, 100.00, 3, 3),
('OC-2024-001004', '2024-02-25', '2024-03-15', '2025-03-14', 'ACTIVE', 'OC', 780.25, -75.00, 4, 4),
('OC-2024-001005', '2024-03-05', '2024-04-01', '2025-03-31', 'ACTIVE', 'OC', 1050.00, 0.00, 5, 5),

-- Sample AC (Comprehensive) Policies
('AC-2024-002001', '2024-01-18', '2024-02-05', '2025-02-04', 'ACTIVE', 'AC', 2450.00, 0.00, 6, 6),
('AC-2024-002002', '2024-01-25', '2024-02-20', '2025-02-19', 'ACTIVE', 'AC', 2890.75, -150.00, 7, 7),
('AC-2024-002003', '2024-02-12', '2024-03-05', '2025-03-04', 'ACTIVE', 'AC', 3250.50, 200.00, 8, 8),
('AC-2024-002004', '2024-02-28', '2024-03-20', '2025-03-19', 'ACTIVE', 'AC', 1850.25, -100.00, 9, 9),
('AC-2024-002005', '2024-03-08', '2024-04-05', '2025-04-04', 'ACTIVE', 'AC', 2150.00, 0.00, 10, 10),

-- Sample NNW (Personal Accident) Policies
('NNW-2024-003001', '2024-01-22', '2024-02-10', '2025-02-09', 'ACTIVE', 'NNW', 180.00, 0.00, 11, 11),
('NNW-2024-003002', '2024-02-05', '2024-02-25', '2025-02-24', 'ACTIVE', 'NNW', 220.50, -20.00, 12, 12),
('NNW-2024-003003', '2024-02-15', '2024-03-10', '2025-03-09', 'ACTIVE', 'NNW', 195.75, 15.00, 13, 13),
('NNW-2024-003004', '2024-03-01', '2024-03-25', '2025-03-24', 'ACTIVE', 'NNW', 165.25, -25.00, 14, 14),
('NNW-2024-003005', '2024-03-12', '2024-04-10', '2025-04-09', 'ACTIVE', 'NNW', 210.00, 0.00, 15, 15),

-- Canceled policies for testing policy lifecycle
('OC-2023-004001', '2023-06-15', '2023-07-01', '2024-06-30', 'CANCELED', 'OC', 890.00, 0.00, 16, 16),
('AC-2023-004002', '2023-08-20', '2023-09-01', '2024-08-31', 'CANCELED', 'AC', 2650.00, -100.00, 17, 17),
('NNW-2023-004003', '2023-10-10', '2023-11-01', '2024-10-31', 'CANCELED', 'NNW', 175.00, 0.00, 18, 18),

-- Expired policies for testing
('OC-2022-005001', '2022-03-15', '2022-04-01', '2023-03-31', 'EXPIRED', 'OC', 820.00, -50.00, 19, 19),
('AC-2022-005002', '2022-05-20', '2022-06-01', '2023-05-31', 'EXPIRED', 'AC', 2350.00, 0.00, 20, 20),

-- Multiple policies for same client (renewal scenarios)
('OC-2024-006001', '2024-01-10', '2024-02-01', '2025-01-31', 'ACTIVE', 'OC', 875.00, -25.00, 1, 21),
('AC-2024-006002', '2024-02-15', '2024-03-01', '2025-02-28', 'ACTIVE', 'AC', 2750.00, 0.00, 2, 22),

-- High-value policies for premium testing
('AC-2024-007001', '2024-01-30', '2024-02-15', '2025-02-14', 'ACTIVE', 'AC', 4500.00, 300.00, 3, 15),
('AC-2024-007002', '2024-02-20', '2024-03-10', '2025-03-09', 'ACTIVE', 'AC', 4850.75, 250.00, 4, 16),

-- Policies with significant discounts/surcharges
('OC-2024-008001', '2024-03-01', '2024-03-20', '2025-03-19', 'ACTIVE', 'OC', 950.00, -200.00, 5, 23),
('AC-2024-008002', '2024-03-10', '2024-04-01', '2025-03-31', 'ACTIVE', 'AC', 3200.00, 400.00, 6, 24),

-- Future-dated policies for testing
('OC-2024-009001', '2024-03-25', '2024-05-01', '2025-04-30', 'ACTIVE', 'OC', 825.00, 0.00, 7, 25),
('AC-2024-009002', '2024-03-28', '2024-05-15', '2025-05-14', 'ACTIVE', 'AC', 2950.00, -75.00, 8, 26);

-- Insert corresponding policy details for each policy
-- OC Policy Details
INSERT INTO policy_details (policy_id, guaranteed_sum, coverage_area, ac_variant, sum_insured, coverage_scope, deductible, workshop_type, covered_persons) VALUES
(1, 5000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),
(2, 5000000.00, 'Poland', NULL, NULL, NULL, NULL, NULL, NULL),
(3, 6000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),
(4, 5000000.00, 'Poland', NULL, NULL, NULL, NULL, NULL, NULL),
(5, 5000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),

-- AC Policy Details
(6, NULL, NULL, 'STANDARD', 150000.00, 'Comprehensive coverage including theft, fire, vandalism', 500.00, 'Authorized', NULL),
(7, NULL, NULL, 'MAXIMUM', 200000.00, 'Full comprehensive coverage with extended protection', 300.00, 'Authorized', NULL),
(8, NULL, NULL, 'MAXIMUM', 250000.00, 'Premium comprehensive coverage with all risks', 200.00, 'Any workshop', NULL),
(9, NULL, NULL, 'STANDARD', 120000.00, 'Standard comprehensive coverage', 750.00, 'Authorized', NULL),
(10, NULL, NULL, 'STANDARD', 140000.00, 'Comprehensive coverage with basic protection', 600.00, 'Authorized', NULL),

-- NNW Policy Details
(11, NULL, NULL, NULL, 100000.00, NULL, NULL, NULL, 'Driver and passengers (max 5 persons)'),
(12, NULL, NULL, NULL, 150000.00, NULL, NULL, NULL, 'Driver and family members'),
(13, NULL, NULL, NULL, 120000.00, NULL, NULL, NULL, 'Driver only'),
(14, NULL, NULL, NULL, 80000.00, NULL, NULL, NULL, 'Driver and spouse'),
(15, NULL, NULL, NULL, 130000.00, NULL, NULL, NULL, 'Driver and passengers (max 7 persons)'),

-- Canceled policy details
(16, 5000000.00, 'Poland', NULL, NULL, NULL, NULL, NULL, NULL),
(17, NULL, NULL, 'STANDARD', 180000.00, 'Comprehensive coverage', 500.00, 'Authorized', NULL),
(18, NULL, NULL, NULL, 90000.00, NULL, NULL, NULL, 'Driver and passengers'),

-- Expired policy details
(19, 5000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),
(20, NULL, NULL, 'MAXIMUM', 220000.00, 'Full comprehensive coverage', 400.00, 'Any workshop', NULL),

-- Renewal policy details
(21, 5000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),
(22, NULL, NULL, 'STANDARD', 160000.00, 'Comprehensive coverage with standard protection', 500.00, 'Authorized', NULL),

-- High-value policy details
(23, NULL, NULL, 'MAXIMUM', 350000.00, 'Premium comprehensive coverage for luxury vehicle', 100.00, 'Authorized premium', NULL),
(24, NULL, NULL, 'MAXIMUM', 380000.00, 'Exclusive comprehensive coverage', 150.00, 'Authorized premium', NULL),

-- Discount/surcharge policy details
(25, 5000000.00, 'Poland', NULL, NULL, NULL, NULL, NULL, NULL),
(26, NULL, NULL, 'STANDARD', 190000.00, 'Comprehensive coverage with additional risks', 400.00, 'Authorized', NULL),

-- Future policy details
(27, 5000000.00, 'Europe', NULL, NULL, NULL, NULL, NULL, NULL),
(28, NULL, NULL, 'STANDARD', 170000.00, 'Standard comprehensive coverage', 550.00, 'Authorized', NULL);

-- Comments for documentation
COMMENT ON TABLE policies IS 'Sample policy data covering all insurance types, statuses, and business scenarios';
COMMENT ON TABLE policy_details IS 'Sample policy details with insurance-type specific information';

-- Note: Policy numbers follow the format: [TYPE]-[YEAR]-[SEQUENCE]
-- Premium calculations are based on realistic market rates for Polish insurance market
-- All dates are set to provide a mix of current, past, and future policies for comprehensive testing