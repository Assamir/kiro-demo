import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EnhancedTextField from './EnhancedTextField';

describe('EnhancedTextField', () => {
  it('should render basic text field', () => {
    render(<EnhancedTextField label="Test Field" />);
    
    expect(screen.getByLabelText('Test Field')).toBeInTheDocument();
  });

  it('should show validation icon for success state', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="success"
        validationMessage="Valid input"
      />
    );
    
    expect(screen.getByTestId('CheckCircleIcon')).toBeInTheDocument();
  });

  it('should show validation icon for error state', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="error"
        validationMessage="Invalid input"
      />
    );
    
    expect(screen.getByTestId('ErrorIcon')).toBeInTheDocument();
  });

  it('should show validation icon for warning state', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="warning"
        validationMessage="Warning message"
      />
    );
    
    expect(screen.getByTestId('WarningIcon')).toBeInTheDocument();
  });

  it('should show validation icon for info state', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="info"
        validationMessage="Info message"
      />
    );
    
    expect(screen.getByTestId('InfoIcon')).toBeInTheDocument();
  });

  it('should hide validation icon when showValidationIcon is false', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="success"
        showValidationIcon={false}
      />
    );
    
    expect(screen.queryByTestId('CheckCircleIcon')).not.toBeInTheDocument();
  });

  it('should render as password field with toggle', () => {
    render(<EnhancedTextField label="Password" isPassword />);
    
    const input = screen.getByLabelText('Password') as HTMLInputElement;
    const toggleButton = screen.getByLabelText('toggle password visibility');
    
    expect(input.type).toBe('password');
    expect(toggleButton).toBeInTheDocument();
  });

  it('should toggle password visibility', () => {
    render(<EnhancedTextField label="Password" isPassword />);
    
    const input = screen.getByLabelText('Password') as HTMLInputElement;
    const toggleButton = screen.getByLabelText('toggle password visibility');
    
    expect(input.type).toBe('password');
    
    fireEvent.click(toggleButton);
    
    expect(input.type).toBe('text');
    
    fireEvent.click(toggleButton);
    
    expect(input.type).toBe('password');
  });

  it('should hide password toggle when showPasswordToggle is false', () => {
    render(
      <EnhancedTextField
        label="Password"
        isPassword
        showPasswordToggle={false}
      />
    );
    
    expect(screen.queryByLabelText('toggle password visibility')).not.toBeInTheDocument();
  });

  it('should validate on change when validateOnChange is true', async () => {
    const mockValidate = jest.fn().mockReturnValue({
      isValid: false,
      message: 'Invalid input',
      severity: 'error',
    });

    render(
      <EnhancedTextField
        label="Test Field"
        onValidate={mockValidate}
        validateOnChange={true}
      />
    );
    
    const input = screen.getByLabelText('Test Field');
    
    fireEvent.change(input, { target: { value: 'test' } });
    
    await waitFor(() => {
      expect(mockValidate).toHaveBeenCalledWith('test');
      expect(screen.getByText('Invalid input')).toBeInTheDocument();
    });
  });

  it('should validate on blur when validateOnBlur is true', async () => {
    const mockValidate = jest.fn().mockReturnValue({
      isValid: false,
      message: 'Invalid input',
      severity: 'error',
    });

    render(
      <EnhancedTextField
        label="Test Field"
        onValidate={mockValidate}
        validateOnBlur={true}
      />
    );
    
    const input = screen.getByLabelText('Test Field');
    
    fireEvent.change(input, { target: { value: 'test' } });
    fireEvent.blur(input);
    
    await waitFor(() => {
      expect(mockValidate).toHaveBeenCalledWith('test');
      expect(screen.getByText('Invalid input')).toBeInTheDocument();
    });
  });

  it('should show success state after valid input', async () => {
    const mockValidate = jest.fn().mockReturnValue({
      isValid: true,
    });

    render(
      <EnhancedTextField
        label="Test Field"
        onValidate={mockValidate}
        validateOnChange={true}
      />
    );
    
    const input = screen.getByLabelText('Test Field');
    
    fireEvent.change(input, { target: { value: 'valid' } });
    
    await waitFor(() => {
      expect(mockValidate).toHaveBeenCalledWith('valid');
      expect(screen.getByTestId('CheckCircleIcon')).toBeInTheDocument();
    });
  });

  it('should display help text', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        helpText="This is help text"
        showHelpIcon={true}
      />
    );
    
    expect(screen.getByText('This is help text')).toBeInTheDocument();
    expect(screen.getByTestId('InfoIcon')).toBeInTheDocument();
  });

  it('should call original onChange and onBlur handlers', () => {
    const mockOnChange = jest.fn();
    const mockOnBlur = jest.fn();

    render(
      <EnhancedTextField
        label="Test Field"
        onChange={mockOnChange}
        onBlur={mockOnBlur}
      />
    );
    
    const input = screen.getByLabelText('Test Field');
    
    fireEvent.change(input, { target: { value: 'test' } });
    fireEvent.blur(input);
    
    expect(mockOnChange).toHaveBeenCalled();
    expect(mockOnBlur).toHaveBeenCalled();
  });

  it('should be disabled when loading is true', () => {
    render(<EnhancedTextField label="Test Field" loading={true} />);
    
    const input = screen.getByLabelText('Test Field');
    expect(input).toBeDisabled();
  });

  it('should preserve original InputProps', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        InputProps={{
          startAdornment: <span data-testid="start-adornment">Start</span>,
          endAdornment: <span data-testid="end-adornment">End</span>,
        }}
      />
    );
    
    expect(screen.getByTestId('start-adornment')).toBeInTheDocument();
    expect(screen.getByTestId('end-adornment')).toBeInTheDocument();
  });

  it('should show validation message as helper text', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        validationState="error"
        validationMessage="This field is required"
      />
    );
    
    expect(screen.getByText('This field is required')).toBeInTheDocument();
  });

  it('should prioritize validation message over regular helper text', () => {
    render(
      <EnhancedTextField
        label="Test Field"
        helperText="Regular helper text"
        validationState="error"
        validationMessage="Validation error"
      />
    );
    
    expect(screen.getByText('Validation error')).toBeInTheDocument();
    expect(screen.queryByText('Regular helper text')).not.toBeInTheDocument();
  });
});