# Database Migration Guide

Complete guide for managing database migrations in the Insurance Backoffice System.

## 🔄 Migration Overview

The system uses Flyway for database migrations, providing version control for database schema changes.

## 📁 Migration Files Location

```
backend/src/main/resources/db/migration/
├── V1__Create_initial_schema.sql
├── V2__Add_users_table.sql
├── V3__Add_clients_table.sql
├── V4__Add_vehicles_table.sql
├── V5__Add_policies_table.sql
├── V6__Add_rating_tables.sql
├── V7__Add_indexes.sql
├── V8__Add_constraints.sql
├── V9__Insert_rating_data.sql
├── V10__Insert_sample_users.sql
├── V11__Insert_sample_clients.sql
├── V12__Insert_sample_vehicles.sql
├── V13__Insert_sample_policies.sql
└── V18__Add_sample_data_fixed.sql
```

## 🏗️ Migration Naming Convention

### File Naming Pattern
```
V{version}__{description}.sql
```

**Examples:**
- `V1__Create_initial_schema.sql`
- `V2__Add_users_table.sql`
- `V15__Update_policy_constraints.sql`

### Version Numbering
- Use sequential integers (1, 2, 3, ...)
- Never reuse version numbers
- Leave gaps for hotfixes if needed (10, 20, 30, ...)

## 📝 Migration File Structure

### Schema Changes
```sql
-- V15__Update_policy_constraints.sql
-- Description: Add new constraints to policies table
-- Author: Developer Name
-- Date: 2024-01-15

-- Add check constraint for premium amount
ALTER TABLE policies 
ADD CONSTRAINT chk_policies_premium_positive 
CHECK (premium > 0);

-- Add check constraint for date range
ALTER TABLE policies 
ADD CONSTRAINT chk_policies_date_range 
CHECK (end_date > start_date);

-- Update existing data if needed
UPDATE policies 
SET premium = 100.00 
WHERE premium <= 0;
```

### Data Changes
```sql
-- V16__Insert_new_rating_data.sql
-- Description: Add new rating table entries for motorcycles
-- Author: Developer Name
-- Date: 2024-01-16

INSERT INTO rating_tables (insurance_type, vehicle_type, engine_capacity_min, engine_capacity_max, base_premium, risk_multiplier)
VALUES 
    ('OC', 'MOTORCYCLE', 0, 125, 300.00, 0.8),
    ('OC', 'MOTORCYCLE', 126, 250, 400.00, 1.0),
    ('OC', 'MOTORCYCLE', 251, 500, 600.00, 1.2),
    ('OC', 'MOTORCYCLE', 501, 999999, 800.00, 1.5);
```

## 🚀 Running Migrations

### Automatic Migration (Recommended)
Migrations run automatically when the application starts:

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

### Manual Migration
```bash
# Using Gradle
./gradlew flywayMigrate

# Using Docker
docker-compose exec backend ./gradlew flywayMigrate

# Using Flyway CLI
flyway -url=jdbc:postgresql://localhost:5432/insurance_db \
       -user=insurance_user \
       -password=insurance_password \
       migrate
```

## 🔍 Migration Status

### Check Migration Status
```bash
# Using Gradle
./gradlew flywayInfo

# Using Flyway CLI
flyway -url=jdbc:postgresql://localhost:5432/insurance_db \
       -user=insurance_user \
       -password=insurance_password \
       info
```

### View Migration History
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Example Output:**
```
installed_rank | version | description           | type | script                    | checksum    | installed_by | installed_on        | execution_time | success
1              | 1       | Create initial schema | SQL  | V1__Create_initial_schema.sql | 1234567890 | insurance_user | 2024-01-01 10:00:00 | 45            | true
2              | 2       | Add users table       | SQL  | V2__Add_users_table.sql      | 2345678901 | insurance_user | 2024-01-01 10:00:01 | 12            | true
```

## 🛠️ Development Workflow

### Creating New Migrations

1. **Identify the Change**
   - Schema modification
   - Data update
   - Index creation
   - Constraint addition

2. **Create Migration File**
   ```bash
   # Create new migration file
   touch backend/src/main/resources/db/migration/V19__Add_policy_notes.sql
   ```

3. **Write Migration Script**
   ```sql
   -- V19__Add_policy_notes.sql
   -- Add notes field to policies table
   
   ALTER TABLE policies 
   ADD COLUMN notes TEXT;
   
   -- Add index for text search
   CREATE INDEX idx_policies_notes ON policies USING gin(to_tsvector('english', notes));
   ```

4. **Test Migration**
   ```bash
   # Test on development database
   ./gradlew flywayMigrate
   
   # Verify changes
   ./gradlew flywayInfo
   ```

5. **Commit Changes**
   ```bash
   git add backend/src/main/resources/db/migration/V19__Add_policy_notes.sql
   git commit -m "Add notes field to policies table"
   ```

## 🔄 Rollback Strategies

### Flyway Rollback (Commercial Feature)
```bash
# Rollback to specific version
flyway undo -target=18

# Rollback last migration
flyway undo
```

