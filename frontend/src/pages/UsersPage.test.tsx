import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import theme from '../theme/theme';
import UsersPage from './UsersPage';
import { userService } from '../services/userService';
import { User } from '../types/auth';

// Mock the userService
jest.mock('../services/userService');
const mockedUserService = userService as jest.Mocked<typeof userService>;

// Mock the child components
jest.mock('../components/users/UserList', () => {
  return function MockUserList({ users, onEditUser, onDeleteUser }: any) {
    return (
      <div data-testid="user-list">
        <div>User List - {users.length} users</div>
        <button onClick={() => onEditUser(users[0])}>Edit First User</button>
        <button onClick={() => onDeleteUser(users[0])}>Delete First User</button>
      </div>
    );
  };
});

jest.mock('../components/users/UserForm', () => {
  return function MockUserForm({ open, onClose, onSubmit, user }: any) {
    if (!open) return null;
    return (
      <div data-testid="user-form">
        <div>{user ? 'Edit User Form' : 'Create User Form'}</div>
        <button onClick={() => onSubmit({ firstName: 'Test', lastName: 'User', email: 'test@example.com', role: 'OPERATOR' })}>
          Submit Form
        </button>
        <button onClick={onClose}>Close Form</button>
      </div>
    );
  };
});

jest.mock('../components/users/DeleteUserDialog', () => {
  return function MockDeleteUserDialog({ open, onClose, onConfirm, user }: any) {
    if (!open) return null;
    return (
      <div data-testid="delete-dialog">
        <div>Delete {user?.firstName} {user?.lastName}</div>
        <button onClick={onConfirm}>Confirm Delete</button>
        <button onClick={onClose}>Cancel Delete</button>
      </div>
    );
  };
});

const mockUsers: User[] = [
  {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    role: 'ADMIN',
  },
  {
    id: 2,
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane.smith@example.com',
    role: 'OPERATOR',
  },
];

const renderWithTheme = (component: React.ReactElement) => {
  return render(
    <ThemeProvider theme={theme}>
      {component}
    </ThemeProvider>
  );
};

