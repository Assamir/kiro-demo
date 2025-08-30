# Integration Testing Guide

This document provides comprehensive information about the integration tests implemented for the Insurance Backoffice System.

## Overview

The integration testing suite includes:

1. **Backend Integration Tests** - Using TestContainers with PostgreSQL
2. **Frontend Integration Tests** - Using React Testing Library
3. **End-to-End Workflow Tests** - Testing complete user scenarios
4. **Test Data Fixtures** - Consistent test data setup and cleanup

## Backend Integration Tests

### Test Structure

All backend integration tests extend `BaseIntegrationTest` which provides:
- TestContainers PostgreSQL database setup
- Authentication utilities (admin/operator tokens)
- Common test data creation methods
- HTTP client configuration

### Test Categories

#### 1. Authentication Integration Tests (`AuthenticationIntegrationTest`)
- JWT token generation and validation
- Login/logout workflows
- Invalid credential handling
- Token expiration scenarios

#### 2. User Management Integration Tests (`UserManagementIntegrationTest`)
- Complete CRUD operations for users
- Role-based access control validation
- Data validation and error handling
- Duplicate email prevention

#### 3. Policy Management Integration Tests (`PolicyManagementIntegrationTest`)
- Policy creation for all insurance types (OC, AC, NNW)
- Policy lifecycle management (create, update, cancel)
- Search and filtering functionality
- Business rule validation

#### 4. PDF Generation Integration Tests (`PdfGenerationIntegrationTest`)
- PDF generation for different policy types
- File format validation
- Error handling for invalid policies
- Access control verification

#### 5. End-to-End Workflow Tests (`EndToEndWorkflowTest`)
- Complete admin workflow (user management)
- Complete operator workflow (policy management)
- Multi-insurance type scenarios
- Security and role-based access control

### Running Backend Integration Tests

```bash
# Run all integration tests
./gradlew test --tests "com.insurance.backoffice.integration.*"

# Run specific test class
./gradlew test --tests "AuthenticationIntegrationTest"

# Run integration test suite
./gradlew test --tests "IntegrationTestSuite"

# Run with detailed output
./gradlew test --tests "com.insurance.backoffice.integration.*" --info
```

### Test Configuration

Integration tests use:
- **TestContainers**: PostgreSQL 15 container for database
- **Spring Boot Test**: Full application context
- **Test Profiles**: `application-integration.properties`
- **Data Cleanup**: Automatic cleanup between tests

## Frontend Integration Tests

### Test Structure

Frontend integration tests use:
- **React Testing Library**: Component testing utilities
- **User Event**: Realistic user interaction simulation
- **Mock Services**: Controlled API responses
- **Theme Provider**: Material-UI theme context

### Test Categories

#### 1. Authentication Flow Tests (`AuthenticationFlow.test.tsx`)
- Login/logout workflows
- Token management
- Route protection
- Session handling

#### 2. User Management Flow Tests (`UserManagementFlow.test.tsx`)
- Complete user CRUD operations
- Form validation and error handling
- Search and filtering functionality
- Role-based UI elements

#### 3. Policy Management Flow Tests (`PolicyManagementFlow.test.tsx`)
- Policy creation for all insurance types
- Form validation and premium calculation
- Policy search and filtering
- PDF generation workflow

#### 4. End-to-End Workflow Tests (`EndToEndWorkflow.test.tsx`)
- Complete admin workflow
- Complete operator workflow
- Role-based access control
- Error handling and recovery
- Session management

### Running Frontend Integration Tests

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Run all tests
npm test

# Run integration tests only
npm run test:integration

# Run tests with coverage
npm run test:coverage

# Run specific test file
npm test -- AuthenticationFlow.test.tsx

# Run tests in CI mode (no watch)
npm test -- --watchAll=false
```

## Test Data Management

### Test Fixtures

The `TestDataFixtures` class provides:
- Consistent test data creation
- Realistic sample data
- Proper entity relationships
- Cleanup utilities

### Data Cleanup

The `TestCleanupUtility` provides:
- Automatic cleanup between tests
- Referential integrity maintenance
- Sequence reset capabilities
- Complete database reset

### Sample Data

Test fixtures include:
- **Users**: Admin and Operator roles
- **Clients**: Realistic client information
- **Vehicles**: Various vehicle types and ages
- **Policies**: All insurance types with proper details
- **Rating Tables**: Complete rating factor data

## Best Practices

### Test Isolation

1. **Database Cleanup**: Each test starts with a clean database
2. **Mock Reset**: All mocks are reset between tests
3. **Independent Data**: Tests create their own test data
4. **Transaction Rollback**: Database changes are rolled back

### Test Reliability

1. **Deterministic Data**: Fixed test data for consistent results
2. **Proper Waits**: Use `waitFor` for async operations
3. **Error Handling**: Test both success and failure scenarios
4. **Realistic Scenarios**: Test real user workflows

### Performance Optimization

1. **TestContainers Reuse**: Database container reuse between tests
2. **Selective Testing**: Run specific test categories
3. **Parallel Execution**: Tests can run in parallel where safe
4. **Mock Services**: Frontend tests use mocked services

## Continuous Integration

### GitHub Actions Configuration

```yaml
name: Integration Tests

on: [push, pull_request]

jobs:
  backend-integration:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run Backend Integration Tests
        run: ./gradlew test --tests "com.insurance.backoffice.integration.*"

  frontend-integration:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install Dependencies
        run: cd frontend && npm ci
      - name: Run Frontend Integration Tests
        run: cd frontend && npm run test:integration
```

### Local Development

1. **Docker Required**: Ensure Docker is running for TestContainers
2. **Port Availability**: Ensure ports 5432, 8080, 3000 are available
3. **Memory**: Allocate sufficient memory for containers
4. **Network**: Ensure network connectivity for container communication

## Troubleshooting

### Common Issues

#### TestContainers Issues
```bash
# Check Docker status
docker ps

# Clean up containers
docker system prune

# Check logs
docker logs <container-id>
```

#### Frontend Test Issues
```bash
# Clear Jest cache
npm test -- --clearCache

# Update snapshots
npm test -- --updateSnapshot

# Debug mode
npm test -- --verbose
```

#### Database Issues
```bash
# Check PostgreSQL logs
docker logs <postgres-container>

# Verify database connection
docker exec -it <postgres-container> psql -U test_user -d insurance_test
```

### Performance Issues

1. **Slow Tests**: Check container startup time
2. **Memory Usage**: Monitor Docker memory allocation
3. **Network Latency**: Ensure local network performance
4. **Parallel Execution**: Adjust test parallelization

## Coverage Reports

### Backend Coverage
```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Frontend Coverage
```bash
cd frontend
npm run test:coverage
open coverage/lcov-report/index.html
```

### Coverage Targets

- **Unit Tests**: 90%+ coverage
- **Integration Tests**: 80%+ coverage
- **Critical Paths**: 100% coverage
- **Business Logic**: 95%+ coverage

## Maintenance

### Regular Tasks

1. **Update Dependencies**: Keep testing libraries current
2. **Review Test Data**: Ensure test data remains realistic
3. **Performance Monitoring**: Track test execution times
4. **Coverage Analysis**: Monitor coverage trends

### Test Evolution

1. **New Features**: Add integration tests for new functionality
2. **Bug Fixes**: Add regression tests for fixed bugs
3. **Refactoring**: Update tests when code structure changes
4. **Performance**: Add performance regression tests

This comprehensive integration testing suite ensures the reliability, security, and functionality of the Insurance Backoffice System across all user workflows and system components.