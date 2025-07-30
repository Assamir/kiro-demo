import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import App from './App';
import theme from './theme/theme';

// Mock the AuthContext to avoid API calls in tests
const mockAuthContext = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  login: () => Promise.resolve(),
  logout: () => {},
};

jest.mock('./contexts/AuthContext', () => ({
  AuthProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  useAuth: () => mockAuthContext,
}));

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        {component}
      </ThemeProvider>
    </BrowserRouter>
  );
};

test('renders login page when not authenticated', () => {
  renderWithProviders(<App />);
  
  // Should redirect to login page when not authenticated
  expect(screen.getByText(/Insurance Backoffice/i)).toBeInTheDocument();
  expect(screen.getByText(/Please sign in to continue/i)).toBeInTheDocument();
});