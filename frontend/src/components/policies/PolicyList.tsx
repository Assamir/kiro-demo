import React, { useState, useMemo } from 'react';
import {
  Box,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  Chip,
  Typography,
  Paper,
  InputAdornment,
  Tooltip,
} from '@mui/material';
import {
  Search,
  Edit,
  Cancel,
  FilterList,
  PictureAsPdf,
} from '@mui/icons-material';
import { Policy, PolicySearchFilters } from '../../types/policy';

interface PolicyListProps {
  policies: Policy[];
  loading?: boolean;
  onEditPolicy: (policy: Policy) => void;
  onCancelPolicy: (policy: Policy) => void;
  onGeneratePdf: (policy: Policy) => void;
}

const PolicyList: React.FC<PolicyListProps> = ({
  policies,
  loading = false,
  onEditPolicy,
  onCancelPolicy,
  onGeneratePdf,
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState<PolicySearchFilters>({
    insuranceType: 'ALL',
    status: 'ALL',
  });

  // Filter and search policies
  const filteredPolicies = useMemo(() => {
    return policies.filter((policy) => {
      const matchesSearch = 
        policy.clientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        policy.policyNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
        policy.vehicleRegistration.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesInsuranceType = filters.insuranceType === 'ALL' || policy.insuranceType === filters.insuranceType;
      const matchesStatus = filters.status === 'ALL' || policy.status === filters.status;
      
      return matchesSearch && matchesInsuranceType && matchesStatus;
    });
  }, [policies, searchTerm, filters]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'CANCELED':
        return 'error';
      case 'EXPIRED':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getInsuranceTypeColor = (type: string) => {
    switch (type) {
      case 'OC':
        return 'primary';
      case 'AC':
        return 'secondary';
      case 'NNW':
        return 'info';
      default:
        return 'default';
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

  if (loading) {
    return (
      <Card>
        <CardContent>
          <Typography>Loading policies...</Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        {/* Search and Filter Controls */}
        <Box sx={{ mb: 3, display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
          <TextField
            placeholder="Search by client name, policy number, or vehicle registration..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
            sx={{ flexGrow: 1, minWidth: 300 }}
          />
          
          <FormControl sx={{ minWidth: 140 }}>
            <InputLabel id="insurance-type-filter-label">Insurance Type</InputLabel>
            <Select
              labelId="insurance-type-filter-label"
              value={filters.insuranceType}
              label="Insurance Type"
              onChange={(e) => setFilters(prev => ({ ...prev, insuranceType: e.target.value as any }))}
              startAdornment={<FilterList sx={{ mr: 1 }} />}
            >
              <MenuItem value="ALL">All Types</MenuItem>
              <MenuItem value="OC">OC</MenuItem>
              <MenuItem value="AC">AC</MenuItem>
              <MenuItem value="NNW">NNW</MenuItem>
            </Select>
          </FormControl>

          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel id="status-filter-label">Status</InputLabel>
            <Select
              labelId="status-filter-label"
              value={filters.status}
              label="Status"
              onChange={(e) => setFilters(prev => ({ ...prev, status: e.target.value as any }))}
              startAdornment={<FilterList sx={{ mr: 1 }} />}
            >
              <MenuItem value="ALL">All Status</MenuItem>
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="CANCELED">Canceled</MenuItem>
              <MenuItem value="EXPIRED">Expired</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* Results Summary */}
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Showing {filteredPolicies.length} of {policies.length} policies
        </Typography>

        {/* Policies Table */}
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Policy Number</TableCell>
                <TableCell>Client</TableCell>
                <TableCell>Vehicle</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Period</TableCell>
                <TableCell>Premium</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredPolicies.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={8} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">
                      {searchTerm || filters.insuranceType !== 'ALL' || filters.status !== 'ALL'
                        ? 'No policies match your search criteria' 
                        : 'No policies found'}
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                filteredPolicies.map((policy) => (
                  <TableRow key={policy.id} hover>
                    <TableCell>
                      <Typography variant="body2" fontWeight="medium">
                        {policy.policyNumber}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {policy.clientName}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {policy.vehicleRegistration}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={policy.insuranceType}
                        color={getInsuranceTypeColor(policy.insuranceType) as any}
                        size="small"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {formatDate(policy.startDate)} - {formatDate(policy.endDate)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontWeight="medium">
                        {formatCurrency(policy.premium)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={policy.status}
                        color={getStatusColor(policy.status) as any}
                        size="small"
                        variant="filled"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Tooltip title="Generate PDF">
                        <IconButton
                          size="small"
                          onClick={() => onGeneratePdf(policy)}
                          color="info"
                        >
                          <PictureAsPdf />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Edit policy">
                        <IconButton
                          size="small"
                          onClick={() => onEditPolicy(policy)}
                          color="primary"
                          disabled={policy.status !== 'ACTIVE'}
                        >
                          <Edit />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Cancel policy">
                        <IconButton
                          size="small"
                          onClick={() => onCancelPolicy(policy)}
                          color="error"
                          disabled={policy.status !== 'ACTIVE'}
                        >
                          <Cancel />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </Card>
  );
};

export default PolicyList;