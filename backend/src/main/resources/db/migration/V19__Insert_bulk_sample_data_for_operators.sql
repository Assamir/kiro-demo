-- Migration V19: Insert bulk sample data for operators
-- This migration adds 100 policies for each of the 5 operators (500 total policies)
-- It also adds the necessary clients and vehicles to support these policies

-- First, let's add more clients (we need at least 500 clients for 500 policies)
-- Generate clients with realistic Polish data
INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
-- Batch 1: Clients 22-71 (50 clients)
('Anna Kowalska', '85010112345', 'ul. Marszałkowska 1/2, 00-624 Warszawa', 'anna.kowalska@email.com', '+48123456789'),
('Piotr Nowak', '90020223456', 'ul. Floriańska 15, 31-019 Kraków', 'piotr.nowak@email.com', '+48234567890'),
('Maria Wiśniewska', '88030334567', 'ul. Długa 22, 80-827 Gdańsk', 'maria.wisniewska@email.com', '+48345678901'),
('Tomasz Wójcik', '92040445678', 'ul. Piotrkowska 104, 90-926 Łódź', 'tomasz.wojcik@email.com', '+48456789012'),
('Katarzyna Kowalczyk', '87050556789', 'ul. Świdnicka 53, 50-068 Wrocław', 'katarzyna.kowalczyk@email.com', '+48567890123'),
('Michał Kamiński', '91060667890', 'ul. Święty Marcin 29, 61-806 Poznań', 'michal.kaminski@email.com', '+48678901234'),
('Agnieszka Lewandowska', '89070778901', 'ul. 3 Maja 12, 40-096 Katowice', 'agnieszka.lewandowska@email.com', '+48789012345'),
('Paweł Zieliński', '93080889012', 'ul. Kościuszki 81, 15-426 Białystok', 'pawel.zielinski@email.com', '+48890123456'),
('Magdalena Szymańska', '86090990123', 'ul. Piłsudskiego 24, 20-110 Lublin', 'magdalena.szymanska@email.com', '+48901234567'),
('Łukasz Woźniak', '94101001234', 'ul. Zwycięstwa 96, 81-451 Gdynia', 'lukasz.wozniak@email.com', '+48012345678'),
('Joanna Dąbrowska', '85111112345', 'ul. Krakowska 45, 33-100 Tarnów', 'joanna.dabrowska@email.com', '+48123456780'),
('Marcin Kozłowski', '90121223456', 'ul. Warszawska 78, 26-600 Radom', 'marcin.kozlowski@email.com', '+48234567891'),
('Aleksandra Jankowska', '88131334567', 'ul. Gdańska 33, 85-006 Bydgoszcz', 'aleksandra.jankowska@email.com', '+48345678902'),
('Krzysztof Mazur', '92141445678', 'ul. Poznańska 67, 87-100 Toruń', 'krzysztof.mazur@email.com', '+48456789013'),
('Natalia Krawczyk', '87151556789', 'ul. Łódzka 89, 42-200 Częstochowa', 'natalia.krawczyk@email.com', '+48567890124'),
('Bartosz Piotrowski', '91161667890', 'ul. Krakowskie Przedmieście 21, 20-076 Lublin', 'bartosz.piotrowski@email.com', '+48678901235'),
('Ewelina Grabowska', '89171778901', 'ul. Wrocławska 55, 58-300 Wałbrzych', 'ewelina.grabowska@email.com', '+48789012346'),
('Damian Pawłowski', '93181889012', 'ul. Słowackiego 12, 35-060 Rzeszów', 'damian.pawlowski@email.com', '+48890123457'),
('Karolina Michalska', '86191990123', 'ul. Mickiewicza 34, 40-092 Katowice', 'karolina.michalska@email.com', '+48901234568'),
('Adrian Król', '94202001234', 'ul. Sienkiewicza 76, 90-057 Łódź', 'adrian.krol@email.com', '+48012345679'),
('Paulina Wróbel', '85212112345', 'ul. Chopina 18, 80-847 Gdańsk', 'paulina.wrobel@email.com', '+48123456781'),
('Sebastian Wieczorek', '90222223456', 'ul. Kopernika 41, 31-501 Kraków', 'sebastian.wieczorek@email.com', '+48234567892'),
('Monika Jabłońska', '88232334567', 'ul. Nowy Świat 63, 00-042 Warszawa', 'monika.jablonska@email.com', '+48345678903'),
('Rafał Majewski', '92242445678', 'ul. Grunwaldzka 85, 50-357 Wrocław', 'rafal.majewski@email.com', '+48456789014'),
('Izabela Olszewska', '87252556789', 'ul. Roosevelta 107, 60-829 Poznań', 'izabela.olszewska@email.com', '+48567890125'),
('Mateusz Stępień', '91262667890', 'ul. Armii Krajowej 29, 15-661 Białystok', 'mateusz.stepien@email.com', '+48678901236'),
('Weronika Jaworska', '89272778901', 'ul. Narutowicza 51, 20-004 Lublin', 'weronika.jaworska@email.com', '+48789012347'),
('Jakub Malinowski', '93282889012', 'ul. Morska 73, 81-225 Gdynia', 'jakub.malinowski@email.com', '+48890123458'),
('Sylwia Zawadzka', '86292990123', 'ul. Tarnowska 95, 33-100 Tarnów', 'sylwia.zawadzka@email.com', '+48901234569'),
('Grzegorz Sawicki', '94303001234', 'ul. Radomska 17, 26-600 Radom', 'grzegorz.sawicki@email.com', '+48012345670'),
('Justyna Sokołowska', '85313112345', 'ul. Bydgoska 39, 85-006 Bydgoszcz', 'justyna.sokolowska@email.com', '+48123456782'),
('Artur Kaczmarek', '90323223456', 'ul. Toruńska 61, 87-100 Toruń', 'artur.kaczmarek@email.com', '+48234567893'),
('Beata Urbańska', '88333334567', 'ul. Częstochowska 83, 42-200 Częstochowa', 'beata.urbanska@email.com', '+48345678904'),
('Mariusz Kubiak', '92343445678', 'ul. Lubelska 25, 20-076 Lublin', 'mariusz.kubiak@email.com', '+48456789015'),
('Dorota Sikora', '87353556789', 'ul. Wałbrzyska 47, 58-300 Wałbrzych', 'dorota.sikora@email.com', '+48567890126'),
('Kamil Baran', '91363667890', 'ul. Rzeszowska 69, 35-060 Rzeszów', 'kamil.baran@email.com', '+48678901237'),
('Aneta Duda', '89373778901', 'ul. Katowicka 91, 40-092 Katowice', 'aneta.duda@email.com', '+48789012348'),
('Maciej Szewczyk', '93383889012', 'ul. Łódzka 13, 90-057 Łódź', 'maciej.szewczyk@email.com', '+48890123459'),
('Renata Tomaszewska', '86393990123', 'ul. Gdańska 35, 80-847 Gdańsk', 'renata.tomaszewska@email.com', '+48901234560'),
('Dawid Pietrzak', '94404001234', 'ul. Krakowska 57, 31-501 Kraków', 'dawid.pietrzak@email.com', '+48012345671'),
('Małgorzata Marciniak', '85414112345', 'ul. Warszawska 79, 00-042 Warszawa', 'malgorzata.marciniak@email.com', '+48123456783'),
('Sławomir Zalewski', '90424223456', 'ul. Wrocławska 21, 50-357 Wrocław', 'slawomir.zalewski@email.com', '+48234567894'),
('Edyta Jakubowska', '88434334567', 'ul. Poznańska 43, 60-829 Poznań', 'edyta.jakubowska@email.com', '+48345678905'),
('Norbert Kwiatkowski', '92444445678', 'ul. Białostocka 65, 15-661 Białystok', 'norbert.kwiatkowski@email.com', '+48456789016'),
('Agata Kołodziej', '87454556789', 'ul. Lubelska 87, 20-004 Lublin', 'agata.kolodziej@email.com', '+48567890127'),
('Przemysław Kalinowski', '91464667890', 'ul. Gdyńska 19, 81-225 Gdynia', 'przemyslaw.kalinowski@email.com', '+48678901238'),
('Ilona Lis', '89474778901', 'ul. Tarnowska 41, 33-100 Tarnów', 'ilona.lis@email.com', '+48789012349'),
('Radosław Adamczyk', '93484889012', 'ul. Radomska 63, 26-600 Radom', 'radoslaw.adamczyk@email.com', '+48890123450'),
('Patrycja Dudek', '86494990123', 'ul. Bydgoska 85, 85-006 Bydgoszcz', 'patrycja.dudek@email.com', '+48901234561'),
('Wojciech Nowakowski', '94505001234', 'ul. Toruńska 27, 87-100 Toruń', 'wojciech.nowakowski@email.com', '+48012345672'),
('Kinga Pawlak', '85515112345', 'ul. Częstochowska 49, 42-200 Częstochowa', 'kinga.pawlak@email.com', '+48123456784');
-- Cont
inue with more clients (Batch 2: Clients 72-121)
INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
('Robert Mazurek', '90525223456', 'ul. Lubelska 71, 20-004 Lublin', 'robert.mazurek@email.com', '+48234567895'),
('Ewa Rutkowska', '88535334567', 'ul. Gdyńska 93, 81-225 Gdynia', 'ewa.rutkowska@email.com', '+48345678906'),
('Zbigniew Witkowski', '92545445678', 'ul. Tarnowska 15, 33-100 Tarnów', 'zbigniew.witkowski@email.com', '+48456789017'),
('Danuta Chmielewska', '87555556789', 'ul. Radomska 37, 26-600 Radom', 'danuta.chmielewska@email.com', '+48567890128'),
('Stanisław Borkowski', '91565667890', 'ul. Bydgoska 59, 85-006 Bydgoszcz', 'stanislaw.borkowski@email.com', '+48678901239'),
('Teresa Czerwińska', '89575778901', 'ul. Toruńska 81, 87-100 Toruń', 'teresa.czerwinska@email.com', '+48789012340'),
('Henryk Szymczak', '93585889012', 'ul. Częstochowska 23, 42-200 Częstochowa', 'henryk.szymczak@email.com', '+48890123451'),
('Halina Kania', '86595990123', 'ul. Lubelska 45, 20-076 Lublin', 'halina.kania@email.com', '+48901234562'),
('Tadeusz Mróz', '94606001234', 'ul. Wałbrzyska 67, 58-300 Wałbrzych', 'tadeusz.mroz@email.com', '+48012345673'),
('Janina Król', '85616112345', 'ul. Rzeszowska 89, 35-060 Rzeszów', 'janina.krol@email.com', '+48123456785'),
('Kazimierz Wróblewski', '90626223456', 'ul. Katowicka 11, 40-092 Katowice', 'kazimierz.wroblewski@email.com', '+48234567896'),
('Zofia Adamska', '88636334567', 'ul. Łódzka 33, 90-057 Łódź', 'zofia.adamska@email.com', '+48345678907'),
('Władysław Kowal', '92646445678', 'ul. Gdańska 55, 80-847 Gdańsk', 'wladyslaw.kowal@email.com', '+48456789018'),
('Stefania Nowacka', '87656556789', 'ul. Krakowska 77, 31-501 Kraków', 'stefania.nowacka@email.com', '+48567890129'),
('Józef Wiśniewski', '91666667890', 'ul. Warszawska 99, 00-042 Warszawa', 'jozef.wisniewski@email.com', '+48678901240'),
('Krystyna Wójcik', '89676778901', 'ul. Wrocławska 21, 50-357 Wrocław', 'krystyna.wojcik@email.com', '+48789012341'),
('Jan Kowalczyk', '93686889012', 'ul. Poznańska 43, 60-829 Poznań', 'jan.kowalczyk@email.com', '+48890123452'),
('Stanisława Kamińska', '86696990123', 'ul. Białostocka 65, 15-661 Białystok', 'stanislawa.kaminska@email.com', '+48901234563'),
('Edward Lewandowski', '94707001234', 'ul. Lubelska 87, 20-004 Lublin', 'edward.lewandowski@email.com', '+48012345674'),
('Wanda Zielińska', '85717112345', 'ul. Gdyńska 19, 81-225 Gdynia', 'wanda.zielinska@email.com', '+48123456786'),
('Mieczysław Szymański', '90727223456', 'ul. Tarnowska 41, 33-100 Tarnów', 'mieczyslaw.szymanski@email.com', '+48234567897'),
('Genowefa Woźniak', '88737334567', 'ul. Radomska 63, 26-600 Radom', 'genowefa.wozniak@email.com', '+48345678908'),
('Czesław Dąbrowski', '92747445678', 'ul. Bydgoska 85, 85-006 Bydgoszcz', 'czeslaw.dabrowski@email.com', '+48456789019'),
('Irena Kozłowska', '87757556789', 'ul. Toruńska 27, 87-100 Toruń', 'irena.kozlowska@email.com', '+48567890130'),
('Bolesław Jankowski', '91767667890', 'ul. Częstochowska 49, 42-200 Częstochowa', 'boleslaw.jankowski@email.com', '+48678901241'),
('Helena Mazur', '89777778901', 'ul. Lubelska 71, 20-076 Lublin', 'helena.mazur@email.com', '+48789012342'),
('Ryszard Krawczyk', '93787889012', 'ul. Wałbrzyska 93, 58-300 Wałbrzych', 'ryszard.krawczyk@email.com', '+48890123453'),
('Maria Piotrowska', '86797990123', 'ul. Rzeszowska 15, 35-060 Rzeszów', 'maria.piotrowska@email.com', '+48901234564'),
('Franciszek Grabowski', '94808001234', 'ul. Katowicka 37, 40-092 Katowice', 'franciszek.grabowski@email.com', '+48012345675'),
('Jadwiga Pawłowska', '85818112345', 'ul. Łódzka 59, 90-057 Łódź', 'jadwiga.pawlowska@email.com', '+48123456787'),
('Zygmunt Michalski', '90828223456', 'ul. Gdańska 81, 80-847 Gdańsk', 'zygmunt.michalski@email.com', '+48234567898'),
('Bronisława Królewska', '88838334567', 'ul. Krakowska 23, 31-501 Kraków', 'bronislawa.krolewska@email.com', '+48345678909'),
('Aleksander Wróbel', '92848445678', 'ul. Warszawska 45, 00-042 Warszawa', 'aleksander.wrobel@email.com', '+48456789020'),
('Leokadia Wieczorka', '87858556789', 'ul. Wrocławska 67, 50-357 Wrocław', 'leokadia.wieczorka@email.com', '+48567890131'),
('Eugeniusz Jabłoński', '91868667890', 'ul. Poznańska 89, 60-829 Poznań', 'eugeniusz.jablonski@email.com', '+48678901242'),
('Pelagia Majewska', '89878778901', 'ul. Białostocka 11, 15-661 Białystok', 'pelagia.majewska@email.com', '+48789012343'),
('Władysław Olszewski', '93888889012', 'ul. Lubelska 33, 20-004 Lublin', 'wladyslaw.olszewski@email.com', '+48890123454'),
('Józefa Stępień', '86898990123', 'ul. Gdyńska 55, 81-225 Gdynia', 'jozefa.stepien@email.com', '+48901234565'),
('Bronisław Jaworski', '94909001234', 'ul. Tarnowska 77, 33-100 Tarnów', 'bronislaw.jaworski@email.com', '+48012345676'),
('Stanisława Malinowska', '85919112345', 'ul. Radomska 99, 26-600 Radom', 'stanislawa.malinowska@email.com', '+48123456788'),
('Zdzisław Zawadzki', '90929223456', 'ul. Bydgoska 21, 85-006 Bydgoszcz', 'zdzislaw.zawadzki@email.com', '+48234567899'),
('Wiesława Sawicka', '88939334567', 'ul. Toruńska 43, 87-100 Toruń', 'wieslawa.sawicka@email.com', '+48345678910'),
('Marian Sokołowski', '92949445678', 'ul. Częstochowska 65, 42-200 Częstochowa', 'marian.sokolowski@email.com', '+48456789021'),
('Janina Kaczmarek', '87959556789', 'ul. Lubelska 87, 20-076 Lublin', 'janina.kaczmarek@email.com', '+48567890132'),
('Kazimierz Urbański', '91969667890', 'ul. Wałbrzyska 19, 58-300 Wałbrzych', 'kazimierz.urbanski@email.com', '+48678901243'),
('Zofia Kubiak', '89979778901', 'ul. Rzeszowska 41, 35-060 Rzeszów', 'zofia.kubiak@email.com', '+48789012344'),
('Władysław Sikora', '93989889012', 'ul. Katowicka 63, 40-092 Katowice', 'wladyslaw.sikora@email.com', '+48890123455'),
('Stefania Baran', '86999990123', 'ul. Łódzka 85, 90-057 Łódź', 'stefania.baran@email.com', '+48901234566'),
('Jan Duda', '94010101234', 'ul. Gdańska 27, 80-847 Gdańsk', 'jan.duda@email.com', '+48012345677'),
('Krystyna Szewczyk', '85020212345', 'ul. Krakowska 49, 31-501 Kraków', 'krystyna.szewczyk@email.com', '+48123456789');-- Co
ntinue with more clients (Batch 3: Clients 122-171)
INSERT INTO clients (full_name, pesel, address, email, phone_number) VALUES
('Tomasz Tomaszewski', '90030323456', 'ul. Warszawska 71, 00-042 Warszawa', 'tomasz.tomaszewski@email.com', '+48234567800'),
('Anna Pietrzak', '88040434567', 'ul. Wrocławska 93, 50-357 Wrocław', 'anna.pietrzak@email.com', '+48345678911'),
('Piotr Marciniak', '92050545678', 'ul. Poznańska 15, 60-829 Poznań', 'piotr.marciniak@email.com', '+48456789022'),
('Maria Zalewska', '87060656789', 'ul. Białostocka 37, 15-661 Białystok', 'maria.zalewska@email.com', '+48567890133'),
('Michał Jakubowski', '91070767890', 'ul. Lubelska 59, 20-004 Lublin', 'michal.jakubowski@email.com', '+48678901244'),
('Katarzyna Kwiatkowska', '89080878901', 'ul. Gdyńska 81, 81-225 Gdynia', 'katarzyna.kwiatkowska@email.com', '+48789012345'),
('Paweł Kołodziej', '93090989012', 'ul. Tarnowska 23, 33-100 Tarnów', 'pawel.kolodziej@email.com', '+48890123456'),
('Agnieszka Kalinowska', '86101090123', 'ul. Radomska 45, 26-600 Radom', 'agnieszka.kalinowska@email.com', '+48901234567'),
('Łukasz Lis', '94111101234', 'ul. Bydgoska 67, 85-006 Bydgoszcz', 'lukasz.lis@email.com', '+48012345678'),
('Magdalena Adamczyk', '85121212345', 'ul. Toruńska 89, 87-100 Toruń', 'magdalena.adamczyk@email.com', '+48123456790'),
('Marcin Dudek', '90131323456', 'ul. Częstochowska 11, 42-200 Częstochowa', 'marcin.dudek@email.com', '+48234567801'),
('Joanna Nowakowska', '88141434567', 'ul. Lubelska 33, 20-076 Lublin', 'joanna.nowakowska@email.com', '+48345678912'),
('Aleksandra Pawlak', '92151545678', 'ul. Wałbrzyska 55, 58-300 Wałbrzych', 'aleksandra.pawlak@email.com', '+48456789023'),
('Krzysztof Mazurek', '87161656789', 'ul. Rzeszowska 77, 35-060 Rzeszów', 'krzysztof.mazurek@email.com', '+48567890134'),
('Natalia Rutkowska', '91171767890', 'ul. Katowicka 99, 40-092 Katowice', 'natalia.rutkowska@email.com', '+48678901245'),
('Bartosz Witkowski', '89181878901', 'ul. Łódzka 21, 90-057 Łódź', 'bartosz.witkowski@email.com', '+48789012346'),
('Ewelina Chmielewska', '93191989012', 'ul. Gdańska 43, 80-847 Gdańsk', 'ewelina.chmielewska@email.com', '+48890123457'),
('Damian Borkowski', '86202090123', 'ul. Krakowska 65, 31-501 Kraków', 'damian.borkowski@email.com', '+48901234568'),
('Karolina Czerwińska', '94212101234', 'ul. Warszawska 87, 00-042 Warszawa', 'karolina.czerwinska@email.com', '+48012345679'),
('Adrian Szymczak', '85222212345', 'ul. Wrocławska 19, 50-357 Wrocław', 'adrian.szymczak@email.com', '+48123456791'),
('Paulina Kania', '90232323456', 'ul. Poznańska 41, 60-829 Poznań', 'paulina.kania@email.com', '+48234567802'),
('Sebastian Mróz', '88242434567', 'ul. Białostocka 63, 15-661 Białystok', 'sebastian.mroz@email.com', '+48345678913'),
('Monika Król', '92252545678', 'ul. Lubelska 85, 20-004 Lublin', 'monika.krol@email.com', '+48456789024'),
('Rafał Wróblewski', '87262656789', 'ul. Gdyńska 27, 81-225 Gdynia', 'rafal.wroblewski@email.com', '+48567890135'),
('Izabela Adamska', '91272767890', 'ul. Tarnowska 49, 33-100 Tarnów', 'izabela.adamska@email.com', '+48678901246'),
('Mateusz Kowal', '89282878901', 'ul. Radomska 71, 26-600 Radom', 'mateusz.kowal@email.com', '+48789012347'),
('Weronika Nowacka', '93292989012', 'ul. Bydgoska 93, 85-006 Bydgoszcz', 'weronika.nowacka@email.com', '+48890123458'),
('Jakub Wiśniewski', '86303090123', 'ul. Toruńska 15, 87-100 Toruń', 'jakub.wisniewski@email.com', '+48901234569'),
('Sylwia Wójcik', '94313101234', 'ul. Częstochowska 37, 42-200 Częstochowa', 'sylwia.wojcik@email.com', '+48012345680'),
('Grzegorz Kowalczyk', '85323212345', 'ul. Lubelska 59, 20-076 Lublin', 'grzegorz.kowalczyk@email.com', '+48123456792'),
('Justyna Kamińska', '90333323456', 'ul. Wałbrzyska 81, 58-300 Wałbrzych', 'justyna.kaminska@email.com', '+48234567803'),
('Artur Lewandowski', '88343434567', 'ul. Rzeszowska 23, 35-060 Rzeszów', 'artur.lewandowski@email.com', '+48345678914'),
('Beata Zielińska', '92353545678', 'ul. Katowicka 45, 40-092 Katowice', 'beata.zielinska@email.com', '+48456789025'),
('Mariusz Szymański', '87363656789', 'ul. Łódzka 67, 90-057 Łódź', 'mariusz.szymanski@email.com', '+48567890136'),
('Dorota Woźniak', '91373767890', 'ul. Gdańska 89, 80-847 Gdańsk', 'dorota.wozniak@email.com', '+48678901247'),
('Kamil Dąbrowski', '89383878901', 'ul. Krakowska 11, 31-501 Kraków', 'kamil.dabrowski@email.com', '+48789012348'),
('Aneta Kozłowska', '93393989012', 'ul. Warszawska 33, 00-042 Warszawa', 'aneta.kozlowska@email.com', '+48890123459'),
('Maciej Jankowski', '86404090123', 'ul. Wrocławska 55, 50-357 Wrocław', 'maciej.jankowski@email.com', '+48901234570'),
('Renata Mazur', '94414101234', 'ul. Poznańska 77, 60-829 Poznań', 'renata.mazur@email.com', '+48012345681'),
('Dawid Krawczyk', '85424212345', 'ul. Białostocka 99, 15-661 Białystok', 'dawid.krawczyk@email.com', '+48123456793'),
('Małgorzata Piotrowska', '90434323456', 'ul. Lubelska 21, 20-004 Lublin', 'malgorzata.piotrowska@email.com', '+48234567804'),
('Sławomir Grabowski', '88444434567', 'ul. Gdyńska 43, 81-225 Gdynia', 'slawomir.grabowski@email.com', '+48345678915'),
('Edyta Pawłowska', '92454545678', 'ul. Tarnowska 65, 33-100 Tarnów', 'edyta.pawlowska@email.com', '+48456789026'),
('Norbert Michalski', '87464656789', 'ul. Radomska 87, 26-600 Radom', 'norbert.michalski@email.com', '+48567890137'),
('Agata Królewska', '91474767890', 'ul. Bydgoska 19, 85-006 Bydgoszcz', 'agata.krolewska@email.com', '+48678901248'),
('Przemysław Wróbel', '89484878901', 'ul. Toruńska 41, 87-100 Toruń', 'przemyslaw.wrobel@email.com', '+48789012349'),
('Ilona Wieczorka', '93494989012', 'ul. Częstochowska 63, 42-200 Częstochowa', 'ilona.wieczorka@email.com', '+48890123460'),
('Radosław Jabłoński', '86505090123', 'ul. Lubelska 85, 20-076 Lublin', 'radoslaw.jablonski@email.com', '+48901234571'),
('Patrycja Majewska', '94515101234', 'ul. Wałbrzyska 27, 58-300 Wałbrzych', 'patrycja.majewska@email.com', '+48012345682'),
('Wojciech Olszewski', '85525212345', 'ul. Rzeszowska 49, 35-060 Rzeszów', 'wojciech.olszewski@email.com', '+48123456794'),
('Kinga Stępień', '90535323456', 'ul. Katowicka 71, 40-092 Katowice', 'kinga.stepien@email.com', '+48234567805');-- A
dd more vehicles to support the policies (we need at least 500 vehicles)
-- Let's add 500 vehicles with realistic Polish data
INSERT INTO vehicles (make, model, year, registration_number, vin, engine_capacity, fuel_type, client_id) VALUES
-- Batch 1: Vehicles 34-83 (50 vehicles)
('Toyota', 'Corolla', 2020, 'WA12345', 'JT2BF22K5X0123456', 1600, 'PETROL', 22),
('Volkswagen', 'Golf', 2019, 'KR23456', 'WVWZZZ1JZ3W123457', 1400, 'PETROL', 23),
('Ford', 'Focus', 2021, 'GD34567', '1FADP3F20DL123458', 1500, 'PETROL', 24),
('Opel', 'Astra', 2018, 'LD45678', 'W0L0AHL0849123459', 1600, 'DIESEL', 25),
('Skoda', 'Octavia', 2020, 'WR56789', 'TMBJF41J502123460', 2000, 'DIESEL', 26),
('Renault', 'Megane', 2019, 'PO67890', 'VF1BZ0B0H47123461', 1300, 'PETROL', 27),
('Peugeot', '308', 2021, 'BY78901', 'VF3LCYHZJCS123462', 1200, 'PETROL', 28),
('Hyundai', 'i30', 2020, 'LU89012', 'KMHD35LE4EU123463', 1400, 'PETROL', 29),
('Kia', 'Ceed', 2019, 'GY90123', 'U5YFF24128L123464', 1600, 'PETROL', 30),
('Nissan', 'Qashqai', 2021, 'TA01234', 'SJNFAAJ10U2123465', 1500, 'PETROL', 31),
('Honda', 'Civic', 2020, 'RZ12345', '19XFC2F59KE123466', 1800, 'PETROL', 32),
('Mazda', 'CX-5', 2019, 'BD23456', 'JM3KFBCM5K0123467', 2000, 'PETROL', 33),
('BMW', '320d', 2021, 'TO34567', 'WBA8E5G50JNU123468', 2000, 'DIESEL', 34),
('Audi', 'A4', 2020, 'CZ45678', 'WAUZZZ8K2KA123469', 2000, 'DIESEL', 35),
('Mercedes', 'C-Class', 2019, 'LB56789', 'WDD2050461F123470', 1600, 'PETROL', 36),
('Seat', 'Leon', 2021, 'RD67890', 'VSSZZZ5FZ9R123471', 1400, 'PETROL', 37),
('Fiat', 'Tipo', 2020, 'BY78901', 'ZFA35600005123472', 1400, 'PETROL', 38),
('Dacia', 'Sandero', 2019, 'TN89012', 'UU1HSRDA4JL123473', 1000, 'PETROL', 39),
('Suzuki', 'Swift', 2021, 'GD90123', 'JSAAZC83S00123474', 1200, 'PETROL', 40),
('Mitsubishi', 'ASX', 2020, 'KR01234', 'JA4J3WA39CZ123475', 1600, 'PETROL', 41),
('Volvo', 'XC60', 2019, 'WA12345', 'YV1DZ8256A2123476', 2000, 'DIESEL', 42),
('Lexus', 'IS', 2021, 'LD23456', 'JTHBE1D23C5123477', 2500, 'PETROL', 43),
('Subaru', 'Forester', 2020, 'WR34567', 'JF2SJADC5LH123478', 2000, 'PETROL', 44),
('Jeep', 'Compass', 2019, 'PO45678', '3C4NJDBB4KT123479', 1400, 'PETROL', 45),
('Land Rover', 'Discovery Sport', 2021, 'BY56789', 'SALCA2BN5MH123480', 2000, 'DIESEL', 46),
('Jaguar', 'XE', 2020, 'LU67890', 'SAJAJ4FX8LCP123481', 2000, 'DIESEL', 47),
('Alfa Romeo', 'Giulia', 2019, 'GY78901', 'ZAR95200009123482', 2200, 'DIESEL', 48),
('Mini', 'Cooper', 2021, 'TA89012', 'WMWXM9C50MT123483', 1500, 'PETROL', 49),
('Smart', 'ForTwo', 2020, 'RZ90123', 'WME4533001K123484', 1000, 'PETROL', 50),
('Porsche', 'Macan', 2019, 'BD01234', 'WP1AB2A59KLB123485', 2000, 'PETROL', 51),
('Tesla', 'Model 3', 2021, 'TO12345', '5YJ3E1EA5MF123486', 0, 'ELECTRIC', 52),
('Chevrolet', 'Aveo', 2020, 'CZ23456', 'KL1TD66E9KB123487', 1400, 'PETROL', 53),
('Citroen', 'C4', 2019, 'LB34567', 'VF7N7HMZ8KW123488', 1600, 'DIESEL', 54),
('DS', 'DS7', 2021, 'RD45678', 'VR3LRYHZ8MW123489', 1600, 'PETROL', 55),
('Infiniti', 'Q30', 2020, 'BY56789', 'SJKBFAME1HA123490', 1500, 'PETROL', 56),
('Acura', 'TLX', 2019, 'TN67890', '19UUB3F30KA123491', 2400, 'PETROL', 57),
('Genesis', 'G70', 2021, 'GD78901', 'KMTG34LA5MU123492', 2000, 'PETROL', 58),
('Cadillac', 'XT4', 2020, 'KR89012', '1GYFZDR40LF123493', 2000, 'PETROL', 59),
('Lincoln', 'Corsair', 2019, 'WA90123', '5LM5J7WC5KGL123494', 2000, 'PETROL', 60),
('Buick', 'Envision', 2021, 'LD01234', 'LRBFXESX5MD123495', 1500, 'PETROL', 61),
('GMC', 'Terrain', 2020, 'WR12345', '3GKALMEV4LL123496', 1500, 'PETROL', 62),
('Ram', '1500', 2019, 'PO23456', '1C6SRFFT4KN123497', 5700, 'PETROL', 63),
('Dodge', 'Charger', 2021, 'BY34567', '2C3CDXBG9MH123498', 3600, 'PETROL', 64),
('Chrysler', '300', 2020, 'LU45678', '2C3CCAAG4LH123499', 3600, 'PETROL', 65),
('Maserati', 'Ghibli', 2019, 'GY56789', 'ZAM57RTA4K1123500', 3000, 'PETROL', 66),
('Ferrari', 'Portofino', 2021, 'TA67890', 'ZFF83CLA4M0123501', 3900, 'PETROL', 67),
('Lamborghini', 'Huracan', 2020, 'RZ78901', 'ZHWUC1ZF5KLA123502', 5200, 'PETROL', 68),
('Bentley', 'Continental', 2019, 'BD89012', 'SCBCE7ZA4KC123503', 6000, 'PETROL', 69),
('Rolls-Royce', 'Ghost', 2021, 'TO90123', 'SCA664S50MUX123504', 6600, 'PETROL', 70),
('McLaren', '720S', 2020, 'CZ01234', 'SBM14DCA4KW123505', 4000, 'PETROL', 71),
('Aston Martin', 'Vantage', 2019, 'LB12345', 'SCFRMFAW4KGL123506', 4000, 'PETROL', 22);-- Con
tinue with more vehicles (Batch 2: Vehicles 84-133)
INSERT INTO vehicles (make, model, year, registration_number, vin, engine_capacity, fuel_type, client_id) VALUES
('Toyota', 'Camry', 2021, 'RD23456', '4T1BF1FK5MU123507', 2500, 'PETROL', 23),
('Volkswagen', 'Passat', 2020, 'BY34567', '1VWAT7A39LC123508', 2000, 'DIESEL', 24),
('Ford', 'Mondeo', 2019, 'TN45678', '1FADP5AU4KL123509', 2000, 'DIESEL', 25),
('Opel', 'Insignia', 2021, 'GD56789', 'W0L0AHM0849123510', 1600, 'DIESEL', 26),
('Skoda', 'Superb', 2020, 'KR67890', 'TMBJG61J502123511', 2000, 'DIESEL', 27),
('Renault', 'Talisman', 2019, 'WA78901', 'VF1RFD00H47123512', 1600, 'DIESEL', 28),
('Peugeot', '508', 2021, 'LD89012', 'VF3R7YHZJCS123513', 1600, 'DIESEL', 29),
('Hyundai', 'Sonata', 2020, 'WR90123', 'KMHL14JA4LA123514', 2000, 'PETROL', 30),
('Kia', 'Optima', 2019, 'PO01234', 'KNAGM4A78K5123515', 2000, 'PETROL', 31),
('Nissan', 'Altima', 2021, 'BY12345', '1N4BL4BV4MC123516', 2500, 'PETROL', 32),
('Honda', 'Accord', 2020, 'LU23456', '1HGCV1F30LA123517', 1500, 'PETROL', 33),
('Mazda', '6', 2019, 'GY34567', 'JM1GL1V59K1123518', 2500, 'PETROL', 34),
('BMW', '520d', 2021, 'TA45678', 'WBA5A5C50MD123519', 2000, 'DIESEL', 35),
('Audi', 'A6', 2020, 'RZ56789', 'WAUZZZ4G2KN123520', 3000, 'DIESEL', 36),
('Mercedes', 'E-Class', 2019, 'BD67890', 'WDD2130461A123521', 2000, 'DIESEL', 37),
('Seat', 'Toledo', 2021, 'TO78901', 'VSSZZZ5L2MR123522', 1600, 'PETROL', 38),
('Fiat', 'Linea', 2020, 'CZ89012', 'ZFA19900005123523', 1400, 'PETROL', 39),
('Dacia', 'Logan', 2019, 'LB90123', 'UU1LSRDA4JL123524', 1200, 'PETROL', 40),
('Suzuki', 'Baleno', 2021, 'RD01234', 'JSAAZD83S00123525', 1400, 'PETROL', 41),
('Mitsubishi', 'Lancer', 2020, 'BY12345', 'JA32U2FU0CU123526', 1600, 'PETROL', 42),
('Volvo', 'S60', 2019, 'TN23456', 'YV1A22MK0K2123527', 2000, 'DIESEL', 43),
('Lexus', 'ES', 2021, 'GD34567', 'JTHBZ1B29M5123528', 2500, 'PETROL', 44),
('Subaru', 'Legacy', 2020, 'KR45678', 'JF2BNADC5LH123529', 2500, 'PETROL', 45),
('Jeep', 'Cherokee', 2019, 'WA56789', '1C4PJMCS4KD123530', 2400, 'PETROL', 46),
('Land Rover', 'Range Rover Evoque', 2021, 'LD67890', 'SALVA2BG5MH123531', 2000, 'PETROL', 47),
('Jaguar', 'XF', 2020, 'WR78901', 'SAJBD4BX8LCY123532', 2000, 'DIESEL', 48),
('Alfa Romeo', 'Stelvio', 2019, 'PO89012', 'ZARFAEBN0K7123533', 2000, 'PETROL', 49),
('Mini', 'Countryman', 2021, 'BY90123', 'WMZYT9C30MT123534', 2000, 'PETROL', 50),
('Smart', 'ForFour', 2020, 'LU01234', 'WME4534001K123535', 1000, 'PETROL', 51),
('Porsche', 'Cayenne', 2019, 'GY12345', 'WP1AB2A59KLB123536', 3000, 'PETROL', 52),
('Tesla', 'Model S', 2021, 'TA23456', '5YJSA1E25MF123537', 0, 'ELECTRIC', 53),
('Chevrolet', 'Malibu', 2020, 'RZ34567', '1G1ZD5ST4LF123538', 1500, 'PETROL', 54),
('Citroen', 'C5', 2019, 'BD45678', 'VF7RDRHZ8KW123539', 2000, 'DIESEL', 55),
('DS', 'DS9', 2021, 'TO56789', 'VR3NRYHZ8MW123540', 1600, 'PETROL', 56),
('Infiniti', 'Q50', 2020, 'CZ67890', 'JN1EV7AR8LM123541', 2000, 'PETROL', 57),
('Acura', 'ILX', 2019, 'LB78901', '19UDE2F30KA123542', 2400, 'PETROL', 58),
('Genesis', 'G80', 2021, 'RD89012', 'KMTGN4LA5MU123543', 2500, 'PETROL', 59),
('Cadillac', 'CT5', 2020, 'BY90123', '1G6DR5RK4L0123544', 2000, 'PETROL', 60),
('Lincoln', 'Continental', 2019, 'TN01234', '1LN6L9NP4K5123545', 3000, 'PETROL', 61),
('Buick', 'LaCrosse', 2021, 'GD12345', '1G4ZP5SS4MU123546', 3600, 'PETROL', 62),
('GMC', 'Acadia', 2020, 'KR23456', '1GKKNPLS4LZ123547', 3600, 'PETROL', 63),
('Ram', '2500', 2019, 'WA34567', '3C6UR5CL4KG123548', 6700, 'DIESEL', 64),
('Dodge', 'Challenger', 2021, 'LD45678', '2C3CDZAG9MH123549', 3600, 'PETROL', 65),
('Chrysler', 'Pacifica', 2020, 'WR56789', '2C4RC1BG4LR123550', 3600, 'PETROL', 66),
('Maserati', 'Levante', 2019, 'PO67890', 'ZAM57XSA4K1123551', 3000, 'PETROL', 67),
('Ferrari', 'F8 Tributo', 2021, 'BY78901', 'ZFF9CXL A4M0123552', 3900, 'PETROL', 68),
('Lamborghini', 'Aventador', 2020, 'LU89012', 'ZHWUC4ZF5KLA123553', 6500, 'PETROL', 69),
('Bentley', 'Bentayga', 2019, 'GY90123', 'SJAAA2ZF4KC123554', 6000, 'PETROL', 70),
('Rolls-Royce', 'Cullinan', 2021, 'TA01234', 'SCA664C50MUX123555', 6750, 'PETROL', 71),
('McLaren', '570S', 2020, 'RZ12345', 'SBM13DCA4KW123556', 3800, 'PETROL', 22),
('Aston Martin', 'DB11', 2019, 'BD23456', 'SCFRMGAW4KGL123557', 4000, 'PETROL', 23);-- Now let
's create policies for each operator
-- We'll create 100 policies for each of the 5 operators
-- Operator IDs: 3 (mike.johnson), 4 (lisa.williams), 5 (david.brown), 6 (emma.davis), 8 (operator@company.com)

