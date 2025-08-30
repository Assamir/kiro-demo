# Testing Guide

Comprehensive testing guide for the Insurance Backoffice System.

## 🧪 Testing Strategy

### Testing Pyramid

```
                    ┌─────────────┐
                    │     E2E     │  ← Few, slow, expensive
                    │   Tests     │
                ┌───┴─────────────┴───┐
                │   Integration       │  ← Some, medium speed
                │     Tests           │
            ┌───┴─────────────────────┴───┐
            │        Unit Tests           │  ← Many, fast, cheap
            └─────────────────────────────┘
```

### Test Types

#### Unit Tests (70%)
- Test individual components in isolation
- Fast execution
- Mock external dependencies
- High code coverage

#### Integration Tests (20%)
- Test component interactions
- Database integration
- API endpoint testing
- Service layer integration

#### End-to-End Tests (10%)
- Full user workflow testing
- Browser automation
- Complete system validation
- User acceptance scenarios

## 🔧 Backend Testing

### Technology Stack
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Testcontainers**: Integration testing with real databases
- **Spring Boot Test**: Spring-specific testing utilities
- **AssertJ**: Fluent assertions

### Unit Testing

#### Service Layer Tests
```java
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {
    
    @Mock
    private PolicyRepository policyRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private PolicyMapper policyMapper;
    
    @InjectMocks
    private PolicyService policyService;
    
    @Test
    @DisplayName("Should create policy successfully")
    void shouldCreatePolicySuccessfully() {
        // Given
        CreatePolicyDto createDto = CreatePolicyDto.builder()
            .clientId(1L)
            .vehicleId(1L)
            .insuranceType(InsuranceType.OC)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusYears(1))
            .build();
            
        Client client = new Client();
        client.setId(1L);
        
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        
        Policy policy = new Policy();
        policy.setId(1L);
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(policyMapper.toEntity(createDto)).thenReturn(policy);
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
        when(policyMapper.toDto(policy)).thenReturn(new PolicyDto());
        
        // When
        PolicyDto result = policyService.createPolicy(createDto);
        
        // Then
        assertThat(result).isNotNull();
        verify(policyRepository).save(any(Policy.class));
        verify(clientRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when client not found")
    void shouldThrowExceptionWhenClientNotFound() {
        // Given
        CreatePolicyDto createDto = CreatePolicyDto.builder()
            .clientId(999L)
            .vehicleId(1L)
            .insuranceType(InsuranceType.OC)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusYears(1))
            .build();
            
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> policyService.createPolicy(createDto))
            .isInstanceOf(ClientNotFoundException.class)
            .hasMessage("Client not found with id: 999");
    }
}
```

#### Repository Tests
```java
@DataJpaTest
@Testcontainers
class PolicyRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    @DisplayName("Should find policies by client id")
    void shouldFindPoliciesByClientId() {
        // Given
        Client client = new Client();
        client.setFullName("John Doe");
        client.setPesel("12345678901");
        client.setAddress("Test Address");
        entityManager.persistAndFlush(client);
        
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setRegistrationNumber("ABC123");
        vehicle.setVin("12345678901234567");
        vehicle.setYearOfManufacture(2020);
        vehicle.setEngineCapacity(1600);
        vehicle.setPower(120);
        entityManager.persistAndFlush(vehicle);
        
        Policy policy = new Policy();
        policy.setPolicyNumber("OC-2024-001");
        policy.setClient(client);
        policy.setVehicle(vehicle);
        policy.setInsuranceType(InsuranceType.OC);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(1));
        policy.setPremium(BigDecimal.valueOf(1000));
        policy.setStatus(PolicyStatus.ACTIVE);
        entityManager.persistAndFlush(policy);
        
        // When
        List<Policy> policies = policyRepository.findByClientId(client.getId());
        
        // Then
        assertThat(policies).hasSize(1);
        assertThat(policies.get(0).getPolicyNumber()).isEqualTo("OC-2024-001");
    }
}
```

### Integration Testing

