import React, { useState } from 'react';
import { 
  Box, 
  Typography, 
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper
} from '@mui/material';
import { ExpandMore } from '@mui/icons-material';
import { Policy } from '../../types/policy';

interface PolicyDebugPanelProps {
  policies: Policy[];
  refreshVersion: number;
}

const PolicyDebugPanel: React.FC<PolicyDebugPanelProps> = ({ policies, refreshVersion }) => {
  const [expanded, setExpanded] = useState(false);

  const testPolicy = policies.find(p => p.policyNumber === 'AC-2024-002001');

  return (
    <Accordion 
      expanded={expanded} 
      onChange={() => setExpanded(!expanded)}
      sx={{ mb: 2 }}
    >
      <AccordionSummary expandIcon={<ExpandMore />}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="h6">
            Policy Debug Panel
          </Typography>
          <Chip 
            label={`Version: ${refreshVersion}`} 
            color="primary" 
            size="small" 
          />
          <Chip 
            label={`Count: ${policies.length}`} 
            color="secondary" 
            size="small" 
          />
          <Chip 
            label={`Updated: ${new Date().toLocaleTimeString()}`} 
            color="info" 
            size="small" 
          />
        </Box>
      </AccordionSummary>
      
      <AccordionDetails>
        {testPolicy && (
          <Box sx={{ mb: 3 }}>
            <Typography variant="subtitle1" gutterBottom>
              Test Policy (AC-2024-002001) Current State:
            </Typography>
            <TableContainer component={Paper} sx={{ mb: 2 }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Field</TableCell>
                    <TableCell>Value</TableCell>
                    <TableCell>Type</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>{testPolicy.id}</TableCell>
                    <TableCell>{typeof testPolicy.id}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Policy Number</TableCell>
                    <TableCell>{testPolicy.policyNumber}</TableCell>
                    <TableCell>{typeof testPolicy.policyNumber}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Discount/Surcharge</TableCell>
                    <TableCell>{testPolicy.discountSurcharge ?? 'null'}</TableCell>
                    <TableCell>{typeof testPolicy.discountSurcharge}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Amount Guaranteed</TableCell>
                    <TableCell>{testPolicy.amountGuaranteed ?? 'null'}</TableCell>
                    <TableCell>{typeof testPolicy.amountGuaranteed}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Coverage Area</TableCell>
                    <TableCell>{testPolicy.coverageArea ?? 'null'}</TableCell>
                    <TableCell>{typeof testPolicy.coverageArea}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Start Date</TableCell>
                    <TableCell>{testPolicy.startDate}</TableCell>
                    <TableCell>{typeof testPolicy.startDate}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>End Date</TableCell>
                    <TableCell>{testPolicy.endDate}</TableCell>
                    <TableCell>{typeof testPolicy.endDate}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell>Status</TableCell>
                    <TableCell>{testPolicy.status}</TableCell>
                    <TableCell>{typeof testPolicy.status}</TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}

        <Typography variant="subtitle1" gutterBottom>
          All Policies Summary:
        </Typography>
        <TableContainer component={Paper}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Policy Number</TableCell>
                <TableCell>Discount</TableCell>
                <TableCell>Amount</TableCell>
                <TableCell>Coverage</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {policies.map((policy) => (
                <TableRow 
                  key={policy.id}
                  sx={{ 
                    backgroundColor: policy.policyNumber === 'AC-2024-002001' ? 'yellow.50' : 'inherit' 
                  }}
                >
                  <TableCell>
                    {policy.policyNumber}
                    {policy.policyNumber === 'AC-2024-002001' && (
                      <Chip label="TEST" size="small" color="warning" sx={{ ml: 1 }} />
                    )}
                  </TableCell>
                  <TableCell>{policy.discountSurcharge ?? 'null'}</TableCell>
                  <TableCell>{policy.amountGuaranteed ?? 'null'}</TableCell>
                  <TableCell>{policy.coverageArea ?? 'null'}</TableCell>
                  <TableCell>{policy.status}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <Box sx={{ mt: 2, p: 1, bgcolor: 'grey.100', borderRadius: 1 }}>
          <Typography variant="caption" color="text.secondary">
            Debug Info: Refresh Version {refreshVersion} | 
            Last Render: {new Date().toLocaleTimeString()} | 
            Policies Array Length: {policies.length}
          </Typography>
        </Box>
      </AccordionDetails>
    </Accordion>
  );
};

export default PolicyDebugPanel;