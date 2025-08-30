# Development Setup Guide

Complete guide for setting up the Insurance Backoffice System for development.

## 🛠️ Prerequisites

### Required Software
- **Java 17+** (OpenJDK or Oracle JDK)
- **Node.js 18+** and npm
- **Docker** and Docker Compose
- **Git**
- **IDE** (IntelliJ IDEA, VS Code, or similar)

### Optional Tools
- **Postman** or **Insomnia** for API testing
- **pgAdmin** for database management
- **Maven** or **Gradle** (included in project)

## 🚀 Quick Development Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd insurance-backoffice-system
```

### 2. Start Database
```bash
# Start only PostgreSQL
docker-compose up -d postgres

# Wait for database to be ready
docker-compose logs -f postgres
```

### 3. Backend Setup
```bash
cd backend

# Install dependencies and build
./gradlew build

# Run application
./gradlew bootRun
```

The backend will start at: http://localhost:8080

### 4. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will start at: http://localhost:3000

## 🏗️ Project Structure

```
insurance-backoffice-system/
├── backend/                    # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/insurance/backoffice/
│   │   │   │       ├── application/        # Application layer
│   │   │   │       ├── domain/             # Domain models
│   │   │   │       ├── infrastructure/     # Infrastructure layer
│   │   │   │       └── interfaces/         # Controllers
│   │   │   └── resources/
│   │   │       ├── application.yml         # Configuration
│   │   │       └── db/migration/           # Database migrations
│   │   └── test/                           # Tests
│   ├── build.gradle                        # Build configuration
│   └── Dockerfile                          # Docker configuration
├── frontend/                   # React application
│   ├── src/
│   │   ├── components/                     # React components
│   │   ├── contexts/                       # React contexts
│   │   ├── hooks/                          # Custom hooks
│   │   ├── pages/                          # Page components
│   │   ├── services/                       # API services
│   │   ├── types/                          # TypeScript types
│   │   └── utils/                          # Utility functions
│   ├── public/                             # Static assets
│   ├── package.json                        # Dependencies
│   └── Dockerfile                          # Docker configuration
├── docs/                       # Documentation
├── docker-compose.yml          # Docker orchestration
└── README.md                   # Project overview
```

## 🔧 Backend Development

### Technology Stack
- **Framework**: Spring Boot 3.2
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito

### Configuration Files

#### application.yml
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/insurance_db
    username: insurance_user
    password: insurance_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret: your-secret-key
  expiration: 86400000

logging:
  level:
    com.insurance.backoffice: DEBUG
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PolicyServiceTest

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Database Migrations
```bash
# Create new migration
# Create file: src/main/resources/db/migration/V{version}__{description}.sql

# Apply migrations
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo
```

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🎨 Frontend Development

### Technology Stack
- **Framework**: React 18
- **Language**: TypeScript
- **UI Library**: Material-UI (MUI)
- **State Management**: React Context + Hooks
- **HTTP Client**: Axios
- **Build Tool**: Create React App
- **Testing**: Jest, React Testing Library

### Environment Configuration

#### .env.development
```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
REACT_APP_VERSION=1.0.0
```

### Running Tests
```bash
# Run all tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage
```

### Building for Production
```bash
# Create production build
npm run build

# Serve production build locally
npx serve -s build
```

## 🗄️ Database Development

### Local Database Setup
```bash
# Start PostgreSQL container
docker-compose up -d postgres

# Connect to database
docker exec -it insurance-postgres psql -U insurance_user -d insurance_db

# Or use connection string
postgresql://insurance_user:insurance_password@localhost:5432/insurance_db
```

### Sample Queries
```sql
-- Check data
SELECT COUNT(*) FROM policies;
SELECT COUNT(*) FROM clients;
SELECT COUNT(*) FROM vehicles;

-- View recent policies
SELECT p.policy_number, c.full_name, v.registration_number, p.created_at
FROM policies p
JOIN clients c ON p.client_id = c.id
JOIN vehicles v ON p.vehicle_id = v.id
ORDER BY p.created_at DESC
LIMIT 10;
```

## 🧪 Testing Strategy

### Backend Testing

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {
    @Mock
    private PolicyRepository policyRepository;
    
    @InjectMocks
    private PolicyService policyService;
    
    @Test
    void shouldCreatePolicy() {
        // Test implementation
    }
}
```

#### Integration Tests
```java
@SpringBootTest
@Testcontainers
class PolicyControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Test
    void shouldCreatePolicyEndToEnd() {
        // Test implementation
    }
}
```

### Frontend Testing

#### Component Tests
```typescript
import { render, screen } from '@testing-library/react';
import PolicyList from './PolicyList';

test('renders policy list', () => {
  render(<PolicyList policies={mockPolicies} />);
  expect(screen.getByText('Policy Number')).toBeInTheDocument();
});
```

#### Integration Tests
```typescript
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PoliciesPage from './PoliciesPage';

test('creates new policy', async () => {
  render(<PoliciesPage />);
  await userEvent.click(screen.getByText('New Policy'));
  // Test implementation
});
```

## 🔍 Debugging

### Backend Debugging

#### IntelliJ IDEA
1. Set breakpoints in code
2. Run application in debug mode
3. Use "Debug" configuration

#### VS Code
1. Install Java Extension Pack
2. Set breakpoints
3. Use F5 to start debugging

#### Logs
```bash
# View application logs
tail -f logs/application.log

# View specific logger
grep "PolicyService" logs/application.log
```

### Frontend Debugging

#### Browser DevTools
1. Open Chrome DevTools (F12)
2. Use Sources tab for breakpoints
3. Use Console for logging
4. Use Network tab for API calls

#### React DevTools
1. Install React DevTools extension
2. Inspect component state and props
3. Profile component performance

## 📦 Build and Deployment

### Development Build
```bash
# Backend
./gradlew bootJar

# Frontend
npm run build
```

### Docker Development
```bash
# Build images
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

### Hot Reload Setup

#### Backend (Spring Boot DevTools)
```gradle
dependencies {
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
}
```

#### Frontend (Create React App)
Hot reload is enabled by default with `npm start`.

## 🔧 IDE Configuration

### IntelliJ IDEA
1. Import project as Gradle project
2. Set Project SDK to Java 17
3. Enable annotation processing
4. Install plugins:
   - Spring Boot
   - Lombok
   - Database Navigator

### VS Code
1. Install extensions:
   - Java Extension Pack
   - Spring Boot Extension Pack
   - ES7+ React/Redux/React-Native snippets
   - TypeScript Importer
2. Configure settings.json:
```json
{
  "java.home": "/path/to/java17",
  "typescript.preferences.importModuleSpecifier": "relative"
}
```

## 🚨 Common Development Issues

### Backend Issues

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

#### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Reset database
docker-compose down -v
docker-compose up -d postgres
```

### Frontend Issues

#### Node Modules Issues
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### CORS Issues
Ensure backend CORS configuration allows frontend origin:
```java
@CrossOrigin(origins = "http://localhost:3000")
```

## 📚 Additional Resources

### Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/docs)
- [Material-UI Documentation](https://mui.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Tools
- [Postman Collections](../api/postman-collection.json)
- [Database Schema](../database/schema.md)
- [API Documentation](../api/endpoints.md)

---

**Next Steps:**
- [Code Structure Guide](code-structure.md)
- [Testing Guide](testing.md)
- [Contributing Guidelines](contributing.md)