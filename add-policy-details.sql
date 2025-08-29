-- Add policy details for the new policies
-- Get the policy IDs for the new policies and add corresponding details

-- Add policy details for OC policies (liability insurance)
INSERT INTO policy_details (policy_id, guaranteed_sum, coverage_area, ac_variant, sum_insured, coverage_scope, deductible, workshop_type, covered_persons)
SELECT 
    p.id,
    CASE 
        WHEN (p.id % 3) = 0 THEN 6000000.00
        WHEN (p.id % 3) = 1 THEN 5000000.00
        ELSE 7000000.00
    END,
    CASE 
        WHEN (p.id % 2) = 0 THEN 'Europe'
        ELSE 'Poland'
    END,
    NULL, NULL, NULL, NULL, NULL, NULL
FROM policies p 
WHERE p.insurance_type = 'OC' 
AND p.policy_number LIKE '%MJ%' OR p.policy_number LIKE '%LW%' OR p.policy_number LIKE '%DB%' OR p.policy_number LIKE '%ED%'
AND p.id NOT IN (SELECT policy_id FROM policy_details);

-- Add policy details for AC policies (comprehensive insurance)
INSERT INTO policy_details (policy_id, guaranteed_sum, coverage_area, ac_variant, sum_insured, coverage_scope, deductible, workshop_type, covered_persons)
SELECT 
    p.id,
    NULL, NULL,
    CASE 
        WHEN (p.id % 3) = 0 THEN 'MAXIMUM'
        WHEN (p.id % 3) = 1 THEN 'STANDARD'
        ELSE 'STANDARD'
    END,
    CASE 
        WHEN (p.id % 4) = 0 THEN 250000.00
        WHEN (p.id % 4) = 1 THEN 180000.00
        WHEN (p.id % 4) = 2 THEN 150000.00
        ELSE 200000.00
    END,
    CASE 
        WHEN (p.id % 3) = 0 THEN 'Full comprehensive coverage with extended protection'
        WHEN (p.id % 3) = 1 THEN 'Standard comprehensive coverage'
        ELSE 'Comprehensive coverage including theft, fire, vandalism'
    END,
    CASE 
        WHEN (p.id % 5) = 0 THEN 500.00
        WHEN (p.id % 5) = 1 THEN 300.00
        WHEN (p.id % 5) = 2 THEN 750.00
        WHEN (p.id % 5) = 3 THEN 400.00
        ELSE 600.00
    END,
    CASE 
        WHEN (p.id % 2) = 0 THEN 'Authorized'
        ELSE 'Any workshop'
    END,
    NULL
FROM policies p 
WHERE p.insurance_type = 'AC' 
AND (p.policy_number LIKE '%MJ%' OR p.policy_number LIKE '%LW%' OR p.policy_number LIKE '%DB%' OR p.policy_number LIKE '%ED%')
AND p.id NOT IN (SELECT policy_id FROM policy_details);

-- Add policy details for NNW policies (personal accident insurance)
INSERT INTO policy_details (policy_id, guaranteed_sum, coverage_area, ac_variant, sum_insured, coverage_scope, deductible, workshop_type, covered_persons)
SELECT 
    p.id,
    NULL, NULL, NULL,
    CASE 
        WHEN (p.id % 4) = 0 THEN 150000.00
        WHEN (p.id % 4) = 1 THEN 100000.00
        WHEN (p.id % 4) = 2 THEN 120000.00
        ELSE 130000.00
    END,
    NULL, NULL, NULL,
    CASE 
        WHEN (p.id % 4) = 0 THEN 'Driver and passengers (max 5 persons)'
        WHEN (p.id % 4) = 1 THEN 'Driver only'
        WHEN (p.id % 4) = 2 THEN 'Driver and family members'
        ELSE 'Driver and passengers (max 7 persons)'
    END
FROM policies p 
WHERE p.insurance_type = 'NNW' 
AND (p.policy_number LIKE '%MJ%' OR p.policy_number LIKE '%LW%' OR p.policy_number LIKE '%DB%' OR p.policy_number LIKE '%ED%')
AND p.id NOT IN (SELECT policy_id FROM policy_details);