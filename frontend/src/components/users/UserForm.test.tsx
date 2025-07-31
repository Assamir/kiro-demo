import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import theme from '../../theme/theme';
import UserForm from './UserForm';
import { User } from '../../types/auth';

const mockUser: User = {
  id: 1,
  firstName: 'John',
  lastName: 'Doe',
  email: 'john.doe@example.com',
  role: 'ADMIN',
};

const renderWithTheme = (component: React.ReactElement) => {
  return render(
    <ThemeProvider theme={theme}>
      {component}
    </ThemeProvider>
  );
};

describe('UserForm', () => {
  const mockOnClose = jest.fn();
  const mockOnSubmit = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockOnSubmit.mockResolvedValue(undefined);
  });

  describe('Create User Mode', () => {
    it('renders create user form with all fields', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      expect(screen.getByText('Create New User')).toBeInTheDocument();
      expect(screen.getByLabelText('First Name')).toBeInTheDocument();
      expect(screen.getByLabelText('Last Name')).toBeInTheDocument();
      expect(screen.getByLabelText('Email')).toBeInTheDocument();
      expect(screen.getByLabelText('Password')).toBeInTheDocument();
      expect(screen.getByLabelText('Role')).toBeInTheDocument();
      expect(screen.getByText('Create')).toBeInTheDocument();
    });

    it('validates required fields', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('First name is required')).toBeInTheDocument();
        expect(screen.getByText('Last name is required')).toBeInTheDocument();
        expect(screen.getByText('Email is required')).toBeInTheDocument();
        expect(screen.getByText('Password is required')).toBeInTheDocument();
      });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('validates email format', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const emailInput = screen.getByRole('textbox', { name: /email/i });
      fireEvent.change(emailInput, { target: { value: 'invalid-email' } });

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Please enter a valid email address')).toBeInTheDocument();
      });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('validates password requirements', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const passwordInput = screen.getByLabelText(/password/i);
      fireEvent.change(passwordInput, { target: { value: 'weak' } });

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Password must be at least 8 characters')).toBeInTheDocument();
      });

      // Test complex password requirement
      fireEvent.change(passwordInput, { target: { value: 'weakpassword' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Password must contain at least one uppercase letter, one lowercase letter, and one number')).toBeInTheDocument();
      });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('validates minimum name lengths', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const firstNameInput = screen.getByRole('textbox', { name: /first name/i });
      const lastNameInput = screen.getByRole('textbox', { name: /last name/i });
      
      fireEvent.change(firstNameInput, { target: { value: 'A' } });
      fireEvent.change(lastNameInput, { target: { value: 'B' } });

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('First name must be at least 2 characters')).toBeInTheDocument();
        expect(screen.getByText('Last name must be at least 2 characters')).toBeInTheDocument();
      });

      expect(mockOnSubmit).not.toHaveBeenCalled();
    });

    it('submits valid create user form', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      // Fill in valid form data
      fireEvent.change(screen.getByRole('textbox', { name: /first name/i }), { target: { value: 'John' } });
      fireEvent.change(screen.getByRole('textbox', { name: /last name/i }), { target: { value: 'Doe' } });
      fireEvent.change(screen.getByRole('textbox', { name: /email/i }), { target: { value: 'john.doe@example.com' } });
      fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'SecurePass123' } });
      
      // Select role
      const roleSelect = screen.getByRole('combobox', { name: /role/i });
      fireEvent.mouseDown(roleSelect);
      fireEvent.click(screen.getByText('Admin'));

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalledWith({
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          password: 'SecurePass123',
          role: 'ADMIN',
        });
      });

      expect(mockOnClose).toHaveBeenCalled();
    });

    it('toggles password visibility', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const passwordInput = screen.getByLabelText(/password/i) as HTMLInputElement;
      const toggleButton = screen.getByLabelText('toggle password visibility');

      expect(passwordInput.type).toBe('password');

      fireEvent.click(toggleButton);
      expect(passwordInput.type).toBe('text');

      fireEvent.click(toggleButton);
      expect(passwordInput.type).toBe('password');
    });
  });

  describe('Edit User Mode', () => {
    it('renders edit user form with populated data', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
          user={mockUser}
        />
      );

      expect(screen.getByText('Edit User')).toBeInTheDocument();
      expect(screen.getByDisplayValue('John')).toBeInTheDocument();
      expect(screen.getByDisplayValue('Doe')).toBeInTheDocument();
      expect(screen.getByDisplayValue('john.doe@example.com')).toBeInTheDocument();
      expect(screen.queryByLabelText('Password')).not.toBeInTheDocument(); // Password field should not be shown
      expect(screen.getByText('Update')).toBeInTheDocument();
    });

    it('submits valid edit user form', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
          user={mockUser}
        />
      );

      // Modify form data
      fireEvent.change(screen.getByDisplayValue('John'), { target: { value: 'Jane' } });
      fireEvent.change(screen.getByDisplayValue('Doe'), { target: { value: 'Smith' } });
      
      const submitButton = screen.getByText('Update');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalledWith({
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'john.doe@example.com',
          role: 'ADMIN',
        });
      });

      expect(mockOnClose).toHaveBeenCalled();
    });

    it('does not validate password in edit mode', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
          user={mockUser}
        />
      );

      const submitButton = screen.getByText('Update');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalled();
      });

      // Should not show password validation errors
      expect(screen.queryByText('Password is required')).not.toBeInTheDocument();
    });
  });

  describe('Form Behavior', () => {
    it('clears errors when user starts typing', async () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      // Trigger validation error
      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('First name is required')).toBeInTheDocument();
      });

      // Start typing in first name field
      const firstNameInput = screen.getByRole('textbox', { name: /first name/i });
      fireEvent.change(firstNameInput, { target: { value: 'J' } });

      // Error should be cleared
      expect(screen.queryByText('First name is required')).not.toBeInTheDocument();
    });

    it('resets form when user prop changes', () => {
      const { rerender } = renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      // Fill in some data
      const firstNameInput = screen.getByRole('textbox', { name: /first name/i });
      fireEvent.change(firstNameInput, { target: { value: 'Test' } });

      // Switch to edit mode
      rerender(
        <ThemeProvider theme={theme}>
          <UserForm
            open={true}
            onClose={mockOnClose}
            onSubmit={mockOnSubmit}
            user={mockUser}
          />
        </ThemeProvider>
      );

      // Form should be populated with user data
      expect(screen.getByDisplayValue('John')).toBeInTheDocument();
    });

    it('handles submit error', async () => {
      const errorMessage = 'Email already exists';
      mockOnSubmit.mockRejectedValue({
        response: { data: { message: errorMessage } }
      });

      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      // Fill in valid form data
      fireEvent.change(screen.getByRole('textbox', { name: /first name/i }), { target: { value: 'John' } });
      fireEvent.change(screen.getByRole('textbox', { name: /last name/i }), { target: { value: 'Doe' } });
      fireEvent.change(screen.getByRole('textbox', { name: /email/i }), { target: { value: 'john.doe@example.com' } });
      fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'SecurePass123' } });

      const submitButton = screen.getByText('Create');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(errorMessage)).toBeInTheDocument();
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });

    it('disables form when loading', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
          loading={true}
        />
      );

      expect(screen.getByText('Saving...')).toBeInTheDocument();
      expect(screen.getByText('Cancel')).toBeDisabled();
      expect(screen.getByText('Saving...')).toBeDisabled();
    });

    it('calls onClose when cancel button is clicked', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
        />
      );

      const cancelButton = screen.getByText('Cancel');
      fireEvent.click(cancelButton);

      expect(mockOnClose).toHaveBeenCalled();
    });

    it('does not close when loading', () => {
      renderWithTheme(
        <UserForm
          open={true}
          onClose={mockOnClose}
          onSubmit={mockOnSubmit}
          loading={true}
        />
      );

      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toBeDisabled();
    });
  });
});