import {
  withRetry,
  createRetryWrapper,
  RetryError,
  retryWithJitter,
  CircuitBreaker,
  isRetryableError,
  createDebouncedRetry,
} from './retryUtils';

// Mock setTimeout and clearTimeout
jest.useFakeTimers();

describe('retryUtils', () => {
  beforeEach(() => {
    jest.clearAllTimers();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
  });

  describe('withRetry', () => {
    it('should succeed on first attempt', async () => {
      const mockFn = jest.fn().mockResolvedValue('success');

      const result = await withRetry(mockFn);

      expect(result).toBe('success');
      expect(mockFn).toHaveBeenCalledTimes(1);
    });

    it('should retry on retryable errors', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      const promise = withRetry(mockFn, { maxAttempts: 3, baseDelay: 100 });

      // Fast-forward timers
      jest.advanceTimersByTime(100);

      const result = await promise;

      expect(result).toBe('success');
      expect(mockFn).toHaveBeenCalledTimes(2);
    });

    it('should not retry on non-retryable errors', async () => {
      const mockFn = jest.fn().mockRejectedValue({ status: 400, retryable: false });

      await expect(withRetry(mockFn)).rejects.toEqual({ status: 400, retryable: false });
      expect(mockFn).toHaveBeenCalledTimes(1);
    });

    it('should respect maxAttempts', async () => {
      const mockFn = jest.fn().mockRejectedValue({ status: 500, retryable: true });

      const promise = withRetry(mockFn, { maxAttempts: 2, baseDelay: 100 });

      // Fast-forward through all retry attempts
      jest.advanceTimersByTime(1000);

      await expect(promise).rejects.toEqual({ status: 500, retryable: true });
      expect(mockFn).toHaveBeenCalledTimes(2);
    });

    it('should call onRetry callback', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');
      const onRetry = jest.fn();

      const promise = withRetry(mockFn, { onRetry, baseDelay: 100 });

      jest.advanceTimersByTime(100);

      await promise;

      expect(onRetry).toHaveBeenCalledWith(1, { status: 500, retryable: true });
    });

    it('should use exponential backoff', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      const promise = withRetry(mockFn, {
        maxAttempts: 3,
        baseDelay: 100,
        backoffFactor: 2,
      });

      // First retry after 100ms
      jest.advanceTimersByTime(100);
      // Second retry after 200ms
      jest.advanceTimersByTime(200);

      await promise;

      expect(mockFn).toHaveBeenCalledTimes(3);
    });

    it('should respect maxDelay', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      const promise = withRetry(mockFn, {
        baseDelay: 1000,
        maxDelay: 500,
        backoffFactor: 2,
      });

      // Should use maxDelay instead of calculated delay
      jest.advanceTimersByTime(500);

      await promise;

      expect(mockFn).toHaveBeenCalledTimes(2);
    });
  });

  describe('createRetryWrapper', () => {
    it('should create a wrapped function with retry logic', async () => {
      const originalFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      const wrappedFn = createRetryWrapper(originalFn, { baseDelay: 100 });

      const promise = wrappedFn('arg1', 'arg2');

      jest.advanceTimersByTime(100);

      const result = await promise;

      expect(result).toBe('success');
      expect(originalFn).toHaveBeenCalledWith('arg1', 'arg2');
      expect(originalFn).toHaveBeenCalledTimes(2);
    });
  });

  describe('retryWithJitter', () => {
    it('should add jitter to delay', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      // Mock Math.random to return a predictable value
      const originalRandom = Math.random;
      Math.random = jest.fn().mockReturnValue(0.5);

      const promise = retryWithJitter(mockFn, {
        baseDelay: 1000,
        jitter: true,
      });

      // With jitter, delay should be 1000 * (0.5 + 0.5 * 0.5) = 750ms
      jest.advanceTimersByTime(750);

      await promise;

      expect(mockFn).toHaveBeenCalledTimes(2);

      Math.random = originalRandom;
    });

    it('should throw RetryError after max attempts', async () => {
      const mockFn = jest.fn().mockRejectedValue({ status: 500, retryable: true });

      const promise = retryWithJitter(mockFn, { maxAttempts: 2, baseDelay: 100 });

      jest.advanceTimersByTime(1000);

      await expect(promise).rejects.toBeInstanceOf(RetryError);
    });
  });

  describe('CircuitBreaker', () => {
    it('should allow calls when circuit is closed', async () => {
      const mockFn = jest.fn().mockResolvedValue('success');
      const circuitBreaker = new CircuitBreaker(mockFn);

      const result = await circuitBreaker.execute('arg1');

      expect(result).toBe('success');
      expect(mockFn).toHaveBeenCalledWith('arg1');
      expect(circuitBreaker.getState().state).toBe('CLOSED');
    });

    it('should open circuit after failure threshold', async () => {
      const mockFn = jest.fn().mockRejectedValue(new Error('failure'));
      const circuitBreaker = new CircuitBreaker(mockFn, { failureThreshold: 2 });

      // First failure
      await expect(circuitBreaker.execute()).rejects.toThrow('failure');
      expect(circuitBreaker.getState().state).toBe('CLOSED');

      // Second failure - should open circuit
      await expect(circuitBreaker.execute()).rejects.toThrow('failure');
      expect(circuitBreaker.getState().state).toBe('OPEN');

      // Third call should be rejected immediately
      await expect(circuitBreaker.execute()).rejects.toThrow('Circuit breaker is OPEN');
      expect(mockFn).toHaveBeenCalledTimes(2);
    });

    it('should transition to half-open after reset timeout', async () => {
      const mockFn = jest.fn().mockRejectedValue(new Error('failure'));
      const circuitBreaker = new CircuitBreaker(mockFn, {
        failureThreshold: 1,
        resetTimeout: 1000,
      });

      // Trigger circuit open
      await expect(circuitBreaker.execute()).rejects.toThrow('failure');
      expect(circuitBreaker.getState().state).toBe('OPEN');

      // Fast-forward past reset timeout
      jest.advanceTimersByTime(1000);

      // Mock successful call
      mockFn.mockResolvedValueOnce('success');

      const result = await circuitBreaker.execute();

      expect(result).toBe('success');
      expect(circuitBreaker.getState().state).toBe('CLOSED');
    });

    it('should reset circuit breaker', () => {
      const mockFn = jest.fn();
      const circuitBreaker = new CircuitBreaker(mockFn);

      // Manually set some state
      circuitBreaker['failureCount'] = 5;
      circuitBreaker['state'] = 'OPEN';

      circuitBreaker.reset();

      const state = circuitBreaker.getState();
      expect(state.state).toBe('CLOSED');
      expect(state.failureCount).toBe(0);
    });
  });

  describe('isRetryableError', () => {
    it('should identify network errors as retryable', () => {
      expect(isRetryableError({ code: 'ERR_NETWORK' })).toBe(true);
      expect(isRetryableError({ code: 'ECONNABORTED' })).toBe(true);
    });

    it('should identify errors marked as retryable', () => {
      expect(isRetryableError({ retryable: true })).toBe(true);
      expect(isRetryableError({ retryable: false })).toBe(false);
    });

    it('should identify retryable HTTP status codes', () => {
      expect(isRetryableError({ status: 500 })).toBe(true);
      expect(isRetryableError({ status: 502 })).toBe(true);
      expect(isRetryableError({ status: 503 })).toBe(true);
      expect(isRetryableError({ status: 504 })).toBe(true);
      expect(isRetryableError({ status: 429 })).toBe(true);
      expect(isRetryableError({ status: 408 })).toBe(true);
    });

    it('should identify non-retryable errors', () => {
      expect(isRetryableError({ status: 400 })).toBe(false);
      expect(isRetryableError({ status: 401 })).toBe(false);
      expect(isRetryableError({ status: 403 })).toBe(false);
      expect(isRetryableError({ status: 404 })).toBe(false);
    });
  });

  describe('createDebouncedRetry', () => {
    it('should debounce function calls', async () => {
      const mockFn = jest.fn().mockResolvedValue('success');
      const debouncedFn = createDebouncedRetry(mockFn, 300);

      // Call multiple times quickly
      const promise1 = debouncedFn('arg1');
      const promise2 = debouncedFn('arg2');
      const promise3 = debouncedFn('arg3');

      // Fast-forward past debounce time
      jest.advanceTimersByTime(300);

      const results = await Promise.all([promise1, promise2, promise3]);

      // Should only call the function once with the last arguments
      expect(mockFn).toHaveBeenCalledTimes(1);
      expect(mockFn).toHaveBeenCalledWith('arg3');
      expect(results).toEqual(['success', 'success', 'success']);
    });

    it('should retry on failure', async () => {
      const mockFn = jest
        .fn()
        .mockRejectedValueOnce({ status: 500, retryable: true })
        .mockResolvedValue('success');

      const debouncedFn = createDebouncedRetry(mockFn, 300, { baseDelay: 100 });

      const promise = debouncedFn('arg1');

      // Fast-forward past debounce time
      jest.advanceTimersByTime(300);
      // Fast-forward past retry delay
      jest.advanceTimersByTime(100);

      const result = await promise;

      expect(result).toBe('success');
      expect(mockFn).toHaveBeenCalledTimes(2);
    });
  });
});