import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import NNWInsuranceForm from './NNWInsuranceForm';
import { PolicyDetails } from '../../types/policy';

describe('NNWInsuranceForm', () => {
  const mockOnChange = jest.fn();
  const defaultPolicyDetails: PolicyDetails = {};

  beforeEach(() => {
    mockOnChange.mockClear();
  });

  test('renders NNW insurance form with required fields', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText('NNW Insurance Details')).toBeInTheDocument();
    expect(screen.getByLabelText('Sum Insured')).toBeInTheDocument();
    expect(screen.getByLabelText('Covered Persons')).toBeInTheDocument();
  });

  test('displays sum insured value correctly', () => {
    const policyDetails: PolicyDetails = {
      sumInsured: 25000,
    };

    render(
      <NNWInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const sumInsuredInput = screen.getByLabelText('Sum Insured') as HTMLInputElement;
    expect(sumInsuredInput.value).toBe('25000');
  });

  test('calls onChange when sum insured is updated', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const sumInsuredInput = screen.getByLabelText('Sum Insured');
    fireEvent.change(sumInsuredInput, { target: { value: '30000' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      sumInsured: 30000,
    });
  });

  test('displays covered persons value correctly', () => {
    const policyDetails: PolicyDetails = {
      coveredPersons: 'Driver and all passengers',
    };

    render(
      <NNWInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const coveredPersonsInput = screen.getByLabelText('Covered Persons') as HTMLTextAreaElement;
    expect(coveredPersonsInput.value).toBe('Driver and all passengers');
  });

  test('calls onChange when covered persons is updated', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const coveredPersonsInput = screen.getByLabelText('Covered Persons');
    fireEvent.change(coveredPersonsInput, { 
      target: { value: 'Driver only' } 
    });

    expect(mockOnChange).toHaveBeenCalledWith({
      coveredPersons: 'Driver only',
    });
  });

  test('displays validation errors', () => {
    const errors = {
      sumInsured: 'Sum insured is required',
      coveredPersons: 'Covered persons specification is required',
    };

    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
        errors={errors}
      />
    );

    expect(screen.getByText('Sum insured is required')).toBeInTheDocument();
    expect(screen.getByText('Covered persons specification is required')).toBeInTheDocument();
  });

  test('shows coverage distribution information', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText('Coverage Distribution')).toBeInTheDocument();
    expect(screen.getByText(/Death: 50% of sum insured/)).toBeInTheDocument();
    expect(screen.getByText(/Permanent damage: Up to 100%/)).toBeInTheDocument();
    expect(screen.getByText(/Medical costs: Up to 20%/)).toBeInTheDocument();
  });

  test('shows coverage information', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText(/NNW Insurance Coverage/)).toBeInTheDocument();
    expect(screen.getByText(/personal accident coverage/)).toBeInTheDocument();
  });

  test('adds covered persons option when chip is clicked', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const driverOnlyChip = screen.getByText('Driver only');
    fireEvent.click(driverOnlyChip);

    expect(mockOnChange).toHaveBeenCalledWith({
      coveredPersons: 'Driver only',
    });
  });

  test('appends covered persons option when chip is clicked with existing persons', () => {
    const policyDetails: PolicyDetails = {
      coveredPersons: 'Driver only',
    };

    render(
      <NNWInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const familyChip = screen.getByText('Family members');
    fireEvent.click(familyChip);

    expect(mockOnChange).toHaveBeenCalledWith({
      coveredPersons: 'Driver only, Family members',
    });
  });

  test('handles empty sum insured input', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const sumInsuredInput = screen.getByLabelText('Sum Insured');
    fireEvent.change(sumInsuredInput, { target: { value: '' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      sumInsured: undefined,
    });
  });

  test('shows important notice about NNW coverage', () => {
    render(
      <NNWInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText(/Important:/)).toBeInTheDocument();
    expect(screen.getByText(/NNW insurance covers accidents that occur while the vehicle is in use/)).toBeInTheDocument();
  });
});