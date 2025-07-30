import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { User, LoginRequest, AuthResponse } from '../types/auth';
import { authService } from '../services/authService';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
  hasRole: (role: 'ADMIN' | 'OPERATOR') => boolean;
  canManageUsers: () => boolean;
  canIssuePolicies: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in on app start
    const initializeAuth = async () => {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          const userData = await authService.getCurrentUser();
          setUser(userData);
        }
      } catch (error) {
        // Token might be invalid, remove it
        localStorage.removeItem('token');
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      const response: AuthResponse = await authService.login(credentials);
      localStorage.setItem('token', response.token);
      setUser(response.user);
    } catch (error) {
      throw error;
    }
  };

  const logout = useCallback(async (): Promise<void> => {
    try {
      await authService.logout();
    } catch (error) {
      // Continue with logout even if server call fails
      console.warn('Logout request failed:', error);
    } finally {
      localStorage.removeItem('token');
      setUser(null);
    }
  }, []);

  const refreshUser = useCallback(async (): Promise<void> => {
    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
    } catch (error) {
      // If refresh fails, user is likely not authenticated
      localStorage.removeItem('token');
      setUser(null);
      throw error;
    }
  }, []);

  const hasRole = useCallback((role: 'ADMIN' | 'OPERATOR'): boolean => {
    return user?.role === role;
  }, [user]);

  const canManageUsers = useCallback((): boolean => {
    return user?.role === 'ADMIN';
  }, [user]);

  const canIssuePolicies = useCallback((): boolean => {
    return user?.role === 'OPERATOR' || user?.role === 'ADMIN';
  }, [user]);

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    logout,
    refreshUser,
    hasRole,
    canManageUsers,
    canIssuePolicies,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};