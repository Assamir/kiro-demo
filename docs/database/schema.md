# Database Schema Documentation

Complete database schema reference for the Insurance Backoffice System.

## 🗄️ Database Overview

- **Database**: PostgreSQL 15
- **Schema**: `public`
- **Character Set**: UTF-8
- **Collation**: `en_US.utf8`

## 📊 Entity Relationship Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    Users    │    │   Clients   │    │  Vehicles   │
│             │    │             │    │             │
│ id (PK)     │    │ id (PK)     │    │ id (PK)     │
│ email       │    │ full_name   │    │ make        │
│ password    │    │ pesel       │    │ model       │
│ full_name   │    │ address     │    │ reg_number  │
│ role        │    │ email       │    │ vin         │
│ created_at  │    │ phone       │    │ year        │
│ updated_at  │    │ created_at  │    │ engine_cap  │
└─────────────┘    │ updated_at  │    │ power       │
                   └─────────────┘    │ created_at  │
                           │          │ updated_at  │
                           │          └─────────────┘
                           │                  │
                           │                  │
                   ┌───────▼──────────────────▼───────┐
                   │           Policies              │
                   │                                 │
                   │ id (PK)                         │
                   │ policy_number                   │
                   │ client_id (FK)                  │
                   │ vehicle_id (FK)                 │
                   │ insurance_type                  │
                   │ start_date                      │
                   │ end_date                        │
                   │ premium                         │
                   │ discount_surcharge              │
                   │ status                          │
                   │ created_at                      │
                   │ updated_at                      │
                   └─────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                Rating Tables                        │
│                                                     │
│ id (PK)                                             │
│ insurance_type                                      │
│ vehicle_type                                        │
│ engine_capacity_min                                 │
│ engine_capacity_max                                 │
│ base_premium                                        │
│ risk_multiplier                                     │
│ created_at                                          │
│ updated_at                                          │
└─────────────────────────────────────────────────────┘
```

## 📋 Table Definitions

### Users Table
Stores system user accounts with authentication and authorization information.

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_users_email` - Unique index on email for login
- `idx_users_role` - Index on role for authorization queries

### Clients Table
Stores client (policyholder) information.

```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    pesel VARCHAR(11) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_clients_pesel` - Unique index on PESEL (Polish national ID)
- `idx_clients_email` - Index on email for searches
- `idx_clients_full_name` - Index on full name for searches

**Constraints:**
- PESEL must be exactly 11 digits
- Email must be valid format (if provided)

### Vehicles Table
Stores vehicle information for insurance policies.

```sql
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    vin VARCHAR(17) NOT NULL UNIQUE,
    year_of_manufacture INTEGER NOT NULL,
    engine_capacity INTEGER NOT NULL,
    power INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_vehicles_registration` - Unique index on registration number
- `idx_vehicles_vin` - Unique index on VIN
- `idx_vehicles_make_model` - Composite index for searches

**Constraints:**
- VIN must be exactly 17 characters
- Year must be between 1900 and current year + 1
- Engine capacity must be positive
- Power must be positive

### Policies Table
Main table storing insurance policy information.

```sql
CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    insurance_type VARCHAR(10) NOT NULL CHECK (insurance_type IN ('OC', 'AC', 'NNW')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    premium DECIMAL(10,2) NOT NULL,
    discount_surcharge DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELED', 'EXPIRED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_policies_policy_number` - Unique index on policy number
- `idx_policies_client_id` - Index on client for client's policies
- `idx_policies_vehicle_id` - Index on vehicle for vehicle's policies
- `idx_policies_insurance_type` - Index on insurance type
- `idx_policies_status` - Index on status for active policies
- `idx_policies_dates` - Composite index on start_date and end_date

**Constraints:**
- End date must be after start date
- Premium must be positive
- Policy number follows format: {TYPE}-{YEAR}-{SEQUENCE}

### Rating Tables Table
Stores premium calculation rules based on vehicle and insurance type.

