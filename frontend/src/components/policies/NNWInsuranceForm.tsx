import React from 'react';
import {
  Grid,
  TextField,
  Typography,
  Box,
  InputAdornment,
  Chip,
} from '@mui/material';
import { Euro, LocalHospital, Person } from '@mui/icons-material';
import { PolicyDetails } from '../../types/policy';

interface FormErrors {
  sumInsured?: string;
  coveredPersons?: string;
}

interface NNWInsuranceFormProps {
  policyDetails: PolicyDetails;
  onChange: (details: PolicyDetails) => void;
  errors?: FormErrors;
}

const NNWInsuranceForm: React.FC<NNWInsuranceFormProps> = ({
  policyDetails,
  onChange,
  errors = {},
}) => {
  const handleChange = (field: keyof PolicyDetails) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const value = event.target.value;
    let processedValue: any = value;
    
    if (field === 'sumInsured') {
      processedValue = value === '' ? undefined : Number(value);
    }
    
    onChange({
      ...policyDetails,
      [field]: processedValue,
    });
  };

  const getCoveredPersonsOptions = () => [
    'Driver only',
    'Driver and passengers',
    'All vehicle occupants',
    'Named individuals',
    'Family members',
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
        <LocalHospital color="primary" />
        <Typography variant="h6" color="primary">
          NNW Insurance Details
        </Typography>
      </Box>
      
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Accidents (NNW) insurance provides personal accident coverage for death, permanent damage, 
        and medical costs resulting from vehicle accidents.
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <TextField
            label="Sum Insured"
            type="number"
            value={policyDetails.sumInsured || ''}
            onChange={handleChange('sumInsured')}
            error={Boolean(errors.sumInsured)}
            helperText={errors.sumInsured || 'Maximum amount covered for accident claims'}
            required
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

        <Grid item xs={12} md={6}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
            <Person />
            <Typography variant="body2" fontWeight="medium">
              Coverage Distribution
            </Typography>
          </Box>
          <Box sx={{ p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Typography variant="body2" color="text.secondary">
              <strong>Death:</strong> 50% of sum insured<br />
              <strong>Permanent damage:</strong> Up to 100% based on severity<br />
              <strong>Medical costs:</strong> Up to 20% of sum insured
            </Typography>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <TextField
            label="Covered Persons"
            multiline
            rows={4}
            value={policyDetails.coveredPersons || ''}
            onChange={handleChange('coveredPersons')}
            error={Boolean(errors.coveredPersons)}
            helperText={errors.coveredPersons || 'Specify who is covered by this accident insurance'}
            required
            fullWidth
            placeholder="Describe who is covered by this NNW policy (e.g., driver and all passengers, specific named individuals, etc.)"
          />
          
          <Box sx={{ mt: 1 }}>
            <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: 'block' }}>
              Common coverage options:
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {getCoveredPersonsOptions().map((option) => (
                <Chip
                  key={option}
                  label={option}
                  size="small"
                  variant="outlined"
                  onClick={() => {
                    const currentPersons = policyDetails.coveredPersons || '';
                    const newPersons = currentPersons 
                      ? `${currentPersons}, ${option}`
                      : option;
                    onChange({
                      ...policyDetails,
                      coveredPersons: newPersons,
                    });
                  }}
                  sx={{ cursor: 'pointer' }}
                />
              ))}
            </Box>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ p: 2, bgcolor: 'warning.light', borderRadius: 1 }}>
            <Typography variant="body2" color="warning.contrastText">
              <strong>Important:</strong> NNW insurance covers accidents that occur while the vehicle is in use.
              Coverage includes medical expenses, permanent disability compensation, and death benefits for
              the specified covered persons.
            </Typography>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ p: 2, bgcolor: 'info.light', borderRadius: 1 }}>
            <Typography variant="body2" color="info.contrastText">
              <strong>NNW Insurance Coverage:</strong> This policy provides personal accident coverage with
              benefits distributed as follows: 50% for death, up to 100% for permanent damage (based on severity),
              and up to 20% for medical costs. Coverage applies to all specified persons when using the insured vehicle.
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default NNWInsuranceForm;