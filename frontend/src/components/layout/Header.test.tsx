import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import Header from './Header';
import theme from '../../theme/theme';
import { User } from '../../types/auth';

// Mock the useAuth hook directly
const mockUser: User = {
  id: 1,
  firstName: 'John',
  lastName: 'Doe',
  email: 'john.doe@example.com',
  role: 'ADMIN',
};

const mockLogout = jest.fn();

jest.mock('../../contexts/AuthContext', () => ({
  useAuth: () => ({
    user: mockUser,
    isAuthenticated: true,
    isLoading: false,
    login: jest.fn(),
    logout: mockLogout,
    refreshUser: jest.fn(),
    hasRole: jest.fn(),
    canManageUsers: jest.fn(),
    canIssuePolicies: jest.fn(),
  }),
}));

const renderWithProviders = () => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <Header />
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('Header Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders header with system title', () => {
    renderWithProviders();
    
    expect(screen.getByText('Insurance Backoffice System')).toBeInTheDocument();
  });

  test('displays user information for admin user', () => {
    renderWithProviders();
    
    expect(screen.getAllByText('John Doe')).toHaveLength(2); // Header and menu
    expect(screen.getAllByText('Administrator')).toHaveLength(2); // Header and menu
  });

  test('shows user avatar with initials', () => {
    renderWithProviders();
    
    expect(screen.getByText('JD')).toBeInTheDocument();
  });

  test('opens user menu when avatar is clicked', () => {
    renderWithProviders();
    
    const avatarButton = screen.getByLabelText('account of current user');
    fireEvent.click(avatarButton);
    
    expect(screen.getByText('Logout')).toBeInTheDocument();
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
  });

  test('shows mobile menu button on small screens', () => {
    renderWithProviders();
    
    const menuButton = screen.getByLabelText('open drawer');
    expect(menuButton).toBeInTheDocument();
  });

  test('opens mobile navigation drawer when menu button is clicked', () => {
    renderWithProviders();
    
    const menuButton = screen.getByLabelText('open drawer');
    fireEvent.click(menuButton);
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Policies')).toBeInTheDocument();
    expect(screen.getByText('Users')).toBeInTheDocument(); // Admin can see users
  });

  test('handles logout functionality', () => {
    renderWithProviders();
    
    const avatarButton = screen.getByLabelText('account of current user');
    fireEvent.click(avatarButton);
    
    const logoutButton = screen.getByText('Logout');
    fireEvent.click(logoutButton);
    
    expect(mockLogout).toHaveBeenCalled();
  });
});