#### Controller Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PolicyControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    private String authToken;
    
    @BeforeEach
    void setUp() {
        // Login and get auth token
        LoginRequest loginRequest = new LoginRequest("admin@insurance.com", "admin123");
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
            "/api/auth/login", loginRequest, LoginResponse.class);
        authToken = "Bearer " + response.getBody().getToken();
    }
    
    @Test
    @DisplayName("Should create policy via API")
    void shouldCreatePolicyViaApi() {
        // Given
        Client client = createTestClient();
        Vehicle vehicle = createTestVehicle();
        
        CreatePolicyDto createDto = CreatePolicyDto.builder()
            .clientId(client.getId())
            .vehicleId(vehicle.getId())
            .insuranceType(InsuranceType.OC)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusYears(1))
            .build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        HttpEntity<CreatePolicyDto> request = new HttpEntity<>(createDto, headers);
        
        // When
        ResponseEntity<PolicyDto> response = restTemplate.postForEntity(
            "/api/policies", request, PolicyDto.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getClientName()).isEqualTo(client.getFullName());
        
        // Verify in database
        List<Policy> policies = policyRepository.findAll();
        assertThat(policies).hasSize(1);
    }
    
    private Client createTestClient() {
        Client client = new Client();
        client.setFullName("Test Client");
        client.setPesel("12345678901");
        client.setAddress("Test Address");
        client.setEmail("test@example.com");
        return clientRepository.save(client);
    }
    
    private Vehicle createTestVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setRegistrationNumber("TEST123");
        vehicle.setVin("12345678901234567");
        vehicle.setYearOfManufacture(2020);
        vehicle.setEngineCapacity(1600);
        vehicle.setPower(120);
        return vehicleRepository.save(vehicle);
    }
}
```

### Test Configuration

#### Test Properties
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  
  flyway:
    enabled: false

jwt:
  secret: test-secret-key
  expiration: 3600000

logging:
  level:
    com.insurance.backoffice: DEBUG
```

#### Test Profiles
```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
    }
    
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Faster for tests
    }
}
```

## 🎨 Frontend Testing

### Technology Stack
- **Jest**: Testing framework
- **React Testing Library**: Component testing
- **MSW (Mock Service Worker)**: API mocking
- **User Event**: User interaction simulation

### Component Testing

#### Basic Component Test
```typescript
// PolicyList.test.tsx
import { render, screen } from '@testing-library/react';
import { PolicyList } from './PolicyList';
import { Policy } from '../../types/policy';

const mockPolicies: Policy[] = [
  {
    id: 1,
    policyNumber: 'OC-2024-001',
    clientName: 'John Doe',
    vehicleRegistration: 'ABC123',
    insuranceType: 'OC',
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    premium: 1000,
    discountSurcharge: 0,
    status: 'ACTIVE',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z'
  }
];

describe('PolicyList', () => {
  test('renders policy list correctly', () => {
    render(<PolicyList policies={mockPolicies} loading={false} error={null} />);
    
    expect(screen.getByText('OC-2024-001')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('ABC123')).toBeInTheDocument();
    expect(screen.getByText('OC')).toBeInTheDocument();
  });
  
  test('shows loading state', () => {
    render(<PolicyList policies={[]} loading={true} error={null} />);
    
    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });
  
  test('shows error state', () => {
    const errorMessage = 'Failed to load policies';
    render(<PolicyList policies={[]} loading={false} error={errorMessage} />);
    
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });
});
```

