import React, { createContext, useContext, useState, ReactNode } from 'react';
import {
  Snackbar,
  Alert,
  AlertColor,
  Slide,
  SlideProps,
} from '@mui/material';

interface Notification {
  id: string;
  message: string;
  severity: AlertColor;
  duration?: number;
  action?: ReactNode;
}

interface NotificationContextType {
  showNotification: (message: string, severity?: AlertColor, duration?: number, action?: ReactNode) => void;
  showSuccess: (message: string, duration?: number) => void;
  showError: (message: string, duration?: number, action?: ReactNode) => void;
  showWarning: (message: string, duration?: number) => void;
  showInfo: (message: string, duration?: number) => void;
  hideNotification: () => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

function SlideTransition(props: SlideProps) {
  return <Slide {...props} direction="up" />;
}

interface NotificationProviderProps {
  children: ReactNode;
}

export const NotificationProvider: React.FC<NotificationProviderProps> = ({ children }) => {
  const [notification, setNotification] = useState<Notification | null>(null);

  const showNotification = (
    message: string,
    severity: AlertColor = 'info',
    duration: number = 6000,
    action?: ReactNode
  ) => {
    const id = `notification-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    setNotification({
      id,
      message,
      severity,
      duration,
      action,
    });
  };

  const showSuccess = (message: string, duration: number = 4000) => {
    showNotification(message, 'success', duration);
  };

  const showError = (message: string, duration: number = 8000, action?: ReactNode) => {
    showNotification(message, 'error', duration, action);
  };

  const showWarning = (message: string, duration: number = 6000) => {
    showNotification(message, 'warning', duration);
  };

  const showInfo = (message: string, duration: number = 5000) => {
    showNotification(message, 'info', duration);
  };

  const hideNotification = () => {
    setNotification(null);
  };

  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    hideNotification();
  };

  const contextValue: NotificationContextType = {
    showNotification,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    hideNotification,
  };

  return (
    <NotificationContext.Provider value={contextValue}>
      {children}
      <Snackbar
        open={Boolean(notification)}
        autoHideDuration={notification?.duration}
        onClose={handleClose}
        TransitionComponent={SlideTransition}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        key={notification?.id}
      >
        {notification && (
          <Alert
            onClose={handleClose}
            severity={notification.severity}
            variant="filled"
            sx={{ width: '100%' }}
            action={notification.action}
          >
            {notification.message}
          </Alert>
        )}
      </Snackbar>
    </NotificationContext.Provider>
  );
};

export const useNotification = (): NotificationContextType => {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};