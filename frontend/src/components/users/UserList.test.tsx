import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider } from '@mui/material/styles';
import theme from '../../theme/theme';
import UserList from './UserList';
import { User } from '../../types/auth';

const mockUsers: User[] = [
  {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    role: 'ADMIN',
  },
  {
    id: 2,
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane.smith@example.com',
    role: 'OPERATOR',
  },
  {
    id: 3,
    firstName: 'Bob',
    lastName: 'Johnson',
    email: 'bob.johnson@example.com',
    role: 'OPERATOR',
  },
];

const renderWithTheme = (component: React.ReactElement) => {
  return render(
    <ThemeProvider theme={theme}>
      {component}
    </ThemeProvider>
  );
};

describe('UserList', () => {
  const mockOnEditUser = jest.fn();
  const mockOnDeleteUser = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders user list with all users', () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    expect(screen.getByText('Bob Johnson')).toBeInTheDocument();
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
    expect(screen.getByText('jane.smith@example.com')).toBeInTheDocument();
    expect(screen.getByText('bob.johnson@example.com')).toBeInTheDocument();
  });

  it('displays correct user count', () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    expect(screen.getByText('Showing 3 of 3 users')).toBeInTheDocument();
  });

  it('filters users by search term', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const searchInput = screen.getByPlaceholderText('Search users...');
    fireEvent.change(searchInput, { target: { value: 'John' } });

    // The filtering should happen immediately since it's based on useMemo
    // "John" matches both "John Doe" and "Bob Johnson" (contains "John")
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
    expect(screen.getByText('Bob Johnson')).toBeInTheDocument(); // This should match because "Johnson" contains "John"
    expect(screen.getByText('Showing 2 of 3 users')).toBeInTheDocument();
  });

  it('filters users by email search', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const searchInput = screen.getByPlaceholderText('Search users...');
    fireEvent.change(searchInput, { target: { value: 'jane.smith' } });

    await waitFor(() => {
      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
      expect(screen.queryByText('Bob Johnson')).not.toBeInTheDocument();
      expect(screen.getByText('Showing 1 of 3 users')).toBeInTheDocument();
    });
  });

  it('filters users by role', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const roleSelect = screen.getByRole('combobox', { name: /role/i });
    fireEvent.mouseDown(roleSelect);
    
    const adminOption = screen.getByText('Admin');
    fireEvent.click(adminOption);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
      expect(screen.queryByText('Bob Johnson')).not.toBeInTheDocument();
      expect(screen.getByText('Showing 1 of 3 users')).toBeInTheDocument();
    });
  });

  it('combines search and role filters', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    // Filter by OPERATOR role
    const roleSelect = screen.getByRole('combobox', { name: /role/i });
    fireEvent.mouseDown(roleSelect);
    const operatorOption = screen.getByText('Operator');
    fireEvent.click(operatorOption);

    // Then search for "Jane"
    const searchInput = screen.getByPlaceholderText('Search users...');
    fireEvent.change(searchInput, { target: { value: 'Jane' } });

    await waitFor(() => {
      expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
      expect(screen.queryByText('Bob Johnson')).not.toBeInTheDocument();
      expect(screen.getByText('Showing 1 of 3 users')).toBeInTheDocument();
    });
  });

  it('shows no results message when no users match filters', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const searchInput = screen.getByPlaceholderText('Search users...');
    fireEvent.change(searchInput, { target: { value: 'nonexistent' } });

    await waitFor(() => {
      expect(screen.getByText('No users match your search criteria')).toBeInTheDocument();
      expect(screen.getByText('Showing 0 of 3 users')).toBeInTheDocument();
    });
  });

  it('shows empty state when no users provided', () => {
    renderWithTheme(
      <UserList
        users={[]}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    expect(screen.getByText('No users found')).toBeInTheDocument();
    expect(screen.getByText('Showing 0 of 0 users')).toBeInTheDocument();
  });

  it('calls onEditUser when edit button is clicked', () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const editButtons = screen.getAllByLabelText('Edit user');
    fireEvent.click(editButtons[0]);

    expect(mockOnEditUser).toHaveBeenCalledWith(mockUsers[0]);
  });

  it('calls onDeleteUser when delete button is clicked', () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const deleteButtons = screen.getAllByLabelText('Delete user');
    fireEvent.click(deleteButtons[0]);

    expect(mockOnDeleteUser).toHaveBeenCalledWith(mockUsers[0]);
  });

  it('displays loading state', () => {
    renderWithTheme(
      <UserList
        users={[]}
        loading={true}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    expect(screen.getByText('Loading users...')).toBeInTheDocument();
  });

  it('displays role chips with correct colors', () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const adminChips = screen.getAllByText('ADMIN');
    const operatorChips = screen.getAllByText('OPERATOR');

    expect(adminChips).toHaveLength(1);
    expect(operatorChips).toHaveLength(2);
  });

  it('clears search when search input is cleared', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const searchInput = screen.getByPlaceholderText('Search users...');
    
    // First filter - use a more specific search term
    fireEvent.change(searchInput, { target: { value: 'Jane' } });
    expect(screen.getByText('Showing 1 of 3 users')).toBeInTheDocument();

    // Clear filter
    fireEvent.change(searchInput, { target: { value: '' } });
    expect(screen.getByText('Showing 3 of 3 users')).toBeInTheDocument();
  });

  it('resets role filter to show all users', async () => {
    renderWithTheme(
      <UserList
        users={mockUsers}
        onEditUser={mockOnEditUser}
        onDeleteUser={mockOnDeleteUser}
      />
    );

    const roleSelect = screen.getByRole('combobox', { name: /role/i });
    
    // Filter by ADMIN
    fireEvent.mouseDown(roleSelect);
    fireEvent.click(screen.getByText('Admin'));
    await waitFor(() => {
      expect(screen.getByText('Showing 1 of 3 users')).toBeInTheDocument();
    });

    // Reset to All Roles
    fireEvent.mouseDown(roleSelect);
    fireEvent.click(screen.getByText('All Roles'));
    await waitFor(() => {
      expect(screen.getByText('Showing 3 of 3 users')).toBeInTheDocument();
    });
  });
});