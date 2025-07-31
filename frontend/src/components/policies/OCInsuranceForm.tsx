import React from 'react';
import {
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Box,
  InputAdornment,
} from '@mui/material';
import { Euro, Security } from '@mui/icons-material';
import { PolicyDetails } from '../../types/policy';

interface FormErrors {
  guaranteedSum?: string;
  coverageArea?: string;
}

interface OCInsuranceFormProps {
  policyDetails: PolicyDetails;
  onChange: (details: PolicyDetails) => void;
  errors?: FormErrors;
}

const OCInsuranceForm: React.FC<OCInsuranceFormProps> = ({
  policyDetails,
  onChange,
  errors = {},
}) => {
  const handleChange = (field: keyof PolicyDetails) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | any
  ) => {
    const value = event.target.value;
    let processedValue: any = value;
    
    if (field === 'guaranteedSum') {
      processedValue = value === '' ? undefined : Number(value);
    }
    
    onChange({
      ...policyDetails,
      [field]: processedValue,
    });
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
        <Security color="primary" />
        <Typography variant="h6" color="primary">
          OC Insurance Details
        </Typography>
      </Box>
      
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Obligatory Civil Liability insurance provides mandatory coverage as required by law.
        This insurance covers damages caused to third parties.
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <TextField
            label="Guaranteed Sum"
            type="number"
            value={policyDetails.guaranteedSum || ''}
            onChange={handleChange('guaranteedSum')}
            error={Boolean(errors.guaranteedSum)}
            helperText={errors.guaranteedSum || 'Minimum coverage amount guaranteed by the policy'}
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
          <FormControl fullWidth error={Boolean(errors.coverageArea)} required>
            <InputLabel id="coverage-area-label">Coverage Area</InputLabel>
            <Select
              labelId="coverage-area-label"
              value={policyDetails.coverageArea || ''}
              label="Coverage Area"
              onChange={handleChange('coverageArea')}
            >
              <MenuItem value="POLAND">Poland</MenuItem>
              <MenuItem value="EUROPE">Europe</MenuItem>
              <MenuItem value="WORLDWIDE">Worldwide</MenuItem>
            </Select>
            {errors.coverageArea && (
              <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                {errors.coverageArea}
              </Typography>
            )}
          </FormControl>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ p: 2, bgcolor: 'info.light', borderRadius: 1 }}>
            <Typography variant="body2" color="info.contrastText">
              <strong>OC Insurance Coverage:</strong> This policy covers damages to third parties including:
              property damage, personal injury, and legal liability. The guaranteed sum represents the minimum
              amount available for claims within the selected coverage area.
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default OCInsuranceForm;