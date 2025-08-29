-- Add sample clients and policies for demonstration
-- This will be executed manually

-- Add some additional clients
INSERT INTO clients (first_name, last_name, email, phone, pesel, address, city, postal_code) VALUES
('Anna', 'Kowalski', 'anna.kowalski@email.com', '+48123456789', '85010112345', 'ul. Marszałkowska 1', 'Warszawa', '00-001'),
('Piotr', 'Nowak', 'piotr.nowak@email.com', '+48123456790', '90020223456', 'ul. Krakowska 2', 'Kraków', '30-001'),
('Maria', 'Wiśniewska', 'maria.wisniewska@email.com', '+48123456791', '88030334567', 'ul. Gdańska 3', 'Gdańsk', '80-001'),
('Tomasz', 'Wójcik', 'tomasz.wojcik@email.com', '+48123456792', '92040445678', 'ul. Wrocławska 4', 'Wrocław', '50-001'),
('Katarzyna', 'Kowalczyk', 'katarzyna.kowalczyk@email.com', '+48123456793', '87050556789', 'ul. Poznańska 5', 'Poznań', '60-001');

-- Add some additional vehicles
INSERT INTO vehicles (make, model, year, engine_capacity, fuel_type, vin, registration_number, owner_id) VALUES
('Toyota', 'Corolla', 2020, 1600, 'Gasoline', 'JT2BF28K0X0123456', 'WA12345', 22),
('Volkswagen', 'Golf', 2019, 1400, 'Gasoline', 'WVWZZZ1JZ3W123456', 'KR23456', 23),
('Ford', 'Focus', 2021, 1500, 'Gasoline', '1FADP3F20DL123456', 'GD34567', 24),
('Opel', 'Astra', 2018, 1600, 'Diesel', 'W0L0AHL0849123456', 'WR45678', 25),
('Skoda', 'Octavia', 2020, 1400, 'Gasoline', 'TMBJF7NE0C7123456', 'PO56789', 26);

-- Add sample policies for Mike Johnson (user_id = 3)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-MJ-001', '2024-01-05', '2024-01-15', '2025-01-14', 'ACTIVE', 'OC', 850.00, 0.00, 22, 34),
('OC-2024-MJ-002', '2024-01-10', '2024-01-20', '2025-01-19', 'ACTIVE', 'OC', 920.50, -50.00, 23, 35),
('OC-2024-MJ-003', '2024-01-15', '2024-01-25', '2025-01-24', 'ACTIVE', 'OC', 780.25, -75.00, 24, 36),
('AC-2024-MJ-001', '2024-01-08', '2024-01-18', '2025-01-17', 'ACTIVE', 'AC', 2450.00, 0.00, 25, 37),
('AC-2024-MJ-002', '2024-01-12', '2024-01-22', '2025-01-21', 'ACTIVE', 'AC', 2890.75, -150.00, 26, 38),
('NNW-2024-MJ-001', '2024-01-06', '2024-01-16', '2025-01-15', 'ACTIVE', 'NNW', 180.00, 0.00, 22, 34),
('NNW-2024-MJ-002', '2024-01-16', '2024-01-26', '2025-01-25', 'ACTIVE', 'NNW', 220.50, -20.00, 23, 35);

-- Add sample policies for Lisa Williams (user_id = 4)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-LW-001', '2024-01-07', '2024-01-17', '2025-01-16', 'ACTIVE', 'OC', 875.00, -25.00, 24, 36),
('OC-2024-LW-002', '2024-01-11', '2024-01-21', '2025-01-20', 'ACTIVE', 'OC', 945.50, -75.00, 25, 37),
('AC-2024-LW-001', '2024-01-09', '2024-01-19', '2025-01-18', 'ACTIVE', 'AC', 2475.00, 25.00, 26, 38),
('AC-2024-LW-002', '2024-01-13', '2024-01-23', '2025-01-22', 'ACTIVE', 'AC', 2915.75, -125.00, 22, 34),
('NNW-2024-LW-001', '2024-01-07', '2024-01-17', '2025-01-16', 'ACTIVE', 'NNW', 185.00, 5.00, 23, 35);

-- Add sample policies for David Brown (user_id = 5)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-DB-001', '2024-01-08', '2024-01-18', '2025-01-17', 'ACTIVE', 'OC', 880.00, -20.00, 22, 34),
('AC-2024-DB-001', '2024-01-10', '2024-01-20', '2025-01-19', 'ACTIVE', 'AC', 2480.00, 30.00, 23, 35),
('NNW-2024-DB-001', '2024-01-08', '2024-01-18', '2025-01-17', 'ACTIVE', 'NNW', 190.00, 10.00, 24, 36);

-- Add sample policies for Emma Davis (user_id = 6)
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-ED-001', '2024-01-09', '2024-01-19', '2025-01-18', 'ACTIVE', 'OC', 885.00, -15.00, 25, 37),
('AC-2024-ED-001', '2024-01-11', '2024-01-21', '2025-01-20', 'ACTIVE', 'AC', 2485.00, 35.00, 26, 38),
('NNW-2024-ED-001', '2024-01-09', '2024-01-19', '2025-01-18', 'ACTIVE', 'NNW', 195.00, 15.00, 22, 34);