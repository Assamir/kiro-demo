import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { NotificationProvider, useNotification } from './NotificationContext';
import { Button } from '@mui/material';

// Test component that uses the notification context
const TestComponent: React.FC = () => {
  const {
    showSuccess,
    showError,
    showWarning,
    showInfo,
    showNotification,
    hideNotification,
  } = useNotification();

  return (
    <div>
      <Button onClick={() => showSuccess('Success message')}>Show Success</Button>
      <Button onClick={() => showError('Error message')}>Show Error</Button>
      <Button onClick={() => showWarning('Warning message')}>Show Warning</Button>
      <Button onClick={() => showInfo('Info message')}>Show Info</Button>
      <Button onClick={() => showNotification('Custom message', 'success', 3000)}>
        Show Custom
      </Button>
      <Button onClick={hideNotification}>Hide Notification</Button>
    </div>
  );
};

const renderWithProvider = (component: React.ReactElement) => {
  return render(
    <NotificationProvider>
      {component}
    </NotificationProvider>
  );
};

describe('NotificationContext', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  it('should throw error when used outside provider', () => {
    // Suppress console.error for this test
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    
    expect(() => {
      render(<TestComponent />);
    }).toThrow('useNotification must be used within a NotificationProvider');

    consoleSpy.mockRestore();
  });

  it('should show success notification', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Success');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Success message')).toBeInTheDocument();
    });

    // Check that it has success styling
    const alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-filledSuccess');
  });

  it('should show error notification', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Error');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Error message')).toBeInTheDocument();
    });

    // Check that it has error styling
    const alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-filledError');
  });

  it('should show warning notification', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Warning');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Warning message')).toBeInTheDocument();
    });

    // Check that it has warning styling
    const alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-filledWarning');
  });

  it('should show info notification', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Info');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Info message')).toBeInTheDocument();
    });

    // Check that it has info styling
    const alert = screen.getByRole('alert');
    expect(alert).toHaveClass('MuiAlert-filledInfo');
  });

  it('should show custom notification', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Custom');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Custom message')).toBeInTheDocument();
    });
  });

  it('should hide notification manually', async () => {
    renderWithProvider(<TestComponent />);

    // Show notification
    const showButton = screen.getByText('Show Success');
    fireEvent.click(showButton);

    await waitFor(() => {
      expect(screen.getByText('Success message')).toBeInTheDocument();
    });

    // Hide notification
    const hideButton = screen.getByText('Hide Notification');
    fireEvent.click(hideButton);

    await waitFor(() => {
      expect(screen.queryByText('Success message')).not.toBeInTheDocument();
    });
  });

  it('should close notification when close button is clicked', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Success');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Success message')).toBeInTheDocument();
    });

    // Click the close button
    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);

    await waitFor(() => {
      expect(screen.queryByText('Success message')).not.toBeInTheDocument();
    });
  });

  it('should auto-hide notification after duration', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Success');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Success message')).toBeInTheDocument();
    });

    // Fast-forward time
    jest.advanceTimersByTime(4000);

    await waitFor(() => {
      expect(screen.queryByText('Success message')).not.toBeInTheDocument();
    });
  });

  it('should not close on clickaway', async () => {
    renderWithProvider(<TestComponent />);

    const button = screen.getByText('Show Success');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Success message')).toBeInTheDocument();
    });

    // Click outside the notification
    fireEvent.click(document.body);

    // Should still be visible
    expect(screen.getByText('Success message')).toBeInTheDocument();
  });
});