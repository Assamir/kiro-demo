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
  Grid,
  Divider,
} from '@mui/material';
import {
  Policy as PolicyIcon,
  Person,
  DirectionsCar,
  Euro,
  Security,
} from '@mui/icons-material';
import { Policy, CreatePolicyRequest, UpdatePolicyRequest, Client, Vehicle, PolicyDetails } from '../../types/policy';
import { useFormValidation } from '../../hooks/useFormValidation';
import { useNotification } from '../../contexts/NotificationContext';
import EnhancedTextField from '../common/EnhancedTextField';
import RetryButton from '../common/RetryButton';
import { ValidationSchema, validateRequired, validateNumber, validateDateRange } from '../../utils/validators';
import OCInsuranceForm from './OCInsuranceForm';
import ACInsuranceForm from './ACInsuranceForm';
import NNWInsuranceForm from './NNWInsuranceForm';
import PremiumCalculationDisplay from './PremiumCalculationDisplay';

interface PolicyFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (policyData: CreatePolicyRequest | UpdatePolicyRequest) => Promise<void>;
  policy?: Policy | null;
  clients: Client[];
  vehicles: Vehicle[];
  loading?: boolean;
}

interface FormData {
  clientId: number | '';
  vehicleId: number | '';
  insuranceType: 'OC' | 'AC' | 'NNW' | '';
  startDate: string;
  endDate: string;
  discountSurcharge: number | '';
  policyDetails: PolicyDetails;
}

interface FormErrors {
  clientId?: string;
  vehicleId?: string;
  insuranceType?: string;
  startDate?: string;
  endDate?: string;
  discountSurcharge?: string;
  // Policy details errors
  guaranteedSum?: string;
  coverageArea?: string;
  acVariant?: string;
  sumInsured?: string;
  coverageScope?: string;
  deductible?: string;
  workshopType?: string;
  coveredPersons?: string;
}

