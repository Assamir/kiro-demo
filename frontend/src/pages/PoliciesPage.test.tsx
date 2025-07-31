import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PoliciesPage from './PoliciesPage';
import { policyService } from '../services/policyService';

// Mock the policy service
jest.mock('../services/policyService');
const mockedPolicyService = policyService as jest.Mocked<typeof policyService>;

// Mock the child components
jest.mock('../components/policies/PolicyList', () => {
  return function MockPolicyList({ policies, onEditPolicy, onCancelPolicy, onGeneratePdf }: any) {
    return (
      <div data-testid="policy-list">
        <div>Policy List - {policies.length} policies</div>
        <button onClick={() => onEditPolicy(policies[0])}>Edit First Policy</button>
        <button onClick={() => onCancelPolicy(policies[0])}>Cancel First Policy</button>
        <button onClick={() => onGeneratePdf(policies[0])}>Generate PDF</button>
      </div>
    );
  };
});

jest.mock('../components/policies/PolicyForm', () => {
  return function MockPolicyForm({ open, onSubmit, onClose }: any) {
    if (!open) return null;
    return (
      <div data-testid="policy-form">
        <button onClick={() => onSubmit({ clientId: 1, vehicleId: 1, insuranceType: 'OC', startDate: '2024-01-01', endDate: '2024-12-31' })}>
          Submit Form
        </button>
        <button onClick={onClose}>Close Form</button>
      </div>
    );
  };
});

jest.mock('../components/policies/CancelPolicyDialog', () => {
  return function MockCancelPolicyDialog({ open, onConfirm, onClose }: any) {
    if (!open) return null;
    return (
      <div data-testid="cancel-dialog">
        <button onClick={onConfirm}>Confirm Cancel</button>
        <button onClick={onClose}>Close Dialog</button>
      </div>
    );
  };
});

const mockPolicies = [
  {
    id: 1,
    policyNumber: 'POL-2024-001',
    clientName: 'John Doe',
    vehicleRegistration: 'ABC123',
    insuranceType: 'OC' as const,
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    premium: 1200.00,
    status: 'ACTIVE' as const,
  },
];

const mockClients = [
  {
    id: 1,
    fullName: 'John Doe',
    pesel: '12345678901',
    address: '123 Main St',
    email: 'john@example.com',
    phoneNumber: '+48123456789',
  },
];

const mockVehicles = [
  {
    id: 1,
    make: 'Toyota',
    model: 'Corolla',
    yearOfManufacture: 2020,
    registrationNumber: 'ABC123',
    vin: '1234567890ABCDEFG',
    engineCapacity: 1600,
    power: 120,
    firstRegistrationDate: '2020-01-15',
  },
];

