import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Typography,
  Alert,
  InputAdornment,
} from '@mui/material';
import {
  Person,
  Email,
  Lock,
  AdminPanelSettings,
} from '@mui/icons-material';
import { User, CreateUserRequest, UpdateUserRequest } from '../../types/auth';
import { useFormValidation } from '../../hooks/useFormValidation';
import { useNotification } from '../../contexts/NotificationContext';
import EnhancedTextField from '../common/EnhancedTextField';
import RetryButton from '../common/RetryButton';
import { ValidationSchema, validateEmail, validatePassword, validateRequired } from '../../utils/validators';

interface UserFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (userData: CreateUserRequest | UpdateUserRequest) => Promise<void>;
  user?: User | null;
  loading?: boolean;
}

interface FormData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: 'ADMIN' | 'OPERATOR';
}

interface FormErrors {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  role?: string;
}

const UserForm: React.FC<UserFormProps> = ({
  open,
  onClose,
  onSubmit,
  user = null,
  loading = false,
}) => {
  const isEditing = Boolean(user);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const { showError, showSuccess } = useNotification();

  // Define validation schema
  const validationSchema: ValidationSchema<FormData> = {
    firstName: {
      required: true,
      rules: [
        {
          validator: (value: string) => value.trim().length >= 2,
          message: 'First name must be at least 2 characters',
        },
      ],
    },
    lastName: {
      required: true,
      rules: [
        {
          validator: (value: string) => value.trim().length >= 2,
          message: 'Last name must be at least 2 characters',
        },
      ],
    },
    email: {
      required: true,
      customValidator: (value: string) => validateEmail(value),
    },
    password: {
      required: !isEditing, // Only required for new users
      customValidator: (value: string) => {
        if (isEditing && !value) return { isValid: true }; // Skip validation for empty password in edit mode
        return validatePassword(value);
      },
    },
    role: {
      required: true,
      customValidator: (value: string) => validateRequired(value, 'Role'),
    },
  };

  // Initialize form data
  const initialValues: FormData = {
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    password: '', // Never populate password for editing
    role: user?.role || 'OPERATOR',
  };

  // Use enhanced form validation
  const {
    values,
    errors,
    touched,
    isSubmitting,
    isValid,
    handleChange,
    handleBlur,
    handleSubmit,
    reset,
    setError,
  } = useFormValidation({
    initialValues,
    validationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    onSubmit: async (formData) => {
      setSubmitError(null);
      try {
        if (isEditing) {
          const updateData: UpdateUserRequest = {
            firstName: formData.firstName.trim(),
            lastName: formData.lastName.trim(),
            email: formData.email.trim(),
            role: formData.role,
          };
          await onSubmit(updateData);
          showSuccess('User updated successfully');
        } else {
          const createData: CreateUserRequest = {
            firstName: formData.firstName.trim(),
            lastName: formData.lastName.trim(),
            email: formData.email.trim(),
            password: formData.password,
            role: formData.role,
          };
          await onSubmit(createData);
          showSuccess('User created successfully');
        }
        onClose();
      } catch (error: any) {
        const errorMessage = error?.message || 'An error occurred while saving the user';
        setSubmitError(errorMessage);
        showError(errorMessage);
        
        // Set field-specific errors if available
        if (error?.details?.fieldErrors) {
          Object.entries(error.details.fieldErrors).forEach(([field, message]) => {
            setError(field as keyof FormData, message as string);
          });
        }
      }
    },
  });

  // Reset form when dialog opens/closes or user changes
  useEffect(() => {
    if (open) {
      reset(initialValues);
      setSubmitError(null);
    }
  }, [user, open, reset]);

  const handleRoleChange = (event: any) => {
    const value = event.target.value as 'ADMIN' | 'OPERATOR';
    handleChange('role')({ target: { value } } as any);
  };

  const handleClose = () => {
    if (!loading && !isSubmitting) {
      onClose();
    }
  };

  const handleRetrySubmit = async () => {
    await handleSubmit({} as React.FormEvent);
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="sm" 
      fullWidth
      PaperProps={{
        sx: { borderRadius: 2 }
      }}
    >
      <DialogTitle sx={{ pb: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {isEditing ? <Person /> : <AdminPanelSettings />}
          <Typography variant="h6">
            {isEditing ? 'Edit User' : 'Create New User'}
          </Typography>
        </Box>
      </DialogTitle>

      <form onSubmit={handleSubmit}>
        <DialogContent sx={{ pt: 2 }}>
          {submitError && (
            <Alert 
              severity="error" 
              sx={{ mb: 2 }}
              action={
                <RetryButton
                  onRetry={handleRetrySubmit}
                  size="small"
                  variant="text"
                  color="inherit"
                >
                  Retry
                </RetryButton>
              }
            >
              {submitError}
            </Alert>
          )}

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            {/* First Name */}
            <EnhancedTextField
              label="First Name"
              value={values.firstName}
              onChange={handleChange('firstName')}
              onBlur={handleBlur('firstName')}
              validationState={touched.firstName ? (errors.firstName ? 'error' : 'success') : null}
              validationMessage={touched.firstName ? errors.firstName : undefined}
              required
              fullWidth
              loading={isSubmitting}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Person />
                  </InputAdornment>
                ),
              }}
              helpText="Enter the user's first name (minimum 2 characters)"
            />

            {/* Last Name */}
            <EnhancedTextField
              label="Last Name"
              value={values.lastName}
              onChange={handleChange('lastName')}
              onBlur={handleBlur('lastName')}
              validationState={touched.lastName ? (errors.lastName ? 'error' : 'success') : null}
              validationMessage={touched.lastName ? errors.lastName : undefined}
              required
              fullWidth
              loading={isSubmitting}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Person />
                  </InputAdornment>
                ),
              }}
              helpText="Enter the user's last name (minimum 2 characters)"
            />

            {/* Email */}
            <EnhancedTextField
              label="Email"
              type="email"
              value={values.email}
              onChange={handleChange('email')}
              onBlur={handleBlur('email')}
              validationState={touched.email ? (errors.email ? 'error' : 'success') : null}
              validationMessage={touched.email ? errors.email : undefined}
              required
              fullWidth
              loading={isSubmitting}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Email />
                  </InputAdornment>
                ),
              }}
              helpText="Enter a valid email address for system access"
            />

            {/* Password (only for new users) */}
            {!isEditing && (
              <EnhancedTextField
                label="Password"
                isPassword
                value={values.password}
                onChange={handleChange('password')}
                onBlur={handleBlur('password')}
                validationState={touched.password ? (errors.password ? 'error' : 'success') : null}
                validationMessage={touched.password ? errors.password : undefined}
                required
                fullWidth
                loading={isSubmitting}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock />
                    </InputAdornment>
                  ),
                }}
                helpText="Password must be at least 8 characters with uppercase, lowercase, and number"
              />
            )}

            {/* Role */}
            <FormControl fullWidth error={Boolean(touched.role && errors.role)}>
              <InputLabel id="user-role-label">Role</InputLabel>
              <Select
                labelId="user-role-label"
                value={values.role}
                label="Role"
                onChange={handleRoleChange}
                onBlur={handleBlur('role')}
                disabled={isSubmitting}
                startAdornment={
                  <InputAdornment position="start">
                    <AdminPanelSettings />
                  </InputAdornment>
                }
              >
                <MenuItem value="OPERATOR">Operator</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </Select>
              {touched.role && errors.role && (
                <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                  {errors.role}
                </Typography>
              )}
            </FormControl>
          </Box>
        </DialogContent>

        <DialogActions sx={{ p: 3, pt: 2 }}>
          <Button 
            onClick={handleClose} 
            disabled={loading || isSubmitting}
            color="inherit"
          >
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading || isSubmitting || !isValid}
            sx={{ minWidth: 100 }}
          >
            {isSubmitting ? 'Saving...' : (isEditing ? 'Update' : 'Create')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default UserForm;