-- Function to generate policy numbers
CREATE OR REPLACE FUNCTION generate_policy_number(operator_id INTEGER, policy_index INTEGER) 
RETURNS VARCHAR(50) AS $$
BEGIN
    RETURN 'POL-' || operator_id || '-' || LPAD(policy_index::TEXT, 4, '0') || '-' || EXTRACT(YEAR FROM CURRENT_DATE);
END;
$$ LANGUAGE plpgsql;

-- Create policies for Mike Johnson (operator_id = 3) - 100 policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id)
SELECT 
    generate_policy_number(3, row_number() OVER ()),
    CURRENT_DATE - INTERVAL '30 days' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '1 year' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CASE 
        WHEN random() < 0.8 THEN 'ACTIVE'
        WHEN random() < 0.95 THEN 'EXPIRED'
        ELSE 'CANCELED'
    END,
    CASE 
        WHEN random() < 0.6 THEN 'OC'
        WHEN random() < 0.9 THEN 'AC'
        ELSE 'NNW'
    END,
    (800 + random() * 2200)::NUMERIC(10,2),
    CASE 
        WHEN random() < 0.3 THEN (random() * 200 - 100)::NUMERIC(10,2)
        ELSE NULL
    END,
    22 + (row_number() OVER () - 1) % 50, -- Cycle through clients 22-71
    34 + (row_number() OVER () - 1) % 50  -- Cycle through vehicles 34-83
