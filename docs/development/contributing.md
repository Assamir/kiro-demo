# Contributing Guide

Guidelines for contributing to the Insurance Backoffice System.

## 🤝 Getting Started

### Prerequisites
- Read the [Development Setup](setup.md) guide
- Understand the [Code Structure](code-structure.md)
- Familiarize yourself with the [Testing Guide](testing.md)

### Development Environment
1. Fork the repository
2. Clone your fork locally
3. Set up the development environment
4. Create a feature branch

```bash
git clone https://github.com/your-username/insurance-backoffice-system.git
cd insurance-backoffice-system
git checkout -b feature/your-feature-name
```

## 📋 Development Workflow

### 1. Planning
- Check existing issues and discussions
- Create or comment on relevant issues
- Discuss major changes before implementation
- Break down large features into smaller tasks

### 2. Implementation
- Follow coding standards and conventions
- Write tests for new functionality
- Update documentation as needed
- Keep commits focused and atomic

### 3. Testing
- Run all tests locally
- Ensure code coverage meets requirements
- Test edge cases and error scenarios
- Verify functionality in different environments

### 4. Review
- Create a pull request with clear description
- Address review feedback promptly
- Update tests and documentation if needed
- Ensure CI/CD pipeline passes

## 🔧 Coding Standards

### Backend (Java/Spring Boot)

#### Code Style
```java
// Use meaningful names
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    
    // Constructor injection preferred
    public PolicyService(PolicyRepository policyRepository, PolicyMapper policyMapper) {
        this.policyRepository = policyRepository;
        this.policyMapper = policyMapper;
    }
    
    // Clear method names
    public PolicyDto createPolicy(CreatePolicyDto createDto) {
        validatePolicyData(createDto);
        
        Policy policy = policyMapper.toEntity(createDto);
        policy.generatePolicyNumber();
        policy.calculatePremium();
        
        Policy savedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(savedPolicy);
    }
    
    private void validatePolicyData(CreatePolicyDto createDto) {
        if (createDto.getStartDate().isAfter(createDto.getEndDate())) {
            throw new InvalidPolicyDateException("Start date must be before end date");
        }
    }
}
```

#### Best Practices
- Use dependency injection
- Implement proper exception handling
- Write comprehensive JavaDoc for public APIs
- Follow SOLID principles
- Use DTOs for API boundaries
- Implement proper validation

