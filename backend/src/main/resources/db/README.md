# Database Migration and Data Seeding

This directory contains database migration scripts and data seeding configurations for the Insurance Backoffice System.

## Migration Scripts

The migration scripts are executed in order by Flyway during application startup:

### Schema Migrations
- `V1__Create_users_table.sql` - Creates the users table for system authentication
- `V2__Create_clients_table.sql` - Creates the clients table for policy holders
- `V3__Create_vehicles_table.sql` - Creates the vehicles table for insured vehicles
- `V4__Create_policies_table.sql` - Creates the policies table for insurance policies
- `V5__Create_policy_details_table.sql` - Creates policy details table for insurance-specific data
- `V6__Create_rating_tables_table.sql` - Creates rating tables for premium calculations
- `V7__Add_performance_indexes.sql` - Adds database indexes for performance optimization

### Data Seeding Migrations
- `V8__Insert_rating_table_seed_data.sql` - Populates rating tables with calculation factors
- `V9__Insert_sample_users.sql` - Sample users for development and testing
- `V10__Insert_sample_clients.sql` - Sample clients with realistic Polish data
- `V11__Insert_sample_vehicles.sql` - Sample vehicles covering various scenarios
- `V12__Insert_sample_policies.sql` - Sample policies for all insurance types
- `V13__Insert_production_seed_data.sql` - Minimal production seed data

## Data Seeding Strategy

The system uses a multi-layered approach to data seeding:

### 1. Database Migrations (Always Applied)
- **Rating Tables** (V8): Essential business data for premium calculations
- **Production Seed Data** (V13): Minimal data required for production deployment

### 2. Development Sample Data (Development/Test Only)
- **Sample Users** (V9): Admin and operator accounts for testing
- **Sample Clients** (V10): Diverse client data for testing scenarios
- **Sample Vehicles** (V11): Various vehicle types and specifications
- **Sample Policies** (V12): Complete policy examples for all insurance types

### 3. Programmatic Seeding (Development/Test Only)
- **DataSeedingService**: Java-based seeding service that runs on application startup
- **TestDataGenerator**: Utility for generating test data programmatically

## Configuration

### Application Properties

Data seeding behavior can be controlled through application properties:

```properties
# Enable/disable data seeding
app.data-seeding.enabled=true

# Control what data to seed
app.data-seeding.seed-users=true
app.data-seeding.seed-clients=true
app.data-seeding.seed-vehicles=true
app.data-seeding.seed-policies=true
app.data-seeding.seed-rating-tables=false

# Force seeding even if data exists
app.data-seeding.force-seeding=false

# Number of sample records to create
app.data-seeding.sample-size=10
```

### Environment-Specific Behavior

- **Production**: Only essential migrations (V1-V7, V8, V13) are applied
- **Development**: All migrations including sample data (V1-V13) plus programmatic seeding
- **Test**: Same as development, with additional test data generation utilities

## Sample Data Overview

### Users
- **Admin Users**: 2 admin accounts for system management
- **Operator Users**: 4 operator accounts for policy management
- **Default Passwords**: Documented in migration files (must be changed in production)

### Clients
- **20 Sample Clients**: Covering various demographics and scenarios
- **Realistic Data**: Polish names, addresses, PESEL numbers, contact information
- **Edge Cases**: Different name formats, age groups, address types

### Vehicles
- **30 Sample Vehicles**: Various makes, models, years, and specifications
- **Rating Categories**: Different engine sizes, power ratings, and ages for testing calculations
- **Vehicle Types**: From small city cars to luxury vehicles and commercial vehicles

### Policies
- **28 Sample Policies**: Covering all insurance types and statuses
- **Insurance Types**: OC (liability), AC (comprehensive), NNW (personal accident)
- **Policy Statuses**: Active, canceled, expired policies for lifecycle testing
- **Premium Scenarios**: Various premium amounts, discounts, and surcharges

### Rating Tables
- **Comprehensive Rating Data**: Factors for all insurance types
- **Vehicle-Based Factors**: Age, engine capacity, power ratings
- **Regional Factors**: Urban, suburban, rural multipliers
- **Temporal Factors**: Seasonal adjustments and historical data

## Usage Examples

### Development Environment
```bash
# Start with sample data (default behavior in dev profile)
./gradlew bootRun --args="--spring.profiles.active=dev"
```

### Testing Environment
```bash
# Run tests with fresh sample data
./gradlew test
```

### Production Deployment
```bash
# Production deployment (only essential data)
./gradlew bootRun --args="--spring.profiles.active=prod"
```

### Custom Data Generation
```java
@Autowired
private TestDataGenerator testDataGenerator;

// Generate test users
List<User> testUsers = testDataGenerator.generateUsers(5, UserRole.OPERATOR);

// Generate test policies
Policy testPolicy = testDataGenerator.generatePolicy(client, vehicle, InsuranceType.OC);
```

## Security Considerations

### Production Deployment
1. **Change Default Passwords**: All default passwords must be changed after deployment
2. **Remove Sample Data**: Sample data migrations should not run in production
3. **Secure Database Access**: Ensure proper database security configuration
4. **Monitor Data Access**: Set up logging and monitoring for data operations

### Development Security
1. **No Real Data**: Never use real client data in development/test environments
2. **Secure Test Credentials**: Even test passwords should follow security guidelines
3. **Environment Isolation**: Keep development and production databases separate

## Troubleshooting

### Common Issues

1. **Migration Failures**
   - Check database connectivity
   - Verify migration script syntax
   - Ensure proper permissions

2. **Seeding Failures**
   - Check application profile configuration
   - Verify data seeding is enabled
   - Review application logs for errors

3. **Duplicate Data**
   - Seeding service checks for existing data
   - Use `force-seeding=true` to override (development only)
   - Clear database and restart for clean state

### Debugging

Enable debug logging for data seeding:
```properties
logging.level.com.insurance.backoffice.infrastructure.DataSeedingService=DEBUG
logging.level.org.flywaydb=DEBUG
```

## Maintenance

### Adding New Sample Data
1. Create new migration script with appropriate version number
2. Follow existing naming conventions
3. Include proper documentation and comments
4. Test in development environment before deployment

### Updating Rating Tables
1. Create new migration script (don't modify existing ones)
2. Use proper validity dates for rating factors
3. Test premium calculations with new rates
4. Document business rationale for changes

### Performance Considerations
1. **Indexes**: Ensure proper indexes exist for seeded data queries
2. **Batch Operations**: Use batch inserts for large data sets
3. **Memory Usage**: Monitor memory usage during seeding operations
4. **Startup Time**: Consider impact on application startup time