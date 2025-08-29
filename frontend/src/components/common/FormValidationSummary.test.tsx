import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import FormValidationSummary from './FormValidationSummary';

describe('FormValidationSummary', () => {
  const mockErrors = {
    firstName: 'First name is required',
    email: 'Please enter a valid email address',
  };

  const mockWarnings = {
    password: 'Password could be stronger',
  };

  const mockInfos = {
    username: 'Username is available',
  };

  const mockTouched = {
    firstName: true,
    email: true,
    password: true,
    username: true,
  };

  it('should not render when there are no issues', () => {
    const { container } = render(
      <FormValidationSummary
        errors={{}}
        touched={{}}
      />
    );

    expect(container.firstChild).toBeNull();
  });

  it('should render errors with proper severity', () => {
    render(
      <FormValidationSummary
        errors={mockErrors}
        touched={mockTouched}
      />
    );

    expect(screen.getByText(/Form Validation - 2 issues found/)).toBeInTheDocument();
    expect(screen.getByText('First name is required')).toBeInTheDocument();
    expect(screen.getByText('Please enter a valid email address')).toBeInTheDocument();
  });

  it('should render warnings and infos', () => {
    render(
      <FormValidationSummary
        errors={{}}
        warnings={mockWarnings}
        infos={mockInfos}
        touched={mockTouched}
      />
    );

    expect(screen.getByText('Password could be stronger')).toBeInTheDocument();
    expect(screen.getByText('Username is available')).toBeInTheDocument();
  });

  it('should show only touched fields when showOnlyTouched is true', () => {
    const partialTouched = {
      firstName: true,
      email: false,
    };

    render(
      <FormValidationSummary
        errors={mockErrors}
        touched={partialTouched}
        showOnlyTouched={true}
      />
    );

    expect(screen.getByText('First name is required')).toBeInTheDocument();
    expect(screen.queryByText('Please enter a valid email address')).not.toBeInTheDocument();
  });

  it('should show all fields when showOnlyTouched is false', () => {
    const partialTouched = {
      firstName: true,
      email: false,
    };

    render(
      <FormValidationSummary
        errors={mockErrors}
        touched={partialTouched}
        showOnlyTouched={false}
      />
    );

    expect(screen.getByText('First name is required')).toBeInTheDocument();
    expect(screen.getByText('Please enter a valid email address')).toBeInTheDocument();
  });

  it('should be collapsible when collapsible prop is true', () => {
    render(
      <FormValidationSummary
        errors={mockErrors}
        touched={mockTouched}
        collapsible={true}
      />
    );

    const toggleButton = screen.getByLabelText('toggle validation details');
    expect(toggleButton).toBeInTheDocument();

    // Initially expanded
    expect(screen.getByText('First name is required')).toBeInTheDocument();

    // Click to collapse - check that the collapse container is hidden
    fireEvent.click(toggleButton);
    const collapseContainer = screen.getByText('First name is required').closest('.MuiCollapse-root');
    expect(collapseContainer).toHaveClass('MuiCollapse-hidden');

    // Click to expand again
    fireEvent.click(toggleButton);
    expect(collapseContainer).toHaveClass('MuiCollapse-entered');
  });

  it('should use custom title when provided', () => {
    render(
      <FormValidationSummary
        errors={mockErrors}
        touched={mockTouched}
        title="Custom Validation Title"
      />
    );

    expect(screen.getByText(/Custom Validation Title - 2 issues found/)).toBeInTheDocument();
  });

  it('should group issues by severity', () => {
    render(
      <FormValidationSummary
        errors={mockErrors}
        warnings={mockWarnings}
        infos={mockInfos}
        touched={mockTouched}
      />
    );

    expect(screen.getByText('Errors (2)')).toBeInTheDocument();
    expect(screen.getByText('Warnings (1)')).toBeInTheDocument();
    expect(screen.getByText('Information (1)')).toBeInTheDocument();
  });

  it('should format field names correctly', () => {
    const camelCaseErrors = {
      firstName: 'First name error',
      phoneNumber: 'Phone number error',
      dateOfBirth: 'Date of birth error',
    };

    const camelCaseTouched = {
      firstName: true,
      phoneNumber: true,
      dateOfBirth: true,
    };

    render(
      <FormValidationSummary
        errors={camelCaseErrors}
        touched={camelCaseTouched}
      />
    );

    // Check that the error messages are displayed
    expect(screen.getByText('First name error')).toBeInTheDocument();
    expect(screen.getByText('Phone number error')).toBeInTheDocument();
    expect(screen.getByText('Date of birth error')).toBeInTheDocument();
  });

  it('should determine overall severity correctly', () => {
    // Test error severity (highest priority)
    const { rerender } = render(
      <FormValidationSummary
        errors={mockErrors}
        warnings={mockWarnings}
        infos={mockInfos}
        touched={mockTouched}
      />
    );

    let alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-standardError');

    // Test warning severity (when no errors)
    rerender(
      <FormValidationSummary
        errors={{}}
        warnings={mockWarnings}
        infos={mockInfos}
        touched={mockTouched}
      />
    );

    alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-standardWarning');

    // Test info severity (when no errors or warnings)
    rerender(
      <FormValidationSummary
        errors={{}}
        warnings={{}}
        infos={mockInfos}
        touched={mockTouched}
      />
    );

    alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-standardInfo');
  });
});