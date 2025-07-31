import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Alert,
  Chip,
} from '@mui/material';
import {
  Warning,
  Policy as PolicyIcon,
} from '@mui/icons-material';
import { Policy } from '../../types/policy';

interface CancelPolicyDialogProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void>;
  policy: Policy | null;
  loading?: boolean;
}

const CancelPolicyDialog: React.FC<CancelPolicyDialogProps> = ({
  open,
  onClose,
  onConfirm,
  policy,
  loading = false,
}) => {
  const handleConfirm = async () => {
    try {
      await onConfirm();
      onClose();
    } catch (error) {
      // Error handling is done in the parent component
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('pl-PL', {
      style: 'currency',
      currency: 'PLN',
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pl-PL');
  };

  if (!policy) return null;

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="sm" 
      fullWidth
      PaperProps={{
        sx: { borderRadius: 2 }
      }}
    >
      <DialogTitle sx={{ pb: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Warning color="warning" />
          <Typography variant="h6">
            Cancel Policy
          </Typography>
        </Box>
      </DialogTitle>

      <DialogContent sx={{ pt: 2 }}>
        <Alert severity="warning" sx={{ mb: 3 }}>
          <Typography variant="body2">
            Are you sure you want to cancel this policy? This action cannot be undone.
          </Typography>
        </Alert>

        {/* Policy Details */}
        <Box sx={{ 
          p: 2, 
          bgcolor: 'grey.50', 
          borderRadius: 1, 
          border: '1px solid',
          borderColor: 'grey.200'
        }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
            <PolicyIcon color="primary" />
            <Typography variant="h6">
              {policy.policyNumber}
            </Typography>
            <Chip
              label={policy.insuranceType}
              color="primary"
              size="small"
              variant="outlined"
            />
          </Box>

          <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
            <Box>
              <Typography variant="caption" color="text.secondary">
                Client
              </Typography>
              <Typography variant="body2" fontWeight="medium">
                {policy.clientName}
              </Typography>
            </Box>

            <Box>
              <Typography variant="caption" color="text.secondary">
                Vehicle
              </Typography>
              <Typography variant="body2" fontWeight="medium">
                {policy.vehicleRegistration}
              </Typography>
            </Box>

            <Box>
              <Typography variant="caption" color="text.secondary">
                Policy Period
              </Typography>
              <Typography variant="body2">
                {formatDate(policy.startDate)} - {formatDate(policy.endDate)}
              </Typography>
            </Box>

            <Box>
              <Typography variant="caption" color="text.secondary">
                Premium
              </Typography>
              <Typography variant="body2" fontWeight="medium">
                {formatCurrency(policy.premium)}
              </Typography>
            </Box>
          </Box>
        </Box>

        <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
          Once canceled, the policy will no longer provide coverage and cannot be reactivated.
        </Typography>
      </DialogContent>

      <DialogActions sx={{ p: 3, pt: 2 }}>
        <Button 
          onClick={onClose} 
          disabled={loading}
          color="inherit"
        >
          Keep Policy
        </Button>
        <Button 
          onClick={handleConfirm}
          variant="contained" 
          color="error"
          disabled={loading}
          sx={{ minWidth: 120 }}
        >
          {loading ? 'Canceling...' : 'Cancel Policy'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CancelPolicyDialog;