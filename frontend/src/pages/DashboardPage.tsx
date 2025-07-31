import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Chip,
  Paper,
} from '@mui/material';
import {
  Policy,
  People,
  TrendingUp,
  Assessment,
  Security,
  Schedule,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const dashboardCards = [
    {
      title: 'Policies',
      description: 'Manage insurance policies',
      icon: <Policy sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Policies',
      path: '/policies',
      available: true,
    },
    ...(user?.role === 'ADMIN' ? [{
      title: 'Users',
      description: 'Manage system users',
      icon: <People sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'Manage Users',
      path: '/users',
      available: true,
    }] : []),
    {
      title: 'Reports',
      description: 'View system reports',
      icon: <Assessment sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Reports',
      path: '/reports',
      available: false,
    },
    {
      title: 'Analytics',
      description: 'System analytics',
      icon: <TrendingUp sx={{ fontSize: 40, color: 'primary.main' }} />,
      action: 'View Analytics',
      path: '/analytics',
      available: false,
    },
  ];

  const handleNavigation = (path: string, available: boolean) => {
    if (available) {
      navigate(path);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Welcome back, {user?.firstName}!
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Here's an overview of your insurance backoffice system.
      </Typography>

      {/* System Status Overview */}
      <Paper sx={{ p: 3, mb: 4, backgroundColor: 'background.paper' }}>
        <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Security color="primary" />
          System Status
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h4" color="success.main">
                Online
              </Typography>
              <Typography variant="body2" color="text.secondary">
                System Status
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h4" color="primary.main">
                {user?.role}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Your Role
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h4" color="info.main">
                <Schedule />
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Last Login: Today
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h4" color="warning.main">
                v1.0.0
              </Typography>
              <Typography variant="body2" color="text.secondary">
                System Version
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Quick Actions */}
      <Typography variant="h5" gutterBottom sx={{ mt: 4, mb: 2 }}>
        Quick Actions
      </Typography>

      <Grid container spacing={3}>
        {/* Role-based access information */}
        {user?.role === 'ADMIN' && (
          <Grid item xs={12}>
            <Paper sx={{ p: 2, mb: 2, backgroundColor: 'info.light', color: 'info.contrastText' }}>
              <Typography variant="body2">
                <strong>Admin Access:</strong> You have full system access including user management. 
                Note: Admins cannot issue policies directly - this is restricted to Operators only.
              </Typography>
            </Paper>
          </Grid>
        )}
        {user?.role === 'OPERATOR' && (
          <Grid item xs={12}>
            <Paper sx={{ p: 2, mb: 2, backgroundColor: 'success.light', color: 'success.contrastText' }}>
              <Typography variant="body2">
                <strong>Operator Access:</strong> You can issue and manage insurance policies. 
                User management functions are restricted to Admin users only.
              </Typography>
            </Paper>
          </Grid>
        )}
        
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
                  variant={card.available ? "contained" : "outlined"}
                  disabled={!card.available}
                  onClick={() => handleNavigation(card.path, card.available)}
                  sx={{
                    ...(card.available ? {} : {
                      opacity: 0.6,
                      cursor: 'not-allowed'
                    })
                  }}
                >
                  {card.action}
                </Button>
                {!card.available && (
                  <Chip 
                    label="Coming Soon" 
                    size="small" 
                    color="secondary" 
                    sx={{ mt: 1 }}
                  />
                )}
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default DashboardPage;