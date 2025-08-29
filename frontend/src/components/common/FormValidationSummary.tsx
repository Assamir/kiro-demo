import React from 'react';
import {
  Alert,
  AlertTitle,
  Box,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
  Collapse,
  IconButton,
} from '@mui/material';
import {
  Error,
  Warning,
  Info,
  CheckCircle,
  ExpandMore,
  ExpandLess,
} from '@mui/icons-material';

interface ValidationIssue {
  field: string;
  message: string;
  severity: 'error' | 'warning' | 'info';
}

interface FormValidationSummaryProps {
  errors: Record<string, string>;
  warnings?: Record<string, string>;
  infos?: Record<string, string>;
  touched: Record<string, boolean>;
  showOnlyTouched?: boolean;
  collapsible?: boolean;
  title?: string;
  maxHeight?: number;
}

const FormValidationSummary: React.FC<FormValidationSummaryProps> = ({
  errors = {},
  warnings = {},
  infos = {},
  touched = {},
  showOnlyTouched = true,
  collapsible = false,
  title = 'Form Validation',
  maxHeight = 200,
}) => {
  const [expanded, setExpanded] = React.useState(!collapsible);

  // Collect all validation issues
  const issues: ValidationIssue[] = [];

  // Add errors
  Object.entries(errors).forEach(([field, message]) => {
    if (!showOnlyTouched || touched[field]) {
      issues.push({ field, message, severity: 'error' });
    }
  });

  // Add warnings
  Object.entries(warnings).forEach(([field, message]) => {
    if (!showOnlyTouched || touched[field]) {
      issues.push({ field, message, severity: 'warning' });
    }
  });

  // Add infos
  Object.entries(infos).forEach(([field, message]) => {
    if (!showOnlyTouched || touched[field]) {
      issues.push({ field, message, severity: 'info' });
    }
  });

  // Don't render if no issues
  if (issues.length === 0) {
    return null;
  }

  // Group issues by severity
  const errorIssues = issues.filter(issue => issue.severity === 'error');
  const warningIssues = issues.filter(issue => issue.severity === 'warning');
  const infoIssues = issues.filter(issue => issue.severity === 'info');

  // Determine overall severity
  const overallSeverity = errorIssues.length > 0 ? 'error' : 
                         warningIssues.length > 0 ? 'warning' : 'info';

  const getIcon = (severity: 'error' | 'warning' | 'info') => {
    switch (severity) {
      case 'error':
        return <Error fontSize="small" />;
      case 'warning':
        return <Warning fontSize="small" />;
      case 'info':
        return <Info fontSize="small" />;
      default:
        return <Info fontSize="small" />;
    }
  };

  const formatFieldName = (field: string): string => {
    // Convert camelCase to readable format
    return field
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase())
      .trim();
  };

  const renderIssueList = (issueList: ValidationIssue[], severity: 'error' | 'warning' | 'info') => {
    if (issueList.length === 0) return null;

    return (
      <Box sx={{ mb: 1 }}>
        <Typography variant="subtitle2" color={`${severity}.main`} sx={{ mb: 0.5 }}>
          {severity === 'error' ? 'Errors' : severity === 'warning' ? 'Warnings' : 'Information'}
          {` (${issueList.length})`}
        </Typography>
        <List dense sx={{ py: 0 }}>
          {issueList.map((issue, index) => (
            <ListItem key={`${issue.field}-${index}`} sx={{ py: 0.25, px: 0 }}>
              <ListItemIcon sx={{ minWidth: 32 }}>
                {getIcon(issue.severity)}
              </ListItemIcon>
              <ListItemText
                primary={
                  <Typography variant="body2">
                    <strong>{formatFieldName(issue.field)}:</strong> {issue.message}
                  </Typography>
                }
              />
            </ListItem>
          ))}
        </List>
      </Box>
    );
  };

  return (
    <Alert 
      severity={overallSeverity}
      sx={{ mb: 2 }}
      action={
        collapsible ? (
          <IconButton
            aria-label="toggle validation details"
            color="inherit"
            size="small"
            onClick={() => setExpanded(!expanded)}
          >
            {expanded ? <ExpandLess /> : <ExpandMore />}
          </IconButton>
        ) : undefined
      }
    >
      <AlertTitle>
        {title} - {issues.length} issue{issues.length !== 1 ? 's' : ''} found
      </AlertTitle>
      
      <Collapse in={expanded}>
        <Box sx={{ maxHeight, overflow: 'auto' }}>
          {renderIssueList(errorIssues, 'error')}
          {renderIssueList(warningIssues, 'warning')}
          {renderIssueList(infoIssues, 'info')}
        </Box>
      </Collapse>
    </Alert>
  );
};

export default FormValidationSummary;