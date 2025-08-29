-- Add more policies to reach closer to 100 per operator
-- This adds 75 more policies for each operator (bringing total to 100 each)

-- Generate additional policies for Mike Johnson (75 more policies)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'OC-2024-MJ-' || LPAD((ROW_NUMBER() OVER() + 10)::text, 3, '0'),
    '2024-03-01'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-14'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'OC',
    750.00 + (ROW_NUMBER() OVER() % 500),
    -100.00 + (ROW_NUMBER() OVER() % 200),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'AC-2024-MJ-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-01'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-14'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'AC',
    2000.00 + (ROW_NUMBER() OVER() % 1500),
    -200.00 + (ROW_NUMBER() OVER() % 400),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'NNW-2024-MJ-' || LPAD((ROW_NUMBER() OVER() + 7)::text, 3, '0'),
    '2024-03-01'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-14'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'NNW',
    150.00 + (ROW_NUMBER() OVER() % 100),
    -30.00 + (ROW_NUMBER() OVER() % 60),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 15);

-- Generate additional policies for Lisa Williams (75 more policies)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'OC-2024-LW-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-02'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'OC',
    760.00 + (ROW_NUMBER() OVER() % 480),
    -90.00 + (ROW_NUMBER() OVER() % 180),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'AC-2024-LW-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-02'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'AC',
    2100.00 + (ROW_NUMBER() OVER() % 1400),
    -180.00 + (ROW_NUMBER() OVER() % 360),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'NNW-2024-LW-' || LPAD((ROW_NUMBER() OVER() + 9)::text, 3, '0'),
    '2024-03-02'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-15'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'NNW',
    160.00 + (ROW_NUMBER() OVER() % 90),
    -25.00 + (ROW_NUMBER() OVER() % 50),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 15);

-- Generate additional policies for David Brown (75 more policies)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'OC-2024-DB-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-03'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'OC',
    770.00 + (ROW_NUMBER() OVER() % 460),
    -80.00 + (ROW_NUMBER() OVER() % 160),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'AC-2024-DB-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-03'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'AC',
    2200.00 + (ROW_NUMBER() OVER() % 1300),
    -160.00 + (ROW_NUMBER() OVER() % 320),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'NNW-2024-DB-' || LPAD((ROW_NUMBER() OVER() + 9)::text, 3, '0'),
    '2024-03-03'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-16'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'NNW',
    170.00 + (ROW_NUMBER() OVER() % 80),
    -20.00 + (ROW_NUMBER() OVER() % 40),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 15);

-- Generate additional policies for Emma Davis (75 more policies)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'OC-2024-ED-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-04'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-18'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'OC',
    780.00 + (ROW_NUMBER() OVER() % 440),
    -70.00 + (ROW_NUMBER() OVER() % 140),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'AC-2024-ED-' || LPAD((ROW_NUMBER() OVER() + 8)::text, 3, '0'),
    '2024-03-04'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-18'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'AC',
    2300.00 + (ROW_NUMBER() OVER() % 1200),
    -140.00 + (ROW_NUMBER() OVER() % 280),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 30);

INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) 
SELECT 
    'NNW-2024-ED-' || LPAD((ROW_NUMBER() OVER() + 9)::text, 3, '0'),
    '2024-03-04'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2024-03-18'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    '2025-03-17'::date + (ROW_NUMBER() OVER() % 120) * INTERVAL '1 day',
    'ACTIVE',
    'NNW',
    180.00 + (ROW_NUMBER() OVER() % 70),
    -15.00 + (ROW_NUMBER() OVER() % 30),
    ((ROW_NUMBER() OVER() % 21) + 1),
    ((ROW_NUMBER() OVER() % 33) + 1)
FROM generate_series(1, 15);