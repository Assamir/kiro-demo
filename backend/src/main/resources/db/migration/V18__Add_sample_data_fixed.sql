-- Migration V18: Add sample data for operators (fixed VINs)
-- This migration adds sample policies for each operator to demonstrate the system

-- Add some additional clients using the correct column structure and unique PESEL numbers
INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
('Anna Kowalska', '85010112346', 'ul. Marszałkowska 1/2, 00-624 Warszawa', 'anna.kowalska@email.com', '+48123456789'),
('Piotr Nowak', '90020223457', 'ul. Floriańska 15, 31-019 Kraków', 'piotr.nowak@email.com', '+48234567890'),
('Maria Wiśniewska', '88030334568', 'ul. Długa 22, 80-827 Gdańsk', 'maria.wisniewska@email.com', '+48345678901'),
('Tomasz Wójcik', '92040445679', 'ul. Piotrkowska 104, 90-926 Łódź', 'tomasz.wojcik@email.com', '+48456789012'),
('Katarzyna Kowalczyk', '87050556780', 'ul. Świdnicka 53, 50-068 Wrocław', 'katarzyna.kowalczyk@email.com', '+48567890123'),
('Michał Kamiński', '91060667891', 'ul. Święty Marcin 29, 61-806 Poznań', 'michal.kaminski@email.com', '+48678901234'),
('Agnieszka Lewandowska', '89070778902', 'ul. 3 Maja 12, 40-096 Katowice', 'agnieszka.lewandowska@email.com', '+48789012345'),
('Paweł Zieliński', '93080889013', 'ul. Kościuszki 81, 15-426 Białystok', 'pawel.zielinski@email.com', '+48890123456'),
('Magdalena Szymańska', '86090990124', 'ul. Piłsudskiego 24, 20-110 Lublin', 'magdalena.szymanska@email.com', '+48901234567'),
('Łukasz Woźniak', '94101001235', 'ul. Zwycięstwa 96, 81-451 Gdynia', 'lukasz.wozniak@email.com', '+48012345678');

-- Add more vehicles using valid VINs (exactly 17 characters, no I, O, Q allowed)
INSERT INTO vehicles (make, model, year_of_manufacture, registration_number, vin, engine_capacity, power, first_registration_date) VALUES
('Toyota', 'Corolla', 2020, 'WA12346', 'JT2BF22K5X0123456', 1600, 132, '2020-03-15'),
('Volkswagen', 'Golf', 2019, 'KR23457', 'WVWZZZ1JZ3W123457', 1400, 125, '2019-05-20'),
('Ford', 'Focus', 2021, 'GD34568', '1FADP3F20DL123458', 1500, 150, '2021-01-10'),
('Skoda', 'Octavia', 2020, 'WR56780', 'TMBJF41J502123460', 2000, 150, '2020-06-12'),
('Renault', 'Megane', 2019, 'PZ67891', 'VF1BZ0B0H47123461', 1300, 115, '2019-09-18'),
('Peugeot', '308', 2021, 'BY78902', 'VF3LCYHZJCS123462', 1200, 110, '2021-04-22'),
('Hyundai', 'i30', 2020, 'LU89013', 'KMHD35LE4EU123463', 1400, 120, '2020-07-30'),
('Kia', 'Ceed', 2019, 'GY90124', 'U5YFF24128L123464', 1600, 128, '2019-11-05'),
('Nissan', 'Qashqai', 2021, 'TA01235', 'SJNFAAJ10U2123465', 1500, 140, '2021-02-14'),
('Honda', 'Civic', 2020, 'RZ12346', '19XFC2F59KE123466', 1800, 142, '2020-10-08');

