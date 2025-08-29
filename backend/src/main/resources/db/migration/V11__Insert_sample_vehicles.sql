-- Insert sample vehicles for testing and demonstration
-- This migration populates the vehicles table with diverse vehicle data for different scenarios

INSERT INTO vehicles (make, model, year_of_manufacture, registration_number, vin, engine_capacity, power, first_registration_date) VALUES
-- Popular Polish market vehicles - different ages and engine sizes
('Toyota', 'Corolla', 2020, 'WA12345', 'JTDBL40E309012345', 1600, 132, '2020-03-15'),
('Volkswagen', 'Golf', 2019, 'KR67890', 'WVWZZZ1JZ3W123456', 1400, 125, '2019-07-22'),
('Skoda', 'Octavia', 2021, 'GD11111', 'TMBJF25J0C7123456', 2000, 190, '2021-01-10'),
('Ford', 'Focus', 2018, 'WR22222', '1FADP3F23EL123456', 1500, 150, '2018-05-18'),
('Opel', 'Astra', 2017, 'PO33333', 'W0L0AHL08E4123456', 1600, 136, '2017-09-12'),

-- Luxury vehicles for AC insurance testing
('BMW', '320d', 2022, 'WA44444', 'WBA8E5G50JNU12345', 2000, 190, '2022-02-28'),
('Mercedes-Benz', 'C220', 2021, 'KR55555', 'WDD2050461F123456', 2200, 194, '2021-06-14'),
('Audi', 'A4', 2020, 'GD66666', 'WAUZZZ8K0DA123456', 2000, 204, '2020-11-03'),

-- Small city cars for different engine categories
('Fiat', '500', 2019, 'WR77777', 'ZFA31200000123456', 900, 85, '2019-04-25'),
('Peugeot', '208', 2020, 'PO88888', 'VF3C68HM0FS123456', 1200, 100, '2020-08-17'),
('Renault', 'Clio', 2018, 'WA99999', 'VF1RJ0K0H62123456', 1000, 90, '2018-12-05'),

-- Older vehicles for age-based rating testing
('Volkswagen', 'Passat', 2010, 'KR00000', 'WVWZZZ3CZ9P123456', 1900, 140, '2010-03-20'),
('Ford', 'Mondeo', 2012, 'GD12121', '1FAHP2E85CG123456', 2000, 163, '2012-07-15'),
('Toyota', 'Avensis', 2008, 'WR34343', 'JTDBT923385123456', 2200, 177, '2008-11-28'),
('Opel', 'Vectra', 2006, 'PO56565', 'W0L0ZCF6961123456', 1800, 122, '2006-05-10'),

-- High-performance vehicles for power-based rating
('BMW', 'M3', 2021, 'WA78787', 'WBS8M9C55M5N12345', 3000, 480, '2021-09-12'),
('Mercedes-Benz', 'AMG C63', 2020, 'KR90909', 'WDD2050071F123456', 4000, 476, '2020-04-08'),
('Audi', 'RS4', 2019, 'GD01010', 'WAUZZZ8K5DA123456', 2900, 450, '2019-12-22'),

-- Commercial/utility vehicles
('Volkswagen', 'Caddy', 2019, 'WR23232', 'WVWZZZ2KZ9X123456', 1600, 102, '2019-06-30'),
('Ford', 'Transit', 2020, 'PO45454', 'WF0XXXTTGXDA12345', 2200, 130, '2020-01-25'),

-- Electric/Hybrid vehicles for future testing
('Tesla', 'Model 3', 2022, 'WA67676', '5YJ3E1EA4MF123456', 0, 283, '2022-03-18'),
('Toyota', 'Prius', 2021, 'KR89898', 'JTDKN3DU5D0123456', 1800, 122, '2021-08-05'),

-- Motorcycles (if system needs to handle them)
('Honda', 'CBR600RR', 2020, 'GD10203', 'JH2PC40E0LM123456', 600, 118, '2020-05-12'),
('Yamaha', 'MT-07', 2019, 'WR30405', 'JYARN23E0KA123456', 689, 75, '2019-09-18'),

-- Vintage/Classic cars for edge case testing
('Volkswagen', 'Beetle', 1998, 'PO60708', 'WVWZZZ1CZ6W123456', 1600, 50, '1998-07-14'),
('BMW', 'E36', 1995, 'WA90102', 'WBABF71010PK12345', 1800, 140, '1995-11-20'),

-- Vehicles with different registration patterns
('Skoda', 'Fabia', 2021, 'DW12345', 'TMBJG11J0C7123456', 1000, 95, '2021-02-14'),
('Hyundai', 'i30', 2020, 'EL67890', 'KMHD35LE4LU123456', 1400, 140, '2020-10-07'),
('Kia', 'Ceed', 2019, 'LU11223', 'U5YFF24259L123456', 1600, 134, '2019-12-19'),

-- Vehicles for specific client assignments (matching client count)
('Nissan', 'Qashqai', 2021, 'BI33445', 'SJNFAAJ10U2123456', 1300, 140, '2021-04-16'),
('Mazda', 'CX-5', 2020, 'RZ55667', 'JM3KE4BE4L0123456', 2500, 194, '2020-07-23'),
('Seat', 'Leon', 2018, 'KT77889', 'VSSZZZ5FZ9R123456', 1400, 150, '2018-03-11'),
('Dacia', 'Duster', 2019, 'SZ99001', 'UU1HSRHE4JL123456', 1600, 115, '2019-08-29');

-- Comments for documentation
COMMENT ON TABLE vehicles IS 'Sample vehicle data covering various makes, models, ages, and engine specifications for testing';

-- Note: VIN numbers are generated for testing purposes following the correct 17-character format
-- Registration numbers follow Polish format patterns but are fictional
-- Engine capacities and power ratings are realistic for the respective models