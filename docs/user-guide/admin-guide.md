# Administrator Guide

Complete guide for system administrators of the Insurance Backoffice System.

## 🔐 Administrator Overview

As an administrator, you have full access to all system features including user management, system configuration, and advanced operations.

## 👥 User Management

### User Roles

#### ADMIN Role
- Full system access
- User management capabilities
- System configuration access
- All policy operations
- System monitoring and maintenance

#### OPERATOR Role
- Policy management only
- Cannot manage users
- Cannot access system settings
- Limited to operational tasks

### Managing User Accounts

#### Creating New Users
1. Navigate to **Users** section
2. Click **"Add New User"**
3. Fill in required information:
   - **Full Name**: Display name for the user
   - **Email**: Must be unique, used for login
   - **Password**: Initial password (user should change on first login)
   - **Role**: ADMIN or OPERATOR
4. Click **"Create User"**

#### Editing User Information
1. Find the user in the users list
2. Click **Edit** button
3. Modify fields as needed:
   - Full Name
   - Email (must remain unique)
   - Role (be careful when changing admin roles)
4. Click **"Update User"**

#### Password Management
- Users can change their own passwords through profile settings
- Administrators can reset passwords for any user
- Passwords must meet security requirements:
  - Minimum 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number

#### Deactivating Users
1. Find the user in the users list
2. Click **"Deactivate"** button
3. Confirm the action
4. User will be unable to log in but data remains intact

### User Security Best Practices
- Regularly review user accounts and remove unused ones
- Ensure strong passwords are used
- Monitor login activities for suspicious behavior
- Limit admin privileges to necessary personnel only

## 📊 System Monitoring

### Dashboard Metrics
Monitor key system metrics from the admin dashboard:

#### Policy Statistics
- Total policies in system
- Active vs. inactive policies
- Policies by insurance type (OC, AC, NNW)
- Recent policy activity

#### User Activity
- Active user sessions
- Recent login activity
- Failed login attempts
- User role distribution

#### System Health
- Database connection status
- Application performance metrics
- Error rates and logs
- System resource usage

### Health Checks
Regular system health monitoring:

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check detailed health information
curl http://localhost:8080/actuator/health/detailed

# Monitor system metrics
curl http://localhost:8080/actuator/metrics
```

## 🗄️ Database Administration

### Database Monitoring

#### Connection Status
```sql
-- Check active connections
SELECT count(*) as active_connections 
FROM pg_stat_activity 
WHERE datname = 'insurance_db';

-- Check connection limits
SELECT setting as max_connections 
FROM pg_settings 
WHERE name = 'max_connections';
```

#### Performance Monitoring
```sql
-- Check slow queries
SELECT query, mean_time, calls, total_time
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Check table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Database Maintenance

#### Regular Maintenance Tasks
```sql
-- Update table statistics (run weekly)
ANALYZE;

-- Vacuum tables (run monthly)
VACUUM ANALYZE;

-- Reindex tables (run quarterly)
REINDEX DATABASE insurance_db;
```

#### Backup Procedures
```bash
# Create database backup
docker exec insurance-postgres pg_dump -U insurance_user insurance_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup
docker exec -i insurance-postgres psql -U insurance_user insurance_db < backup_file.sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
docker exec insurance-postgres pg_dump -U insurance_user insurance_db > "$BACKUP_DIR/insurance_backup_$DATE.sql"
# Keep only last 7 days of backups
find $BACKUP_DIR -name "insurance_backup_*.sql" -mtime +7 -delete
```

## 🔧 System Configuration

### Environment Configuration

#### Production Settings
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: false

logging:
  level:
    com.insurance.backoffice: INFO
    org.springframework.security: WARN
```

#### Security Configuration
```yaml
jwt:
  expiration: 3600000  # 1 hour for production
  
cors:
  allowed-origins: https://your-production-domain.com
  allowed-methods: GET,POST,PUT,DELETE
  allow-credentials: true
```

### Rating Tables Management

#### Viewing Current Rates
```sql
SELECT * FROM rating_tables 
ORDER BY insurance_type, vehicle_type, engine_capacity_min;
```

#### Updating Premium Rates
```sql
-- Update base premiums for OC insurance
UPDATE rating_tables 
SET base_premium = base_premium * 1.05  -- 5% increase
WHERE insurance_type = 'OC';

-- Add new vehicle type
INSERT INTO rating_tables (insurance_type, vehicle_type, engine_capacity_min, engine_capacity_max, base_premium, risk_multiplier)
VALUES ('OC', 'MOTORCYCLE', 0, 250, 400.00, 0.9);
```

## 📈 Performance Optimization

### Application Performance

#### JVM Tuning
```yaml
# docker-compose.yml
services:
  backend:
    environment:
      - JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

#### Database Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### Database Performance

#### Index Optimization
```sql
-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;

-- Create missing indexes
CREATE INDEX CONCURRENTLY idx_policies_created_at ON policies(created_at);
CREATE INDEX CONCURRENTLY idx_policies_status_type ON policies(status, insurance_type);
```

