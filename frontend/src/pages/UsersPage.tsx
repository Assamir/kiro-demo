import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Alert,
  Snackbar,
} from '@mui/material';
import { Add, Refresh } from '@mui/icons-material';
import { User, CreateUserRequest, UpdateUserRequest } from '../types/auth';
import { userService } from '../services/userService';
import UserList from '../components/users/UserList';
import UserForm from '../components/users/UserForm';
import DeleteUserDialog from '../components/users/DeleteUserDialog';

const UsersPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Form state
  const [formOpen, setFormOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [formLoading, setFormLoading] = useState(false);
  
  // Delete state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deletingUser, setDeletingUser] = useState<User | null>(null);
  const [deleteLoading, setDeleteLoading] = useState(false);
  
  // Success message state
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Load users on component mount
  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const usersData = await userService.getAllUsers();
      setUsers(usersData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = () => {
    setEditingUser(null);
    setFormOpen(true);
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    setFormOpen(true);
  };

  const handleDeleteUser = (user: User) => {
    setDeletingUser(user);
    setDeleteDialogOpen(true);
  };

  const handleFormSubmit = async (userData: CreateUserRequest | UpdateUserRequest) => {
    try {
      setFormLoading(true);
      
      if (editingUser) {
        // Update existing user
        const updatedUser = await userService.updateUser(editingUser.id, userData as UpdateUserRequest);
        setUsers(prev => prev.map(user => 
          user.id === editingUser.id ? updatedUser : user
        ));
        setSuccessMessage('User updated successfully');
      } else {
        // Create new user
        const newUser = await userService.createUser(userData as CreateUserRequest);
        setUsers(prev => [...prev, newUser]);
        setSuccessMessage('User created successfully');
      }
      
      setFormOpen(false);
      setEditingUser(null);
    } catch (error: any) {
      // Error is handled in the form component
      throw error;
    } finally {
      setFormLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deletingUser) return;
    
    try {
      setDeleteLoading(true);
      await userService.deleteUser(deletingUser.id);
      setUsers(prev => prev.filter(user => user.id !== deletingUser.id));
      setSuccessMessage('User deleted successfully');
      setDeleteDialogOpen(false);
      setDeletingUser(null);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to delete user');
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleCloseForm = () => {
    setFormOpen(false);
    setEditingUser(null);
  };

  const handleCloseDeleteDialog = () => {
    setDeleteDialogOpen(false);
    setDeletingUser(null);
  };

  const handleCloseSuccessMessage = () => {
    setSuccessMessage(null);
  };

  const handleCloseError = () => {
    setError(null);
  };

  return (
    <Box>
      {/* Header */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'flex-start',
        mb: 4 
      }}>
        <Box>
          <Typography variant="h4" gutterBottom>
            User Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage system users and their permissions.
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={loadUsers}
            disabled={loading}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={handleCreateUser}
          >
            Add User
          </Button>
        </Box>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert 
          severity="error" 
          sx={{ mb: 3 }}
          onClose={handleCloseError}
        >
          {error}
        </Alert>
      )}

      {/* User List */}
      <UserList
        users={users}
        loading={loading}
        onEditUser={handleEditUser}
        onDeleteUser={handleDeleteUser}
      />

      {/* User Form Dialog */}
      <UserForm
        open={formOpen}
        onClose={handleCloseForm}
        onSubmit={handleFormSubmit}
        user={editingUser}
        loading={formLoading}
      />

      {/* Delete Confirmation Dialog */}
      <DeleteUserDialog
        open={deleteDialogOpen}
        onClose={handleCloseDeleteDialog}
        onConfirm={handleDeleteConfirm}
        user={deletingUser}
        loading={deleteLoading}
      />

      {/* Success Message */}
      <Snackbar
        open={Boolean(successMessage)}
        autoHideDuration={4000}
        onClose={handleCloseSuccessMessage}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={handleCloseSuccessMessage} 
          severity="success"
          variant="filled"
        >
          {successMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default UsersPage;