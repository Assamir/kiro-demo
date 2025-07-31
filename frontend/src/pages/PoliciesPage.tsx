import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Alert,
  Snackbar,
} from '@mui/material';
import { Add } from '@mui/icons-material';
import { Policy, CreatePolicyRequest, UpdatePolicyRequest, Client, Vehicle } from '../types/policy';
import { policyService } from '../services/policyService';
import PolicyList from '../components/policies/PolicyList';
import PolicyForm from '../components/policies/PolicyForm';
import CancelPolicyDialog from '../components/policies/CancelPolicyDialog';
import PdfPreviewModal from '../components/policies/PdfPreviewModal';

const PoliciesPage: React.FC = () => {
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [pdfPreviewOpen, setPdfPreviewOpen] = useState(false);
  const [selectedPolicy, setSelectedPolicy] = useState<Policy | null>(null);
  const [formLoading, setFormLoading] = useState(false);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [notification, setNotification] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error' | 'info';
  }>({
    open: false,
    message: '',
    severity: 'success',
  });

  // Load initial data
  useEffect(() => {
    loadData();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const loadData = async () => {
    try {
      setLoading(true);
      const [policiesData, clientsData, vehiclesData] = await Promise.all([
        policyService.getAllPolicies(),
        policyService.getAllClients(),
        policyService.getAllVehicles(),
      ]);
      
      setPolicies(policiesData);
      setClients(clientsData);
      setVehicles(vehiclesData);
    } catch (error: any) {
      showNotification('Failed to load data: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message: string, severity: 'success' | 'error' | 'info') => {
    setNotification({ open: true, message, severity });
  };

  const handleCreatePolicy = () => {
    setSelectedPolicy(null);
    setFormOpen(true);
  };

  const handleEditPolicy = (policy: Policy) => {
    setSelectedPolicy(policy);
    setFormOpen(true);
  };

  const handleCancelPolicy = (policy: Policy) => {
    setSelectedPolicy(policy);
    setCancelDialogOpen(true);
  };

  const handleGeneratePdf = (policy: Policy) => {
    setSelectedPolicy(policy);
    setPdfPreviewOpen(true);
  };

  const handleFormSubmit = async (policyData: CreatePolicyRequest | UpdatePolicyRequest) => {
    try {
      setFormLoading(true);
      
      if (selectedPolicy) {
        // Update existing policy
        const updatedPolicy = await policyService.updatePolicy(selectedPolicy.id, policyData as UpdatePolicyRequest);
        setPolicies(prev => prev.map(p => p.id === selectedPolicy.id ? updatedPolicy : p));
        showNotification('Policy updated successfully', 'success');
      } else {
        // Create new policy
        const newPolicy = await policyService.createPolicy(policyData as CreatePolicyRequest);
        setPolicies(prev => [...prev, newPolicy]);
        showNotification('Policy created successfully', 'success');
      }
    } catch (error: any) {
      throw error; // Re-throw to be handled by the form component
    } finally {
      setFormLoading(false);
    }
  };

  const handleCancelConfirm = async () => {
    if (!selectedPolicy) return;
    
    try {
      setCancelLoading(true);
      await policyService.cancelPolicy(selectedPolicy.id);
      
      // Update the policy status in the local state
      setPolicies(prev => prev.map(p => 
        p.id === selectedPolicy.id 
          ? { ...p, status: 'CANCELED' as const }
          : p
      ));
      
      showNotification('Policy canceled successfully', 'success');
    } catch (error: any) {
      showNotification('Failed to cancel policy: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setCancelLoading(false);
    }
  };

  const handleCloseNotification = () => {
    setNotification(prev => ({ ...prev, open: false }));
  };

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" gutterBottom>
            Policy Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Create, edit, and manage insurance policies.
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleCreatePolicy}
          disabled={loading}
        >
          Create Policy
        </Button>
      </Box>

      {/* Policy List */}
      <PolicyList
        policies={policies}
        loading={loading}
        onEditPolicy={handleEditPolicy}
        onCancelPolicy={handleCancelPolicy}
        onGeneratePdf={handleGeneratePdf}
      />

      {/* Policy Form Dialog */}
      <PolicyForm
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSubmit={handleFormSubmit}
        policy={selectedPolicy}
        clients={clients}
        vehicles={vehicles}
        loading={formLoading}
      />

      {/* Cancel Policy Dialog */}
      <CancelPolicyDialog
        open={cancelDialogOpen}
        onClose={() => setCancelDialogOpen(false)}
        onConfirm={handleCancelConfirm}
        policy={selectedPolicy}
        loading={cancelLoading}
      />

      {/* PDF Preview Modal */}
      <PdfPreviewModal
        open={pdfPreviewOpen}
        onClose={() => setPdfPreviewOpen(false)}
        policy={selectedPolicy}
      />

      {/* Notification Snackbar */}
      <Snackbar
        open={notification.open}
        autoHideDuration={6000}
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={handleCloseNotification} 
          severity={notification.severity}
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default PoliciesPage;