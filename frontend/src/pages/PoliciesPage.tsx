import React from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
} from '@mui/material';
import { Policy } from '@mui/icons-material';

const PoliciesPage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Policy Management
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Create, edit, and manage insurance policies.
      </Typography>

      <Card>
        <CardContent sx={{ textAlign: 'center', py: 8 }}>
          <Policy sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="text.secondary">
            Policy management interface will be implemented in a future task
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            This will include policy creation, editing, and search functionality
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default PoliciesPage;