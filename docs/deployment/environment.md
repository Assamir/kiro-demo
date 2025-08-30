# Environment Configuration

Complete guide for configuring the Insurance Backoffice System across different environments.

## 🌍 Environment Types

### Development
- Local development with hot reload
- Debug logging enabled
- Sample data included
- CORS enabled for localhost

### Staging
- Production-like environment for testing
- Reduced logging
- Test data included
- Limited CORS origins

### Production
- Optimized for performance and security
- Error logging only
- Real data
- Strict CORS policy

## ⚙️ Configuration Files

### Backend Configuration

#### application.yml (Default)
```yaml
spring:
  application:
    name: insurance-backoffice
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  
server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    root: INFO
    com.insurance.backoffice: ${LOG_LEVEL:DEBUG}
```

#### application-dev.yml (Development)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:insurance_db}
    username: ${DB_USERNAME:insurance_user}
    password: ${DB_PASSWORD:insurance_password}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

jwt:
  secret: ${JWT_SECRET:dev-secret-key-change-in-production}
  expiration: ${JWT_EXPIRATION:86400000}

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000}
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true

logging:
  level:
    com.insurance.backoffice: DEBUG
    org.springframework.security: DEBUG
```

#### application-prod.yml (Production)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    validate-on-migrate: true

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:3600000}

cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS}
  allowed-methods: GET,POST,PUT,DELETE
  allowed-headers: Authorization,Content-Type
  allow-credentials: true

logging:
  level:
    com.insurance.backoffice: INFO
    org.springframework.security: WARN
```

### Frontend Configuration

#### .env.development
```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
REACT_APP_VERSION=1.0.0
REACT_APP_LOG_LEVEL=debug
REACT_APP_ENABLE_MOCK_DATA=false
```

#### .env.production
```env
REACT_APP_API_BASE_URL=${REACT_APP_API_BASE_URL}
REACT_APP_ENVIRONMENT=production
REACT_APP_VERSION=1.0.0
REACT_APP_LOG_LEVEL=error
REACT_APP_ENABLE_MOCK_DATA=false
```

#### .env.staging
```env
REACT_APP_API_BASE_URL=${REACT_APP_API_BASE_URL}
REACT_APP_ENVIRONMENT=staging
REACT_APP_VERSION=1.0.0
REACT_APP_LOG_LEVEL=warn
REACT_APP_ENABLE_MOCK_DATA=false
```

## 🔐 Environment Variables

### Required Variables

#### Backend
| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` | No |
| `DB_HOST` | Database host | `localhost` | Yes (prod) |
| `DB_PORT` | Database port | `5432` | No |
| `DB_NAME` | Database name | `insurance_db` | Yes (prod) |
| `DB_USERNAME` | Database username | `insurance_user` | Yes (prod) |
| `DB_PASSWORD` | Database password | `insurance_password` | Yes (prod) |
| `JWT_SECRET` | JWT signing secret | - | Yes (prod) |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` | No |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000` | Yes (prod) |
| `SERVER_PORT` | Server port | `8080` | No |
| `LOG_LEVEL` | Application log level | `DEBUG` | No |

#### Frontend
| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `REACT_APP_API_BASE_URL` | Backend API URL | `http://localhost:8080` | Yes |
| `REACT_APP_ENVIRONMENT` | Environment name | `development` | No |
| `REACT_APP_VERSION` | Application version | `1.0.0` | No |
| `REACT_APP_LOG_LEVEL` | Frontend log level | `debug` | No |

#### Database
| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `POSTGRES_DB` | Database name | `insurance_db` | Yes |
| `POSTGRES_USER` | Database user | `insurance_user` | Yes |
| `POSTGRES_PASSWORD` | Database password | `insurance_password` | Yes |

### Optional Variables

#### Performance Tuning
| Variable | Description | Default |
|----------|-------------|---------|
| `JAVA_OPTS` | JVM options | `-Xmx1g -Xms512m` |
| `HIKARI_MAX_POOL_SIZE` | DB connection pool size | `10` |
| `HIKARI_MIN_IDLE` | DB minimum idle connections | `5` |

#### Security
| Variable | Description | Default |
|----------|-------------|---------|
| `BCRYPT_ROUNDS` | Password hashing rounds | `12` |
| `SESSION_TIMEOUT` | Session timeout (minutes) | `30` |
| `MAX_LOGIN_ATTEMPTS` | Max failed login attempts | `5` |