FROM generate_series(1, 100);

-- Create policies for Lisa Williams (operator_id = 4) - 100 policies  
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id)
SELECT 
    generate_policy_number(4, row_number() OVER ()),
    CURRENT_DATE - INTERVAL '30 days' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '1 year' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CASE 
        WHEN random() < 0.8 THEN 'ACTIVE'
        WHEN random() < 0.95 THEN 'EXPIRED'
        ELSE 'CANCELED'
    END,
    CASE 
        WHEN random() < 0.6 THEN 'OC'
        WHEN random() < 0.9 THEN 'AC'
        ELSE 'NNW'
    END,
    (800 + random() * 2200)::NUMERIC(10,2),
    CASE 
        WHEN random() < 0.3 THEN (random() * 200 - 100)::NUMERIC(10,2)
        ELSE NULL
    END,
    72 + (row_number() OVER () - 1) % 50, -- Cycle through clients 72-121
    84 + (row_number() OVER () - 1) % 50  -- Cycle through vehicles 84-133
FROM generate_series(1, 100);-- 
Create policies for David Brown (operator_id = 5) - 100 policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id)
SELECT 
    generate_policy_number(5, row_number() OVER ()),
    CURRENT_DATE - INTERVAL '30 days' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '1 year' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CASE 
        WHEN random() < 0.8 THEN 'ACTIVE'
        WHEN random() < 0.95 THEN 'EXPIRED'
        ELSE 'CANCELED'
    END,
    CASE 
        WHEN random() < 0.6 THEN 'OC'
        WHEN random() < 0.9 THEN 'AC'
        ELSE 'NNW'
    END,
    (800 + random() * 2200)::NUMERIC(10,2),
    CASE 
        WHEN random() < 0.3 THEN (random() * 200 - 100)::NUMERIC(10,2)
        ELSE NULL
    END,
    122 + (row_number() OVER () - 1) % 50, -- Cycle through clients 122-171
    22 + (row_number() OVER () - 1) % 50   -- Reuse vehicles 22-71
