import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import theme from '../../theme/theme';
import DeleteUserDialog from './DeleteUserDialog';
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

describe('DeleteUserDialog', () => {
  const mockOnClose = jest.fn();
  const mockOnConfirm = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockOnConfirm.mockResolvedValue(undefined);
  });

  it('renders delete confirmation dialog with user details', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    expect(screen.getByText('Delete User')).toBeInTheDocument();
    expect(screen.getByText('This action cannot be undone.')).toBeInTheDocument();
    expect(screen.getByText('Are you sure you want to delete the following user?')).toBeInTheDocument();
    
    // Check user details are displayed
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
    expect(screen.getByText('ADMIN')).toBeInTheDocument();
    
    expect(screen.getByText('Cancel')).toBeInTheDocument();
    expect(screen.getByText('Delete')).toBeInTheDocument();
  });

  it('does not render when user is null', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={null}
      />
    );

    expect(screen.queryByText('Delete User')).not.toBeInTheDocument();
  });

  it('calls onClose when cancel button is clicked', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    const cancelButton = screen.getByText('Cancel');
    fireEvent.click(cancelButton);

    expect(mockOnClose).toHaveBeenCalled();
    expect(mockOnConfirm).not.toHaveBeenCalled();
  });

  it('calls onConfirm and onClose when delete button is clicked', async () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    const deleteButton = screen.getByText('Delete');
    fireEvent.click(deleteButton);

    await waitFor(() => {
      expect(mockOnConfirm).toHaveBeenCalled();
      expect(mockOnClose).toHaveBeenCalled();
    });
  });

  it('handles confirm error gracefully', async () => {
    mockOnConfirm.mockRejectedValue(new Error('Delete failed'));

    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    const deleteButton = screen.getByText('Delete');
    fireEvent.click(deleteButton);

    await waitFor(() => {
      expect(mockOnConfirm).toHaveBeenCalled();
    });

    // Dialog should not close on error
    expect(mockOnClose).not.toHaveBeenCalled();
  });

  it('shows loading state when loading prop is true', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
        loading={true}
      />
    );

    expect(screen.getByText('Deleting...')).toBeInTheDocument();
    expect(screen.getByText('Cancel')).toBeDisabled();
    expect(screen.getByText('Deleting...')).toBeDisabled();
  });

  it('disables close when loading', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
        loading={true}
      />
    );

    const cancelButton = screen.getByText('Cancel');
    expect(cancelButton).toBeDisabled();

    // Try to close - should not call onClose
    fireEvent.click(cancelButton);
    expect(mockOnClose).not.toHaveBeenCalled();
  });

  it('displays user information in structured format', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    // Check that labels are present
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Role')).toBeInTheDocument();

    // Check that values are present
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
    expect(screen.getByText('ADMIN')).toBeInTheDocument();
  });

  it('displays warning icon and styling', () => {
    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    // Check for warning alert
    const warningAlert = screen.getByRole('alert');
    expect(warningAlert).toBeInTheDocument();
    expect(warningAlert).toHaveTextContent('This action cannot be undone.');
  });

  it('renders with different user roles correctly', () => {
    const operatorUser: User = {
      ...mockUser,
      role: 'OPERATOR',
    };

    renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={operatorUser}
      />
    );

    expect(screen.getByText('OPERATOR')).toBeInTheDocument();
    expect(screen.queryByText('ADMIN')).not.toBeInTheDocument();
  });

  it('handles dialog close via backdrop click when not loading', () => {
    const { container } = renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
      />
    );

    // Find the backdrop and click it
    const backdrop = container.querySelector('.MuiBackdrop-root');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(mockOnClose).toHaveBeenCalled();
    }
  });

  it('prevents dialog close via backdrop click when loading', () => {
    const { container } = renderWithTheme(
      <DeleteUserDialog
        open={true}
        onClose={mockOnClose}
        onConfirm={mockOnConfirm}
        user={mockUser}
        loading={true}
      />
    );

    // Find the backdrop and click it
    const backdrop = container.querySelector('.MuiBackdrop-root');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(mockOnClose).not.toHaveBeenCalled();
    }
  });
});