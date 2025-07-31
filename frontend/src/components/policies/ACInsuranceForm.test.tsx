import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ACInsuranceForm from './ACInsuranceForm';
import { PolicyDetails } from '../../types/policy';

describe('ACInsuranceForm', () => {
  const mockOnChange = jest.fn();
  const defaultPolicyDetails: PolicyDetails = {};

  beforeEach(() => {
    mockOnChange.mockClear();
  });

  test('renders AC insurance form with required fields', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText('AC Insurance Details')).toBeInTheDocument();
    expect(screen.getByLabelText('AC Variant')).toBeInTheDocument();
    expect(screen.getByLabelText('Sum Insured')).toBeInTheDocument();
    expect(screen.getByLabelText('Coverage Scope')).toBeInTheDocument();
  });

  test('displays AC variant value correctly', () => {
    const policyDetails: PolicyDetails = {
      acVariant: 'MAXIMUM',
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByDisplayValue('MAXIMUM')).toBeInTheDocument();
  });

  test('calls onChange when AC variant is selected', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const variantSelect = screen.getByLabelText('AC Variant');
    fireEvent.mouseDown(variantSelect);
    
    const standardOption = screen.getByText('Standard');
    fireEvent.click(standardOption);

    expect(mockOnChange).toHaveBeenCalledWith({
      acVariant: 'STANDARD',
    });
  });

  test('displays sum insured value correctly', () => {
    const policyDetails: PolicyDetails = {
      sumInsured: 100000,
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const sumInsuredInput = screen.getByLabelText('Sum Insured') as HTMLInputElement;
    expect(sumInsuredInput.value).toBe('100000');
  });

  test('calls onChange when sum insured is updated', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const sumInsuredInput = screen.getByLabelText('Sum Insured');
    fireEvent.change(sumInsuredInput, { target: { value: '150000' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      sumInsured: 150000,
    });
  });

  test('displays deductible value correctly', () => {
    const policyDetails: PolicyDetails = {
      deductible: 500,
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const deductibleInput = screen.getByLabelText('Deductible') as HTMLInputElement;
    expect(deductibleInput.value).toBe('500');
  });

  test('calls onChange when deductible is updated', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const deductibleInput = screen.getByLabelText('Deductible');
    fireEvent.change(deductibleInput, { target: { value: '1000' } });

    expect(mockOnChange).toHaveBeenCalledWith({
      deductible: 1000,
    });
  });

  test('displays workshop type value correctly', () => {
    const policyDetails: PolicyDetails = {
      workshopType: 'AUTHORIZED',
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByDisplayValue('AUTHORIZED')).toBeInTheDocument();
  });

  test('calls onChange when workshop type is selected', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const workshopSelect = screen.getByLabelText('Workshop Type');
    fireEvent.mouseDown(workshopSelect);
    
    const authorizedOption = screen.getByText('Authorized Workshop');
    fireEvent.click(authorizedOption);

    expect(mockOnChange).toHaveBeenCalledWith({
      workshopType: 'AUTHORIZED',
    });
  });

  test('displays coverage scope value correctly', () => {
    const policyDetails: PolicyDetails = {
      coverageScope: 'Comprehensive coverage including collision and theft',
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const coverageScopeInput = screen.getByLabelText('Coverage Scope') as HTMLTextAreaElement;
    expect(coverageScopeInput.value).toBe('Comprehensive coverage including collision and theft');
  });

  test('calls onChange when coverage scope is updated', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const coverageScopeInput = screen.getByLabelText('Coverage Scope');
    fireEvent.change(coverageScopeInput, { 
      target: { value: 'Full comprehensive coverage' } 
    });

    expect(mockOnChange).toHaveBeenCalledWith({
      coverageScope: 'Full comprehensive coverage',
    });
  });

  test('displays validation errors', () => {
    const errors = {
      acVariant: 'AC variant is required',
      sumInsured: 'Sum insured is required',
      coverageScope: 'Coverage scope is required',
    };

    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
        errors={errors}
      />
    );

    expect(screen.getByText('AC variant is required')).toBeInTheDocument();
    expect(screen.getByText('Sum insured is required')).toBeInTheDocument();
    expect(screen.getByText('Coverage scope is required')).toBeInTheDocument();
  });

  test('shows coverage information', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    expect(screen.getByText(/AC Insurance Coverage/)).toBeInTheDocument();
    expect(screen.getByText(/collision, theft, vandalism/)).toBeInTheDocument();
  });

  test('adds coverage scope option when chip is clicked', () => {
    render(
      <ACInsuranceForm
        policyDetails={defaultPolicyDetails}
        onChange={mockOnChange}
      />
    );

    const theftChip = screen.getByText('Theft and Vandalism');
    fireEvent.click(theftChip);

    expect(mockOnChange).toHaveBeenCalledWith({
      coverageScope: 'Theft and Vandalism',
    });
  });

  test('appends coverage scope option when chip is clicked with existing scope', () => {
    const policyDetails: PolicyDetails = {
      coverageScope: 'Collision and Comprehensive',
    };

    render(
      <ACInsuranceForm
        policyDetails={policyDetails}
        onChange={mockOnChange}
      />
    );

    const glassChip = screen.getByText('Glass Coverage');
    fireEvent.click(glassChip);

    expect(mockOnChange).toHaveBeenCalledWith({
      coverageScope: 'Collision and Comprehensive, Glass Coverage',
    });
  });
});