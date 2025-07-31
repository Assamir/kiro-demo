import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import DashboardPage from './DashboardPage';
import { AuthProvider } from '../contexts/AuthContext';
import theme from '../theme/theme';
import { User } from '../types/auth';

// Mock the auth service
jest.mock('../services/authService', () => ({
  authService: {
    getCurrentUser: jest.fn(),
    login: jest.fn(),
    logout: jest.fn(),
  },
}));

// Mock react-router-dom navigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const mockAdminUser: User = {
  id: 1,
  firstName: 'John',
  lastName: 'Admin',
  email: 'admin@example.com',
  role: 'ADMIN',
};

const mockOperatorUser: User = {
  id: 2,
  firstName: 'Jane',
  lastName: 'Operator',
  email: 'operator@example.com',
  role: 'OPERATOR',
};

const renderWithProviders = (user: User = mockAdminUser) => {
  // Mock localStorage
  const mockLocalStorage = {
    getItem: jest.fn(() => 'mock-token'),
    setItem: jest.fn(),
    removeItem: jest.fn(),
  };
  Object.defineProperty(window, 'localStorage', {
    value: mockLocalStorage,
  });

  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <AuthProvider>
          <DashboardPage />
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('DashboardPage Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders welcome message with user name', async () => {
    renderWithProviders(mockAdminUser);
    
    await waitFor(() => {
      expect(screen.getByText('Welcome back, John!')).toBeInTheDocument();
    });
  });

  test('displays system overview description', () => {
    renderWithProviders();
    
    expect(screen.getByText("Here's an overview of your insurance backoffice system.")).toBeInTheDocument();
  });

  test('shows system status section', () => {
    renderWithProviders();
    
    expect(screen.getByText('System Status')).toBeInTheDocument();
    expect(screen.getByText('Online')).toBeInTheDocument();
    expect(screen.getByText('System Status')).toBeInTheDocument();
    expect(screen.getByText('v1.0.0')).toBeInTheDocument();
  });

  test('displays user role in system status', async () => {
    renderWithProviders(mockAdminUser);
    
    await waitFor(() => {
      expect(screen.getByText('ADMIN')).toBeInTheDocument();
    });
  });

  test('shows admin access information for admin users', async () => {
    renderWithProviders(mockAdminUser);
    
    await waitFor(() => {
      expect(screen.getByText(/Admin Access:/)).toBeInTheDocument();
      expect(screen.getByText(/You have full system access including user management/)).toBeInTheDocument();
      expect(screen.getByText(/Admins cannot issue policies directly/)).toBeInTheDocument();
    });
  });

  test('shows operator access information for operator users', async () => {
    renderWithProviders(mockOperatorUser);
    
    await waitFor(() => {
      expect(screen.getByText(/Operator Access:/)).toBeInTheDocument();
      expect(screen.getByText(/You can issue and manage insurance policies/)).toBeInTheDocument();
      expect(screen.getByText(/User management functions are restricted to Admin users/)).toBeInTheDocument();
    });
  });

  test('displays quick actions section', () => {
    renderWithProviders();
    
    expect(screen.getByText('Quick Actions')).toBeInTheDocument();
  });

  test('shows policies card for all users', () => {
    renderWithProviders();
    
    expect(screen.getByText('Policies')).toBeInTheDocument();
    expect(screen.getByText('Manage insurance policies')).toBeInTheDocument();
    expect(screen.getByText('View Policies')).toBeInTheDocument();
  });

  test('shows users card only for admin users', async () => {
    renderWithProviders(mockAdminUser);
    
    await waitFor(() => {
      expect(screen.getByText('Users')).toBeInTheDocument();
      expect(screen.getByText('Manage system users')).toBeInTheDocument();
      expect(screen.getByText('Manage Users')).toBeInTheDocument();
    });
  });

  test('hides users card for operator users', async () => {
    renderWithProviders(mockOperatorUser);
    
    await waitFor(() => {
      expect(screen.queryByText('Manage system users')).not.toBeInTheDocument();
    });
  });

  test('shows coming soon cards as disabled', () => {
    renderWithProviders();
    
    expect(screen.getByText('Reports')).toBeInTheDocument();
    expect(screen.getByText('Analytics')).toBeInTheDocument();
    
    const reportButton = screen.getByText('View Reports');
    const analyticsButton = screen.getByText('View Analytics');
    
    expect(reportButton).toBeDisabled();
    expect(analyticsButton).toBeDisabled();
    
    expect(screen.getAllByText('Coming Soon')).toHaveLength(2);
  });

  test('navigates to policies page when policies button is clicked', async () => {
    renderWithProviders();
    
    const policiesButton = screen.getByText('View Policies');
    fireEvent.click(policiesButton);
    
    expect(mockNavigate).toHaveBeenCalledWith('/policies');
  });

  test('navigates to users page when users button is clicked (admin only)', async () => {
    renderWithProviders(mockAdminUser);
    
    await waitFor(() => {
      const usersButton = screen.getByText('Manage Users');
      fireEvent.click(usersButton);
    });
    
    expect(mockNavigate).toHaveBeenCalledWith('/users');
  });

  test('does not navigate when disabled buttons are clicked', () => {
    renderWithProviders();
    
    const reportButton = screen.getByText('View Reports');
    fireEvent.click(reportButton);
    
    // Should not navigate for disabled buttons
    expect(mockNavigate).not.toHaveBeenCalledWith('/reports');
  });

  test('applies hover effects to available cards', () => {
    renderWithProviders();
    
    const policiesCard = screen.getByText('Policies').closest('.MuiCard-root');
    expect(policiesCard).toHaveStyle('transition: transform 0.2s');
  });

  test('displays system version information', () => {
    renderWithProviders();
    
    expect(screen.getByText('v1.0.0')).toBeInTheDocument();
    expect(screen.getByText('System Version')).toBeInTheDocument();
  });

  test('shows last login information', () => {
    renderWithProviders();
    
    expect(screen.getByText('Last Login: Today')).toBeInTheDocument();
  });
});