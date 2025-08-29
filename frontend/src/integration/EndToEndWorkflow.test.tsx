import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
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

describe('End-to-End Workflow Integration Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    
    // Default mock responses
    mockUserService.getAllUsers.mockResolvedValue([]);
    mockPolicyService.getAllPolicies.mockResolvedValue([]);
  });

  test('complete admin workflow - login, create user, manage system', async () => {
    const user = userEvent.setup();
    
    // Mock initial state - no current user
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    
    renderApp();

    // Step 1: Should show login page
    expect(await screen.findByText(/insurance backoffice/i)).toBeInTheDocument();
    expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();

    // Step 2: Admin logs in
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(emailInput, 'admin@test.com');
    await user.type(passwordInput, 'admin123');

    // Mock successful login
    const adminUser = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@test.com',
      role: 'ADMIN',
    };

    mockAuthService.login.mockResolvedValue({
      token: 'admin-token',
      user: adminUser,
    });

    mockAuthService.getCurrentUser.mockResolvedValue(adminUser);

    await user.click(loginButton);

    // Step 3: Should navigate to dashboard
    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
      expect(screen.getByText(/admin user/i)).toBeInTheDocument();
    });

    // Step 4: Navigate to user management
    const usersNavItem = screen.getByRole('button', { name: /users/i });
    await user.click(usersNavItem);

    await waitFor(() => {
      expect(screen.getByText(/user management/i)).toBeInTheDocument();
    });

    // Step 5: Create new user
    const createUserButton = screen.getByRole('button', { name: /add user/i });
    await user.click(createUserButton);

    // Fill user form
    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const userEmailInput = screen.getByLabelText(/email/i);
    const userPasswordInput = screen.getByLabelText(/password/i);
    const roleSelect = screen.getByLabelText(/role/i);

    await user.type(firstNameInput, 'New');
    await user.type(lastNameInput, 'Operator');
    await user.type(userEmailInput, 'operator@test.com');
    await user.type(userPasswordInput, 'password123');
    await user.click(roleSelect);
    
    const operatorOption = screen.getByRole('option', { name: /operator/i });
    await user.click(operatorOption);

    // Mock user creation
    const newOperator = {
      id: 2,
      firstName: 'New',
      lastName: 'Operator',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    mockUserService.createUser.mockResolvedValue(newOperator);
    mockUserService.getAllUsers.mockResolvedValue([adminUser, newOperator]);

    const submitUserButton = screen.getByRole('button', { name: /create user/i });
    await user.click(submitUserButton);

    // Step 6: Verify user creation
    await waitFor(() => {
      expect(mockUserService.createUser).toHaveBeenCalledWith({
        firstName: 'New',
        lastName: 'Operator',
        email: 'operator@test.com',
        password: 'password123',
        role: 'OPERATOR',
      });
      expect(screen.getByText(/user created successfully/i)).toBeInTheDocument();
    });

    // Step 7: Logout
    const logoutButton = screen.getByRole('button', { name: /logout/i });
    mockAuthService.logout.mockResolvedValue(undefined);
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));

    await user.click(logoutButton);

    // Should return to login page
    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
    });
  });

  test('complete operator workflow - login, create policy, generate PDF', async () => {
    const user = userEvent.setup();
    
    // Mock initial state - no current user
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    
    renderApp();

    // Step 1: Operator logs in
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
      token: 'operator-token',
      user: operatorUser,
    });

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);

    await user.click(loginButton);

    // Step 2: Navigate to policies
    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    await waitFor(() => {
      expect(screen.getByText(/policy management/i)).toBeInTheDocument();
    });

    // Step 3: Create new policy
    const createPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createPolicyButton);

    // Fill policy form
    const clientNameInput = screen.getByLabelText(/client name/i);
    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);

    await user.type(clientNameInput, 'John Doe');
    await user.type(vehicleRegInput, 'ABC123');
    await user.click(insuranceTypeSelect);
    
    const ocOption = screen.getByRole('option', { name: /oc/i });
    await user.click(ocOption);

    // Fill OC-specific fields
    await waitFor(() => {
      expect(screen.getByLabelText(/guaranteed sum/i)).toBeInTheDocument();
    });

    const guaranteedSumInput = screen.getByLabelText(/guaranteed sum/i);
    const coverageAreaInput = screen.getByLabelText(/coverage area/i);
    const startDateInput = screen.getByLabelText(/start date/i);
    const endDateInput = screen.getByLabelText(/end date/i);

    await user.type(guaranteedSumInput, '5000000');
    await user.type(coverageAreaInput, 'Europe');
    await user.type(startDateInput, '2024-03-01');
    await user.type(endDateInput, '2025-02-28');

    // Calculate premium
    mockRatingService.calculatePremium.mockResolvedValue({
      basePremium: 1000.00,
      totalPremium: 1200.00,
      appliedFactors: []
    });

    const calculateButton = screen.getByRole('button', { name: /calculate premium/i });
    await user.click(calculateButton);

    await waitFor(() => {
      expect(screen.getByText(/total premium: 1,200.00/i)).toBeInTheDocument();
    });

    // Submit policy
    const newPolicy = {
      id: 1,
      policyNumber: 'POL-001',
      clientName: 'John Doe',
      vehicleRegistration: 'ABC123',
      insuranceType: 'OC',
      startDate: '2024-03-01',
      endDate: '2025-02-28',
      premium: 1200.00,
      status: 'ACTIVE',
    };

    mockPolicyService.createPolicy.mockResolvedValue(newPolicy);
    mockPolicyService.getAllPolicies.mockResolvedValue([newPolicy]);

    const submitPolicyButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitPolicyButton);

    // Step 4: Verify policy creation and generate PDF
    await waitFor(() => {
      expect(screen.getByText(/policy created successfully/i)).toBeInTheDocument();
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });

    // Generate PDF
    const pdfButton = screen.getByRole('button', { name: /generate pdf/i });
    
    const mockPdfBlob = new Blob(['PDF content'], { type: 'application/pdf' });
    mockPolicyService.generatePdf.mockResolvedValue(mockPdfBlob);

    await user.click(pdfButton);

    await waitFor(() => {
      expect(mockPolicyService.generatePdf).toHaveBeenCalledWith(1);
      expect(screen.getByText(/pdf preview/i)).toBeInTheDocument();
    });
  });

  test('role-based access control workflow', async () => {
    const user = userEvent.setup();
    
    // Test operator trying to access admin features
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

    // Operator should not see Users navigation
    expect(screen.queryByRole('button', { name: /users/i })).not.toBeInTheDocument();

    // Operator should see Policies navigation
    expect(screen.getByRole('button', { name: /policies/i })).toBeInTheDocument();

    // Test admin access
    const adminUser = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@test.com',
      role: 'ADMIN',
    };

    mockAuthService.getCurrentUser.mockResolvedValue(adminUser);
    
    // Re-render with admin user
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/admin user/i)).toBeInTheDocument();
    });

    // Admin should see Users navigation
    expect(screen.getByRole('button', { name: /users/i })).toBeInTheDocument();

    // Admin should not see Policies navigation (admin cannot create policies)
    expect(screen.queryByRole('button', { name: /policies/i })).not.toBeInTheDocument();
  });

  test('error handling and recovery workflow', async () => {
    const user = userEvent.setup();
    
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    
    renderApp();

    // Test login error
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole('button', { name: /sign in/i });

    await user.type(emailInput, 'wrong@test.com');
    await user.type(passwordInput, 'wrongpassword');

    mockAuthService.login.mockRejectedValue(new Error('Invalid credentials'));

    await user.click(loginButton);

    await waitFor(() => {
      expect(screen.getByText(/invalid credentials/i)).toBeInTheDocument();
    });

    // Test successful login after error
    await user.clear(emailInput);
    await user.clear(passwordInput);
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
      token: 'operator-token',
      user: operatorUser,
    });

    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);

    await user.click(loginButton);

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Test network error recovery
    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    // Mock network error
    mockPolicyService.getAllPolicies.mockRejectedValue(new Error('Network error'));

    await waitFor(() => {
      expect(screen.getByText(/network error/i)).toBeInTheDocument();
    });

    // Test retry functionality
    const retryButton = screen.getByRole('button', { name: /retry/i });
    
    mockPolicyService.getAllPolicies.mockResolvedValue([]);
    
    await user.click(retryButton);

    await waitFor(() => {
      expect(screen.getByText(/policy management/i)).toBeInTheDocument();
      expect(screen.queryByText(/network error/i)).not.toBeInTheDocument();
    });
  });

  test('session management and token expiration', async () => {
    const user = userEvent.setup();
    
    const operatorUser = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@test.com',
      role: 'OPERATOR',
    };

    // Start with valid session
    mockAuthService.getCurrentUser.mockResolvedValue(operatorUser);
    
    renderApp();

    await waitFor(() => {
      expect(screen.getByText(/welcome to insurance backoffice/i)).toBeInTheDocument();
    });

    // Simulate token expiration during API call
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('Token expired'));
    mockPolicyService.getAllPolicies.mockRejectedValue(new Error('Unauthorized'));

    const policiesNavItem = screen.getByRole('button', { name: /policies/i });
    await user.click(policiesNavItem);

    // Should redirect to login page
    await waitFor(() => {
      expect(screen.getByText(/please sign in to continue/i)).toBeInTheDocument();
    });

    // Should show session expired message
    expect(screen.getByText(/session expired/i)).toBeInTheDocument();
  });
});