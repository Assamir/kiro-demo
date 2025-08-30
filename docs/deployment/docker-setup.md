# Docker Deployment Guide

Complete guide for deploying the Insurance Backoffice System using Docker.

## 🐳 Docker Architecture

The system consists of three main containers:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (React)       │    │  (Spring Boot)  │    │  (PostgreSQL)   │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 Docker Configuration Files

### docker-compose.yml
Main orchestration file defining all services and their relationships.

### Dockerfile (Backend)
Multi-stage build:
1. **Build Stage**: Uses Gradle to compile Java application
2. **Runtime Stage**: Uses OpenJDK 17 JRE for minimal footprint

### Dockerfile (Frontend)
Static file serving using lightweight HTTP server.

## 🚀 Deployment Commands

### Development Deployment
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Deployment
```bash
# Build with no cache
docker-compose build --no-cache

# Start in production mode
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Scale services (if needed)
docker-compose up -d --scale backend=2
```

## 🔧 Configuration

### Environment Variables

#### Backend Configuration
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - DATABASE_URL=jdbc:postgresql://database:5432/insurance_db
  - DATABASE_USERNAME=insurance_user
  - DATABASE_PASSWORD=insurance_password
  - JWT_SECRET=your-secret-key
  - CORS_ALLOWED_ORIGINS=http://localhost:3000
```

#### Database Configuration
```yaml
environment:
  - POSTGRES_DB=insurance_db
  - POSTGRES_USER=insurance_user
  - POSTGRES_PASSWORD=insurance_password
```

### Volume Mounts

#### Database Persistence
```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

#### Application Logs
```yaml
volumes:
  - ./logs:/app/logs
```

## 🔍 Health Checks

### Backend Health Check
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

### Database Health Check
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U insurance_user -d insurance_db"]
  interval: 10s
  timeout: 5s
  retries: 5
```

## 📊 Monitoring

### Container Status
```bash
# Check all containers
docker-compose ps

# Check specific container
docker-compose ps backend

# View resource usage
docker stats
```

### Logs Management
```bash
# View all logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f backend

# View last 100 lines
docker-compose logs --tail=100 backend

# Filter by timestamp
docker-compose logs --since="2024-01-01T00:00:00" backend
```

## 🔄 Updates and Maintenance

### Application Updates
```bash
# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build

# Zero-downtime update (with load balancer)
docker-compose up -d --scale backend=2
docker-compose stop backend_1
docker-compose up -d --build backend_1
docker-compose stop backend_2
docker-compose up -d --build backend_2
```

### Database Maintenance
```bash
# Backup database
docker exec insurance-postgres pg_dump -U insurance_user insurance_db > backup.sql

# Restore database
docker exec -i insurance-postgres psql -U insurance_user insurance_db < backup.sql

# Access database shell
docker exec -it insurance-postgres psql -U insurance_user -d insurance_db
```

## 🛡️ Security Considerations

### Network Security
```yaml
networks:
  insurance-network:
    driver: bridge
    internal: true  # Isolate from external networks
```

### Secrets Management
```yaml
secrets:
  db_password:
    file: ./secrets/db_password.txt
  jwt_secret:
    file: ./secrets/jwt_secret.txt
```

### User Permissions
```dockerfile
# Run as non-root user
RUN groupadd -r insurance && useradd -r -g insurance insurance
USER insurance
```

## 🚨 Troubleshooting

### Common Issues

#### Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep :8080

# Kill process using port
sudo kill -9 $(lsof -t -i:8080)
```

#### Memory Issues
```bash
# Check Docker memory usage
docker system df

# Clean up unused resources
docker system prune -a

# Increase Docker memory limit
# Docker Desktop: Settings > Resources > Memory
```

#### Database Connection Issues
```bash
# Check database logs
docker-compose logs postgres

# Test database connection
docker exec insurance-postgres pg_isready -U insurance_user

# Reset database
docker-compose down -v
docker volume rm insurance_postgres_data
docker-compose up -d
```

### Performance Tuning

#### JVM Settings
```yaml
environment:
  - JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC
```

#### Database Settings
```yaml
command: >
  postgres
  -c max_connections=200
  -c shared_buffers=256MB
  -c effective_cache_size=1GB
```

## 📈 Scaling

### Horizontal Scaling
```bash
# Scale backend instances
docker-compose up -d --scale backend=3

# Use load balancer (nginx)
docker-compose -f docker-compose.yml -f docker-compose.scale.yml up -d
```

### Vertical Scaling
```yaml
deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 4G
    reservations:
      cpus: '1.0'
      memory: 2G
```

---

**Next Steps**: 
- [Environment Configuration](environment.md)
- [Users and Passwords](users-and-passwords.md)
- [Troubleshooting Guide](../user-guide/troubleshooting.md)