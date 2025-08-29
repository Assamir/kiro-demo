import React from 'react';
import {
  Box,
  Typography,
  LinearProgress,
  Chip,
  Stack,
  Tooltip,
  IconButton,
} from '@mui/material';
import {
  CheckCircle,
  Error,
  Warning,
  Info,
  HelpOutline,
} from '@mui/icons-material';

interface ValidationRule {
  id: string;
  label: string;
  description?: string;
  isValid: boolean;
  severity: 'error' | 'warning' | 'info' | 'success';
}

interface ValidationFeedbackProps {
  rules: ValidationRule[];
  showProgress?: boolean;
  showLabels?: boolean;
  compact?: boolean;
  title?: string;
}

const ValidationFeedback: React.FC<ValidationFeedbackProps> = ({
  rules,
  showProgress = true,
  showLabels = true,
  compact = false,
  title = 'Validation Status',
}) => {
  const validRules = rules.filter(rule => rule.isValid);
  const invalidRules = rules.filter(rule => !rule.isValid);
  const progressValue = rules.length > 0 ? (validRules.length / rules.length) * 100 : 0;

  const getIcon = (severity: ValidationRule['severity'], isValid: boolean) => {
    if (isValid) {
      return <CheckCircle color="success" fontSize="small" />;
    }

    switch (severity) {
      case 'error':
        return <Error color="error" fontSize="small" />;
      case 'warning':
        return <Warning color="warning" fontSize="small" />;
      case 'info':
        return <Info color="info" fontSize="small" />;
      default:
        return <Error color="error" fontSize="small" />;
    }
  };

  const getChipColor = (severity: ValidationRule['severity'], isValid: boolean) => {
    if (isValid) return 'success';
    
    switch (severity) {
      case 'error':
        return 'error';
      case 'warning':
        return 'warning';
      case 'info':
        return 'info';
      default:
        return 'error';
    }
  };

  const getProgressColor = () => {
    if (progressValue === 100) return 'success';
    if (progressValue >= 75) return 'info';
    if (progressValue >= 50) return 'warning';
    return 'error';
  };

  if (rules.length === 0) {
    return null;
  }

  if (compact) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        {showProgress && (
          <Box sx={{ flex: 1, mr: 1 }}>
            <LinearProgress
              variant="determinate"
              value={progressValue}
              color={getProgressColor()}
              sx={{ height: 6, borderRadius: 3 }}
            />
          </Box>
        )}
        <Typography variant="caption" color="text.secondary">
          {validRules.length}/{rules.length} valid
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ mb: 2 }}>
      {title && (
        <Typography variant="subtitle2" gutterBottom>
          {title}
        </Typography>
      )}

      {showProgress && (
        <Box sx={{ mb: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
            <Box sx={{ flex: 1, mr: 1 }}>
              <LinearProgress
                variant="determinate"
                value={progressValue}
                color={getProgressColor()}
                sx={{ height: 8, borderRadius: 4 }}
              />
            </Box>
            <Typography variant="body2" color="text.secondary">
              {Math.round(progressValue)}%
            </Typography>
          </Box>
          <Typography variant="caption" color="text.secondary">
            {validRules.length} of {rules.length} validation rules passed
          </Typography>
        </Box>
      )}

      {showLabels && (
        <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
          {rules.map((rule) => (
            <Box key={rule.id} sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
              <Chip
                icon={getIcon(rule.severity, rule.isValid)}
                label={rule.label}
                size="small"
                color={getChipColor(rule.severity, rule.isValid)}
                variant={rule.isValid ? 'filled' : 'outlined'}
              />
              {rule.description && (
                <Tooltip title={rule.description} arrow>
                  <IconButton size="small" sx={{ ml: 0.5 }}>
                    <HelpOutline fontSize="small" />
                  </IconButton>
                </Tooltip>
              )}
            </Box>
          ))}
        </Stack>
      )}

      {invalidRules.length > 0 && (
        <Box sx={{ mt: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Issues to resolve:
          </Typography>
          {invalidRules.map((rule) => (
            <Box key={rule.id} sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
              {getIcon(rule.severity, false)}
              <Typography variant="body2" sx={{ ml: 1 }}>
                {rule.label}
              </Typography>
              {rule.description && (
                <Tooltip title={rule.description} arrow>
                  <IconButton size="small" sx={{ ml: 0.5 }}>
                    <HelpOutline fontSize="small" />
                  </IconButton>
                </Tooltip>
              )}
            </Box>
          ))}
        </Box>
      )}
    </Box>
  );
};

export default ValidationFeedback;