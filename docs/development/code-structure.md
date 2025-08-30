# Code Structure Guide

Complete guide to the codebase organization and conventions for the Insurance Backoffice System.

## 🏗️ Overall Architecture

The system follows a clean architecture pattern with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (React TS)    │◄──►│  (Spring Boot)  │◄──►│  (PostgreSQL)   │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔧 Backend Structure

### Package Organization

```
com.insurance.backoffice/
├── application/           # Application services and use cases
│   ├── service/          # Business logic services
│   ├── dto/              # Data Transfer Objects
│   └── mapper/           # Entity-DTO mappers
├── domain/               # Domain models and business rules
│   ├── model/            # Entity classes
│   ├── repository/       # Repository interfaces
│   └── exception/        # Domain exceptions
├── infrastructure/       # External concerns
│   ├── persistence/      # JPA repositories
│   ├── config/           # Configuration classes
│   └── security/         # Security configuration
└── interfaces/           # External interfaces
    ├── controller/       # REST controllers
    ├── dto/              # API DTOs
    └── exception/        # Exception handlers
```

### Layer Responsibilities

#### Domain Layer
- **Entities**: Core business objects
- **Repository Interfaces**: Data access contracts
- **Domain Services**: Business logic that doesn't fit in entities
- **Value Objects**: Immutable objects representing concepts

```java
// Example: Policy entity
@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_number", unique = true, nullable = false)
    private String policyNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // Business methods
    public void cancel() {
        if (this.status == PolicyStatus.CANCELED) {
            throw new PolicyAlreadyCanceledException();
        }
        this.status = PolicyStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }
}
```

#### Application Layer
- **Services**: Orchestrate business operations
- **DTOs**: Data transfer between layers
- **Mappers**: Convert between entities and DTOs

```java
// Example: Policy service
@Service
@Transactional
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    
    public PolicyDto createPolicy(CreatePolicyDto createDto) {
        Policy policy = policyMapper.toEntity(createDto);
        policy.generatePolicyNumber();
        policy.calculatePremium();
        
        Policy savedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(savedPolicy);
    }
}
```

#### Infrastructure Layer
- **JPA Repositories**: Data persistence implementation
- **Configuration**: Spring configuration classes
- **Security**: Authentication and authorization

```java
// Example: JPA repository
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByClientId(Long clientId);
    List<Policy> findByStatus(PolicyStatus status);
    
    @Query("SELECT p FROM Policy p WHERE p.endDate BETWEEN :startDate AND :endDate")
    List<Policy> findExpiringPolicies(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
}
```

#### Interface Layer
- **Controllers**: HTTP request handling
- **DTOs**: API request/response objects
- **Exception Handlers**: Global error handling

```java
// Example: REST controller
@RestController
@RequestMapping("/api/policies")
@PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
public class PolicyController {
    private final PolicyService policyService;
    
    @GetMapping
    public ResponseEntity<List<PolicyDto>> getAllPolicies() {
        List<PolicyDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }
    
    @PostMapping
    public ResponseEntity<PolicyDto> createPolicy(@Valid @RequestBody CreatePolicyDto createDto) {
        PolicyDto policy = policyService.createPolicy(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(policy);
    }
}
```

### Configuration Structure

#### Application Configuration
```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  application:
    name: insurance-backoffice
  
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/insurance_db}
    username: ${DATABASE_USERNAME:insurance_user}
    password: ${DATABASE_PASSWORD:insurance_password}
```

#### Security Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

## 🎨 Frontend Structure

### Component Organization