#### Form Testing
```typescript
// PolicyForm.test.tsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { PolicyForm } from './PolicyForm';

describe('PolicyForm', () => {
  test('submits form with correct data', async () => {
    const user = userEvent.setup();
    const mockOnSubmit = jest.fn();
    
    render(<PolicyForm onSubmit={mockOnSubmit} />);
    
    // Fill form
    await user.selectOptions(screen.getByLabelText('Client'), '1');
    await user.selectOptions(screen.getByLabelText('Vehicle'), '1');
    await user.selectOptions(screen.getByLabelText('Insurance Type'), 'OC');
    await user.type(screen.getByLabelText('Start Date'), '2024-01-01');
    await user.type(screen.getByLabelText('End Date'), '2024-12-31');
    
    // Submit form
    await user.click(screen.getByRole('button', { name: 'Create Policy' }));
    
    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith({
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31'
      });
    });
  });
  
  test('shows validation errors', async () => {
    const user = userEvent.setup();
    
    render(<PolicyForm onSubmit={jest.fn()} />);
    
    // Submit without filling required fields
    await user.click(screen.getByRole('button', { name: 'Create Policy' }));
    
    expect(screen.getByText('Client is required')).toBeInTheDocument();
    expect(screen.getByText('Vehicle is required')).toBeInTheDocument();
  });
});
```

### Hook Testing

```typescript
// usePolicies.test.ts
import { renderHook, waitFor } from '@testing-library/react';
import { usePolicies } from './usePolicies';
import { policyService } from '../services/policyService';

jest.mock('../services/policyService');
const mockPolicyService = policyService as jest.Mocked<typeof policyService>;

describe('usePolicies', () => {
  test('fetches policies successfully', async () => {
    const mockPolicies = [{ id: 1, policyNumber: 'OC-2024-001' }];
    mockPolicyService.getPolicies.mockResolvedValue(mockPolicies);
    
    const { result } = renderHook(() => usePolicies({}));
    
    expect(result.current.loading).toBe(true);
    
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
    
    expect(result.current.policies).toEqual(mockPolicies);
    expect(result.current.error).toBeNull();
  });
  
  test('handles error correctly', async () => {
    const errorMessage = 'Failed to fetch policies';
    mockPolicyService.getPolicies.mockRejectedValue(new Error(errorMessage));
    
    const { result } = renderHook(() => usePolicies({}));
    
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });
    
    expect(result.current.error).toBe(errorMessage);
    expect(result.current.policies).toEqual([]);
  });
});
```

### API Mocking with MSW

```typescript
// mocks/handlers.ts
import { rest } from 'msw';

export const handlers = [
  rest.get('/api/policies', (req, res, ctx) => {
    return res(
      ctx.json([
        {
          id: 1,
          policyNumber: 'OC-2024-001',
          clientName: 'John Doe',
          vehicleRegistration: 'ABC123',
          insuranceType: 'OC',
          status: 'ACTIVE'
        }
      ])
    );
  }),
  
  rest.post('/api/policies', (req, res, ctx) => {
    return res(
      ctx.status(201),
      ctx.json({
        id: 2,
        policyNumber: 'OC-2024-002',
        ...req.body
      })
    );
  }),
  
  rest.post('/api/auth/login', (req, res, ctx) => {
    return res(
      ctx.json({
        token: 'mock-jwt-token',
        user: {
          id: 1,
          email: 'admin@insurance.com',
          fullName: 'Admin User',
          role: 'ADMIN'
        }
      })
    );
  })
];
```

```typescript
// setupTests.ts
import { setupServer } from 'msw/node';
import { handlers } from './mocks/handlers';

const server = setupServer(...handlers);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
```

## 🔄 End-to-End Testing

### Cypress Configuration

```typescript
// cypress.config.ts
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:3000',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    video: true,
    screenshotOnRunFailure: true,
    viewportWidth: 1280,
    viewportHeight: 720
  }
});
```

### E2E Test Examples

