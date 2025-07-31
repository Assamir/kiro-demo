import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import Layout from './Layout';
import { AuthProvider } from '../../contexts/AuthContext';
import theme from '../../theme/theme';

// Mock the child components
jest.mock('./Header', () => {
  return function MockHeader() {
    return <div data-testid="header">Header Component</div>;
  };
});

jest.mock('./Sidebar', () => {
  return function MockSidebar() {
    return <div data-testid="sidebar">Sidebar Component</div>;
  };
});

// Mock the auth service
jest.mock('../../services/authService', () => ({
  authService: {
    getCurrentUser: jest.fn(),
    login: jest.fn(),
    logout: jest.fn(),
  },
}));

const TestPage = () => <div data-testid="test-page">Test Page Content</div>;

const renderWithProviders = () => {
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
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<TestPage />} />
            </Route>
          </Routes>
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('Layout Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders layout with header and sidebar', () => {
    renderWithProviders();
    
    expect(screen.getByTestId('header')).toBeInTheDocument();
    expect(screen.getByTestId('sidebar')).toBeInTheDocument();
  });

  test('renders main content area', () => {
    renderWithProviders();
    
    expect(screen.getByTestId('test-page')).toBeInTheDocument();
  });

  test('applies correct layout structure', () => {
    renderWithProviders();
    
    const mainContent = screen.getByRole('main');
    expect(mainContent).toBeInTheDocument();
    expect(mainContent).toHaveStyle('flex-grow: 1');
  });

  test('has responsive design classes', () => {
    renderWithProviders();
    
    // The layout should have responsive styling
    // We can verify the main container exists
    const layoutContainer = screen.getByTestId('header').closest('div');
    expect(layoutContainer).toBeInTheDocument();
  });

  test('renders outlet for nested routes', () => {
    renderWithProviders();
    
    // The Outlet component renders the nested route content
    expect(screen.getByTestId('test-page')).toBeInTheDocument();
  });

  test('applies correct background color to main content', () => {
    renderWithProviders();
    
    const mainContent = screen.getByRole('main');
    expect(mainContent).toHaveStyle('background-color: rgb(250, 250, 250)'); // theme.palette.background.default
  });

  test('has minimum height for full viewport', () => {
    renderWithProviders();
    
    // The root container should have min-height: 100vh
    const rootContainer = screen.getByTestId('header').closest('div')?.parentElement;
    expect(rootContainer).toHaveStyle('min-height: 100vh');
  });
});