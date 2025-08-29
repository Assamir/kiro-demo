-- Add sample clients and policies with correct schema
-- Add some additional clients
INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
('Anna Kowalski', '85010112345', 'ul. Marszałkowska 1, Warszawa, 00-001', 'anna.kowalski@email.com', '+48123456789'),
('Piotr Nowak', '90020223456', 'ul. Krakowska 2, Kraków, 30-001', 'piotr.nowak@email.com', '+48123456790'),
('Maria Wiśniewska', '88030334567', 'ul. Gdańska 3, Gdańsk, 80-001', 'maria.wisniewska@email.com', '+48123456791'),
('Tomasz Wójcik', '92040445678', 'ul. Wrocławska 4, Wrocław, 50-001', 'tomasz.wojcik@email.com', '+48123456792'),
('Katarzyna Kowalczyk', '87050556789', 'ul. Poznańska 5, Poznań, 60-001', 'katarzyna.kowalczyk@email.com', '+48123456793'),
('Michał Kamiński', '91060667890', 'ul. Łódzka 6, Łódź, 90-001', 'michal.kaminski@email.com', '+48123456794'),
('Agnieszka Lewandowska', '89070778901', 'ul. Szczecińska 7, Szczecin, 70-001', 'agnieszka.lewandowska@email.com', '+48123456795'),
('Paweł Zieliński', '93080889012', 'ul. Bydgoska 8, Bydgoszcz, 85-001', 'pawel.zielinski@email.com', '+48123456796'),
('Magdalena Szymańska', '86090990123', 'ul. Lubelska 9, Lublin, 20-001', 'magdalena.szymanska@email.com', '+48123456797'),
('Krzysztof Woźniak', '94101001234', 'ul. Katowice 10, Katowice, 40-001', 'krzysztof.wozniak@email.com', '+48123456798');

-- Add some additional vehicles
INSERT INTO vehicles (make, model, year_of_manufacture, registration_number, vin, engine_capacity, power, first_registration_date) VALUES
('Toyota', 'Corolla', 2020, 'WA12345', 'JT2BF28K0X0123456', 1600, 132, '2020-03-15'),
('Volkswagen', 'Golf', 2019, 'KR23456', 'WVWZZZ1JZ3W123456', 1400, 150, '2019-05-20'),
('Ford', 'Focus', 2021, 'GD34567', '1FADP3F20DL123456', 1500, 125, '2021-02-10'),
('Opel', 'Astra', 2018, 'WR45678', 'W0L0AHL0849123456', 1600, 136, '2018-08-12'),
('Skoda', 'Octavia', 2020, 'PO56789', 'TMBJF7NE0C7123456', 1400, 150, '2020-06-25'),
('Renault', 'Megane', 2019, 'LD67890', 'VF1BZ0B0H54123456', 1300, 140, '2019-09-18'),
('Peugeot', '308', 2021, 'SZ78901', 'VF3LCYHZJHS123456', 1200, 130, '2021-01-22'),
('Hyundai', 'i30', 2020, 'BY89012', 'KMHD35LE4EU123456', 1400, 140, '2020-04-30'),
('Kia', 'Ceed', 2019, 'LU90123', 'U5YFF24128L123456', 1600, 128, '2019-11-05'),
('Nissan', 'Qashqai', 2021, 'KT01234', 'SJNFAAJ10U2123456', 1300, 140, '2021-03-08');

