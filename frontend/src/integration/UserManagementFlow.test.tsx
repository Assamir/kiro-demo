import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { AuthProvider } from '../contexts/AuthContext';
import { NotificationProvider } from '../contexts/NotificationContext';
import UsersPage from '../pages/UsersPage';
import theme from '../theme/theme';

// Mock services
jest.mock('../services/userService', () => ({
  userService: {
    getAllUsers: jest.fn(),
    createUser: jest.fn(),
    updateUser: jest.fn(),
    deleteUser: jest.fn(),
    getUserById: jest.fn(),
  },
}));

jest.mock('../services/authService', () => ({
  authService: {
    getCurrentUser: jest.fn(),
    login: jest.fn(),
    logout: jest.fn(),
  },
}));

import { userService } from '../services/userService';
import { authService } from '../services/authService';

const renderUsersPage = () => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <NotificationProvider>
          <AuthProvider>
            <UsersPage />
          </AuthProvider>
        </NotificationProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('User Management Flow Integration', () => {
  const mockUsers = [
    {
      id: 1,
      firstName: 'John',
      lastName: 'Admin',
      email: 'john.admin@test.com',
      role: 'ADMIN',
    },
    {
      id: 2,
      firstName: 'Jane',
      lastName: 'Operator',
      email: 'jane.operator@test.com',
      role: 'OPERATOR',
    },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock current user as admin
    mockAuthService.getCurrentUser.mockResolvedValue({
      id: 1,
      firstName: 'John',
      lastName: 'Admin',
      email: 'john.admin@test.com',
      role: 'ADMIN',
    });
    
    // Mock initial users list
    mockUserService.getAllUsers.mockResolvedValue(mockUsers);
  });

  test('complete user management workflow - create, edit, delete', async () => {
    const user = userEvent.setup();
    renderUsersPage();

    // Wait for initial users to load
    await waitFor(() => {
      expect(screen.getByText('John Admin')).toBeInTheDocument();
      expect(screen.getByText('Jane Operator')).toBeInTheDocument();
    });

    // Step 1: Create new user
    const createButton = screen.getByRole('button', { name: /add user/i });
    await user.click(createButton);

    // Fill in create user form
    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const roleSelect = screen.getByLabelText(/role/i);

    await user.type(firstNameInput, 'New');
    await user.type(lastNameInput, 'User');
    await user.type(emailInput, 'new.user@test.com');
    await user.type(passwordInput, 'password123');
    await user.click(roleSelect);
    
    const operatorOption = screen.getByRole('option', { name: /operator/i });
    await user.click(operatorOption);

    // Mock successful user creation
    const newUser = {
      id: 3,
      firstName: 'New',
      lastName: 'User',
      email: 'new.user@test.com',
      role: 'OPERATOR',
    };
    mockUserService.createUser.mockResolvedValue(newUser);
    mockUserService.getAllUsers.mockResolvedValue([...mockUsers, newUser]);

    const submitButton = screen.getByRole('button', { name: /create user/i });
    await user.click(submitButton);

    // Verify user creation API call
    await waitFor(() => {
      expect(mockUserService.createUser).toHaveBeenCalledWith({
        firstName: 'New',
        lastName: 'User',
        email: 'new.user@test.com',
        password: 'password123',
        role: 'OPERATOR',
      });
    });

    // Wait for success notification and updated list
    await waitFor(() => {
      expect(screen.getByText(/user created successfully/i)).toBeInTheDocument();
      expect(screen.getByText('New User')).toBeInTheDocument();
    });

    // Step 2: Edit the newly created user
    const userRows = screen.getAllByRole('row');
    const newUserRow = userRows.find(row => 
      within(row).queryByText('New User')
    );
    
    expect(newUserRow).toBeInTheDocument();
    const editButton = within(newUserRow!).getByRole('button', { name: /edit/i });
    await user.click(editButton);

    // Update user details
    const editFirstNameInput = screen.getByDisplayValue('New');
    await user.clear(editFirstNameInput);
    await user.type(editFirstNameInput, 'Updated');

    // Mock successful user update
    const updatedUser = { ...newUser, firstName: 'Updated' };
    mockUserService.updateUser.mockResolvedValue(updatedUser);
    mockUserService.getAllUsers.mockResolvedValue([
      ...mockUsers,
      updatedUser,
    ]);

    const updateButton = screen.getByRole('button', { name: /update user/i });
    await user.click(updateButton);

    // Verify user update API call
    await waitFor(() => {
      expect(mockUserService.updateUser).toHaveBeenCalledWith(3, {
        firstName: 'Updated',
        lastName: 'User',
        email: 'new.user@test.com',
        role: 'OPERATOR',
      });
    });

    // Wait for success notification and updated display
    await waitFor(() => {
      expect(screen.getByText(/user updated successfully/i)).toBeInTheDocument();
      expect(screen.getByText('Updated User')).toBeInTheDocument();
    });

    // Step 3: Delete the user
    const updatedUserRows = screen.getAllByRole('row');
    const updatedUserRow = updatedUserRows.find(row => 
      within(row).queryByText('Updated User')
    );
    
    const deleteButton = within(updatedUserRow!).getByRole('button', { name: /delete/i });
    await user.click(deleteButton);

    // Confirm deletion in dialog
    const confirmButton = screen.getByRole('button', { name: /confirm/i });
    
    // Mock successful user deletion
    mockUserService.deleteUser.mockResolvedValue(undefined);
    mockUserService.getAllUsers.mockResolvedValue(mockUsers);

    await user.click(confirmButton);

    // Verify user deletion API call
    await waitFor(() => {
      expect(mockUserService.deleteUser).toHaveBeenCalledWith(3);
    });

    // Wait for success notification and user removal
    await waitFor(() => {
      expect(screen.getByText(/user deleted successfully/i)).toBeInTheDocument();
      expect(screen.queryByText('Updated User')).not.toBeInTheDocument();
    });
  });

  test('user creation validation and error handling', async () => {
    const user = userEvent.setup();
    renderUsersPage();

    await waitFor(() => {
      expect(screen.getByText('John Admin')).toBeInTheDocument();
    });

    // Open create user form
    const createButton = screen.getByRole('button', { name: /add user/i });
    await user.click(createButton);

    // Try to submit empty form
    const submitButton = screen.getByRole('button', { name: /create user/i });
    await user.click(submitButton);

    // Check for validation errors
    await waitFor(() => {
      expect(screen.getByText(/first name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/last name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/email is required/i)).toBeInTheDocument();
      expect(screen.getByText(/password is required/i)).toBeInTheDocument();
    });

    // Fill form with invalid email
    const emailInput = screen.getByLabelText(/email/i);
    await user.type(emailInput, 'invalid-email');

    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/please enter a valid email/i)).toBeInTheDocument();
    });

    // Test server error handling
    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const passwordInput = screen.getByLabelText(/password/i);

    await user.clear(emailInput);
    await user.type(firstNameInput, 'Test');
    await user.type(lastNameInput, 'User');
    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'password123');

    // Mock server error
    mockUserService.createUser.mockRejectedValue(new Error('Email already exists'));

    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/email already exists/i)).toBeInTheDocument();
    });
  });

  test('user search and filtering functionality', async () => {
    const user = userEvent.setup();
    
    // Add more users for search testing
    const extendedUsers = [
      ...mockUsers,
      {
        id: 3,
        firstName: 'Alice',
        lastName: 'Manager',
        email: 'alice.manager@test.com',
        role: 'ADMIN',
      },
      {
        id: 4,
        firstName: 'Bob',
        lastName: 'Support',
        email: 'bob.support@test.com',
        role: 'OPERATOR',
      },
    ];
    
    mockUserService.getAllUsers.mockResolvedValue(extendedUsers);
    
    renderUsersPage();

    await waitFor(() => {
      expect(screen.getByText('John Admin')).toBeInTheDocument();
      expect(screen.getByText('Alice Manager')).toBeInTheDocument();
    });

    // Test search functionality
    const searchInput = screen.getByPlaceholderText(/search users/i);
    await user.type(searchInput, 'Alice');

    await waitFor(() => {
      expect(screen.getByText('Alice Manager')).toBeInTheDocument();
      expect(screen.queryByText('John Admin')).not.toBeInTheDocument();
    });

    // Clear search
    await user.clear(searchInput);

    await waitFor(() => {
      expect(screen.getByText('John Admin')).toBeInTheDocument();
      expect(screen.getByText('Alice Manager')).toBeInTheDocument();
    });

    // Test role filtering
    const roleFilter = screen.getByLabelText(/filter by role/i);
    await user.click(roleFilter);
    
    const operatorOption = screen.getByRole('option', { name: /operator/i });
    await user.click(operatorOption);

    await waitFor(() => {
      expect(screen.getByText('Jane Operator')).toBeInTheDocument();
      expect(screen.getByText('Bob Support')).toBeInTheDocument();
      expect(screen.queryByText('John Admin')).not.toBeInTheDocument();
      expect(screen.queryByText('Alice Manager')).not.toBeInTheDocument();
    });
  });

  test('user permissions and role-based UI elements', async () => {
    // Test as operator user (should not see user management)
    mockAuthService.getCurrentUser.mockResolvedValue({
      id: 2,
      firstName: 'Jane',
      lastName: 'Operator',
      email: 'jane.operator@test.com',
      role: 'OPERATOR',
    });

    renderUsersPage();

    await waitFor(() => {
      expect(screen.getByText(/access denied/i)).toBeInTheDocument();
      expect(screen.queryByRole('button', { name: /add user/i })).not.toBeInTheDocument();
    });
  });
});