#### Query Optimization
```sql
-- Analyze slow queries
EXPLAIN ANALYZE 
SELECT p.*, c.full_name, v.registration_number
FROM policies p
JOIN clients c ON p.client_id = c.id
JOIN vehicles v ON p.vehicle_id = v.id
WHERE p.status = 'ACTIVE'
AND p.end_date > CURRENT_DATE;
```

## 🛡️ Security Management

### Authentication Security

#### Password Policies
- Minimum 8 characters
- Must contain uppercase, lowercase, and numbers
- Password expiration (optional)
- Account lockout after failed attempts

#### JWT Token Management
```yaml
jwt:
  secret: ${JWT_SECRET}  # Use strong, unique secret
  expiration: 3600000    # 1 hour for production
  refresh-token-expiration: 86400000  # 24 hours
```

### Access Control

#### Role-Based Permissions
```java
// Example security configuration
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    // Admin only endpoint
}

@PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
public ResponseEntity<List<Policy>> getAllPolicies() {
    // Admin and Operator access
}
```

#### API Security
- All endpoints require authentication (except health checks)
- CORS configured for specific origins only
- Rate limiting implemented
- Input validation on all endpoints

### Data Protection

#### Sensitive Data Handling
- Passwords hashed with BCrypt
- PESEL numbers should be encrypted in production
- Audit logging for sensitive operations
- Regular security updates

#### GDPR Compliance
- Data retention policies
- Right to be forgotten implementation
- Data export capabilities
- Privacy by design principles

## 📊 Reporting and Analytics

### System Reports

#### Policy Reports
```sql
-- Monthly policy creation report
SELECT 
    DATE_TRUNC('month', created_at) as month,
    insurance_type,
    COUNT(*) as policies_created,
    SUM(premium) as total_premium
FROM policies 
WHERE created_at >= CURRENT_DATE - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', created_at), insurance_type
ORDER BY month DESC, insurance_type;

-- Top clients by policy count
SELECT 
    c.full_name,
    COUNT(p.id) as policy_count,
    SUM(p.premium) as total_premium
FROM clients c
JOIN policies p ON c.id = p.client_id
GROUP BY c.id, c.full_name
ORDER BY policy_count DESC
LIMIT 10;
```

#### User Activity Reports
```sql
-- User login activity (requires audit table)
SELECT 
    u.full_name,
    u.email,
    u.role,
    COUNT(al.id) as login_count,
    MAX(al.login_time) as last_login
FROM users u
LEFT JOIN audit_logs al ON u.id = al.user_id AND al.action = 'LOGIN'
WHERE al.login_time >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY u.id, u.full_name, u.email, u.role
ORDER BY login_count DESC;
```

### Export Capabilities
```bash
# Export policies to CSV
psql -U insurance_user -d insurance_db -c "COPY (SELECT * FROM policies) TO STDOUT WITH CSV HEADER" > policies_export.csv

# Export clients to CSV
psql -U insurance_user -d insurance_db -c "COPY (SELECT * FROM clients) TO STDOUT WITH CSV HEADER" > clients_export.csv
```

## 🚨 Incident Response

### Common Issues

#### High CPU Usage
1. Check application logs for errors
2. Monitor database query performance
3. Review recent code deployments
4. Scale resources if needed

#### Database Connection Issues
1. Check connection pool settings
2. Monitor active connections
3. Restart application if needed
4. Check database server health

#### Authentication Problems
1. Verify JWT secret configuration
2. Check user account status
3. Review security logs
4. Clear user sessions if needed

### Emergency Procedures

#### System Recovery
```bash
# Stop all services
docker-compose down

# Restore from backup
docker exec -i insurance-postgres psql -U insurance_user insurance_db < latest_backup.sql

# Restart services
docker-compose up -d

# Verify system health
curl http://localhost:8080/actuator/health
```

#### Data Recovery
```bash
# Point-in-time recovery (if available)
pg_restore -U insurance_user -d insurance_db -t policies backup_file.dump

# Verify data integrity
psql -U insurance_user -d insurance_db -c "SELECT COUNT(*) FROM policies;"
```

## 📚 Maintenance Schedules

### Daily Tasks
- [ ] Check system health status
- [ ] Review error logs
- [ ] Monitor resource usage
- [ ] Verify backup completion

### Weekly Tasks
- [ ] Update database statistics (ANALYZE)
- [ ] Review user activity logs
- [ ] Check for security updates
- [ ] Performance monitoring review

### Monthly Tasks
- [ ] Database maintenance (VACUUM)
- [ ] Security audit
- [ ] Backup verification
- [ ] Capacity planning review

### Quarterly Tasks
- [ ] Database reindexing
- [ ] Security penetration testing
- [ ] Disaster recovery testing
- [ ] Performance optimization review

---

**Related Documentation:**
- [User Manual](user-manual.md)
- [Troubleshooting Guide](troubleshooting.md)
- [Database Schema](../database/schema.md)
- [API Documentation](../api/endpoints.md)