# Troubleshooting Guide

Complete troubleshooting guide for the Insurance Backoffice System.

## 🚨 Quick Diagnostics

### System Health Check
```bash
# Check all services
docker-compose ps

# Check backend health
curl http://localhost:8080/actuator/health

# Check frontend accessibility
curl http://localhost:3000

# Check database connectivity
docker exec insurance-postgres pg_isready -U insurance_user
```

### Expected Results
- All containers should show "Up" status
- Health check should return `{"status":"UP"}`
- Frontend should return HTML content
- Database should return "accepting connections"

## 🔧 Common Issues and Solutions

### 1. Services Won't Start

#### Symptoms
- `docker-compose up` fails
- Containers exit immediately
- Port binding errors

#### Solutions

**Check Docker Status**
```bash
# Verify Docker is running
docker --version
docker info

# Restart Docker if needed (Windows/Mac)
# Docker Desktop -> Restart
```

**Port Conflicts**
```bash
# Check what's using the ports
netstat -tulpn | grep :3000
netstat -tulpn | grep :8080
netstat -tulpn | grep :5432

# Kill processes using required ports
sudo kill -9 $(lsof -t -i:8080)
```

**Clean Docker Environment**
```bash
# Remove old containers and volumes
docker-compose down -v
docker system prune -a

# Rebuild and start
docker-compose build --no-cache
docker-compose up -d
```

### 2. Backend Won't Start

#### Symptoms
- Backend container keeps restarting
- Database connection errors
- Migration failures

#### Solutions

**Check Backend Logs**
```bash
docker logs insurance-backend --tail 50
```

**Common Backend Issues:**

**Database Connection Failed**
```bash
# Ensure database is running first
docker-compose up -d postgres
sleep 30
docker-compose up -d backend
```

**Migration Conflicts**
```bash
# Check migration status
docker exec insurance-postgres psql -U insurance_user -d insurance_db -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"

# If needed, reset database
docker-compose down -v
docker-compose up -d
```

**Java/Memory Issues**
```bash
# Increase memory limit in docker-compose.yml
services:
  backend:
    environment:
      - JAVA_OPTS=-Xmx2g -Xms1g
```

### 3. Frontend Issues

#### Symptoms
- White screen or loading forever
- API calls failing
- Login not working

#### Solutions

**Check Frontend Logs**
```bash
docker logs insurance-frontend --tail 20
```

**API Connection Issues**
```bash
# Verify backend is accessible from frontend
docker exec insurance-frontend curl http://backend:8080/actuator/health

# Check CORS configuration
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     http://localhost:8080/api/policies
```

**Browser Issues**
1. Clear browser cache and cookies
2. Try incognito/private mode
3. Check browser console for errors (F12)
4. Disable browser extensions

### 4. Database Issues

#### Symptoms
- Connection refused errors
- Data not persisting
- Migration errors

#### Solutions

**Check Database Status**
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check database logs
docker logs insurance-postgres --tail 20

# Test connection
docker exec insurance-postgres psql -U insurance_user -d insurance_db -c "SELECT 1;"
```

**Reset Database**
```bash
# Complete database reset
docker-compose down
docker volume rm insurance_postgres_data
docker-compose up -d postgres

# Wait for database to initialize
sleep 60
docker-compose up -d
```

**Manual Database Access**
```bash
# Connect to database
docker exec -it insurance-postgres psql -U insurance_user -d insurance_db

# Check tables
\dt

# Check data
SELECT COUNT(*) FROM policies;
SELECT COUNT(*) FROM clients;
SELECT COUNT(*) FROM vehicles;
```

### 5. Authentication Issues

#### Symptoms
- Cannot login
- "Invalid credentials" errors
- Token expired messages

#### Solutions

**Verify User Credentials**
```bash
# Check users in database
docker exec insurance-postgres psql -U insurance_user -d insurance_db -c "SELECT id, email, role FROM users;"

# Reset admin password (if needed)
docker exec insurance-postgres psql -U insurance_user -d insurance_db -c "UPDATE users SET password = '\$2a\$10\$rZ8R8qSHjxVKzf7wmGqIu.Bq8qM8qM8qM8qM8qM8qM8qM8qM8qM8qM' WHERE email = 'admin@insurance.com';"
```

**JWT Token Issues**
1. Check if JWT secret is configured correctly
2. Verify token expiration settings
3. Clear browser localStorage/sessionStorage
4. Try logging in again

**Browser Storage Issues**
```javascript
// Open browser console (F12) and run:
localStorage.clear();
sessionStorage.clear();
// Then refresh page and try logging in
```

### 6. Performance Issues

#### Symptoms
- Slow page loading
- API timeouts
- High memory usage

#### Solutions

**Check Resource Usage**
```bash
# Monitor Docker containers
docker stats

# Check system resources
htop  # or top on some systems
df -h  # disk usage
free -h  # memory usage
```

**Database Performance**
```sql
-- Check slow queries
SELECT query, mean_time, calls 
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

**Optimize Performance**
```bash
# Increase Docker memory limits
# Edit docker-compose.yml:
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 2G
        reservations:
          memory: 1G
```

### 7. Network Issues

#### Symptoms
- Services can't communicate
- External API calls fail
- DNS resolution errors

#### Solutions

**Check Docker Networks**
```bash
# List Docker networks
docker network ls

# Inspect network
docker network inspect insurance_insurance-network

# Test connectivity between containers
docker exec insurance-frontend ping backend
docker exec insurance-backend ping database
```