describe('PoliciesPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedPolicyService.getAllPolicies.mockResolvedValue(mockPolicies);
    mockedPolicyService.getAllClients.mockResolvedValue(mockClients);
    mockedPolicyService.getAllVehicles.mockResolvedValue(mockVehicles);
  });

  test('renders policies page with header and create button', async () => {
    render(<PoliciesPage />);
    
    expect(screen.getByText('Policy Management')).toBeInTheDocument();
    expect(screen.getByText('Create, edit, and manage insurance policies.')).toBeInTheDocument();
    expect(screen.getByText('Create Policy')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
  });

  test('loads initial data on mount', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(mockedPolicyService.getAllPolicies).toHaveBeenCalled();
      expect(mockedPolicyService.getAllClients).toHaveBeenCalled();
      expect(mockedPolicyService.getAllVehicles).toHaveBeenCalled();
    });
  });

  test('opens create policy form when create button is clicked', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    const createButton = screen.getByText('Create Policy');
    fireEvent.click(createButton);
    
    expect(screen.getByTestId('policy-form')).toBeInTheDocument();
  });

  test('opens edit policy form when edit is triggered', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    const editButton = screen.getByText('Edit First Policy');
    fireEvent.click(editButton);
    
    expect(screen.getByTestId('policy-form')).toBeInTheDocument();
  });

  test('opens cancel dialog when cancel is triggered', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    const cancelButton = screen.getByText('Cancel First Policy');
    fireEvent.click(cancelButton);
    
    expect(screen.getByTestId('cancel-dialog')).toBeInTheDocument();
  });

  test('creates new policy successfully', async () => {
    const mockCreatedPolicy = { ...mockPolicies[0], id: 2, policyNumber: 'POL-2024-002' };
    mockedPolicyService.createPolicy.mockResolvedValue(mockCreatedPolicy);
    
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    // Open create form
    const createButton = screen.getByText('Create Policy');
    fireEvent.click(createButton);
    
    // Submit form
    const submitButton = screen.getByText('Submit Form');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(mockedPolicyService.createPolicy).toHaveBeenCalledWith({
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
      });
    });
  });

  test('cancels policy successfully', async () => {
    mockedPolicyService.cancelPolicy.mockResolvedValue();
    
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    // Open cancel dialog
    const cancelButton = screen.getByText('Cancel First Policy');
    fireEvent.click(cancelButton);
    
    // Confirm cancellation
    const confirmButton = screen.getByText('Confirm Cancel');
    fireEvent.click(confirmButton);
    
    await waitFor(() => {
      expect(mockedPolicyService.cancelPolicy).toHaveBeenCalledWith(1);
    });
  });

  test('generates PDF successfully', async () => {
    const mockBlob = new Blob(['pdf content'], { type: 'application/pdf' });
    mockedPolicyService.generatePolicyPdf.mockResolvedValue(mockBlob);
    
    // Mock URL.createObjectURL and related methods
    const mockCreateObjectURL = jest.fn().mockReturnValue('blob:mock-url');
    const mockRevokeObjectURL = jest.fn();
    global.URL.createObjectURL = mockCreateObjectURL;
    global.URL.revokeObjectURL = mockRevokeObjectURL;
    
    // Mock document.createElement and appendChild
    const mockLink = {
      href: '',
      download: '',
      click: jest.fn(),
    };
    const mockCreateElement = jest.fn().mockReturnValue(mockLink);
    const mockAppendChild = jest.fn();
    const mockRemoveChild = jest.fn();
    
    document.createElement = mockCreateElement;
    document.body.appendChild = mockAppendChild;
    document.body.removeChild = mockRemoveChild;
    
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    const pdfButton = screen.getByText('Generate PDF');
    fireEvent.click(pdfButton);
    
    await waitFor(() => {
      expect(mockedPolicyService.generatePolicyPdf).toHaveBeenCalledWith(1);
      expect(mockCreateObjectURL).toHaveBeenCalledWith(mockBlob);
      expect(mockLink.download).toBe('policy_POL-2024-001.pdf');
      expect(mockLink.click).toHaveBeenCalled();
    });
  });

  test('handles data loading error', async () => {
    mockedPolicyService.getAllPolicies.mockRejectedValue(new Error('Network error'));
    
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByText(/Failed to load data/)).toBeInTheDocument();
    });
  });

  test('closes form when close button is clicked', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    // Open form
    const createButton = screen.getByText('Create Policy');
    fireEvent.click(createButton);
    
    expect(screen.getByTestId('policy-form')).toBeInTheDocument();
    
    // Close form
    const closeButton = screen.getByText('Close Form');
    fireEvent.click(closeButton);
    
    expect(screen.queryByTestId('policy-form')).not.toBeInTheDocument();
  });

  test('closes cancel dialog when close button is clicked', async () => {
    render(<PoliciesPage />);
    
    await waitFor(() => {
      expect(screen.getByTestId('policy-list')).toBeInTheDocument();
    });
    
    // Open dialog
    const cancelButton = screen.getByText('Cancel First Policy');
    fireEvent.click(cancelButton);
    
    expect(screen.getByTestId('cancel-dialog')).toBeInTheDocument();
    
    // Close dialog
    const closeButton = screen.getByText('Close Dialog');
    fireEvent.click(closeButton);
    
    expect(screen.queryByTestId('cancel-dialog')).not.toBeInTheDocument();
  });
});