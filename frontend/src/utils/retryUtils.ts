// import { ApiError } from '../services/apiClient';

export interface RetryOptions {
  maxAttempts?: number;
  baseDelay?: number;
  maxDelay?: number;
  backoffFactor?: number;
  retryCondition?: (error: any) => boolean;
  onRetry?: (attempt: number, error: any) => void;
}

const DEFAULT_RETRY_OPTIONS: Required<RetryOptions> = {
  maxAttempts: 3,
  baseDelay: 1000,
  maxDelay: 10000,
  backoffFactor: 2,
  retryCondition: (error: any) => {
    // Retry on network errors or retryable API errors
    if (error?.code === 'ERR_NETWORK' || error?.code === 'ECONNABORTED') {
      return true;
    }
    
    // Retry on API errors marked as retryable
    if (error?.retryable === true) {
      return true;
    }
    
    // Retry on specific HTTP status codes
    const retryableStatuses = [408, 429, 500, 502, 503, 504];
    return retryableStatuses.includes(error?.status);
  },
  onRetry: () => {},
};

/**
 * Executes a function with retry logic
 */
export async function withRetry<T>(
  fn: () => Promise<T>,
  options: RetryOptions = {}
): Promise<T> {
  const config = { ...DEFAULT_RETRY_OPTIONS, ...options };
  let lastError: any;

  for (let attempt = 1; attempt <= config.maxAttempts; attempt++) {
    try {
      return await fn();
    } catch (error) {
      lastError = error;

      // Don't retry if this is the last attempt
      if (attempt === config.maxAttempts) {
        break;
      }

      // Check if we should retry this error
      if (!config.retryCondition(error)) {
        break;
      }

      // Calculate delay with exponential backoff
      const delay = Math.min(
        config.baseDelay * Math.pow(config.backoffFactor, attempt - 1),
        config.maxDelay
      );

      // Call retry callback
      config.onRetry(attempt, error);

      // Wait before retrying
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw lastError;
}

/**
 * Creates a retry wrapper for a function
 */
export function createRetryWrapper<TArgs extends any[], TReturn>(
  fn: (...args: TArgs) => Promise<TReturn>,
  options: RetryOptions = {}
) {
  return async (...args: TArgs): Promise<TReturn> => {
    return withRetry(() => fn(...args), options);
  };
}

/**
 * Retry-specific error types
 */
export class RetryError extends Error {
  public readonly attempts: number;
  public readonly lastError: any;

  constructor(message: string, attempts: number, lastError: any) {
    super(message);
    this.name = 'RetryError';
    this.attempts = attempts;
    this.lastError = lastError;
  }
}

/**
 * Creates a retry function with exponential backoff and jitter
 */
export async function retryWithJitter<T>(
  fn: () => Promise<T>,
  options: RetryOptions & { jitter?: boolean } = {}
): Promise<T> {
  const config = { ...DEFAULT_RETRY_OPTIONS, jitter: true, ...options };
  let lastError: any;

  for (let attempt = 1; attempt <= config.maxAttempts; attempt++) {
    try {
      return await fn();
    } catch (error) {
      lastError = error;

      if (attempt === config.maxAttempts || !config.retryCondition(error)) {
        break;
      }

      // Calculate delay with exponential backoff
      let delay = config.baseDelay * Math.pow(config.backoffFactor, attempt - 1);
      
      // Add jitter to prevent thundering herd
      if (config.jitter) {
        delay = delay * (0.5 + Math.random() * 0.5);
      }
      
      delay = Math.min(delay, config.maxDelay);

      config.onRetry(attempt, error);
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw new RetryError(
    `Operation failed after ${config.maxAttempts} attempts`,
    config.maxAttempts,
    lastError
  );
}

/**
 * Circuit breaker pattern implementation
 */
export class CircuitBreaker<T extends any[], R> {
  private failureCount = 0;
  private lastFailureTime = 0;
  private state: 'CLOSED' | 'OPEN' | 'HALF_OPEN' = 'CLOSED';

  constructor(
    private fn: (...args: T) => Promise<R>,
    private options: {
      failureThreshold?: number;
      resetTimeout?: number;
      monitoringPeriod?: number;
    } = {}
  ) {
    this.options = {
      failureThreshold: 5,
      resetTimeout: 60000, // 1 minute
      monitoringPeriod: 10000, // 10 seconds
      ...options,
    };
  }

  async execute(...args: T): Promise<R> {
    if (this.state === 'OPEN') {
      if (Date.now() - this.lastFailureTime >= this.options.resetTimeout!) {
        this.state = 'HALF_OPEN';
      } else {
        throw new Error('Circuit breaker is OPEN');
      }
    }

    try {
      const result = await this.fn(...args);
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }

  private onSuccess() {
    this.failureCount = 0;
    this.state = 'CLOSED';
  }

  private onFailure() {
    this.failureCount++;
    this.lastFailureTime = Date.now();

    if (this.failureCount >= this.options.failureThreshold!) {
      this.state = 'OPEN';
    }
  }

  getState() {
    return {
      state: this.state,
      failureCount: this.failureCount,
      lastFailureTime: this.lastFailureTime,
    };
  }

  reset() {
    this.failureCount = 0;
    this.lastFailureTime = 0;
    this.state = 'CLOSED';
  }
}

/**
 * Utility to check if an error is retryable
 */
export function isRetryableError(error: any): boolean {
  // Network errors
  if (error?.code === 'ERR_NETWORK' || error?.code === 'ECONNABORTED') {
    return true;
  }

  // API errors marked as retryable
  if (error?.retryable === true) {
    return true;
  }

  // HTTP status codes that are typically retryable
  const retryableStatuses = [408, 429, 500, 502, 503, 504];
  return retryableStatuses.includes(error?.status);
}

/**
 * Creates a debounced retry function
 */
export function createDebouncedRetry<TArgs extends any[], TReturn>(
  fn: (...args: TArgs) => Promise<TReturn>,
  debounceMs: number = 300,
  retryOptions: RetryOptions = {}
) {
  let timeoutId: NodeJS.Timeout | null = null;
  let lastPromise: Promise<TReturn> | null = null;

  return (...args: TArgs): Promise<TReturn> => {
    // Clear existing timeout
    if (timeoutId) {
      clearTimeout(timeoutId);
    }

    // Return existing promise if one is pending
    if (lastPromise) {
      return lastPromise;
    }

    return new Promise((resolve, reject) => {
      timeoutId = setTimeout(async () => {
        try {
          lastPromise = withRetry(() => fn(...args), retryOptions);
          const result = await lastPromise;
          lastPromise = null;
          resolve(result);
        } catch (error) {
          lastPromise = null;
          reject(error);
        }
      }, debounceMs);
    });
  };
}