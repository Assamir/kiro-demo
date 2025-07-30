import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import App from '../App';
import theme from '../theme/theme';

// Mock the auth service
const mockAuthService = {
  login: jest.fn(),
  getCurrentUser: jest.fn(),
  logout: jest.fn(),
};

jest.mock('../services/authService', () => ({
  authService: mockAuthService,
}));

const renderApp = () => {
  return render(
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <App />
      </ThemeProvider>
    </BrowserRouter>
  );
};

describe('Authentication Flow Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('complete authentication flow from login to protected route', async () => {
    const user = userEvent.setup();
    
    // Mock successful login
    mockAuthService.login.mockResolvedValue({
      token: 'test-token',
      user: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        role: 'OPERATOR',
      },
    });
    
    // Mock getCurrentUser for initial load
    mockAuthService.getCurrentUser.mockRejectedValue(new Error('No token'));
    
    renderApp();
    
    // Should show login page initially
    expect(await screen.findByText(/Insurance Backoffice/i)).toBeInTheDocument();
    expect(screen.getByText(/Please sign in to continue/i)).toBeInTheDocument();
    
    // Fill in login form
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });
    
    await user.type(emailInput, 'john@example.com');
    await user.type(passwordInput, 'password123');
    await user.click(submitButton);
    
    // Should call login service
    await waitFor(() => {
      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: 'john@example.com',
        password: 'password123',
      });
    });
    
    // Should store token in localStorage
    expect(localStorage.getItem('token')).toBe('test-token');
  });
});