import { useState, useCallback } from 'react';
import { useNotification } from '../contexts/NotificationContext';
import { ApiError } from '../services/apiClient';
import { RatingDataMissingError, PremiumCalculationError } from '../services/ratingService';

export interface ErrorState {
  hasError: boolean;
  error: Error | null;
  errorType: 'network' | 'validation' | 'rating' | 'permission' | 'unknown';
  isRetryable: boolean;
  retryCount: number;
  fieldErrors: Record<string, string>;
}

export interface ErrorHandlingOptions {
  showNotification?: boolean;
  maxRetries?: number;
  onError?: (error: Error, errorState: ErrorState) => void;
  onRetry?: (retryCount: number) => void;
}

export function useErrorHandling(options: ErrorHandlingOptions = {}) {
  const {
    showNotification = true,
    maxRetries = 3,
    onError,
    onRetry,
  } = options;

  const { showError, showWarning } = useNotification();
  
  const [errorState, setErrorState] = useState<ErrorState>({
    hasError: false,
    error: null,
    errorType: 'unknown',
    isRetryable: false,
    retryCount: 0,
    fieldErrors: {},
  });

  const determineErrorType = useCallback((error: any): ErrorState['errorType'] => {
    if (error instanceof RatingDataMissingError) return 'rating';
    if (error instanceof PremiumCalculationError) return 'rating';
    if (error?.code === 'ERR_NETWORK' || error?.code === 'ECONNABORTED') return 'network';
    if (error?.status === 401 || error?.status === 403) return 'permission';
    if (error?.status === 400 || error?.status === 422) return 'validation';
    return 'unknown';
  }, []);

  const isErrorRetryable = useCallback((error: any, errorType: ErrorState['errorType']): boolean => {
    // Rating errors are generally not retryable (missing data)
    if (errorType === 'rating') return false;
    
    // Permission errors are not retryable
    if (errorType === 'permission') return false;
    
    // Validation errors are not retryable
    if (errorType === 'validation') return false;
    
    // Network errors are retryable
    if (errorType === 'network') return true;
    
    // Check if API error is marked as retryable
    if (error?.retryable === true) return true;
    
    // Server errors (5xx) are retryable
    if (error?.status >= 500) return true;
    
    return false;
  }, []);

  const extractFieldErrors = useCallback((error: any): Record<string, string> => {
    if (error?.details?.fieldErrors) {
      return error.details.fieldErrors;
    }
    
    if (error?.response?.data?.fieldErrors) {
      return error.response.data.fieldErrors;
    }
    
    return {};
  }, []);

  const getErrorMessage = useCallback((error: any, errorType: ErrorState['errorType']): string => {
    // Custom messages for specific error types
    switch (errorType) {
      case 'rating':
        if (error instanceof RatingDataMissingError) {
          return `Cannot calculate premium: ${error.message}. Please contact system administrator.`;
        }
        if (error instanceof PremiumCalculationError) {
          return `Premium calculation failed: ${error.message}`;
        }
        return 'Rating system error occurred';
        
      case 'network':
        return 'Network connection error. Please check your internet connection and try again.';
        
      case 'permission':
        return 'You do not have permission to perform this action.';
        
      case 'validation':
        return error?.message || 'Please check your input and try again.';
        
      default:
        return error?.message || 'An unexpected error occurred';
    }
  }, []);

  const handleError = useCallback((error: any) => {
    const errorType = determineErrorType(error);
    const isRetryable = isErrorRetryable(error, errorType);
    const fieldErrors = extractFieldErrors(error);
    const message = getErrorMessage(error, errorType);

    const newErrorState: ErrorState = {
      hasError: true,
      error,
      errorType,
      isRetryable,
      retryCount: errorState.retryCount,
      fieldErrors,
    };

    setErrorState(newErrorState);

    // Show notification if enabled
    if (showNotification) {
      if (errorType === 'rating') {
        showWarning(message, 10000); // Longer duration for rating errors
      } else {
        showError(message, 8000);
      }
    }

    // Call custom error handler
    if (onError) {
      onError(error, newErrorState);
    }
  }, [
    determineErrorType,
    isErrorRetryable,
    extractFieldErrors,
    getErrorMessage,
    errorState.retryCount,
    showNotification,
    showError,
    showWarning,
    onError,
  ]);

  const retry = useCallback(async (retryFn: () => Promise<any>) => {
    if (!errorState.isRetryable || errorState.retryCount >= maxRetries) {
      return;
    }

    const newRetryCount = errorState.retryCount + 1;
    
    setErrorState(prev => ({
      ...prev,
      retryCount: newRetryCount,
    }));

    if (onRetry) {
      onRetry(newRetryCount);
    }

    try {
      const result = await retryFn();
      clearError();
      return result;
    } catch (error) {
      handleError(error);
      throw error;
    }
  }, [errorState.isRetryable, errorState.retryCount, maxRetries, onRetry, handleError]);

  const clearError = useCallback(() => {
    setErrorState({
      hasError: false,
      error: null,
      errorType: 'unknown',
      isRetryable: false,
      retryCount: 0,
      fieldErrors: {},
    });
  }, []);

  const canRetry = errorState.isRetryable && errorState.retryCount < maxRetries;

  return {
    errorState,
    handleError,
    clearError,
    retry,
    canRetry,
  };
}