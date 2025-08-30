# Sample Data Documentation

Complete reference for the sample data included in the Insurance Backoffice System.

## 📊 Data Overview

The system comes pre-loaded with realistic sample data for testing and demonstration purposes.

### Data Volume
- **Users**: 8 accounts (3 admins, 5 operators)
- **Clients**: 31 Polish clients with realistic data
- **Vehicles**: 43 vehicles with proper specifications
- **Policies**: 443 insurance policies across all types
- **Rating Tables**: 15 premium calculation rules

## 👥 Sample Users

### Administrator Accounts
| Email | Password | Full Name | Role |
|-------|----------|-----------|------|
| admin@insurance.com | admin123 | Admin User | ADMIN |
| anna.kowalska@insurance.com | password123 | Anna Kowalska | ADMIN |
| piotr.nowak@insurance.com | password123 | Piotr Nowak | ADMIN |

### Operator Accounts
| Email | Password | Full Name | Role |
|-------|----------|-----------|------|
| mike.johnson@insurance.com | password123 | Mike Johnson | OPERATOR |
| sarah.wilson@insurance.com | password123 | Sarah Wilson | OPERATOR |
| david.brown@insurance.com | password123 | David Brown | OPERATOR |
| lisa.davis@insurance.com | password123 | Lisa Davis | OPERATOR |
| john.smith@insurance.com | password123 | John Smith | OPERATOR |

## 👤 Sample Clients

### Client Data Characteristics
- **Names**: Realistic Polish names (Jan Kowalski, Anna Nowak, etc.)
- **PESEL**: Valid Polish national identification numbers
- **Addresses**: Real Polish cities and street addresses
- **Contact**: Email addresses and phone numbers in Polish format

### Example Clients
| ID | Full Name | PESEL | City | Email |
|----|-----------|-------|------|-------|
| 1 | Jan Kowalski | 85010112345 | Warszawa | jan.kowalski@email.com |
| 2 | Anna Nowak | 90050298765 | Kraków | anna.nowak@email.com |
| 3 | Piotr Wiśniewski | 78121587654 | Gdańsk | piotr.wisniewski@email.com |
| 4 | Maria Wójcik | 82030476543 | Wrocław | maria.wojcik@email.com |
| 5 | Tomasz Kowalczyk | 75081265432 | Poznań | tomasz.kowalczyk@email.com |

## 🚗 Sample Vehicles

### Vehicle Data Characteristics
- **Makes**: Popular brands (Toyota, Volkswagen, BMW, Audi, Ford, etc.)
- **Models**: Current and recent model years
- **Registration**: Polish registration number format (e.g., WA12345)
- **VIN**: Valid 17-character Vehicle Identification Numbers
- **Specifications**: Realistic engine capacity and power ratings

### Example Vehicles
| ID | Make | Model | Registration | Year | Engine (cc) | Power (HP) |
|----|------|-------|--------------|------|-------------|------------|
| 1 | Toyota | Corolla | WA12345 | 2020 | 1600 | 120 |
| 2 | Volkswagen | Golf | KR67890 | 2019 | 1400 | 110 |
| 3 | BMW | 320i | GD11111 | 2021 | 2000 | 180 |
| 4 | Audi | A4 | WR22222 | 2020 | 2000 | 190 |
| 5 | Ford | Focus | PO33333 | 2018 | 1500 | 125 |

### Vehicle Distribution
- **Passenger Cars**: 38 vehicles (88%)
- **SUVs**: 3 vehicles (7%)
- **Motorcycles**: 2 vehicles (5%)

## 📋 Sample Policies

### Policy Distribution by Type
| Insurance Type | Count | Percentage |
|----------------|-------|------------|
| OC (Obligatory Civil Liability) | 172 | 39% |
| AC (Auto Casco) | 172 | 39% |
| NNW (Uninsured Drivers) | 99 | 22% |
| **Total** | **443** | **100%** |

