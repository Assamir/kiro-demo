import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import CancelPolicyDialog from './CancelPolicyDialog';
import { Policy } from '../../types/policy';

const mockPolicy: Policy = {
  id: 1,
  policyNumber: 'POL-2024-001',
  clientName: 'John Doe',
  vehicleRegistration: 'ABC123',
  insuranceType: 'OC',
  startDate: '2024-01-01',
  endDate: '2024-12-31',
  premium: 1200.00,
  status: 'ACTIVE',
};

const mockProps = {
  open: true,
  onClose: jest.fn(),
  onConfirm: jest.fn(),
  policy: mockPolicy,
  loading: false,
};

describe('CancelPolicyDialog', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders cancel policy dialog', () => {
    render(<CancelPolicyDialog {...mockProps} />);
    
    expect(screen.getByText('Cancel Policy')).toBeInTheDocument();
    expect(screen.getByText('Are you sure you want to cancel this policy? This action cannot be undone.')).toBeInTheDocument();
  });

  test('displays policy details', () => {
    render(<CancelPolicyDialog {...mockProps} />);
    
    expect(screen.getByText('POL-2024-001')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('ABC123')).toBeInTheDocument();
    expect(screen.getByText('01.01.2024 - 31.12.2024')).toBeInTheDocument();
    expect(screen.getByText('1 200,00 zł')).toBeInTheDocument();
  });

  test('calls onClose when Keep Policy button is clicked', () => {
    render(<CancelPolicyDialog {...mockProps} />);
    
    const keepButton = screen.getByText('Keep Policy');
    fireEvent.click(keepButton);
    
    expect(mockProps.onClose).toHaveBeenCalled();
  });

  test('calls onConfirm when Cancel Policy button is clicked', async () => {
    const mockConfirm = jest.fn().mockResolvedValue(undefined);
    render(<CancelPolicyDialog {...mockProps} onConfirm={mockConfirm} />);
    
    const cancelButton = screen.getByText('Cancel Policy');
    fireEvent.click(cancelButton);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalled();
    });
  });

  test('disables buttons when loading', () => {
    render(<CancelPolicyDialog {...mockProps} loading={true} />);
    
    const keepButton = screen.getByText('Keep Policy');
    const cancelButton = screen.getByText('Canceling...');
    
    expect(keepButton).toBeDisabled();
    expect(cancelButton).toBeDisabled();
  });

  test('does not render when policy is null', () => {
    render(<CancelPolicyDialog {...mockProps} policy={null} />);
    
    expect(screen.queryByText('Cancel Policy')).not.toBeInTheDocument();
  });

  test('displays insurance type chip', () => {
    render(<CancelPolicyDialog {...mockProps} />);
    
    expect(screen.getByText('OC')).toBeInTheDocument();
  });

  test('displays warning message about policy cancellation', () => {
    render(<CancelPolicyDialog {...mockProps} />);
    
    expect(screen.getByText('Once canceled, the policy will no longer provide coverage and cannot be reactivated.')).toBeInTheDocument();
  });

  test('closes dialog after successful confirmation', async () => {
    const mockConfirm = jest.fn().mockResolvedValue(undefined);
    render(<CancelPolicyDialog {...mockProps} onConfirm={mockConfirm} />);
    
    const cancelButton = screen.getByText('Cancel Policy');
    fireEvent.click(cancelButton);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalled();
      expect(mockProps.onClose).toHaveBeenCalled();
    });
  });

  test('handles confirmation error gracefully', async () => {
    const mockConfirm = jest.fn().mockRejectedValue(new Error('Network error'));
    render(<CancelPolicyDialog {...mockProps} onConfirm={mockConfirm} />);
    
    const cancelButton = screen.getByText('Cancel Policy');
    fireEvent.click(cancelButton);
    
    await waitFor(() => {
      expect(mockConfirm).toHaveBeenCalled();
      // Dialog should not close on error
      expect(mockProps.onClose).not.toHaveBeenCalled();
    });
  });
});