**Firewall Issues**
```bash
# Check if ports are accessible
telnet localhost 3000
telnet localhost 8080
telnet localhost 5432

# On Linux, check iptables
sudo iptables -L
```

## 🔍 Diagnostic Commands

### System Information
```bash
# Docker version and info
docker --version
docker-compose --version
docker info

# System resources
free -h
df -h
uname -a
```

### Service Status
```bash
# All services status
docker-compose ps

# Detailed service info
docker inspect insurance-backend
docker inspect insurance-frontend
docker inspect insurance-postgres
```

### Logs Analysis
```bash
# All logs
docker-compose logs

# Specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f backend

# Filter logs by time
docker-compose logs --since="2024-01-01T00:00:00" backend
```

### Database Diagnostics
```sql
-- Connection info
SELECT * FROM pg_stat_activity WHERE datname = 'insurance_db';

-- Database size
SELECT pg_size_pretty(pg_database_size('insurance_db'));

-- Table statistics
SELECT schemaname, tablename, n_tup_ins, n_tup_upd, n_tup_del 
FROM pg_stat_user_tables;

-- Index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

## 🛠️ Recovery Procedures

### Complete System Reset
```bash
# Stop all services
docker-compose down

# Remove all data (WARNING: This deletes all data!)
docker-compose down -v
docker system prune -a

# Rebuild and start fresh
docker-compose build --no-cache
docker-compose up -d
```

### Partial Recovery

#### Backend Only
```bash
# Restart backend service
docker-compose restart backend

# Rebuild backend if needed
docker-compose build backend
docker-compose up -d backend
```

#### Database Only
```bash
# Restart database
docker-compose restart postgres

# Reset database data only
docker-compose stop postgres
docker volume rm insurance_postgres_data
docker-compose up -d postgres
```

#### Frontend Only
```bash
# Restart frontend
docker-compose restart frontend

# Rebuild frontend if needed
docker-compose build frontend
docker-compose up -d frontend
```

## 📊 Monitoring and Alerts

### Health Monitoring
```bash
# Create health check script
#!/bin/bash
# health_check.sh

echo "Checking system health..."

# Check backend
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend: OK"
else
    echo "❌ Backend: FAILED"
fi

# Check frontend
if curl -f http://localhost:3000 > /dev/null 2>&1; then
    echo "✅ Frontend: OK"
else
    echo "❌ Frontend: FAILED"
fi

# Check database
if docker exec insurance-postgres pg_isready -U insurance_user > /dev/null 2>&1; then
    echo "✅ Database: OK"
else
    echo "❌ Database: FAILED"
fi
```

### Performance Monitoring
```bash
# Monitor resource usage
#!/bin/bash
# monitor.sh

echo "=== System Resources ==="
docker stats --no-stream

echo "=== Disk Usage ==="
df -h

echo "=== Memory Usage ==="
free -h

echo "=== Database Connections ==="
docker exec insurance-postgres psql -U insurance_user -d insurance_db -c "SELECT count(*) as active_connections FROM pg_stat_activity WHERE datname = 'insurance_db';"
```

## 🚨 Emergency Contacts

### Internal Support
- **System Administrator**: admin@company.com
- **Database Administrator**: dba@company.com
- **Development Team**: dev-team@company.com

### External Support
- **Docker Support**: https://docs.docker.com/support/
- **PostgreSQL Support**: https://www.postgresql.org/support/
- **Spring Boot Support**: https://spring.io/support

## 📋 Troubleshooting Checklist

### Before Contacting Support
- [ ] Checked system health endpoints
- [ ] Reviewed recent logs
- [ ] Verified all containers are running
- [ ] Tested basic functionality
- [ ] Documented error messages
- [ ] Noted when the issue started
- [ ] Identified what changed recently

### Information to Provide
- System version and environment
- Error messages (exact text)
- Steps to reproduce the issue
- Browser and version (for frontend issues)
- Recent changes or deployments
- Log files (if available)

## 🔧 Advanced Troubleshooting

### Debug Mode
```yaml
# Enable debug logging
logging:
  level:
    com.insurance.backoffice: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### Database Debug
```sql
-- Enable query logging
ALTER SYSTEM SET log_statement = 'all';
SELECT pg_reload_conf();

-- Check locks
SELECT * FROM pg_locks WHERE NOT granted;

-- Check blocking queries
SELECT 
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS current_statement_in_blocking_process
FROM pg_catalog.pg_locks blocked_locks
JOIN pg_catalog.pg_stat_activity blocked_activity ON blocked_activity.pid = blocked_locks.pid
JOIN pg_catalog.pg_locks blocking_locks ON blocking_locks.locktype = blocked_locks.locktype
JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid
WHERE NOT blocked_locks.granted AND blocking_locks.granted;
```

### Network Debug
```bash
# Check container networking
docker network inspect insurance_insurance-network

# Test DNS resolution
docker exec insurance-backend nslookup postgres
docker exec insurance-frontend nslookup backend

# Check port connectivity
docker exec insurance-frontend nc -zv backend 8080
docker exec insurance-backend nc -zv postgres 5432
```

---

**Related Documentation:**
- [User Manual](user-manual.md)
- [Admin Guide](admin-guide.md)
- [Development Setup](../development/setup.md)
- [Docker Setup](../deployment/docker-setup.md)