### Policy Status Distribution
| Status | Count | Percentage |
|--------|-------|------------|
| ACTIVE | 398 | 90% |
| CANCELED | 25 | 6% |
| EXPIRED | 20 | 4% |

### Policy Number Format
- **OC Policies**: OC-2024-001001 to OC-2024-001172
- **AC Policies**: AC-2024-001001 to AC-2024-001172
- **NNW Policies**: NNW-2024-001001 to NNW-2024-001099

### Premium Ranges
| Insurance Type | Min Premium | Max Premium | Average Premium |
|----------------|-------------|-------------|-----------------|
| OC | 800.00 PLN | 2,400.00 PLN | 1,200.00 PLN |
| AC | 1,500.00 PLN | 4,500.00 PLN | 2,500.00 PLN |
| NNW | 300.00 PLN | 800.00 PLN | 450.00 PLN |

## 💰 Rating Tables

### Premium Calculation Rules

#### OC (Obligatory Civil Liability)
| Vehicle Type | Engine Min | Engine Max | Base Premium | Risk Multiplier |
|--------------|------------|------------|--------------|-----------------|
| PASSENGER_CAR | 0 | 1400 | 800.00 | 1.0 |
| PASSENGER_CAR | 1401 | 2000 | 1000.00 | 1.2 |
| PASSENGER_CAR | 2001 | 3000 | 1200.00 | 1.4 |
| PASSENGER_CAR | 3001 | 999999 | 1500.00 | 1.6 |
| SUV | 0 | 2500 | 1100.00 | 1.3 |
| SUV | 2501 | 999999 | 1400.00 | 1.5 |

#### AC (Auto Casco)
| Vehicle Type | Engine Min | Engine Max | Base Premium | Risk Multiplier |
|--------------|------------|------------|--------------|-----------------|
| PASSENGER_CAR | 0 | 1400 | 1500.00 | 1.0 |
| PASSENGER_CAR | 1401 | 2000 | 2000.00 | 1.3 |
| PASSENGER_CAR | 2001 | 3000 | 2500.00 | 1.5 |
| PASSENGER_CAR | 3001 | 999999 | 3000.00 | 1.8 |
| SUV | 0 | 2500 | 2200.00 | 1.4 |
| SUV | 2501 | 999999 | 2800.00 | 1.6 |

#### NNW (Uninsured Drivers)
| Vehicle Type | Engine Min | Engine Max | Base Premium | Risk Multiplier |
|--------------|------------|------------|--------------|-----------------|
| PASSENGER_CAR | 0 | 2000 | 300.00 | 1.0 |
| PASSENGER_CAR | 2001 | 999999 | 400.00 | 1.2 |
| SUV | 0 | 999999 | 450.00 | 1.3 |

## 🔄 Data Generation Process

### Migration Files
The sample data is inserted through Flyway migrations:

1. **V9__Insert_rating_data.sql** - Rating table rules
2. **V10__Insert_sample_users.sql** - User accounts
3. **V11__Insert_sample_clients.sql** - Client information
4. **V12__Insert_sample_vehicles.sql** - Vehicle data
5. **V13__Insert_sample_policies.sql** - Initial policies
6. **V18__Add_sample_data_fixed.sql** - Additional policies and fixes

### Data Relationships
- Each policy is linked to exactly one client and one vehicle
- Clients can have multiple policies
- Vehicles can have multiple policies (different types or time periods)
- Premium calculations use rating tables based on vehicle specifications

## 🧪 Testing Scenarios

### User Authentication Testing
```bash
# Test admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@insurance.com","password":"admin123"}'

# Test operator login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"mike.johnson@insurance.com","password":"password123"}'
```

### Policy Management Testing
```bash
# Get all policies
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/policies

# Get specific policy
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/policies/1

# Search policies by client
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/policies?clientId=1"
```