#### Exception Handling
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(PolicyNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Policy Not Found")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### Frontend (React/TypeScript)

#### Component Structure
```typescript
// Use functional components with hooks
interface PolicyListProps {
  policies: Policy[];
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
}

export const PolicyList: React.FC<PolicyListProps> = ({
  policies,
  loading,
  error,
  onRefresh
}) => {
  // Early returns for loading and error states
  if (loading) {
    return <LoadingSpinner data-testid="loading-spinner" />;
  }
  
  if (error) {
    return <ErrorMessage message={error} onRetry={onRefresh} />;
  }
  
  if (policies.length === 0) {
    return <EmptyState message="No policies found" />;
  }
  
  return (
    <div className="policy-list">
      {policies.map(policy => (
        <PolicyCard key={policy.id} policy={policy} />
      ))}
    </div>
  );
};
```

#### Custom Hooks
```typescript
// Extract logic into custom hooks
export const usePolicies = (filters: PolicyFilters) => {
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const fetchPolicies = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await policyService.getPolicies(filters);
      setPolicies(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  }, [filters]);
  
  useEffect(() => {
    fetchPolicies();
  }, [fetchPolicies]);
  
  return { policies, loading, error, refetch: fetchPolicies };
};
```

#### Type Definitions
```typescript
// Use strict TypeScript
export interface Policy {
  readonly id: number;
  readonly policyNumber: string;
  readonly clientId: number;
  readonly clientName: string;
  readonly vehicleId: number;
  readonly vehicleRegistration: string;
  readonly insuranceType: InsuranceType;
  readonly startDate: string;
  readonly endDate: string;
  readonly premium: number;
  readonly discountSurcharge: number;
  readonly status: PolicyStatus;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface CreatePolicyDto {
  readonly clientId: number;
  readonly vehicleId: number;
  readonly insuranceType: InsuranceType;
  readonly startDate: string;
  readonly endDate: string;
  readonly discountSurcharge?: number;
}
```

## 🧪 Testing Requirements

### Test Coverage
- **Unit Tests**: Minimum 80% coverage
- **Integration Tests**: Cover all API endpoints
- **E2E Tests**: Cover critical user workflows

### Test Quality
```typescript
// Good test example
describe('PolicyService', () => {
  describe('createPolicy', () => {
    it('should create policy with valid data', async () => {
      // Arrange
      const createDto = {
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'OC' as InsuranceType,
        startDate: '2024-01-01',
        endDate: '2024-12-31'
      };
      
      // Act
      const result = await policyService.createPolicy(createDto);
      
      // Assert
      expect(result).toBeDefined();
      expect(result.policyNumber).toMatch(/^OC-\d{4}-\d{6}$/);
      expect(result.premium).toBeGreaterThan(0);
    });
    
    it('should throw error when client not found', async () => {
      // Arrange
      const createDto = {
        clientId: 999,
        vehicleId: 1,
        insuranceType: 'OC' as InsuranceType,
        startDate: '2024-01-01',
        endDate: '2024-12-31'
      };
      
      // Act & Assert
      await expect(policyService.createPolicy(createDto))
        .rejects
        .toThrow('Client not found with id: 999');
    });
  });
});
```

## 📝 Documentation Standards

### Code Documentation
```java
/**
 * Service for managing insurance policies.
 * 
 * This service handles the creation, modification, and cancellation of insurance policies.
 * It integrates with the rating engine to calculate premiums and validates business rules.
 * 
 * @author Your Name
 * @since 1.0.0
 */
@Service
@Transactional
public class PolicyService {
    
    /**
     * Creates a new insurance policy.
     * 
     * @param createDto the policy creation data
     * @return the created policy DTO
     * @throws ClientNotFoundException if the client is not found
     * @throws VehicleNotFoundException if the vehicle is not found
     * @throws InvalidPolicyDateException if the policy dates are invalid
     */
    public PolicyDto createPolicy(CreatePolicyDto createDto) {
        // Implementation
    }
}
```

### API Documentation
```java
@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Policy management operations")
public class PolicyController {
    
    @PostMapping
    @Operation(summary = "Create a new policy", description = "Creates a new insurance policy for a client and vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Policy created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Client or vehicle not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PolicyDto> createPolicy(
        @Valid @RequestBody CreatePolicyDto createDto) {
        // Implementation
    }
}
```

## 🔄 Git Workflow

### Branch Naming
- `feature/policy-management` - New features
- `bugfix/login-error` - Bug fixes
- `hotfix/security-patch` - Critical fixes
- `refactor/service-layer` - Code refactoring
- `docs/api-documentation` - Documentation updates

### Commit Messages
Follow conventional commit format:

```
type(scope): description

[optional body]

[optional footer]
```

Examples:
```
feat(policy): add policy cancellation functionality

- Add cancel policy endpoint
- Implement business rules for cancellation
- Add validation for cancellation conditions

Closes #123
```

```
fix(auth): resolve JWT token expiration issue

The JWT tokens were expiring too quickly due to incorrect
configuration. Updated the expiration time to 1 hour.

Fixes #456
```

```
docs(api): update policy endpoints documentation

- Add examples for all policy endpoints
- Update response schemas
- Add error code descriptions
```

### Commit Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

## 🔍 Code Review Process

### Pull Request Guidelines

#### PR Title and Description
```markdown
# Add policy cancellation feature

## Description
This PR implements the policy cancellation functionality allowing users to cancel active policies.

## Changes
- Added `cancelPolicy` endpoint in PolicyController
- Implemented business logic in PolicyService
- Added validation for cancellation rules
- Updated frontend to show cancel button
- Added comprehensive tests

## Testing
- [x] Unit tests added/updated
- [x] Integration tests added/updated
- [x] E2E tests added/updated
- [x] Manual testing completed

## Screenshots
[Include screenshots for UI changes]

## Breaking Changes
None

## Related Issues
Closes #123
```

#### Review Checklist
- [ ] Code follows project conventions
- [ ] Tests are comprehensive and pass
- [ ] Documentation is updated
- [ ] No security vulnerabilities
- [ ] Performance impact considered
- [ ] Backward compatibility maintained

### Review Guidelines

#### For Authors
- Keep PRs focused and reasonably sized
- Provide clear description and context
- Respond to feedback promptly
- Update tests and documentation
- Ensure CI/CD pipeline passes

#### For Reviewers
- Review code thoroughly but constructively
- Focus on logic, security, and maintainability
- Suggest improvements, don't just point out problems
- Approve when ready, request changes when needed
- Be respectful and helpful

## 🚀 Release Process

### Version Numbering
Follow semantic versioning (SemVer):
- `MAJOR.MINOR.PATCH`
- `1.0.0` - Initial release
- `1.1.0` - New features (backward compatible)
- `1.0.1` - Bug fixes (backward compatible)
- `2.0.0` - Breaking changes

### Release Checklist
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Version numbers updated
- [ ] Changelog updated
- [ ] Security review completed
- [ ] Performance testing completed
- [ ] Deployment tested in staging

## 🆘 Getting Help

### Resources
- [Development Setup](setup.md)
- [Code Structure](code-structure.md)
- [Testing Guide](testing.md)
- [API Documentation](../api/endpoints.md)

### Communication
- **Issues**: Use GitHub issues for bugs and feature requests
- **Discussions**: Use GitHub discussions for questions and ideas
- **Email**: dev-team@insurance-system.com for urgent matters

### Common Questions

#### Q: How do I add a new API endpoint?
1. Create the controller method
2. Add service layer logic
3. Update DTOs if needed
4. Add comprehensive tests
5. Update API documentation

#### Q: How do I add a new database table?
1. Create Flyway migration script
2. Add JPA entity
3. Create repository interface
4. Update service layer
5. Add tests

#### Q: How do I add a new React component?
1. Create component with TypeScript
2. Add proper props interface
3. Implement error handling
4. Add comprehensive tests
5. Update storybook if applicable

---

**Thank you for contributing to the Insurance Backoffice System!**

Your contributions help make this project better for everyone. If you have questions or need help, don't hesitate to reach out to the development team.