```
src/
├── components/           # Reusable UI components
│   ├── common/          # Generic components
│   ├── forms/           # Form components
│   ├── layout/          # Layout components
│   └── ui/              # Basic UI elements
├── pages/               # Page-level components
│   ├── auth/            # Authentication pages
│   ├── policies/        # Policy management pages
│   ├── users/           # User management pages
│   └── dashboard/       # Dashboard page
├── contexts/            # React contexts
│   ├── AuthContext.tsx  # Authentication context
│   └── ThemeContext.tsx # Theme context
├── hooks/               # Custom React hooks
│   ├── useAuth.ts       # Authentication hook
│   ├── useApi.ts        # API calling hook
│   └── usePagination.ts # Pagination hook
├── services/            # API services
│   ├── api.ts           # Base API configuration
│   ├── authService.ts   # Authentication service
│   ├── policyService.ts # Policy service
│   └── userService.ts   # User service
├── types/               # TypeScript type definitions
│   ├── api.ts           # API response types
│   ├── auth.ts          # Authentication types
│   └── policy.ts        # Policy types
├── utils/               # Utility functions
│   ├── constants.ts     # Application constants
│   ├── formatters.ts    # Data formatters
│   └── validators.ts    # Validation functions
└── styles/              # Global styles
    ├── globals.css      # Global CSS
    └── theme.ts         # MUI theme configuration
```

### Component Patterns

#### Page Components
```typescript
// Example: PoliciesPage.tsx
import React, { useState, useEffect } from 'react';
import { PolicyList } from '../components/policies/PolicyList';
import { PolicyFilters } from '../components/policies/PolicyFilters';
import { usePolicies } from '../hooks/usePolicies';

export const PoliciesPage: React.FC = () => {
  const [filters, setFilters] = useState<PolicyFilters>({});
  const { policies, loading, error, refetch } = usePolicies(filters);

  return (
    <div>
      <PolicyFilters filters={filters} onFiltersChange={setFilters} />
      <PolicyList 
        policies={policies} 
        loading={loading} 
        error={error}
        onRefresh={refetch}
      />
    </div>
  );
};
```

#### Custom Hooks
```typescript
// Example: usePolicies.ts
import { useState, useEffect } from 'react';
import { policyService } from '../services/policyService';
import { Policy, PolicyFilters } from '../types/policy';

export const usePolicies = (filters: PolicyFilters) => {
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPolicies = async () => {
    try {
      setLoading(true);
      const data = await policyService.getPolicies(filters);
      setPolicies(data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPolicies();
  }, [filters]);

  return { policies, loading, error, refetch: fetchPolicies };
};
```

#### Service Layer
```typescript
// Example: policyService.ts
import { api } from './api';
import { Policy, CreatePolicyDto, PolicyFilters } from '../types/policy';

export const policyService = {
  async getPolicies(filters?: PolicyFilters): Promise<Policy[]> {
    const response = await api.get('/policies', { params: filters });
    return response.data;
  },

  async getPolicyById(id: number): Promise<Policy> {
    const response = await api.get(`/policies/${id}`);
    return response.data;
  },

  async createPolicy(policy: CreatePolicyDto): Promise<Policy> {
    const response = await api.post('/policies', policy);
    return response.data;
  },

  async updatePolicy(id: number, policy: Partial<Policy>): Promise<Policy> {
    const response = await api.put(`/policies/${id}`, policy);
    return response.data;
  },

  async cancelPolicy(id: number): Promise<void> {
    await api.post(`/policies/${id}/cancel`);
  }
};
```

### Type Definitions

```typescript
// types/policy.ts
export interface Policy {
  id: number;
  policyNumber: string;
  clientId: number;
  clientName: string;
  vehicleId: number;
  vehicleRegistration: string;
  insuranceType: InsuranceType;
  startDate: string;
  endDate: string;
  premium: number;
  discountSurcharge: number;
  status: PolicyStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePolicyDto {
  clientId: number;
  vehicleId: number;
  insuranceType: InsuranceType;
  startDate: string;
  endDate: string;
  discountSurcharge?: number;
}

export enum InsuranceType {
  OC = 'OC',
  AC = 'AC',
  NNW = 'NNW'
}

export enum PolicyStatus {
  ACTIVE = 'ACTIVE',
  CANCELED = 'CANCELED',
  EXPIRED = 'EXPIRED'
}
```

## 🗄️ Database Structure

### Migration Organization

