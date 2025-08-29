import React from 'react';
import {
  TextField,
  TextFieldProps,
  InputAdornment,
  IconButton,
  Tooltip,
  Box,
  Typography,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Info,
  CheckCircle,
  Error,
  Warning,
} from '@mui/icons-material';

interface EnhancedTextFieldProps extends Omit<TextFieldProps, 'error' | 'helperText'> {
  // Enhanced validation props
  validationState?: 'success' | 'error' | 'warning' | 'info' | null;
  validationMessage?: string;
  showValidationIcon?: boolean;
  
  // Password field props
  isPassword?: boolean;
  showPasswordToggle?: boolean;
  
  // Real-time validation
  onValidate?: (value: string) => { isValid: boolean; message?: string; severity?: 'error' | 'warning' | 'info' };
  validateOnChange?: boolean;
  validateOnBlur?: boolean;
  
  // Additional help text
  helpText?: string;
  showHelpIcon?: boolean;
  
  // Loading state
  loading?: boolean;
}

const EnhancedTextField: React.FC<EnhancedTextFieldProps> = ({
  validationState,
  validationMessage,
  showValidationIcon = true,
  isPassword = false,
  showPasswordToggle = true,
  onValidate,
  validateOnChange = false,
  validateOnBlur = true,
  helpText,
  showHelpIcon = false,
  loading = false,
  type: propType,
  InputProps,
  ...textFieldProps
}) => {
  const [showPassword, setShowPassword] = React.useState(false);
  const [internalValidationState, setInternalValidationState] = React.useState<{
    state: 'success' | 'error' | 'warning' | 'info' | null;
    message: string;
  }>({ state: null, message: '' });

  // Determine the actual type
  const actualType = isPassword ? (showPassword ? 'text' : 'password') : propType;

  // Use external validation state if provided, otherwise use internal
  const currentValidationState = validationState || internalValidationState.state;
  const currentValidationMessage = validationMessage || internalValidationState.message;

  // Handle validation
  const handleValidation = React.useCallback((value: string) => {
    if (onValidate) {
      const result = onValidate(value);
      if (!result.isValid) {
        setInternalValidationState({
          state: result.severity || 'error',
          message: result.message || 'Invalid value',
        });
      } else {
        setInternalValidationState({ state: 'success', message: '' });
      }
    }
  }, [onValidate]);

  // Handle change with validation
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (textFieldProps.onChange) {
      textFieldProps.onChange(event);
    }

    if (validateOnChange) {
      handleValidation(event.target.value);
    }
  };

  // Handle blur with validation
  const handleBlur = (event: React.FocusEvent<HTMLInputElement>) => {
    if (textFieldProps.onBlur) {
      textFieldProps.onBlur(event);
    }

    if (validateOnBlur) {
      handleValidation(event.target.value);
    }
  };

  // Toggle password visibility
  const handleTogglePassword = () => {
    setShowPassword(prev => !prev);
  };

  // Get validation icon
  const getValidationIcon = () => {
    if (!showValidationIcon || !currentValidationState) return null;

    switch (currentValidationState) {
      case 'success':
        return <CheckCircle color="success" fontSize="small" />;
      case 'error':
        return <Error color="error" fontSize="small" />;
      case 'warning':
        return <Warning color="warning" fontSize="small" />;
      case 'info':
        return <Info color="info" fontSize="small" />;
      default:
        return null;
    }
  };

  // Build InputProps
  const enhancedInputProps = {
    ...InputProps,
    endAdornment: (
      <>
        {/* Validation icon */}
        {getValidationIcon() && (
          <InputAdornment position="end">
            {getValidationIcon()}
          </InputAdornment>
        )}
        
        {/* Password toggle */}
        {isPassword && showPasswordToggle && (
          <InputAdornment position="end">
            <IconButton
              aria-label="toggle password visibility"
              onClick={handleTogglePassword}
              edge="end"
              size="small"
            >
              {showPassword ? <VisibilityOff /> : <Visibility />}
            </IconButton>
          </InputAdornment>
        )}
        
        {/* Original end adornment */}
        {InputProps?.endAdornment}
      </>
    ),
  };

  // Determine error state and helper text
  const hasError = currentValidationState === 'error';
  const helperText = currentValidationMessage || textFieldProps.helperText;

  return (
    <Box>
      <TextField
        {...textFieldProps}
        type={actualType}
        error={hasError}
        helperText={helperText}
        onChange={handleChange}
        onBlur={handleBlur}
        InputProps={enhancedInputProps}
        disabled={textFieldProps.disabled || loading}
      />
      
      {/* Additional help text */}
      {helpText && (
        <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5 }}>
          {showHelpIcon && (
            <Tooltip title="Help">
              <Info fontSize="small" color="action" sx={{ mr: 0.5 }} />
            </Tooltip>
          )}
          <Typography variant="caption" color="text.secondary">
            {helpText}
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default EnhancedTextField;