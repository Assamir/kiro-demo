import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Close,
  Download,
  PictureAsPdf,
  Refresh,
} from '@mui/icons-material';
import { Policy } from '../../types/policy';
import { policyService } from '../../services/policyService';

interface PdfPreviewModalProps {
  open: boolean;
  onClose: () => void;
  policy: Policy | null;
}

const PdfPreviewModal: React.FC<PdfPreviewModalProps> = ({
  open,
  onClose,
  policy,
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [pdfBlob, setPdfBlob] = useState<Blob | null>(null);

  // Generate PDF when modal opens
  useEffect(() => {
    if (open && policy) {
      generatePdf();
    }
    
    // Cleanup URL when modal closes
    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, [open, policy]); // eslint-disable-line react-hooks/exhaustive-deps

  const generatePdf = async () => {
    if (!policy) return;

    try {
      setLoading(true);
      setError(null);
      
      // Clean up previous URL
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
        setPdfUrl(null);
      }

      const blob = await policyService.generatePolicyPdf(policy.id);
      setPdfBlob(blob);
      
      // Create object URL for preview
      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    } catch (err: any) {
      console.error('PDF generation failed:', err);
      setError(
        err.response?.data?.message || 
        err.message || 
        'Failed to generate PDF. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = () => {
    if (!pdfBlob || !policy) return;

    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `policy_${policy.policyNumber}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const handleRetry = () => {
    generatePdf();
  };

  const handleClose = () => {
    // Clean up URL when closing
    if (pdfUrl) {
      URL.revokeObjectURL(pdfUrl);
      setPdfUrl(null);
    }
    setPdfBlob(null);
    setError(null);
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: { height: '90vh' }
      }}
    >
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <PictureAsPdf color="primary" />
          <Typography variant="h6">
            Policy PDF Preview
          </Typography>
          {policy && (
            <Typography variant="body2" color="text.secondary">
              - {policy.policyNumber}
            </Typography>
          )}
        </Box>
        <IconButton onClick={handleClose} size="small">
          <Close />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ p: 0, display: 'flex', flexDirection: 'column' }}>
        {loading && (
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              height: '400px',
              gap: 2,
            }}
          >
            <CircularProgress size={48} />
            <Typography variant="body1" color="text.secondary">
              Generating PDF document...
            </Typography>
          </Box>
        )}

        {error && (
          <Box sx={{ p: 3 }}>
            <Alert 
              severity="error" 
              action={
                <Button 
                  color="inherit" 
                  size="small" 
                  onClick={handleRetry}
                  startIcon={<Refresh />}
                >
                  Retry
                </Button>
              }
            >
              <Typography variant="body2">
                <strong>PDF Generation Failed</strong>
              </Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>
                {error}
              </Typography>
            </Alert>
          </Box>
        )}

        {pdfUrl && !loading && !error && (
          <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
            <iframe
              src={pdfUrl}
              style={{
                width: '100%',
                height: '100%',
                border: 'none',
                minHeight: '500px',
              }}
              title="PDF Preview"
            />
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={handleClose} color="inherit">
          Close
        </Button>
        <Tooltip title="Download PDF file">
          <Button
            onClick={handleDownload}
            variant="contained"
            startIcon={<Download />}
            disabled={!pdfBlob || loading}
          >
            Download
          </Button>
        </Tooltip>
      </DialogActions>
    </Dialog>
  );
};

export default PdfPreviewModal;