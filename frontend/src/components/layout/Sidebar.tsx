import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Box,
  Typography,
} from '@mui/material';
import {
  Dashboard,
  Policy,
  People,
  Business,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';

const drawerWidth = 240;

interface NavigationItem {
  text: string;
  icon: React.ReactElement;
  path: string;
  requiredRole?: 'ADMIN' | 'OPERATOR';
}

const navigationItems: NavigationItem[] = [
  {
    text: 'Dashboard',
    icon: <Dashboard />,
    path: '/dashboard',
  },
  {
    text: 'Policies',
    icon: <Policy />,
    path: '/policies',
  },
  {
    text: 'Users',
    icon: <People />,
    path: '/users',
    requiredRole: 'ADMIN',
  },
];

const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  const filteredItems = navigationItems.filter(item => 
    !item.requiredRole || user?.role === item.requiredRole
  );

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSizing: 'border-box',
          backgroundColor: 'background.paper',
          borderRight: '1px solid',
          borderColor: 'divider',
          display: { xs: 'none', md: 'block' }, // Hide on mobile
        },
      }}
    >
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Business sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
        <Typography variant="h6" color="primary.main" fontWeight="bold">
          Insurance
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Backoffice
        </Typography>
      </Box>
      
      <Divider />
      
      <List sx={{ pt: 2 }}>
        {filteredItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => handleNavigation(item.path)}
              sx={{
                mx: 1,
                borderRadius: 1,
                '&.Mui-selected': {
                  backgroundColor: 'primary.main',
                  color: 'primary.contrastText',
                  '&:hover': {
                    backgroundColor: 'primary.dark',
                  },
                  '& .MuiListItemIcon-root': {
                    color: 'primary.contrastText',
                  },
                },
              }}
            >
              <ListItemIcon
                sx={{
                  color: location.pathname === item.path 
                    ? 'inherit' 
                    : 'primary.main',
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Drawer>
  );
};

export default Sidebar;