const PolicyForm: React.FC<PolicyFormProps> = ({
  open,
  onClose,
  onSubmit,
  policy = null,
  clients,
  vehicles,
  loading = false,
}) => {
  const isEditing = Boolean(policy);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const { showError, showSuccess } = useNotification();

  // Define validation schema
  const validationSchema: ValidationSchema<FormData> = {
    clientId: {
      required: true,
      customValidator: (value: number | '') => {
        if (!value) return { isValid: false, message: 'Client is required' };
        return { isValid: true };
      },
    },
    vehicleId: {
      required: true,
      customValidator: (value: number | '') => {
        if (!value) return { isValid: false, message: 'Vehicle is required' };
        return { isValid: true };
      },
    },
    insuranceType: {
      required: true,
      customValidator: (value: string) => validateRequired(value, 'Insurance type'),
    },
    startDate: {
      required: true,
      customValidator: (value: string) => validateRequired(value, 'Start date'),
    },
    endDate: {
      required: true,
      customValidator: (value: string) => validateRequired(value, 'End date'),
    },
    discountSurcharge: {
      required: false,
      customValidator: (value: number | '') => {
        if (value === '') return { isValid: true };
        return validateNumber(value, 'Discount/Surcharge', -10000, 10000);
      },
    },
  };

  // Initialize form data
  const getInitialValues = (): FormData => {
    if (policy) {
      // Find client and vehicle IDs based on names (this is a simplified approach)
      const client = clients.find(c => c.fullName === policy.clientName);
      const vehicle = vehicles.find(v => v.registrationNumber === policy.vehicleRegistration);
      
      return {
        clientId: client?.id || '',
        vehicleId: vehicle?.id || '',
        insuranceType: policy.insuranceType,
        startDate: policy.startDate,
        endDate: policy.endDate,
        discountSurcharge: '',
        policyDetails: policy.policyDetails || {},
      };
    } else {
      // Set default dates for new policies
      const today = new Date();
      const nextYear = new Date(today);
      nextYear.setFullYear(today.getFullYear() + 1);
      
      return {
        clientId: '',
        vehicleId: '',
        insuranceType: '',
        startDate: today.toISOString().split('T')[0],
        endDate: nextYear.toISOString().split('T')[0],
        discountSurcharge: '',
        policyDetails: {},
      };
    }
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
    setValue,
  } = useFormValidation({
    initialValues: getInitialValues(),
    validationSchema,
    validateOnChange: true,
    validateOnBlur: true,
    onSubmit: async (formData) => {
      setSubmitError(null);
      
      // Additional validation for date range
      const dateValidation = validateDateRange(formData.startDate, formData.endDate);
      if (!dateValidation.isValid) {
        setError('endDate', dateValidation.message || 'Invalid date range');
        return;
      }

      try {
        const policyData = {
          clientId: Number(formData.clientId),
          vehicleId: Number(formData.vehicleId),
          insuranceType: formData.insuranceType as 'OC' | 'AC' | 'NNW',
          startDate: formData.startDate,
          endDate: formData.endDate,
          discountSurcharge: formData.discountSurcharge === '' ? undefined : Number(formData.discountSurcharge),
          policyDetails: formData.policyDetails,
        };

        await onSubmit(policyData);
        showSuccess(isEditing ? 'Policy updated successfully' : 'Policy created successfully');
        onClose();
      } catch (error: any) {
        const errorMessage = error?.message || 'An error occurred while saving the policy';
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

  // Reset form when dialog opens/closes or policy changes
  useEffect(() => {
    if (open) {
      reset(getInitialValues());
      setSubmitError(null);
    }
  }, [policy, open, clients, vehicles, reset]);

  const handleSelectChange = (field: keyof FormData) => (event: any) => {
    const value = event.target.value;
    setValue(field, value);
    
    // Reset policy details when insurance type changes
    if (field === 'insuranceType') {
      setValue('policyDetails', {});
    }
  };

  const handlePolicyDetailsChange = (details: PolicyDetails) => {
    setValue('policyDetails', details);
  };

  const handleClose = () => {
    if (!loading && !isSubmitting) {
      onClose();
    }
  };

  const handleRetrySubmit = async () => {
    await handleSubmit({} as React.FormEvent);
  };

  const getInsuranceTypeDescription = (type: string) => {
    switch (type) {
      case 'OC':
        return 'Obligatory Civil Liability - mandatory coverage as per law';
      case 'AC':
        return 'Autocasco - comprehensive vehicle coverage';
      case 'NNW':
        return 'Accidents - personal accident coverage';
      default:
        return '';
    }
  };

  const selectedClient = clients.find(c => c.id === values.clientId);
  const selectedVehicle = vehicles.find(v => v.id === values.vehicleId);

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="md" 
      fullWidth
      PaperProps={{
        sx: { borderRadius: 2 }
      }}
    >
      <DialogTitle sx={{ pb: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <PolicyIcon />
          <Typography variant="h6">
            {isEditing ? 'Edit Policy' : 'Create New Policy'}
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

          <Grid container spacing={3}>
            {/* Client Selection */}
            <Grid item xs={12} md={6}>
              <FormControl fullWidth error={Boolean(touched.clientId && errors.clientId)}>
                <InputLabel id="client-select-label">Client</InputLabel>
                <Select
                  labelId="client-select-label"
                  value={values.clientId}
                  label="Client"
                  onChange={handleSelectChange('clientId')}
                  onBlur={handleBlur('clientId')}
                  disabled={isSubmitting}
                  startAdornment={
                    <InputAdornment position="start">
                      <Person />
                    </InputAdornment>
                  }
                >
                  {clients.map((client) => (
                    <MenuItem key={client.id} value={client.id}>
                      {client.fullName} ({client.pesel})
                    </MenuItem>
                  ))}
                </Select>
                {touched.clientId && errors.clientId && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                    {errors.clientId}
                  </Typography>
                )}
              </FormControl>
              
              {selectedClient && (
                <Box sx={{ mt: 1, p: 1, bgcolor: 'grey.50', borderRadius: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Email: {selectedClient.email} | Phone: {selectedClient.phoneNumber}
                  </Typography>
                </Box>
              )}
            </Grid>

            {/* Vehicle Selection */}
            <Grid item xs={12} md={6}>
              <FormControl fullWidth error={Boolean(touched.vehicleId && errors.vehicleId)}>
                <InputLabel id="vehicle-select-label">Vehicle</InputLabel>
                <Select
                  labelId="vehicle-select-label"
                  value={values.vehicleId}
                  label="Vehicle"
                  onChange={handleSelectChange('vehicleId')}
                  onBlur={handleBlur('vehicleId')}
                  disabled={isSubmitting}
                  startAdornment={
                    <InputAdornment position="start">
                      <DirectionsCar />
                    </InputAdornment>
                  }
                >
                  {vehicles.map((vehicle) => (
                    <MenuItem key={vehicle.id} value={vehicle.id}>
                      {vehicle.make} {vehicle.model} ({vehicle.registrationNumber})
                    </MenuItem>
                  ))}
                </Select>
                {touched.vehicleId && errors.vehicleId && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                    {errors.vehicleId}
                  </Typography>
                )}
              </FormControl>
              
              {selectedVehicle && (
                <Box sx={{ mt: 1, p: 1, bgcolor: 'grey.50', borderRadius: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Year: {selectedVehicle.yearOfManufacture} | Engine: {selectedVehicle.engineCapacity}cc | Power: {selectedVehicle.power}HP
                  </Typography>
                </Box>
              )}
            </Grid>

            <Grid item xs={12}>
              <Divider />
            </Grid>

            {/* Insurance Type */}
            <Grid item xs={12} md={6}>
              <FormControl fullWidth error={Boolean(touched.insuranceType && errors.insuranceType)}>
                <InputLabel id="insurance-type-label">Insurance Type</InputLabel>
                <Select
                  labelId="insurance-type-label"
                  value={values.insuranceType}
                  label="Insurance Type"
                  onChange={handleSelectChange('insuranceType')}
                  onBlur={handleBlur('insuranceType')}
                  disabled={isSubmitting}
                  startAdornment={
                    <InputAdornment position="start">
                      <Security />
                    </InputAdornment>
                  }
                >
                  <MenuItem value="OC">OC - Obligatory Civil Liability</MenuItem>
                  <MenuItem value="AC">AC - Autocasco</MenuItem>
                  <MenuItem value="NNW">NNW - Accidents</MenuItem>
                </Select>
                {touched.insuranceType && errors.insuranceType && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                    {errors.insuranceType}
                  </Typography>
                )}
              </FormControl>
              
              {values.insuranceType && (
                <Box sx={{ mt: 1, p: 1, bgcolor: 'info.light', color: 'info.contrastText', borderRadius: 1 }}>
                  <Typography variant="caption">
                    {getInsuranceTypeDescription(values.insuranceType)}
                  </Typography>
                </Box>
              )}
            </Grid>

            {/* Discount/Surcharge */}
            <Grid item xs={12} md={6}>
              <EnhancedTextField
                label="Discount/Surcharge"
                type="number"
                value={values.discountSurcharge}
                onChange={handleChange('discountSurcharge')}
                onBlur={handleBlur('discountSurcharge')}
                validationState={touched.discountSurcharge ? (errors.discountSurcharge ? 'error' : 'success') : null}
                validationMessage={touched.discountSurcharge ? errors.discountSurcharge : undefined}
                fullWidth
                loading={isSubmitting}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Euro />
                    </InputAdornment>
                  ),
                }}
                helpText="Negative for discount, positive for surcharge (optional)"
              />
            </Grid>

            {/* Insurance Type Specific Forms */}
            {values.insuranceType && (
              <>
                <Grid item xs={12}>
                  <Divider />
                </Grid>
                
                <Grid item xs={12}>
                  {values.insuranceType === 'OC' && (
                    <OCInsuranceForm
                      policyDetails={values.policyDetails}
                      onChange={handlePolicyDetailsChange}
                      errors={errors}
                    />
                  )}
                  {values.insuranceType === 'AC' && (
                    <ACInsuranceForm
                      policyDetails={values.policyDetails}
                      onChange={handlePolicyDetailsChange}
                      errors={errors}
                    />
                  )}
                  {values.insuranceType === 'NNW' && (
                    <NNWInsuranceForm
                      policyDetails={values.policyDetails}
                      onChange={handlePolicyDetailsChange}
                      errors={errors}
                    />
                  )}
                </Grid>
              </>
            )}

            {/* Premium Calculation Display */}
            <Grid item xs={12}>
              <PremiumCalculationDisplay
                insuranceType={values.insuranceType as 'OC' | 'AC' | 'NNW'}
                vehicleId={values.vehicleId}
                policyDate={values.startDate}
                discountSurcharge={values.discountSurcharge === '' ? 0 : Number(values.discountSurcharge)}
              />
            </Grid>

            {/* Policy Period */}
            <Grid item xs={12} md={6}>
              <EnhancedTextField
                id="start-date"
                label="Start Date"
                type="date"
                value={values.startDate}
                onChange={handleChange('startDate')}
                onBlur={handleBlur('startDate')}
                validationState={touched.startDate ? (errors.startDate ? 'error' : 'success') : null}
                validationMessage={touched.startDate ? errors.startDate : undefined}
                required
                fullWidth
                loading={isSubmitting}
                InputLabelProps={{
                  shrink: true,
                }}
                helpText="Policy coverage start date"
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <EnhancedTextField
                id="end-date"
                label="End Date"
                type="date"
                value={values.endDate}
                onChange={handleChange('endDate')}
                onBlur={handleBlur('endDate')}
                validationState={touched.endDate ? (errors.endDate ? 'error' : 'success') : null}
                validationMessage={touched.endDate ? errors.endDate : undefined}
                required
                fullWidth
                loading={isSubmitting}
                InputLabelProps={{
                  shrink: true,
                }}
                helpText="Policy coverage end date (must be after start date)"
              />
            </Grid>
          </Grid>
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

export default PolicyForm;