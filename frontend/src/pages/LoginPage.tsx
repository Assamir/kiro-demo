import React, { useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
} from '@mui/material';
import { Business } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { LoginRequest } from '../types/auth';
import { isValidEmail, isRequired } from '../utils/validators';

interface FormErrors {
  email?: string;
  password?: string;
}

const LoginPage: React.FC = () => {
  const { login, isAuthenticated } = useAuth();
  const location = useLocation();
  const [credentials, setCredentials] = useState<LoginRequest>({
    email: '',
    password: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formErrors, setFormErrors] = useState<FormErrors>({});

  const from = location.state?.from?.pathname || '/dashboard';

  if (isAuthenticated) {
    return <Navigate to={from} replace />;
  }

  const validateForm = (): boolean => {
    const errors: FormErrors = {};

    if (!isRequired(credentials.email)) {
      errors.email = 'Email is required';
    } else if (!isValidEmail(credentials.email)) {
      errors.email = 'Please enter a valid email address';
    }

    if (!isRequired(credentials.password)) {
      errors.password = 'Password is required';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);
    setError(null);
    setFormErrors({});

    try {
      await login(credentials);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (field: keyof LoginRequest) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = e.target.value;
    setCredentials(prev => ({
      ...prev,
      [field]: value,
    }));

    // Clear field error when user starts typing
    if (formErrors[field]) {
      setFormErrors(prev => ({
        ...prev,
        [field]: undefined,
      }));
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'background.default',
        p: 2,
      }}
    >
      <Card sx={{ maxWidth: 400, width: '100%' }}>
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Business sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
            <Typography variant="h4" color="primary.main" fontWeight="bold">
              Insurance Backoffice
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              Please sign in to continue
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Email"
              type="email"
              value={credentials.email}
              onChange={handleChange('email')}
              margin="normal"
              required
              disabled={isLoading}
              error={!!formErrors.email}
              helperText={formErrors.email}
              autoComplete="email"
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              value={credentials.password}
              onChange={handleChange('password')}
              margin="normal"
              required
              disabled={isLoading}
              error={!!formErrors.password}
              helperText={formErrors.password}
              autoComplete="current-password"
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={isLoading}
              sx={{ mt: 3, mb: 2 }}
            >
              {isLoading ? <CircularProgress size={24} /> : 'Sign In'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
};

export default LoginPage;