### Manual Rollback
Create reverse migration:
```sql
-- V20__Remove_policy_notes.sql
-- Remove notes field from policies table (rollback V19)

DROP INDEX IF EXISTS idx_policies_notes;
ALTER TABLE policies DROP COLUMN IF EXISTS notes;
```

### Database Backup Strategy
```bash
# Before major migrations
docker exec insurance-postgres pg_dump -U insurance_user insurance_db > backup_before_v19.sql

# Restore if needed
docker exec -i insurance-postgres psql -U insurance_user insurance_db < backup_before_v19.sql
```

## 🚨 Common Issues and Solutions

### Migration Checksum Mismatch
```
ERROR: Migration checksum mismatch for migration version 15
```

**Solution:**
```bash
# Repair migration history
./gradlew flywayRepair

# Or update checksum manually
UPDATE flyway_schema_history 
SET checksum = 1234567890 
WHERE version = '15';
```

### Failed Migration
```
ERROR: Migration V16__Insert_new_rating_data.sql failed
```

**Solution:**
```bash
# Check migration status
./gradlew flywayInfo

# Fix the issue and mark as resolved
UPDATE flyway_schema_history 
SET success = true 
WHERE version = '16';

# Or delete failed entry and retry
DELETE FROM flyway_schema_history 
WHERE version = '16';

./gradlew flywayMigrate
```

### Out-of-Order Migrations
```
ERROR: Detected resolved migration not applied to database: 15.1
```

**Solution:**
```yaml
# Allow out-of-order migrations
spring:
  flyway:
    out-of-order: true
```

## 🔒 Production Migration Best Practices

### Pre-Migration Checklist
- [ ] Create database backup
- [ ] Test migration on staging environment
- [ ] Verify migration script syntax
- [ ] Check for data conflicts
- [ ] Plan rollback strategy
- [ ] Schedule maintenance window

### Migration Execution
```bash
# 1. Create backup
docker exec insurance-postgres pg_dump -U insurance_user insurance_db > production_backup_$(date +%Y%m%d_%H%M%S).sql

# 2. Run migration
docker-compose exec backend ./gradlew flywayMigrate

# 3. Verify migration
docker-compose exec backend ./gradlew flywayInfo

# 4. Test application
curl http://localhost:8080/actuator/health
```

### Post-Migration Verification
```sql
-- Check table structure
\d policies

-- Verify data integrity
SELECT COUNT(*) FROM policies;
SELECT COUNT(*) FROM clients;
SELECT COUNT(*) FROM vehicles;

-- Check constraints
SELECT conname, contype FROM pg_constraint WHERE conrelid = 'policies'::regclass;
```

## 📊 Migration Performance

### Large Data Migrations
```sql
-- Use batch processing for large updates
DO $$
DECLARE
    batch_size INTEGER := 1000;
    total_rows INTEGER;
    processed INTEGER := 0;
BEGIN
    SELECT COUNT(*) INTO total_rows FROM policies WHERE notes IS NULL;
    
    WHILE processed < total_rows LOOP
        UPDATE policies 
        SET notes = 'Migrated policy'
        WHERE id IN (
            SELECT id FROM policies 
            WHERE notes IS NULL 
            LIMIT batch_size
        );
        
        processed := processed + batch_size;
        RAISE NOTICE 'Processed % of % rows', processed, total_rows;
        
        -- Commit batch
        COMMIT;
    END LOOP;
END $$;
```

### Index Creation
```sql
-- Create indexes concurrently to avoid locks
CREATE INDEX CONCURRENTLY idx_policies_created_at ON policies(created_at);

-- Drop old indexes after verification
DROP INDEX IF EXISTS old_index_name;
```

## 🔧 Troubleshooting

### Reset Migration History
```sql
-- DANGER: Only use in development
DROP TABLE flyway_schema_history;
```

### Clean Database
```bash
# Reset entire database (development only)
docker-compose down -v
docker-compose up -d postgres
# Wait for database to start
docker-compose up -d backend
```

### Validate Migrations
```bash
# Validate all migrations
./gradlew flywayValidate

# Check for pending migrations
./gradlew flywayInfo | grep "Pending"
```

## 📚 Migration Examples

### Adding New Table
```sql
-- V21__Create_policy_documents_table.sql
CREATE TABLE policy_documents (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policy_documents_policy_id ON policy_documents(policy_id);
CREATE INDEX idx_policy_documents_type ON policy_documents(document_type);
```

### Modifying Existing Column
```sql
-- V22__Extend_client_phone_length.sql
-- Extend phone number field to support international formats

-- Add new column
ALTER TABLE clients ADD COLUMN phone_number_new VARCHAR(30);

-- Copy data with transformation
UPDATE clients 
SET phone_number_new = phone_number 
WHERE phone_number IS NOT NULL;

-- Drop old column
ALTER TABLE clients DROP COLUMN phone_number;

-- Rename new column
ALTER TABLE clients RENAME COLUMN phone_number_new TO phone_number;
```

---

**Related Documentation:**
- [Database Schema](schema.md)
- [Sample Data](sample-data.md)
- [Development Setup](../development/setup.md)