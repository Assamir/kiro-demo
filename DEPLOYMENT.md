# Production Deployment Guide

This guide covers the production deployment setup for the Insurance Backoffice System, including Docker optimization, environment configuration, database tuning, and monitoring.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Production Deployment](#production-deployment)
4. [Staging Deployment](#staging-deployment)
5. [Monitoring and Health Checks](#monitoring-and-health-checks)
6. [Performance Tuning](#performance-tuning)
7. [Troubleshooting](#troubleshooting)
8. [Maintenance](#maintenance)

## Prerequisites

### System Requirements

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Memory**: Minimum 4GB RAM (8GB recommended for production)
- **Storage**: Minimum 20GB free space
- **Network**: Ports 80, 8080, 5432 available

### Security Requirements

- Strong passwords for database and JWT secrets
- SSL certificates for HTTPS (recommended for production)
- Firewall configuration for port access
- Regular security updates

## Environment Setup

### 1. Production Environment Configuration

Copy the example environment file and configure it:

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod` with your production values:

```bash
# Database Configuration
POSTGRES_DB=insurance_db
POSTGRES_USER=insurance_user
POSTGRES_PASSWORD=your_very_secure_password_here

# Application Security
JWT_SECRET=your_256_bit_jwt_secret_key_here

# Network Configuration
DB_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=80

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Frontend Configuration
REACT_APP_API_URL=https://api.yourdomain.com/api
```

### 2. Staging Environment Configuration

```bash
cp .env.staging.example .env.staging
```

Configure staging-specific values in `.env.staging`.

## Production Deployment

### Option 1: Using PowerShell Script (Windows)

```powershell
# Deploy to production
.\scripts\deploy-prod.ps1

# Deploy without database backup
.\scripts\deploy-prod.ps1 -SkipBackup
```

### Option 2: Using Bash Script (Linux/macOS)

```bash
# Make script executable
chmod +x scripts/deploy-prod.sh

# Deploy to production
./scripts/deploy-prod.sh
```

### Option 3: Manual Deployment

```bash
# Build and start production services
docker-compose -f docker-compose.prod.yml --env-file .env.prod build --no-cache
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Check service health
docker-compose -f docker-compose.prod.yml --env-file .env.prod ps
```

## Staging Deployment

### Using Scripts

```bash
# Bash (Linux/macOS)
./scripts/deploy-staging.sh

# PowerShell (Windows)
.\scripts\deploy-staging.ps1
```

### Manual Staging Deployment

```bash
docker-compose -f docker-compose.staging.yml --env-file .env.staging up -d
```

## Monitoring and Health Checks

### Health Check Endpoints

The application provides several health check endpoints:

| Endpoint | Purpose | Use Case |
|----------|---------|----------|
| `/health` | Simple health check | Load balancer health checks |
| `/health/live` | Liveness probe | Kubernetes liveness probe |
| `/health/ready` | Readiness probe | Kubernetes readiness probe |
| `/health/startup` | Startup probe | Kubernetes startup probe |
| `/actuator/health` | Detailed health info | Monitoring systems |
| `/actuator/metrics` | Application metrics | Prometheus monitoring |

### Monitoring Script

Use the monitoring script to check system status:

```bash
# Monitor production environment
./scripts/monitor.sh prod

# Monitor staging environment
./scripts/monitor.sh staging
```

### Key Metrics to Monitor

1. **Application Health**
   - HTTP response times
   - Error rates
   - Database connectivity

2. **System Resources**
   - CPU usage
   - Memory consumption
   - Disk space

3. **Database Performance**
   - Connection pool usage
   - Query execution times
   - Active connections

## Performance Tuning

### Database Optimization

The production configuration includes optimized HikariCP settings:

```properties
# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

### JVM Optimization

Production Docker images include JVM tuning:

```dockerfile
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

### Nginx Optimization

Frontend uses optimized Nginx configuration:

- Gzip compression enabled
- Static asset caching (1 year)
- Security headers
- Connection keep-alive

## Troubleshooting

### Common Issues

#### 1. Services Not Starting

**Symptoms**: Containers exit immediately or fail health checks

**Solutions**:
```bash
# Check logs
docker-compose -f docker-compose.prod.yml logs backend

# Check environment variables
docker-compose -f docker-compose.prod.yml config

# Verify database connectivity
docker exec insurance-postgres-prod pg_isready -U insurance_user
```

#### 2. Database Connection Issues

**Symptoms**: Backend fails to connect to database

**Solutions**:
```bash
# Check database container
docker logs insurance-postgres-prod

# Test database connection
docker exec insurance-backend-prod curl -f http://localhost:8080/health/ready

# Check network connectivity
docker network ls
docker network inspect insurance-network-prod
```

#### 3. High Memory Usage

**Symptoms**: Containers consuming excessive memory

**Solutions**:
```bash
# Check memory usage
docker stats

# Adjust JVM heap size
# Edit JAVA_OPTS in docker-compose.prod.yml

# Scale down if necessary
docker-compose -f docker-compose.prod.yml up -d --scale backend=1
```

### Log Analysis

#### Backend Logs
```bash
# View recent logs
docker-compose -f docker-compose.prod.yml logs --tail=100 backend

# Follow logs in real-time
docker-compose -f docker-compose.prod.yml logs -f backend

# Search for errors
docker-compose -f docker-compose.prod.yml logs backend | grep ERROR
```

#### Database Logs
```bash
# View PostgreSQL logs
docker logs insurance-postgres-prod

# Check slow queries (if enabled)
docker exec insurance-postgres-prod psql -U insurance_user -d insurance_db -c "SELECT * FROM pg_stat_activity WHERE state = 'active';"
```

## Maintenance

### Regular Maintenance Tasks

#### 1. Database Backup

```bash
# Create backup
docker exec insurance-postgres-prod pg_dump -U insurance_user insurance_db > backup_$(date +%Y%m%d).sql

# Restore from backup
docker exec -i insurance-postgres-prod psql -U insurance_user insurance_db < backup_20240101.sql
```

#### 2. Log Rotation

Logs are automatically rotated with the following configuration:
- Maximum file size: 10MB
- Maximum files: 3
- Total size cap: 1GB

#### 3. Security Updates

```bash
# Update base images
docker-compose -f docker-compose.prod.yml pull

# Rebuild with latest security patches
docker-compose -f docker-compose.prod.yml build --no-cache --pull

# Deploy updated images
./scripts/deploy-prod.sh
```

#### 4. Performance Monitoring

Regular monitoring should include:

- Weekly performance reports
- Monthly capacity planning reviews
- Quarterly security audits
- Annual disaster recovery testing

### Scaling

#### Horizontal Scaling

```bash
# Scale backend service
docker-compose -f docker-compose.prod.yml up -d --scale backend=3

# Scale with load balancer (requires additional configuration)
# Add nginx load balancer configuration
```

#### Vertical Scaling

Adjust resource limits in `docker-compose.prod.yml`:

```yaml
deploy:
  resources:
    limits:
      memory: 2G
      cpus: '2.0'
    reservations:
      memory: 1G
      cpus: '1.0'
```

## Security Considerations

### Production Security Checklist

- [ ] Strong passwords for all services
- [ ] JWT secrets are cryptographically secure
- [ ] Database access is restricted
- [ ] HTTPS is enabled (SSL certificates)
- [ ] Security headers are configured
- [ ] Regular security updates are applied
- [ ] Monitoring and alerting are configured
- [ ] Backup and recovery procedures are tested

### Network Security

```bash
# Restrict database access
# Only allow backend container to access database port

# Use Docker secrets for sensitive data
docker secret create postgres_password /path/to/password/file
```

## Support and Troubleshooting

For additional support:

1. Check application logs for specific error messages
2. Verify all environment variables are correctly set
3. Ensure all required ports are available
4. Test database connectivity independently
5. Review Docker and system resource usage

### Emergency Procedures

#### Quick Rollback

```bash
# Stop current deployment
docker-compose -f docker-compose.prod.yml down

# Restore from backup
docker exec -i insurance-postgres-prod psql -U insurance_user insurance_db < backup_latest.sql

# Start previous version
docker-compose -f docker-compose.prod.yml up -d
```

#### Service Recovery

```bash
# Restart specific service
docker-compose -f docker-compose.prod.yml restart backend

# Force recreate containers
docker-compose -f docker-compose.prod.yml up -d --force-recreate
```