## 🐳 Docker Environment Configuration

### Development (docker-compose.yml)
```yaml
version: '3.8'
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=postgres
      - DB_NAME=insurance_db
      - DB_USERNAME=insurance_user
      - DB_PASSWORD=insurance_password
      - JWT_SECRET=dev-secret-key
      - CORS_ALLOWED_ORIGINS=http://localhost:3000
  
  frontend:
    environment:
      - REACT_APP_API_BASE_URL=http://localhost:8080
      - REACT_APP_ENVIRONMENT=development
  
  postgres:
    environment:
      - POSTGRES_DB=insurance_db
      - POSTGRES_USER=insurance_user
      - POSTGRES_PASSWORD=insurance_password
```

### Production (docker-compose.prod.yml)
```yaml
version: '3.8'
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
      - DB_NAME=${DB_NAME}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}
      - JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC
  
  frontend:
    environment:
      - REACT_APP_API_BASE_URL=${REACT_APP_API_BASE_URL}
      - REACT_APP_ENVIRONMENT=production
  
  postgres:
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USERNAME}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
```

## 🔧 Configuration Management

### Environment Files

#### .env (Development)
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=insurance_db
DB_USERNAME=insurance_user
DB_PASSWORD=insurance_password

# JWT
JWT_SECRET=dev-secret-key-change-in-production
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:3000

# Frontend
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
```

#### .env.prod (Production)
```env
# Database
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=insurance_db_prod
DB_USERNAME=insurance_user_prod
DB_PASSWORD=your-secure-password

# JWT
JWT_SECRET=your-very-secure-jwt-secret-key
JWT_EXPIRATION=3600000

# CORS
CORS_ALLOWED_ORIGINS=https://your-domain.com

# Frontend
REACT_APP_API_BASE_URL=https://api.your-domain.com
REACT_APP_ENVIRONMENT=production
```

### Secrets Management

#### Using Docker Secrets
```yaml
secrets:
  db_password:
    external: true
  jwt_secret:
    external: true

services:
  backend:
    secrets:
      - db_password
      - jwt_secret
    environment:
      - DB_PASSWORD_FILE=/run/secrets/db_password
      - JWT_SECRET_FILE=/run/secrets/jwt_secret
```

#### Using External Secret Management
```yaml
services:
  backend:
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    # Secrets injected by external system (Kubernetes, AWS Secrets Manager, etc.)
```

## 🔍 Configuration Validation

### Health Check Endpoints
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check configuration info
curl http://localhost:8080/actuator/info

# Check environment (dev only)
curl http://localhost:8080/actuator/env
```

### Configuration Testing
```bash
# Test database connection
docker exec backend java -jar app.jar --spring.profiles.active=test --spring.datasource.url=jdbc:postgresql://postgres:5432/insurance_db

# Test JWT configuration
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@insurance.com","password":"admin123"}'

# Test CORS configuration
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     http://localhost:8080/api/policies
```

## 🚨 Common Configuration Issues

### Database Connection
```bash
# Check database connectivity
docker exec postgres pg_isready -U insurance_user -d insurance_db

# Test connection string
psql "postgresql://insurance_user:insurance_password@localhost:5432/insurance_db"
```

### JWT Configuration
```bash
# Verify JWT secret is set
echo $JWT_SECRET

# Test token generation
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@insurance.com","password":"admin123"}'
```

### CORS Issues
```javascript
// Check browser console for CORS errors
// Verify allowed origins match frontend URL
fetch('http://localhost:8080/api/policies', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
```

## 📚 Best Practices

### Security
- Never commit secrets to version control
- Use strong, unique passwords for each environment
- Rotate JWT secrets regularly
- Use HTTPS in production
- Limit CORS origins to necessary domains

### Performance
- Tune database connection pool sizes
- Set appropriate JVM memory limits
- Use production-optimized Docker images
- Enable compression for static assets

### Monitoring
- Enable health checks for all services
- Set up log aggregation
- Monitor resource usage
- Set up alerts for critical metrics

---

**Related Documentation:**
- [Docker Setup](docker-setup.md)
- [Production Setup](production-setup.md)
- [Troubleshooting Guide](../user-guide/troubleshooting.md)