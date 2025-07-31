import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
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
import { Policy, CreatePolicyRequest, UpdatePolicyRequest, Client, Vehicle } from '../../types/policy';

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
}

interface FormErrors {
  clientId?: string;
  vehicleId?: string;
  insuranceType?: string;
  startDate?: string;
  endDate?: string;
  discountSurcharge?: string;
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
  
  const [formData, setFormData] = useState<FormData>({
    clientId: '',
    vehicleId: '',
    insuranceType: '',
    startDate: '',
    endDate: '',
    discountSurcharge: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});

  // Initialize form data when policy prop changes
  useEffect(() => {
    if (policy) {
      // Find client and vehicle IDs based on names (this is a simplified approach)
      const client = clients.find(c => c.fullName === policy.clientName);
      const vehicle = vehicles.find(v => v.registrationNumber === policy.vehicleRegistration);
      
      setFormData({
        clientId: client?.id || '',
        vehicleId: vehicle?.id || '',
        insuranceType: policy.insuranceType,
        startDate: policy.startDate,
        endDate: policy.endDate,
        discountSurcharge: '',
      });
    } else {
      // Set default dates for new policies
      const today = new Date();
      const nextYear = new Date(today);
      nextYear.setFullYear(today.getFullYear() + 1);
      
      setFormData({
        clientId: '',
        vehicleId: '',
        insuranceType: '',
        startDate: today.toISOString().split('T')[0],
        endDate: nextYear.toISOString().split('T')[0],
        discountSurcharge: '',
      });
    }
    setErrors({});
    setSubmitError(null);
  }, [policy, open, clients, vehicles]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    // Client validation
    if (!formData.clientId) {
      newErrors.clientId = 'Client is required';
    }

    // Vehicle validation
    if (!formData.vehicleId) {
      newErrors.vehicleId = 'Vehicle is required';
    }

    // Insurance type validation
    if (!formData.insuranceType) {
      newErrors.insuranceType = 'Insurance type is required';
    }

    // Start date validation
    if (!formData.startDate) {
      newErrors.startDate = 'Start date is required';
    }

    // End date validation
    if (!formData.endDate) {
      newErrors.endDate = 'End date is required';
    } else if (formData.startDate && new Date(formData.endDate) <= new Date(formData.startDate)) {
      newErrors.endDate = 'End date must be after start date';
    }

    // Discount/surcharge validation
    if (formData.discountSurcharge !== '' && (isNaN(Number(formData.discountSurcharge)) || formData.discountSurcharge === null)) {
      newErrors.discountSurcharge = 'Must be a valid number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field: keyof FormData) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const value = event.target.value;
    let processedValue: any = value;
    
    if (field === 'discountSurcharge') {
      processedValue = value === '' ? '' : value;
    }
    
    setFormData(prev => ({ 
      ...prev, 
      [field]: processedValue
    }));
    
    // Clear error for this field when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSelectChange = (field: keyof FormData) => (event: any) => {
    const value = event.target.value;
    setFormData(prev => ({ ...prev, [field]: value }));
    
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSubmitError(null);

    if (!validateForm()) {
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
      };

      await onSubmit(policyData);
      onClose();
    } catch (error: any) {
      setSubmitError(error.response?.data?.message || 'An error occurred while saving the policy');
    }
  };

  const handleClose = () => {
    if (!loading) {
      onClose();
    }
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

  const selectedClient = clients.find(c => c.id === formData.clientId);
  const selectedVehicle = vehicles.find(v => v.id === formData.vehicleId);

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
            <Alert severity="error" sx={{ mb: 2 }}>
              {submitError}
            </Alert>
          )}

          <Grid container spacing={3}>
            {/* Client Selection */}
            <Grid item xs={12} md={6}>
              <FormControl fullWidth error={Boolean(errors.clientId)}>
                <InputLabel id="client-select-label">Client</InputLabel>
                <Select
                  labelId="client-select-label"
                  value={formData.clientId}
                  label="Client"
                  onChange={handleSelectChange('clientId')}
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
                {errors.clientId && (
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
              <FormControl fullWidth error={Boolean(errors.vehicleId)}>
                <InputLabel id="vehicle-select-label">Vehicle</InputLabel>
                <Select
                  labelId="vehicle-select-label"
                  value={formData.vehicleId}
                  label="Vehicle"
                  onChange={handleSelectChange('vehicleId')}
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
                {errors.vehicleId && (
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
              <FormControl fullWidth error={Boolean(errors.insuranceType)}>
                <InputLabel id="insurance-type-label">Insurance Type</InputLabel>
                <Select
                  labelId="insurance-type-label"
                  value={formData.insuranceType}
                  label="Insurance Type"
                  onChange={handleSelectChange('insuranceType')}
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
                {errors.insuranceType && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                    {errors.insuranceType}
                  </Typography>
                )}
              </FormControl>
              
              {formData.insuranceType && (
                <Box sx={{ mt: 1, p: 1, bgcolor: 'info.light', color: 'info.contrastText', borderRadius: 1 }}>
                  <Typography variant="caption">
                    {getInsuranceTypeDescription(formData.insuranceType)}
                  </Typography>
                </Box>
              )}
            </Grid>

            {/* Discount/Surcharge */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Discount/Surcharge"
                type="number"
                value={formData.discountSurcharge}
                onChange={handleInputChange('discountSurcharge')}
                error={Boolean(errors.discountSurcharge)}
                helperText={errors.discountSurcharge || 'Negative for discount, positive for surcharge'}
                fullWidth
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Euro />
                    </InputAdornment>
                  ),
                }}
              />
            </Grid>

            {/* Policy Period */}
            <Grid item xs={12} md={6}>
              <TextField
                id="start-date"
                label="Start Date"
                type="date"
                value={formData.startDate}
                onChange={handleInputChange('startDate')}
                error={Boolean(errors.startDate)}
                helperText={errors.startDate}
                required
                fullWidth
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                id="end-date"
                label="End Date"
                type="date"
                value={formData.endDate}
                onChange={handleInputChange('endDate')}
                error={Boolean(errors.endDate)}
                helperText={errors.endDate}
                required
                fullWidth
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions sx={{ p: 3, pt: 2 }}>
          <Button 
            onClick={handleClose} 
            disabled={loading}
            color="inherit"
          >
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading}
            sx={{ minWidth: 100 }}
          >
            {loading ? 'Saving...' : (isEditing ? 'Update' : 'Create')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default PolicyForm;