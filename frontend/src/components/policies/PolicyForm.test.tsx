import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PolicyForm from './PolicyForm';
import { Policy, Client, Vehicle } from '../../types/policy';

const mockClients: Client[] = [
  {
    id: 1,
    fullName: 'John Doe',
    pesel: '12345678901',
    address: '123 Main St',
    email: 'john@example.com',
    phoneNumber: '+48123456789',
  },
  {
    id: 2,
    fullName: 'Jane Smith',
    pesel: '98765432109',
    address: '456 Oak Ave',
    email: 'jane@example.com',
    phoneNumber: '+48987654321',
  },
];

const mockVehicles: Vehicle[] = [
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
  {
    id: 2,
    make: 'BMW',
    model: 'X5',
    yearOfManufacture: 2019,
    registrationNumber: 'XYZ789',
    vin: 'ABCDEFG1234567890',
    engineCapacity: 3000,
    power: 250,
    firstRegistrationDate: '2019-03-20',
  },
];

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
  onSubmit: jest.fn(),
  policy: null,
  clients: mockClients,
  vehicles: mockVehicles,
  loading: false,
};

describe('PolicyForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders create policy form', () => {
    render(<PolicyForm {...mockProps} />);
    
    expect(screen.getByText('Create New Policy')).toBeInTheDocument();
    expect(screen.getByLabelText('Client')).toBeInTheDocument();
    expect(screen.getByLabelText('Vehicle')).toBeInTheDocument();
    expect(screen.getByLabelText('Insurance Type')).toBeInTheDocument();
    expect(screen.getByLabelText('Start Date')).toBeInTheDocument();
    expect(screen.getByLabelText('End Date')).toBeInTheDocument();
  });

  test('renders edit policy form with existing data', () => {
    render(<PolicyForm {...mockProps} policy={mockPolicy} />);
    
    expect(screen.getByText('Edit Policy')).toBeInTheDocument();
  });

  test('validates required fields', async () => {
    render(<PolicyForm {...mockProps} />);
    
    const submitButton = screen.getByText('Create');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText('Client is required')).toBeInTheDocument();
      expect(screen.getByText('Vehicle is required')).toBeInTheDocument();
      expect(screen.getByText('Insurance type is required')).toBeInTheDocument();
    });
  });

  test('validates date range', async () => {
    render(<PolicyForm {...mockProps} />);
    
    // Set end date before start date
    const startDateInput = screen.getByLabelText('Start Date');
    const endDateInput = screen.getByLabelText('End Date');
    
    fireEvent.change(startDateInput, { target: { value: '2024-12-31' } });
    fireEvent.change(endDateInput, { target: { value: '2024-01-01' } });
    
    const submitButton = screen.getByText('Create');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText('End date must be after start date')).toBeInTheDocument();
    });
  });

  test('submits form with valid data', async () => {
    const mockSubmit = jest.fn().mockResolvedValue(undefined);
    render(<PolicyForm {...mockProps} onSubmit={mockSubmit} />);
    
    // Fill out the form
    const clientSelect = screen.getByLabelText('Client');
    fireEvent.mouseDown(clientSelect);
    fireEvent.click(screen.getByText('John Doe (12345678901)'));
    
    const vehicleSelect = screen.getByLabelText('Vehicle');
    fireEvent.mouseDown(vehicleSelect);
    fireEvent.click(screen.getByText('Toyota Corolla (ABC123)'));
    
    const insuranceTypeSelect = screen.getByLabelText('Insurance Type');
    fireEvent.mouseDown(insuranceTypeSelect);
    fireEvent.click(screen.getByText('OC - Obligatory Civil Liability'));
    
    const startDateInput = screen.getByLabelText('Start Date');
    const endDateInput = screen.getByLabelText('End Date');
    
    fireEvent.change(startDateInput, { target: { value: '2024-01-01' } });
    fireEvent.change(endDateInput, { target: { value: '2024-12-31' } });
    
    const submitButton = screen.getByText('Create');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledWith({
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        discountSurcharge: undefined,
      });
    });
  });

  test('displays client details when selected', async () => {
    render(<PolicyForm {...mockProps} />);
    
    const clientSelect = screen.getByLabelText('Client');
    fireEvent.mouseDown(clientSelect);
    fireEvent.click(screen.getByText('John Doe (12345678901)'));
    
    await waitFor(() => {
      expect(screen.getByText('Email: john@example.com | Phone: +48123456789')).toBeInTheDocument();
    });
  });

  test('displays vehicle details when selected', async () => {
    render(<PolicyForm {...mockProps} />);
    
    const vehicleSelect = screen.getByLabelText('Vehicle');
    fireEvent.mouseDown(vehicleSelect);
    fireEvent.click(screen.getByText('Toyota Corolla (ABC123)'));
    
    await waitFor(() => {
      expect(screen.getByText('Year: 2020 | Engine: 1600cc | Power: 120HP')).toBeInTheDocument();
    });
  });

  test('displays insurance type description when selected', async () => {
    render(<PolicyForm {...mockProps} />);
    
    const insuranceTypeSelect = screen.getByLabelText('Insurance Type');
    fireEvent.mouseDown(insuranceTypeSelect);
    fireEvent.click(screen.getByText('OC - Obligatory Civil Liability'));
    
    await waitFor(() => {
      expect(screen.getByText('Obligatory Civil Liability - mandatory coverage as per law')).toBeInTheDocument();
    });
  });

  test('handles discount/surcharge input', async () => {
    const mockSubmit = jest.fn().mockResolvedValue(undefined);
    render(<PolicyForm {...mockProps} onSubmit={mockSubmit} />);
    
    // Fill required fields
    const clientSelect = screen.getByLabelText('Client');
    fireEvent.mouseDown(clientSelect);
    fireEvent.click(screen.getByText('John Doe (12345678901)'));
    
    const vehicleSelect = screen.getByLabelText('Vehicle');
    fireEvent.mouseDown(vehicleSelect);
    fireEvent.click(screen.getByText('Toyota Corolla (ABC123)'));
    
    const insuranceTypeSelect = screen.getByLabelText('Insurance Type');
    fireEvent.mouseDown(insuranceTypeSelect);
    fireEvent.click(screen.getByText('OC - Obligatory Civil Liability'));
    
    const discountInput = screen.getByLabelText('Discount/Surcharge');
    fireEvent.change(discountInput, { target: { value: '-100' } });
    
    const submitButton = screen.getByText('Create');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          discountSurcharge: -100,
        })
      );
    });
  });

  test('calls onClose when cancel button is clicked', () => {
    render(<PolicyForm {...mockProps} />);
    
    const cancelButton = screen.getByText('Cancel');
    fireEvent.click(cancelButton);
    
    expect(mockProps.onClose).toHaveBeenCalled();
  });

  test('disables form when loading', () => {
    render(<PolicyForm {...mockProps} loading={true} />);
    
    const submitButton = screen.getByText('Saving...');
    const cancelButton = screen.getByText('Cancel');
    
    expect(submitButton).toBeDisabled();
    expect(cancelButton).toBeDisabled();
  });

  test('validates discount/surcharge as number', async () => {
    render(<PolicyForm {...mockProps} />);
    
    const discountInput = screen.getByLabelText('Discount/Surcharge');
    fireEvent.change(discountInput, { target: { value: 'invalid' } });
    
    const submitButton = screen.getByText('Create');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText('Must be a valid number')).toBeInTheDocument();
    });
  });
});