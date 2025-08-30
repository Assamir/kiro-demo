# Requirements Document

## Introduction

The Insurance Backoffice System is a comprehensive web-based application for managing car insurance policies for passenger vehicles. The system provides internal functionalities for policy management, client management, and user administration. It supports three types of insurance (OC, AC, NNW) with different variants and includes role-based access control for Admin and Operator users. The system excludes payment processing, focusing solely on policy lifecycle management and administrative functions.

## Requirements

### Requirement 1

**User Story:** As an Admin, I want to manage system users, so that I can control who has access to the backoffice system and maintain proper user administration.

#### Acceptance Criteria

1. WHEN an Admin accesses the user management panel THEN the system SHALL display a form with fields for First name, Last name, Email, and Password
2. WHEN an Admin creates a new user THEN the system SHALL validate all required fields and store the user with appropriate role assignment
3. WHEN an Admin edits an existing user THEN the system SHALL allow modification of user details while maintaining data integrity
4. WHEN an Admin deletes a user THEN the system SHALL remove the user from the system and prevent future access
5. IF an Admin attempts to issue policies THEN the system SHALL deny access with appropriate error messaging

### Requirement 2

**User Story:** As an Operator, I want to issue new insurance policies, so that I can provide coverage for clients' vehicles according to their needs.

#### Acceptance Criteria

1. WHEN an Operator selects to issue a new policy THEN the system SHALL present a form appropriate to the selected insurance type (OC, AC, or NNW)
2. WHEN issuing an OC policy THEN the system SHALL collect guaranteed sum, coverage area, premium, and discount/surcharge information
3. WHEN issuing an AC policy THEN the system SHALL collect variant (Standard/Maximum), sum insured, coverage scope, deductible, workshop type, premium, and discount/surcharge information
4. WHEN issuing an NNW policy THEN the system SHALL collect sum insured, covered persons, and premium information
5. WHEN all policy data is entered THEN the system SHALL validate the information against business rules and rating tables
6. WHEN a policy is confirmed THEN the system SHALL generate a unique policy number and set the status to active

### Requirement 3

**User Story:** As an Operator, I want to manage existing policies, so that I can update policy information and handle policy lifecycle events.

#### Acceptance Criteria

1. WHEN an Operator searches for policies by client THEN the system SHALL display a list of all policies associated with that client
2. WHEN an Operator selects to edit a policy THEN the system SHALL display the current policy data in an editable form
3. WHEN policy changes are saved THEN the system SHALL validate the modifications and update the policy record
4. WHEN an Operator cancels a policy THEN the system SHALL update the policy status to canceled and record the cancellation date
5. IF an Operator attempts to access user management functions THEN the system SHALL deny access with appropriate error messaging

### Requirement 4

**User Story:** As a system user, I want the system to capture comprehensive client and vehicle data, so that policies are properly documented and legally compliant.

#### Acceptance Criteria

1. WHEN entering client data THEN the system SHALL require full name, PESEL, address, email, and phone number
2. WHEN entering vehicle data THEN the system SHALL require make, model, year of manufacture, registration number, VIN, engine capacity, power, and first registration date
3. WHEN policy data is entered THEN the system SHALL store policy number, issue date, coverage period, status, and insurance type
4. WHEN data validation occurs THEN the system SHALL ensure all required fields are completed and formatted correctly
5. WHEN policy information is saved THEN the system SHALL maintain referential integrity between client, vehicle, and policy data

### Requirement 5

**User Story:** As an Operator, I want the system to use rating tables for premium calculations, so that pricing is consistent and based on established business rules.

#### Acceptance Criteria

1. WHEN calculating premiums THEN the system SHALL reference rating tables specific to each insurance type (OC, AC, NNW)
2. WHEN applying rating factors THEN the system SHALL use multipliers based on rating keys such as driver age and vehicle age
3. WHEN rating tables are updated THEN the system SHALL apply validity periods to ensure correct rates are used for policy effective dates
4. WHEN premium calculations are performed THEN the system SHALL show the breakdown of base premium and applied multipliers
5. IF rating data is missing for a specific scenario THEN the system SHALL alert the operator and prevent policy issuance

### Requirement 6

**User Story:** As an Operator, I want to generate PDF documents for confirmed policies, so that clients receive proper documentation of their insurance coverage.

#### Acceptance Criteria

1. WHEN a policy is confirmed THEN the system SHALL provide an option to generate a PDF document
2. WHEN generating a PDF THEN the system SHALL include all policy data: client details, vehicle information, coverage details, sum insured, and premium
3. WHEN the PDF is created THEN the system SHALL format the document in a professional, readable layout
4. WHEN the PDF generation is complete THEN the system SHALL allow the operator to download or view the document
5. IF PDF generation fails THEN the system SHALL display an error message and allow retry

### Requirement 7

**User Story:** As a system administrator, I want the system to be containerized and web-based, so that it can be easily deployed and accessed from any location.

#### Acceptance Criteria

1. WHEN the system is deployed THEN it SHALL run as separate Docker containers for frontend, backend, and database components
2. WHEN using Docker Compose THEN the system SHALL start all components with proper networking and dependencies
3. WHEN accessing the system THEN users SHALL be able to use any modern web browser without additional software installation
4. WHEN the frontend communicates with the backend THEN it SHALL use REST API endpoints for all data operations
5. WHEN data is stored THEN it SHALL be persisted in a PostgreSQL database with proper schema design

### Requirement 8

**User Story:** As a business stakeholder, I want the system to support specific insurance types and variants, so that we can offer appropriate coverage options to our clients.

#### Acceptance Criteria

1. WHEN offering OC insurance THEN the system SHALL support only the Optimum variant with standard coverage as per law
2. WHEN offering AC insurance THEN the system SHALL support both Standard and Maximum variants with different Sum Insured values
3. WHEN offering NNW insurance THEN the system SHALL support only the Optimum variant covering death, permanent damage, and medical costs
4. WHEN selecting insurance variants THEN the system SHALL present only valid options for each insurance type
5. WHEN policy data is stored THEN the system SHALL maintain the relationship between insurance type and selected variant

### Requirement 9

**User Story:** As a developer, I want comprehensive unit tests for all system classes, so that code quality is maintained and regressions are prevented during development and maintenance.

#### Acceptance Criteria

1. WHEN developing backend classes THEN each class SHALL have corresponding unit tests covering all public methods
2. WHEN testing business logic THEN unit tests SHALL verify correct behavior for both valid and invalid inputs
3. WHEN testing data access layers THEN unit tests SHALL mock external dependencies and verify data operations
4. WHEN testing service classes THEN unit tests SHALL validate business rules and error handling scenarios
5. WHEN running the test suite THEN all unit tests SHALL pass and provide adequate code coverage (minimum 80%)
6. WHEN frontend components are developed THEN they SHALL include unit tests for component behavior and user interactions