describe('UsersPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedUserService.getAllUsers.mockResolvedValue(mockUsers);
  });

  it('renders page header and controls', async () => {
    renderWithTheme(<UsersPage />);

    expect(screen.getByText('User Management')).toBeInTheDocument();
    expect(screen.getByText('Manage system users and their permissions.')).toBeInTheDocument();
    expect(screen.getByText('Add User')).toBeInTheDocument();
    expect(screen.getByText('Refresh')).toBeInTheDocument();
  });

  it('loads users on component mount', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(mockedUserService.getAllUsers).toHaveBeenCalled();
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
      expect(screen.getByText('User List - 2 users')).toBeInTheDocument();
    });
  });

  it('handles loading error', async () => {
    const errorMessage = 'Failed to load users';
    mockedUserService.getAllUsers.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  it('opens create user form when Add User button is clicked', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    const addButton = screen.getByText('Add User');
    fireEvent.click(addButton);

    expect(screen.getByTestId('user-form')).toBeInTheDocument();
    expect(screen.getByText('Create User Form')).toBeInTheDocument();
  });

  it('opens edit user form when edit is triggered', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    const editButton = screen.getByText('Edit First User');
    fireEvent.click(editButton);

    expect(screen.getByTestId('user-form')).toBeInTheDocument();
    expect(screen.getByText('Edit User Form')).toBeInTheDocument();
  });

  it('opens delete dialog when delete is triggered', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    const deleteButton = screen.getByText('Delete First User');
    fireEvent.click(deleteButton);

    expect(screen.getByTestId('delete-dialog')).toBeInTheDocument();
    expect(screen.getByText('Delete John Doe')).toBeInTheDocument();
  });

  it('creates new user successfully', async () => {
    const newUser: User = {
      id: 3,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      role: 'OPERATOR',
    };

    mockedUserService.createUser.mockResolvedValue(newUser);

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open create form
    const addButton = screen.getByText('Add User');
    fireEvent.click(addButton);

    // Submit form
    const submitButton = screen.getByText('Submit Form');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockedUserService.createUser).toHaveBeenCalledWith({
        firstName: 'Test',
        lastName: 'User',
        email: 'test@example.com',
        role: 'OPERATOR',
      });
      expect(screen.getByText('User created successfully')).toBeInTheDocument();
    });
  });

  it('updates existing user successfully', async () => {
    const updatedUser: User = {
      ...mockUsers[0],
      firstName: 'Updated',
    };

    mockedUserService.updateUser.mockResolvedValue(updatedUser);

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open edit form
    const editButton = screen.getByText('Edit First User');
    fireEvent.click(editButton);

    // Submit form
    const submitButton = screen.getByText('Submit Form');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockedUserService.updateUser).toHaveBeenCalledWith(1, {
        firstName: 'Test',
        lastName: 'User',
        email: 'test@example.com',
        role: 'OPERATOR',
      });
      expect(screen.getByText('User updated successfully')).toBeInTheDocument();
    });
  });

  it('deletes user successfully', async () => {
    mockedUserService.deleteUser.mockResolvedValue();

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open delete dialog
    const deleteButton = screen.getByText('Delete First User');
    fireEvent.click(deleteButton);

    // Confirm delete
    const confirmButton = screen.getByText('Confirm Delete');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(mockedUserService.deleteUser).toHaveBeenCalledWith(1);
      expect(screen.getByText('User deleted successfully')).toBeInTheDocument();
    });
  });

  it('handles delete error', async () => {
    const errorMessage = 'Failed to delete user';
    mockedUserService.deleteUser.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open delete dialog
    const deleteButton = screen.getByText('Delete First User');
    fireEvent.click(deleteButton);

    // Confirm delete
    const confirmButton = screen.getByText('Confirm Delete');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  it('refreshes user list when refresh button is clicked', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(mockedUserService.getAllUsers).toHaveBeenCalledTimes(1);
    });

    const refreshButton = screen.getByText('Refresh');
    fireEvent.click(refreshButton);

    await waitFor(() => {
      expect(mockedUserService.getAllUsers).toHaveBeenCalledTimes(2);
    });
  });

  it('closes form when close button is clicked', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open form
    const addButton = screen.getByText('Add User');
    fireEvent.click(addButton);

    expect(screen.getByTestId('user-form')).toBeInTheDocument();

    // Close form
    const closeButton = screen.getByText('Close Form');
    fireEvent.click(closeButton);

    expect(screen.queryByTestId('user-form')).not.toBeInTheDocument();
  });

  it('closes delete dialog when cancel is clicked', async () => {
    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Open delete dialog
    const deleteButton = screen.getByText('Delete First User');
    fireEvent.click(deleteButton);

    expect(screen.getByTestId('delete-dialog')).toBeInTheDocument();

    // Cancel delete
    const cancelButton = screen.getByText('Cancel Delete');
    fireEvent.click(cancelButton);

    expect(screen.queryByTestId('delete-dialog')).not.toBeInTheDocument();
  });

  it('closes success message', async () => {
    const newUser: User = {
      id: 3,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
      role: 'OPERATOR',
    };

    mockedUserService.createUser.mockResolvedValue(newUser);

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByTestId('user-list')).toBeInTheDocument();
    });

    // Create user to show success message
    const addButton = screen.getByText('Add User');
    fireEvent.click(addButton);

    const submitButton = screen.getByText('Submit Form');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('User created successfully')).toBeInTheDocument();
    });

    // Close success message
    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);

    await waitFor(() => {
      expect(screen.queryByText('User created successfully')).not.toBeInTheDocument();
    });
  });

  it('closes error message', async () => {
    const errorMessage = 'Failed to load users';
    mockedUserService.getAllUsers.mockRejectedValue({
      response: { data: { message: errorMessage } }
    });

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });

    // Close error message
    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);

    expect(screen.queryByText(errorMessage)).not.toBeInTheDocument();
  });

  it('handles generic error messages', async () => {
    mockedUserService.getAllUsers.mockRejectedValue(new Error('Network error'));

    renderWithTheme(<UsersPage />);

    await waitFor(() => {
      expect(screen.getByText('Failed to load users')).toBeInTheDocument();
    });
  });
});