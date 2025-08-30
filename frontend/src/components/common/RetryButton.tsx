import React, { useState } from 'react';
import {
  Button,
  ButtonProps,
  CircularProgress,
  Box,
  Typography,
  Tooltip,
} from '@mui/material';
import { Refresh, Error } from '@mui/icons-material';
import { useNotification } from '../../contexts/NotificationContext';
import { withRetry, isRetryableError } from '../../utils/retryUtils';

interface RetryButtonProps extends Omit<ButtonProps, 'onClick'> {
  onRetry: () => Promise<void> | void;
  maxAttempts?: number;
  retryDelay?: number;
  showAttemptCount?: boolean;
  retryMessage?: string;
  successMessage?: string;
  errorMessage?: string;
  autoRetry?: boolean;
  retryCondition?: (error: any) => boolean;
}

const RetryButton: React.FC<RetryButtonProps> = ({
  onRetry,
  maxAttempts = 3,
  retryDelay = 1000,
  showAttemptCount = true,
  retryMessage = 'Retrying...',
  successMessage = 'Operation completed successfully',
  errorMessage = 'Operation failed after all retry attempts',
  autoRetry = false,
  retryCondition = isRetryableError,
  children = 'Retry',
  disabled,
  ...buttonProps
}) => {
  const [isRetrying, setIsRetrying] = useState(false);
  const [attemptCount, setAttemptCount] = useState(0);
  const [lastError, setLastError] = useState<any>(null);
  const { showSuccess, showError, showInfo } = useNotification();

  const handleRetry = async () => {
    if (isRetrying) return;

    setIsRetrying(true);
    setAttemptCount(0);
    setLastError(null);

    try {
      await withRetry(
        async () => {
          const result = await onRetry();
          return result;
        },
        {
          maxAttempts,
          baseDelay: retryDelay,
          retryCondition,
          onRetry: (attempt, error) => {
            setAttemptCount(attempt);
            setLastError(error);
            
            if (showAttemptCount) {
              showInfo(`${retryMessage} (Attempt ${attempt}/${maxAttempts})`);
            }
          },
        }
      );

      // Success
      setAttemptCount(0);
      setLastError(null);
      showSuccess(successMessage);
    } catch (error: any) {
      setLastError(error);
      
      const finalErrorMessage = error?.message || errorMessage;
      showError(finalErrorMessage, 8000);
    } finally {
      setIsRetrying(false);
    }
  };

  // Auto retry on mount if enabled and there's a retryable error
  React.useEffect(() => {
    if (autoRetry && lastError && retryCondition(lastError) && !isRetrying) {
      const timer = setTimeout(() => {
        handleRetry();
      }, retryDelay);

      return () => clearTimeout(timer);
    }
  }, [autoRetry, lastError, retryCondition, isRetrying, retryDelay]);

  const getButtonContent = () => {
    if (isRetrying) {
      return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <CircularProgress size={16} color="inherit" />
          <Typography variant="button">
            {showAttemptCount && attemptCount > 0
              ? `${retryMessage} (${attemptCount}/${maxAttempts})`
              : retryMessage}
          </Typography>
        </Box>
      );
    }

    if (lastError && !retryCondition(lastError)) {
      return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Error fontSize="small" />
          <Typography variant="button">Failed</Typography>
        </Box>
      );
    }

    return (
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <Refresh fontSize="small" />
        <Typography variant="button">{children}</Typography>
      </Box>
    );
  };

  const isDisabled = disabled || isRetrying || (lastError && !retryCondition(lastError));

  const buttonElement = (
    <Button
      {...buttonProps}
      onClick={handleRetry}
      disabled={isDisabled}
      variant={buttonProps.variant || 'outlined'}
      color={lastError ? 'error' : buttonProps.color || 'primary'}
    >
      {getButtonContent()}
    </Button>
  );

  // Show tooltip with error details if there's a non-retryable error
  if (lastError && !retryCondition(lastError)) {
    return (
      <Tooltip
        title={`Operation failed: ${lastError.message || 'Unknown error'}`}
        arrow
      >
        <span>{buttonElement}</span>
      </Tooltip>
    );
  }

  return buttonElement;
};

export default RetryButton;