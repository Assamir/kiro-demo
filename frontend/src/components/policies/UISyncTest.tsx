import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Button, 
  Typography, 
  Alert, 
  Card, 
  CardContent,
  Grid,
  Chip,
  Divider
} from '@mui/material';
import { Policy } from '../../types/policy';
import { policyService } from '../../services/policyService';

interface UISyncTestProps {
  policies: Policy[];
  onRefresh?: () => void;
  refreshVersion: number;
  onForceUpdate?: () => void;
  onDirectUpdate?: (policyId: number, updatedPolicy: Policy) => void;
}

const UISyncTest: React.FC<UISyncTestProps> = ({ policies, onRefresh, refreshVersion, onForceUpdate, onDirectUpdate }) => {
  const [testResults, setTestResults] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const [serverData, setServerData] = useState<Policy[]>([]);

  // Find the test policy
  const testPolicy = policies.find(p => p.policyNumber === 'AC-2024-002001');

  useEffect(() => {
    setLastUpdate(new Date());
    addTestResult(`🔄 Component refreshed - Version: ${refreshVersion}, Time: ${new Date().toLocaleTimeString()}`);
  }, [refreshVersion, policies]);

  const addTestResult = (message: string) => {
    setTestResults(prev => [`${new Date().toLocaleTimeString()}: ${message}`, ...prev.slice(0, 9)]);
  };

  const compareUIWithServer = async () => {
    if (!testPolicy) {
      addTestResult('❌ Test policy AC-2024-002001 not found in UI');
      return;
    }

    setLoading(true);
    try {
      addTestResult('🔍 Fetching fresh data from server...');
      
      // Get fresh data from server
      const freshPolicies = await policyService.getAllPolicies();
      const freshTestPolicy = freshPolicies.find(p => p.policyNumber === 'AC-2024-002001');
      
      setServerData(freshPolicies);
      
      if (!freshTestPolicy) {
        addTestResult('❌ Test policy not found on server');
        return;
      }

      // Compare UI data with server data
      const uiDiscount = testPolicy.discountSurcharge;
      const serverDiscount = freshTestPolicy.discountSurcharge;
      const uiAmount = testPolicy.amountGuaranteed;
      const serverAmount = freshTestPolicy.amountGuaranteed;
      const uiCoverage = testPolicy.coverageArea;
      const serverCoverage = freshTestPolicy.coverageArea;

      addTestResult(`📊 UI Data: Discount=${uiDiscount}, Amount=${uiAmount}, Coverage=${uiCoverage}`);
      addTestResult(`🌐 Server Data: Discount=${serverDiscount}, Amount=${serverAmount}, Coverage=${serverCoverage}`);

      // Check for discrepancies
      const discountMatch = uiDiscount === serverDiscount;
      const amountMatch = uiAmount === serverAmount;
      const coverageMatch = uiCoverage === serverCoverage;

      if (discountMatch && amountMatch && coverageMatch) {
        addTestResult('✅ UI and Server data are synchronized!');
      } else {
        addTestResult('❌ UI and Server data are NOT synchronized:');
        if (!discountMatch) addTestResult(`  - Discount: UI=${uiDiscount} vs Server=${serverDiscount}`);
        if (!amountMatch) addTestResult(`  - Amount: UI=${uiAmount} vs Server=${serverAmount}`);
        if (!coverageMatch) addTestResult(`  - Coverage: UI=${uiCoverage} vs Server=${serverCoverage}`);
      }

    } catch (error: any) {
      addTestResult(`❌ Error comparing data: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const testUIUpdate = async () => {
    if (!testPolicy) {
      addTestResult('❌ Test policy not found');
      return;
    }

    setLoading(true);
    try {
      const testValue = Math.floor(Math.random() * 1000) * -1; // Random negative discount
      
      addTestResult(`🧪 Testing UI update with discount: ${testValue}`);
      
      // Update the policy
      const updateData = {
        startDate: testPolicy.startDate,
        endDate: testPolicy.endDate,
        discountSurcharge: testValue,
        amountGuaranteed: testPolicy.amountGuaranteed,
        coverageArea: testPolicy.coverageArea
      };

      const updatedPolicy = await policyService.updatePolicy(testPolicy.id, updateData);
      addTestResult(`✅ Server updated successfully: ${updatedPolicy.discountSurcharge}`);

      // Wait a moment then check if UI updated
      setTimeout(() => {
        const currentUIPolicy = policies.find(p => p.policyNumber === 'AC-2024-002001');
        if (currentUIPolicy && currentUIPolicy.discountSurcharge === testValue) {
          addTestResult('✅ UI updated correctly!');
        } else {
          addTestResult(`❌ UI not updated: Expected ${testValue}, got ${currentUIPolicy?.discountSurcharge}`);
          addTestResult('🔄 Triggering manual refresh...');
          if (onRefresh) {
            onRefresh();
          }
        }
      }, 1000);

    } catch (error: any) {
      addTestResult(`❌ Update test failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const testMultipleUpdates = async () => {
    if (!testPolicy) return;

    setLoading(true);
    addTestResult('🔄 Testing multiple rapid updates...');

    try {
      for (let i = 1; i <= 3; i++) {
        const testValue = -100 * i;
        addTestResult(`📝 Update ${i}: Setting discount to ${testValue}`);
        
        await policyService.updatePolicy(testPolicy.id, {
          startDate: testPolicy.startDate,
          endDate: testPolicy.endDate,
          discountSurcharge: testValue,
          amountGuaranteed: testPolicy.amountGuaranteed,
          coverageArea: testPolicy.coverageArea
        });

        // Small delay between updates
        await new Promise(resolve => setTimeout(resolve, 500));
      }

      addTestResult('✅ Multiple updates completed');
      
      // Check final state
      setTimeout(async () => {
        await compareUIWithServer();
      }, 1000);

    } catch (error: any) {
      addTestResult(`❌ Multiple update test failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const testSimpleUpdate = async () => {
    if (!testPolicy) return;

    setLoading(true);
    const testValue = Math.floor(Math.random() * 1000) * -1;
    
    try {
      addTestResult(`🧪 Simple test: Setting discount to ${testValue}`);
      
      // Direct API call
      const updatedPolicy = await policyService.updatePolicy(testPolicy.id, {
        startDate: testPolicy.startDate,
        endDate: testPolicy.endDate,
        discountSurcharge: testValue,
        amountGuaranteed: testPolicy.amountGuaranteed,
        coverageArea: testPolicy.coverageArea
      });

      addTestResult('✅ API call successful');
      
      // Try direct state update first
      if (onDirectUpdate) {
        addTestResult('🔄 Triggering direct state update...');
        onDirectUpdate(testPolicy.id, updatedPolicy);
      }
      
      // Check immediately after direct update
      setTimeout(() => {
        const currentPolicy = policies.find(p => p.policyNumber === 'AC-2024-002001');
        if (currentPolicy && currentPolicy.discountSurcharge === testValue) {
          addTestResult('✅ UI updated correctly with direct update!');
        } else {
          addTestResult(`❌ Direct update failed: Expected ${testValue}, got ${currentPolicy?.discountSurcharge}`);
          
          // Fallback to refresh
          if (onRefresh) {
            addTestResult('🔄 Fallback: Triggering refresh...');
            onRefresh();
          }
        }
      }, 500);

    } catch (error: any) {
      addTestResult(`❌ Simple test failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card sx={{ mb: 3 }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          UI Data Synchronization Test
        </Typography>
        
        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid item>
            <Chip 
              label={`Refresh Version: ${refreshVersion}`} 
              color="primary" 
              size="small" 
            />
          </Grid>
          <Grid item>
            <Chip 
              label={`Policies Count: ${policies.length}`} 
              color="secondary" 
              size="small" 
            />
          </Grid>
          <Grid item>
            <Chip 
              label={`Last Update: ${lastUpdate.toLocaleTimeString()}`} 
              color="info" 
              size="small" 
            />
          </Grid>
        </Grid>

        {testPolicy && (
          <Box sx={{ mb: 2, p: 1, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Typography variant="subtitle2">Current Test Policy (AC-2024-002001):</Typography>
            <Typography variant="body2">
              Discount: {testPolicy.discountSurcharge || 'null'} | 
              Amount: {testPolicy.amountGuaranteed || 'null'} | 
              Coverage: {testPolicy.coverageArea || 'null'}
            </Typography>
          </Box>
        )}

        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 2 }}>
          <Button 
            variant="contained" 
            onClick={compareUIWithServer}
            disabled={loading}
            size="small"
          >
            Compare UI vs Server
          </Button>
          
          <Button 
            variant="outlined" 
            onClick={testUIUpdate}
            disabled={loading}
            size="small"
          >
            Test UI Update
          </Button>
          
          <Button 
            variant="outlined" 
            onClick={testSimpleUpdate}
            disabled={loading}
            size="small"
          >
            Simple Test
          </Button>
          
          <Button 
            variant="outlined" 
            onClick={testMultipleUpdates}
            disabled={loading}
            size="small"
          >
            Test Multiple Updates
          </Button>
          
          <Button 
            variant="outlined" 
            color="info"
            onClick={() => {
              addTestResult('🔄 Manual refresh triggered');
              if (onRefresh) onRefresh();
            }}
            size="small"
          >
            Manual Refresh
          </Button>
          
          <Button 
            variant="outlined" 
            color="warning"
            onClick={() => {
              addTestResult('⚡ Force update triggered');
              if (onForceUpdate) onForceUpdate();
            }}
            size="small"
          >
            Force Update
          </Button>
          
          <Button 
            variant="outlined" 
            color="error"
            onClick={async () => {
              if (!testPolicy) return;
              
              setLoading(true);
              const testValue = Math.floor(Math.random() * 1000) * -1;
              
              try {
                addTestResult(`🚀 Nuclear test: Setting discount to ${testValue}`);
                
                // Update via API
                await policyService.updatePolicy(testPolicy.id, {
                  startDate: testPolicy.startDate,
                  endDate: testPolicy.endDate,
                  discountSurcharge: testValue,
                  amountGuaranteed: testPolicy.amountGuaranteed,
                  coverageArea: testPolicy.coverageArea
                });
                
                addTestResult('✅ API call successful');
                
                // Force complete page reload as last resort
                addTestResult('💥 Forcing complete page reload...');
                setTimeout(() => {
                  window.location.reload();
                }, 1000);
                
              } catch (error: any) {
                addTestResult(`❌ Nuclear test failed: ${error.message}`);
              } finally {
                setLoading(false);
              }
            }}
            size="small"
          >
            Nuclear Test
          </Button>
          
          <Button 
            variant="outlined" 
            color="warning"
            onClick={() => {
              setTestResults([]);
              addTestResult('🧹 Test results cleared');
            }}
            size="small"
          >
            Clear Results
          </Button>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Typography variant="subtitle2" gutterBottom>
          Test Results (Latest First):
        </Typography>
        
        <Box sx={{ maxHeight: 300, overflow: 'auto' }}>
          {testResults.length === 0 ? (
            <Typography variant="body2" color="text.secondary">
              No test results yet. Click a test button to start.
            </Typography>
          ) : (
            testResults.map((result, index) => (
              <Alert 
                key={index}
                severity={
                  result.includes('✅') ? 'success' : 
                  result.includes('❌') ? 'error' : 
                  result.includes('🔄') || result.includes('🧪') ? 'info' : 'info'
                }
                sx={{ mb: 1, fontSize: '0.8rem' }}
              >
                {result}
              </Alert>
            ))
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default UISyncTest;