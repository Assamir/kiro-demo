# Insurance Backoffice System

A comprehensive web-based application for managing car insurance policies for passenger vehicles.

## Project Structure

This is a Gradle multi-module project with the following structure:

```
insurance-backoffice-system/
├── backend/                 # Spring Boot backend application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration and resources
│   ├── src/test/           # Test code
│   ├── build.gradle        # Backend dependencies
│   └── Dockerfile          # Backend container configuration
├── frontend/               # React frontend application (placeholder)
│   ├── build.gradle        # Frontend build configuration
│   └── Dockerfile          # Frontend container configuration
├── docker-compose.yml      # Docker orchestration
├── docker-compose.override.yml # Development overrides
├── build.gradle           # Root project configuration
└── settings.gradle        # Multi-module configuration
```

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security (JWT authentication)
- Spring Data JPA
- PostgreSQL
- Flyway (database migrations)
- SpringDoc OpenAPI (Swagger)
- iText (PDF generation)
- JUnit 5 & Mockito (testing)

### Frontend
- React 18+ with TypeScript (to be implemented)
- Material-UI (MUI)
- React Router
- Axios

### Infrastructure
- Docker & Docker Compose
- PostgreSQL 15
- Gradle 8.x

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 17+ (for local development)
- Gradle 8.x (or use the wrapper)

### Running with Docker Compose

1. Clone the repository
2. Start the application:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Frontend: http://localhost:3000 (placeholder)
   - Database: localhost:5432

### Local Development

1. Start the database:
   ```bash
   docker-compose up -d database
   ```

2. Run the backend:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

3. Run tests:
   ```bash
   ./gradlew test
   ```

## Configuration

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Set to `dev` for development
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

### Profiles
- `dev`: Development environment with debug logging
- `test`: Test environment with in-memory H2 database
- `prod`: Production environment (to be configured)

## API Documentation

Once the backend is running, visit http://localhost:8080/swagger-ui.html for interactive API documentation.

## Development Guidelines

This project follows Clean Code principles and Clean Architecture patterns:
- Domain-driven design
- SOLID principles
- Comprehensive unit testing
- Meaningful naming conventions
- Small, focused methods and classes

## Next Steps

1. Implement domain models and database schema
2. Create repository layer with Spring Data JPA
3. Implement service layer with business logic
4. Set up Spring Security configuration
5. Create REST API controllers
6. Implement React frontend components