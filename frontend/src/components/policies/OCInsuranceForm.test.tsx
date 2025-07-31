import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import OCInsuranceForm from './OCInsuranceForm';
import { PolicyDetails } from '../../types/policy';

describe('OCInsuranceForm', () => {
  const mockOnChange = jest.fn();
  const defaultPolicyDetails: PolicyDetails = {};

  beforeEach(() => {
    mockOnChange.mockClear();
  });

  test('renders OC insurance form with required fields', () => {
    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText('OC Insurance Details')).toBeInTheDocument();
    expect(screen.getByLabelText('Guaranteed Sum')).toBeInTheDocument();
    expect(screen.getByLabelText('Coverage Area')).toBeInTheDocument();
  });

  test('displays guaranteed sum value correctly', () => {
    const policyDetails: PolicyDetails = {
      guaranteedSum: 50000,
    };

    render(
      <OCInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const guaranteedSumInput = screen.getByLabelText('Guaranteed Sum') as HTMLInputElement;
    expect(guaranteedSumInput.value).toBe('50000');
  });

  test('calls onChange when guaranteed sum is updated', () => {
    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const guaranteedSumInput = screen.getByLabelText('Guaranteed Sum');
    fireEvent.change(guaranteedSumInput, { target: { value: '75000' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      guaranteedSum: 75000,
    });
  });

  test('displays coverage area value correctly', () => {
    const policyDetails: PolicyDetails = {
      coverageArea: 'EUROPE',
    };

    render(
      <OCInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByDisplayValue('Europe')).toBeInTheDocument();
  });

  test('calls onChange when coverage area is selected', () => {
    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const coverageAreaSelect = screen.getByLabelText('Coverage Area');
    fireEvent.mouseDown(coverageAreaSelect);
    
    const polandOption = screen.getByText('Poland');
    fireEvent.click(polandOption);

    expect(mockOnChange).toHaveBeenCalledWith({
      coverageArea: 'POLAND',
    });
  });

  test('displays validation errors', () => {
    const errors = {
      guaranteedSum: 'Guaranteed sum is required',
      coverageArea: 'Coverage area is required',
    };

    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
        errors={errors}
      />
    );

    expect(screen.getByText('Guaranteed sum is required')).toBeInTheDocument();
    expect(screen.getByText('Coverage area is required')).toBeInTheDocument();
  });

  test('shows coverage information', () => {
    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText(/OC Insurance Coverage/)).toBeInTheDocument();
    expect(screen.getByText(/property damage, personal injury/)).toBeInTheDocument();
  });

  test('handles empty guaranteed sum input', () => {
    render(
      <OCInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const guaranteedSumInput = screen.getByLabelText('Guaranteed Sum');
    fireEvent.change(guaranteedSumInput, { target: { value: '' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      guaranteedSum: undefined,
    });
  });
});