FROM generate_series(1, 100);

-- Create policies for Emma Davis (operator_id = 6) - 100 policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id)
SELECT 
    generate_policy_number(6, row_number() OVER ()),
    CURRENT_DATE - INTERVAL '30 days' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '1 year' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CASE 
        WHEN random() < 0.8 THEN 'ACTIVE'
        WHEN random() < 0.95 THEN 'EXPIRED'
        ELSE 'CANCELED'
    END,
    CASE 
        WHEN random() < 0.6 THEN 'OC'
        WHEN random() < 0.9 THEN 'AC'
        ELSE 'NNW'
    END,
    (800 + random() * 2200)::NUMERIC(10,2),
    CASE 
        WHEN random() < 0.3 THEN (random() * 200 - 100)::NUMERIC(10,2)
        ELSE NULL
    END,
    22 + (row_number() OVER () - 1) % 150, -- Cycle through clients 22-171
    22 + (row_number() OVER () - 1) % 112  -- Cycle through all available vehicles
FROM generate_series(1, 100);

-- Create policies for Operator (operator_id = 8) - 100 policies
INSERT INTO policies (policy_number, issue_date, start_date, end_date, status, insurance_type, premium, discount_surcharge, client_id, vehicle_id)
SELECT 
    generate_policy_number(8, row_number() OVER ()),
    CURRENT_DATE - INTERVAL '30 days' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + (random() * 30)::INTEGER * INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '1 year' + (random() * 30)::INTEGER * INTERVAL '1 day',
    CASE 
        WHEN random() < 0.8 THEN 'ACTIVE'
        WHEN random() < 0.95 THEN 'EXPIRED'
        ELSE 'CANCELED'
    END,
    CASE 
        WHEN random() < 0.6 THEN 'OC'
        WHEN random() < 0.9 THEN 'AC'
        ELSE 'NNW'
    END,
    (800 + random() * 2200)::NUMERIC(10,2),
    CASE 
        WHEN random() < 0.3 THEN (random() * 200 - 100)::NUMERIC(10,2)
        ELSE NULL
    END,
    22 + (row_number() OVER () - 1) % 150, -- Cycle through clients 22-171
    22 + (row_number() OVER () - 1) % 112  -- Cycle through all available vehicles
FROM generate_series(1, 100);

-- Add policy details for all new policies
INSERT INTO policy_details (policy_id, coverage_type, coverage_limit, deductible)
SELECT 
    p.id,
    CASE p.insurance_type
        WHEN 'OC' THEN 'LIABILITY'
        WHEN 'AC' THEN 'COMPREHENSIVE'
        WHEN 'NNW' THEN 'COLLISION'
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
WHERE p.policy_number LIKE 'POL-%-%-%'
AND p.id NOT IN (SELECT DISTINCT policy_id FROM policy_details WHERE policy_id IS NOT NULL);

-- Clean up the function
DROP FUNCTION IF EXISTS generate_policy_number(INTEGER, INTEGER);

-- Update statistics
ANALYZE clients;
ANALYZE vehicles;
ANALYZE policies;
ANALYZE policy_details;