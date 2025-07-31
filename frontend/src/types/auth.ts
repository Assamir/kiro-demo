export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'OPERATOR';
}

export interface CreateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: 'ADMIN' | 'OPERATOR';
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'OPERATOR';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface ApiError {
  message: string;
  code?: string;
}