```sql
CREATE TABLE rating_tables (
    id BIGSERIAL PRIMARY KEY,
    insurance_type VARCHAR(10) NOT NULL CHECK (insurance_type IN ('OC', 'AC', 'NNW')),
    vehicle_type VARCHAR(50) NOT NULL,
    engine_capacity_min INTEGER NOT NULL,
    engine_capacity_max INTEGER NOT NULL,
    base_premium DECIMAL(10,2) NOT NULL,
    risk_multiplier DECIMAL(5,3) NOT NULL DEFAULT 1.000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_rating_tables_lookup` - Composite index on insurance_type, vehicle_type, engine_capacity_min, engine_capacity_max

## 🔗 Relationships

### Foreign Key Constraints

1. **policies.client_id → clients.id**
   - ON DELETE: RESTRICT (cannot delete client with active policies)
   - ON UPDATE: CASCADE

2. **policies.vehicle_id → vehicles.id**
   - ON DELETE: RESTRICT (cannot delete vehicle with active policies)
   - ON UPDATE: CASCADE

## 📈 Data Statistics

### Current Data Volume (Sample Data)
- **Users**: 8 records (3 admins, 5 operators)
- **Clients**: 31 records
- **Vehicles**: 43 records
- **Policies**: 443 records
  - OC: 172 policies
  - AC: 172 policies
  - NNW: 104 policies
- **Rating Tables**: 15 records

### Expected Production Volume
- **Users**: 50-100 records
- **Clients**: 10,000-50,000 records
- **Vehicles**: 15,000-75,000 records
- **Policies**: 100,000-500,000 records

## 🔍 Common Queries

### Find Active Policies for Client
```sql
SELECT p.*, c.full_name, v.registration_number
FROM policies p
JOIN clients c ON p.client_id = c.id
JOIN vehicles v ON p.vehicle_id = v.id
WHERE c.pesel = '85010112345'
  AND p.status = 'ACTIVE'
  AND p.end_date >= CURRENT_DATE;
```

### Calculate Premium for Vehicle
```sql
SELECT rt.base_premium * rt.risk_multiplier as calculated_premium
FROM rating_tables rt
JOIN vehicles v ON v.engine_capacity BETWEEN rt.engine_capacity_min AND rt.engine_capacity_max
WHERE rt.insurance_type = 'OC'
  AND rt.vehicle_type = 'PASSENGER_CAR'
  AND v.id = 1;
```

### Find Expiring Policies
```sql
SELECT p.*, c.full_name, c.email
FROM policies p
JOIN clients c ON p.client_id = c.id
WHERE p.status = 'ACTIVE'
  AND p.end_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days'
ORDER BY p.end_date;
```

## 🛡️ Security Considerations

### Data Encryption
- Passwords are hashed using BCrypt
- Sensitive data (PESEL) should be encrypted at rest in production
- Database connections use SSL/TLS

### Access Control
- Database user has minimal required permissions
- Application uses connection pooling
- Prepared statements prevent SQL injection

### Audit Trail
- All tables include created_at and updated_at timestamps
- Consider adding audit tables for sensitive operations in production

## 🔧 Maintenance

### Regular Maintenance Tasks
```sql
-- Update table statistics
ANALYZE;

-- Reindex tables (monthly)
REINDEX TABLE policies;
REINDEX TABLE clients;
REINDEX TABLE vehicles;

-- Clean up old data (if applicable)
DELETE FROM policies 
WHERE status = 'EXPIRED' 
AND end_date < CURRENT_DATE - INTERVAL '7 years';
```

### Performance Monitoring
```sql
-- Check table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check index usage
SELECT 
    indexrelname,
    idx_tup_read,
    idx_tup_fetch,
    idx_scan
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

---

**Related Documentation:**
- [Migration Guide](migrations.md)
- [Sample Data](sample-data.md)
- [API Documentation](../api/endpoints.md)