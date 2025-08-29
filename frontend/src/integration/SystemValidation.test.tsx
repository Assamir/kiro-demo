import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import App from '../App';
import theme from '../theme/theme';

// Mock all services
const mockAuthService = {
  login: jest.fn(),
  getCurrentUser: jest.fn(),
  logout: jest.fn(),
};

const mockUserService = {
  getAllUsers: jest.fn(),
  createUser: jest.fn(),
  updateUser: jest.fn(),
  deleteUser: jest.fn(),
};

const mockPolicyService = {
  getAllPolicies: jest.fn(),
  createPolicy: jest.fn(),
  updatePolicy: jest.fn(),
  cancelPolicy: jest.fn(),
  generatePdf: jest.fn(),
  getPoliciesByClient: jest.fn(),
};

const mockRatingService = {
  calculatePremium: jest.fn(),
  getRatingTables: jest.fn(),
};

jest.mock('../services/authService', () => ({
  authService: mockAuthService,
}));

jest.mock('../services/userService', () => ({
  userService: mockUserService,
}));

jest.mock('../services/policyService', () => ({
  policyService: mockPolicyService,
}));

jest.mock('../services/ratingService', () => ({
  ratingService: mockRatingService,
}));

const renderApp = () => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <App />
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('System Validation Integration Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    
    // Default mock responses
    mockUserService.getAllUsers.mockResolvedValue([]);
    mockPolicyService.getAllPolicies.mockResolvedValue([]);
    mockPolicyService.getPoliciesByClient.mockResolvedValue([]);
  });

  test('comprehensive role-based access control validation', async () => {
    const user = userEvent.setup();
    
    // Test 1: Admin user access
    const adminUser = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@test.com',
      role: 'ADMIN',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(adminUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/admin user/i)).toBeInTheDocument();
    });

    // Admin should see Users navigation
    expect(screen.getByRole('button', { name: /users/i })).toBeInTheDocument();
    
    // Admin should NOT see Policies navigation (business rule: admins cannot issue policies)
    expect(screen.queryByRole('button', { name: /policies/i })).not.toBeInTheDocument();

    // Test 2: Operator user access
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/operator user/i)).toBeInTheDocument();
    });

    // Operator should see Policies navigation
    expect(screen.getByRole('button', { name: /policies/i })).toBeInTheDocument();
    
    // Operator should NOT see Users navigation (business rule: operators cannot manage users)
    expect(screen.queryByRole('button', { name: /users/i })).not.toBeInTheDocument();

    // Test 3: Unauthenticated access
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
    });

    // Should not see any navigation items
    expect(screen.queryByRole('button', { name: /users/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /policies/i })).not.toBeInTheDocument();
  });

  test('comprehensive policy workflow validation - all insurance types', async () => {
    const user = userEvent.setup();
    
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Navigate to policies
    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    await waitFor(() => {
      expect(screen.getByText(/policy management/i)).toBeInTheDocument();
    });

    // Test OC Insurance Policy Creation
    await testInsurancePolicyCreation(user, 'OC', {
      guaranteedSum: '5000000',
      coverageArea: 'Europe',
    });

    // Test AC Insurance Policy Creation
    await testInsurancePolicyCreation(user, 'AC', {
      sumInsured: '70000',
      coverageScope: 'Comprehensive',
      deductible: '1000',
      workshopType: 'Authorized',
    });

    // Test NNW Insurance Policy Creation
    await testInsurancePolicyCreation(user, 'NNW', {
      sumInsured: '120000',
      coveredPersons: 'Driver and up to 4 passengers',
    });
  });

  const testInsurancePolicyCreation = async (user: any, insuranceType: string, specificFields: any) => {
    // Click create policy button
    const createPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createPolicyButton);

    // Fill common fields
    const clientNameInput = screen.getByLabelText(/client name/i);
    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);
    const startDateInput = screen.getByLabelText(/start date/i);
    const endDateInput = screen.getByLabelText(/end date/i);

    await user.type(clientNameInput, 'John Doe');
    await user.type(vehicleRegInput, 'ABC123');
    await user.click(insuranceTypeSelect);
    
    const insuranceOption = screen.getByRole('option', { name: new RegExp(insuranceType, 'i') });
    await user.click(insuranceOption);

    await user.type(startDateInput, '2024-03-01');
    await user.type(endDateInput, '2025-02-28');

    // Fill insurance-specific fields
    await waitFor(() => {
      // Wait for dynamic fields to appear
      if (insuranceType === 'OC') {
        expect(screen.getByLabelText(/guaranteed sum/i)).toBeInTheDocument();
      } else if (insuranceType === 'AC') {
        expect(screen.getByLabelText(/sum insured/i)).toBeInTheDocument();
      } else if (insuranceType === 'NNW') {
        expect(screen.getByLabelText(/covered persons/i)).toBeInTheDocument();
      }
    });

    // Fill specific fields based on insurance type
    for (const [fieldName, value] of Object.entries(specificFields)) {
      const fieldInput = screen.getByLabelText(new RegExp(fieldName.replace(/([A-Z])/g, ' $1').toLowerCase(), 'i'));
      await user.type(fieldInput, value as string);
    }

    // Calculate premium
    mockRatingService.calculatePremium.mockResolvedValue({
      basePremium: 1000.00,
      totalPremium: 1200.00,
      appliedFactors: [
        { name: 'Base Premium', value: 1000.00 },
        { name: 'Age Factor', multiplier: 1.2 },
      ],
    });

    const calculateButton = screen.getByRole('button', { name: /calculate premium/i });
    await user.click(calculateButton);

    await waitFor(() => {
      expect(screen.getByText(/total premium: 1,200.00/i)).toBeInTheDocument();
      expect(screen.getByText(/base premium/i)).toBeInTheDocument();
      expect(screen.getByText(/age factor/i)).toBeInTheDocument();
    });

    // Submit policy
    const newPolicy = {
      id: Math.floor(Math.random() * 1000),
      policyNumber: `POL-${insuranceType}-001`,
      clientName: 'John Doe',
      vehicleRegistration: 'ABC123',
      insuranceType: insuranceType,
      startDate: '2024-03-01',
      endDate: '2025-02-28',
      premium: 1200.00,
      status: 'ACTIVE',
    };

    mockPolicyService.createPolicy.mockResolvedValue(newPolicy);
    mockPolicyService.getAllPolicies.mockResolvedValue([newPolicy]);

    const submitPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitPolicyButton);

    // Verify policy creation
    await waitFor(() => {
      expect(screen.getByText(/policy created successfully/i)).toBeInTheDocument();
      expect(screen.getByText(newPolicy.policyNumber)).toBeInTheDocument();
    });

    // Test PDF generation for the created policy
    const pdfButton = screen.getByRole('button', { name: /generate pdf/i });
    
    const mockPdfBlob = new Blob([`PDF content for ${insuranceType} policy`], { type: 'application/pdf' });
    mockPolicyService.generatePdf.mockResolvedValue(mockPdfBlob);

    await user.click(pdfButton);

    await waitFor(() => {
      expect(mockPolicyService.generatePdf).toHaveBeenCalledWith(newPolicy.id);
      expect(screen.getByText(/pdf preview/i)).toBeInTheDocument();
    });

    // Close PDF preview
    const closePdfButton = screen.getByRole('button', { name: /close/i });
    await user.click(closePdfButton);
  };

  test('comprehensive form validation and error handling', async () => {
    const user = userEvent.setup();
    
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Navigate to policies
    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    // Test form validation
    const createPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createPolicyButton);

    // Try to submit empty form
    const submitButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitButton);

    // Should show validation errors
    await waitFor(() => {
      expect(screen.getByText(/client name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/vehicle registration is required/i)).toBeInTheDocument();
      expect(screen.getByText(/insurance type is required/i)).toBeInTheDocument();
    });

    // Test invalid date range
    const startDateInput = screen.getByLabelText(/start date/i);
    const endDateInput = screen.getByLabelText(/end date/i);

    await user.type(startDateInput, '2025-12-31');
    await user.type(endDateInput, '2024-01-01');

    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/end date must be after start date/i)).toBeInTheDocument();
    });

    // Test network error handling
    const clientNameInput = screen.getByLabelText(/client name/i);
    await user.type(clientNameInput, 'John Doe');

    // Mock network error
    mockPolicyService.createPolicy.mockRejectedValue(new Error('Network error'));

    // Fill valid form and submit
    await user.clear(startDateInput);
    await user.clear(endDateInput);
    await user.type(startDateInput, '2024-03-01');
    await user.type(endDateInput, '2025-02-28');

    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    await user.type(vehicleRegInput, 'ABC123');

    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);
    await user.click(insuranceTypeSelect);
    const ocOption = screen.getByRole('option', { name: /oc/i });
    await user.click(ocOption);

    await user.click(submitButton);

    // Should show network error
    await waitFor(() => {
      expect(screen.getByText(/network error/i)).toBeInTheDocument();
    });

    // Test retry functionality
    const retryButton = screen.getByRole('button', { name: /retry/i });
    
    mockPolicyService.createPolicy.mockResolvedValue({
      id: 1,
      policyNumber: 'POL-001',
      clientName: 'John Doe',
      vehicleRegistration: 'ABC123',
      insuranceType: 'OC',
      status: 'ACTIVE',
    });

    await user.click(retryButton);

    await waitFor(() => {
      expect(screen.getByText(/policy created successfully/i)).toBeInTheDocument();
    });
  });

  test('performance validation - large data sets', async () => {
    const user = userEvent.setup();
    
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    // Create large dataset
    const largePolicyList = Array.from({ length: 100 }, (_, index) => ({
      id: index + 1,
      policyNumber: `POL-${String(index + 1).padStart(3, '0')}`,
      clientName: `Client ${index + 1}`,
      vehicleRegistration: `REG${String(index + 1).padStart(3, '0')}`,
      insuranceType: ['OC', 'AC', 'NNW'][index % 3],
      startDate: '2024-03-01',
      endDate: '2025-02-28',
      premium: 1000 + (index * 10),
      status: 'ACTIVE',
    }));

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    mockPolicyService.getAllPolicies.mockResolvedValue(largePolicyList);

    const startTime = performance.now();
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Navigate to policies
    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    // Wait for large policy list to load
    await waitFor(() => {
      expect(screen.getByText(/policy management/i)).toBeInTheDocument();
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.getByText('POL-100')).toBeInTheDocument();
    }, { timeout: 10000 });

    const endTime = performance.now();
    const loadTime = endTime - startTime;

    // Performance assertion - should load large dataset within reasonable time
    expect(loadTime).toBeLessThan(5000); // Should load within 5 seconds

    // Test search functionality with large dataset
    const searchInput = screen.getByLabelText(/search policies/i);
    await user.type(searchInput, 'POL-050');

    await waitFor(() => {
      expect(screen.getByText('POL-050')).toBeInTheDocument();
      // Should not show other policies
      expect(screen.queryByText('POL-001')).not.toBeInTheDocument();
    });
  });

  test('accessibility validation', async () => {
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Test keyboard navigation
    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    
    // Should be focusable
    policiesNavItem.focus();
    expect(document.activeElement).toBe(policiesNavItem);

    // Test ARIA labels and roles
    expect(policiesNavItem).toHaveAttribute('role', 'button');
    expect(policiesNavItem).toHaveAccessibleName();

    // Test form accessibility
    const user = userEvent.setup();
    await user.click(policiesNavItem);

    const createPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createPolicyButton);

    // All form inputs should have proper labels
    const clientNameInput = screen.getByLabelText(/client name/i);
    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);

    expect(clientNameInput).toHaveAccessibleName();
    expect(vehicleRegInput).toHaveAccessibleName();
    expect(insuranceTypeSelect).toHaveAccessibleName();

    // Test error message accessibility
    const submitButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitButton);

    await waitFor(() => {
      const errorMessage = screen.getByText(/client name is required/i);
      expect(errorMessage).toHaveAttribute('role', 'alert');
    });
  });

  test('session management and security validation', async () => {
    const user = userEvent.setup();
    
    // Test initial unauthenticated state
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
    });

    // Test login
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(emailInput, 'operator@test.com');
    await user.type(passwordInput, 'operator123');

    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockAuthService.login.mockResolvedValue({
      token: 'valid-token',
      user: operatorUser,
    });

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);

    await user.click(loginButton);

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Test token expiration during session
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('Token expired'));
    mockPolicyService.getAllPolicies.mockRejectedValue(new Error('Unauthorized'));

    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    // Should redirect to login on token expiration
    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
      expect(screen.getByText(/session expired/i)).toBeInTheDocument();
    });

    // Test logout functionality
    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    const logoutButton = screen.getByRole('button', { name: /logout/i });
    mockAuthService.logout.mockResolvedValue(undefined);
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));

    await user.click(logoutButton);

    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
    });

    // Verify token is cleared from localStorage
    expect(localStorage.getItem('token')).toBeNull();
  });
});