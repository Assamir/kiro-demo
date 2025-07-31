import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Divider,
  Chip,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import { Euro, TrendingUp, Assessment } from '@mui/icons-material';
import { PremiumBreakdown } from '../../types/policy';
import ratingService from '../../services/ratingService';

interface PremiumCalculationDisplayProps {
  insuranceType: 'OC' | 'AC' | 'NNW';
  vehicleId: number | '';
  policyDate: string;
  discountSurcharge?: number;
}

const PremiumCalculationDisplay: React.FC<PremiumCalculationDisplayProps> = ({
  insuranceType,
  vehicleId,
  policyDate,
  discountSurcharge = 0,
}) => {
  const [breakdown, setBreakdown] = useState<PremiumBreakdown | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (insuranceType && vehicleId && policyDate) {
      calculatePremium();
    } else {
      setBreakdown(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [insuranceType, vehicleId, policyDate]);

  const calculatePremium = async () => {
    if (!vehicleId || typeof vehicleId !== 'number') return;
    
    setLoading(true);
    setError(null);
    
    try {
      const result = await ratingService.calculatePremiumBreakdown({
        insuranceType,
        vehicleId: Number(vehicleId),
        policyDate,
      });
      setBreakdown(result);
    } catch (err: any) {
      setError(err.message || 'Failed to calculate premium');
      setBreakdown(null);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('pl-PL', {
      style: 'currency',
      currency: 'PLN',
    }).format(amount);
  };

  const getFinalPremium = () => {
    if (!breakdown) return 0;
    return breakdown.finalPremium + (discountSurcharge || 0);
  };

  const getRatingFactorDescription = (key: string) => {
    const descriptions: Record<string, string> = {
      VEHICLE_AGE: 'Vehicle Age Factor',
      ENGINE_CAPACITY: 'Engine Capacity Factor',
      POWER: 'Power Factor',
      OC_COVERAGE: 'OC Coverage Factor',
      AC_COVERAGE: 'AC Coverage Factor',
      NNW_COVERAGE: 'NNW Coverage Factor',
    };
    return descriptions[key] || key;
  };

  const getRatingFactorColor = (multiplier: number) => {
    if (multiplier > 1.1) return 'error';
    if (multiplier > 1.0) return 'warning';
    if (multiplier < 0.9) return 'success';
    return 'default';
  };

  if (!insuranceType || !vehicleId || !policyDate || typeof vehicleId !== 'number') {
    return (
      <Card sx={{ mt: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
            <Euro color="disabled" />
            <Typography variant="h6" color="text.secondary">
              Premium Calculation
            </Typography>
          </Box>
          <Typography variant="body2" color="text.secondary">
            Complete the insurance type, vehicle, and policy date to see premium calculation.
          </Typography>
        </CardContent>
      </Card>
    );
  }

  if (loading) {
    return (
      <Card sx={{ mt: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <CircularProgress size={24} />
            <Typography variant="body2">Calculating premium...</Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card sx={{ mt: 2 }}>
        <CardContent>
          <Alert severity="error">{error}</Alert>
        </CardContent>
      </Card>
    );
  }

  if (!breakdown) {
    return null;
  }

  return (
    <Card sx={{ mt: 2 }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
          <Assessment color="primary" />
          <Typography variant="h6" color="primary">
            Premium Calculation
          </Typography>
        </Box>

        {/* Base Premium */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Base Premium ({insuranceType} Insurance)
          </Typography>
          <Typography variant="h6">
            {formatCurrency(breakdown.basePremium)}
          </Typography>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* Rating Factors */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Rating Factors
          </Typography>
          
          <TableContainer component={Paper} variant="outlined" sx={{ mt: 1 }}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Factor</TableCell>
                  <TableCell align="right">Multiplier</TableCell>
                  <TableCell align="right">Impact</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {Object.entries(breakdown.ratingFactors).map(([key, multiplier]) => (
                  <TableRow key={key}>
                    <TableCell>
                      <Typography variant="body2">
                        {getRatingFactorDescription(key)}
                      </Typography>
                    </TableCell>
                    <TableCell align="right">
                      <Chip
                        label={`×${multiplier.toFixed(3)}`}
                        size="small"
                        color={getRatingFactorColor(multiplier)}
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Typography 
                        variant="body2" 
                        color={multiplier > 1 ? 'error.main' : 'success.main'}
                      >
                        {multiplier > 1 ? '+' : ''}{((multiplier - 1) * 100).toFixed(1)}%
                      </Typography>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* Calculated Premium */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Calculated Premium (after rating factors)
          </Typography>
          <Typography variant="h6">
            {formatCurrency(breakdown.finalPremium)}
          </Typography>
        </Box>

        {/* Discount/Surcharge */}
        {discountSurcharge !== 0 && (
          <>
            <Box sx={{ mb: 2 }}>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {discountSurcharge > 0 ? 'Surcharge' : 'Discount'}
              </Typography>
              <Typography 
                variant="body1" 
                color={discountSurcharge > 0 ? 'error.main' : 'success.main'}
              >
                {discountSurcharge > 0 ? '+' : ''}{formatCurrency(discountSurcharge)}
              </Typography>
            </Box>
            <Divider sx={{ my: 2 }} />
          </>
        )}

        {/* Final Premium */}
        <Box sx={{ 
          p: 2, 
          bgcolor: 'primary.light', 
          borderRadius: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <TrendingUp />
            <Typography variant="h6" color="primary.contrastText">
              Final Premium
            </Typography>
          </Box>
          <Typography variant="h5" color="primary.contrastText" fontWeight="bold">
            {formatCurrency(getFinalPremium())}
          </Typography>
        </Box>

        <Box sx={{ mt: 2 }}>
          <Typography variant="caption" color="text.secondary">
            * Premium calculation is based on current rating tables and vehicle characteristics.
            Final premium may be subject to additional underwriting adjustments.
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

export default PremiumCalculationDisplay;