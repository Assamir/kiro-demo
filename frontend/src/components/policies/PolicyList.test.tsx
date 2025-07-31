import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PolicyList from './PolicyList';
import { Policy } from '../../types/policy';

const mockPolicies: Policy[] = [
  {
    id: 1,
    policyNumber: 'POL-2024-001',
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
    policyNumber: 'POL-2024-002',
    clientName: 'Jane Smith',
    vehicleRegistration: 'XYZ789',
    insuranceType: 'AC',
    startDate: '2024-02-01',
    endDate: '2025-01-31',
    premium: 2500.00,
    status: 'CANCELED',
  },
];

const mockProps = {
  policies: mockPolicies,
  loading: false,
  onEditPolicy: jest.fn(),
  onCancelPolicy: jest.fn(),
  onGeneratePdf: jest.fn(),
};

describe('PolicyList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders policy list with policies', () => {
    render(<PolicyList {...mockProps} />);
    
    expect(screen.getByText('POL-2024-001')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('ABC123')).toBeInTheDocument();
    expect(screen.getByText('POL-2024-002')).toBeInTheDocument();
    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    expect(screen.getByText('XYZ789')).toBeInTheDocument();
  });

  test('displays loading state', () => {
    render(<PolicyList {...mockProps} loading={true} />);
    
    expect(screen.getByText('Loading policies...')).toBeInTheDocument();
  });

  test('displays empty state when no policies', () => {
    render(<PolicyList {...mockProps} policies={[]} />);
    
    expect(screen.getByText('No policies found')).toBeInTheDocument();
  });

  test('filters policies by search term', async () => {
    render(<PolicyList {...mockProps} />);
    
    const searchInput = screen.getByPlaceholderText(/search by client name/i);
    fireEvent.change(searchInput, { target: { value: 'John' } });
    
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
    });
  });

  test('filters policies by insurance type', async () => {
    render(<PolicyList {...mockProps} />);
    
    const insuranceTypeSelect = screen.getByLabelText('Insurance Type');
    fireEvent.mouseDown(insuranceTypeSelect);
    fireEvent.click(screen.getByText('OC'));
    
    await waitFor(() => {
      expect(screen.getByText('POL-2024-001')).toBeInTheDocument();
      expect(screen.queryByText('POL-2024-002')).not.toBeInTheDocument();
    });
  });

  test('filters policies by status', async () => {
    render(<PolicyList {...mockProps} />);
    
    const statusSelect = screen.getByLabelText('Status');
    fireEvent.mouseDown(statusSelect);
    fireEvent.click(screen.getByText('Active'));
    
    await waitFor(() => {
      expect(screen.getByText('POL-2024-001')).toBeInTheDocument();
      expect(screen.queryByText('POL-2024-002')).not.toBeInTheDocument();
    });
  });

  test('calls onEditPolicy when edit button is clicked', () => {
    render(<PolicyList {...mockProps} />);
    
    const editButtons = screen.getAllByTitle('Edit policy');
    fireEvent.click(editButtons[0]);
    
    expect(mockProps.onEditPolicy).toHaveBeenCalledWith(mockPolicies[0]);
  });

  test('calls onCancelPolicy when cancel button is clicked', () => {
    render(<PolicyList {...mockProps} />);
    
    const cancelButtons = screen.getAllByTitle('Cancel policy');
    fireEvent.click(cancelButtons[0]);
    
    expect(mockProps.onCancelPolicy).toHaveBeenCalledWith(mockPolicies[0]);
  });

  test('calls onGeneratePdf when PDF button is clicked', () => {
    render(<PolicyList {...mockProps} />);
    
    const pdfButtons = screen.getAllByTitle('Generate PDF');
    fireEvent.click(pdfButtons[0]);
    
    expect(mockProps.onGeneratePdf).toHaveBeenCalledWith(mockPolicies[0]);
  });

  test('disables edit and cancel buttons for non-active policies', () => {
    render(<PolicyList {...mockProps} />);
    
    const editButtons = screen.getAllByTitle('Edit policy');
    const cancelButtons = screen.getAllByTitle('Cancel policy');
    
    // First policy is ACTIVE - buttons should be enabled
    expect(editButtons[0]).not.toBeDisabled();
    expect(cancelButtons[0]).not.toBeDisabled();
    
    // Second policy is CANCELED - buttons should be disabled
    expect(editButtons[1]).toBeDisabled();
    expect(cancelButtons[1]).toBeDisabled();
  });

  test('displays correct currency formatting', () => {
    render(<PolicyList {...mockProps} />);
    
    expect(screen.getByText('1 200,00 zł')).toBeInTheDocument();
    expect(screen.getByText('2 500,00 zł')).toBeInTheDocument();
  });

  test('displays correct date formatting', () => {
    render(<PolicyList {...mockProps} />);
    
    expect(screen.getByText('01.01.2024 - 31.12.2024')).toBeInTheDocument();
    expect(screen.getByText('01.02.2024 - 31.01.2025')).toBeInTheDocument();
  });

  test('shows filtered results count', async () => {
    render(<PolicyList {...mockProps} />);
    
    expect(screen.getByText('Showing 2 of 2 policies')).toBeInTheDocument();
    
    const searchInput = screen.getByPlaceholderText(/search by client name/i);
    fireEvent.change(searchInput, { target: { value: 'John' } });
    
    await waitFor(() => {
      expect(screen.getByText('Showing 1 of 2 policies')).toBeInTheDocument();
    });
  });
});