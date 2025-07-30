import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
} from '@mui/material';
import {
  Policy,
  People,
  TrendingUp,
  Assessment,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const dashboardCards = [
    {
      title: 'Policies',
      description: 'Manage insurance policies',
      icon: <Policy sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Policies',
      path: '/policies',
    },
    ...(user?.role === 'ADMIN' ? [{
      title: 'Users',
      description: 'Manage system users',
      icon: <People sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'Manage Users',
      path: '/users',
    }] : []),
    {
      title: 'Reports',
      description: 'View system reports',
      icon: <Assessment sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Reports',
      path: '/reports',
    },
    {
      title: 'Analytics',
      description: 'System analytics',
      icon: <TrendingUp sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Analytics',
      path: '/analytics',
    },
  ];

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Welcome back, {user?.firstName}!
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Here's an overview of your insurance backoffice system.
      </Typography>

      <Grid container spacing={3}>
        {dashboardCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={4} key={index}>
            <Card 
              sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 4,
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1, textAlign: 'center', pt: 3 }}>
                {card.icon}
                <Typography variant="h6" component="h2" sx={{ mt: 2, mb: 1 }}>
                  {card.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {card.description}
                </Typography>
              </CardContent>
              <CardActions sx={{ justifyContent: 'center', pb: 2 }}>
                <Button 
                  size="small" 
                  variant="contained"
                  onClick={() => {
                    // Navigation will be implemented in later tasks
                    console.log(`Navigate to ${card.path}`);
                  }}
                >
                  {card.action}
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default DashboardPage;