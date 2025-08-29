import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { AuthProvider } from '../contexts/AuthContext';
import { NotificationProvider } from '../contexts/NotificationContext';
import PoliciesPage from '../pages/PoliciesPage';
import theme from '../theme/theme';

// Mock services
const mockPolicyService = {
  getAllPolicies: jest.fn(),
  createPolicy: jest.fn(),
  updatePolicy: jest.fn(),
  cancelPolicy: jest.fn(),
  getPolicyById: jest.fn(),
  getPoliciesByClient: jest.fn(),
  generatePdf: jest.fn(),
};

const mockAuthService = {
  getCurrentUser: jest.fn(),
  login: jest.fn(),
  logout: jest.fn(),
};

const mockRatingService = {
  calculatePremium: jest.fn(),
  getRatingTables: jest.fn(),
};

jest.mock('../services/policyService', () => ({
  policyService: mockPolicyService,
}));

jest.mock('../services/authService', () => ({
  authService: mockAuthService,
}));

jest.mock('../services/ratingService', () => ({
  ratingService: mockRatingService,
}));

const renderPoliciesPage = () => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <NotificationProvider>
          <AuthProvider>
            <PoliciesPage />
          </AuthProvider>
        </NotificationProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('Policy Management Flow Integration', () => {
  const mockPolicies = [
    {
      id: 1,
      policyNumber: 'POL-001',
      clientName: 'John Doe',
      vehicleRegistration: 'ABC123',
      insuranceType: 'OC',
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      premium: 1200.00,
      status: 'ACTIVE',
    },
    {
      id: 2,
      policyNumber: 'POL-002',
      clientName: 'Jane Smith',
      vehicleRegistration: 'XYZ789',
      insuranceType: 'AC',
      startDate: '2024-02-01',
      endDate: '2025-01-31',
      premium: 2400.00,
      status: 'ACTIVE',
    },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock current user as operator
    mockAuthService.getCurrentUser.mockResolvedValue({
      id: 2,
      firstName: 'Jane',
      lastName: 'Operator',
      email: 'jane.operator@test.com',
      role: 'OPERATOR',
    });
    
    // Mock initial policies list
    mockPolicyService.getAllPolicies.mockResolvedValue(mockPolicies);
    
    // Mock rating service
    mockRatingService.calculatePremium.mockResolvedValue({
      basePremium: 1000.00,
      totalPremium: 1200.00,
      appliedFactors: [
        { factor: 'driver_age_26_35', multiplier: 1.2 }
      ]
    });
  });

  test('complete policy creation workflow for OC insurance', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    // Wait for initial policies to load
    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    // Step 1: Start creating new policy
    const createButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createButton);

    // Step 2: Fill in basic policy information
    const clientNameInput = screen.getByLabelText(/client name/i);
    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);

    await user.type(clientNameInput, 'New Client');
    await user.type(vehicleRegInput, 'NEW123');
    await user.click(insuranceTypeSelect);
    
    const ocOption = screen.getByRole('option', { name: /oc/i });
    await user.click(ocOption);

    // Step 3: Fill in OC-specific fields
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

    // Step 4: Calculate premium
    const calculateButton = screen.getByRole('button', { name: /calculate premium/i });
    await user.click(calculateButton);

    await waitFor(() => {
      expect(mockRatingService.calculatePremium).toHaveBeenCalled();
      expect(screen.getByText(/total premium: 1,200.00/i)).toBeInTheDocument();
    });

    // Step 5: Submit policy
    const newPolicy = {
      id: 3,
      policyNumber: 'POL-003',
      clientName: 'New Client',
      vehicleRegistration: 'NEW123',
      insuranceType: 'OC',
      startDate: '2024-03-01',
      endDate: '2025-02-28',
      premium: 1200.00,
      status: 'ACTIVE',
    };

    mockPolicyService.createPolicy.mockResolvedValue(newPolicy);
    mockPolicyService.getAllPolicies.mockResolvedValue([...mockPolicies, newPolicy]);

    const submitButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitButton);

    // Verify policy creation API call
    await waitFor(() => {
      expect(mockPolicyService.createPolicy).toHaveBeenCalledWith({
        clientName: 'New Client',
        vehicleRegistration: 'NEW123',
        insuranceType: 'OC',
        startDate: '2024-03-01',
        endDate: '2025-02-28',
        guaranteedSum: 5000000,
        coverageArea: 'Europe',
        premium: 1200.00,
      });
    });

    // Wait for success notification and updated list
    await waitFor(() => {
      expect(screen.getByText(/policy created successfully/i)).toBeInTheDocument();
      expect(screen.getByText('POL-003')).toBeInTheDocument();
      expect(screen.getByText('New Client')).toBeInTheDocument();
    });
  });

  test('complete policy creation workflow for AC insurance', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });

    // Start creating AC policy
    const createButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createButton);

    // Fill basic information
    const clientNameInput = screen.getByLabelText(/client name/i);
    const vehicleRegInput = screen.getByLabelText(/vehicle registration/i);
    const insuranceTypeSelect = screen.getByLabelText(/insurance type/i);

    await user.type(clientNameInput, 'AC Client');
    await user.type(vehicleRegInput, 'AC123');
    await user.click(insuranceTypeSelect);
    
    const acOption = screen.getByRole('option', { name: /ac/i });
    await user.click(acOption);

    // Fill AC-specific fields
    await waitFor(() => {
      expect(screen.getByLabelText(/variant/i)).toBeInTheDocument();
    });

    const variantSelect = screen.getByLabelText(/variant/i);
    const sumInsuredInput = screen.getByLabelText(/sum insured/i);
    const coverageScopeInput = screen.getByLabelText(/coverage scope/i);
    const deductibleInput = screen.getByLabelText(/deductible/i);
    const workshopTypeInput = screen.getByLabelText(/workshop type/i);

    await user.click(variantSelect);
    const standardOption = screen.getByRole('option', { name: /standard/i });
    await user.click(standardOption);

    await user.type(sumInsuredInput, '60000');
    await user.type(coverageScopeInput, 'Comprehensive');
    await user.type(deductibleInput, '1000');
    await user.type(workshopTypeInput, 'Authorized');

    // Set dates
    const startDateInput = screen.getByLabelText(/start date/i);
    const endDateInput = screen.getByLabelText(/end date/i);
    await user.type(startDateInput, '2024-03-01');
    await user.type(endDateInput, '2025-02-28');

    // Calculate and submit
    const calculateButton = screen.getByRole('button', { name: /calculate premium/i });
    await user.click(calculateButton);

    await waitFor(() => {
      expect(screen.getByText(/total premium: 1,200.00/i)).toBeInTheDocument();
    });

    const newACPolicy = {
      id: 4,
      policyNumber: 'POL-004',
      clientName: 'AC Client',
      vehicleRegistration: 'AC123',
      insuranceType: 'AC',
      startDate: '2024-03-01',
      endDate: '2025-02-28',
      premium: 1200.00,
      status: 'ACTIVE',
    };

    mockPolicyService.createPolicy.mockResolvedValue(newACPolicy);
    mockPolicyService.getAllPolicies.mockResolvedValue([...mockPolicies, newACPolicy]);

    const submitButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockPolicyService.createPolicy).toHaveBeenCalledWith(
        expect.objectContaining({
          insuranceType: 'AC',
          variant: 'STANDARD',
          sumInsured: 60000,
          deductible: 1000,
        })
      );
    });
  });

  test('policy search and filtering functionality', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.getByText('POL-002')).toBeInTheDocument();
    });

    // Test search by policy number
    const searchInput = screen.getByPlaceholderText(/search policies/i);
    await user.type(searchInput, 'POL-001');

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.queryByText('POL-002')).not.toBeInTheDocument();
    });

    // Clear search
    await user.clear(searchInput);

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.getByText('POL-002')).toBeInTheDocument();
    });

    // Test filter by insurance type
    const insuranceTypeFilter = screen.getByLabelText(/filter by insurance type/i);
    await user.click(insuranceTypeFilter);
    
    const ocOption = screen.getByRole('option', { name: /oc/i });
    await user.click(ocOption);

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
      expect(screen.queryByText('POL-002')).not.toBeInTheDocument();
    });

    // Test filter by status
    const statusFilter = screen.getByLabelText(/filter by status/i);
    await user.click(statusFilter);
    
    const activeOption = screen.getByRole('option', { name: /active/i });
    await user.click(activeOption);

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });
  });

  test('policy update and cancellation workflow', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });

    // Find and edit policy
    const policyRows = screen.getAllByRole('row');
    const policyRow = policyRows.find(row => 
      within(row).queryByText('POL-001')
    );
    
    expect(policyRow).toBeInTheDocument();
    const editButton = within(policyRow!).getByRole('button', { name: /edit/i });
    await user.click(editButton);

    // Update policy details
    const discountInput = screen.getByLabelText(/discount\/surcharge/i);
    await user.type(discountInput, '100');

    // Mock successful update
    const updatedPolicy = { ...mockPolicies[0], discountSurcharge: 100 };
    mockPolicyService.updatePolicy.mockResolvedValue(updatedPolicy);
    mockPolicyService.getAllPolicies.mockResolvedValue([
      updatedPolicy,
      mockPolicies[1],
    ]);

    const updateButton = screen.getByRole('button', { name: /update policy/i });
    await user.click(updateButton);

    await waitFor(() => {
      expect(mockPolicyService.updatePolicy).toHaveBeenCalledWith(1, {
        discountSurcharge: 100,
      });
    });

    // Test policy cancellation
    const cancelButton = within(policyRow!).getByRole('button', { name: /cancel/i });
    await user.click(cancelButton);

    // Confirm cancellation
    const confirmButton = screen.getByRole('button', { name: /confirm/i });
    
    mockPolicyService.cancelPolicy.mockResolvedValue(undefined);
    const canceledPolicy = { ...mockPolicies[0], status: 'CANCELED' };
    mockPolicyService.getAllPolicies.mockResolvedValue([
      canceledPolicy,
      mockPolicies[1],
    ]);

    await user.click(confirmButton);

    await waitFor(() => {
      expect(mockPolicyService.cancelPolicy).toHaveBeenCalledWith(1);
      expect(screen.getByText(/policy canceled successfully/i)).toBeInTheDocument();
    });
  });

  test('PDF generation workflow', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });

    // Find policy and generate PDF
    const policyRows = screen.getAllByRole('row');
    const policyRow = policyRows.find(row => 
      within(row).queryByText('POL-001')
    );
    
    const pdfButton = within(policyRow!).getByRole('button', { name: /generate pdf/i });
    
    // Mock PDF generation
    const mockPdfBlob = new Blob(['PDF content'], { type: 'application/pdf' });
    mockPolicyService.generatePdf.mockResolvedValue(mockPdfBlob);

    await user.click(pdfButton);

    await waitFor(() => {
      expect(mockPolicyService.generatePdf).toHaveBeenCalledWith(1);
    });

    // Verify PDF preview modal opens
    await waitFor(() => {
      expect(screen.getByText(/pdf preview/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /download/i })).toBeInTheDocument();
    });
  });

  test('form validation and error handling', async () => {
    const user = userEvent.setup();
    renderPoliciesPage();

    await waitFor(() => {
      expect(screen.getByText('POL-001')).toBeInTheDocument();
    });

    // Start creating policy
    const createButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(createButton);

    // Try to submit empty form
    const submitButton = screen.getByRole('button', { name: /create policy/i });
    await user.click(submitButton);

    // Check for validation errors
    await waitFor(() => {
      expect(screen.getByText(/client name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/vehicle registration is required/i)).toBeInTheDocument();
      expect(screen.getByText(/insurance type is required/i)).toBeInTheDocument();
    });

    // Test date validation
    const startDateInput = screen.getByLabelText(/start date/i);
    const endDateInput = screen.getByLabelText(/end date/i);
    
    await user.type(startDateInput, '2024-12-31');
    await user.type(endDateInput, '2024-01-01');

    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/end date must be after start date/i)).toBeInTheDocument();
    });

    // Test server error handling
    const clientNameInput = screen.getByLabelText(/client name/i);
    await user.type(clientNameInput, 'Test Client');

    mockPolicyService.createPolicy.mockRejectedValue(new Error('Server error'));

    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/server error/i)).toBeInTheDocument();
    });
  });
});