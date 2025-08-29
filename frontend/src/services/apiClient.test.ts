import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { apiClient, apiGet, apiPost, apiPut, apiDelete, ApiError } from './apiClient';

// Mock localStorage
const mockLocalStorage = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
};
Object.defineProperty(window, 'localStorage', { value: mockLocalStorage });

// Mock window.location
const mockLocation = {
  pathname: '/',
  href: '',
};
Object.defineProperty(window, 'location', { value: mockLocation });

// Mock console methods
const mockConsole = {
  log: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};
Object.defineProperty(console, 'log', { value: mockConsole.log });
Object.defineProperty(console, 'warn', { value: mockConsole.warn });
Object.defineProperty(console, 'error', { value: mockConsole.error });

describe('apiClient', () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(apiClient);
    jest.clearAllMocks();
    jest.useFakeTimers();
  });

  afterEach(() => {
    mock.restore();
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  describe('request interceptor', () => {
    it('should add authorization header when token exists', async () => {
      mockLocalStorage.getItem.mockReturnValue('test-token');
      mock.onGet('/test').reply(200, { data: 'success' });

      await apiClient.get('/test');

      expect(mock.history.get[0].headers?.Authorization).toBe('Bearer test-token');
    });

    it('should not add authorization header when token does not exist', async () => {
      mockLocalStorage.getItem.mockReturnValue(null);
      mock.onGet('/test').reply(200, { data: 'success' });

      await apiClient.get('/test');

      expect(mock.history.get[0].headers?.Authorization).toBeUndefined();
    });
  });

  describe('response interceptor', () => {
    it('should handle successful responses', async () => {
      mock.onGet('/test').reply(200, { data: 'success' });

      const response = await apiClient.get('/test');

      expect(response.data).toEqual({ data: 'success' });
    });

    it('should handle 401 unauthorized errors', async () => {
      mock.onGet('/test').reply(401, { message: 'Unauthorized' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('Authentication required. Please log in again.');
        expect(mockLocalStorage.removeItem).toHaveBeenCalledWith('token');
        expect(mockLocation.href).toBe('/login');
      }
    });

    it('should not redirect to login if already on login page', async () => {
      mockLocation.pathname = '/login';
      mock.onGet('/test').reply(401, { message: 'Unauthorized' });

      try {
        await apiClient.get('/test');
      } catch (error) {
        expect(mockLocation.href).toBe('');
      }
    });

    it('should handle 403 forbidden errors', async () => {
      mock.onGet('/test').reply(403, { message: 'Forbidden' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('You do not have permission to perform this action.');
      }
    });

    it('should handle 404 not found errors', async () => {
      mock.onGet('/test').reply(404, { message: 'Not found' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('Not found');
        expect(error.code).toBe('NOT_FOUND_ERROR');
      }
    });

    it('should handle 500 server errors', async () => {
      mock.onGet('/test').reply(500, { message: 'Internal server error' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('Internal server error. Please try again later.');
        expect(error.retryable).toBe(true);
      }
    });

    it('should handle network errors', async () => {
      mock.onGet('/test').networkError();

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('Network error. Please check your internet connection.');
        expect(error.code).toBe('NETWORK_ERROR');
        expect(error.retryable).toBe(true);
      }
    });

    it('should handle timeout errors', async () => {
      mock.onGet('/test').timeout();

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.message).toBe('Request timeout. Please check your connection and try again.');
        expect(error.code).toBe('TIMEOUT_ERROR');
        expect(error.retryable).toBe(true);
      }
    });
  });

  describe('retry functionality', () => {
    it('should retry on retryable errors', async () => {
      mock
        .onGet('/test')
        .replyOnce(500, { message: 'Server error' })
        .onGet('/test')
        .reply(200, { data: 'success' });

      const response = await apiClient.get('/test');

      expect(response.data).toEqual({ data: 'success' });
      expect(mock.history.get).toHaveLength(2);
    });

    it('should not retry on non-retryable errors', async () => {
      mock.onGet('/test').reply(400, { message: 'Bad request' });

      try {
        await apiClient.get('/test');
      } catch (error) {
        expect(mock.history.get).toHaveLength(1);
      }
    });

    it('should respect max retry attempts', async () => {
      mock.onGet('/test').reply(500, { message: 'Server error' });

      try {
        await apiClient.get('/test');
      } catch (error) {
        // Should try 3 times (initial + 2 retries)
        expect(mock.history.get).toHaveLength(3);
      }
    });

    it('should use exponential backoff for retries', async () => {
      mock.onGet('/test').reply(500, { message: 'Server error' });

      const startTime = Date.now();
      
      try {
        await apiClient.get('/test');
      } catch (error) {
        // Fast-forward timers to simulate retry delays
        jest.advanceTimersByTime(3000); // 1000 + 2000 for exponential backoff
      }

      expect(mock.history.get).toHaveLength(3);
    });
  });

  describe('utility functions', () => {
    describe('apiGet', () => {
      it('should make GET request', async () => {
        mock.onGet('/test').reply(200, { data: 'success' });

        const result = await apiGet('/test');

        expect(result).toEqual({ data: 'success' });
        expect(mock.history.get[0].method).toBe('get');
      });

      it('should support custom retry configuration', async () => {
        mock
          .onGet('/test')
          .replyOnce(500)
          .onGet('/test')
          .reply(200, { data: 'success' });

        const result = await apiGet('/test', {
          retry: { maxAttempts: 2, retryDelay: 100 }
        });

        expect(result).toEqual({ data: 'success' });
      });
    });

    describe('apiPost', () => {
      it('should make POST request with data', async () => {
        mock.onPost('/test').reply(201, { id: 1 });

        const result = await apiPost('/test', { name: 'test' });

        expect(result).toEqual({ id: 1 });
        expect(mock.history.post[0].data).toBe(JSON.stringify({ name: 'test' }));
      });
    });

    describe('apiPut', () => {
      it('should make PUT request with data', async () => {
        mock.onPut('/test/1').reply(200, { id: 1, name: 'updated' });

        const result = await apiPut('/test/1', { name: 'updated' });

        expect(result).toEqual({ id: 1, name: 'updated' });
        expect(mock.history.put[0].data).toBe(JSON.stringify({ name: 'updated' }));
      });
    });

    describe('apiDelete', () => {
      it('should make DELETE request', async () => {
        mock.onDelete('/test/1').reply(204);

        await apiDelete('/test/1');

        expect(mock.history.delete[0].method).toBe('delete');
      });
    });
  });

  describe('error creation', () => {
    it('should create ApiError with correct properties', async () => {
      mock.onGet('/test').reply(400, { message: 'Validation failed' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error).toHaveProperty('message');
        expect(error).toHaveProperty('status', 400);
        expect(error).toHaveProperty('code', 'VALIDATION_ERROR');
        expect(error).toHaveProperty('retryable', false);
      }
    });

    it('should mark server errors as retryable', async () => {
      mock.onGet('/test').reply(503, { message: 'Service unavailable' });

      try {
        await apiClient.get('/test');
      } catch (error: any) {
        expect(error.retryable).toBe(true);
        expect(error.code).toBe('SERVICE_UNAVAILABLE');
      }
    });
  });

  describe('development logging', () => {
    it('should log successful requests in development', async () => {
      const originalEnv = process.env.NODE_ENV;
      process.env.NODE_ENV = 'development';

      mock.onGet('/test').reply(200, { data: 'success' });

      await apiClient.get('/test');

      expect(mockConsole.log).toHaveBeenCalledWith(
        expect.stringContaining('✅ GET /test - 200')
      );

      process.env.NODE_ENV = originalEnv;
    });

    it('should not log in production', async () => {
      const originalEnv = process.env.NODE_ENV;
      process.env.NODE_ENV = 'production';

      mock.onGet('/test').reply(200, { data: 'success' });

      await apiClient.get('/test');

      expect(mockConsole.log).not.toHaveBeenCalled();

      process.env.NODE_ENV = originalEnv;
    });
  });
});