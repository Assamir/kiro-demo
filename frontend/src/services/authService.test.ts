import { authService } from './authService';
import { apiClient } from './apiClient';
import { LoginRequest, AuthResponse, User } from '../types/auth';

// Mock the API client
jest.mock('./apiClient');
const mockedApiClient = apiClient as jest.Mocked<typeof apiClient>;

describe('authService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('login', () => {
    test('calls API with correct credentials and returns auth response', async () => {
      const credentials: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };
      
      const mockResponse: AuthResponse = {
        token: 'jwt-token',
        user: {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'test@example.com',
          role: 'OPERATOR',
        },
      };
      
      mockedApiClient.post.mockResolvedValue({ data: mockResponse });
      
      const result = await authService.login(credentials);
      
      expect(mockedApiClient.post).toHaveBeenCalledWith('/auth/login', credentials);
      expect(result).toEqual(mockResponse);
    });

    test('throws error when login fails', async () => {
      const credentials: LoginRequest = {
        email: 'test@example.com',
        password: 'wrongpassword',
      };
      
      const error = new Error('Invalid credentials');
      mockedApiClient.post.mockRejectedValue(error);
      
      await expect(authService.login(credentials)).rejects.toThrow('Invalid credentials');
      expect(mockedApiClient.post).toHaveBeenCalledWith('/auth/login', credentials);
    });
  });

  describe('getCurrentUser', () => {
    test('calls API and returns user data', async () => {
      const mockUser: User = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'test@example.com',
        role: 'ADMIN',
      };
      
      mockedApiClient.get.mockResolvedValue({ data: mockUser });
      
      const result = await authService.getCurrentUser();
      
      expect(mockedApiClient.get).toHaveBeenCalledWith('/auth/me');
      expect(result).toEqual(mockUser);
    });

    test('throws error when user data cannot be retrieved', async () => {
      const error = new Error('Unauthorized');
      mockedApiClient.get.mockRejectedValue(error);
      
      await expect(authService.getCurrentUser()).rejects.toThrow('Unauthorized');
      expect(mockedApiClient.get).toHaveBeenCalledWith('/auth/me');
    });
  });

  describe('logout', () => {
    test('calls logout API endpoint', async () => {
      mockedApiClient.post.mockResolvedValue({ data: {} });
      
      await authService.logout();
      
      expect(mockedApiClient.post).toHaveBeenCalledWith('/auth/logout');
    });

    test('handles logout API errors gracefully', async () => {
      const error = new Error('Server error');
      mockedApiClient.post.mockRejectedValue(error);
      
      await expect(authService.logout()).rejects.toThrow('Server error');
      expect(mockedApiClient.post).toHaveBeenCalledWith('/auth/logout');
    });
  });
});