```
db/migration/
├── V1__Create_initial_schema.sql
├── V2__Add_users_table.sql
├── V3__Add_clients_table.sql
├── V4__Add_vehicles_table.sql
├── V5__Add_policies_table.sql
├── V6__Add_rating_tables.sql
├── V7__Add_indexes.sql
├── V8__Add_constraints.sql
├── V9__Insert_rating_data.sql
├── V10__Insert_sample_users.sql
├── V11__Insert_sample_clients.sql
├── V12__Insert_sample_vehicles.sql
├── V13__Insert_sample_policies.sql
└── V18__Add_sample_data_fixed.sql
```

### Table Relationships

```sql
-- Core entities with relationships
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    pesel VARCHAR(11) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    vin VARCHAR(17) NOT NULL UNIQUE,
    year_of_manufacture INTEGER NOT NULL,
    engine_capacity INTEGER NOT NULL,
    power INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    insurance_type VARCHAR(10) NOT NULL CHECK (insurance_type IN ('OC', 'AC', 'NNW')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    premium DECIMAL(10,2) NOT NULL,
    discount_surcharge DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELED', 'EXPIRED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📁 File Naming Conventions

### Backend (Java)
- **Classes**: PascalCase (e.g., `PolicyService`, `ClientRepository`)
- **Methods**: camelCase (e.g., `createPolicy`, `findByClientId`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_POLICY_DURATION`)
- **Packages**: lowercase (e.g., `com.insurance.backoffice.domain`)

### Frontend (TypeScript/React)
- **Components**: PascalCase (e.g., `PolicyList.tsx`, `UserForm.tsx`)
- **Hooks**: camelCase starting with 'use' (e.g., `useAuth.ts`, `usePolicies.ts`)
- **Services**: camelCase (e.g., `policyService.ts`, `authService.ts`)
- **Types**: PascalCase (e.g., `Policy`, `CreatePolicyDto`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`)

### Database
- **Tables**: snake_case (e.g., `policies`, `rating_tables`)
- **Columns**: snake_case (e.g., `policy_number`, `created_at`)
- **Indexes**: `idx_table_column` (e.g., `idx_policies_client_id`)
- **Constraints**: `chk_table_condition` (e.g., `chk_policies_premium_positive`)

## 🔄 Data Flow

### Request Flow
```
Frontend → API Controller → Service → Repository → Database
    ↓           ↓            ↓         ↓          ↓
   User    HTTP Request   Business   Data      Storage
  Action      (JSON)      Logic     Access
```

### Response Flow
```
Database → Repository → Service → Controller → Frontend
    ↓         ↓          ↓         ↓           ↓
  Storage   Entity     DTO      JSON       User
            Data    Mapping   Response   Interface
```

## 🧪 Testing Structure

### Backend Tests
```
src/test/java/
├── unit/                 # Unit tests
│   ├── service/         # Service layer tests
│   ├── repository/      # Repository tests
│   └── mapper/          # Mapper tests
├── integration/         # Integration tests
│   ├── controller/      # Controller tests
│   └── repository/      # Database integration tests
└── e2e/                 # End-to-end tests
```

### Frontend Tests
```
src/
├── components/
│   └── __tests__/       # Component tests
├── hooks/
│   └── __tests__/       # Hook tests
├── services/
│   └── __tests__/       # Service tests
└── utils/
    └── __tests__/       # Utility tests
```

## 📚 Code Standards

### Backend Standards
- Use Spring Boot conventions
- Follow SOLID principles
- Implement proper exception handling
- Use DTOs for API boundaries
- Write comprehensive tests

### Frontend Standards
- Use functional components with hooks
- Implement proper error boundaries
- Use TypeScript strictly
- Follow React best practices
- Implement proper loading states

### General Standards
- Write self-documenting code
- Use meaningful variable names
- Keep functions small and focused
- Follow DRY principle
- Implement proper logging

---

**Related Documentation:**
- [Development Setup](setup.md)
- [Testing Guide](testing.md)
- [API Documentation](../api/endpoints.md)
- [Database Schema](../database/schema.md)