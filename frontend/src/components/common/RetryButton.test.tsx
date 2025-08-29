import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RetryButton from './RetryButton';
import { NotificationProvider } from '../../contexts/NotificationContext';

// Mock the retry utils
jest.mock('../../utils/retryUtils', () => ({
  withRetry: jest.fn(),
  isRetryableError: jest.fn(),
}));

const { withRetry, isRetryableError } = require('../../utils/retryUtils');

const renderWithProvider = (component: React.ReactElement) => {
  return render(
    <NotificationProvider>
      {component}
    </NotificationProvider>
  );
};

describe('RetryButton', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  it('should render retry button with default text', () => {
    const mockOnRetry = jest.fn();
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    expect(screen.getByText('Retry')).toBeInTheDocument();
    expect(screen.getByTestId('RefreshIcon')).toBeInTheDocument();
  });

  it('should render custom button text', () => {
    const mockOnRetry = jest.fn();
    
    renderWithProvider(
      <RetryButton onRetry={mockOnRetry}>
        Try Again
      </RetryButton>
    );
    
    expect(screen.getByText('Try Again')).toBeInTheDocument();
  });

  it('should call onRetry when clicked', async () => {
    const mockOnRetry = jest.fn().mockResolvedValue(undefined);
    withRetry.mockImplementation((fn) => fn());
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(mockOnRetry).toHaveBeenCalled();
    });
  });

  it('should show loading state during retry', async () => {
    const mockOnRetry = jest.fn().mockImplementation(
      () => new Promise(resolve => setTimeout(resolve, 1000))
    );
    withRetry.mockImplementation((fn) => fn());
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    expect(screen.getByText('Retrying...')).toBeInTheDocument();
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    expect(button).toBeDisabled();
  });

  it('should show attempt count during retry', async () => {
    const mockOnRetry = jest.fn().mockRejectedValue(new Error('Test error'));
    withRetry.mockImplementation(async (fn, options) => {
      // Simulate retry attempts
      options.onRetry(1, new Error('Test error'));
      options.onRetry(2, new Error('Test error'));
      throw new Error('Test error');
    });
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry} 
        maxAttempts={3}
        showAttemptCount={true}
      />
    );
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Retrying... (2/3)')).toBeInTheDocument();
    });
  });

  it('should hide attempt count when showAttemptCount is false', async () => {
    const mockOnRetry = jest.fn().mockRejectedValue(new Error('Test error'));
    withRetry.mockImplementation(async (fn, options) => {
      options.onRetry(1, new Error('Test error'));
      throw new Error('Test error');
    });
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry} 
        showAttemptCount={false}
      />
    );
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Retrying...')).toBeInTheDocument();
      expect(screen.queryByText(/\(\d+\/\d+\)/)).not.toBeInTheDocument();
    });
  });

  it('should show error state for non-retryable errors', async () => {
    const mockOnRetry = jest.fn().mockRejectedValue(new Error('Non-retryable error'));
    withRetry.mockRejectedValue(new Error('Non-retryable error'));
    isRetryableError.mockReturnValue(false);
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Failed')).toBeInTheDocument();
      expect(screen.getByTestId('ErrorIcon')).toBeInTheDocument();
      expect(button).toBeDisabled();
    });
  });

  it('should show tooltip for non-retryable errors', async () => {
    const mockOnRetry = jest.fn().mockRejectedValue(new Error('Non-retryable error'));
    withRetry.mockRejectedValue(new Error('Non-retryable error'));
    isRetryableError.mockReturnValue(false);
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(screen.getByText('Failed')).toBeInTheDocument();
    });
    
    // Hover to show tooltip
    fireEvent.mouseOver(button);
    
    await waitFor(() => {
      expect(screen.getByText('Operation failed: Non-retryable error')).toBeInTheDocument();
    });
  });

  it('should use custom retry condition', async () => {
    const mockOnRetry = jest.fn().mockRejectedValue({ status: 400 });
    const customRetryCondition = jest.fn().mockReturnValue(true);
    withRetry.mockImplementation(async (fn, options) => {
      expect(options.retryCondition).toBe(customRetryCondition);
      throw { status: 400 };
    });
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry} 
        retryCondition={customRetryCondition}
      />
    );
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(customRetryCondition).toHaveBeenCalledWith({ status: 400 });
    });
  });

  it('should auto retry when autoRetry is enabled', async () => {
    const mockOnRetry = jest.fn()
      .mockRejectedValueOnce(new Error('Retryable error'))
      .mockResolvedValue(undefined);
    
    withRetry
      .mockRejectedValueOnce(new Error('Retryable error'))
      .mockResolvedValueOnce(undefined);
    
    isRetryableError.mockReturnValue(true);
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry} 
        autoRetry={true}
        retryDelay={100}
      />
    );
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    // Wait for first attempt to fail
    await waitFor(() => {
      expect(screen.getByText('Failed')).toBeInTheDocument();
    });
    
    // Fast-forward to trigger auto retry
    jest.advanceTimersByTime(100);
    
    await waitFor(() => {
      expect(withRetry).toHaveBeenCalledTimes(2);
    });
  });

  it('should pass through button props', () => {
    const mockOnRetry = jest.fn();
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry}
        variant="contained"
        color="secondary"
        size="large"
        data-testid="custom-retry-button"
      />
    );
    
    const button = screen.getByTestId('custom-retry-button');
    expect(button).toHaveClass('MuiButton-contained');
    expect(button).toHaveClass('MuiButton-sizeLarge');
  });

  it('should be disabled when disabled prop is true', () => {
    const mockOnRetry = jest.fn();
    
    renderWithProvider(
      <RetryButton onRetry={mockOnRetry} disabled={true} />
    );
    
    const button = screen.getByRole('button');
    expect(button).toBeDisabled();
  });

  it('should use custom messages', async () => {
    const mockOnRetry = jest.fn().mockResolvedValue(undefined);
    withRetry.mockImplementation((fn) => fn());
    
    renderWithProvider(
      <RetryButton 
        onRetry={mockOnRetry}
        retryMessage="Processing..."
        successMessage="Done!"
      />
    );
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    // Should show custom retry message
    expect(screen.getByText('Processing...')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(mockOnRetry).toHaveBeenCalled();
    });
  });

  it('should handle synchronous onRetry function', async () => {
    const mockOnRetry = jest.fn(); // Synchronous function
    withRetry.mockImplementation((fn) => fn());
    
    renderWithProvider(<RetryButton onRetry={mockOnRetry} />);
    
    const button = screen.getByRole('button');
    fireEvent.click(button);
    
    await waitFor(() => {
      expect(mockOnRetry).toHaveBeenCalled();
    });
  });
});