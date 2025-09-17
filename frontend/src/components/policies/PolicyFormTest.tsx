import React, { useState } from 'react';
import { Box, Button, Typography, Alert } from '@mui/material';
import { Policy } from '../../types/policy';
import { policyService } from '../../services/policyService';

interface PolicyFormTestProps {
  policy: Policy;
  onRefresh?: () => void;
}

const PolicyFormTest: React.FC<PolicyFormTestProps> = ({ policy, onRefresh }) => {
  const [testResult, setTestResult] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const testPolicyUpdate = async () => {
    setLoading(true);
    setTestResult('');
    
    try {
      console.log('=== POLICY UPDATE TEST ===');
      console.log('Original policy:', policy);
      
      // Test data
      const testData = {
        startDate: policy.startDate,
        endDate: policy.endDate,
        discountSurcharge: 123,
        amountGuaranteed: 999000,
        coverageArea: 'Europe'
      };
      
      console.log('Sending update data:', testData);
      
      // Make the API call
      const updatedPolicy = await policyService.updatePolicy(policy.id, testData);
      
      console.log('Received updated policy:', updatedPolicy);
      
      // Check if the update was successful
      const success = 
        updatedPolicy.discountSurcharge === 123 &&
        updatedPolicy.amountGuaranteed === 999000 &&
        updatedPolicy.coverageArea === 'Europe';
      
      if (success) {
        setTestResult('✅ SUCCESS: Policy update worked correctly!');
        // Trigger refresh after successful test
        if (onRefresh) {
          console.log('Triggering refresh after successful test...');
          setTimeout(() => onRefresh(), 500);
        }
      } else {
        setTestResult(`❌ FAILED: Policy update did not return expected values.
          Expected: discountSurcharge=123, amountGuaranteed=999000, coverageArea=Europe
          Received: discountSurcharge=${updatedPolicy.discountSurcharge}, amountGuaranteed=${updatedPolicy.amountGuaranteed}, coverageArea=${updatedPolicy.coverageArea}`);
      }
      
    } catch (error: any) {
      console.error('Policy update test failed:', error);
      setTestResult(`❌ ERROR: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const testFormFields = () => {
    console.log('=== FORM FIELD TEST ===');
    
    // Test if form fields exist and are accessible
    const amountField = document.querySelector('input[name="amountGuaranteed"]') as HTMLInputElement;
    const coverageField = document.querySelector('select[name="coverageArea"]') as HTMLSelectElement;
    const discountField = document.querySelector('input[name="discountSurcharge"]') as HTMLInputElement;
    
    console.log('Amount Guaranteed field:', amountField);
    console.log('Coverage Area field:', coverageField);
    console.log('Discount/Surcharge field:', discountField);
    
    if (amountField) {
      console.log('Amount field value:', amountField.value);
      amountField.value = '555000';
      amountField.dispatchEvent(new Event('change', { bubbles: true }));
      console.log('Set amount field to 555000');
    }
    
    if (coverageField) {
      console.log('Coverage field value:', coverageField.value);
      coverageField.value = 'Worldwide';
      coverageField.dispatchEvent(new Event('change', { bubbles: true }));
      console.log('Set coverage field to Worldwide');
    }
    
    if (discountField) {
      console.log('Discount field value:', discountField.value);
      discountField.value = '250';
      discountField.dispatchEvent(new Event('change', { bubbles: true }));
      console.log('Set discount field to 250');
    }
    
    setTestResult('🔍 Form field test completed - check console for details');
  };

  const testDiscountField = async () => {
    setLoading(true);
    setTestResult('');
    
    try {
      console.log('=== DISCOUNT FIELD SPECIFIC TEST ===');
      console.log('Original policy discount:', policy.discountSurcharge);
      
      // Test discount field specifically
      const testData = {
        startDate: policy.startDate,
        endDate: policy.endDate,
        discountSurcharge: -500, // Test negative discount
        amountGuaranteed: policy.amountGuaranteed,
        coverageArea: policy.coverageArea
      };
      
      console.log('Testing discount update with data:', testData);
      
      // Make the API call
      const updatedPolicy = await policyService.updatePolicy(policy.id, testData);
      
      console.log('Received updated policy:', updatedPolicy);
      console.log('Updated discount value:', updatedPolicy.discountSurcharge);
      
      // Check if the discount update was successful
      if (updatedPolicy.discountSurcharge === -500) {
        setTestResult('✅ DISCOUNT SUCCESS: Discount field update worked correctly!');
        // Trigger refresh after successful test
        if (onRefresh) {
          console.log('Triggering refresh after successful discount test...');
          setTimeout(() => onRefresh(), 500);
        }
      } else {
        setTestResult(`❌ DISCOUNT FAILED: Expected discount=-500, got ${updatedPolicy.discountSurcharge}`);
      }
      
    } catch (error: any) {
      console.error('Discount field test failed:', error);
      setTestResult(`❌ DISCOUNT ERROR: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 2, border: '1px solid #ccc', borderRadius: 1, mb: 2 }}>
      <Typography variant="h6" gutterBottom>
        Policy Form Test - {policy.policyNumber}
      </Typography>
      
      <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
        <Button 
          variant="contained" 
          onClick={testPolicyUpdate}
          disabled={loading}
        >
          Test API Update
        </Button>
        
        <Button 
          variant="outlined" 
          onClick={testFormFields}
        >
          Test Form Fields
        </Button>
        
        <Button 
          variant="outlined" 
          color="warning"
          onClick={testDiscountField}
          disabled={loading}
        >
          Test Discount Field
        </Button>
        
        {onRefresh && (
          <Button 
            variant="outlined" 
            color="info"
            onClick={() => {
              console.log('Manual refresh triggered');
              onRefresh();
            }}
          >
            Refresh Data
          </Button>
        )}
        
        <Button 
          variant="outlined" 
          color="error"
          onClick={() => {
            console.log('Force page reload');
            window.location.reload();
          }}
        >
          Force Reload
        </Button>
        
        <Button 
          variant="outlined" 
          color="secondary"
          onClick={async () => {
            console.log('=== DIRECT API TEST ===');
            try {
              const freshPolicy = await policyService.getPolicyById(policy.id);
              console.log('Fresh policy from API:', freshPolicy);
              setTestResult(`🔍 API DIRECT: Amount=${freshPolicy.amountGuaranteed}, Coverage=${freshPolicy.coverageArea}, Discount=${freshPolicy.discountSurcharge}`);
            } catch (error: any) {
              console.error('Direct API test failed:', error);
              setTestResult(`❌ API ERROR: ${error.message}`);
            }
          }}
        >
          Check API Direct
        </Button>
      </Box>
      
      {testResult && (
        <Alert severity={testResult.includes('SUCCESS') ? 'success' : testResult.includes('ERROR') ? 'error' : 'info'}>
          <pre style={{ whiteSpace: 'pre-wrap', fontSize: '12px' }}>{testResult}</pre>
        </Alert>
      )}
      
      <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
        Current values: Amount={policy.amountGuaranteed || 'null'}, Coverage={policy.coverageArea || 'null'}, Discount={policy.discountSurcharge || 'null'}
      </Typography>
      
      <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
        Policy ID: {policy.id} | Last updated: {new Date().toLocaleTimeString()}
      </Typography>
    </Box>
  );
};

export default PolicyFormTest;