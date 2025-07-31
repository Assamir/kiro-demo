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
  Chip,
} from '@mui/material';
import { Euro, DirectionsCar, Build } from '@mui/icons-material';
import { PolicyDetails } from '../../types/policy';

interface FormErrors {
  acVariant?: string;
  sumInsured?: string;
  coverageScope?: string;
  deductible?: string;
  workshopType?: string;
}

interface ACInsuranceFormProps {
  policyDetails: PolicyDetails;
  onChange: (details: PolicyDetails) => void;
  errors?: FormErrors;
}

const ACInsuranceForm: React.FC<ACInsuranceFormProps> = ({
  policyDetails,
  onChange,
  errors = {},
}) => {
  const handleChange = (field: keyof PolicyDetails) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | any
  ) => {
    const value = event.target.value;
    let processedValue: any = value;
    
    if (field === 'sumInsured' || field === 'deductible') {
      processedValue = value === '' ? undefined : Number(value);
    }
    
    onChange({
      ...policyDetails,
      [field]: processedValue,
    });
  };

  const getVariantDescription = (variant: string) => {
    switch (variant) {
      case 'STANDARD':
        return 'Basic coverage with standard protection options';
      case 'MAXIMUM':
        return 'Comprehensive coverage with maximum protection options';
      default:
        return '';
    }
  };

  const getCoverageScopeOptions = () => [
    'Collision and Comprehensive',
    'Theft and Vandalism',
    'Natural Disasters',
    'Glass Coverage',
    'Roadside Assistance',
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
        <DirectionsCar color="primary" />
        <Typography variant="h6" color="primary">
          AC Insurance Details
        </Typography>
      </Box>
      
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Autocasco insurance provides comprehensive vehicle coverage including collision, theft, 
        and other damages to your own vehicle.
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <FormControl fullWidth error={Boolean(errors.acVariant)} required>
            <InputLabel id="ac-variant-label">AC Variant</InputLabel>
            <Select
              labelId="ac-variant-label"
              value={policyDetails.acVariant || ''}
              label="AC Variant"
              onChange={handleChange('acVariant')}
            >
              <MenuItem value="STANDARD">
                <Box>
                  <Typography variant="body2">Standard</Typography>
                  <Typography variant="caption" color="text.secondary">
                    Basic coverage with standard protection
                  </Typography>
                </Box>
              </MenuItem>
              <MenuItem value="MAXIMUM">
                <Box>
                  <Typography variant="body2">Maximum</Typography>
                  <Typography variant="caption" color="text.secondary">
                    Comprehensive coverage with maximum protection
                  </Typography>
                </Box>
              </MenuItem>
            </Select>
            {errors.acVariant && (
              <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                {errors.acVariant}
              </Typography>
            )}
          </FormControl>
          
          {policyDetails.acVariant && (
            <Box sx={{ mt: 1, p: 1, bgcolor: 'grey.50', borderRadius: 1 }}>
              <Typography variant="caption" color="text.secondary">
                {getVariantDescription(policyDetails.acVariant)}
              </Typography>
            </Box>
          )}
        </Grid>

        <Grid item xs={12} md={6}>
          <TextField
            label="Sum Insured"
            type="number"
            value={policyDetails.sumInsured || ''}
            onChange={handleChange('sumInsured')}
            error={Boolean(errors.sumInsured)}
            helperText={errors.sumInsured || 'Maximum amount covered by the policy'}
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
          <TextField
            label="Deductible"
            type="number"
            value={policyDetails.deductible || ''}
            onChange={handleChange('deductible')}
            error={Boolean(errors.deductible)}
            helperText={errors.deductible || 'Amount you pay before insurance coverage begins'}
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
          <FormControl fullWidth error={Boolean(errors.workshopType)}>
            <InputLabel id="workshop-type-label">Workshop Type</InputLabel>
            <Select
              labelId="workshop-type-label"
              value={policyDetails.workshopType || ''}
              label="Workshop Type"
              onChange={handleChange('workshopType')}
              startAdornment={
                <InputAdornment position="start">
                  <Build />
                </InputAdornment>
              }
            >
              <MenuItem value="AUTHORIZED">Authorized Workshop</MenuItem>
              <MenuItem value="RECOMMENDED">Recommended Workshop</MenuItem>
              <MenuItem value="ANY">Any Workshop</MenuItem>
            </Select>
            {errors.workshopType && (
              <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 2 }}>
                {errors.workshopType}
              </Typography>
            )}
          </FormControl>
        </Grid>

        <Grid item xs={12}>
          <TextField
            label="Coverage Scope"
            multiline
            rows={3}
            value={policyDetails.coverageScope || ''}
            onChange={handleChange('coverageScope')}
            error={Boolean(errors.coverageScope)}
            helperText={errors.coverageScope || 'Detailed description of what is covered by this policy'}
            required
            fullWidth
            placeholder="Describe the specific coverage included in this AC policy..."
          />
          
          <Box sx={{ mt: 1 }}>
            <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: 'block' }}>
              Common coverage options:
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {getCoverageScopeOptions().map((option) => (
                <Chip
                  key={option}
                  label={option}
                  size="small"
                  variant="outlined"
                  onClick={() => {
                    const currentScope = policyDetails.coverageScope || '';
                    const newScope = currentScope 
                      ? `${currentScope}, ${option}`
                      : option;
                    onChange({
                      ...policyDetails,
                      coverageScope: newScope,
                    });
                  }}
                  sx={{ cursor: 'pointer' }}
                />
              ))}
            </Box>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ p: 2, bgcolor: 'info.light', borderRadius: 1 }}>
            <Typography variant="body2" color="info.contrastText">
              <strong>AC Insurance Coverage:</strong> This comprehensive policy covers damages to your own vehicle
              including collision, theft, vandalism, and natural disasters. The sum insured represents the maximum
              payout, while the deductible is your contribution to each claim.
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ACInsuranceForm;