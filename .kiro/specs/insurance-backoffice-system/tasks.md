# Implementation Plan

- [x] 1. Set up project structure and build configuration
  - Create Gradle multi-module project structure with backend and frontend modules
  - Configure Gradle build files with all required dependencies (Spring Boot, PostgreSQL, SpringDoc OpenAPI, etc.)
  - Set up Docker Compose configuration for development environment
  - Create basic application.properties with database and security configurations
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 2. Implement core domain models and database schema
  - Create JPA entity classes for User, Client, Vehicle, Policy, PolicyDetails, and RatingTable
  - Implement Builder pattern for entity creation following clean code principles
  - Create database migration scripts using Flyway for schema creation
  - Add proper indexes and constraints for data integrity
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [x] 3. Implement repository layer with Spring Data JPA
  - Create repository interfaces extending JpaRepository for all entities
  - Implement custom query methods for policy search by client and complex filtering
  - Add repository unit tests using @DataJpaTest with TestContainers
  - Implement proper exception handling for data access operations
  - _Requirements: 2.1, 3.1, 3.2, 4.5_

- [x] 4. Create service layer with business logic
  - Implement UserService with user management operations and role-based access control
  - Create PolicyService with policy creation, update, and cancellation logic
  - Implement RatingService for premium calculations using rating tables
  - Add comprehensive unit tests for all service classes using Mockito
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 5. Implement Spring Security configuration
  - Configure JWT-based authentication with custom UserDetailsService
  - Set up role-based authorization with @PreAuthorize annotations
  - Create authentication endpoints for login and token validation
  - Implement security unit tests to verify access control rules
  - _Requirements: 1.5, 3.5_

- [x] 6. Create REST API controllers with Swagger documentation
  - Implement UserController with full CRUD operations for admin users
  - Create PolicyController with policy management endpoints for operators
  - Add AuthController for authentication operations
  - Implement comprehensive Swagger/OpenAPI documentation with examples
  - Add controller unit tests using @WebMvcTest with MockMvc
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 7. Implement PDF generation service
  - Create PdfService using iText library for policy document generation
  - Design professional PDF template with company branding
  - Implement PDF generation endpoint in PolicyController
  - Add unit tests for PDF generation functionality
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [x] 8. Create rating system for premium calculations
  - Implement rating table data structure and seed data
  - Create premium calculation algorithms for OC, AC, and NNW insurance types
  - Add validation for rating factors and business rules
  - Implement unit tests for all premium calculation scenarios
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 9. Set up React frontend project structure
  - Initialize React TypeScript project with Material-UI
  - Configure custom theme with light colors, blue and gold accents
  - Set up React Router for navigation and protected routes
  - Create basic project structure with components, services, and utilities folders
  - _Requirements: 7.1, 7.3_

- [x] 10. Implement authentication and routing
  - Create login form component with form validation
  - Implement JWT token management and HTTP interceptors
  - Set up protected routes based on user roles (Admin/Operator)
  - Create authentication context and hooks for state management
  - _Requirements: 1.5, 3.5_

- [x] 11. Create main layout and navigation components

  - Implement responsive app header with user information and role display
  - Create sidebar navigation with role-based menu items
  - Design dashboard landing page with system overview
  - Add logout functionality and session management
  - _Requirements: 1.5, 3.5_

- [x] 12. Implement user management interface (Admin only)

  - Create user list component with search and filtering capabilities
  - Implement user creation form with validation
  - Add user edit and delete functionality
  - Create unit tests for all user management components
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [x] 13. Create policy management interface
  - Implement policy list component with search by client and filtering options
  - Create policy creation form with dynamic fields based on insurance type
  - Add policy edit functionality with proper validation
  - Implement policy cancellation with confirmation dialog
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 3.5_

- [ ] 14. Implement insurance type specific forms
  - Create OC insurance form with guaranteed sum and coverage area fields
  - Implement AC insurance form with variant selection and comprehensive coverage options
  - Add NNW insurance form with sum insured and covered persons fields
  - Include premium calculation display with rating factor breakdown
  - _Requirements: 2.2, 2.3, 2.4, 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 15. Add PDF generation and download functionality
  - Implement PDF generation button in policy details view
  - Create PDF preview modal with download option
  - Add error handling for PDF generation failures
  - Implement unit tests for PDF-related components
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 16. Create comprehensive error handling and validation
  - Implement global error boundary for React application
  - Add form validation with real-time feedback
  - Create user-friendly error messages and notifications
  - Implement retry mechanisms for failed operations
  - _Requirements: 4.4, 5.5_

- [ ] 17. Add integration tests and end-to-end testing
  - Create integration tests for API endpoints using TestContainers
  - Implement frontend integration tests with React Testing Library
  - Add end-to-end tests for critical user workflows
  - Set up test data fixtures and cleanup procedures
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

- [ ] 18. Implement data seeding and sample data
  - Create database seed scripts with sample users, clients, and vehicles
  - Add rating table data for all insurance types
  - Implement sample policy data for testing and demonstration
  - Create data migration scripts for production deployment
  - _Requirements: 5.1, 5.2, 5.3_

- [ ] 19. Configure production deployment setup
  - Optimize Docker images for production use
  - Configure environment-specific application properties
  - Set up database connection pooling and performance tuning
  - Implement health checks and monitoring endpoints
  - _Requirements: 7.1, 7.2, 7.4, 7.5_

- [ ] 20. Final integration and system testing
  - Perform end-to-end testing of all user workflows
  - Verify role-based access control across the entire system
  - Test PDF generation with various policy types and data combinations
  - Validate premium calculations against business requirements
  - Conduct performance testing and optimization
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.3, 8.4, 8.5, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_