```typescript
// cypress/e2e/policy-management.cy.ts
describe('Policy Management', () => {
  beforeEach(() => {
    // Login before each test
    cy.login('admin@insurance.com', 'admin123');
  });
  
  it('should create a new policy', () => {
    cy.visit('/policies');
    
    // Click new policy button
    cy.get('[data-testid="new-policy-button"]').click();
    
    // Fill form
    cy.get('[data-testid="client-select"]').select('John Doe');
    cy.get('[data-testid="vehicle-select"]').select('Toyota Corolla - ABC123');
    cy.get('[data-testid="insurance-type-select"]').select('OC');
    cy.get('[data-testid="start-date"]').type('2024-01-01');
    cy.get('[data-testid="end-date"]').type('2024-12-31');
    
    // Submit form
    cy.get('[data-testid="submit-button"]').click();
    
    // Verify success
    cy.get('[data-testid="success-message"]').should('contain', 'Policy created successfully');
    cy.url().should('include', '/policies');
    
    // Verify policy appears in list
    cy.get('[data-testid="policy-list"]').should('contain', 'OC-2024-');
  });
  
  it('should cancel a policy', () => {
    cy.visit('/policies');
    
    // Find and click cancel button for first policy
    cy.get('[data-testid="policy-row"]').first().within(() => {
      cy.get('[data-testid="cancel-button"]').click();
    });
    
    // Confirm cancellation
    cy.get('[data-testid="confirm-dialog"]').within(() => {
      cy.get('[data-testid="confirm-button"]').click();
    });
    
    // Verify policy is canceled
    cy.get('[data-testid="policy-row"]').first().should('contain', 'CANCELED');
  });
});
```

### Custom Cypress Commands

```typescript
// cypress/support/commands.ts
declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      createPolicy(policyData: any): Chainable<void>;
    }
  }
}

Cypress.Commands.add('login', (email: string, password: string) => {
  cy.request({
    method: 'POST',
    url: '/api/auth/login',
    body: { email, password }
  }).then((response) => {
    window.localStorage.setItem('authToken', response.body.token);
    window.localStorage.setItem('user', JSON.stringify(response.body.user));
  });
});

Cypress.Commands.add('createPolicy', (policyData) => {
  const token = window.localStorage.getItem('authToken');
  cy.request({
    method: 'POST',
    url: '/api/policies',
    headers: {
      Authorization: `Bearer ${token}`
    },
    body: policyData
  });
});
```

## 📊 Test Coverage

### Coverage Configuration

#### Backend (JaCoCo)
```gradle
// build.gradle
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.8"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
    
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/config/**',
                '**/dto/**',
                '**/exception/**',
                '**/*Application.class'
            ])
        }))
    }
}

test {
    finalizedBy jacocoTestReport
}
```

#### Frontend (Jest)
```json
// package.json
{
  "scripts": {
    "test:coverage": "npm test -- --coverage --watchAll=false"
  },
  "jest": {
    "collectCoverageFrom": [
      "src/**/*.{js,jsx,ts,tsx}",
      "!src/**/*.d.ts",
      "!src/index.tsx",
      "!src/reportWebVitals.ts"
    ],
    "coverageThreshold": {
      "global": {
        "branches": 80,
        "functions": 80,
        "lines": 80,
        "statements": 80
      }
    }
  }
}
```

### Coverage Targets
- **Unit Tests**: 90%+ coverage
- **Integration Tests**: 80%+ coverage
- **E2E Tests**: Cover critical user paths

## 🚀 Running Tests

### Backend Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PolicyServiceTest

# Run tests with coverage
./gradlew test jacocoTestReport

# Run integration tests only
./gradlew test --tests "*IntegrationTest"
```

### Frontend Tests
```bash
# Run all tests
npm test

# Run tests in CI mode
npm test -- --ci --coverage --watchAll=false

# Run specific test file
npm test -- PolicyList.test.tsx

# Run tests with coverage
npm run test:coverage
```

### E2E Tests
```bash
# Run Cypress tests headlessly
npx cypress run

# Open Cypress GUI
npx cypress open

# Run specific test file
npx cypress run --spec "cypress/e2e/policy-management.cy.ts"
```

## 📋 Testing Checklist

### Before Committing
- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Code coverage meets threshold
- [ ] No test warnings or errors
- [ ] Tests are meaningful and not just for coverage

### Before Deploying
- [ ] All test suites pass
- [ ] E2E tests pass
- [ ] Performance tests pass
- [ ] Security tests pass
- [ ] Database migration tests pass

---

**Related Documentation:**
- [Development Setup](setup.md)
- [Code Structure](code-structure.md)
- [Integration Testing](integration-testing.md)
- [API Documentation](../api/endpoints.md)