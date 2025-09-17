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
import UISyncTest from '../components/policies/UISyncTest';
import PolicyDebugPanel from '../components/policies/PolicyDebugPanel';

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
  const [refreshVersion, setRefreshVersion] = useState(0);
  const [forceUpdateCounter, setForceUpdateCounter] = useState(0);
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

  // Keep selectedPolicy synchronized with policies array
  useEffect(() => {
    if (selectedPolicy && policies.length > 0) {
      const updatedSelectedPolicy = policies.find(p => p.id === selectedPolicy.id);
      if (updatedSelectedPolicy && JSON.stringify(updatedSelectedPolicy) !== JSON.stringify(selectedPolicy)) {
        console.log('=== SYNCING SELECTED POLICY ===');
        console.log('Old selectedPolicy:', selectedPolicy);
        console.log('New selectedPolicy from policies array:', updatedSelectedPolicy);
        setSelectedPolicy({ ...updatedSelectedPolicy });
      }
    }
  }, [policies, selectedPolicy]);

  const loadData = async () => {
    try {
      setLoading(true);
      console.log('Loading fresh data from server...');
      
      const [policiesData, clientsData, vehiclesData] = await Promise.all([
        policyService.getAllPolicies(),
        policyService.getAllClients(),
        policyService.getAllVehicles(),
      ]);
      
      console.log('Loaded policies:', policiesData);
      console.log('AC-2024-002001 policy:', policiesData.find(p => p.policyNumber === 'AC-2024-002001'));
      
      setPolicies(policiesData);
      setClients(clientsData);
      setVehicles(vehiclesData);
      setRefreshVersion(prev => prev + 1);
      
      console.log('Data loading completed, refresh version:', refreshVersion + 1);
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
        console.log('=== FORM SUBMIT START ===');
        console.log('Selected policy ID:', selectedPolicy.id);
        console.log('Updating policy with data:', policyData);
        
        const updatedPolicy = await policyService.updatePolicy(selectedPolicy.id, policyData as UpdatePolicyRequest);
        console.log('Received updated policy from API:', updatedPolicy);
        
        showNotification('Policy updated successfully', 'success');
        
        // Enhanced refresh strategy for reliable UI updates
        console.log('=== ENHANCED REFRESH STRATEGY ===');
        
        // Use the same direct update mechanism
        console.log('=== USING DIRECT UPDATE MECHANISM ===');
        handleDirectUpdate(selectedPolicy.id, updatedPolicy);
        
        // Close form immediately to show changes
        console.log('Closing form to show updated data');
        setFormOpen(false);
        
        // Backup: Server refresh after a delay to ensure consistency
        setTimeout(async () => {
          console.log('=== BACKUP SERVER REFRESH ===');
          await loadData();
          console.log('Backup server refresh completed');
        }, 2000);
        
        console.log('=== FORM SUBMIT COMPLETED ===');
      } else {
        // Create new policy
        console.log('=== CREATING NEW POLICY ===');
        const newPolicy = await policyService.createPolicy(policyData as CreatePolicyRequest);
        console.log('New policy created:', newPolicy);
        
        // Immediate state update for new policy
        setPolicies(prev => {
          const newPolicies = [...prev, { ...newPolicy }];
          console.log('Added new policy to array:', newPolicies);
          return newPolicies;
        });
        
        showNotification('Policy created successfully', 'success');
        
        // Force immediate re-render and close form
        setRefreshVersion(prev => prev + 1);
        setFormOpen(false);
      }
    } catch (error: any) {
      console.error('Form submit error:', error);
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

  const handleForceUpdate = () => {
    console.log('=== FORCE UPDATE TRIGGERED ===');
    setForceUpdateCounter(prev => prev + 1);
    setRefreshVersion(prev => prev + 1000); // Large increment to force change
    console.log('Force update completed');
  };

  const handleDirectUpdate = (policyId: number, updatedPolicy: Policy) => {
    console.log('=== DIRECT UPDATE TRIGGERED ===');
    console.log('Updating policy ID:', policyId);
    console.log('With data:', updatedPolicy);
    
    // CRITICAL FIX: Update selectedPolicy if it's the one being updated
    if (selectedPolicy && selectedPolicy.id === policyId) {
      console.log('=== UPDATING SELECTED POLICY ===');
      console.log('Old selectedPolicy:', selectedPolicy);
      const newSelectedPolicy = JSON.parse(JSON.stringify(updatedPolicy));
      console.log('New selectedPolicy:', newSelectedPolicy);
      setSelectedPolicy(newSelectedPolicy);
    }
    
    // AGGRESSIVE: Force complete state replacement
    setPolicies(prevPolicies => {
      console.log('Previous policies before direct update:', prevPolicies);
      
      // Create completely new array with new object references
      const newPolicies = prevPolicies.map(p => {
        if (p.id === policyId) {
          console.log('Found policy to update:', p);
          console.log('Replacing with:', updatedPolicy);
          // Return completely new object
          return JSON.parse(JSON.stringify(updatedPolicy));
        }
        // Return new object for all policies to force re-render
        return JSON.parse(JSON.stringify(p));
      });
      
      console.log('New policies after direct update:', newPolicies);
      console.log('Updated policy in new array:', newPolicies.find(p => p.id === policyId));
      return newPolicies;
    });
    
    // Force aggressive re-render
    setRefreshVersion(prev => {
      const newVersion = prev + 100;
      console.log('Direct update: incrementing refresh version to:', newVersion);
      return newVersion;
    });
    
    setForceUpdateCounter(prev => {
      const newCounter = prev + 100;
      console.log('Direct update: incrementing force counter to:', newCounter);
      return newCounter;
    });
    
    console.log('Direct update completed');
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

      {/* Policy Debug Panel */}
      <PolicyDebugPanel 
        key={`debug-${refreshVersion}-${forceUpdateCounter}`}
        policies={policies}
        refreshVersion={refreshVersion}
      />

      {/* UI Synchronization Test */}
      <UISyncTest 
        key={`sync-test-${refreshVersion}-${forceUpdateCounter}`}
        policies={policies}
        onRefresh={loadData}
        refreshVersion={refreshVersion}
        onForceUpdate={handleForceUpdate}
        onDirectUpdate={handleDirectUpdate}
      />

      {/* Policy List */}
      <PolicyList
        key={`policies-refresh-${refreshVersion}-force-${forceUpdateCounter}`}
        policies={policies}
        loading={loading}
        onEditPolicy={handleEditPolicy}
        onCancelPolicy={handleCancelPolicy}
        onGeneratePdf={handleGeneratePdf}
        onRefresh={loadData}
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