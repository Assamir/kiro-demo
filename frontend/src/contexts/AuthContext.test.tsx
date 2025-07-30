import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AuthProvider, useAuth } from './AuthContext';
import { User } from '../types/auth';

// Mock the auth service
const mockAuthService = {
  login: jest.fn(),
  getCurrentUser: jest.fn(),
  logout: jest.fn(),
};

jest.mock('../services/authService', () => ({
  authService: mockAuthService,
}));

// Test component to access auth context
const TestComponent: React.FC = () => {
  const {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    hasRole,
    canManageUsers,
    canIssuePolicies,
  } = useAuth();

  return (
    <div>
      <div data-testid="user">{user ? JSON.stringify(user) : 'null'}</div>
      <div data-testid="isAuthenticated">{isAuthenticated.toString()}</div>
      <div data-testid="isLoading">{isLoading.toString()}</div>
      <div data-testid="hasAdminRole">{hasRole('ADMIN').toString()}</div>
      <div data-testid="hasOperatorRole">{hasRole('OPERATOR').toString()}</div>
      <div data-testid="canManageUsers">{canManageUsers().toString()}</div>
      <div data-testid="canIssuePolicies">{canIssuePolicies().toString()}</div>
      <button onClick={() => login({ email: 'test@example.com', password: 'password' })}>
        Login
      </button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

const renderWithAuthProvider = () => {
  return render(
    <AuthProvider>
      <TestComponent />
    </AuthProvider>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('initializes with unauthenticated state', async () => {
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isLoading')).toHaveTextContent('false');
    });
    
    expect(screen.getByTestId('user')).toHaveTextContent('null');
    expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('false');
    expect(screen.getByTestId('hasAdminRole')).toHaveTextContent('false');
    expect(screen.getByTestId('hasOperatorRole')).toHaveTextContent('false');
    expect(screen.getByTestId('canManageUsers')).toHaveTextContent('false');
    expect(screen.getByTestId('canIssuePolicies')).toHaveTextContent('false');
  });

  test('initializes with existing token and user data', async () => {
    const mockUser: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'ADMIN',
    };
    
    localStorage.setItem('token', 'mock-token');
    mockAuthService.getCurrentUser.mockResolvedValue(mockUser);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isLoading')).toHaveTextContent('false');
    });
    
    expect(screen.getByTestId('user')).toHaveTextContent(JSON.stringify(mockUser));
    expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('true');
    expect(screen.getByTestId('hasAdminRole')).toHaveTextContent('true');
    expect(screen.getByTestId('hasOperatorRole')).toHaveTextContent('false');
    expect(screen.getByTestId('canManageUsers')).toHaveTextContent('true');
    expect(screen.getByTestId('canIssuePolicies')).toHaveTextContent('true');
  });

  test('handles login successfully', async () => {
    const user = userEvent.setup();
    const mockUser: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'OPERATOR',
    };
    
    mockAuthService.login.mockResolvedValue({
      token: 'new-token',
      user: mockUser,
    });
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isLoading')).toHaveTextContent('false');
    });
    
    await user.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent(JSON.stringify(mockUser));
    });
    
    expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('true');
    expect(screen.getByTestId('hasOperatorRole')).toHaveTextContent('true');
    expect(screen.getByTestId('canManageUsers')).toHaveTextContent('false');
    expect(screen.getByTestId('canIssuePolicies')).toHaveTextContent('true');
    expect(localStorage.getItem('token')).toBe('new-token');
  });

  test('handles logout successfully', async () => {
    const user = userEvent.setup();
    const mockUser: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'ADMIN',
    };
    
    localStorage.setItem('token', 'existing-token');
    mockAuthService.getCurrentUser.mockResolvedValue(mockUser);
    mockAuthService.logout.mockResolvedValue(undefined);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('true');
    });
    
    await user.click(screen.getByText('Logout'));
    
    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('null');
    });
    
    expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('false');
    expect(localStorage.getItem('token')).toBeNull();
  });

  test('handles logout even when server call fails', async () => {
    const user = userEvent.setup();
    const mockUser: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'ADMIN',
    };
    
    localStorage.setItem('token', 'existing-token');
    mockAuthService.getCurrentUser.mockResolvedValue(mockUser);
    mockAuthService.logout.mockRejectedValue(new Error('Server error'));
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('true');
    });
    
    await user.click(screen.getByText('Logout'));
    
    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('null');
    });
    
    expect(screen.getByTestId('isAuthenticated')).toHaveTextContent('false');
    expect(localStorage.getItem('token')).toBeNull();
  });

  test('role-based permissions work correctly for ADMIN', async () => {
    const mockUser: User = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@example.com',
      role: 'ADMIN',
    };
    
    localStorage.setItem('token', 'admin-token');
    mockAuthService.getCurrentUser.mockResolvedValue(mockUser);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isLoading')).toHaveTextContent('false');
    });
    
    expect(screen.getByTestId('hasAdminRole')).toHaveTextContent('true');
    expect(screen.getByTestId('hasOperatorRole')).toHaveTextContent('false');
    expect(screen.getByTestId('canManageUsers')).toHaveTextContent('true');
    expect(screen.getByTestId('canIssuePolicies')).toHaveTextContent('true');
  });

  test('role-based permissions work correctly for OPERATOR', async () => {
    const mockUser: User = {
      id: 2,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@example.com',
      role: 'OPERATOR',
    };
    
    localStorage.setItem('token', 'operator-token');
    mockAuthService.getCurrentUser.mockResolvedValue(mockUser);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('isLoading')).toHaveTextContent('false');
    });
    
    expect(screen.getByTestId('hasAdminRole')).toHaveTextContent('false');
    expect(screen.getByTestId('hasOperatorRole')).toHaveTextContent('true');
    expect(screen.getByTestId('canManageUsers')).toHaveTextContent('false');
    expect(screen.getByTestId('canIssuePolicies')).toHaveTextContent('true');
  });
});