-- Add sample policies for Mike Johnson (user_id = 3) - 25 policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id) VALUES
('OC-2024-MJ-001', '2024-01-05', '2024-01-15', '2025-01-14', 'ACTIVE', 'OC', 850.00, 0.00, 22, 34),
('OC-2024-MJ-002', '2024-01-10', '2024-01-20', '2025-01-19', 'ACTIVE', 'OC', 920.50, -50.00, 23, 35),
('OC-2024-MJ-003', '2024-01-15', '2024-01-25', '2025-01-24', 'ACTIVE', 'OC', 780.25, -75.00, 24, 36),
('OC-2024-MJ-004', '2024-01-20', '2024-01-30', '2025-01-29', 'ACTIVE', 'OC', 1050.00, 0.00, 25, 37),
('OC-2024-MJ-005', '2024-01-25', '2024-02-04', '2025-02-03', 'ACTIVE', 'OC', 890.75, -25.00, 26, 38),
('OC-2024-MJ-006', '2024-02-01', '2024-02-11', '2025-02-10', 'ACTIVE', 'OC', 950.00, 50.00, 27, 39),
('OC-2024-MJ-007', '2024-02-05', '2024-02-15', '2025-02-14', 'ACTIVE', 'OC', 820.50, -100.00, 28, 40),
('OC-2024-MJ-008', '2024-02-10', '2024-02-20', '2025-02-19', 'ACTIVE', 'OC', 1120.25, 75.00, 29, 41),
('AC-2024-MJ-001', '2024-01-08', '2024-01-18', '2025-01-17', 'ACTIVE', 'AC', 2450.00, 0.00, 30, 42),
('AC-2024-MJ-002', '2024-01-12', '2024-01-22', '2025-01-21', 'ACTIVE', 'AC', 2890.75, -150.00, 31, 43),
('AC-2024-MJ-003', '2024-01-18', '2024-01-28', '2025-01-27', 'ACTIVE', 'AC', 1850.25, -100.00, 22, 34),
('AC-2024-MJ-004', '2024-01-22', '2024-02-01', '2025-01-31', 'ACTIVE', 'AC', 3250.50, 200.00, 23, 35),
('AC-2024-MJ-005', '2024-01-28', '2024-02-07', '2025-02-06', 'ACTIVE', 'AC', 2150.00, 0.00, 24, 36),
('AC-2024-MJ-006', '2024-02-03', '2024-02-13', '2025-02-12', 'ACTIVE', 'AC', 2750.75, 100.00, 25, 37),
('AC-2024-MJ-007', '2024-02-08', '2024-02-18', '2025-02-17', 'ACTIVE', 'AC', 1950.25, -200.00, 26, 38),
('AC-2024-MJ-008', '2024-02-12', '2024-02-22', '2025-02-21', 'ACTIVE', 'AC', 3450.50, 300.00, 27, 39),
('NNW-2024-MJ-001', '2024-01-06', '2024-01-16', '2025-01-15', 'ACTIVE', 'NNW', 180.00, 0.00, 28, 40),
('NNW-2024-MJ-002', '2024-01-16', '2024-01-26', '2025-01-25', 'ACTIVE', 'NNW', 220.50, -20.00, 29, 41),
('NNW-2024-MJ-003', '2024-01-26', '2024-02-05', '2025-02-04', 'ACTIVE', 'NNW', 195.75, 15.00, 30, 42),
('NNW-2024-MJ-004', '2024-02-06', '2024-02-16', '2025-02-15', 'ACTIVE', 'NNW', 165.25, -25.00, 31, 43),
('NNW-2024-MJ-005', '2024-02-16', '2024-02-26', '2025-02-25', 'ACTIVE', 'NNW', 210.00, 0.00, 22, 34),
('NNW-2024-MJ-006', '2024-02-26', '2024-03-08', '2025-03-07', 'ACTIVE', 'NNW', 185.50, -10.00, 23, 35),
('NNW-2024-MJ-007', '2024-03-06', '2024-03-16', '2025-03-15', 'ACTIVE', 'NNW', 230.75, 25.00, 24, 36),
('NNW-2024-MJ-008', '2024-03-16', '2024-03-26', '2025-03-25', 'ACTIVE', 'NNW', 175.25, -15.00, 25, 37),
('NNW-2024-MJ-009', '2024-03-26', '2024-04-05', '2025-04-04', 'ACTIVE', 'NNW', 200.00, 5.00, 26, 38);