-- Add sample policies for operators (20 policies total as demonstration)
-- Using existing clients (IDs 1-20) and existing vehicles (IDs 14-33)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('POL-OP-0001-2025', '2025-01-15', '2025-02-01', '2026-02-01', 'ACTIVE', 'OC', 1200.00, NULL, 1, 14),
('POL-OP-0002-2025', '2025-01-16', '2025-02-02', '2026-02-02', 'ACTIVE', 'AC', 1800.00, -50.00, 2, 15),
('POL-OP-0003-2025', '2025-01-17', '2025-02-03', '2026-02-03', 'ACTIVE', 'OC', 1100.00, NULL, 3, 16),
('POL-OP-0004-2025', '2025-01-18', '2025-02-04', '2026-02-04', 'ACTIVE', 'NNW', 950.00, 25.00, 4, 17),
('POL-OP-0005-2025', '2025-01-19', '2025-02-05', '2026-02-05', 'ACTIVE', 'AC', 2100.00, NULL, 5, 18),
('POL-OP-0006-2025', '2025-01-20', '2025-02-06', '2026-02-06', 'ACTIVE', 'OC', 1350.00, -75.00, 6, 19),
('POL-OP-0007-2025', '2025-01-21', '2025-02-07', '2026-02-07', 'ACTIVE', 'AC', 1950.00, NULL, 7, 20),
('POL-OP-0008-2025', '2025-01-22', '2025-02-08', '2026-02-08', 'ACTIVE', 'OC', 1250.00, NULL, 8, 21),
('POL-OP-0009-2025', '2025-01-23', '2025-02-09', '2026-02-09', 'ACTIVE', 'NNW', 875.00, 50.00, 9, 22),
('POL-OP-0010-2025', '2025-01-24', '2025-02-10', '2026-02-10', 'ACTIVE', 'AC', 2250.00, NULL, 10, 23),
('POL-OP-0011-2025', '2025-01-25', '2025-02-11', '2026-02-11', 'ACTIVE', 'OC', 1150.00, -25.00, 11, 24),
('POL-OP-0012-2025', '2025-01-26', '2025-02-12', '2026-02-12', 'ACTIVE', 'AC', 1750.00, NULL, 12, 25),
('POL-OP-0013-2025', '2025-01-27', '2025-02-13', '2026-02-13', 'ACTIVE', 'OC', 1300.00, NULL, 13, 26),
('POL-OP-0014-2025', '2025-01-28', '2025-02-14', '2026-02-14', 'ACTIVE', 'NNW', 925.00, 30.00, 14, 27),
('POL-OP-0015-2025', '2025-01-29', '2025-02-15', '2026-02-15', 'ACTIVE', 'AC', 2000.00, NULL, 15, 28),
('POL-OP-0016-2025', '2025-01-30', '2025-02-16', '2026-02-16', 'ACTIVE', 'OC', 1400.00, -100.00, 16, 29),
('POL-OP-0017-2025', '2025-01-31', '2025-02-17', '2026-02-17', 'ACTIVE', 'AC', 1850.00, NULL, 17, 30),
('POL-OP-0018-2025', '2025-02-01', '2025-02-18', '2026-02-18', 'ACTIVE', 'OC', 1175.00, NULL, 18, 31),
('POL-OP-0019-2025', '2025-02-02', '2025-02-19', '2026-02-19', 'ACTIVE', 'NNW', 800.00, 75.00, 19, 32),
('POL-OP-0020-2025', '2025-02-03', '2025-02-20', '2026-02-20', 'ACTIVE', 'AC', 2150.00, NULL, 20, 33);

-- Add policy details for all new policies using correct column structure
INSERT INTO policy_details (policy_id, guaranteed_sum, coverage_area, ac_variant, sum_insured, deductible)
SELECT 
    p.id,
    CASE p.insurance_type
        WHEN 'OC' THEN 500000.00
        WHEN 'AC' THEN 1000000.00
        WHEN 'NNW' THEN 750000.00
    END,
    'POLAND',
    CASE p.insurance_type
        WHEN 'AC' THEN 'STANDARD'
        ELSE NULL
    END,
    CASE p.insurance_type
        WHEN 'OC' THEN 500000.00
        WHEN 'AC' THEN 1000000.00
        WHEN 'NNW' THEN 750000.00
    END,
    CASE p.insurance_type
        WHEN 'OC' THEN 0.00
        WHEN 'AC' THEN 500.00
        WHEN 'NNW' THEN 300.00
    END
FROM policies p
WHERE p.policy_number LIKE 'POL-OP-%'
AND p.id NOT IN (SELECT DISTINCT policy_id FROM policy_details WHERE policy_id IS NOT NULL);