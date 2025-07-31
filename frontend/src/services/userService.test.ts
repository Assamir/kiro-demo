import { userService } from './userService';
import { apiClient } from './apiClient';
import { User, CreateUserRequest, UpdateUserRequest } from '../types/auth';

// Mock the apiClient
jest.mock('./apiClient');
const mockedApiClient = apiClient as jest.Mocked<typeof apiClient>;

describe('userService', () => {
  const mockUser: User = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    role: 'ADMIN',
  };

  const mockUsers: User[] = [
    mockUser,
    {
      id: 2,
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane.smith@example.com',
      role: 'OPERATOR',
    },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAllUsers', () => {
    it('should fetch all users successfully', async () => {
      mockedApiClient.get.mockResolvedValue({ data: mockUsers });

      const result = await userService.getAllUsers();

      expect(mockedApiClient.get).toHaveBeenCalledWith('/users');
      expect(result).toEqual(mockUsers);
    });

    it('should handle API error', async () => {
      const errorMessage = 'Failed to fetch users';
      mockedApiClient.get.mockRejectedValue(new Error(errorMessage));

      await expect(userService.getAllUsers()).rejects.toThrow(errorMessage);
      expect(mockedApiClient.get).toHaveBeenCalledWith('/users');
    });
  });

  describe('getUserById', () => {
    it('should fetch user by id successfully', async () => {
      mockedApiClient.get.mockResolvedValue({ data: mockUser });

      const result = await userService.getUserById(1);

      expect(mockedApiClient.get).toHaveBeenCalledWith('/users/1');
      expect(result).toEqual(mockUser);
    });

    it('should handle user not found error', async () => {
      const errorMessage = 'User not found';
      mockedApiClient.get.mockRejectedValue(new Error(errorMessage));

      await expect(userService.getUserById(999)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.get).toHaveBeenCalledWith('/users/999');
    });
  });

  describe('createUser', () => {
    it('should create user successfully', async () => {
      const createUserRequest: CreateUserRequest = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        password: 'SecurePass123',
        role: 'ADMIN',
      };

      mockedApiClient.post.mockResolvedValue({ data: mockUser });

      const result = await userService.createUser(createUserRequest);

      expect(mockedApiClient.post).toHaveBeenCalledWith('/users', createUserRequest);
      expect(result).toEqual(mockUser);
    });

    it('should handle validation error', async () => {
      const createUserRequest: CreateUserRequest = {
        firstName: '',
        lastName: '',
        email: 'invalid-email',
        password: 'weak',
        role: 'ADMIN',
      };

      const errorMessage = 'Validation failed';
      mockedApiClient.post.mockRejectedValue(new Error(errorMessage));

      await expect(userService.createUser(createUserRequest)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.post).toHaveBeenCalledWith('/users', createUserRequest);
    });

    it('should handle duplicate email error', async () => {
      const createUserRequest: CreateUserRequest = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'existing@example.com',
        password: 'SecurePass123',
        role: 'ADMIN',
      };

      const errorMessage = 'Email already exists';
      mockedApiClient.post.mockRejectedValue(new Error(errorMessage));

      await expect(userService.createUser(createUserRequest)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.post).toHaveBeenCalledWith('/users', createUserRequest);
    });
  });

  describe('updateUser', () => {
    it('should update user successfully', async () => {
      const updateUserRequest: UpdateUserRequest = {
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane.smith@example.com',
        role: 'OPERATOR',
      };

      const updatedUser: User = {
        ...mockUser,
        ...updateUserRequest,
      };

      mockedApiClient.put.mockResolvedValue({ data: updatedUser });

      const result = await userService.updateUser(1, updateUserRequest);

      expect(mockedApiClient.put).toHaveBeenCalledWith('/users/1', updateUserRequest);
      expect(result).toEqual(updatedUser);
    });

    it('should handle user not found error', async () => {
      const updateUserRequest: UpdateUserRequest = {
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane.smith@example.com',
        role: 'OPERATOR',
      };

      const errorMessage = 'User not found';
      mockedApiClient.put.mockRejectedValue(new Error(errorMessage));

      await expect(userService.updateUser(999, updateUserRequest)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.put).toHaveBeenCalledWith('/users/999', updateUserRequest);
    });

    it('should handle validation error during update', async () => {
      const updateUserRequest: UpdateUserRequest = {
        firstName: '',
        lastName: '',
        email: 'invalid-email',
        role: 'ADMIN',
      };

      const errorMessage = 'Validation failed';
      mockedApiClient.put.mockRejectedValue(new Error(errorMessage));

      await expect(userService.updateUser(1, updateUserRequest)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.put).toHaveBeenCalledWith('/users/1', updateUserRequest);
    });
  });

  describe('deleteUser', () => {
    it('should delete user successfully', async () => {
      mockedApiClient.delete.mockResolvedValue({});

      await userService.deleteUser(1);

      expect(mockedApiClient.delete).toHaveBeenCalledWith('/users/1');
    });

    it('should handle user not found error', async () => {
      const errorMessage = 'User not found';
      mockedApiClient.delete.mockRejectedValue(new Error(errorMessage));

      await expect(userService.deleteUser(999)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.delete).toHaveBeenCalledWith('/users/999');
    });

    it('should handle permission error', async () => {
      const errorMessage = 'Access denied';
      mockedApiClient.delete.mockRejectedValue(new Error(errorMessage));

      await expect(userService.deleteUser(1)).rejects.toThrow(errorMessage);
      expect(mockedApiClient.delete).toHaveBeenCalledWith('/users/1');
    });
  });

  describe('API call parameters', () => {
    it('should use correct endpoints for all operations', async () => {
      // Mock all API calls
      mockedApiClient.get.mockResolvedValue({ data: mockUsers });
      mockedApiClient.post.mockResolvedValue({ data: mockUser });
      mockedApiClient.put.mockResolvedValue({ data: mockUser });
      mockedApiClient.delete.mockResolvedValue({});

      const createRequest: CreateUserRequest = {
        firstName: 'Test',
        lastName: 'User',
        email: 'test@example.com',
        password: 'TestPass123',
        role: 'OPERATOR',
      };

      const updateRequest: UpdateUserRequest = {
        firstName: 'Updated',
        lastName: 'User',
        email: 'updated@example.com',
        role: 'ADMIN',
      };

      // Test all service methods
      await userService.getAllUsers();
      await userService.getUserById(1);
      await userService.createUser(createRequest);
      await userService.updateUser(1, updateRequest);
      await userService.deleteUser(1);

      // Verify correct endpoints were called
      expect(mockedApiClient.get).toHaveBeenCalledWith('/users');
      expect(mockedApiClient.get).toHaveBeenCalledWith('/users/1');
      expect(mockedApiClient.post).toHaveBeenCalledWith('/users', createRequest);
      expect(mockedApiClient.put).toHaveBeenCalledWith('/users/1', updateRequest);
      expect(mockedApiClient.delete).toHaveBeenCalledWith('/users/1');
    });
  });

  describe('data transformation', () => {
    it('should return data from API response correctly', async () => {
      const apiResponse = {
        data: mockUsers,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {},
      };

      mockedApiClient.get.mockResolvedValue(apiResponse);

      const result = await userService.getAllUsers();

      expect(result).toEqual(mockUsers);
      expect(result).not.toEqual(apiResponse); // Should return only the data, not the full response
    });

    it('should handle empty user list', async () => {
      mockedApiClient.get.mockResolvedValue({ data: [] });

      const result = await userService.getAllUsers();

      expect(result).toEqual([]);
      expect(Array.isArray(result)).toBe(true);
    });
  });
});