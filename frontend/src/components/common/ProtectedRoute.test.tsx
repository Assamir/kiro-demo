import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import ProtectedRoute from './ProtectedRoute';
import theme from '../../theme/theme';
import { User } from '../../types/auth';

// Mock the AuthContext
const mockAuthContext = {
  user: null as User | null,
  isAuthenticated: false,
  isLoading: false,
  login: jest.fn(),
  logout: jest.fn(),
  refreshUser: jest.fn(),
  hasRole: jest.fn(),
  canManageUsers: jest.fn(),
  canIssuePolicies: jest.fn(),
};

jest.mock('../../contexts/AuthContext', () => ({
  useAuth: () => mockAuthContext,
}));

const TestComponent: React.FC = () => <div>Protected Content</div>;
const LoginComponent: React.FC = () => <div>Login Page</div>;
const DashboardComponent: React.FC = () => <div>Dashboard</div>;

const renderWithRouter = (
  component: React.ReactElement,
  initialEntries: string[] = ['/protected']
) => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <Routes>
          <Route path="/login" element={<LoginComponent />} />
          <Route path="/dashboard" element={<DashboardComponent />} />
          <Route path="/protected" element={component} />
        </Routes>
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('ProtectedRoute', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Reset mock context to default state
    mockAuthContext.user = null;
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.isLoading = false;
  });

  test('shows loading spinner when authentication is loading', () => {
    mockAuthContext.isLoading = true;
    
    renderWithRouter(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  test('redirects to login when user is not authenticated', () => {
    mockAuthContext.isAuthenticated = false;
    mockAuthContext.isLoading = false;
    
    renderWithRouter(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Login Page')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  test('renders protected content when user is authenticated', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.isLoading = false;
    mockAuthContext.user = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'OPERATOR',
    };
    
    renderWithRouter(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
    expect(screen.queryByText('Login Page')).not.toBeInTheDocument();
  });

  test('allows access when user has required role', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.isLoading = false;
    mockAuthContext.user = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@example.com',
      role: 'ADMIN',
    };
    
    renderWithRouter(
      <ProtectedRoute requiredRole="ADMIN">
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });

  test('redirects to dashboard when user lacks required role', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.isLoading = false;
    mockAuthContext.user = {
      id: 1,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@example.com',
      role: 'OPERATOR',
    };
    
    renderWithRouter(
      <ProtectedRoute requiredRole="ADMIN">
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  test('allows OPERATOR to access OPERATOR-only routes', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.isLoading = false;
    mockAuthContext.user = {
      id: 1,
      firstName: 'Operator',
      lastName: 'User',
      email: 'operator@example.com',
      role: 'OPERATOR',
    };
    
    renderWithRouter(
      <ProtectedRoute requiredRole="OPERATOR">
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });

  test('prevents ADMIN from accessing OPERATOR-only routes', () => {
    mockAuthContext.isAuthenticated = true;
    mockAuthContext.isLoading = false;
    mockAuthContext.user = {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@example.com',
      role: 'ADMIN',
    };
    
    renderWithRouter(
      <ProtectedRoute requiredRole="OPERATOR">
        <TestComponent />
      </ProtectedRoute>
    );
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });
});