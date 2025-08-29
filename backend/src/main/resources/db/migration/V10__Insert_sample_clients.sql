-- Insert sample clients for testing and demonstration
-- This migration populates the clients table with diverse sample client data

INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
-- Individual clients with realistic Polish data
('Jan Kowalski', '85010112345', 'ul. Marszałkowska 123/45, 00-001 Warszawa', 'jan.kowalski@email.com', '+48 123 456 789'),
('Anna Nowak', '90052298765', 'ul. Krakowska 67/12, 31-066 Kraków', 'anna.nowak@gmail.com', '+48 234 567 890'),
('Piotr Wiśniewski', '78111587654', 'ul. Gdańska 89/3, 80-309 Gdańsk', 'piotr.wisniewski@outlook.com', '+48 345 678 901'),
('Maria Wójcik', '92030445678', 'ul. Wrocławska 234/56, 50-001 Wrocław', 'maria.wojcik@yahoo.com', '+48 456 789 012'),
('Tomasz Kowalczyk', '87070723456', 'ul. Poznańska 45/7, 60-001 Poznań', 'tomasz.kowalczyk@email.com', '+48 567 890 123'),

-- Business clients
('Katarzyna Lewandowska', '83041534567', 'ul. Łódzka 178/23, 90-001 Łódź', 'katarzyna.lewandowska@company.pl', '+48 678 901 234'),
('Michał Zieliński', '89122156789', 'ul. Szczecińska 90/11, 70-001 Szczecin', 'michal.zielinski@business.com', '+48 789 012 345'),
('Agnieszka Szymańska', '91081267890', 'ul. Bydgoska 156/8, 85-001 Bydgoszcz', 'agnieszka.szymanska@firm.pl', '+48 890 123 456'),

-- Young drivers (for testing different age groups)
('Jakub Dąbrowski', '00030178901', 'ul. Lubelska 67/14, 20-001 Lublin', 'jakub.dabrowski@student.edu.pl', '+48 901 234 567'),
('Natalia Kamińska', '99121289012', 'ul. Białostocka 123/9, 15-001 Białystok', 'natalia.kaminska@young.com', '+48 012 345 678'),

-- Senior clients (for testing different age groups)
('Stanisław Jankowski', '55041223456', 'ul. Rzeszowska 234/12, 35-001 Rzeszów', 'stanislaw.jankowski@senior.pl', '+48 123 987 654'),
('Halina Mazur', '58092334567', 'ul. Kielecka 89/5, 25-001 Kielce', 'halina.mazur@retiree.com', '+48 234 876 543'),

-- Clients with different address formats for testing
('Robert Piotrowski', '82060145678', 'os. Słoneczne 12/34, 40-001 Katowice', 'robert.piotrowski@test.pl', '+48 345 765 432'),
('Magdalena Grabowska', '86041256789', 'al. Jerozolimskie 456/78, 02-001 Warszawa', 'magdalena.grabowska@demo.com', '+48 456 654 321'),
('Paweł Nowakowski', '79081367890', 'pl. Centralny 23/1, 90-001 Łódź', 'pawel.nowakowski@example.org', '+48 567 543 210'),

-- International clients (for edge case testing)
('John Smith', '88050178901', 'ul. Międzynarodowa 45/67, 00-001 Warszawa', 'john.smith@international.com', '+48 678 432 109'),
('Marie Dubois', '84070289012', 'ul. Europejska 123/89, 31-001 Kraków', 'marie.dubois@europe.fr', '+48 789 321 098'),

-- Clients with compound surnames
('Anna Maria Kowalska-Nowak', '93041223456', 'ul. Długa 567/12, 80-001 Gdańsk', 'anna.kowalska.nowak@compound.pl', '+48 890 210 987'),
('Jan Kazimierz Wiśniewski-Kowalski', '76111334567', 'ul. Krótka 234/45, 50-001 Wrocław', 'jan.wisniewski.kowalski@long.com', '+48 901 109 876'),

-- Clients for specific insurance type testing
('Marcin Transportowiec', '81020145678', 'ul. Przemysłowa 789/23, 60-001 Poznań', 'marcin.transport@logistics.pl', '+48 012 098 765'),
('Beata Kierowca', '89111256789', 'ul. Szybka 345/67, 70-001 Szczecin', 'beata.kierowca@driver.com', '+48 123 876 543');

-- Comments for documentation
COMMENT ON TABLE clients IS 'Sample client data for testing and demonstration purposes';

-- Note: PESEL numbers are generated for testing purposes and follow the correct format
-- but may not represent real individuals. In production, real client data should be used.