import { renderHook, act } from '@testing-library/react';
import { useErrorHandling } from './useErrorHandling';
import { RatingDataMissingError, PremiumCalculationError } from '../services/ratingService';
import { useNotification } from '../contexts/NotificationContext';

// Mock the notification context
jest.mock('../contexts/NotificationContext', () => ({
  useNotification: jest.fn(),
}));

const mockUseNotification = useNotification as jest.MockedFunction<typeof useNotification>;

describe('useErrorHandling', () => {
  const mockShowError = jest.fn();
  const mockShowWarning = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockUseNotification.mockReturnValue({
      showError: mockShowError,
      showWarning: mockShowWarning,
      showSuccess: jest.fn(),
      showInfo: jest.fn(),
      showNotification: jest.fn(),
      hideNotification: jest.fn(),
    });
  });

  it('should initialize with no error state', () => {
    const { result } = renderHook(() => useErrorHandling());

    expect(result.current.errorState.hasError).toBe(false);
    expect(result.current.errorState.error).toBeNull();
    expect(result.current.errorState.errorType).toBe('unknown');
    expect(result.current.errorState.isRetryable).toBe(false);
    expect(result.current.errorState.retryCount).toBe(0);
    expect(result.current.canRetry).toBe(false);
  });

  it('should handle network errors correctly', () => {
    const { result } = renderHook(() => useErrorHandling());
    const networkError = { code: 'ERR_NETWORK', message: 'Network error' };

    act(() => {
      result.current.handleError(networkError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('network');
    expect(result.current.errorState.isRetryable).toBe(true);
    expect(result.current.canRetry).toBe(true);
    expect(mockShowError).toHaveBeenCalledWith(
      'Network connection error. Please check your internet connection and try again.',
      8000
    );
  });

  it('should handle rating data missing errors correctly', () => {
    const { result } = renderHook(() => useErrorHandling());
    const ratingError = new RatingDataMissingError('Missing rating tables', ['OC_COVERAGE']);

    act(() => {
      result.current.handleError(ratingError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('rating');
    expect(result.current.errorState.isRetryable).toBe(false);
    expect(result.current.canRetry).toBe(false);
    expect(mockShowWarning).toHaveBeenCalledWith(
      'Cannot calculate premium: Missing rating tables. Please contact system administrator.',
      10000
    );
  });

  it('should handle premium calculation errors correctly', () => {
    const { result } = renderHook(() => useErrorHandling());
    const calculationError = new PremiumCalculationError('Calculation failed');

    act(() => {
      result.current.handleError(calculationError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('rating');
    expect(result.current.errorState.isRetryable).toBe(false);
    expect(mockShowWarning).toHaveBeenCalledWith(
      'Premium calculation failed: Calculation failed',
      10000
    );
  });

  it('should handle validation errors correctly', () => {
    const { result } = renderHook(() => useErrorHandling());
    const validationError = {
      status: 400,
      message: 'Validation failed',
      details: {
        fieldErrors: {
          email: 'Invalid email format',
          password: 'Password too short',
        },
      },
    };

    act(() => {
      result.current.handleError(validationError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('validation');
    expect(result.current.errorState.isRetryable).toBe(false);
    expect(result.current.errorState.fieldErrors).toEqual({
      email: 'Invalid email format',
      password: 'Password too short',
    });
    expect(mockShowError).toHaveBeenCalledWith('Validation failed', 8000);
  });

  it('should handle permission errors correctly', () => {
    const { result } = renderHook(() => useErrorHandling());
    const permissionError = { status: 403, message: 'Forbidden' };

    act(() => {
      result.current.handleError(permissionError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('permission');
    expect(result.current.errorState.isRetryable).toBe(false);
    expect(mockShowError).toHaveBeenCalledWith(
      'You do not have permission to perform this action.',
      8000
    );
  });

  it('should handle server errors as retryable', () => {
    const { result } = renderHook(() => useErrorHandling());
    const serverError = { status: 500, message: 'Internal server error' };

    act(() => {
      result.current.handleError(serverError);
    });

    expect(result.current.errorState.hasError).toBe(true);
    expect(result.current.errorState.errorType).toBe('unknown');
    expect(result.current.errorState.isRetryable).toBe(true);
    expect(result.current.canRetry).toBe(true);
  });

  it('should clear error state', () => {
    const { result } = renderHook(() => useErrorHandling());
    const error = { code: 'ERR_NETWORK', message: 'Network error' };

    act(() => {
      result.current.handleError(error);
    });

    expect(result.current.errorState.hasError).toBe(true);

    act(() => {
      result.current.clearError();
    });

    expect(result.current.errorState.hasError).toBe(false);
    expect(result.current.errorState.error).toBeNull();
    expect(result.current.errorState.retryCount).toBe(0);
  });

  it('should handle retry functionality', async () => {
    const { result } = renderHook(() => useErrorHandling({ maxRetries: 2 }));
    const error = { code: 'ERR_NETWORK', message: 'Network error' };
    const mockRetryFn = jest.fn().mockResolvedValue('success');

    act(() => {
      result.current.handleError(error);
    });

    expect(result.current.canRetry).toBe(true);

    await act(async () => {
      const result_value = await result.current.retry(mockRetryFn);
      expect(result_value).toBe('success');
    });

    expect(mockRetryFn).toHaveBeenCalled();
    expect(result.current.errorState.hasError).toBe(false);
  });

  it('should increment retry count on failed retry', async () => {
    const { result } = renderHook(() => useErrorHandling({ maxRetries: 3 }));
    const error = { code: 'ERR_NETWORK', message: 'Network error' };
    const mockRetryFn = jest.fn().mockRejectedValue(error);

    act(() => {
      result.current.handleError(error);
    });

    expect(result.current.errorState.retryCount).toBe(0);

    try {
      await act(async () => {
        await result.current.retry(mockRetryFn);
      });
    } catch (e) {
      // Expected to throw
    }

    expect(result.current.errorState.retryCount).toBe(1);
    expect(result.current.canRetry).toBe(true);
  });

  it('should not retry beyond max attempts', async () => {
    const { result } = renderHook(() => useErrorHandling({ maxRetries: 1 }));
    const error = { code: 'ERR_NETWORK', message: 'Network error' };
    const mockRetryFn = jest.fn().mockRejectedValue(error);

    act(() => {
      result.current.handleError(error);
    });

    // First retry
    try {
      await act(async () => {
        await result.current.retry(mockRetryFn);
      });
    } catch (e) {
      // Expected to throw
    }

    expect(result.current.errorState.retryCount).toBe(1);
    expect(result.current.canRetry).toBe(false);

    // Should not retry again
    await act(async () => {
      await result.current.retry(mockRetryFn);
    });

    expect(mockRetryFn).toHaveBeenCalledTimes(1);
  });

  it('should call custom error handler', () => {
    const mockOnError = jest.fn();
    const { result } = renderHook(() => useErrorHandling({ onError: mockOnError }));
    const error = { message: 'Test error' };

    act(() => {
      result.current.handleError(error);
    });

    expect(mockOnError).toHaveBeenCalledWith(error, expect.objectContaining({
      hasError: true,
      error,
      errorType: 'unknown',
    }));
  });

  it('should call custom retry handler', async () => {
    const mockOnRetry = jest.fn();
    const { result } = renderHook(() => useErrorHandling({ onRetry: mockOnRetry }));
    const error = { code: 'ERR_NETWORK', message: 'Network error' };
    const mockRetryFn = jest.fn().mockResolvedValue('success');

    act(() => {
      result.current.handleError(error);
    });

    await act(async () => {
      await result.current.retry(mockRetryFn);
    });

    expect(mockOnRetry).toHaveBeenCalledWith(1);
  });

  it('should not show notifications when disabled', () => {
    const { result } = renderHook(() => useErrorHandling({ showNotification: false }));
    const error = { code: 'ERR_NETWORK', message: 'Network error' };

    act(() => {
      result.current.handleError(error);
    });

    expect(mockShowError).not.toHaveBeenCalled();
    expect(mockShowWarning).not.toHaveBeenCalled();
  });
});