### Data Validation Testing
```sql
-- Check data integrity
SELECT COUNT(*) as total_policies FROM policies;
SELECT COUNT(*) as active_policies FROM policies WHERE status = 'ACTIVE';
SELECT COUNT(*) as total_clients FROM clients;
SELECT COUNT(*) as total_vehicles FROM vehicles;

-- Check relationships
SELECT COUNT(*) as policies_with_clients 
FROM policies p 
JOIN clients c ON p.client_id = c.id;

SELECT COUNT(*) as policies_with_vehicles 
FROM policies p 
JOIN vehicles v ON p.vehicle_id = v.id;
```

## 🔧 Data Customization

### Adding More Sample Data

#### New Clients
```sql
INSERT INTO clients (full_name, pesel, address, email, phone_number)
VALUES 
    ('Katarzyna Lewandowska', '88042187654', 'ul. Słoneczna 15, 30-001 Kraków', 'katarzyna.lewandowska@email.com', '+48567890123'),
    ('Michał Dąbrowski', '91111298765', 'ul. Parkowa 8, 80-001 Gdańsk', 'michal.dabrowski@email.com', '+48678901234');
```

#### New Vehicles
```sql
INSERT INTO vehicles (make, model, registration_number, vin, year_of_manufacture, engine_capacity, power)
VALUES 
    ('Skoda', 'Octavia', 'KR99999', '1HGBH41JXMN109999', 2022, 1400, 115),
    ('Hyundai', 'i30', 'WA88888', '2HGBH41JXMN108888', 2021, 1600, 130);
```

#### New Policies
```sql
INSERT INTO policies (policy_number, client_id, vehicle_id, insurance_type, start_date, end_date, premium, discount_surcharge, status)
VALUES 
    ('OC-2024-999999', 32, 44, 'OC', '2024-01-01', '2024-12-31', 950.00, 0.00, 'ACTIVE'),
    ('AC-2024-999999', 32, 44, 'AC', '2024-01-01', '2024-12-31', 1800.00, -100.00, 'ACTIVE');
```

### Resetting Sample Data
```bash
# Reset database with fresh sample data
docker-compose down -v
docker-compose up -d postgres
# Wait for database initialization
sleep 30
docker-compose up -d backend
```

## 📊 Data Analysis Queries

### Policy Statistics
```sql
-- Policies by insurance type
SELECT insurance_type, COUNT(*) as count, AVG(premium) as avg_premium
FROM policies 
GROUP BY insurance_type;

-- Policies by status
SELECT status, COUNT(*) as count
FROM policies 
GROUP BY status;

-- Monthly policy creation
SELECT DATE_TRUNC('month', created_at) as month, COUNT(*) as policies_created
FROM policies 
GROUP BY DATE_TRUNC('month', created_at)
ORDER BY month;
```

### Client Analysis
```sql
-- Clients by city
SELECT SUBSTRING(address FROM '([^,]+)$') as city, COUNT(*) as client_count
FROM clients 
GROUP BY SUBSTRING(address FROM '([^,]+)$')
ORDER BY client_count DESC;

-- Clients with multiple policies
SELECT c.full_name, COUNT(p.id) as policy_count
FROM clients c
JOIN policies p ON c.id = p.client_id
GROUP BY c.id, c.full_name
HAVING COUNT(p.id) > 1
ORDER BY policy_count DESC;
```

### Vehicle Analysis
```sql
-- Vehicle distribution by make
SELECT make, COUNT(*) as count
FROM vehicles 
GROUP BY make
ORDER BY count DESC;

-- Average engine capacity by make
SELECT make, AVG(engine_capacity) as avg_engine_capacity, AVG(power) as avg_power
FROM vehicles 
GROUP BY make
ORDER BY avg_engine_capacity DESC;
```

---

**Related Documentation:**
- [Database Schema](schema.md)
- [Migration Guide](migrations.md)
- [API Documentation](../api/endpoints.md)